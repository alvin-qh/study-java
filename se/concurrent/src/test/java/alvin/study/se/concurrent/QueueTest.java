package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.delay.DelayedValue;

public class QueueTest {
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
}
