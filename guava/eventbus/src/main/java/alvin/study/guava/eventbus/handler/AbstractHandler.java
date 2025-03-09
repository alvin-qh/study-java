package alvin.study.guava.eventbus.handler;

import com.google.common.eventbus.EventBus;

/**
 * {@link Handler} 接口的基本实现, 为当前对象增加事件订阅
 *
 * <p>
 * {@link EventBus#register(Object)} 方法将一个对象注册为事件监听对象,
 * 当一个事件被发布时, 事件监听对象中标记了
 * {@link com.google.common.eventbus.Subscribe @Subscribe}
 * 注解的方法将接收到对应类型的事件对象进行处理
 * </p>
 *
 * <p>
 * 当前类型用于将通过自身产生的对象注册到指定的 {@link EventBus} 对象中,
 * 参见 {@link #AbstractHandler()} 构造器方法
 * </p>
 */
public abstract class AbstractHandler implements Handler {
    /**
     * 构造器, 将当前对象注册到 {@link EventBus} 对象中
     */
    protected AbstractHandler() {
        eventBus().register(this);
    }

    @Override
    public void unregister() {
        eventBus().unregister(this);
    }

    /**
     * 获取用于对当前类型对象进行注册的 {@link EventBus} 对象
     *
     * @return 用于对当前类型对象进行注册的 {@link EventBus} 对象
     */
    protected abstract EventBus eventBus();
}
