package alvin.study.springboot.graphql.core.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

import graphql.ErrorClassification;
import graphql.GraphQLException;

/**
 * 所有和 Graphql 相关的异常类超类
 *
 * <p>
 * Graphql 处理过程中产生的异常均由
 * {@link alvin.study.springboot.graphql.core.graphql.adapter.GraphqlErrorResolver GraphQLErrorHandler}
 * 全局异常处理器进行统一处理
 * </p>
 */
@Getter
public abstract class GraphqlBaseException extends GraphQLException {
    // 错误分类对象
    private final ErrorClassification errorType;

    /**
     * 构造器, 默认错误分类为 {@link ErrorClassification#toSpecification()}
     *
     * @param message   错误信息
     * @param errorType 错误分类对象
     */
    public GraphqlBaseException(String message, ErrorClassification errorType) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * 构造器, 默认错误分类为 {@link ErrorClassification#toSpecification()}
     *
     * @param message   错误信息
     * @param cause     异常原因
     * @param errorType 错误分类对象
     */
    public GraphqlBaseException(String message, Throwable cause, ErrorClassification errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    /**
     * 错误信息转化为 Graphql 错误扩展信息字段
     *
     * @return 包括扩展信息的 {@link Map} 对象
     */
    public Map<String, Object> toExtensions() {
        var extensions = new LinkedHashMap<String, Object>();
        if (this.getCause() != null) {
            extensions.put(ErrorExtensionCode.REASON, this.getCause().getMessage());
        }
        return extensions;
    }
}
