package alvin.study.springboot.graphql.core.exception;

import java.util.Arrays;
import java.util.Collection;

/**
 * 调用 API 时因为输入参数错误导致的异常类型
 */
public class InputException extends ApiException {
    /**
     * 构造器, 创建 {@link InputException} 对象
     */
    public InputException() {
        super(ErrorCode.INPUT_ERROR);
    }

    /**
     * 构造器, 创建 {@link InputException} 对象
     *
     * @param cause 异常原因
     */
    public InputException(Throwable cause) {
        super(ErrorCode.INPUT_ERROR, cause);
    }

    /**
     * 构造器, 创建 {@link InputException} 对象
     *
     * @param reason 输入参数错误原因
     */
    public InputException(String reason) {
        super(ErrorCode.INPUT_ERROR);
        addExtension(ErrorExtensionCode.REASON, reason);
    }

    /**
     * 设置输入参数错误原因
     *
     * @param fieldErrors 字段校验相关的错误对象集合
     * @return 当前异常对象
     */
    public InputException setFieldError(Collection<FieldError> fieldErrors) {
        this.addExtension(ErrorExtensionCode.ERROR_FIELDS, fieldErrors);
        return this;
    }

    /**
     * 设置输入参数错误原因
     *
     * @param fieldErrors 字段校验相关的错误对象集合
     * @return 当前异常对象
     */
    public InputException setFieldError(FieldError... fieldErrors) {
        this.addExtension(ErrorExtensionCode.ERROR_FIELDS, Arrays.asList(fieldErrors));
        return this;
    }
}
