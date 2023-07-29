package alvin.study.guava.cache;

/**
 * 事件接口
 *
 * <p>
 * 所有发往消息总线的事件对象, 其类型必须为当前接口类型
 * </p>
 *
 * @param <T> 实体类型
 */
public interface Event<T> {
    /**
     * 获取实体对象
     *
     * @return 当前事件对应的实体对象
     */
    T getEntry();
}
