package alvin.study.cache.event;

import alvin.study.cache.model.User;

/**
 * 表示 {@link User} 实体被删除的事件类
 */
public class UserDeleteEvent extends SimpleEvent<User> {
    public UserDeleteEvent(User entry) {
        super(entry);
    }
}
