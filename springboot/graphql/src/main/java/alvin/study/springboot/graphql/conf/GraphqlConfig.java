package alvin.study.springboot.graphql.conf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.dataloader.BatchLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import graphql.ExecutionResult;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import alvin.study.springboot.graphql.app.dataloader.DepartmentLoader;
import alvin.study.springboot.graphql.app.dataloader.OrgLoader;
import alvin.study.springboot.graphql.app.dataloader.UserLoader;
import alvin.study.springboot.graphql.core.graphql.directive.LengthDirective;
import alvin.study.springboot.graphql.core.graphql.directive.UppercaseDirective;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;

/**
 * Spring Graphql 配置类
 *
 * <p>
 * {@link Instrumentation} 接口提供了一组拦截器, 用于拦截 Graphql 执行过程中的各个阶段, 参考:
 * {@link Instrumentation#beginExecution(InstrumentationExecutionParameters)} 等方法
 * </p>
 *
 * <p>
 * 另外, 该类型还提供了如下类型:
 * <ul>
 * <li>{@link RuntimeWiringConfigurer} 类型实例, 用于对 Graphql 的数据类型进行扩展, 包括: 1. 注册新的 Graphql scalar 类型;
 * 2. 注册 Graphql 中使用的 directive 处理器;</li>
 * <li>{@link BatchLoaderRegistry} 类型实例, 用于对 Graphql 处理过程中使用到的 {@code DataLoader} 进行注册;</li>
 * </ul>
 * </p>
 */
@Slf4j
@Configuration("conf/graphql")
@RequiredArgsConstructor
public class GraphqlConfig implements Instrumentation {
    private final ObjectMapper objectMapper;

    /**
     * 拦截 Graphql 执行前的节点
     *
     * <p>
     * {@link Instrumentation} 接口提供了一组方法, 用于拦截 Graphql 语句执行过程中的各个阶段
     * </p>
     *
     * <p>
     * 本方法拦截了 Graphql 执行前阶段, 用于输出 Graphql 执行过程中的日志
     * </p>
     *
     * @param parameters Graphql 语句执行参数
     * @param state      Graphql 语句执行状态
     * @return Graphql 执行上下文对象
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
     * @param variables Graphql 执行参数, 通过 {@link Map} 对象存储
     * @return 格式化结果字符串
     */
    private String formatGraphqlVariables(Map<String, ?> variables) {
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
     * 打印 Graphql 执行过程中发生的错误信息
     *
     * @param er {@link ExecutionResult} 类型对象, 表示 Graphql 语句的执行结果
     * @return {@code true} 表示有错误发生, {@code false} 表示没有错误发生
     */
    @SneakyThrows
    private boolean logIfHasError(ExecutionResult er) {
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
     * 打印 Graphql 执行完毕后的成功信息
     *
     * @param er {@link ExecutionResult} 类型对象, 表示 Graphql 语句的执行结果
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
     * 打印 Graphql 执行过程中发生的异常信息
     *
     * @param t {@link Throwable} 类型对象, 表示 Graphql 语句执行过程中抛出的异常
     * @return {@code true} 表示有异常发生, {@code false} 表示没有异常发生
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

    /**
     * 构建 {@code Void} 扩展类型
     *
     * @return {@link GraphQLScalarType} 类型对象, 表示扩展 Graphql 数据类型的 {@code Void} 集合
     */
    private GraphQLScalarType buildVoidType() {
        // 新增 Graphql Scalar 类型 (Void 类型)
        return GraphQLScalarType.newScalar()
                .name("Void")
                .description("An Void scalar that means nothing")
                .coercing(new Coercing<Void, Void>() {
                    // 该类用于演示 Scalar 的定义, 该方法不会被调用, 正常情况下不应该返回 null 值
                    @Override
                    public Void parseLiteral(
                            Value<?> input,
                            CoercedVariables variables,
                            GraphQLContext graphQLContext,
                            Locale locale) throws CoercingParseLiteralException {
                        return null;
                    }

                    @Override
                    public Void parseValue(
                            Object input,
                            GraphQLContext graphQLContext,
                            Locale locale) throws CoercingParseValueException {
                        return null;
                    }

                    @Override
                    public Void serialize(
                            Object dataFetcherResult,
                            GraphQLContext graphQLContext,
                            Locale locale) throws CoercingSerializeException {
                        return null;
                    }
                })
                .build();
    }

    /**
     * 构建 {@link RuntimeWiringConfigurer} 类型对象, 用于配置 Graphql 运行时环境
     *
     * <p>
     * {@link RuntimeWiringConfigurer} 类型对象用于配置 Graphql 运行时环境, 包括: 1. 扩展类型; 2. 扩展指令;
     * </p>
     *
     * @return {@link RuntimeWiringConfigurer} 类型对象, 用于配置 Graphql 运行时环境
     */
    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
                .scalar(ExtendedScalars.Object) // 添加扩展类型
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Url)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Locale)
                .scalar(buildVoidType())
                .directive("uppercase", new UppercaseDirective()) // 添加扩展指令
                .directiveWiring(new LengthDirective())
                .build();
    }

    /**
     * 构建 {@link BatchLoaderRegistry} 类型对象, 用于配置 {@link BatchLoader} 类型对象
     *
     * <p>
     * 要在代码中使用 {@link BatchLoader} 类型对象, 需要使用 {@link BatchLoaderRegistry} 类型对象进行注册
     * </p>
     *
     * @param userLoader {@link BatchLoader} 类型对象, 用于根据 {@code ID} 加载用户实体对象
     * @param orgLoader  {@link BatchLoader} 类型对象, 用于根据 {@code ID} 加载组织实体对象
     * @return {@link BatchLoaderRegistry} 类型对象, 用于配置 {@link BatchLoader} 类型对象
     */
    @Bean
    BatchLoaderRegistry batchLoaderRegistry(
            UserLoader userLoader,
            OrgLoader orgLoader,
            DepartmentLoader departmentLoader) {
        var registry = new DefaultBatchLoaderRegistry();

        registry.forTypePair(Long.class, User.class)
                .registerMappedBatchLoader(userLoader);

        registry.forTypePair(Long.class, Org.class)
                .registerMappedBatchLoader(orgLoader);

        registry.forTypePair(Long.class, Department.class)
                .registerMappedBatchLoader(departmentLoader);

        return registry;
    }
}
