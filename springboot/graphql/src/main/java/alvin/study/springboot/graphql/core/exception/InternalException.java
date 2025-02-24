package alvin.study.springboot.graphql.core.exception;

import java.util.Map;

import org.springframework.graphql.execution.ErrorType;

import com.google.common.base.Strings;


/**
 * 调用 API 时因为内部错误导致异常
 */
public class InternalException extends GraphqlBaseException {
    // 异常原因
    private final String reason;

    /**
     * 构造器, 创建一个 {@link InternalException} 对象
     *
     * @param reason 异常原因
     */
    public InternalException(String reason) {
        super(ErrorCode.INTERNAL_ERROR, ErrorType.INTERNAL_ERROR);
        this.reason = reason;
    }

    /**
     * 构造器, 创建一个 {@link InternalException} 对象
     *
     * @param cause 异常原因
     */
    public InternalException(Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, cause, ErrorType.INTERNAL_ERROR);
        this.reason = cause.getMessage();
    }

    @Override
    public Map<String, Object> toExtensions() {
        var extensions = super.toExtensions();
        if (!Strings.isNullOrEmpty(this.reason)) {
            extensions.put(ErrorExtensionCode.REASON, reason);
        }
        return extensions;
    }
}
