package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

class ExecutorsTest {
    /**
     * 测试单线程线程池
     *
     * <p>
     * 通过 {@link Executors#newSingleThreadExecutor()}
     * 方法可以创建一个只包含一个线程的线程池
     * </p>
     *
     * <p>
     * 该线程池只实例化一个线程, 并通过一个不限长度的任务队列
     * {@link java.util.concurrent.LinkedBlockingQueue LinkedBlockingQueue}
     * 来缓存待执行的任务
     * </p>
     *
     * <p>
     * 注意, 一旦执行线程死锁或执行速度过慢, 有可能会导致消息队列迅速膨胀,
     * 故一般情况下不推荐使用 {@link Executors#newSingleThreadExecutor()}
     * 方法来创建线程池, 而应该采用固定长度的任务队列,
     * 并设置任务队列满后的任务淘汰策略, 参见
     * {@link alvin.study.se.concurrent.util.ThreadPool#arrayBlockingQueueExecutor(int)
     * ThreadPool#arrayBlockingQueueExecutor(1)} 方法
     * </p>
     */
    @Test
    @SneakyThrows
    void newSingleThreadExecutor_shouldExecuteTaskInSingleThread() {
        var threadIds = new ArrayList<Long>();

        // 创建一个单线程线程池
        try (var executor = Executors.newSingleThreadExecutor()) {
            // 向线程池中加入 10 个任务
            for (var i = 0; i < 10; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前线程的 ID
                        threadIds.add(Thread.currentThread().threadId());

                        // 发送通知, 表示线程结束
                        threadIds.notify();
                    }
                });
            }

            // 等待所有线程发送通知, 且 10 个线程都写入了自己的线程 ID
            while (threadIds.size() < 10) {
                synchronized (threadIds) {
                    if (threadIds.size() < 10) {
                        threadIds.wait();
                    }
                }
            }
        }

        // 确认 10 个线程都执行完毕
        then(threadIds).hasSize(10);

        // 确认 10 个线程的 ID 都相同, 即本质上都是在一个线程中执行
        then(Set.copyOf(threadIds)).hasSize(1);
    }

    @Test
    @SneakyThrows
    void newSingleThreadScheduledExecutor_shouldExecuteScheduleTask() {
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            var timer = new long[] {
                System.currentTimeMillis(),
                0L
            };

            var future = executor.schedule(() -> {
                synchronized (timer) {
                    timer[1] = System.currentTimeMillis();
                    timer.notify();
                }
            }, 100, TimeUnit.MILLISECONDS);

            future.get();
            then(timer[1] - timer[0]).isGreaterThanOrEqualTo(100L);
        }
    }
}
