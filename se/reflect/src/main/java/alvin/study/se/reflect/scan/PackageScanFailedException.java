package alvin.study.se.reflect.scan;

/**
 * 包扫描失败异常类型
 */
public class PackageScanFailedException extends RuntimeException {
    public PackageScanFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
