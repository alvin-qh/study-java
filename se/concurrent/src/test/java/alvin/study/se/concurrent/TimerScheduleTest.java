package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.delay.DelayedValue;
import alvin.study.se.concurrent.util.ThreadPool;

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
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor
 * ScheduledThreadPoolExecutor} 类型实现了
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 接口, 即通过线程池的方式处理延时异步任务,
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor
 * ScheduledThreadPoolExecutor} 从
 * {@link java.util.concurrent.ThreadPoolExecutor
 * ThreadPoolExecutor} 类继承, 两者的主要区别在于前者使用了 "延时队列"
 * 来作为任务队列
 * </p>
 *
 * <p>
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor#execute(Runnable)
 * ScheduledExecutorService.execute(Runnable)} 和
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor#submit(
 * java.util.concurrent.Callable)
 * ScheduledThreadPoolExecutor.submit(Callable)} 方法的语义未发生变化,
 * 但也可以理解为提交了"延时时间为 {@code 0}"的延时任务
 * </p>
 *
 * <p>
 * 另外, {@link Timer} 类也可以处理定时任务, 且两者具有类似的方法, 和
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 相比, {@link Timer}
 * 也是通过延时队列来作为任务队列的, 但后者只启动了一个线程(而不是通过线程池),
 * 所以一旦队列中有某个任务执行时间过久或被阻塞, 这会影响到之后的所有其它任务
 * </p>
 *
 * <p>
 * 且 {@link Timer} 类型属于较早期的 API, 使用已经过时的
 * {@link java.util.Date Date} 和 {@link java.util.Calendar Calendar}
 * 类型来表示时间
 * </p>
 */
class TimerScheduleTest {
    private final ThreadPool threadPool = new ThreadPool();

    /**
     * 每次测试结束后执行, 用于关闭线程池
     */
    @AfterEach
    void afterEach() {
        threadPool.close();
    }

    /**
     * 测试延时队列
     *
     * <p>
     * 延时队列 ({@link DelayQueue}) 是一种阻塞式优先队列
     * ({@link java.util.concurrent.PriorityBlockingQueue PriorityBlockingQueue}),
     * 其队列元素具备延时, 在时间到达后方可按时间到达的顺序从队列中获取
     * </p>
     *
     * <p>
     * {@link DelayQueue} 中的元素类型必须实现自 {@link java.util.concurrent.Delayed
     * Delayed} 接口, 其中:
     * <ul>
     * <li>
     * {@link java.util.concurrent.Delayed#getDelay(TimeUnit)
     * Delayed.getDelay(TimeUnit)} 方法用于确定延时剩余时间, 当其返回值小于等于 {@code 0}
     * 后, 方可从队列中取出
     * </li>
     * <li>
     * {@link java.util.concurrent.Delayed#compareTo(Object)
     * Delayed.compareTo(Delayed)} 方法比较两个队列元素, 用于确定元素在队列中的"优先级",
     * 比较结果越小的元素具有越高的出队优先级
     * </li>
     * </ul>
     * </p>
     *
     * @see DelayedValue
     */
    @Test
    @SneakyThrows
    void delayQueue_shouldGetDelayedValueFromQueue() {
        // 创建延时队列
        var queue = new DelayQueue<DelayedValue<Integer>>();

        // 入队 3 个元素, 并各自具备不同的延时时间
        queue.offer(new DelayedValue<>(1, 200, TimeUnit.MILLISECONDS));
        queue.offer(new DelayedValue<>(2, 100, TimeUnit.MILLISECONDS));
        queue.offer(new DelayedValue<>(3, 210, TimeUnit.MILLISECONDS));

        // 记录当前时间
        var millis = System.currentTimeMillis();

        // 出队, 因为此时无元素到达延时时间, 所以返回值为 null
        then(queue.poll()).isNull();

        // 休眠 1s 后再次出队, 此时可以出队延时时间为 1s 的元素
        Thread.sleep(100);
        then(queue.poll()).extracting("value").isEqualTo(2);

        // 继续出队, 因为此时无元素到达延时时间, 所以返回值为 null
        then(queue.poll()).isNull();

        // 通过 take 方法, 阻塞直到有元素出队, 此时出队延时为 2s 的元素
        then(queue.take()).extracting("value").isEqualTo(1);

        // 继续出队, 并设定超时时间为 110ms, 此时出队延时时间为 2.1s 的元素
        then(queue.poll(110, TimeUnit.MILLISECONDS)).extracting("value").isEqualTo(3);

        // 确认整体出队耗时 2100ms, 为延时时间最久的元素出队时间
        then(System.currentTimeMillis() - millis).isGreaterThanOrEqualTo(210).isLessThan(220);
    }

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
     *
     * <p>
     * {@code schedule} 方法返回 {@link java.util.concurrent.ScheduledFuture
     * ScheduledFuture} 类型对象, 用于查看任务执行情况, 获取任务执行结果, 包括:
     * <ul>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#isDone()
     * ScheduledFuture.isDone()} 方法查看任务是否执行完毕
     * </li>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#get()
     * ScheduledFuture.get()} 方法获取任务执行结果
     * </li>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#getDelay(TimeUnit)
     * ScheduledFuture.getDelay(TimeUnit)} 方法获取任务剩余延时时间
     * </li>
     * </ul>
     * </p>
     */
    @Test
    @SneakyThrows
    void scheduleFuture_shouldScheduleTaskAfterWhile() {
        // 创建延时任务线程池
        try (var executor = threadPool.scheduledExecutor(0)) {
            // 记录起始时间
            var startedMillis = System.currentTimeMillis();

            // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
            var future1 = executor.schedule(System::currentTimeMillis, 200, TimeUnit.MILLISECONDS);
            var future2 = executor.schedule(System::currentTimeMillis, 100, TimeUnit.MILLISECONDS);
            var future3 = executor.schedule(System::currentTimeMillis, 210, TimeUnit.MILLISECONDS);

            // 确认整体任务执行完毕耗时 2100ms, 即最后一个任务执行的时间
            await().atMost(1, TimeUnit.SECONDS).until(() -> future1.isDone() && future2.isDone() && future3.isDone());
            then(System.currentTimeMillis() - startedMillis).isGreaterThanOrEqualTo(210).isLessThan(310);

            // 确认每个任务的延时时间, 和设定的延时时间一致
            then(future1.get() - startedMillis).isGreaterThanOrEqualTo(200).isLessThan(210);
            then(future2.get() - startedMillis).isGreaterThanOrEqualTo(100).isLessThan(110);
            then(future3.get() - startedMillis).isGreaterThanOrEqualTo(210).isLessThan(220);
        }
    }

    /**
     * 测试设定延时任务
     *
     * <p>
     * 通过 {@link Timer#schedule(TimerTask, long)} 方法可以提交一个延时任务,
     * 最后一个参数用于指定延时时间 (从当前时间起), 到达指定时间后, 任务才会被执行
     * </p>
     *
     * <p>
     * {@code schedule} 方法通过一个 {@link TimerTask} 对象执行任务,
     * 当指定时间到达后, {@link TimerTask#run()} 方法会被执行, 和
     * {@link java.util.concurrent.ScheduledFuture ScheduledFuture}
     * 类型不同, 需要在 {@link TimerTask} 中自行处理任务结果和任务状态
     * </p>
     */
    @Test
    void scheduleFuture_shouldScheduleTaskAfterWhileByTimer() {
        // 定义定时器任务类
        class RecordTask extends TimerTask {
            // 记录任务执行时间
            private long executionTime;

            @Override
            public void run() {
                executionTime = System.currentTimeMillis();
            }

            /**
             * 返回任务是否完成
             *
             * @return 任务是否完成
             */
            public boolean isDone() { return executionTime != 0; }

            /**
             * 获取任务执行时间
             *
             * @return 任务何时执行完毕的毫秒数
             */
            public long getExecutionTime() { return executionTime; }
        }

        // 定义定时器对象
        var timer = new Timer();

        // 记录起始时间
        var startedMillis = System.currentTimeMillis();

        // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
        var task1 = new RecordTask();
        timer.schedule(task1, 200);

        var task2 = new RecordTask();
        timer.schedule(task2, 100);

        var task3 = new RecordTask();
        timer.schedule(task3, 210);

        // 确认整体任务执行完毕耗时 2100ms, 即最后一个任务执行的时间
        await().atMost(3, TimeUnit.SECONDS).until(() -> task1.isDone() && task2.isDone() && task3.isDone());

        // 确认每个任务的延时时间, 和设定的延时时间一致
        then(task1.getExecutionTime() - startedMillis).isGreaterThanOrEqualTo(200).isLessThan(210);
        then(task2.getExecutionTime() - startedMillis).isGreaterThanOrEqualTo(100).isLessThan(110);
        then(task3.getExecutionTime() - startedMillis).isGreaterThanOrEqualTo(210).isLessThan(220);
    }

    /**
     * 按固定频率重复执行任务
     *
     * <p>
     * 通过 {@link java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(
     * Runnable, long, long, TimeUnit)
     * ScheduledExecutorService.scheduleAtFixedRate(Runnable,long,
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
        // 创建延时任务线程池
        var executor = threadPool.scheduledExecutor(0);

        // 记录起始时间
        var startedMillis = System.currentTimeMillis();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 启动一个固定频率的定时器任务
        var future = executor.scheduleAtFixedRate(
            // 记录执行时间
            () -> records.add(System.currentTimeMillis()),
            // 设置执行延迟时间和重复执行频率
            100, 200, TimeUnit.MILLISECONDS);

        // 等待定时器执行 3 次 (最大耗时约 600ms) 以后, 取消定时器
        await().atMost(600, TimeUnit.MILLISECONDS).untilAsserted(() -> then(records).hasSize(3));
        future.cancel(false);

        // 确认 3 此任务共耗时 500ms~600ms (第一次间隔 100ms, 后两次均间隔 200ms, 共 500ms)
        then(System.currentTimeMillis() - startedMillis).isGreaterThanOrEqualTo(500).isLessThan(600);

        // 确认每次任务执行间隔时间
        then(records).map(n -> (n - startedMillis) / 100).containsExactly(1L, 3L, 5L);
    }

    /**
     * 按固定频率重复执行任务
     *
     * <p>
     * 通过 {@link Timer#scheduleAtFixedRate(TimerTask, long, long)}
     * 方法可以按一个固定的频率重复执行某个任务, 后两个参数分别表示:
     * 任务第一次执行的延迟时间以及之后每次任务执行的间隔时间
     * </p>
     *
     * <p>
     * {@code scheduleAtFixedRate} 方法以第一次任务执行时间作为后续任务执行的基准,
     * 即经过 {@code delay} 参数延迟后的时间, 但由于每次任务执行后才会追加下一次任务,
     * 所以某次任务的阻塞仍有可能会影响下次任务 (例如阻塞时间超过了 {@code period})
     * 参数设定的间隔时间, 但下一次任务会尽可能的快速执行以弥补耽搁的时间,
     * 所以从宏观上看, {@code scheduleAtFixedRate} 方法仍可以认为是基于固定频率的
     * </p>
     */
    @Test
    void scheduleAtFixedRate_shouldScheduleTaskWithFixedRateByTimer() {
        // 实例化 Timer 对象
        var timer = new Timer();

        // 记录任务执行时间的集合
        var records = new ArrayList<Long>();

        // 实例化任务对象
        var task = new TimerTask() {
            @Override
            public void run() {
                // 记录执行时间
                records.add(System.currentTimeMillis());
            }
        };

        // 记录程序执行的起始时间
        var startedMillis = System.currentTimeMillis();

        // 开始执行任务, 第一次 100ms 后执行, 之后每 200ms 重复执行一次
        timer.scheduleAtFixedRate(task, 100, 200);

        // 等待执行 3 次后 (最大耗时 600ms), 取消任务执行
        await().atMost(600, TimeUnit.MILLISECONDS).untilAsserted(() -> then(records).hasSize(3));
        timer.cancel();

        // 确认每次任务的时间间隔
        then(records).map(n -> (n - startedMillis) / 100).containsExactly(1L, 3L, 5L);
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
        // 创建延时任务线程池
        var executor = threadPool.scheduledExecutor(0);

        // 记录起始时间
        var startedMillis = System.currentTimeMillis();

        // 记录每次任务执行时间的集合
        var records = new ArrayList<Long>();

        // 启动一个固定频率的定时器任务
        var future = executor.scheduleWithFixedDelay(
            // 记录执行时间
            () -> records.add(System.currentTimeMillis()),
            // 设置执行延迟时间和重复执行频率
            100, 200, TimeUnit.MILLISECONDS);

        // 等待定时器执行 3 次 (最大耗时 600ms) 以后, 取消定时器
        await().atMost(600, TimeUnit.MILLISECONDS).untilAsserted(() -> then(records).hasSize(3));
        future.cancel(false);

        // 确认 3 此任务共耗时 5s (第一次间隔 1s, 后两次均间隔 2s, 共 5s)
        then(System.currentTimeMillis() - startedMillis).isGreaterThanOrEqualTo(500).isLessThan(600);

        // 确认每次任务执行间隔时间
        then(records).map(n -> (n - startedMillis) / 100).containsExactly(1L, 3L, 5L);
    }

    /**
     * 按固定间隔时间重复执行任务
     *
     * <p>
     * 通过 {@link Timer#schedule(TimerTask, long, long)}
     * 方法可以按一个固定的频率重复执行某个任务,
     * 后两个参数用于表示任务第一次执行的延迟时间和之后每次执行的间隔时间
     * </p>
     *
     * <p>
     * {@code schedule} 是以上一次任务执行时间来计算下一次任务执行的时间的, 即
     * {@code delay} 参数表示的是两次任务的间隔时间, 所以如果一次任务的执行时间超过了
     * {@code delay} 参数, 则后续的任务都会受到影响
     * </p>
     */
    @Test
    void schedule_shouldScheduleTaskWithFixedDelayByTimer() {
        // 实例化 Timer 对象
        var timer = new Timer();

        // 记录任务执行时间的集合
        var records = new ArrayList<Long>();

        // 实例化任务对象
        var task = new TimerTask() {
            @Override
            public void run() {
                // 记录执行时间
                records.add(System.currentTimeMillis());
            }
        };

        // 记录程序执行的起始时间
        var startedMillis = System.currentTimeMillis();

        // 开始执行任务, 第一次 100ms 后执行, 之后每 200ms 重复执行一次
        timer.schedule(task, 100, 200);

        // 等待执行 3 次 (最大耗时 600ms) 后, 取消任务执行
        await().atMost(600, TimeUnit.MILLISECONDS).untilAsserted(() -> then(records).hasSize(3));
        timer.cancel();

        // 确认每次任务的时间间隔
        then(records).map(n -> (n - startedMillis) / 100).containsExactly(1L, 3L, 5L);
    }
}
