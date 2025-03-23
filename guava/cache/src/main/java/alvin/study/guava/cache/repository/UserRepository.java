package alvin.study.guava.cache.repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.cache.LoadingCache;
import com.google.common.hash.BloomFilter;

import alvin.study.guava.cache.CacheEventBus;
import alvin.study.guava.cache.event.UserDeleteEvent;
import alvin.study.guava.cache.event.UserUpdateEvent;
import alvin.study.guava.cache.model.User;

/**
 * 用于 {@link User} 对象的持久化类型
 */
public class UserRepository {
    // 存储 User 对象的 Map
    private final Map<Long, User> userStorage = new ConcurrentHashMap<>();

    // 消息总线
    private final CacheEventBus eventBus = CacheEventBus.getInstance();

    /**
     * 根据 {@code id} 属性查找 {@link User} 对象
     *
     * @param id {@link User} 对象的 {@code id} 属性
     * @return {@link User} 对象的 {@link Optional} 包装对象
     */
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userStorage.get(Objects.requireNonNull(id)));
    }

    /**
     * 插入 {@link User} 对象
     *
     * <p>
     * 该方法不会向消息总线发送消息, 因为缓存在未命中后, {@link LoadingCache}
     * 对象会通过内部的加载方法读取数据源
     * </p>
     *
     * @param user {@link User} 对象
     */
    public void insertUser(User user) {
        userStorage.compute(user.id(), (key, value) -> {
            if (value != null) {
                throw new IllegalArgumentException("id %d already exists");
            }
            return user;
        });
    }

    /**
     * 更新 {@link User} 对象
     *
     * <p>
     * 该方法会在更新完实体后, 向消息总线发送 {@link UserUpdateEvent} 事件,
     * 以更新缓存
     * </p>
     *
     * @param user {@link User} 对象
     */
    public void updateUser(User user) {
        userStorage.compute(user.id(), (key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("id %d not exists");
            }
            return user;
        });

        // 向消息总线发送用户已更新的消息
        eventBus.post(new UserUpdateEvent(user));
    }

    /**
     * 删除 {@link User} 对象
     *
     * <p>
     * 该方法会在更新完实体后, 向消息总线发送 {@link UserDeleteEvent} 事件,
     * 以删除缓存
     * </p>
     *
     * @param userId {@link User} 实体的 {@code id} 属性值
     */
    public void deleteUser(Long userId) {
        var removedUser = userStorage.remove(userId);

        // 向消息总线发送用户已删除的消息
        if (removedUser != null) {
            eventBus.post(new UserDeleteEvent(removedUser));
        }
    }

    /**
     * 通过所有持久化的 {@link User} 实体产生对应的布隆过滤器
     *
     * @return 布隆过滤器对象
     */
    public BloomFilter<Long> toBloomFilter(long expectedInsertions) {
        return userStorage.values()
                .stream()
                .map(User::id)
                .collect(BloomFilter.toBloomFilter(
                    (id, into) -> into.putLong(id), expectedInsertions, 0.001));
    }
}
