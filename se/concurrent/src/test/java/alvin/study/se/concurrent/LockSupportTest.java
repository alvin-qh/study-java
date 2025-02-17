package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.jupiter.api.Test;

/**
 * 演示通过 {@link LockSupport} 类型执行更加底层的缩操作
 */
class LockSupportTest {
    /**
     * 将秒数转换为纳秒数
     *
     * @param seconds 要转换的秒数
     * @return 转换后的纳秒数
     */
    private static long toNanos(long seconds) {
        return TimeUnit.SECONDS.toNanos(seconds);
    }

    /**
     * 将纳秒数转换为秒数
     *
     * @param nanos 要转换的纳秒数
     * @return 转换后的秒数
     */
    private static long toSeconds(long nanos) {
        return TimeUnit.NANOSECONDS.toSeconds(nanos);
    }

    /**
     * 将线程阻塞指定的时间
     *
     * <p>
     * 通过 {@link LockSupport#parkNanos(long)} 方法可以将当前线程阻塞指定的时间 (时间单位为纳秒), 并在等待指定时间后唤醒线程,
     * 该方法类似于 {@link Thread#sleep(long)} 方法
     * </p>
     *
     * <p>
     * 和 {@link Thread#sleep(long)} 方法不同, {@code parkNanos} 方法更为底层, 而 {@code sleep} 方法是借助
     * {@link LockSupport#park()} 和自旋锁实现, 会对系统资源有占用
     * </p>
     *
     * <p>
     * {@link Thread#sleep(long)} 方法在线程 {@code interrupt} 后会抛出 {@link InterruptedException} 异常来中断阻塞, 而
     * {@link LockSupport#parkNanos(long)} 方法只是结束阻塞, 继续执行后续代码, 但线程状态已经为 {@code interrupted} (即
     * {@link Thread#isInterrupted()}) 会返回 {@code true}
     * </p>
     *
     * <p>
     * 在执行 {@link LockSupport#parkNanos(long)} 过程中的线程状态为 {@code TIMED_WAITING}, 这一点和执行
     * {@link Thread#sleep(long)} 方法以及 {@link Object#wait(long)} 是一致的
     * </p>
     */
    @Test
    void parkNanos_shouldSuspendThreadForAWhile() throws InterruptedException {
        var thread = new Thread(() -> {
            // 将当前线程阻塞 1 秒钟
            LockSupport.parkNanos(toNanos(1L));
        });

        var startNanos = System.nanoTime();

        // 启动线程并等待结束
        thread.start();
        thread.join();

        // 确认线程整个执行时间为 1s
        then(toSeconds(System.nanoTime() - startNanos)).isEqualTo(1L);
    }

    /**
     * 阻塞线程到指定时间
     *
     * <p>
     * 通过 {@link LockSupport#parkUntil(long)} 方法可以将当前线程阻塞到指定的时间, 并在到达指定时间后唤醒线程
     * </p>
     *
     * <p>
     * {@code parkUntil} 方法的参数是一个 UTC 时区, 从 1970-01-01 开始计数的毫秒数, 可以通过 {@link Instant} 类进行获取
     * </p>
     */
    @Test
    void parkUntil_shouldSuspendThreadToSpecifiedTime() throws InterruptedException {
        var thread = new Thread(() -> {
            // 设置当前时间 2s 后为阻塞结束时间
            var deadline = Instant.now().plus(2, ChronoUnit.SECONDS).toEpochMilli();

            // 阻塞当前线程直到指定的结束时间
            LockSupport.parkUntil(deadline);
        });

        var startNanos = System.nanoTime();

        // 启动线程并等待结束
        thread.start();
        thread.join();

        // 确认线程整个执行时间为 2s
        then(toSeconds(System.nanoTime() - startNanos)).isBetween(1L, 2L);
    }

    /**
     * 阻塞当前线程, 并由另一个线程结束阻塞
     *
     * <p>
     * 通过在当前线程执行 {@link LockSupport#park()} 方法可以对当前线程一直阻塞, 直到另一个线程执行了
     * {@link LockSupport#unpark(Thread)} 方法, 被阻塞线程解除阻塞
     * </p>
     */
    @Test
    void parkAndUnpack_shouldPackOneThreadAndUnpackAtOtherThread() throws InterruptedException {
        // 用于测试阻塞线程
        // 将当前线程阻塞
        var thread = new Thread(LockSupport::park);

        // 用于解除阻塞的线程
        new Thread(() -> {
            try {
                // 等待 1 秒
                Thread.sleep(1000);
                // 通过线程对象解除其阻塞
                LockSupport.unpark(thread);
            } catch (InterruptedException ignored) {}
        }).start();

        var startNanos = System.nanoTime();

        // 启动线程并等待结束
        thread.start();
        thread.join();

        // 确认被阻塞线程需要 1s 后解除阻塞
        then(toSeconds(System.nanoTime() - startNanos)).isEqualTo(1L);
    }

    /**
     * 阻塞当前线程并在线程间传递值
     *
     * <p>
     * 通过 {@link LockSupport#park(Object)}, {@link LockSupport#parkUntil(Object, long)} 以及
     * {@link LockSupport#parkUntil(Object, long)} 方法, 可以在阻塞当前线程的同时传递一个对象值
     * </p>
     *
     * <p>
     * 通过 {@link LockSupport#getBlocker(Thread)} 方法可以在另一个线程中, 通过被阻塞线程对象获取到阻塞线程传递的对象
     * </p>
     */
    @Test
    void parkObject_shouldPassBlockerObjectBetweenThread() throws InterruptedException {
        // 用于测试阻塞线程
        var thread = new Thread(() -> {
            // 将当前线程阻塞, 并传递一个字符串值
            LockSupport.park("Thread-A");
        });

        // 启动线程并等待结束
        thread.start();
        Thread.sleep(100);

        // 通过线程对象获取线程传递的值
        var blocker = LockSupport.getBlocker(thread);
        then(blocker).isEqualTo("Thread-A");

        LockSupport.unpark(thread);
        thread.join();
    }

    /**
     * 在线程间传递值但并不阻塞当前线程
     *
     * <p>
     * 通过 {@link LockSupport#setCurrentBlocker(Object)} 可以在线程间传递一个对象值
     * </p>
     *
     * <p>
     * 通过 {@link LockSupport#getBlocker(Thread)} 方法可以在另一个线程中, 通过被阻塞线程对象获取到阻塞线程传递的对象
     * </p>
     */
    @Test
    void setCurrentBlocker_shouldPassBlockerObjectBetweenThread() throws InterruptedException {
        var state = new Object();

        // 用于测试阻塞线程
        var thread = new Thread(() -> {
            // 在线程间传递一个字符串值
            LockSupport.setCurrentBlocker("Thread-A");

            // 阻塞并等待一个信号
            try {
                synchronized (state) {
                    state.wait();
                }
            } catch (InterruptedException ignored) {}
        });

        // 启动线程并等待结束
        thread.start();
        Thread.sleep(100);

        // 通过线程对象获取线程传递的值
        var blocker = LockSupport.getBlocker(thread);
        then(blocker).isEqualTo("Thread-A");

        // 发送通知, 解除线程阻塞
        synchronized (state) {
            state.notify();
        }

        // 等待线程结束
        thread.join();
    }
}
