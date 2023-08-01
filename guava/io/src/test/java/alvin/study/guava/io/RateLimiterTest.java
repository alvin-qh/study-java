package alvin.study.guava.io;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试速率限制类型
 *
 * <p>
 * 通过 {@link RateLimiter} 类型可以创建一个"速率限制对象", 通过该对象可以将访问速率限制在指定的范围内
 * </p>
 *
 * <p>
 * 通过 {@link RateLimiter#create(double, long, TimeUnit)} 方法可以创建 {@link RateLimiter} 对象, 其参数为:
 * <ul>
 * <li>
 * 第 1 个参数表示一个速率, 即"访问次数/s" (QPS)
 * </li>
 * <li>
 * 第 2, 3 个参数表示一个预热时间, 即访问从 {@code 0} 提升到最大限制值需要经历的时间
 * </li>
 * </ul>
 * </p>
 */
class RateLimiterTest {
    /**
     * 按照指定的速率调用函数
     *
     * @param rate   调用频率, 即"调用次数/s"
     * @param until  持续调用时间
     * @param unit   持续调用时间单位
     * @param runner 要掉用的函数
     */
    private static void callByRate(int rate, long until, TimeUnit unit, Runnable runner) {
        // 换算调用持续时间到 nano
        until = unit.toNanos(until);

        // 记录调用起始时间
        var start = System.nanoTime();
        // 计算每次调用的停顿时间
        var parkNanos = TimeUnit.SECONDS.toNanos(1) / rate;

        // 循环直到调用时间结束
        while (System.nanoTime() - start < until) {
            // 执行被调用函数
            runner.run();

            // 等待指定时间
            LockSupport.parkNanos(parkNanos);
        }
    }

    /**
     * 限制指定方法每秒的调用次数 (即调用频率)
     *
     * <p>
     * 通过 {@link RateLimiter#tryAcquire(int, long, TimeUnit)} 类型对象限制指定方法每秒的调用次数, 其中:
     * <ul>
     * <li>
     * 第 1 个参数表示要消耗的调用次数, 如果为 1, 表示进行 1 次调用
     * </li>
     * <li>
     * 第 2, 3 个参数表示如果调用被限制, 则需要等待的时间
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void limit_shouldLimitRateInPerSecond() {
        // 定义一个允许每秒调用 50 次的调用速率限制器对象
        var rateLimiter = RateLimiter.create(50.0);

        // 记录允许调用和不允许调用数量
        var acquired = new AtomicLong();
        var missed = new AtomicLong();
        var total = new AtomicLong();

        // 按每秒 100 次调用指定函数, 持续 3 秒
        callByRate(100, 3, TimeUnit.SECONDS, () -> {
            total.incrementAndGet();

            // 判断是否允许调用, 允许调用 1 次, 不进行等待
            if (rateLimiter.tryAcquire(1, 0, TimeUnit.NANOSECONDS)) {
                // 记录允许调用次数
                acquired.getAndIncrement();
            } else {
                // 记录不允许调用的次数
                missed.getAndIncrement();
            }
        });

        // 确认总体调用次数在 260~300 次左右
        then(total.get()).isGreaterThan(250).isLessThanOrEqualTo(300);

        // 确认整个过程中, 允许调用 150 次, 即 3 * 50 = 150
        then(acquired.get()).isEqualTo(150L);

        // 确认整个过程中, 不允许调用为 total - acquired, 约为 145~150 次
        then(missed.get()).isEqualTo(total.get() - acquired.get());
    }

    /**
     * 演示调用频率限制的预热过程
     *
     * <p>
     * 调用频率限制的预热指的是一个调用点从"冷"到"热"的过程, 在大部分时候, 第一次调用某个接口或者长时间不调用开始调用时, 其缓存
     * (包括数据库和内存缓存), 以及其它资源可能都会在准备过程中, 此时系统可能无法承受突然爆发的 QPS, 所以 {@link RateLimiter}
     * 允许有一个预热时间, 在此时间过程中, 逐步达到稳定的最大限制值, 例如: 预热时间为 3 秒, 最终的 QPS 限制在 50, 则第一秒的 QPS
     * 可能被限制在 20, 第二秒为 25 ... 直到在规定时间内达到最高的 50
     * </p>
     */
    @Test
    void limit_shouldLimitRateInPerSecondAndWarmup() {
        // 定义一个允许每秒调用 50 次的调用速率限制器对象, 预热时间 2 秒
        var rateLimiter = RateLimiter.create(50.0, 2, TimeUnit.SECONDS);

        // 记录调用次数的类型, 包括: 总调用次数以及成功的调用次数
        record CallRecord(long total, long acquired) {
        }

        // 记录多次调用情况的集合
        var records = Lists.<CallRecord>newArrayList();

        // 设置 4 阶段调用, 每个阶段持续 1s 时间
        for (var i = 0; i < 4; i++) {
            // 记录总共调用次数和成功调用数量
            var total = new AtomicLong();
            var acquired = new AtomicLong();

            // 按每秒 100 次调用指定函数, 持续 1 秒
            callByRate(100, 1, TimeUnit.SECONDS, () -> {
                total.incrementAndGet();

                // 判断是否允许调用, 允许调用 1 次, 不进行等待
                if (rateLimiter.tryAcquire(1, 0, TimeUnit.NANOSECONDS)) {
                    // 记录允许调用次数
                    acquired.getAndIncrement();
                }
            });

            // 记录本秒的总调用次数和成功调用次数
            records.add(new CallRecord(total.get(), acquired.get()));
        }

        // 确认总共进行了 4 个阶段的调用
        then(records).hasSize(4);
        // 确认每个阶段调用次数都在 90~100 次
        then(records).extracting("total").allMatch(total -> (long) total >= 80 && (long) total <= 100);

        // 确认成功调用次数依次上升
        then(records.get(0).acquired())
                .isLessThan(records.get(1).acquired())
                .isLessThan(records.get(2).acquired())
                .isLessThan(records.get(3).acquired());

        // 确认最后一阶段调用已经达到预设的限制值
        then(records.get(3).acquired()).isGreaterThan(30).isLessThanOrEqualTo(50);
    }
}
