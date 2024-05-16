package alvin.study.springboot.jooq.core.context;

/**
 * 上下文内容无法找到时抛出的异常
 */
public class NoContextAttributeException extends RuntimeException {
    public NoContextAttributeException(String message) {
        super(message);
    }

    public NoContextAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoContextAttributeException(Throwable cause) {
        super(cause);
    }
}
