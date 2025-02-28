package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

/**
 * 调用 API 时因为内部错误导致异常
 */
public class InternalException extends GraphqlBaseException {
    /**
     * 构造器, 创建一个 {@link InternalException} 对象
     *
     * @param cause 异常原因
     */
    public InternalException(Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, "", cause, ErrorType.INTERNAL_ERROR);
    }
}
