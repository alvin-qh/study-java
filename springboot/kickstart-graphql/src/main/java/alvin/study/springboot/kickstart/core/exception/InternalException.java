package alvin.study.springboot.kickstart.core.exception;

import com.google.common.base.Strings;
import graphql.ErrorType;

import java.util.Map;

/**
 * 调用 API 时因为内部错误导致异常
 */
public class InternalException extends GraphqlBaseException {
    // 异常原因
    private final String reason;

    public InternalException(String reason) {
        super(ErrorCode.INTERNAL_ERROR, ErrorType.ExecutionAborted);
        this.reason = reason;
    }

    public InternalException(Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, cause, ErrorType.ExecutionAborted);
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
