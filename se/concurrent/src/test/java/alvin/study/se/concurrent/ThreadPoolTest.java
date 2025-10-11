package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;
import alvin.study.se.concurrent.util.ThreadPool;
import alvin.study.se.concurrent.util.TimeIt;

/**
 * 线程池测试
 *
 * <p>
 * {@link ThreadPool} 类实现了一个线程池工具类, 该类提供了一系列静态方法,
 * 用于创建各类 {@link java.util.concurrent.ExecutorService
 * ExecutorService} 接口的线程池执行器对象
 * </p>
 *
 * <p>
 * Java 的 {@link java.util.concurrent.Executors Executors}
 * 工具类, 提供了类似功能, 但通过 {@link java.util.concurrent.Executors
 * Executors} 类创建的线程池执行器对象, 往往未限制任务队列的长度,
 * 以及当任务队列满后, 添加任务时, 任务的淘汰策略, 这样会导致生产环境中,
 * 因某些任务的执行过慢而导致的任务队列长度暴增, 导致内存溢出或系统崩溃
 * </p>
 */
class ThreadPoolTest {
    /**
     * 测试固定边界线程池执行器, 批量并行执行任务
     *
     * <p>
     * 通过 {@link ThreadPool#fixedPoolExecutor(int, int)}
     * 方法创建固定边界线程池, 通过指定线程数, 确定有多少线程同时从任务队列,
     * 通过指定任务队列长度, 确定任务队列可以最多缓存的任务数
     * </p>
     *
     * <p>
     * 当 {@link ThreadPool#fixedPoolExecutor(int)} 方法只有一个参数时,
     * 该参数表示任务队列长度, 线程池的线程数将和当前系统的 CPU 核心数一致
     * </p>
     *
     * @see ThreadPool#fixedPoolExecutor(int, int)
     * @see ThreadPool#fixedPoolExecutor(int)
     */
    @Test
    @SneakyThrows
    void fixedPoolExecutor_shouldCreateExecutorAndExecuteTask() {
        var futures = new ArrayList<Future<Integer>>();

        // 创建线程池执行器对象, 等待线程池执行结束
        try (var executor = ThreadPool.fixedPoolExecutor(3)) {
            // 提交 5 个任务
            for (var i = 0; i < 5; i++) {
                var n = i + 1;

                futures.add(
                    executor.submit(() -> Fibonacci.calculate(n)));
            }
        }

        // 确认任务结果
        then(futures).hasSize(5);

        // 确认所有任务执行完毕
        then(futures).map(Future::isDone).allMatch(Boolean::booleanValue);

        // 确认所有任务执行结果
        then(futures).map(Future::get).containsExactly(1, 1, 2, 3, 5);
    }

    /**
     * 测试立即执行线程池执行器, 提交并立即执行任务
     *
     * <p>
     * 通过 {@link ThreadPool#synchronousTaskExecutor(int)}
     * 方法可以创建一个立即执行线程池执行器, 当通过该执行器提交任务时,
     * 被提交的任务会立即从任务队列取出并执行, 通过参数可以指定线程池的最大线程数
     * </p>
     *
     * <p>
     * 该线程池的任务队列只能容纳一个任务,
     * 故如果线程池中的线程无法及时取走队列中的任务, 则继续提交任务会失败
     * </p>
     *
     * <p>
     * {@link ThreadPool#synchronousTaskExecutor()} 方法创建的线程池,
     * 会以 {@link ThreadPool#MAX_THREAD_COUNT} 为线程池最大线程数
     * </p>
     *
     * @see ThreadPool#synchronousTaskExecutor(int)
     * @see ThreadPool#synchronousTaskExecutor()
     */
    @Test
    @SneakyThrows
    void synchronousTaskExecutor_shouldExecuteTaskImmediately() {
        // 任务计数器, 计算已完成任务数
        var resultCount = new AtomicInteger();

        // 任务总数量
        var taskCount = 30;

        // 保存 FutureTask 的集合对象
        var tasks = IntStream.range(0, taskCount)
                .mapToObj(n -> {
                    return (Callable<String>) () -> {
                        try {
                            return String.format("%d-Success", n);
                        } finally {
                            resultCount.incrementAndGet();
                            Thread.sleep(50);
                        }
                    };
                })
                .toList();

        List<Future<String>> futures;

        // 创建线程池执行器对象, 等待线程池执行结束
        try (var executor = ThreadPool.synchronousTaskExecutor()) {
            // 执行集合中所有任务, 共等待 1s, 实际应该在 150~200ms 内执行完
            futures = executor.invokeAll(tasks, 1, TimeUnit.SECONDS);
        }

        // 确认任务执行结果
        then(futures).hasSize(tasks.size())
                .map(Future::get)
                .allMatch(s -> s.matches("^[0-3]?\\d-Success$"));
    }

    /**
     * 测试立即执行线程池执行器任务队列阻塞情况
     *
     * <p>
     * 本例通过 {@link ThreadPool#synchronousTaskExecutor(int)} 方法, 通过参数
     * {@code 1} 创建线程池执行器, 由于线程池中只有一个线程, 故当该线程被任务占用时,
     * 该线程池无法提交下一个任务
     * </p>
     */
    @Test
    @SneakyThrows
    void synchronousTaskExecutor_shouldRaiseExceptionWhenTaskQueueWasFull() {
        // 创建线程池执行器对象, 等待线程池执行结束, 该线程池中包含 1 个线程
        try (var executor = ThreadPool.synchronousTaskExecutor(1)) {
            // 提交第 1 个任务, 该任务执行时间为 100ms, 故在 100ms 内, 没有线程可以执行其它任务
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {}
            });

            // 继续提交第 2 个任务, 由于第一个任务尚未执行完毕, 故第二个任务无法正常提交
            thenThrownBy(() -> executor.submit(() -> {}))
                    .isInstanceOf(RejectedExecutionException.class);
        }
    }

    /**
     * 测试包含虚拟线程的线程池, 通过虚拟线程执行任务
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
     *
     * @see ThreadPool#virtualThreadExecutor()
     */
    @Test
    @SneakyThrows
    void virtualThreadExecutor_shouldCreateThreadPoolForVirtualThread() {
        // 创建一个 HTTP 客户端对象
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        Future<HttpResponse<String>> future1, future2;

        // 创建虚拟线程线程池对象, 并等待所有任务执行完毕后关闭
        try (var executor = ThreadPool.virtualThreadExecutor()) {
            // 提交一个 HTTP 请求任务, 令其在虚拟线程池中执行
            future1 = executor.submit(() -> {
                // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
                var request = HttpRequest.newBuilder().GET()
                        .uri(URI.create("https://www.baidu.com"))
                        .timeout(Duration.ofMillis(30000))
                        .build();

                try {
                    // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                    return client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException ignore) {
                    throw new RuntimeException(ignore);
                }
            });

            // 再次提交一个 HTTP 请求任务, 令其在虚拟线程池中执行
            future2 = executor.submit(() -> {
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
        var resp1 = future1.get();
        then(resp1.statusCode()).isEqualTo(200);
        then(resp1.headers().firstValue("Content-Type")).isPresent()
                .get()
                .isEqualTo("text/html");
        then(resp1.body()).contains("<!DOCTYPE html>");

        var resp2 = future2.get();
        then(resp2.statusCode()).isEqualTo(200);
        then(resp2.headers().firstValue("Content-Type")).isPresent()
                .get()
                .isEqualTo("text/html; charset=utf-8");
        then(resp2.body()).contains("<!doctype html>");
    }

    /**
     * 测试延时任务线程池执行器, 在固定时间延迟后执行任务
     *
     * <p>
     * 通过 {@link ThreadPool#scheduledPoolExecutor(int)}
     * 方法可以可以创建一个延时任务线程池执行器, 通过该执行器可以提交一个延时任务,
     * 并指定该任务执行的延时时间, 通过参数可指定线程池的最大线程数量
     * </p>
     *
     * <p>
     * 而通过 {@link ThreadPool#scheduledPoolExecutor()}
     * 方法创建的线程池, 则以当前系统的 CPU 核心数作为线程池的最大线程数
     * </p>
     *
     * @see ThreadPool#scheduledPoolExecutor(int)
     * @see ThreadPool#scheduledPoolExecutor()
     */
    @Test
    @SneakyThrows
    void scheduledPoolExecutor_shouldExecuteTaskAfterWhile() {
        var timeit = TimeIt.start();

        ScheduledFuture<Long> future1, future2, future3;

        // 创建延时任务线程池
        try (var executor = ThreadPool.scheduledPoolExecutor()) {
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
     * 通过 {@link ThreadPool#scheduledPoolExecutor()} 创建的线程池执行器,
     * 也可以按指定的固定频率去重复执行某个任务, 直到显式取消该任务的继续重复执行
     * </p>
     *
     * @see ThreadPool#scheduledPoolExecutor(int)
     * @see ThreadPool#scheduledPoolExecutor()
     */
    @Test
    void scheduledPoolExecutor_shouldScheduleTaskWithFixedRate() {
        // 记录起始时间
        var timeit = TimeIt.start();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 创建延时任务线程池
        try (var executor = ThreadPool.scheduledPoolExecutor()) {
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
     * 通过 {@link ThreadPool#scheduledPoolExecutor()} 创建的线程池执行器,
     * 也可以按固定的延迟间隔时间, 重复去执行某个任务, 直到显式取消该任务的继续重复执行
     * </p>
     *
     * @see ThreadPool#scheduledPoolExecutor(int)
     * @see ThreadPool#scheduledPoolExecutor()
     */
    @Test
    void scheduledPoolExecutor_shouldRunTaskWithFixedDelay() {
        // 记录起始时间
        var timeit = TimeIt.start();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 创建延时任务线程池
        try (var executor = ThreadPool.scheduledPoolExecutor()) {
            timeit.restart();

            // 启动一个固定频率的定时器任务, 设置执行延迟时间和重复执行频率
            var result = executor.scheduleWithFixedDelay(() -> {
                // 记录执行时间
                records.add(timeit.since() / 100);
            }, 100, 200, TimeUnit.MILLISECONDS);

            // 等待定时器执行 3 次 (最大耗时 600ms) 以后, 取消定时器
            await().atMost(600, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> then(records).hasSize(3));

            result.cancel(false);
        }

        // 确认 3 此任务共耗时 500ms~600ms (第一次间隔 100ms, 后两次均间隔 200ms, 共 500ms)
        then(timeit.since()).isBetween(500L, 600L);

        // 确认每次任务执行间隔时间
        then(records).containsExactly(1L, 3L, 5L);
    }
}
