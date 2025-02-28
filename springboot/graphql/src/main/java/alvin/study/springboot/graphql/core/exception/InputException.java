package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

/**
 * 调用 API 时因为输入参数错误导致的异常类型
 */
public class InputException extends GraphqlBaseException {
    /**
     * 构造器, 创建 {@link InputException} 对象
     *
     * @param reason 输入参数错误原因
     */
    public InputException(String reason) {
        super(ErrorCode.INPUT_ERROR, reason, ErrorType.BAD_REQUEST);
    }

    /**
     * 构造器, 创建 {@link InputException} 对象
     *
     * @param cause 异常原因
     */
    public InputException(Throwable cause) {
        super(ErrorCode.INPUT_ERROR, cause, ErrorType.BAD_REQUEST);
    }

    public InputException(String reason, Throwable cause) {
        super(ErrorCode.INPUT_ERROR, reason, cause, ErrorType.FORBIDDEN);
    }
}
