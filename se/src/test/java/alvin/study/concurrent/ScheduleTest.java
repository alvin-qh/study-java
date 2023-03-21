package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.lang.ref.WeakReference;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * 测试延时任务
 *
 * <p>
 * {@link ScheduledExecutorService} 接口用于表示一个延时异步任务执行器, 该执行器通过
 * {@link ScheduledExecutorService#schedule(java.util.concurrent.Callable, long, TimeUnit)
 * ScheduledExecutorService.schedule(Callable, long, TimeUnit)} 方法提交一个延时任务, 该任务会在指定的时间之后执行
 * </p>
 *
 * <p>
 * {@link ScheduledThreadPoolExecutor} 类型实现了 {@link ScheduledExecutorService} 接口, 即通过线程池的方式处理延时异步任务,
 * {@link ScheduledThreadPoolExecutor} 从 {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor} 类继承,
 * 两者的主要区别在于前者使用了"延时队列"来作为任务队列
 * </p>
 *
 * <p>
 * {@link ScheduledThreadPoolExecutor#execute(Runnable)} 和
 * {@link ScheduledThreadPoolExecutor#submit(java.util.concurrent.Callable)
 * ScheduledThreadPoolExecutor.submit(Callable)} 方法的语义未发生变化, 但也可以理解为提交了"延时时间为 {@code 0}"的延时任务
 * </p>
 */
@SuppressWarnings("java:S2925")
class ScheduleTest {
    // 延时任务执行器对象
    private WeakReference<ExecutorService> executorHolder;

    /**
     * 每次测试结束后执行, 用于关闭线程池
     */
    @AfterEach
    void afterEach() {
        var executor = executorHolder.get();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private ScheduledExecutorService createScheduledExecutorService() {
        // 获取当前 CPU 的逻辑内核数 (Logical Kernel)
        var maxThreads = Runtime.getRuntime().availableProcessors();

        // 实例化延迟任务线程池对象
        var executor = new ScheduledThreadPoolExecutor(maxThreads);
        executorHolder = new WeakReference<>(executor);
        return executor;
    }

    /**
     * 测试延时队列
     *
     * <p>
     * 延时队列 ({@link DelayQueue}) 是一种阻塞式优先队列 ({@link java.util.concurrent.PriorityBlockingQueue
     * PriorityBlockingQueue}), 其队列元素具备延时, 在时间到达后方可按时间到达的顺序从队列中获取
     * </p>
     *
     * <p>
     * {@link DelayQueue} 中的元素类型必须实现自 {@link java.util.concurrent.Delayed Delayed} 接口, 其中:
     * <ul>
     * <li>
     * {@link java.util.concurrent.Delayed#getDelay(TimeUnit) Delayed.getDelay(TimeUnit)} 方法用于确定延时剩余时间,
     * 当其返回值小于等于 {@code 0} 后, 方可从队列中取出
     * </li>
     * <li>
     * {@link java.util.concurrent.Delayed#compareTo(java.util.concurrent.Delayed)
     * Delayed.compareTo(Delayed)} 方法比较两个队列元素, 用于确定元素在队列中的"优先级", 比较结果越小的元素具有越高的出队优先级
     * </li>
     * </ul>
     * 参考 {@link DelayedValue} 类型
     * </p>
     */
    @Test
    void delayQueue_shouldGetDelayedValueFromQueue() throws InterruptedException {
        // 创建延时队列
        var queue = new DelayQueue<DelayedValue<Integer>>();

        // 入队 3 个元素, 并各自具备不同的延时时间
        queue.offer(new DelayedValue<>(1, 2000, TimeUnit.MILLISECONDS));
        queue.offer(new DelayedValue<>(2, 1000, TimeUnit.MILLISECONDS));
        queue.offer(new DelayedValue<>(3, 2100, TimeUnit.MILLISECONDS));

        // 记录当前时间
        var millis = System.currentTimeMillis();

        // 出队, 因为此时无元素到达延时时间, 所以返回值为 null
        then(queue.poll()).isNull();

        // 休眠 1s 后再次出队, 此时可以出队延时时间为 1s 的元素
        Thread.sleep(1000);
        then(queue.poll()).extracting("value").isEqualTo(2);

        // 继续出队, 因为此时无元素到达延时时间, 所以返回值为 null
        then(queue.poll()).isNull();

        // 通过 take 方法, 阻塞直到有元素出队, 此时出队延时为 2s 的元素
        then(queue.take()).extracting("value").isEqualTo(1);

        // 继续出队, 并设定超时时间为 110ms, 此时出队延时时间为 2.1s 的元素
        then(queue.poll(110, TimeUnit.MILLISECONDS)).extracting("value").isEqualTo(3);

        // 确认整体出队耗时 2100ms, 为延时时间最久的元素出队时间
        then(System.currentTimeMillis() - millis).isGreaterThanOrEqualTo(2100).isLessThan(2200);
    }

    /**
     * 测试设定延时任务
     *
     * <p>
     * 通过 {@link ScheduledExecutorService#schedule(java.util.concurrent.Callable, long, TimeUnit)
     * ScheduledExecutorService.schedule(Callable, long, TimeUnit)} 方法可以提交一个延时任务, 后两个参数指定延时时间
     * (从当前时间起), 到达指定时间后, 任务才会被执行
     * </p>
     *
     * <p>
     * {@code schedule} 方法返回 {@link java.util.concurrent.ScheduledFuture ScheduledFuture} 类型对象,
     * 用于查看任务执行情况, 获取任务执行结果, 包括:
     * <ul>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#isDone() ScheduledFuture.isDone()} 方法查看任务是否执行完毕
     * </li>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#get() ScheduledFuture.get()} 方法获取任务执行结果
     * </li>
     * <li>
     * 通过 {@link java.util.concurrent.ScheduledFuture#getDelay(TimeUnit) ScheduledFuture.getDelay(TimeUnit)}
     * 方法获取任务剩余延时时间
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void scheduleFuture_shouldMakeScheduleForTasks() throws Exception {
        // 定义表示记录的类型, 记录一个时间戳和任务编号
        record Record(long timestamp, int no) {}

        // 创建延时任务线程池
        var executor = createScheduledExecutorService();

        // 记录起始时间
        var startedMillis = System.currentTimeMillis();

        // 提交 3 个延时任务, 为每个任务设置延时时间, 任务结果为 Record 类型对象
        var future1 = executor.schedule(() -> new Record(System.currentTimeMillis(), 1), 2000, TimeUnit.MILLISECONDS);
        var future2 = executor.schedule(() -> new Record(System.currentTimeMillis(), 2), 1000, TimeUnit.MILLISECONDS);
        var future3 = executor.schedule(() -> new Record(System.currentTimeMillis(), 2), 2100, TimeUnit.MILLISECONDS);

        // 确认整体任务执行完毕耗时 2100ms, 即最后一个任务执行的时间
        await().atMost(3, TimeUnit.SECONDS).until(() -> future1.isDone() && future2.isDone() && future3.isDone());
        then(System.currentTimeMillis() - startedMillis).isGreaterThanOrEqualTo(2100).isLessThan(2200);

        // 确认每个任务的延时时间, 和设定的延时时间一致
        then(future1.get().timestamp() - startedMillis).isGreaterThanOrEqualTo(2000).isLessThan(2100);
        then(future2.get().timestamp() - startedMillis).isGreaterThanOrEqualTo(1000).isLessThan(1100);
        then(future3.get().timestamp() - startedMillis).isGreaterThanOrEqualTo(2100).isLessThan(2200);
    }
}
