package alvin.study.guava.cache.event;

import alvin.study.guava.cache.Event;

/**
 * 简单事件类
 *
 * <p>
 * 该类为抽象类, 以最简单的方式实现了 {@link Event} 接口
 * </p>
 *
 * @param <T> 实体类型
 */
public abstract class SimpleEvent<T> implements Event<T> {
    // 事件相关实体对象
    private final T entry;

    /**
     * 构造器, 设置实体对象
     *
     * @param entry 事件相关的实体对象
     */
    protected SimpleEvent(T entry) {
        this.entry = entry;
    }

    @Override
    public T getEntry() { return entry; }
}
