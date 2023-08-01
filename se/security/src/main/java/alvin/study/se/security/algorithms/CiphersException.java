package alvin.study.se.security.algorithms;

/**
 * 加密运算过程中出现的异常
 */
public class CiphersException extends RuntimeException {
    /**
     * 构造器
     *
     * @param t 导致当前异常的原始异常
     */
    public CiphersException(Throwable t) {
        super(t);
    }
}
