package alvin.study.guava.cache.observer;

import jakarta.annotation.Nonnull;

import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.Subscribe;

import alvin.study.guava.cache.event.UserDeleteEvent;
import alvin.study.guava.cache.event.UserUpdateEvent;
import alvin.study.guava.cache.model.User;

/**
 * 用于缓存处理的观察者类
 *
 * <p>
 * {@link #onUserUpdated(UserUpdateEvent)} 方法用于监听 {@link UserUpdateEvent} 类型事件,
 * 完成 {@link User}
 * 实体被更新后的缓存处理工作
 * </p>
 *
 * <p>
 * {@link #onUserDeleted(UserDeleteEvent)} 方法用于监听 {@link UserDeleteEvent} 类型事件,
 * 完成 {@link User}
 * 实体被删除后的缓存处理工作
 * </p>
 */
public class CacheObserver {
    // 缓存对象
    private final LoadingCache<Long, User> cache;

    /**
     * 构造器, 设置相关的缓存对象
     *
     * @param cache {@link LoadingCache} 类型缓存对象
     */
    public CacheObserver(LoadingCache<Long, User> cache) {
        this.cache = cache;
    }

    /**
     * 处理 {@link User} 实体更新后的后续缓存操作
     *
     * <p>
     * {@link User} 实体对象更新后, 如果缓存中存在对应的缓存项, 则对其进行刷新
     * </p>
     *
     * @param event {@link UserUpdateEvent} 事件对象, 表示一个 {@link User} 实体被更新
     */
    @Subscribe
    public void onUserUpdated(@Nonnull UserUpdateEvent event) {
        var user = event.getEntry();
        cache.refresh(user.id());
    }

    /**
     * 处理 {@link User} 实体删除后的后续缓存操作
     *
     * <p>
     * {@link User} 实体对象删除后, 如果缓存中存在对应的缓存项, 则对其进行删除
     * </p>
     *
     * @param event {@link UserDeleteEvent} 事件对象, 表示一个 {@link User} 实体被删除
     */
    @Subscribe
    public void onUserDeleted(@Nonnull UserDeleteEvent event) {
        var user = event.getEntry();
        cache.invalidate(user.id());
    }
}
