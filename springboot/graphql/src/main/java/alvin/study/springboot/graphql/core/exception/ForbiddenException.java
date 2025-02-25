package alvin.study.springboot.graphql.core.exception;

import org.springframework.graphql.execution.ErrorType;

public class ForbiddenException extends GraphqlBaseException {
    public ForbiddenException(String message) {
        super(message, ErrorType.FORBIDDEN);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, ErrorType.FORBIDDEN);
    }
}
