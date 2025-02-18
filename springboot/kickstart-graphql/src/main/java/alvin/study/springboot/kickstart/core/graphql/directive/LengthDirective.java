package alvin.study.springboot.kickstart.core.graphql.directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import alvin.study.springboot.kickstart.conf.GraphqlConfig;
import graphql.Assert;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import graphql.language.IntValue;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLAppliedDirective;
import graphql.schema.GraphQLDirectiveContainer;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeUtil;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

/**
 * 定义 {@code @len} 字段处理器
 *
 * <p>
 * 本例中对于标注 {@code @len} 处理器的参数或 {@code Input} 类型 {@code String} 类型字段进行处理, 验证其长度
 * </p>
 *
 * <p>
 * 一个字段处理器的典型处理方式如下:
 * <ol>
 * <li>
 * 当框架在解析 {@code *.graphqls} 文件时, 遇到的字段均为交由
 * {@link #onField(SchemaDirectiveWiringEnvironment)} 方法对于需要的字段进行识别和处理,
 * 在处理过程中会同时处理字段的参数, 参数的字段等 (如参数为 {@code Input} 类型)
 * </li>
 * <li>
 * 在 {@link #onField(SchemaDirectiveWiringEnvironment)} 方法内, 可以重写
 * {@link graphql.schema.DataFetcher DataFetcher} 对象, 在实际执行查询时即可针对该字段使用新的
 * {@link graphql.schema.DataFetcher DataFetcher}, 并获取处理后的字段值
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * 参考 {@code classpath:graphql/type.graphqls} 中对 {@code @len} directive 的定义
 * </p>
 *
 * <p>
 * 参考 {@code classpath:graphql/user.graphqls} 中对 {@code UserInput} 的定义,
 * 表示当创建用户实体时, 对 {@code account} 和 {@code password} 字段的长度进行校验
 * </p>
 *
 * <p>
 * 本类型在 {@link GraphqlConfig#lengthDirective()
 * GraphqlConfig.lengthDirective()} 方法中进行注册
 * </p>
 */
public class LengthDirective implements SchemaDirectiveWiring {
    // directive 的名称
    private static final String DIRECTIVE = "len";

    // directive 的 min 参数名
    private static final String DIRECTIVE_ARG_MIN = "min";

    // directive 的 max 参数名
    private static final String DIRECTIVE_ARG_MAX = "max";

    /**
     * 将处理器参数解析为整数型 ({@code min} 和 {@code max}) 参数
     *
     * @param directive    处理器标识对象
     * @param argumentName 处理器标识参数名
     * @return 处理器对象参数值
     */
    private static Integer parseArgument(GraphQLAppliedDirective directive, String argumentName) {
        // 根据参数名获取处理器标识的参数值
        var argument = directive.getArgument(argumentName);
        if (argument == null) {
            throw new IllegalArgumentException("directive");
        }

        // 获取参数值, 参数值为 InputValueWithState 类型对象
        var valueWithState = argument.getArgumentValue();
        if (!valueWithState.isSet()) {
            throw new IllegalArgumentException("directive");
        }

        // 获取参数的实际值
        var value = valueWithState.getValue();
        if (!(value instanceof IntValue v)) {
            throw new IllegalArgumentException("directive");
        }

        // 返回整数类型的 Java 对象值
        return v.getValue().intValue();
    }

    /**
     * 获取参数的实际类型
     *
     * @param inputType 输入的 {@code Input} 对象类型
     * @return {@link GraphQLInputType} 对象
     */
    private static GraphQLInputType unwrapNonNull(GraphQLInputType inputType) {
        var type = GraphQLTypeUtil.unwrapNonNull(inputType);
        if (type instanceof GraphQLInputType qlInputType) {
            return qlInputType;
        }

        var argType = GraphQLTypeUtil.simplePrint(inputType);
        return Assert.assertShouldNeverHappen("You have a wrapped type that is in fact not a input type : %s", argType);
    }

    /**
     * 包装字段返回值
     *
     * <p>
     * 根据字段返回值的不同, 采用不同方式获取字段返回的实际信息, 并包装为 {@link DataFetcherResult} 对象返回
     * </p>
     *
     * @param errors 字段错误信息
     * @param value  字段实际值
     * @return {@link DataFetcherResult} 类型对象
     */
    private static Object makeDFRFromFetchedResult(List<GraphQLError> errors, Object value) {
        // 判断字段值类型是否为 CompletionStage, 这是一个异步字段值包装对象, 需要进一步获取其本身的值
        if (value instanceof CompletionStage<?> stage) {
            // 异步回调, 进一步获取字段返回值 (递归调用)
            return stage.thenApply(v -> makeDFRFromFetchedResult(errors, v));
        }

        // 判断字段值是否为 DataFetcherResult 对象, 这是 graphql 字段值的包装对象, 可以直接获取字段返回的信息
        if (value instanceof DataFetcherResult<?> df) {
            // 设置字段的原始值, 合并错误信息
            return makeDFR(df.getData(), concat(errors, df.getErrors()), df.getLocalContext());
        }

        // 如果字段值为其它类型对象, 则直接包装为 DataFetcherResult 类型并返回
        return makeDFR(value, errors, null);
    }

    /**
     * 产生一个 {@link DataFetcherResult} 对象, 即 {@code graphql} 查询字段的返回值
     *
     * @param value        返回值的实际对象值
     * @param errors       返回值包含的错误信息
     * @param localContext 相关上下文对象
     * @return {@link DataFetcherResult} 对象
     */
    private static DataFetcherResult<Object> makeDFR(Object value, List<GraphQLError> errors, Object localContext) {
        return DataFetcherResult.newResult()
                .data(value)
                .errors(errors)
                .localContext(localContext)
                .build();
    }

    /**
     * 合并两个 {@link List} 集合
     *
     * @param <T> 集合元素类型
     * @param l1  集合 1
     * @param l2  集合 2
     * @return 两个集合合并的结果
     */
    private static <T> List<T> concat(List<T> l1, List<T> l2) {
        var errors = new ArrayList<T>();
        errors.addAll(l1);
        errors.addAll(l2);
        return errors;
    }

    /**
     * 当查询到标注指定处理器的字段时, 执行的方法
     */
    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        // 获取字段值
        var field = env.getElement();

        // 判断字段的参数或参数的 Input 字段是否包含了所需的 directive 处理器标识
        if (appliesTo(field)) {
            // 替换 DataFetcher
            replaceDataFetcher(env);
        }

        return field;
    }

    /**
     * 判断查询字段是否包含具备指定 directive 处理器标识的参数或 Input 字段
     *
     * @param definition 字段定义对象
     * @return 是否包含指定的 directive 处理器标识
     */
    private boolean appliesTo(GraphQLFieldDefinition definition) {
        // 遍历字段定义的参数
        // 参数包含简单类型参数和 Input 类型参数
        return definition.getArguments().stream().anyMatch(it -> {
            // 判断参数上是否标记了指定的处理器标识
            if (appliesTo(it)) {
                return true;
            }

            // 若参数上为标记所需的处理器标识, 则进一步查看参数类型是否为 Input 类型
            var inputType = unwrapNonNull(it.getType());
            if (!(inputType instanceof GraphQLInputObjectType inputObjectType)) {
                // 如果参数类型非 GraphQLInputObjectType 类型, 则返回 false
                return false;
            }

            // 遍历 Input 类型的每个字段, 查看字段上是否标注了指定的处理器标识
            return inputObjectType.getFieldDefinitions().stream().anyMatch(this::appliesTo);
        });
    }

    /**
     * 判断参数或 Input 字段对象是否包含指定的 directive 处理器标识
     *
     * @param container 承载 directive 处理器标识的参数或 Input 字段对象
     * @return 是否包含指定的 directive 处理器标识
     */
    private boolean appliesTo(GraphQLDirectiveContainer container) {
        // 判断标注的 directive 标识是否为期望的标识
        return container.getAppliedDirective(DIRECTIVE) != null;
    }

    /**
     * 替换当前字段的 {@link DataFetcher} 对象
     *
     * @param definition
     */
    private void replaceDataFetcher(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        // 获取原本的 DataFetcher 对象
        var originalFetcher = env.getFieldDataFetcher();

        // 创建一个新的 DataFetcher 对象
        var newFetcher = createDataFetcher(originalFetcher);

        var def = env.getFieldDefinition();
        var type = def.getType();

        // 替换当前字段的 DataFetcher 对象
        env.getCodeRegistry().dataFetcher((GraphQLObjectType) type, def, newFetcher);
    }

    /**
     * 为当前字段创建新的 {@link DataFetcher} 对象
     *
     * <p>
     * 创建的 {@link DataFetcher} 对象表示: 当获取当前字段值时, 会将当前字段的参数 (以及 Input 类型参数的字段) 进行遍历,
     * 如果包含了指定的 {@code @len} 处理器标识, 则根据处理器标识的参数 ({@code min} 和 {@code max}),
     * 校验字段参数值是否符合要求
     * </p>
     *
     * @param originalFetcher
     * @return 新的 {@link DataFetcher} 对象
     */
    private DataFetcher<?> createDataFetcher(DataFetcher<?> originalFetcher) {
        // 返回 DataFetcher 对象 (lambda 表达式)
        return env -> {
            // 记录错误信息的集合
            var errors = new ArrayList<GraphQLError>();

            // 遍历字段的参数
            env.getFieldDefinition().getArguments().forEach(it -> {
                // 判断参数是否包含指定的处理器标识
                if (appliesTo(it)) {
                    // 获取参数值, 处理参数, 并记录返回的错误
                    errors.addAll(apply(it, env, env.getArgument(it.getName())));
                }

                // 如果参数为 Input 类型
                var inputType = unwrapNonNull(it.getType());
                if (inputType instanceof GraphQLInputObjectType inputObjType) {
                    // 获取 Input 参数的字段定义
                    inputObjType.getFieldDefinitions().stream()
                            // 判断字段是否包含指定的处理器标识
                            .filter(this::appliesTo)
                            // 遍历所有 Input 字段
                            .forEach(io -> {
                                // 获取 Input 字段值
                                var value = env.<Map<String, Object>>getArgument(it.getName());
                                if (value != null) {
                                    // 获取 Input 字段值, 处理字段值, 并记录返回的错误
                                    errors.addAll(apply(io, env, value.get(io.getName())));
                                }
                            });
                }
            });

            // 通过原始的 DataFetcher 获取字段原始值
            var result = originalFetcher.get(env);
            if (errors.isEmpty()) {
                // 如果没有错误信息, 则返回字段的原始值
                return result;
            }

            // 将字段原始值和错误信息整合为新的 DataFetcherResult 对象
            return makeDFRFromFetchedResult(errors, result);
        };
    }

    /**
     * 对具备处理器标识的参数值 (或 Input 类型字段值) 进行处理
     *
     * @param it    包含处理器标识的参数或字段对象
     * @param env   {@link DataFetcher} 提供的环境对象
     * @param value 包含处理器标识的参数或字段对象的值
     * @return 错误信息集合
     */
    private List<GraphQLError> apply(GraphQLDirectiveContainer it, DataFetchingEnvironment env, Object value) {
        // 获取参数或 Input 字段值上定义的处理器标识
        var directive = it.getAppliedDirective(DIRECTIVE);

        // 获取处理器参数 (min 和 max 参数)
        var min = parseArgument(directive, DIRECTIVE_ARG_MIN);
        var max = parseArgument(directive, DIRECTIVE_ARG_MAX);

        // 判断参数值或字段值的长度是否符合要求
        if ((value instanceof String s) && (s.length() < min || s.length() > max)) {
            // 产生错误信息
            var err = String.format("Argument value %s is out of range. The range is %s to %s.", value, min, max);
            // 创建错误对象
            return List.of(GraphqlErrorBuilder.newError(env).message(err).build());
        }
        return List.of();
    }
}
