package alvin.study.guava.eventbus.event;

/**
 * 定义一个通用的 {@link Event} 接口实现类
 */
public class DefaultEvent<T> implements Event<T> {
    // 事件携带的载荷对象
    private final T payload;

    // 事件携带的行为对象
    private final Action action;

    /**
     * 构造器, 创建通用事件对象
     *
     * @param payload 事件的载荷
     * @param action  事件的行为
     */
    public DefaultEvent(T payload, Action action) {
        this.payload = payload;
        this.action = action;
    }

    @Override
    public T payload() {
        return payload;
    }

    @Override
    public Action action() {
        return action;
    }
}
