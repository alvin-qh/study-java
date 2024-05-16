package alvin.study.springboot.kickstart.core.graphql.context;

import alvin.study.springboot.kickstart.conf.GraphqlConfig;
import graphql.GraphQLContext;

/**
 * 表示一个 {@link GraphQLContext} 解析器, 即对 Graphql 上下文进行处理
 *
 * <p>
 * 该类型对象会统一注入到 {@link GraphqlConfig#contextResolvers
 * GraphqlConfig.contextResolvers} 集合字段中, 在
 * {@link GraphqlConfig#beginExecution(graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters)
 * GraphqlConfig.beginExecution(InstrumentationExecutionParameters)} 方法中统一调用处理
 * </p>
 */
public interface GraphQLContextResolver {
    /**
     * 对 Graphql 请求上下文进行解析的方法
     *
     * @param context Graphql 请求上下文对象
     */
    void resolve(GraphQLContext context);
}
