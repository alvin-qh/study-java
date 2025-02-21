package alvin.study.guava.future;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.RetryingTest;

import com.google.common.util.concurrent.Monitor;

/**
 * 测试通过 {@link Monitor} 类型对象进行并发控制
 */
class MonitorTest {
    /**
     * 演示在没有并发控制情况下会出现并发冲突问题
     *
     * <p>
     * 代码中设置了 {@code 100} 个线, 每个线程将一个公共的 {@link Counter} 对象的计数值增加 {@code 1000} 次
     * </p>
     *
     * <p>
     * 所以本例中一共对计数器对象进行了 {@code 100 * 1000} 次的递增操作, 但实际的结果却小于这个数字, 这是由于多线程在操作同一内存区域时,
     * 造成的访问问题
     * </p>
     */
    @RetryingTest(3)
    void parallelAdd_shouldAddInMultiThreadWithoutLock() throws Exception {
        // 定义计数器对象
        var counter = new Counter();

        // 定义线程组对象
        var group = new ThreadGroup();

        // 创建 100 个线程, 每个线程将计数器增加 1000 次
        for (var i = 0; i < 100; i++) {
            group.add(new Thread(() -> {
                // 将计数值增加 1000 次
                for (var n = 0; n < 1000; n++) {
                    counter.inc();
                }
            }));
        }

        // 启动所有线程
        group.startAll();

        // 等待所有线程执行完毕
        group.joinAll(10);

        // 确认最终计数结果和计算次数有差异
        then(counter.getCount())
                .isLessThan(100 * 1000);
    }

    /**
     * 设置无条件锁
     *
     * <p>
     * 通过 {@link Monitor#enter()} 方法可以进入一个无条件锁, 进入锁的线程将独占该锁, 直到调用了 {@link Monitor#leave()}
     * 方法提出对锁的占用
     * </p>
     *
     * <p>
     * 通过 {@link Monitor#enter()} 方法可以进入一个无条件锁, 进入锁的线程将独占该锁, 直到调用了 {@link Monitor#leave()}
     * 方法结束对锁的占用
     * </p>
     *
     * <p>
     * 本例的操作和 JDK 中 {@link java.util.concurrent.locks.Condition Condition} 类型的使用非常类似
     * </p>
     */
    @Test
    void monitor_shouldAddInMultiThreadWithoutMonit() throws Exception {
        // 定义计数器对象
        var counter = new Counter();

        // 定义用于并发控制的 Monitor 对象
        var monitor = new Monitor();

        // 定义线程组对象
        var group = new ThreadGroup();

        // 创建 100 个线程, 每个线程将计数器增加 1000 次
        for (var i = 0; i < 100; i++) {
            group.add(new Thread(() -> {
                // 将计数值增加 1000 次
                for (var n = 0; n < 1000; n++) {
                    monitor.enter();
                    try {
                        counter.inc();
                    } finally {
                        monitor.leave();
                    }
                }
            }));
        }

        // 启动所有线程
        group.startAll();
        // 等待所有线程执行完毕
        group.joinAll(10);

        // 确认最终计数结果和计算次数有差异
        then(counter.getCount()).isEqualTo(100 * 1000);
    }
}
