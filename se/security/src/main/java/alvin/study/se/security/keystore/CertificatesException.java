package alvin.study.se.security.keystore;

/**
 * 表示证书异常的异常类型
 */
public class CertificatesException extends RuntimeException {
    /**
     * 根据一个表示原因的其它异常实例化当前异常对象
     *
     * @param cause 导致当前异常的其它异常对象
     */
    public CertificatesException(Throwable cause) {
        super(cause);
    }
}
