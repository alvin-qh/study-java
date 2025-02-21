package alvin.study.guava.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import alvin.study.guava.cache.observer.CacheObserver;

/**
 * 缓存消息总线类
 *
 * <p>
 * 该消息总线用于发布 {@link Event} 类型消息对象到总线上, 并由 {@link CacheObserver} 类型对象 (如果进行了注册)
 * 对发布的事件进行监听
 * </p>
 */
public final class CacheEventBus implements SubscriberExceptionHandler {
    // 日志对象
    private static final Logger LOG = Logger.getLogger(CacheEventBus.class.getName());

    // 当前类型单例对象
    private static final CacheEventBus INSTANCE = new CacheEventBus();

    // 用于发布事件和监听事件的消息总线对象
    private final EventBus eventBus = new EventBus(this);

    /**
     * 获取单例对象
     *
     * @return 当前类型的单例对象
     */
    public static CacheEventBus getInstance() { return INSTANCE; }

    /**
     * 统一事件异常处理
     *
     * <p>
     * 如果在事件处理方法 (例如
     * {@link CacheObserver#onUserUpdated(alvin.study.cache.event.UserUpdateEvent)
     * CacheObserver.onUserUpdated(UserUpdateEvent)} 方法) 中抛出异常, 则该异常统一由该方法处理
     * </p>
     *
     * <p>
     * 一般情况下为了避免耦合, 事件处理方法的异常需要单独处理, 不会向上传递到事件发布的方法中, 但本例中需要对
     * {@link RuntimeException}
     * 和 {@link Error} 类型异常向上传递, 以保证如果缓存处理失败, 则实体操作方法可以正确的回滚事务
     * </p>
     */
    @Override
    public void handleException(@Nonnull Throwable ex, @Nonnull SubscriberExceptionContext ctx) {
        // 对 RuntimeException 类型异常继续向上传递
        if (ex instanceof RuntimeException rex) {
            throw rex;
        }

        // 对 Error 类型异常继续向上传递
        if (ex instanceof Error err) {
            throw err;
        }

        // 其它类型异常不向上传递, 记录日志即可
        LOG.log(Level.SEVERE, "Exception caused", ex);
    }

    /**
     * 发布事件到消息总线
     *
     * @param event {@link Event} 类型对象, 表示一个事件
     */
    public void post(@Nonnull Event<?> event) {
        eventBus.post(event);
    }

    /**
     * 注册一个在当前消息总线进行监听的观察者对象
     *
     * @param observer 用于对缓存进行后续处理的 {@link CacheObserver} 类型对象
     */
    public void register(@Nonnull CacheObserver observer) {
        eventBus.register(observer);
    }
}
