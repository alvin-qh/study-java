package alvin.study.springboot.graphql.core.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Strings;

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
public abstract class GraphqlBaseException extends GraphQLException {
    @Getter
    private final ErrorClassification classification;
    private final Map<String, Object> extensions = new LinkedHashMap<>();

    /**
     * 构造器, 默认错误分类为 {@link ErrorClassification#toSpecification()}
     *
     * @param message   错误信息
     * @param errorType 错误分类对象
     */
    public GraphqlBaseException(String message, String reason, ErrorClassification classification) {
        super(message);
        if (!Strings.isNullOrEmpty(reason)) {
            this.extensions.put(ErrorExtensionCode.REASON, reason);
        }
        this.classification = classification;
    }

    /**
     * 构造器, 默认错误分类为 {@link ErrorClassification#toSpecification()}
     *
     * @param message   错误信息
     * @param cause     异常原因
     * @param errorType 错误分类对象
     */
    public GraphqlBaseException(String message, Throwable cause, ErrorClassification classification) {
        super(message, cause);
        if (cause != null) {
            this.extensions.put(ErrorExtensionCode.REASON, cause.getMessage());
        }
        this.classification = classification;
    }

    public GraphqlBaseException(String message, String reason, Throwable cause, ErrorClassification classification) {
        super(message, cause);
        if (cause != null) {
            this.extensions.put(ErrorExtensionCode.REASON, reason);
        }
        this.classification = classification;
    }

    /**
     * 错误信息转化为 Graphql 错误扩展信息字段
     *
     * @return 包括扩展信息的 {@link Map} 对象
     */
    public Map<String, Object> toExtensions() {
        return extensions;
    }

    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }
}
