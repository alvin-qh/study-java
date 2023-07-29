package alvin.study.se.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link FixedWindowRateLimiter} 类型, 通过固定窗口限流类型
 */
class FixedWindowRateLimiterTest extends RateLimiterTest {
    /**
     * 测试 {@link FixedWindowRateLimiter#tryAcquire(int)} 方法, 通过固定窗口进行限流
     *
     * <p>
     * 本次测试参数值为 {@code 1} 的情况
     * </p>
     */
    @Test
    void testTryAcquire_shouldLimitOneByOne() {
        // 实例化固定窗口限流对象, 时间窗口 1s, 限制 50 次请求
        var limiter = new FixedWindowRateLimiter(1000, 50);

        // 记录通过限流的调用次数
        var executeCount = new AtomicInteger();
        // 记录被限流的调用次数
        var blockedCount = new AtomicInteger();

        // 按每秒执行 100 次的频率执行 2s 时间
        executeByRate(100, 2, () -> {
            if (limiter.tryAcquire(1)) {
                // 记录通过限流
                executeCount.incrementAndGet();
            } else {
                // 记录被限流
                blockedCount.incrementAndGet();
            }
        });

        // 确认通过限流的次数约为 100 次
        then(executeCount.get()).isGreaterThan(70).isLessThanOrEqualTo(135);

        // 确认未通过限流的次数约为 100 次
        then(blockedCount.get()).isGreaterThan(70).isLessThanOrEqualTo(135);
    }

    /**
     * 测试 {@link FixedWindowRateLimiter#tryAcquire(int)} 方法, 通过固定窗口进行限流
     *
     * <p>
     * 本次测试参数值大于 {@code 1} 的情况
     * </p>
     */
    @Test
    void testTryAcquire_shouldLimitByBatch() {
        // 实例化固定窗口限流对象, 时间窗口 1s, 限制 50 次请求
        var limiter = new FixedWindowRateLimiter(1000, 50);

        // 先请求 30 次调用, 在限流次数范围内, 返回允许
        var r = limiter.tryAcquire(30);
        then(r).isTrue();

        // 再请求 30 次调用, 超出限流次数, 返回不允许
        r = limiter.tryAcquire(30);
        then(r).isFalse();
    }
}
