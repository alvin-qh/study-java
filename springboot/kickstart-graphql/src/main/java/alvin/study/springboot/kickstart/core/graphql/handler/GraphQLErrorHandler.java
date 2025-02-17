package alvin.study.springboot.kickstart.core.graphql.handler;

import alvin.study.springboot.kickstart.core.exception.ErrorCode;
import alvin.study.springboot.kickstart.core.exception.ErrorExtensionCode;
import alvin.study.springboot.kickstart.core.exception.FieldError;
import alvin.study.springboot.kickstart.core.exception.GraphqlBaseException;
import com.google.common.collect.Streams;
import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
public class GraphQLErrorHandler implements GraphQLQueryResolver {
    private static final String UNKNOWN_ERROR_REASON = "Unknown error reason";

    /**
     * 将异常转为 {@link GraphQLError} 对象, 统一错误格式
     *
     * @param errorContext        错误上下文对象
     * @param message             错误信息
     * @param extensions          额外的错误信息
     * @param errorClassification 错误分类
     * @return {@link GraphQLError} 对象
     */
    private static GraphQLError makeGraphQLError(
            @NotNull ErrorContext errorContext,
            String message,
            Map<String, Object> extensions,
            ErrorClassification errorClassification) {
        errorClassification = errorClassification == null ? errorContext.getErrorType() : errorClassification;

        if (errorContext.getExtensions() != null) {
            extensions = Streams
                    .concat(extensions.entrySet().stream(), errorContext.getExtensions().entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return GraphqlErrorBuilder.newError()
                .message(message)
                .extensions(extensions)
                .errorType(errorClassification)
                .locations(errorContext.getLocations())
                .path(errorContext.getPath())
                .build();
    }

    /**
     * 对异常记录日志
     *
     * @param t     异常对象
     * @param error Graphql 错误对象
     */
    private static void log(Throwable t, GraphQLError error) {
        log.info("Graphql error handler: {} handled, "
                 + "client message=<message={}, errorType={}, locations={}, extensions={}, path={}>",
            t.getClass().getName(),
            error.getMessage(),
            error.getErrorType(),
            error.getLocations(),
            error.getExtensions(),
            error.getPath());
    }

    /**
     * 处理所有未处理异常
     *
     * @param e            {@link Exception} 类型异常
     * @param errorContext 错误上下文对象
     * @return 将异常转化为 {@link GraphQLError} 错误对象
     */
    @ExceptionHandler(Exception.class)
    public GraphQLError handle(@NotNull Exception e, ErrorContext errorContext) {
        var message = e.getMessage() == null ? UNKNOWN_ERROR_REASON : e.getMessage();
        var err = makeGraphQLError(
            errorContext, ErrorCode.INTERNAL_ERROR, Map.of(ErrorExtensionCode.REASON, message), null);
        log(e, err);
        return err;
    }

    /**
     * 处理 {@link GraphqlBaseException} 异常
     *
     * @param e            {@link GraphqlBaseException} 类型异常, 表示所有 Graphql 执行相关异常
     * @param errorContext 错误上下文对象
     * @return 将异常转化为 {@link GraphQLError} 错误对象
     */
    @ExceptionHandler(GraphqlBaseException.class)
    public GraphQLError handle(@NotNull GraphqlBaseException e, ErrorContext errorContext) {
        var err = makeGraphQLError(
            errorContext, e.getMessage(), e.toExtensions(), e.getErrorType());
        log(e, err);
        return err;
    }

    /**
     * 处理 {@link AccessDeniedException} 异常
     *
     * @param e            {@link AccessDeniedException} 类型异常, 表示访问被拒绝异常
     * @param errorContext 错误上下文对象
     * @return 将异常转化为 {@link GraphQLError} 错误对象
     */
    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError handle(@NotNull AccessDeniedException e, ErrorContext errorContext) {
        var message = e.getMessage() == null ? UNKNOWN_ERROR_REASON : e.getMessage();
        var err = makeGraphQLError(
            errorContext, ErrorCode.UNAUTHORIZED, Map.of(ErrorExtensionCode.REASON, message), null);
        log(e, err);
        return err;
    }

    /**
     * 处理 {@link ConstraintViolationException} 异常
     *
     * @param e            {@link ConstraintViolationException} 类型异常, 表示输入字段验证异常
     * @param errorContext 错误上下文对象
     * @return 将异常转化为 {@link GraphQLError} 错误对象
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public GraphQLError handle(@NotNull ConstraintViolationException e, ErrorContext errorContext) {
        var validations = e.getConstraintViolations();

        var fieldErrors = validations == null ? List.of()
                                              : e.getConstraintViolations().stream().map(FieldError::from).toList();

        var err = makeGraphQLError(
            errorContext,
            ErrorCode.INPUT_ERROR,
            Map.of(ErrorExtensionCode.ERROR_FIELDS, fieldErrors),
            ErrorType.ValidationError);
        log(e, err);
        return err;
    }

    /**
     * 处理 {@link NoSuchElementException} 异常
     *
     * @param e            {@link NoSuchElementException} 类型异常, 表示请求的资源不存在
     * @param errorContext 错误上下文对象
     * @return 将异常转化为 {@link GraphQLError} 错误对象
     */
    @ExceptionHandler(NoSuchElementException.class)
    public GraphQLError handle(@NotNull NoSuchElementException e, ErrorContext errorContext) {
        var message = e.getMessage() == null ? UNKNOWN_ERROR_REASON : e.getMessage();
        var err = makeGraphQLError(
            errorContext,
            ErrorCode.NOT_FOUND,
            Map.of(ErrorExtensionCode.REASON, message),
            ErrorType.ValidationError);
        log(e, err);
        return err;
    }
}
