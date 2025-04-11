package alvin.study.se.concurrent.util;

import java.util.concurrent.TimeUnit;

/**
 * 用于测量代码执行时间的工具类
 */
public final class TimeIt {
    private long timestamp;

    /**
     * 构造器, 创建一个 {@link TimeIt} 对象
     *
     * @param timestamp 初始时间戳
     */
    private TimeIt(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 创建一个 {@link TimeIt} 对象, 并初始化时间戳为当前时间戳
     *
     * @return {@link TimeIt} 对象
     */
    public static TimeIt start() {
        return new TimeIt(System.currentTimeMillis());
    }

    /**
     * 获取从创建 {@link TimeIt} 对象到调用该方法的时间间隔
     *
     * @return 时间间隔, 单位毫秒
     */
    public long since() {
        return System.currentTimeMillis() - this.timestamp;
    }

    /**
     * 重置时间戳, 并返回从创建 {@link TimeIt} 对象到调用该方法的时间间隔
     *
     * @return 时间间隔, 单位毫秒
     */
    public long restart() {
        var timestamp = System.currentTimeMillis();
        var since = System.currentTimeMillis() - timestamp;

        this.timestamp = timestamp;
        return since;
    }

    /**
     * 创建一个 {@link TimeIt} 对象, 并初始化时间戳为当前时间戳
     *
     * @return {@link TimeIt} 对象
     */
    public TimeIt fork() {
        return new TimeIt(this.timestamp);
    }

    /**
     * 计算两个 {@link TimeIt} 对象的时间间隔
     *
     * @param other 另一个 {@link TimeIt} 对象
     * @return 时间间隔, 单位毫秒
     */
    public long since(TimeIt other) {
        return other.timestamp - this.timestamp;
    }

    /**
     * 计算两个 {@link TimeIt} 对象的时间间隔
     *
     * @param timestamp 另一个 {@link TimeIt} 对象的时间戳
     * @param unit      时间单位
     * @return 时间间隔, 单位毫秒
     */
    public long since(long timestamp, TimeUnit unit) {
        return unit.toMillis(timestamp) - this.timestamp;
    }
}
