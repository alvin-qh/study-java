package alvin.study.se.ratelimit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 限流测试超类
 */
abstract class RateLimiterTest {
    /**
     * 按指定频率和执行时长来执行回调函数
     *
     * @param ratePerSecond 执行频率, 即每秒调用的次数
     * @param untilSecond   执行时长, 单位为秒
     * @param runner        要执行的回调函数
     */
    protected void executeByRate(int ratePerSecond, int untilSecond, Runnable runner) {
        for (var i = 0; i < ratePerSecond * untilSecond; i++) {
            runner.run();
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1) / ratePerSecond);
        }
    }
}
