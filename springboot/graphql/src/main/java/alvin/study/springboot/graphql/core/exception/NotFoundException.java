package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

/**
 * 调用 API 时因为输入参数错误导致的异常类型
 */
public class NotFoundException extends GraphqlBaseException {
    /**
     * 构造器, 创建 {@link NotFoundException} 对象
     *
     * @param reason 输入参数错误原因
     */
    public NotFoundException(String reason) {
        super(ErrorCode.NOT_FOUND, reason, ErrorType.BAD_REQUEST);
    }

    /**
     * 构造器, 创建 {@link NotFoundException} 对象
     *
     * @param cause 异常原因
     */
    public NotFoundException(Throwable cause) {
        super(ErrorCode.NOT_FOUND, cause, ErrorType.BAD_REQUEST);
    }

    public NotFoundException(String reason, Throwable cause) {
        super(ErrorCode.NOT_FOUND, reason, cause, ErrorType.FORBIDDEN);
    }
}
