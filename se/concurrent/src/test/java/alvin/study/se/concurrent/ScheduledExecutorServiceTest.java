package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.SystemInfo;
import alvin.study.se.concurrent.util.TimeIt;

/**
 * 测试延时任务
 *
 * <p>
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 接口用于表示一个延时异步任务执行器,
 * 该执行器通过 {@link java.util.concurrent.ScheduledExecutorService#schedule(
 * java.util.concurrent.Callable, long, TimeUnit)
 * ScheduledExecutorService.schedule(Callable, long, TimeUnit)}
 * 方法提交一个延时任务, 该任务会在指定的时间之后执行
 * </p>
 *
 * <p>
 * {@link ScheduledThreadPoolExecutor} 类型实现了
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 接口, 即通过线程池的方式处理延时异步任务,
 * {@link ScheduledThreadPoolExecutor} 从
 * {@link java.util.concurrent.ThreadPoolExecutor
 * ThreadPoolExecutor} 类继承, 两者的主要区别在于前者使用了 "延时队列"
 * 来作为任务队列
 * </p>
 *
 * <p>
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 接口从
 * {@link java.util.concurrent.ExecutorService
 * ExecutorService} 接口继承而来, 故也可以作为一个立即执行任务的执行器
 * 使用, 包括
 * {@link java.util.concurrent.ScheduledExecutorService#submit(Runnable)
 * ExecutorService.submit(Runnable)} 方法以及
 * {@link java.util.concurrent.ScheduledExecutorService#execute(Runnable)}
 * 等方法, 参见 {@link ExecutorServiceTest} 类
 * </p>
 */
class ScheduledExecutorServiceTest {
    /**
     * 测试设定延时任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ScheduledExecutorService#schedule(
     * java.util.concurrent.Callable, long, TimeUnit)
     * ScheduledExecutorService.schedule(Callable, long, TimeUnit)}
     * 方法可以提交一个延时任务, 后两个参数指定延时时间 (从当前时间起), 到达指定时间后,
     * 任务才会被执行
     * </p>
     */
    @Test
    @SneakyThrows
    void schedule_shouldScheduleTaskAfterWhile() {
        // 记录起始时间
        var timeit = TimeIt.start();

        ScheduledFuture<Long> future1, future2, future3;

        // 创建延时任务线程池
        try (var executor = new ScheduledThreadPoolExecutor(SystemInfo.cpuCount())) {
            timeit.restart();

            // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
            future1 = executor.schedule(() -> timeit.since(), 200, TimeUnit.MILLISECONDS);
            future2 = executor.schedule(() -> timeit.since(), 100, TimeUnit.MILLISECONDS);
            future3 = executor.schedule(() -> timeit.since(), 210, TimeUnit.MILLISECONDS);
        }

        // 确认整体任务执行完毕耗时 2100ms, 即最后一个任务执行的时间
        then(future1.isDone() && future2.isDone() && future3.isDone()).isTrue();

        // 确认任务执行时间范围
        then(timeit.since()).isBetween(210L, 310L);

        // 确认每个任务的延时时间, 和设定的延时时间一致
        then(future1.get()).isBetween(200L, 210L);
        then(future2.get()).isBetween(100L, 110L);
        then(future3.get()).isBetween(210L, 220L);
    }

    /**
     * 按固定频率重复执行任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(
     * Runnable, long, long, TimeUnit)
     * ScheduledExecutorService.scheduleAtFixedRate(Runnable, long,
     * long, TimeUnit)} 方法可以按一个固定的频率重复执行某个任务,
     * 后三个参数用于表示任务第一次执行的延迟时间和之后每次执行的间隔时间
     * </p>
     *
     * <p>
     * {@code scheduleAtFixedRate} 方法以第一次任务执行时间作为后续任务执行的基准,
     * 即经过 {@code delay} 参数延迟后的时间, 但由于每次任务执行后才会追加下一次任务,
     * 所以某次任务的阻塞仍有可能会影响下次任务 (例如阻塞时间超过了 {@code period})
     * 参数设定的间隔时间, 但下一次任务会尽可能的快速执行以弥补耽搁的时间, 所以从宏观上看,
     * {@code scheduleAtFixedRate} 方法仍可以认为是基于固定频率的
     * </p>
     */
    @Test
    void scheduleAtFixedRate_shouldScheduleTaskWithFixedRate() {
        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 记录起始时间
        var timeit = TimeIt.start();

        // 创建延时任务线程池
        try (var executor = new ScheduledThreadPoolExecutor(SystemInfo.cpuCount())) {
            // 启动一个固定频率的定时器任务, 设置执行延迟时间和重复执行频率
            var future = executor.scheduleAtFixedRate(() -> {
                // 记录执行时间
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
     * 按固定间隔时间重复执行任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(
     * Runnable, long, long, TimeUnit)
     * ScheduledExecutorService.scheduleWithFixedDelay(Runnable, long,
     * long, TimeUnit)} 方法可以按一个固定的频率重复执行某个任务,
     * 后三个参数用于表示任务第一次执行的延迟时间和之后每次执行的间隔时间
     * </p>
     *
     * <p>
     * {@code scheduleWithFixedDelay} 方法是以上一次任务执行时间来计算下一次任务执行的时间的,
     * 即 {@code delay} 参数表示的是两次任务的间隔时间, 所以如果一次任务的执行时间超过了
     * {@code delay} 参数, 则后续的任务都会受到影响
     * </p>
     */
    @Test
    void scheduleWithFixedDelay_shouldRunTaskWithFixedDelay() {
        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 记录起始时间
        var timeit = TimeIt.start();

        // 创建延时任务线程池
        try (var executor = new ScheduledThreadPoolExecutor(SystemInfo.cpuCount())) {
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

        // 确认 3 此任务共耗时 500ms~600ms (第一次间隔 100ms, 后两次均间隔 200ms, 共 500ms)
        then(timeit.since()).isBetween(500L, 600L);

        // 确认每次任务执行间隔时间
        then(records).containsExactly(1L, 3L, 5L);
    }
}
