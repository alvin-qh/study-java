package alvin.study.springboot.graphql.core.graphql.adapter;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import alvin.study.springboot.graphql.core.exception.GraphqlBaseException;

/**
 * Graphql 错误处理器
 *
 * <p>
 * 该类型用于全局性的处理 Graphql 执行过程中产生的异常, 返回格式化后的, 符合 Graphql 格式标准的错误信息
 * </p>
 *
 * <p>
 * 该类型要正常工作, 需要两个前提条件
 * <ul>
 * <li>
 * 实现 {@link GraphQLQueryResolver} 接口, 表示当前类型同时是一个类型解析器
 * </li>
 * <li>
 * 要在 {@code classpath:application.yml} 文件中设置
 * {@code graphql.servlet.exception-handlers-enabled=true}, 开启全局异常处理器
 * </li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class GraphqlErrorResolver extends DataFetcherExceptionResolverAdapter {
    private static final String UNKNOWN_ERROR_REASON = "Unknown error reason";

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof GraphqlBaseException e) {
            return GraphqlErrorBuilder.newError()
                    .errorType(e.getErrorType())
                    .message(e.getMessage())
                    .extensions(e.toExtensions())
                    .build();
        }

        return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.ExecutionAborted)
                    .message(UNKNOWN_ERROR_REASON)
                    .build();
    }
}
