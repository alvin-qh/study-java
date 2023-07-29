package alvin.study.guava.eventbus.event;

import alvin.study.guava.eventbus.model.User;

/**
 * 针对于 {@link User} 类型定义的对应事件类型
 */
public class UserEvent extends DefaultEvent<User> {
    /**
     * 构造器, 通过 {@link User} 对象和事件类型构造对象
     *
     * @param payload 事件载荷, 为 {@link User} 类型对象
     * @param action  事件类型
     */
    public UserEvent(User payload, Action action) {
        super(payload, action);
    }
}
