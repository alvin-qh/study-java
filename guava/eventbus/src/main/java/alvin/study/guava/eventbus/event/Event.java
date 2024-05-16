package alvin.study.guava.eventbus.event;

/**
 * 事件接口, 表示发送到事件总线的事件对象类型
 *
 * <p>
 * 通过该接口, 给事件的发布和订阅提供一个标准, 而不是令任意类型对象都可以进行发布订阅, 造成系统语义上的混乱
 * </p>
 *
 * <p>
 * 在事件发布时, 应该发布该接口类型对象, 参考:
 * {@link alvin.study.eventbus.repository.UserRepository#insertUser(alvin.study.eventbus.model.User)
 * UserRepository.insertUser(User)} 方法中的事件发布
 * </p>
 *
 * <p>
 * 在事件订阅时, 应该订阅该接口类型, 参考: {@link alvin.study.eventbus.handler.UserHandler#onUserCreated(UserEvent)
 * UserHandler#onUserCreated(UserEvent)} 方法
 * </p>
 */
public interface Event<T> {
    /**
     * 获取事件包含的载荷对象
     *
     * @return 载荷对象
     */
    T payload();

    /**
     * 获取事件的相关行为
     *
     * @return 表示事件行为的 {@link Action} 枚举对象
     */
    Action action();

    /**
     * 检查事件的行为是否匹配
     *
     * @param action 预期的行为对象
     * @return {@code true} 表示当前事件的行为和所给预期行为一致
     */
    default boolean checkAction(Action action) {
        return action == this.action();
    }

    /**
     * 事件行为枚举
     */
    public enum Action {
        /**
         * 创建实体行为
         */
        CREATE,
        /**
         * 读取实体行为
         */
        READ,
        /**
         * 更新实体行为
         */
        UPDATE,
        /**
         * 删除实体行为
         */
        DELETE;
    }
}
