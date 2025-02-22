package alvin.study.springboot.graphql.conf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;

import org.modelmapper.ModelMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.core.context.CustomRequestAttributes;
import alvin.study.springboot.graphql.core.graphql.context.GraphQLContextResolver;
import alvin.study.springboot.graphql.core.graphql.dataloader.DataLoaderProvider;
import alvin.study.springboot.graphql.core.graphql.directive.LengthDirective;
import alvin.study.springboot.graphql.core.graphql.directive.UppercaseDirective;
import graphql.ExecutionResult;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * Kickstart Graphql 配置类
 *
 * <p>
 * {@link SimplePerformantInstrumentation} 超类提供了一组拦截器, 用于拦截 Graphql 执行过程中的各个阶段, 参考:
 * {@link SimplePerformantInstrumentation#beginExecution(InstrumentationExecutionParameters)}
 * 等方法
 * </p>
 *
 * <p>
 * {@link GraphQLServletContextBuilder} 接口用来提供各类请求上下文下
 * {@link GraphQLKickstartContext} 对象如何产生, 参考:
 * {@link GraphQLServletContextBuilder#build()},
 * {@link GraphQLServletContextBuilder#build(HttpServletRequest, HttpServletResponse)}
 * 和 {@link GraphQLServletContextBuilder#build(Session, HandshakeRequest)} 方法
 * </p>
 */
@Slf4j
@Configuration("conf/graphql")
@RequiredArgsConstructor
public class GraphqlConfig implements Instrumentation {
    // 注入所有的 Dataloader 对象
    private final List<DataLoaderProvider<?, ?>> dataLoaderProviders;

    // 注入所有的 Graphql 请求上下文对象进行处理器
    private final List<GraphQLContextResolver> contextResolvers;

    // 注入 JSON 处理对象
    private final ObjectMapper objectMapper;

    // 注入模型转换对象
    private final ModelMapper modelMapper;

    /**
     * 拦截 Graphql 执行前的节点
     *
     * <p>
     * 由于 Kickstart 框架是基于 Graphql Spring Boot 框架, 而 {@link SimplePerformantInstrumentation} 超类是
     * Graphql Spring Boot 框架提供, 用于拦截 graphql 执行过程中的各个阶段
     * </p>
     *
     * <p>
     * 本方法拦截了 Graphql 执行前阶段, 本例中起到三个作用: 1. 打印 Graphql 执行过程中的日志; 2. 对 Graphql
     * Context 进行处理; 3. 注册 Spring MVC Context, 并将 Graphql Context 和 Spring Context
     * 进行集成
     * </p>
     *
     * @param parameters 执行参数
     */
    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters parameters, InstrumentationState state) {
        if (log.isDebugEnabled()) {
            log.info(">>> Begin graphql executor\n\tquery=\"{}\"\n\toperation=\"{}\"\n\tvariables=\"{}\"",
                parameters.getQuery().trim().replace("\n", "\n\t"),
                parameters.getOperation(),
                formatGraphqlVariables(parameters.getVariables()));
        }

        // 将 Graphql 请求上下文对象注入到 Spring web 请求上下文内
        if (RequestContextHolder.getRequestAttributes() == null) {
            RequestContextHolder.setRequestAttributes(new CustomRequestAttributes());
        }

        // 对 Graphql 请求上下文对象进行处理
        var context = parameters.getGraphQLContext();
        for (var resolver : this.contextResolvers) {
            resolver.resolve(context);
        }

        // 返回执行完成拦截器对象, 当 Graphql 处理完成后进行拦截
        return SimpleInstrumentationContext.whenCompleted((executionResult, err) -> {
            // 如果包含异常则打印日志且返回错误则打印日志
            if (!logIfHasException(err) && !logIfHasError(executionResult)) {
                // 如果成功则打印日志
                logIfSuccess(executionResult);
            }
        });
    }

    /**
     * 对 Graphql 参数进行格式化
     *
     * @param variables 一个 Map 对象表示 Graphql 的参数
     * @return 格式化结果
     */
    private @NotNull String formatGraphqlVariables(Map<String, ?> variables) {
        if (variables == null || variables.isEmpty()) {
            return "Empty variables";
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(variables)
                    .replace("\n", "\n\t");
        } catch (JsonProcessingException e) {
            return "Unsupported format variables";
        }
    }

    /**
     * 如果 Graphql 执行返回结果有错误, 则打印日志
     *
     * @param er Graphql 执行结果
     * @return 是否包含错误信息
     */
    @SneakyThrows
    private boolean logIfHasError(@NotNull ExecutionResult er) {
        var errors = er.getErrors();
        if (errors == null || errors.isEmpty()) {
            return false;
        }

        try (var stream = new ByteArrayOutputStream()) {
            try (var ps = new PrintStream(stream)) {
                ps.println("<<< Complete graphql executor with errors");
                for (var i = 0; i < errors.size(); i++) {
                    var error = errors.get(i);
                    ps.printf("\t[%d]. {%s}: ", i, error.getErrorType());
                    ps.printf("\t\t%s", error.getMessage());
                    ps.printf("\t\t%s",
                        objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(error.getExtensions()));
                    ps.println();
                }
            }
            log.warn(stream.toString(StandardCharsets.UTF_8));
        }
        return true;
    }

    /**
     * 如果 Graphql 执行成功, 则打印日志
     *
     * @param er Graphql 执行结果
     */
    @SneakyThrows
    private void logIfSuccess(ExecutionResult er) {
        if (log.isDebugEnabled()) {
            var data = er.getData();
            if (data != null) {
                try (var stream = new ByteArrayOutputStream()) {
                    try (var ps = new PrintStream(stream)) {
                        ps.println("<<< Complete graphql executor success");
                        ps.printf("\tresult: \"%s\"", objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(data)
                                .replace("\n", "\n\t"));
                    }
                    log.info(stream.toString(StandardCharsets.UTF_8));
                }
            }
        }
    }

    /**
     * 如果 Graphql 执行过程中发生异常, 则打印日志
     *
     * @param t 异常对象
     */
    @SneakyThrows
    private boolean logIfHasException(Throwable t) {
        if (t == null) {
            return false;
        }

        log.warn("<<< Complete graphql executor with exception {}", t.getMessage());
        try (var stream = new ByteArrayOutputStream()) {
            try (var ps = new PrintStream(new ByteArrayOutputStream())) {
                t.printStackTrace(ps);
            }
            log.warn(stream.toString(StandardCharsets.UTF_8));
        }
        return true;
    }

    private GraphQLScalarType buildVoidType() {
        // 新增 Graphql Scalar 类型 (Void 类型)
        return GraphQLScalarType.newScalar()
                .name("Void")
                .description("An Void scalar that means nothing")
                .coercing(new Coercing<Void, Void>() {
                    // 该类用于演示 Scalar 的定义, 该方法不会被调用, 正常情况下不应该返回 null 值
                    @Override
                    public Void parseLiteral(
                            @NotNull Value<?> input,
                            @NotNull CoercedVariables variables,
                            @NotNull GraphQLContext graphQLContext,
                            @NotNull Locale locale) throws CoercingParseLiteralException {
                        return null;
                    }

                    @Override
                    public Void parseValue(
                            @NotNull Object input,
                            @NotNull GraphQLContext graphQLContext,
                            @NotNull Locale locale) throws CoercingParseValueException {
                        return null;
                    }

                    @Override
                    public Void serialize(
                            @NotNull Object dataFetcherResult,
                            @NotNull GraphQLContext graphQLContext,
                            @NotNull Locale locale) throws CoercingSerializeException {
                        return null;
                    }
                })
                .build();
    }

    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Object)
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Url)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Locale)
                .scalar(buildVoidType())
                .directive("uppercase", new UppercaseDirective())
                .directiveWiring(new LengthDirective());
    }

    @Bean
    BatchLoaderRegistry batchLoaderRegistry() {
        var registry = new DefaultBatchLoaderRegistry();
        for (var provider : dataLoaderProviders) {
            registry.forTypePair(null, null)
        }
        return registry;
    }
}
