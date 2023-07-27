package alvin.study.se.ratelimit;

/**
 * 限流接口
 */
public interface RateLimiter {
    /**
     * 尝试访问资源
     *
     * @param permits 希望进行的调用次数
     * @return 是否可以进行调用
     */
    boolean tryAcquire(int permits);
}
