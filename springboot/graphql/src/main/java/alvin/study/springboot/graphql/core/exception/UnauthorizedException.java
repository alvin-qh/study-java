package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

public class UnauthorizedException extends GraphqlBaseException {
    public UnauthorizedException(String message) {
        super(message, ErrorType.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, ErrorType.UNAUTHORIZED);
    }
}
