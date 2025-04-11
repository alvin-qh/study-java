package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.TimeIt;

/**
 * 测试通过 {@link Timer} 类处理进行定时任务
 *
 * <p>
 * {@link Timer} 类也可以处理定时任务, 且两者具有类似的方法, 和
 * {@link java.util.concurrent.ScheduledExecutorService
 * ScheduledExecutorService} 相比, {@link Timer}
 * 也是通过延时队列来作为任务队列的, 但后者只启动了一个线程
 * (而不是通过线程池), 所以一旦队列中有某个任务执行时间过久或被阻塞,
 * 这会影响到之后的所有其它任务
 * </p>
 *
 * <p>
 * 且 {@link Timer} 类型属于较早期的 API, 使用已经过时的
 * {@link java.util.Date Date} 和 {@link java.util.Calendar
 * Calendar} 类型来表示时间
 * </p>
 */
class TimerTest {
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
            private TimeIt timeit = TimeIt.start();

            // 记录任务是否完成
            private boolean done = false;

            @Override
            public void run() {
                this.timeit.restart();
                this.done = true;
            }

            /**
             * 返回任务是否完成
             *
             * @return 任务是否完成
             */
            public boolean isDone() { return this.done; }

            /**
             * 获取任务执行时间
             *
             * @return 任务何时执行完毕的毫秒数
             */
            public TimeIt getExecutionTime() { return this.timeit; }
        }

        // 定义定时器对象
        var timer = new Timer();

        // 记录起始时间
        var timeit = TimeIt.start();

        // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
        var task1 = new RecordTask();
        timer.schedule(task1, 200);

        var task2 = new RecordTask();
        timer.schedule(task2, 100);

        var task3 = new RecordTask();
        timer.schedule(task3, 210);

        // 确认整体任务执行完毕耗时 210ms, 即最后一个任务执行的时间
        await().atMost(1, TimeUnit.SECONDS)
                .until(() -> task1.isDone() && task2.isDone() && task3.isDone());

        // 取消定时任务
        timer.cancel();

        // 确认每个任务的延时时间, 和设定的延时时间一致
        then(timeit.since(task1.getExecutionTime())).isBetween(200L, 210L);
        then(timeit.since(task2.getExecutionTime())).isBetween(100L, 110L);
        then(timeit.since(task3.getExecutionTime())).isBetween(210L, 220L);
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
        var records = new ArrayList<TimeIt>();

        // 实例化任务对象
        var task = new TimerTask() {
            @Override
            public void run() {
                // 记录执行时间
                records.add(TimeIt.start());
            }
        };

        // 记录程序执行的起始时间
        var timeit = TimeIt.start();

        // 开始执行任务, 第一次 100ms 后执行, 之后每 200ms 重复执行一次
        timer.scheduleAtFixedRate(task, 100, 200);

        // 等待执行 3 次后 (最大耗时 600ms), 取消任务执行
        await().atMost(600, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> then(records).hasSize(3));

        // 取消定时任务
        timer.cancel();

        // 确认每次任务的时间间隔
        then(records).map(t -> timeit.since(t) / 100)
                .containsExactly(1L, 3L, 5L);
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
        var records = new ArrayList<TimeIt>();

        // 实例化任务对象
        var task = new TimerTask() {
            @Override
            public void run() {
                // 记录执行时间
                records.add(TimeIt.start());
            }
        };

        // 记录程序执行的起始时间
        var timeit = TimeIt.start();

        // 开始执行任务, 第一次 100ms 后执行, 之后每 200ms 重复执行一次
        timer.schedule(task, 100, 200);

        // 等待执行 3 次 (最大耗时 600ms) 后, 取消任务执行
        await().atMost(600, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> then(records).hasSize(3));

        // 取消定时任务
        timer.cancel();

        // 确认每次任务的时间间隔
        then(records).map(t -> timeit.since(t) / 100)
                .containsExactly(1L, 3L, 5L);
    }
}
