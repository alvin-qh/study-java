package alvin.study.se.security.keystore;

/**
 * 密码库异常类型
 */
public class KeyStoresException extends RuntimeException {
    /**
     * 构造器, 通过已知异常构建对象
     *
     * @param cause 导致当前异常的已知异常
     */
    public KeyStoresException(Throwable cause) {
        super(cause);
    }
}
