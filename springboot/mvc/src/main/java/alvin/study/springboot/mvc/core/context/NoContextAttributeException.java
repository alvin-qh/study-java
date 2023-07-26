package alvin.study.springboot.mvc.core.context;

/**
 * 当从 {@link Context} 对象中获取 {@code Key} 不存在时抛出的异常
 *
 * @see Context#get(String)
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
