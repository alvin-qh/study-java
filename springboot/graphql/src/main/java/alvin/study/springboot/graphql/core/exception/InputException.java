package alvin.study.springboot.graphql.core.exception;

import java.util.Arrays;
import java.util.Collection;

/**
 * 调用 API 时因为输入参数错误导致的异常类型
 */
public class InputException extends ApiException {
    public InputException() {
        super(ErrorCode.INPUT_ERROR);
    }

    public InputException(Throwable cause) {
        super(ErrorCode.INPUT_ERROR, cause);
    }

    public InputException(String reason) {
        super(ErrorCode.INPUT_ERROR);
        addExtension(ErrorExtensionCode.REASON, reason);
    }

    public InputException setFieldError(Collection<FieldError> fieldErrors) {
        this.addExtension(ErrorExtensionCode.ERROR_FIELDS, fieldErrors);
        return this;
    }

    public InputException setFieldError(FieldError... fieldErrors) {
        this.addExtension(ErrorExtensionCode.ERROR_FIELDS, Arrays.asList(fieldErrors));
        return this;
    }
}
