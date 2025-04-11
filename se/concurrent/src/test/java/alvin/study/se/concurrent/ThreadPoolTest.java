package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import lombok.SneakyThrows;

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
    /**
     * 通过线程池提交任务, 获取任务执行结果
     *
     * <p>
     * 通过 {@link java.util.concurrent.ExecutorService#submit(
     * java.util.concurrent.Callable)
     * ExecutorService.submit(Callable)} 方法可以向线程池提交一个任务,
     * 返回一个 {@link java.util.concurrent.FutureTask FutureTask} 类型对象
     * </p>
     *
     * @see ThreadPool#arrayBlockingQueueExecutor(int)
     */
    @Test
    @SneakyThrows
    void futureTask_shouldCreateFutureTaskBySubmitThreadPool() {
        // 创建线程池执行器对象, 等待线程池执行结束
        try (var executor = ThreadPool.arrayBlockingQueueExecutor(1)) {

            // 提交一个任务
            var task = executor.submit(() -> Fibonacci.calculate(20));

            // 等待任务执行完毕
            await().atMost(5, TimeUnit.SECONDS).until(task::isDone);

            // 确认任务结果
            then(task.get()).isEqualTo(6765);
        }
    }

    /**
     * 测试以 {@link java.util.concurrent.SynchronousQueue SynchronousQueue}
     * 为任务队列的线程池
     *
     * @see ThreadPool#synchronousQueueExecutor(int)
     */
    @Test
    @SneakyThrows
    void synchronousQueueExecutor_shouldSubmitTaskIntoThreadPoolWithSynchronousQueue() {
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

        List<Future<String>> results;

        // 创建线程池执行器对象, 使用 SynchronousQueue 作为任务队列, 并等待所有任务执行完毕后关闭
        try (var executor = ThreadPool.synchronousQueueExecutor(0)) {
            // 执行集合中所有任务, 共执行 1s, 实际应该在 150~200ms 内执行完
            results = executor.invokeAll(tasks, 1, TimeUnit.SECONDS);
        }

        // 确认任务执行结果
        then(results).hasSize(tasks.size())
                .map(Future::get)
                .allMatch(s -> s.matches("^[0-3]?\\d-Success$"));
    }

    /**
     * 测试包含虚拟线程的线程池
     *
     * <p>
     * 可以通过 {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor}
     * 类创建虚拟线程, 参见 {@link ThreadPool#virtualThreadExecutor()} 方法
     * </p>
     *
     * <p>
     * 大多数时候, 虚拟线程都可以随使用时创建, 因为虚拟线程的低开销特性, 所以可以创建大量虚拟线程,
     * 但有些时候, 希望对线程能处理的任务总数进行控制, 则也可以通过线程池的方式,
     * 通过指定长度消息队列来控制任务数
     * </p>
     */
    @Test
    @SneakyThrows
    void virtualPool_shouldCreateThreadPoolForVirtualThread() {
        // 创建一个 HTTP 客户端对象
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        Future<HttpResponse<String>> task1, task2;

        // 创建虚拟线程线程池对象, 并等待所有任务执行完毕后关闭
        try (var executor = ThreadPool.virtualThreadExecutor()) {
            // 提交一个 HTTP 请求任务, 令其在虚拟线程池中执行
            task1 = executor.submit(() -> {
                // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
                var request = HttpRequest.newBuilder().GET()
                        .uri(URI.create("https://www.baidu.com"))
                        .timeout(Duration.ofMillis(3000))
                        .build();

                try {
                    // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                    return client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException ignore) {
                    throw new RuntimeException(ignore);
                }
            });

            // 再次提交一个 HTTP 请求任务, 令其在虚拟线程池中执行
            task2 = executor.submit(() -> {
                // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
                var request = HttpRequest.newBuilder().GET()
                        .uri(URI.create("https://cn.bing.com/"))
                        .timeout(Duration.ofMillis(3000))
                        .build();

                try {
                    // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                    return client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException ignore) {
                    throw new RuntimeException(ignore);
                }
            });
        }

        // 确认两个任务执行结果
        var resp1 = task1.get();
        then(resp1.statusCode()).isEqualTo(200);
        then(resp1.headers().firstValue("Content-Type")).isPresent().get().isEqualTo("text/html");
        then(resp1.body()).contains("<!DOCTYPE html>");

        var resp2 = task2.get();
        then(resp2.statusCode()).isEqualTo(200);
        then(resp2.headers().firstValue("Content-Type")).isPresent().get().isEqualTo("text/html; charset=utf-8");
        then(resp2.body()).contains("<!doctype html>");
    }
}
