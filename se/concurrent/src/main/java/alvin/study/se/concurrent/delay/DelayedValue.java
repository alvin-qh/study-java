package alvin.study.se.concurrent.delay;

import java.time.Instant;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列元素类型
 *
 * <p>
 * {@link DelayQueue} 中的元素类型必须实现自 {@link java.util.concurrent.Delayed Delayed} 接口, 其中:
 * <ul>
 * <li>
 * {@link java.util.concurrent.Delayed#getDelay(TimeUnit) Delayed.getDelay(TimeUnit)} 方法用于确定延时剩余时间,
 * 当其返回值小于等于 {@code 0} 后, 方可从队列中取出
 * </li>
 * <li>
 * {@link java.util.concurrent.Delayed#compareTo(Object)}
 * Delayed.compareTo(Delayed)} 方法比较两个队列元素, 用于确定元素在队列中的"优先级", 比较结果越小的元素具有越高的出队优先级
 * </li>
 * </ul>
 * </p>
 */
public class DelayedValue<T> implements Delayed {
    // 队列元素值
    private final T value;

    // 队列元素的创建时间
    private final Instant createdAt;

    // 该队列元素的延迟时间
    private final long delayMillis;

    /**
     * 构造器, 设定元素值和延迟时间
     *
     * @param value 元素值
     * @param delay 元素延迟时间
     * @param unit  延迟时间的单位
     */
    public DelayedValue(T value, long delay, TimeUnit unit) {
        this.value = value;
        // 记录当前对象创建时间
        this.createdAt = Instant.now();
        // 记录要延迟时间的毫秒数
        this.delayMillis = unit.toMillis(delay);
    }

    /**
     * 获取元素值
     *
     * @return 元素值
     */
    public T getValue() { return value; }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }
        return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 计算要延迟到的时间毫秒值
        var delayedTo = createdAt.toEpochMilli() + delayMillis;

        // 计算当前时间毫秒值
        var now = Instant.now().toEpochMilli();

        // 返回剩余的延迟时间
        return unit.convert(delayedTo - now, TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return String.format(
            "DelayedValue(value=%s, createdAt=%s, delayedMillis=%d)", value, createdAt, delayMillis);
    }
}
