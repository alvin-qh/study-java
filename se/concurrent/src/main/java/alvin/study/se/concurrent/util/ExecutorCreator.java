package alvin.study.se.concurrent.util;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link ExecutorService} 对象创建器
 */
public final class ExecutorCreator implements AutoCloseable {
    // 允许的最大线程数
    private static final int MAX_THREAD_COUNT = 1024;

    // 记录已创建的 ExecutorService 对象的集合
    private List<WeakReference<ExecutorService>> executorRefs;

    /**
     * 关闭创建器对象, 销毁已经创建过的 {@link ExecutorService} 对象
     */
    @Override
    public void close() {
        if (executorRefs != null) {
            for (var ref : executorRefs) {
                var executor = ref.get();
                if (executor != null) {
                    executor.shutdown();
                }
            }
        }
    }

    /**
     * 记录创建的 {@link ExecutorService} 对象
     *
     * @param executor 已创建的 {@link ExecutorService} 对象
     */
    private void recordHistory(ExecutorService executor) {
        if (executorRefs == null) {
            executorRefs = new LinkedList<>();
        }
        executorRefs.add(new WeakReference<>(executor));
    }

    /**
     * 通过阻塞队列创建线程池
     *
     * <p>
     * 通过
     * {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, java.util.concurrent.BlockingQueue,
     * java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
     * ThreadPoolExecutor.ThreadPoolExecutor(int, int, long, TimeUnit,
     * BlockingQueue, ThreadFactory,
     * RejectedExecutionHandler)} 方法用于创建一个线程池执行器对象, 其参数依次如下:
     * <ul>
     * <li>
     * {@code corePoolSize} 核心线程数量, 即线程池中最少的线程数量, 这些线程和线程池具备相同的生命周期, 不会自动销毁
     * </li>
     * <li>
     * {@code maximumPoolSize} 最大线程数量, 当线程池中无空闲线程, 且任务队列已满时, 线程池会增加线程数来提高消费任务队列的效率,
     * 直到增加到该参数指定的数量. 当任务减少后, 则会逐步减少线程数量, 直到线程数为 {@code corePoolSize} 指定的数量
     * </li>
     * <li>
     * {@code keepAliveTime} 和 {@code unit} 参数共同指定了一个时间量, 表示当一个线程从空闲状态到被销毁间隔的时间
     * </li>
     * <li>
     * {@code workQueue} 指定一个 {@code BlockingQueue<Runtime>} 类型的队列, 所有提交的任务均在队列中等待,
     * 直到线程池中的一个线程将其取走并执行, 该队列如果已满, 则必须通过淘汰机制舍弃某个任务
     * </li>
     * <li>
     * {@code threadFactory} 指定一个用于创建线程对象的工厂对象, 参见
     * {@link java.util.concurrent.ThreadFactory ThreadFactory}
     * </li>
     * <li>
     * {@code handler} 指定一个回调, 当任务队列已满, 则通过该回调决定淘汰那一个任务, 参见
     * {@link java.util.concurrent.RejectedExecutionHandler
     * RejectedExecutionHandler} 类型
     * </li>
     * </ul>
     * </p>
     *
     * @param queueSize 任务队列的长度
     * @return 线程池执行器对象
     */
    public ExecutorService arrayBlockingQueueExecutor(int queueSize) {
        if (queueSize <= 0) {
            throw new IllegalArgumentException("queueSize must great than 0");
        }

        // 获取当前系统的 CPU 逻辑核心数 (Logical Kernel)
        var maxThread = Runtime.getRuntime().availableProcessors();

        // 实例化线程池对象
        // 设置核心线程数和最大线程数一致, 所以线程池不会销毁线程也不会增加线程
        // 使用 ArrayBlockingQueue 保证不会无休止的增加任务
        // 淘汰策略为: 从任务队列中淘汰一个最早的任务, 以容纳新任务 (默认规则是丢弃新任务)
        var executor = new ThreadPoolExecutor(
            maxThread,
            maxThread,
            0,
            TimeUnit.NANOSECONDS,
            new ArrayBlockingQueue<>(queueSize),
            (runnable, exec) -> {
                var queue = exec.getQueue();
                queue.poll();
                queue.offer(runnable);
            });

        // 存储线程池对象以便适时关闭
        recordHistory(executor);
        return executor;
    }

    /**
     * 创建一个通过 {@link SynchronousQueue} 作为任务队列的线程池
     *
     * <p>
     * {@link SynchronousQueue} 队列不存储实际的元素, 每 {@code offer} 一个元素, 需要立即被 {@code poll}
     * 掉, 否则会失败
     * </p>
     *
     * <p>
     * 这种特性放在线程池场景中, 即每个任务都必须立即有一个线程对其进行执行, 否则就以拒绝任务来处理
     * </p>
     *
     * @param maxThreads 允许同时运行的最大线程数, 如果为 {@code 0}, 则使用默认线程数
     * @return 线程池执行器对象
     */
    public ExecutorService synchronousQueueExecutor(int maxThreads) {
        if (maxThreads <= 0) {
            maxThreads = MAX_THREAD_COUNT;
        }

        // 实例化线程池对象
        // 核心线程数为 0, 表示如果无任务时, 没有活动线程
        // maxThreads 表示最大线程数, 当有任务提交, 且线程池中无空闲线程时, 会产生新的线程对齐进行处理, 最多产生 maxThreads 个线程
        // 产生的线程在 60 秒内可以被后续任务复用, 空闲超过该时间后, 线程销毁
        // 未设置淘汰策略, 所以线程达到最大限度后, 增加任务会导致异常抛出
        var executor = new ThreadPoolExecutor(
            0,
            maxThreads,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

        // 存储线程池对象以便适时关闭
        recordHistory(executor);
        return executor;
    }

    /**
     * 创建一个用于执行延时异步任务的线程池执行器对象
     *
     * @param maxThreads 最大线程数, 如果为 {@code 0}, 则使用默认线程数
     * @return 用于执行延时异步任务的线程池执行器对象
     */
    public ScheduledExecutorService scheduledExecutor(int maxThreads) {
        if (maxThreads <= 0) {
            // 获取当前 CPU 的逻辑内核数 (Logical Kernel)
            maxThreads = Runtime.getRuntime().availableProcessors();
        }

        // 实例化延迟任务线程池对象
        var executor = new ScheduledThreadPoolExecutor(maxThreads);

        // 存储线程池对象以便适时关闭
        recordHistory(executor);
        return executor;
    }
}
