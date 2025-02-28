package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

public class UnauthorizedException extends GraphqlBaseException {
    public UnauthorizedException(String reason) {
        super(ErrorCode.UNAUTHORIZED, reason, ErrorType.UNAUTHORIZED);
    }

    public UnauthorizedException(Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, cause, ErrorType.UNAUTHORIZED);
    }

    public UnauthorizedException(String reason, Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, reason, cause, ErrorType.FORBIDDEN);
    }
}
