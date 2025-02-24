package alvin.study.springboot.graphql.core.graphql.directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import graphql.language.IntValue;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLDirectiveContainer;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
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
 * 本类型在 {@link alvin.study.springboot.graphql.conf.GraphqlConfig#lengthDirective()
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
     * 判断参数或 Input 字段对象是否包含指定的 directive 处理器标识
     *
     * @param container 承载 directive 处理器标识的参数或 Input 字段对象
     * @return 是否包含指定的 directive 处理器标识
     */
    private static boolean hasDirective(GraphQLDirectiveContainer container) {
        // 判断标注的 directive 标识是否为期望的标识
        return container.getAppliedDirective(DIRECTIVE) != null;
    }

    /**
     * 判断查询字段是否包含具备指定 directive 处理器标识的参数或 Input 字段
     *
     * @param definition 字段定义对象
     * @return 是否包含指定的 directive 处理器标识
     */
    private static boolean appliesTo(GraphQLFieldDefinition definition) {
        // 遍历字段定义的参数 (参数包含简单类型参数和 Input 类型参数)
        return definition.getArguments().stream().anyMatch(arg -> {
            // 判断参数上是否标记了指定的处理器标识
            if (hasDirective(arg)) {
                return true;
            }

            // 若参数上为标记所需的处理器标识, 则进一步查看参数类型是否为 Input 类型
            var inputType = GraphQLTypeUtil.unwrapNonNull(arg.getType());
            if (!(inputType instanceof GraphQLInputObjectType inputObjectType)) {
                // 如果参数类型非 GraphQLInputObjectType 类型, 则返回 false
                return false;
            }

            // 遍历 Input 类型的每个字段, 查看字段上是否标注了指定的处理器标识
            return inputObjectType.getFieldDefinitions().stream().anyMatch(LengthDirective::hasDirective);
        });
    }

    /**
     * 对具备处理器标识的参数值 (或 Input 类型字段值) 进行处理
     *
     * @param it    包含处理器标识的参数或字段对象
     * @param env   {@link DataFetcher} 提供的环境对象
     * @param value 包含处理器标识的参数或字段对象的值
     * @return 错误信息集合
     */
    private static List<GraphQLError> apply(
            GraphQLDirectiveContainer container, DataFetchingEnvironment env, Object value) {
        // 获取参数或 Input 字段值上定义的处理器标识
        var directive = container.getAppliedDirective(DIRECTIVE);

        // 获取处理器参数 (min 和 max 参数)
        var min = (IntValue) directive.getArgument(DIRECTIVE_ARG_MIN).getArgumentValue().getValue();
        var max = (IntValue) directive.getArgument(DIRECTIVE_ARG_MAX).getArgumentValue().getValue();

        // 判断参数值或字段值的长度是否符合要求
        if ((value instanceof String s)
            && (s.length() < min.getValue().intValue() || s.length() > max.getValue().intValue())) {
            // 创建错误对象
            return List.of(
                GraphqlErrorBuilder
                        .newError(env)
                        .message(
                            "Argument value \"%s\" is out of range. The range is %d to %d.",
                            value,
                            min.getValue().intValue(),
                            max.getValue().intValue())
                        .build());
        }
        return List.of();
    }

    /**
     * 当查询到标注指定处理器的字段时, 执行的方法
     */
    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        var definition = env.getElement();
        if (!appliesTo(definition)) {
            return definition;
        }

        var wrappedDataFetcher = DataFetcherFactories.wrapDataFetcher(env.getFieldDataFetcher(), (dfEnv, value) -> {
            var errors = new ArrayList<GraphQLError>();

            dfEnv.getFieldDefinition().getArguments().stream().forEach(arg -> {
                if (hasDirective(arg)) {
                    errors.addAll(apply(arg, dfEnv, dfEnv.getArgument(arg.getName())));
                }

                var inputType = GraphQLTypeUtil.unwrapNonNull(arg.getType());
                if (inputType instanceof GraphQLInputObjectType inputObjType) {
                    inputObjType.getFieldDefinitions().stream()
                            .filter(LengthDirective::hasDirective)
                            .forEach(field -> {
                                var values = dfEnv.<Map<String, Object>>getArgument(arg.getName());
                                if (values != null) {
                                    errors.addAll(apply(field, dfEnv, values.get(field.getName())));
                                }
                            });
                }
            });

            if (errors.isEmpty()) {
                return value;
            }

            return DataFetcherResult.newResult()
                    .data(value)
                    .errors(errors)
                    .localContext(dfEnv.getLocalContext())
                    .build();
        });

        return env.setFieldDataFetcher(wrappedDataFetcher);
    }
}
