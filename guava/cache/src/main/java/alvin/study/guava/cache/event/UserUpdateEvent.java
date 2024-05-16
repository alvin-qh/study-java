package alvin.study.guava.cache.event;

import alvin.study.guava.cache.model.User;

/**
 * 表示 {@link User} 实体被更新的事件类
 */
public class UserUpdateEvent extends SimpleEvent<User> {
    public UserUpdateEvent(User entry) {
        super(entry);
    }
}
