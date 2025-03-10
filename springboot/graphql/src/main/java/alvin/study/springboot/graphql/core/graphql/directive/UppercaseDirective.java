package alvin.study.springboot.graphql.core.graphql.directive;

import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

/**
 * 定义 {@code @uppercase} 字段处理器
 *
 * <p>
 * 本例中对于标注 {@code @uppercase} 处理器的字段转为大写
 * </p>
 *
 * <p>
 * 一个字段处理器的典型处理方式如下:
 * <ol>
 * <li>
 * 当框架在解析 {@code *.graphqls} 文件时, 遇到相关字段 (本例中为查询 schema 的字段), 会调用到
 * {@link #onField(SchemaDirectiveWiringEnvironment)} 方法进行处理
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
 * 参考 {@code classpath:graphql/type.graphqls} 中对 {@code @uppercase} directive
 * 的定义
 * </p>
 *
 * <p>
 * 参考 {@code classpath:graphql/org.graphqls} 中对 {@code Org} 类型的定义, 表示查询
 * {@code Org} 的 {@code name} 字段时, 会自动的转为大写字母
 * </p>
 *
 * <p>
 * 本类型在 {@link alvin.study.springboot.graphql.conf.GraphqlConfig#runtimeWiringConfigurer()
 * GraphqlConfig.runtimeWiringConfigurer()} 方法中进行注册
 * </p>
 */
public class UppercaseDirective implements SchemaDirectiveWiring {
    /**
     * 当查询到标注指定处理器的字段时, 执行的方法
     */
    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        var dataFetcher = DataFetcherFactories.wrapDataFetcher(env.getFieldDataFetcher(), (dataFetcherEnv, value) -> {
            if (value instanceof String s) {
                return s.toUpperCase();
            }
            return value.toString().toUpperCase();
        });

        return env.setFieldDataFetcher(dataFetcher);
    }
}
