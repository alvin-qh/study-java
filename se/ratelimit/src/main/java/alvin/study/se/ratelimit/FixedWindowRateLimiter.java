package alvin.study.se.ratelimit;

/**
 * 固定窗口限流
 *
 * <p>
 * 这是最简单的限流算法, 其原理如下:
 * <ol>
 * <li>
 * 设定固定时间窗口 ({@code windowUnit}) 以及时间窗口内可调用的次数 ({@code threshold});
 * </li>
 * <li>
 * 每次请求调用前, 先看是否在时间窗口内 ({@code windowUnit})
 * </li>
 * <li>
 * 如果超出时间窗口, 则开启新的时间窗口
 * </li>
 * <li>
 * 如果还在之前的时间窗口, 则查看已经访问的次数是否超出限制 ({@code threshold})
 * </li>
 * </ol>
 * <p>
 * 整个过程如下图所示:
 * </p>
 *
 * <p>
 * <img src="../../../../../../assets/fixwindow-rate-limiter.png">
 * </p>
 *
 * <p>
 * 固定窗口限流算法的明显缺陷是: 无法处理临界问题
 * </p>
 */
public class FixedWindowRateLimiter implements RateLimiter {
    // 窗口时间, 单位毫秒
    private final long windowUnit;
    // 窗口大小
    private final int threshold;

    // 当前请求数量
    private volatile int count = 0;
    // 时间窗口的起点
    private volatile long windowStartTime = 0L;

    /**
     * 构造器, 构造限流器对象
     *
     * @param windowUnit 窗口大小, 单位毫秒
     * @param threshold  调用数量上限
     */
    public FixedWindowRateLimiter(long windowUnit, int threshold) {
        this.windowUnit = windowUnit;
        this.threshold = threshold;
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException("permits must large than 0");
        }

        // 获取当前时间
        long now = System.currentTimeMillis();

        // 计算固定窗口, 即当前时间减去窗口起始时间, 如果超出窗口大小, 则新开一个窗口
        if (now - windowStartTime > windowUnit) {
            count = 0;
            windowStartTime = now;
        }

        // 计算窗口内调用次数是否超出上限
        if (count + permits - 1 >= threshold) {
            return false;
        }

        count += permits;
        return true;
    }
}
