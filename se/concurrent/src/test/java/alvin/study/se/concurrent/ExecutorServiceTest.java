package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;
import alvin.study.se.concurrent.util.ThreadPool;

/**
 * {@link java.util.concurrent.ExecutorService ExecutorService}
 * 接口类型表示一个异步任务执行器
 *
 * <p>
 * {@link java.util.concurrent.ExecutorService ExecutorService}
 * 接口继承自 {@code Executor} 接口, 并提供了异步执行任务的一系列方法, 包括:
 * <ul>
 * <li>
 * {@link java.util.concurrent.ExecutorService#submit(Runnable)
 * ExecutorService.submit(Runnable)} 法用于提交一个无返回值的异步任务,
 * 该方法返回一个 {@link Future} 类型对象, 只用来等待任务执行结束,
 * 并不返回任务执行结果 (结果值为 {@code null})
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
 * ExecutorService.submit(Callable)} 法用于提交一个异步任务, 并返回一个
 * {@link Future} 类型对象, 通过该对象可以获取任务执行的情况以及任务执行结果
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAll(java.util.Collection,
 * long, TimeUnit) ExecutorService.invokeAll(Collection, long, TimeUnit)}
 * 方法用于批量提交多个任务, 并返回表示每个任务的 {@link Future} 对象集合
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAny(java.util.Collection,
 * long, TimeUnit) ExecutorService.invokeAny(Collection, long, TimeUnit)}
 * 方法用于批量提交多个任务, 且任意任务结束即返回结果并终止其它任务,
 * 适合一组任务中一旦某个达成目标, 即可结束所有任务
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#awaitTermination(long, TimeUnit)
 * ExecutorService.awaitTermination(long, TimeUnit)}
 * 方法用于等待所有已提交任务结束 (或超时), 一般用于在结束程序前保证所有任务正常结束
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#shutdown()
 * ExecutorService.shutdown()} 方法用于关闭执行器, 已提交的任务中,
 * 正在执行的任务继续执行, 尚未执行的任务不再执行; 类似的
 * {@link java.util.concurrent.ExecutorService#shutdownNow()
 * ExecutorService.shutdownNow()} 方法则会立即中断所有正在执行的任务
 * </li>
 * <li>
 * JDK 19 之后, 实现了 {@link AutoCloseable} 接口, 并因此加入了
 * {@link java.util.concurrent.ExecutorService#close()
 * ExecutorService.close()} 方法, 该方法通过
 * {@link java.util.concurrent.ExecutorService#shutdown()
 * ExecutorService.shutdown()} 方法配合
 * {@link java.util.concurrent.ExecutorService#awaitTermination(long, TimeUnit)
 * ExecutorService.awaitTermination(long, TimeUnit)}
 * 共同完成了线程池的关闭以及等待所有运行中任务执行完毕的功能
 * </li>
 * </ul>
 * </p>
 */
class ExecutorServiceTest {
    /**
     * 测试提交一个无返回值的异步任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#submit(Runnable)
     * ExecutorService.submit(Runnable)} 方法可以提交一个无返回值的异步任务,
     * 并返回一个 {@link Future} 对象, 该对象可以用来等待任务执行结束,
     * 但并不返回任务执行结果 (结果值为 {@code null})
     * </p>
     */
    @Test
    @SneakyThrows
    void submit_shouldSubmitTaskAndDoExecute() {
        // 用于记录任务是否执行的标识变量
        var executed = new AtomicBoolean(false);

        // 任务执行结果变量
        var result = (Future<?>) null;

        // 创建线程池执行器, 并等待任务执行完毕后关闭
        try (var executor = Executors.newSingleThreadExecutor()) {
            result = executor.submit(() -> {
                // 设置标识变量为 true, 表示任务已经执行
                executed.set(true);
            });
        }

        // 确认任务已经执行
        then(executed.get()).isTrue();

        // 确认任务完成, 并且结果为 null
        then(result.isDone()).isTrue();
        then(result.get()).isNull();
    }

    /**
     * 测试提交任务, 并且返回结果
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
     * submit(Callable)} 方法提交任务, 该任务必须返回一个值,
     * 可以通过 {@link Future#get()} 方法获取任务返回的结果
     * <p>
     */
    @Test
    @SneakyThrows
    void submit_shouldSubmitTaskAndReturnResult() {
        Future<String> result;

        // 创建线程池执行器, 并等待任务执行完毕后关闭
        try (var executor = Executors.newSingleThreadExecutor()) {
            // 提交一个任务, 该任务返回一个字符串
            result = executor.submit(() -> {
                return "Worked";
            });
        }

        // 确认任务执行完毕
        then(result.isDone()).isTrue();

        // 确认任务返回结果
        then(result.get()).isEqualTo("Worked");
    }

    /**
     * 提交多个任务
     *
     * <p>
     * 本例中通过循环依次提交多个任务到线程池
     * </p>
     *
     * @see ThreadPool#arrayBlockingQueueExecutor(int)
     */
    @Test
    void submit_shouldSubmitMultiTasksAndGetResultOfEach() {
        // 保存 FutureTask 的集合对象
        var results = new ArrayList<Future<Integer>>();

        // 创建线程池执行器对象, 并等待任务执行完毕后关闭
        try (var executor = Executors.newFixedThreadPool(20)) {
            // 循环 20 次, 提交 20 个任务
            for (var i = 1; i <= 20; i++) {
                var n = i;

                // 将任务提交返回的 FutureTask 对象保存到集合中
                results.add(
                    executor.submit(() -> {
                        // 执行计算
                        return Fibonacci.calculate(n + 1);
                    }));
            }
        }

        // 确认所有的任务都执行完毕
        then(results).allMatch(Future::isDone);

        // 确认任务计算结果
        then(results).map(Future::get).containsExactly(
            1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144,
            233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946);
    }

    /**
     * 批量提交多个任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#invokeAll(
     * java.util.Collection, long, TimeUnit)
     * ExecutorService.invokeAll(Collection, long, TimeUnit)} 方法可以批量提交多个任务,
     * 且返回每个任务 {@link java.util.concurrent.FutureTask FutureTask} 对象组成的集合
     * </p>
     *
     * @see ThreadPool#arrayBlockingQueueExecutor(int)
     */
    @Test
    @SneakyThrows
    void invokeAll_shouldSubmitMultiTasksAndInvokeThemAll() {
        // 保存 FutureTask 的集合对象
        var tasks = IntStream.range(1, 21)
                .mapToObj(n -> (Callable<Integer>) () -> {
                    // 执行计算
                    return Fibonacci.calculate(n + 1);
                })
                .toList();

        List<Future<Integer>> results = null;

        // 创建线程池执行器对象, 并等待任务执行完毕后关闭
        try (var executor = Executors.newFixedThreadPool(20)) {
            // 执行所有任务
            results = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        }

        // 确认所有的任务都执行完毕
        then(results).hasSize(tasks.size());
        then(results).allMatch(Future::isDone);

        // 确认任务计算结果
        then(results).map(Future::get).containsExactly(
            1, 2, 3, 5, 8, 13, 21,
            34, 55, 89, 144, 233, 377, 610,
            987, 1597, 2584, 4181, 6765, 10946);
    }

    /**
     * 批量提交多个任务
     *
     * <p>
     * 通过
     * {@link java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, TimeUnit)
     * ExecutorService.invokeAny(Collection, long, TimeUnit)} 方法可以批量提交多个任务,
     * 且返回第一个执行完毕的任务结果
     * </p>
     *
     * <p>
     * 在多个提交的任务中, 一旦有一个成功执行完毕, 则其它的任务均被取消,
     * 例如在多个数据表中查找一条数据, 一旦一个任务成功完成, 则其它任务就无需继续执行
     * </p>
     *
     * @see ThreadPool#arrayBlockingQueueExecutor(int)
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

        String result = null;

        // 创建线程池执行器对象, 并等待任务执行完毕后关闭
        try (var executor = Executors.newFixedThreadPool(20)) {
            // 执行任务集合, 持续执行 500ms, 返回第一个执行成功任务的结果, 其余任务都被取消
            result = executor.invokeAny(tasks, 200, TimeUnit.MILLISECONDS);
        }

        // 确认任务计算结果
        then(result).matches("^[1-2]?\\d-Success-Sleep-1\\d{2}$");
    }

    /**
     * 测试 {@link ExecutorService#shutdown()} 方法, 关闭执行器
     *
     * <p>
     * {@link ExecutorService#shutdown()} 方法会关闭当前执行器对象,
     * 当执行器被关闭后, 其将不再接受新的任务,
     * 但已经提交到任务队列的任务会继续执行
     * </p>
     *
     * <p>
     * 如果向一个已经关闭的执行器继续提交任务, 则会引发
     * {@link RejectedExecutionException} 异常, 表示此次任务提交被拒绝
     * </p>
     *
     * <p>
     * 当任务队列的所有任务都被执行完毕后, 执行器对象自动终止, 此时
     * {@link ExecutorService#isTerminated()} 方法返回 {@code true}
     * </p>
     */
    @Test
    @SneakyThrows
    void shutdown_shouldShutdownExecutor() {
        var results = new ArrayList<Future<String>>();

        // 创建单线程线程池执行器对象, 并等待任务执行完毕后关闭
        var executor = Executors.newSingleThreadExecutor();

        // 向执行器提交 10 个任务
        for (var i = 0; i < 10; i++) {
            var index = i + 1;

            results.add(
                executor.submit(() -> {
                    try {
                        // 每个任务休眠 20 毫秒
                        Thread.sleep(20);
                        return String.format("%d", index);
                    } catch (InterruptedException ignore) {
                        return "";
                    }
                }));
        }

        // 确认执行器目前尚未关闭
        then(executor.isShutdown()).isFalse();

        // 关闭执行器
        executor.shutdown();

        // 确认执行器已经关闭
        then(executor.isShutdown()).isTrue();

        // 确认执行器关闭后继续提交任务会抛出异常
        thenThrownBy(() -> executor.submit(() -> "")).isInstanceOf(RejectedExecutionException.class);

        // 确认此时执行器尚未终止
        then(executor.isTerminated()).isFalse();

        // 轮询执行器终止状体, 直到执行器终止
        while (!executor.isTerminated()) {
            Thread.onSpinWait();
        }

        // 确认执行的任务总数
        then(results).hasSize(10);

        // 确认所有任务执行完毕
        then(results.stream().allMatch(Future::isDone)).isTrue();

        // 确认所有任务执行结果
        then(results.stream()).map(Future::get).containsExactly(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }
}
