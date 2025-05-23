package alvin.study.se.concurrent.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 *
 * <p>
 * 所谓线程池, 即一个消息队列配合一组线程协同工作, 当有消息写入队列时,
 * 一组线程中的某一个会被唤醒, 获取消息队列中的消息并进行处理,
 * 这样一来, 就可以有效的控制线程的数量, 避免线程过多造成系统过载
 * </p>
 *
 * <p>
 * JDK 中提供了 {@link java.util.concurrent.Executors Executors}
 * 工厂类用于创建不同构造器参数的 {@link ExecutorService} 对象,
 * 以表示不同场景下的线程池对象, 但直接使用
 * {@link java.util.concurrent.Executors Executors}
 * 工厂类也存在一些问题, 包括:
 *
 * <ul>
 * <ol>
 * 消息队列长度不受限制, 这就导致一旦线程池的线程都被阻塞 (或消费过慢),
 * 就有可能导致消息队列的长度爆炸, 导致系统崩溃
 * </ol>
 * <ol>
 * 缺乏消息丢弃策略, 当消息队列长度到达一个阈值后, 新消息如何丢弃的策略,
 * 包括丢掉新消息或是丢弃旧消息, 这点需要通过消息队列的实现类来控制
 * </ol>
 * </ul>
 * </p>
 *
 * <p>
 * 为了保证线程池在异常情况下, 不会导致系统崩溃,
 * 一些最佳实践中仍是推荐直接使用 {@link java.util.concurrent.ThreadPoolExecutor
 * ThreadPoolExecutor} 类构造器来创建线程池对象,
 * 该类构造器提供了如下构造器参数,
 * {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit,
 * java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory,
 * java.util.concurrent.RejectedExecutionHandler)
 * ThreadPoolExecutor.ThreadPoolExecutor(int, int, long, TimeUnit,
 * BlockingQueue, ThreadFactory, RejectedExecutionHandler)}, 依次为:
 *
 * <ul>
 * <li>
 * {@code corePoolSize} 核心线程数量, 即线程池中最少的线程数量,
 * 这些线程和线程池具备相同的生命周期, 不会自动销毁
 * </li>
 * <li>
 * {@code maximumPoolSize} 最大线程数量, 当线程池中无空闲线程, 且任务队列已满时,
 * 线程池会增加线程数来提高消费任务队列的效率, 直到增加到该参数指定的数量. 当任务减少后,
 * 则会逐步减少线程数量, 直到线程数为 {@code corePoolSize} 指定的数量
 * </li>
 * <li>
 * {@code keepAliveTime} 和 {@code unit} 参数共同指定了一个时间量,
 * 表示当一个线程从空闲状态到被销毁间隔的时间
 * </li>
 * <li>
 * {@code workQueue} 指定一个 {@code BlockingQueue<Runtime>} 类型的队列,
 * 所有提交的任务均在队列中等待, 直到线程池中的一个线程将其取走并执行, 该队列如果已满,
 * 则必须通过淘汰机制舍弃某个任务
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
 *
 * 当然, {@link java.util.concurrent.Executors Executors} 类内部也是通过
 * {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor}
 * 类来创建线程池的, 故 {@link java.util.concurrent.ThreadPoolExecutor
 * ThreadPoolExecutor} 类在使用上会复杂一些, 但更加灵活
 * </p>
 */
public final class ThreadPool {
    // 允许的最大线程数
    public static final int MAX_THREAD_COUNT = 1024;

    /**
     * 私有构造器, 禁止实例化对象
     */
    private ThreadPool() {}

    /**
     * 创建一个有界任务队列线程池执行器
     *
     * <p>
     * 该方法创建的线程池通过 {@link ArrayBlockingQueue} 作为任务队列,
     * 该队列的特点是可设置队列最大长度
     * </p>
     *
     * <p>
     * 所以一旦使用 {@link ArrayBlockingQueue} 作为线程池的消息队列,
     * 则需要同步设置消息队列满时的淘汰策略, 以便在任务队列满时,
     * 再加入新任务时, 如何淘汰一个已有任务或新任务
     * </p>
     *
     * @param maxThread 线程池最大线程数
     * @param queueSize 任务队列的长度
     * @return 线程池执行器对象
     */
    public static ExecutorService fixedPoolExecutor(int maxThread, int queueSize) {
        if (queueSize <= 0) {
            throw new IllegalArgumentException("queueSize must great than 0");
        }

        if (maxThread <= 0) {
            throw new IllegalArgumentException("maxThread must great than 0");
        }

        // 实例化线程池对象
        // 设置核心线程数和最大线程数一致, 所以线程池不会销毁线程也不会增加线程
        // 使用 ArrayBlockingQueue 保证不会无休止的增加任务
        // 淘汰策略为: 从任务队列中淘汰一个最早的任务, 以容纳新任务 (默认规则是丢弃新任务)
        return new ThreadPoolExecutor(
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
    }

    /**
     * 创建一个有界任务队列线程池执行器
     *
     * <p>
     * 和 {@link #fixedPoolExecutor(int, int)} 方法不同,
     * 该方法会以当前系统的 CPU 核心数作为线程池的最大线程数
     * </p>
     *
     * @see #fixedPoolExecutor(int, int)
     *
     * @param queueSize 队列大小
     * @return 线程池对象
     */
    public static ExecutorService fixedPoolExecutor(int queueSize) {
        return fixedPoolExecutor(SystemInfo.cpuCount(), queueSize);
    }

    /**
     * 创建一个立即执行任务的线程池执行器
     *
     * <p>
     * 该线程池通过 {@link SynchronousQueue} 类型作为任务队列,
     * 该队列的特点是不会缓存任何任务, 即每个任务都必须立即有一个线程对其进行执行,
     * 否则就以拒绝任务来处理
     * </p>
     *
     * <p>
     * 该线程池适合大量 IO 密集型任务, 任务的立即执行可以有效提升系统的响应速度,
     * 但又由于 IO 密集型任务, 也不会导致 CPU 大量占用导致的执行效率低下
     * </p>
     *
     * @param maxThreads 允许同时运行的最大线程数
     * @return 线程池执行器对象
     */
    public static ExecutorService synchronousTaskExecutor(int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThread must great than 0");
        }

        // 实例化线程池对象
        // 核心线程数为 0, 表示如果无任务时, 没有活动线程
        // maxThreads 表示最大线程数, 当有任务提交, 且线程池中无空闲线程时,
        // 会产生新的线程对其进行处理, 最多产生 maxThreads 个线程
        // 产生的线程在 60 秒内可以被后续任务复用, 空闲超过该时间后, 线程销毁
        // 未设置淘汰策略, 所以线程达到最大限度后, 增加任务会导致异常抛出
        return new ThreadPoolExecutor(
            0,
            maxThreads,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());
    }

    /**
     * 创建一个立即执行任务的线程池执行器
     *
     * <p>
     * 和 {@link #synchronousQueueExecutor()} 方法的区别在于,
     * 将最大线程数设置为 {@link #MAX_THREAD_COUNT}
     * </p>
     *
     * @return 线程池对象
     */
    public static ExecutorService synchronousTaskExecutor() {
        return synchronousTaskExecutor(MAX_THREAD_COUNT);
    }

    /**
     * 创建一个用于执行延时异步任务的线程池执行器对象
     *
     * @param maxThreads 线程池最大线程数
     * @return 用于执行延时异步任务的线程池执行器对象
     */
    public static ScheduledExecutorService scheduledPoolExecutor(int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThread must great than 0");
        }

        // 实例化延迟任务线程池对象
        return new ScheduledThreadPoolExecutor(maxThreads);
    }

    /**
     * 创建一个用于执行延时异步任务的线程池执行器对象
     *
     * <p>
     * 和 {@link #scheduledPoolExecutor()} 方法的区别在于,
     * 将最大线程数设置为 CPU 核心数
     * </p>
     *
     * @return 用于执行延时异步任务的线程池执行器对象
     */
    public static ScheduledExecutorService scheduledPoolExecutor() {
        return scheduledPoolExecutor(SystemInfo.cpuCount());
    }

    /**
     * 创建一个用于执行虚拟线程的线程池执行器对象
     *
     * <p>
     * 一般情况下, 无需为虚拟线程创建线程池, 这是由于虚拟线程的特性决定的,
     * 虚拟线程的低开销, 可以支持创建数量较大的虚拟线程,
     * 故一般情况下无需通过线程池来管理虚拟线程
     * </p>
     *
     * <p>
     * 但如果要控制任务数量, 则也可以通过线程池的消息队列来进行控制,
     * 由于 Java 的 "虚拟线程" 和 "平台线程" 的 API 接口一致,
     * 所以只需要在创建 {@link ThreadPoolExecutor} 对象时,
     * 指定 {@link java.util.concurrent.ThreadFactory
     * ThreadFactory} 参数, 并传入虚拟线程的线程构造工厂即可
     * </p>
     *
     * @return 用于执行虚拟线程的线程池执行器对象
     */

    public static ExecutorService virtualThreadExecutor() {
        // 通过虚拟线程工厂创建线程池对象
        return new ThreadPoolExecutor(
            0,
            MAX_THREAD_COUNT,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(MAX_THREAD_COUNT),
            Thread.ofVirtual().factory());
    }
}
