package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

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

}
