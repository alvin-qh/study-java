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

    /**
     * 测试单线程定时线程池
     *
     * <p>
     * 通过 {@link Executors#newSingleThreadScheduledExecutor()}
     * 方法可以创建一个单线程的定时线程池
     * </p>
     *
     * <p>
     * 该线程池会通过 {@link java.util.concurrent.ScheduledExecutorService#schedule(Runnable, long, TimeUnit)
     * ScheduledExecutorService.schedule(Runnable, long, TimeUnit)}
     * 方法将任务放入任务队列中, 并在参数指定的时间后执行该任务
     * </p>
     *
     * <p>
     * 该线程池只实例化一个线程, 并通过一个不限长度的任务队列
     * {@link java.util.concurrent.LinkedBlockingQueue LinkedBlockingQueue}
     * 来缓存待执行的任务
     * </p>
     */
    @Test
    @SneakyThrows
    void newSingleThreadScheduledExecutor_shouldExecuteScheduleTask() {
        // 创建一个单线程的定时线程池
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            // 定义数组, 并在数组第一项记录任务开始执行的时间
            var timer = new long[] {
                System.currentTimeMillis(),
                0L
            };

            // 向线程池中加入一个任务, 该任务在 100ms 后执行
            var future = executor.schedule(() -> {
                synchronized (timer) {
                    // 记录任务执行时间
                    timer[1] = System.currentTimeMillis();

                    // 发送通知, 表示任务执行完毕
                    timer.notify();
                }
            }, 100, TimeUnit.MILLISECONDS);

            // 等待任务执行完毕
            future.get();

            // 确认任务在 100ms 后执行
            then(timer[1] - timer[0]).isGreaterThanOrEqualTo(100L);
        }
    }

    /**
     * 测试异步线程执行器
     *
     * <p>
     * 通过 {@link Executors#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory ThreadFactory)
     * Executors.newThreadPerTaskExecutor(ThreadFactory)}
     * 方法可以创建一个特殊线程池, 该线程池会为每个任务创建一个线程
     * </p>
     *
     * <p>
     * 该线程池内部也不存在任务队列, 即每加入一个任务, 就立即创建一个新线程,
     * 任务执行完毕, 对应的线程也会立即结束
     * </p>
     *
     * <p>
     * 大部分情况下, 无需使用该线程池, 因为其结果和直接创建线程的执行结果一致,
     * 但作为线程池, 其依然具备线程池的一些特性, 即通过
     * {@link java.util.concurrent.ExecutorService ExecutorService}
     * 接口的一系列方法对线程池内的线程进行统一管理
     * </p>
     */
    @Test
    @SneakyThrows
    void newSingleThreadScheduledExecutor_shouldExecutePeriodicTask() {
        var threadIds = new ArrayList<Long>();

        try (var executor = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory())) {
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

        // 确认 10 个线程的 ID 都不相同, 即每个任务都在不同的线程中执行
        then(Set.copyOf(threadIds)).hasSize(10);
    }

    /**
     * 测试虚拟线程执行器
     *
     * <p>
     * 通过 {@link Executors#newVirtualThreadPerTaskExecutor()}
     * 方法可以创建一个虚拟线程执行器, 该执行器会为每个任务创建一个虚拟线程
     * </p>
     *
     * <p>
     * 该执行器内部也不存在任务队列, 即每加入一个任务, 就立即创建一个新虚拟线程,
     * 任务执行完毕, 对应的虚拟线程也会立即结束
     * </p>
     *
     * <p>
     * {@link Executors#newVirtualThreadPerTaskExecutor()}
     * 方法内部调用了 {@link Executors#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory ThreadFactory)
     * Executors.newThreadPerTaskExecutor(ThreadFactory)} 方法,
     * 并通过 {@link java.lang.Thread#ofVirtual() Thread.ofPlatform()}
     * 创建线程工厂 {@link java.util.concurrent.ThreadFactory ThreadFactory}
     * 对象
     * </p>
     *
     * <p>
     * 大部分情况下, 无需使用该线程池, 因为其结果和直接创建虚拟线程的执行结果一致,
     * 但作为线程池, 其依然具备线程池的一些特性, 即通过
     * {@link java.util.concurrent.ExecutorService ExecutorService}
     * 接口的一系列方法对线程池内的线程进行统一管理
     * </p>
     */
    @Test
    @SneakyThrows
    void newVirtualThreadPerTaskExecutor_shouldExecutePeriodicTaskByVirtualThread() {
        var threadIds = new ArrayList<Long>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 向虚拟线程池中加入 10 个任务
            for (var i = 0; i < 10; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前虚拟线程的 ID
                        threadIds.add(Thread.currentThread().threadId());

                        // 发送通知, 表示虚拟线程结束
                        threadIds.notify();
                    }
                });
            }
        }

        // 等待所有虚拟线程发送通知, 且 10 个线程都写入了自己的虚拟线程 ID
        for (var i = 0; i < 10; i++) {
            synchronized (threadIds) {
                if (threadIds.size() < 10) {
                    threadIds.wait();
                }
            }
        }

        // 确认 10 个虚拟线程都执行完毕
        then(threadIds).hasSize(10);

        // 确认 10 个虚拟线程的 ID 都不相同, 即每个任务都在不同的虚拟线程中执行
        then(Set.copyOf(threadIds)).hasSize(10);
    }

    /**
     * 测试固定线程数量线程池执行器
     *
     * <p>
     * 通过 {@link Executors#newFixedThreadPool(int)}
     * 方法可以创建一个固定线程数量的线程池,
     * 该线程池会通过一个长度无限制的任务队列缓存任务对象,
     * 并通过线程池中预设数量的线程对任务进行执行
     * </p>
     *
     * <p>
     * 当任务队列的任务数超过线程池中可用线程数时, 后续任务会在消息队列中等待,
     * 直到线程池中有可用线程时, 该任务才会被执行, 有可能会因为线程任务执行缓慢,
     * 导致任务队列中任务挤压, 故在生产环境下不推荐该使用该线程池,
     * 而应该通过创建 {@link java.util.concurrent.ThreadPoolExecutor
     * ThreadPoolExecutor} 类对象来使用线程池, 并定义固定长度的任务队列,
     * 且定义在任务队列满的情况下的任务淘汰策略, 参考
     * {@link alvin.study.se.concurrent.util.ThreadPool#arrayBlockingQueueExecutor(int)
     * ThreadPool.arrayBlockingQueueExecutor(int)} 方法
     * </p>
     *
     * <p>
     * 在定义线程池线程数量时, 一般要参照当前系统 CPU 的核心数量,
     * 可以将线程数量定义为和 CPU 核心数量一致, 或为核心数量的 2 倍,
     * 定义线程数量太多时, 会导致线程间切换效率低下, 反而不利于任务并发
     * (线程都为 IO 密集型的情况除外)
     * </p>
     */
    @Test
    @SneakyThrows
    void newFixedThreadPool_shouldExecuteTasksByFixedThreadCount() {
        // 获取 CPU 逻辑核心数
        var cpuCount = Runtime.getRuntime().availableProcessors();

        var threadIds = new ArrayList<Long>();

        try (var executor = Executors.newFixedThreadPool(cpuCount)) {
            for (var i = 0; i < cpuCount * 2; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前虚拟线程的 ID
                        threadIds.add(Thread.currentThread().threadId());

                        // 发送通知, 表示虚拟线程结束
                        threadIds.notify();
                    }
                });
            }
        }

        // 等待所有虚拟线程发送通知, 且 10 个线程都写入了自己的虚拟线程 ID
        for (var i = 0; i < cpuCount * 2; i++) {
            synchronized (threadIds) {
                if (threadIds.size() < 10) {
                    threadIds.wait();
                }
            }
        }

        // 确认 10 个虚拟线程都执行完毕
        then(threadIds).hasSize(cpuCount * 2);

        // 确认 10 个虚拟线程的 ID 都不相同, 即每个任务都在不同的虚拟线程中执行
        then(Set.copyOf(threadIds)).hasSize(cpuCount);
    }
}
