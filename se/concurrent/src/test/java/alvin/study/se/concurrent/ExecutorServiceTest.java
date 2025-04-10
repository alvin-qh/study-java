package alvin.study.se.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * <p>
 * {@link java.util.concurrent.ExecutorService ExecutorService}
 * 接口继承自 {@code Executor} 接口, 并提供了异步执行任务的一系列方法, 包括:
 * <ul>
 * <li>
 * {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
 * ExecutorService.submit(Callable)} 法用于提交一个异步任务, 并返回一个
 * {@link Future} 类型对象, 通过该对象可以获取任务执行的情况以及任务执行结果
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAll(java.util.Collection,
 * long, TimeUnit)
 * ExecutorService.invokeAll(Collection, long, TimeUnit)}
 * 方法用于批量提交多个任务, 并返回表示每个任务的 {@link Future} 对象集合
 * </li>
 * <li>
 * {@link java.util.concurrent.ExecutorService#invokeAny(java.util.Collection,
 * long, TimeUnit)
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
 */
class ExecutorServiceTest {
    @Test
    void ss() {
        try (var executor = Executors.newSingleThreadExecutor()) {}
    }
}
