package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

/**
 * 线程池测试
 *
 * <p>
 * Java 提供了 {@link java.util.concurrent.Executor Executor} 接口, 表示一个执行器类型, 可以通过
 * {@link java.util.concurrent.Executor#execute(Runnable) Executor.execute(Runnable)} 方法来执行一个任务
 * </p>
 *
 * <p>
 * 而 {@link java.util.concurrent.ExecutorService ExecutorService} 接口继承自 {@code Executor} 接口,
 * 并提供了异步执行任务的一系列方法, 包括:
 * <ul>
 * <li>
 * {@link ExecutorService#submit(java.util.concurrent.Callable) ExecutorService.submit(Callable)} 法用于提交一个异步任务,
 * 并返回一个 {@link Future} 类型对象, 通过该对象可以获取任务执行的情况以及任务执行结果
 * </li>
 * <li>
 * {@link ExecutorService#invokeAll(java.util.Collection, long, TimeUnit)
 * ExecutorService.invokeAll(Collection, long, TimeUnit)} 方法用于批量提交多个任务, 并返回表示每个任务的 {@link Future} 对象集合
 * </li>
 * <li>
 * {@link ExecutorService#invokeAny(java.util.Collection, long, TimeUnit)
 * ExecutorService.invokeAny(Collection, long, TimeUnit)} 方法用于批量提交多个任务, 且任意任务结束即返回结果并终止其它任务,
 * 适合一组任务中一旦某个达成目标, 即可结束所有任务
 * </li>
 * <li>
 * {@link ExecutorService#awaitTermination(long, TimeUnit)} 方法用于等待所有已提交任务结束 (或超时),
 * 一般用于在结束程序前保证所有任务正常结束
 * </li>
 * <li>
 * {@link ExecutorService#shutdown()} 关闭执行器, 已提交的任务中, 正在执行的任务继续执行, 尚未执行的任务不再执行; 类似的
 * {@link ExecutorService#shutdownNow()} 方法则会立即中断所有正在执行的任务
 * </li>
 * </ul>
 * {@link ThreadPoolExecutor} 类型继承自 {@link ExecutorService} 接口, 表示一个以 "线程池" + "队列" 方式执行异步任务的执行器类型
 * </p>
 *
 * <p>
 * {@link Future} 接口表示一个会在"将来"执行的异步任务, 通过一系列方法可以得知任务执行的情况和任务执行的结果, 包括:
 * <ul>
 * <li>
 * {@link Future#isDone()} 方法返回任务是否完成
 * </li>
 * <li>
 * {@link Future#isCancelled()} 方法返回任务是否被取消
 * </li>
 * <li>
 * {@link Future#get()} 方法返回任务的执行结果, 该方法只针对已完成的任务, 如果任务未执行完毕或已被取消, 则会抛出异常
 * </li>
 * <li>
 * {@link Future#get(long, TimeUnit)} 方法返回任务的执行结果, 对于未完成的任务, 该方法进行等待, 直到任务完成或超时
 * </li>
 * </ul>
 * {@link FutureTask} 类型是 {@link Future} 接口的一个实现, 其同时也实现了 {@link Runnable} 接口
 * </p>
 */
class ThreadPoolTest {
    // 保存线程执行器对象的集合, 用于在测试结束后进行关闭
    private WeakReference<ExecutorService> executorsHolder;

    /**
     * 通过阻塞队列创建线程池
     *
     * <p>
     * 通过
     * {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, java.util.concurrent.BlockingQueue,
     * java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
     * ThreadPoolExecutor.ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory,
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
     * {@code threadFactory} 指定一个用于创建线程对象的工厂对象, 参见 {@link java.util.concurrent.ThreadFactory ThreadFactory}
     * </li>
     * <li>
     * {@code handler} 指定一个回调, 当任务队列已满, 则通过该回调决定淘汰那一个任务, 参见
     * {@link java.util.concurrent.RejectedExecutionHandler RejectedExecutionHandler} 类型
     * </li>
     * </ul>
     * </p>
     *
     * @param queueSize 任务队列的长度
     * @return 线程池执行器对象
     */
    private ExecutorService arrayBlockingQueueExecutor(int queueSize) {
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
        executorsHolder = new WeakReference<>(executor);
        return executor;
    }

    /**
     * 在每个测试之后执行, 关闭线程池
     */
    @AfterEach
    void afterEach() {
        // 获取线程池对象
        var executor = executorsHolder == null ? null : executorsHolder.get();
        if (executor != null) {
            // 关闭线程池
            executor.shutdown();
        }
    }

    /**
     * 通过 {@link FutureTask} 类型执行一个线程, 并返回执行结果
     *
     * <p>
     * {@link FutureTask} 类实现了 {@link Runnable} 接口, 所以其对象可以作为参数传递给线程对象, 在线程执行完该接口方法后,
     * 会令其 {@link FutureTask#isDone()} 方法返回 {@code true}, 且可以通过 {@link FutureTask#get()} 方法返回执行结果
     * </p>
     */
    @Test
    void futureTask_shouldUseFutureTaskInThread() throws Exception {
        var task = new FutureTask<>(() -> Fibonacci.calculate(20));

        var thread = new Thread(task);
        thread.start();

        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);
        then(thread.isAlive()).isFalse();
        then(task.get()).isEqualTo(6765);
    }

    @Test
    void futureTask_shouldCreateFutureTaskBySubmitThreadPool() throws Exception {
        var executor = arrayBlockingQueueExecutor(20);

        var task = executor.submit(() -> Fibonacci.calculate(20));

        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);
        then(task.get()).isEqualTo(6765);
    }

    @Test
    void multiFutureTasks_() {
        var results = Lists.<Future<Integer>>newArrayList();
        var resultCount = new AtomicInteger();

        var executor = arrayBlockingQueueExecutor(20);

        for (var i = 0; i < 20; i++) {
            var n = i;

            results.add(executor.submit(() -> {
                try {
                    return Fibonacci.calculate(n + 1);
                } finally {
                    resultCount.incrementAndGet();
                }
            }));
        }

        await().atMost(5, TimeUnit.SECONDS).until(() -> resultCount.get() == 20);

        then(results).allMatch(Future::isDone);
        then(results).map(Future<Integer>::get).containsExactly(1, 1, 2, 3, 5, 8, 13, 21,
            34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765);
    }
}
