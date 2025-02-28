package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

public class ForbiddenException extends GraphqlBaseException {
    public ForbiddenException(String reason) {
        super(ErrorCode.FORBIDDEN, reason, ErrorType.FORBIDDEN);
    }

    public ForbiddenException(Throwable cause) {
        super(ErrorCode.FORBIDDEN, cause, ErrorType.FORBIDDEN);
    }

    public ForbiddenException(String reason, Throwable cause) {
        super(ErrorCode.FORBIDDEN, reason, cause, ErrorType.FORBIDDEN);
    }
}
