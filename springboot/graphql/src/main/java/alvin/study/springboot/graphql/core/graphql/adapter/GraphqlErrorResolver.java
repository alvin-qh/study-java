package alvin.study.springboot.graphql.core.graphql.adapter;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.core.exception.ErrorCode;
import alvin.study.springboot.graphql.core.exception.ErrorExtensionCode;
import alvin.study.springboot.graphql.core.exception.GraphqlBaseException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

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
    private static final String UNKNOWN_ERROR = "Unknown error";

    @Override
    protected GraphQLError resolveToSingleError(
            @NonNull Throwable ex,
            @NonNull DataFetchingEnvironment env) {
        if (ex instanceof GraphqlBaseException e) {
            log.error("GraphQL error: {}", e.getMessage(), e);
            return GraphqlErrorBuilder.newError()
                    .message(e.getMessage())
                    .errorType(e.getClassification())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(e.toExtensions())
                    .build();
        }

        if (ex instanceof DataIntegrityViolationException) {
            log.error("SQL error: {}", ex.getMessage(), ex);
            if (ex instanceof DuplicateKeyException) {
                return GraphqlErrorBuilder.newError()
                        .message(ErrorCode.DUPLICATED_KEY)
                        .errorType(ErrorType.BAD_REQUEST)
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .extensions(Map.of(ErrorExtensionCode.REASON, "Entity key was duplicated"))
                        .build();
            }
            return GraphqlErrorBuilder.newError()
                    .message(ErrorCode.INTERNAL_ERROR)
                    .errorType(ErrorType.BAD_REQUEST)
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }

        if (ex instanceof NumberFormatException e) {
            log.error("Input error: {}", e.getMessage(), e);
            return GraphqlErrorBuilder.newError()
                    .message(ErrorCode.INPUT_ERROR)
                    .errorType(ErrorType.BAD_REQUEST)
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(Map.of(ErrorExtensionCode.REASON, "Input cannot convert to number"))
                    .build();
        }

        log.error("{}: {}", UNKNOWN_ERROR, ex.getMessage(), ex);
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ErrorCode.INTERNAL_ERROR)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
