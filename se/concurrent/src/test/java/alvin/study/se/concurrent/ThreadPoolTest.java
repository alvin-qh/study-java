package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;
import alvin.study.se.concurrent.util.ThreadPool;

/**
 * 线程池测试
 *
 * <p>
 * Java 提供了 {@link java.util.concurrent.Executor Executor} 接口,
 * 表示一个执行器类型, 可以通过
 * {@link java.util.concurrent.Executor#execute(Runnable)
 * Executor.execute(Runnable)} 方法来执行一个任务
 * </p>
 *
 * <p>
 * 而 {@link java.util.concurrent.ExecutorService ExecutorService}
 * 接口继承自 {@code Executor} 接口, 并提供了异步执行任务的一系列方法, 包括:
 * <ul>
 * <li>
 * {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
 * ExecutorService.submit(Callable)} 法用于提交一个异步任务, 并返回一个
 * {@link Future} 类型对象, 通过该对象可以获取任务执行的情况以及任务执行结果
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long, TimeUnit)
 * ExecutorService.invokeAll(Collection, long, TimeUnit)}
 * 方法用于批量提交多个任务, 并返回表示每个任务的 {@link Future} 对象集合
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, TimeUnit)
 * ExecutorService.invokeAny(Collection, long, TimeUnit)}
 * 方法用于批量提交多个任务, 且任意任务结束即返回结果并终止其它任务,
 * 适合一组任务中一旦某个达成目标, 即可结束所有任务
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#awaitTermination(long, TimeUnit)
 * ExecutorService.awaitTermination(long, TimeUnit)} 方法用于等待所有已提交任务结束 (或超时),
 * 一般用于在结束程序前保证所有任务正常结束
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#shutdown()
 * ExecutorService.shutdown()} 方法用于关闭执行器, 已提交的任务中,
 * 正在执行的任务继续执行, 尚未执行的任务不再执行; 类似的
 * {@link java.util.concurrent.ExecutorService#shutdownNow()
 * ExecutorService.shutdownNow()} 方法则会立即中断所有正在执行的任务
 * </li>
 * </ul>
 * {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor}
 * 类型继承自 {@link java.util.concurrent.ExecutorService ExecutorService}
 * 接口, 表示一个以 "线程池" + "队列" 方式执行异步任务的执行器类型
 * </p>
 *
 * <p>
 * {@link Future} 接口表示一个会在"将来"执行的异步任务,
 * 通过一系列方法可以得知任务执行的情况和任务执行的结果, 包括:
 * <ul>
 * <li>
 * {@link Future#isDone()} 方法返回任务是否完成
 * </li>
 * <li>
 * {@link Future#isCancelled()} 方法返回任务是否被取消
 * </li>
 * <li>
 * {@link Future#get()} 方法返回任务的执行结果, 该方法只针对已完成的任务,
 * 如果任务未执行完毕或已被取消, 则会抛出异常
 * </li>
 * <li>
 * {@link Future#get(long, TimeUnit)} 方法返回任务的执行结果, 对于未完成的任务,
 * 该方法进行等待, 直到任务完成或超时
 * </li>
 * </ul>
 * {@link java.util.concurrent.FutureTask FutureTask} 类型是 {@link Future}
 * 接口的一个实现, 其同时也实现了 {@link Runnable} 接口
 * </p>
 */
class ThreadPoolTest {
    private final ThreadPool threadPool = new ThreadPool();

    /**
     * 在每个测试之后执行, 关闭线程池
     */
    @AfterEach
    void afterEach() {
        threadPool.close();
    }

    @Test
    void newFixedThreadPool_shouldCreateFixedSizeThreadPoolByExecutors() {
        try (var executor = Executors.newFixedThreadPool(2)) {
            var results = new ArrayList<Long>();

            for (var i = 0; i < 4; i++) {
                executor.submit(() -> {
                    try {
                        results.add(System.currentTimeMillis());
                        Thread.sleep(200);
                    } catch (InterruptedException ignore) {}
                });
            }

            await().atMost(1, TimeUnit.SECONDS).until(() -> results.size() == 4);

            then(results).hasSize(4);
            then(results.get(1) - results.get(0)).isLessThan(10);
            then(results.get(3) - results.get(2)).isLessThan(10);

            then(results.get(2) - results.get(1)).isBetween(200L, 210L);
        }
    }

    /**
     * 通过线程池提交任务, 获取任务执行结果
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
     * ExecutorService.submit(Callable)} 方法可以向线程池提交一个任务,
     * 返回一个 {@link java.util.concurrent.FutureTask FutureTask} 类型对象
     * </p>
     */
    @Test
    @SneakyThrows
    void futureTask_shouldCreateFutureTaskBySubmitThreadPool() {
        // 创建线程池执行器对象
        var executor = threadPool.arrayBlockingQueueExecutor(20);

        // 提交一个任务
        var task = executor.submit(() -> Fibonacci.calculate(20));

        // 等待任务执行完毕
        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);

        // 确认任务结果
        then(task.get()).isEqualTo(6765);
    }

    /**
     * 提交多个任务
     *
     * <p>
     * 本例中通过循环依次提交多个任务到线程池
     * </p>
     */
    @Test
    void multiFutureTasks_shouldSubmitMultiTasks() {
        // 保存 FutureTask 的集合对象
        var tasks = new ArrayList<Future<Integer>>();

        // 任务计数器, 计算已完成任务数
        var resultCount = new AtomicInteger();

        // 创建线程池执行器对象
        var executor = threadPool.arrayBlockingQueueExecutor(20);

        // 循环 20 次, 提交 20 个任务
        for (var i = 1; i <= 20; i++) {
            var n = i;

            // 将任务提交返回的 FutureTask 对象保存到集合中
            tasks.add(executor.submit(() -> {
                try {
                    // 执行计算
                    return Fibonacci.calculate(n + 1);
                } finally {
                    // 增加任务计数器
                    resultCount.incrementAndGet();
                }
            }));
        }

        // 等待任务全部结束
        await().atMost(5, TimeUnit.SECONDS).until(() -> resultCount.get() == 20);

        // 确认所有的任务都执行完毕
        then(tasks).allMatch(Future::isDone);

        // 确认任务计算结果
        then(tasks).map(Future::get)
                .containsExactly(
                    1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144,
                    233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946);
    }

    /**
     * 批量提交多个任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long, TimeUnit)
     * ExecutorService.invokeAll(Collection, long, TimeUnit)} 方法可以批量提交多个任务,
     * 且返回每个任务 {@link java.util.concurrent.FutureTask FutureTask} 对象组成的集合
     * </p>
     */
    @Test
    @SneakyThrows
    void invokeAll_shouldSubmitMultiTasksAndInvokeThemAll() {
        // 任务计数器, 计算已完成任务数
        var resultCount = new AtomicInteger();

        // 保存 FutureTask 的集合对象
        var tasks = IntStream.range(1, 21)
                .mapToObj(n -> (Callable<Integer>) () -> {
                    try {
                        // 执行计算
                        return Fibonacci.calculate(n + 1);
                    } finally {
                        // 增加任务计数器
                        resultCount.incrementAndGet();
                    }
                })
                .toList();

        // 创建线程池执行器对象
        var executor = threadPool.arrayBlockingQueueExecutor(20);
        // 执行所有任务
        var futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        // 等待任务全部结束
        await().atMost(5, TimeUnit.SECONDS).until(() -> resultCount.get() == 20);

        // 确认所有的任务都执行完毕
        then(futures).allMatch(Future::isDone);

        // 确认任务计算结果
        then(futures).map(Future::get).containsExactly(1, 2, 3, 5, 8, 13, 21,
            34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946);
    }

    /**
     * 批量提交多个任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, TimeUnit)
     * ExecutorService.invokeAny(Collection, long, TimeUnit)} 方法可以批量提交多个任务,
     * 且返回第一个执行完毕的任务结果
     * </p>
     *
     * <p>
     * 在多个提交的任务中, 一旦有一个成功执行完毕, 则其它的任务均被取消,
     * 例如在多个数据表中查找一条数据, 一旦一个任务成功完成, 则其它任务就无需继续执行
     * </p>
     */
    @Test
    @SneakyThrows
    void invokeAny_shouldSubmitMultiTasksAndInvokeAnyOfThem() {
        var rand = new Random();

        // 保存 FutureTask 的集合对象
        var tasks = IntStream.range(1, 21)
                .mapToObj(n -> (Callable<String>) () -> {
                    var sleepMillis = rand.nextInt(200) + 100;
                    Thread.sleep(sleepMillis);

                    return String.format("%d-Success-Sleep-%d", n, sleepMillis);
                })
                .toList();

        // 创建线程池执行器对象
        var executor = threadPool.arrayBlockingQueueExecutor(20);

        // 执行任务集合, 持续执行 500ms, 返回第一个执行成功任务的结果, 其余任务都被取消
        var result = executor.invokeAny(tasks, 200, TimeUnit.MILLISECONDS);

        // 确认任务计算结果
        then(result).matches("^[1-2]?\\d-Success-Sleep-1\\d{2}$");
    }

    /**
     * 测试以 {@link java.util.concurrent.SynchronousQueue SynchronousQueue}
     * 为任务队列的线程池
     */
    @Test
    @SneakyThrows
    void cachedPool_shouldSubmitTaskIntoThreadPoolWithSynchronousQueue() {
        // 任务计数器, 计算已完成任务数
        var resultCount = new AtomicInteger();

        // 任务总数量
        var taskCount = 30;

        // 保存 FutureTask 的集合对象
        var tasks = IntStream.range(0, taskCount)
                .mapToObj(n -> (Callable<String>) () -> {
                    try {
                        Thread.sleep(100);
                        return String.format("%d-Success", n);
                    } finally {
                        resultCount.incrementAndGet();
                    }
                })
                .toList();

        // 创建线程池执行器对象, 使用 SynchronousQueue 作为任务队列
        var executor = threadPool.synchronousQueueExecutor(0);

        // 执行集合中所有任务, 共执行 1s, 实际应该在 150~200ms 内执行完
        var result = executor.invokeAll(tasks, 1, TimeUnit.SECONDS);

        // 等待任务执行完毕
        await().atMost(1, TimeUnit.SECONDS).until(() -> resultCount.get() == taskCount);

        // 确认任务执行结果
        then(result).map(Future::get).allMatch(s -> s.matches("^[0-3]?\\d-Success$"));
    }

    @Test
    void virtualPool_shouldCreateThreadPoolForVirtualThread() {}
}
