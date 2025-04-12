package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;
import alvin.study.se.concurrent.util.SystemInfo;
import alvin.study.se.concurrent.util.TimeIt;

class ExecutorsTest {
    /**
     * 测试单线程线程池
     *
     * <p>
     * 通过 {@link Executors#newSingleThreadExecutor()}
     * 方法返回一个 {@link ThreadPoolExecutor} 类型对象,
     * 表示一个只包含一个线程的线程池
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
     * {@link alvin.study.se.concurrent.util.ThreadPool#fixedPoolExecutor(int)
     * ThreadPool#fixedPoolExecutor(1)} 方法
     * </p>
     *
     * @see ThreadPoolExecutor
     */
    @Test
    @SneakyThrows
    void newSingleThreadExecutor_shouldExecuteTaskInSingleThread() {
        var threadIds = new ArrayList<Long>();

        // 创建一个单线程线程池, 并等待所有任务执行完毕后, 关闭线程池
        try (var executor = Executors.newSingleThreadExecutor()) {
            // 向线程池中加入 10 个任务
            for (var i = 0; i < 10; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前线程的 ID
                        threadIds.add(Thread.currentThread().threadId());
                    }
                });
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
     * 方法返回一个 {@link ScheduledThreadPoolExecutor} 类型对象,
     * 表示一个可对任务延迟执行的单线程线程池执行器
     * </p>
     *
     * <p>
     * 该线程池会通过 {@link ScheduledThreadPoolExecutor#schedule(
     * Runnable, long, TimeUnit)} 方法将任务放入任务队列中,
     * 并在参数指定的时间后执行该任务
     * </p>
     *
     * <p>
     * 该线程池只实例化一个线程, 并通过一个不限长度的任务队列
     * {@link java.util.concurrent.LinkedBlockingQueue LinkedBlockingQueue}
     * 来缓存待执行的任务
     * </p>
     *
     * @see ScheduledThreadPoolExecutor
     */
    @Test
    @SneakyThrows
    void newSingleThreadScheduledExecutor_shouldExecuteScheduleTask() {
        // 定义数组, 并在数组第一项记录任务开始执行的时间
        var timeits = new TimeIt[] {
            TimeIt.start(),
            TimeIt.start()
        };

        // 创建一个单线程的定时线程池, 并等待所有任务执行完毕后, 关闭线程池
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            timeits[0].restart();

            // 向线程池中加入一个任务, 该任务在 100ms 后执行
            executor.schedule(() -> {
                synchronized (timeits) {
                    // 记录任务执行时间
                    timeits[1].restart();
                }
            }, 100, TimeUnit.MILLISECONDS);
        }

        // 确认任务在 100ms 后执行
        then(timeits[0].since() - timeits[1].since()).isGreaterThanOrEqualTo(100L);
    }

    /**
     * 测试异步线程执行器
     *
     * <p>
     * 通过 {@link Executors#newThreadPerTaskExecutor(
     * java.util.concurrent.ThreadFactory ThreadFactory)
     * Executors.newThreadPerTaskExecutor(ThreadFactory)}
     * 方法可以创建一个
     * {@link java.util.concurrent.ThreadPerTaskExecutor
     * ThreadPerTaskExecutor} 类型线程池,执行器,
     * 该执行器会为每个任务创建一个新线程以此来执行该任务
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
     *
     * @see java.util.concurrent.ThreadPerTaskExecutor ThreadPerTaskExecutor
     */
    @Test
    @SneakyThrows
    void newSingleThreadScheduledExecutor_shouldExecutePeriodicTask() {
        var threadIds = new ArrayList<Long>();

        // 创建线程池, 并等待所有任务执行完毕后, 关闭线程池
        try (var executor = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory())) {
            // 向线程池中加入 10 个任务
            for (var i = 0; i < 10; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前线程的 ID
                        threadIds.add(Thread.currentThread().threadId());
                    }
                });
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
     * 方法可以创建一个 {@link java.util.concurrent.ThreadPerTaskExecutor
     * ThreadPerTaskExecutor} 线程执行器对象, 且因为该执行器的线程工厂为虚拟线程工厂,
     * ({@link Thread.Builder.OfVirtual#factory()})
     * 故该执行器会为每个任务创建一个虚拟线程
     * </p>
     *
     * <p>
     * 该执行器内部也不存在任务队列, 即每加入一个任务, 就立即创建一个新虚拟线程,
     * 任务执行完毕, 对应的虚拟线程也会立即结束
     * </p>
     *
     * <p>
     * {@link Executors#newVirtualThreadPerTaskExecutor()}
     * 方法内部调用了 {@link Executors#newThreadPerTaskExecutor(
     * java.util.concurrent.ThreadFactory ThreadFactory)
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

        // 创建线程池, 并等待所有任务执行完毕后, 关闭线程池
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 向虚拟线程池中加入 10 个任务
            for (var i = 0; i < 10; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前虚拟线程的 ID
                        threadIds.add(Thread.currentThread().threadId());
                    }
                });
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
     * 方法返回一个 {@link ThreadPoolExecutor} 类型执行器对象,
     * 且该执行器中具备固定数量的线程,
     * 该执行器会通过一个长度无限制的任务队列缓存任务对象,
     * 并通过执行器中预设数量的线程对任务进行执行
     * </p>
     *
     * <p>
     * 当任务队列的任务数超过线程池中可用线程数时, 后续任务会在消息队列中等待,
     * 直到线程池中有可用线程时, 该任务才会被执行, 有可能会因为线程任务执行缓慢,
     * 导致任务队列中任务挤压, 故在生产环境下不推荐该使用该线程池,
     * 而应该通过创建 {@link ThreadPoolExecutor} 类对象来使用线程池,
     * 并定义固定长度的任务队列, 且定义在任务队列满的情况下的任务淘汰策略, 参考
     * {@link alvin.study.se.concurrent.util.ThreadPool#fixedPoolExecutor(int)
     * ThreadPool.fixedPoolExecutor(int)} 方法
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
        var cpuCount = SystemInfo.cpuCount();

        var threadIds = new ArrayList<Long>();

        try (var executor = Executors.newFixedThreadPool(cpuCount)) {
            for (var i = 0; i < cpuCount * 2; i++) {
                executor.execute(() -> {
                    synchronized (threadIds) {
                        // 在集合中记录当前虚拟线程的 ID
                        threadIds.add(Thread.currentThread().threadId());
                    }
                });
            }
        }

        // 确认 10 个虚拟线程都执行完毕
        then(threadIds).hasSize(cpuCount * 2);

        // 确认 10 个虚拟线程的 ID 都不相同, 即每个任务都在不同的虚拟线程中执行
        then(Set.copyOf(threadIds)).hasSize(cpuCount);
    }

    /**
     * 测试延时任务线程池执行器, 在固定时间延迟后执行任务
     *
     * <p>
     * 通过 {@link Executors#newScheduledThreadPool(int)}
     * 方法可以可以创建一个 {@link ScheduledThreadPoolExecutor} 执行器,
     * 通过该执行器可以提交一个延时任务, 并指定该任务执行的延时时间,
     * 通过参数可指定线程池的最大线程数量
     * </p>
     *
     * <p>
     * 而通过 {@link Executors#newScheduledThreadPool(int,
     * java.util.concurrent.ThreadFactory)
     * Executors.newScheduledThreadPool(int, ThreadFactory)}
     * 方法则可以指定一个线程工厂方法, 用于为线程池创建线程
     * (例如创建为虚拟线程)
     * </p>
     *
     * @see ScheduledThreadPoolExecutor
     */
    @Test
    @SneakyThrows
    void newScheduledThreadPool_shouldExecuteTaskAfterWhile() {
        var timeit = TimeIt.start();

        ScheduledFuture<Long> future1, future2, future3;

        try (var executor = Executors.newScheduledThreadPool(SystemInfo.cpuCount())) {
            timeit.restart();

            // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
            future1 = executor.schedule(() -> timeit.since(), 200, TimeUnit.MILLISECONDS);
            future2 = executor.schedule(() -> timeit.since(), 100, TimeUnit.MILLISECONDS);
            future3 = executor.schedule(() -> timeit.since(), 210, TimeUnit.MILLISECONDS);
        }

        // 确认 3 个任务都执行完毕
        then(future1.isDone() && future2.isDone() && future3.isDone()).isTrue();

        // 确认任务执行时间范围
        then(timeit.since()).isBetween(210L, 310L);

        // 确认每个任务的延时时间, 和设定的延时时间一致
        then(future1.get()).isBetween(200L, 210L);
        then(future2.get()).isBetween(100L, 110L);
        then(future3.get()).isBetween(210L, 220L);
    }

    /**
     * 测试延时任务线程池执行器, 按固定频率重复执行任务
     *
     * <p>
     * 通过 {@link Executors#newScheduledThreadPool(int)} 创建的
     * {@link ScheduledThreadPoolExecutor} 执行器,
     * 也可以按指定的固定频率去重复执行某个任务, 直到显式取消该任务的继续重复执行
     * </p>
     *
     * @see ScheduledThreadPoolExecutor
     */
    @Test
    void newScheduledThreadPool_shouldScheduleTaskWithFixedRate() {
        // 记录起始时间
        var timeit = TimeIt.start();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 创建延时任务线程池
        try (var executor = Executors.newScheduledThreadPool(SystemInfo.cpuCount())) {
            timeit.restart();

            // 启动一个固定频率的定时器任务, 设置执行延迟时间和重复执行频率
            var future = executor.scheduleAtFixedRate(() -> {
                records.add(timeit.since() / 100);
            }, 100, 200, TimeUnit.MILLISECONDS);

            // 等待定时器执行 3 次 (最大耗时约 600ms) 以后, 取消定时器
            await().atMost(600, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> then(records).hasSize(3));

            // 取消定时器执行
            future.cancel(false);
        }

        // 确认 3 此任务共耗时 500ms~600ms (第一次间隔 100ms, 后两次均间隔 200ms, 共 500ms)
        then(timeit.since()).isBetween(500L, 600L);

        // 确认每次任务执行间隔时间
        then(records).containsExactly(1L, 3L, 5L);
    }

    /**
     * 测试延时任务线程池执行器, 按固定间隔时间重复执行任务
     *
     * <p>
     * 通过 {@link Executors#newScheduledThreadPool(int)} 创建的
     * {@link ScheduledThreadPoolExecutor} 线程池执行器, 也可以按固定的延迟间隔时间,
     * 重复去执行某个任务, 直到显式取消该任务的继续重复执行
     * </p>
     *
     * @see ScheduledThreadPoolExecutor
     */
    @Test
    void newScheduledThreadPool_shouldRunTaskWithFixedDelay() {
        // 记录起始时间
        var timeit = TimeIt.start();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 创建延时任务线程池
        try (var executor = Executors.newScheduledThreadPool(SystemInfo.cpuCount())) {
            timeit.restart();

            // 启动一个固定频率的定时器任务, 设置执行延迟时间和重复执行频率
            var future = executor.scheduleWithFixedDelay(() -> {
                // 记录执行时间
                records.add(timeit.since() / 100);
            }, 100, 200, TimeUnit.MILLISECONDS);

            // 等待定时器执行 3 次 (最大耗时 600ms) 以后, 取消定时器
            await().atMost(600, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> then(records).hasSize(3));

            future.cancel(false);
        }

        // 确认 3 此任务共耗时 5s (第一次间隔 1s, 后两次均间隔 2s, 共 5s)
        then(timeit.since()).isBetween(500L, 600L);

        // 确认每次任务执行间隔时间
        then(records).containsExactly(1L, 3L, 5L);
    }

    /**
     * 测试缓存线程池执行器
     *
     * <p>
     * 通过 {@link Executors#newCachedThreadPool()} 方法创建的
     * {@link ThreadPoolExecutor} 执行器, 其任务队列不具备任务缓存能力,
     * 每提交一个任务, 就会有一个对应线程来执行该任务, 当线程执行完毕任务后,
     * 会将该线程保留 60s 时间, 如果在此时间范围内有新任务提交,
     * 则该线程会继续执行该任务, 否则线程会被终止
     * </p>
     *
     * <p>
     * 故 {@link Executors#newCachedThreadPool()} 方法创建的线程池只适合 IO
     * 密集型任务, 即任务数量虽然较大, 但对 CPU 资源占用较小, 在 JDK 21 版本后,
     * 推荐使用虚拟线程, 即 {@link Executors#newVirtualThreadPerTaskExecutor()}
     * 替代 {@link Executors#newCachedThreadPool()} 方法创建线程池
     * </p>
     *
     * <p>
     * {@link Executors#newCachedThreadPool()}
     * 创建的线程池并未限制可创建的线程上限数量, 所以如果提交任务过快,
     * 可能会导致线程数量过多, 导致系统崩溃, 故该方法创建的线程池不推荐在生产环境使用
     * </p>
     *
     * @see ThreadPoolExecutor
     */
    @Test
    void newCachedThreadPool_shouldExecuteTaskImmediatelyWithCachedPool() {
        var futures = new ArrayList<Future<Integer>>();

        // 创建缓存线程池执行器, 并在执行完所有任务后关闭
        try (var executor = Executors.newCachedThreadPool()) {
            for (var i = 0; i < 10; i++) {
                var n = i + 1;

                futures.add(
                    // 向线程池提交一个任务
                    executor.submit(() -> {
                        Thread.sleep(50);
                        return n;
                    }));
            }
        }

        // 确认所有任务都已执行完毕
        then(futures).map(Future::isDone).allMatch(Boolean::booleanValue);

        // 确认所有任务都执行成功, 并且返回结果为 1~10
        then(futures).map(Future::get).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    /**
     * 测试 “任务窃取” 线程池执行器
     *
     * <p>
     * 通过 {@link Executors#newWorkStealingPool(int)}
     * 方法创建的线程池执行器为一个
     * {@link java.util.concurrent.ForkJoinPool ForkJoinPool} 对象,
     * 而 "任务窃取" 则是 {@link java.util.concurrent.ForkJoinPool
     * ForkJoinPool} 线程池的特性
     * </p>
     *
     * <p>
     * {@link java.util.concurrent.ForkJoinPool ForkJoinPool}
     * 线程池会为线程池中的每个线程创建一个任务队列, 一般情况下,
     * 各个线程只会从自己的任务队列中获取任务并执行,
     * 但如果一个线程的任务队列被消费完毕,
     * 则该线程会从其它线程的队列中窃取一个任务来执行,
     * 以保证各线程执行的平衡
     * </p>
     *
     * @see ForkJoinPoolTest
     */
    @Test
    void newWorkStealingPool_shouldExecuteTaskByForkJoinPool() {
        var futures = new ArrayList<Future<Integer>>();

        // 创建任务窃取线程池执行器, 并在执行完所有任务后关闭
        try (var executor = Executors.newWorkStealingPool()) {
            for (var i = 0; i < 20; i++) {
                var n = i + 1;

                futures.add(
                    // 向线程池提交一个任务
                    executor.submit(() -> {
                        return Fibonacci.calculate(n);
                    }));
            }
        }

        // 确认所有任务都已执行完毕
        then(futures).map(Future::isDone).allMatch(Boolean::booleanValue);

        // 确认所有任务都执行成功, 并且返回结果为 1~10
        then(futures).map(Future::get).containsExactly(
            1, 1, 2, 3, 5,
            8, 13, 21, 34, 55, 89, 144,
            233, 377, 610, 987, 1597,
            2584, 4181, 6765);
    }

    /**
     * 测试 {@link Executors#unconfigurableExecutorService(ExecutorService)}
     * 方法, 该方法创建一个 "不可配置" 的执行器对象
     *
     * <p>
     * 所谓 "不可配置" 的执行器对象, 即其类型无法转换为其它可配置的执行器类型
     * (例如 {@link ThreadPoolExecutor} 类型), 故也无法进一步修改其配置属性
     * </p>
     */
    @Test
    void unconfigurableExecutorService_shouldCreateUnconfigurableExecutorService() {
        // 实例化执行器对象, 该执行器对象是 "可配置" 的, 可以在实例化后修改其配置属性
        var executor = new ThreadPoolExecutor(
            0,
            SystemInfo.cpuCount() * 2,
            10,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1024),
            Thread.ofPlatform().factory(),
            new ThreadPoolExecutor.AbortPolicy());

        // 设置执行器对象的配置属性
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        // 获取执行器对象的配置属性
        then(executor.getCorePoolSize()).isEqualTo(0);
        then(executor.getMaximumPoolSize()).isEqualTo(SystemInfo.cpuCount() * 2);
        then(executor.getRejectedExecutionHandler()).isInstanceOf(ThreadPoolExecutor.DiscardOldestPolicy.class);

        // 创建一个 "不可配置" 的执行器对象
        try (var unconfigurableExecutor = Executors.unconfigurableExecutorService(executor)) {
            // 此 "不可配置" 执行器对象无法将自身类型转为 "可配置" 执行器类型
            thenThrownBy(() -> {
                ((ThreadPoolExecutor) unconfigurableExecutor).setCorePoolSize(1);
            }).isInstanceOf(ClassCastException.class);
        }
    }

    /**
     * 测试 {@link Executors#unconfigurableExecutorService(ExecutorService)}
     * 方法, 该方法创建一个 "不可配置" 的执行器对象
     *
     * <p>
     * 所谓 "不可配置" 的执行器对象, 即其类型无法转换为其它可配置的执行器类型
     * (例如 {@link ScheduledThreadPoolExecutor} 类型), 故也无法进一步修改其配置属性
     * </p>
     */
    @Test
    void unconfigurableScheduledExecutorService_shouldCreateUnconfigurableExecutorService() {
        // 实例化执行器对象, 该执行器对象是 "可配置" 的, 可以在实例化后修改其配置属性
        var executor = new ScheduledThreadPoolExecutor(8);

        // 设置执行器对象的配置属性
        executor.setRejectedExecutionHandler(new ScheduledThreadPoolExecutor.DiscardOldestPolicy());

        // 获取执行器对象的配置属性
        then(executor.getCorePoolSize()).isEqualTo(8);
        then(executor.getMaximumPoolSize()).isEqualTo(Integer.MAX_VALUE);
        then(executor.getRejectedExecutionHandler()).isInstanceOf(
            ScheduledThreadPoolExecutor.DiscardOldestPolicy.class);

        // 创建一个 "不可配置" 的执行器对象
        try (var unconfigurableExecutor = Executors.unconfigurableScheduledExecutorService(executor)) {
            // 此 "不可配置" 执行器对象无法将自身类型转为 "可配置" 执行器类型
            thenThrownBy(() -> {
                ((ScheduledThreadPoolExecutor) unconfigurableExecutor).setCorePoolSize(1);
            }).isInstanceOf(ClassCastException.class);
        }
    }

    @Test
    @SneakyThrows
    void ss() {
        var record = new AtomicReference<Object>(null);

        var caller = Executors.callable(() -> {
            record.set("1 - Executed");
        });

        var result = caller.call();

        then(result).isNull();

        then(record.get()).isEqualTo("1 - Executed");

        caller = Executors.callable(() -> {
            record.set("2 - Executed");
        }, "2 - Result");

        result = caller.call();

        then(result).isEqualTo("2 - Result");

        then(record.get()).isEqualTo("2 - Executed");

        caller = Executors.callable(() -> {
            record.set("3 - Executed");
            return "3 - Result";
        });
    }
}
