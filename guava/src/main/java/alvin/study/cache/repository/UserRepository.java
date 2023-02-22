package alvin.study.cache.repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.hash.BloomFilter;

import alvin.study.cache.model.User;

/**
 * 用于 {@link User} 对象的持久化类型
 */
public class UserRepository {
    // 存储 User 对象的 Map
    private final Map<Long, User> userStorage = new ConcurrentHashMap<>();

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
     * @param user {@link User} 对象
     */
    public void insertUser(User user) {
        userStorage.put(user.getId(), user);
    }

    /**
     * 通过所有持久化的 {@link User} 实体产生对应的布隆过滤器
     *
     * @return 布隆过滤器对象
     */
    public BloomFilter<Long> toBloomFilter(long expectedInsertions) {
        return userStorage.values()
                .stream()
                .map(User::getId)
                .collect(BloomFilter.toBloomFilter((id, into) -> into.putLong(id), expectedInsertions, 0.001));
    }
}
