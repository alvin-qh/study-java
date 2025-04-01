package alvin.study.springboot.shiro.core.shiro;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;

import io.lettuce.core.RedisException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存管理器类型
 *
 * <p>
 * 缓存管理器是 {@link CustomerRealm} 中用于缓存用户登录信息和角色权限信息的类型, 可以有效的提高登录认证的效率, 避免频繁查询数据库
 * </p>
 *
 * <p>
 * 缓存管理器通过 {@link #getCache(String)} 方法根据一个名称获取对应主题的缓存对象
 * </p>
 *
 * <p>
 * 缓存对象是一个实现了 {@link Cache} 接口的对象, 通过一个 {@code key} 值对缓存进行存取操作
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {
    // 缓存 key 模板
    private static final String KEY_FORMAT = "shiro:cache:%s:%s";

    // 注入 Redis 操作对象
    private final RedisTemplate<String, Object> redis;

    /**
     * 根据名称获取对应的缓存对象
     *
     * @param <K>  缓存 Key 类型
     * @param <V>  缓存值类型
     * @param name 缓存主题名称, 不同主题名称的缓存内容应该相互隔离
     * @return 缓存对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        // 返回缓存对象
        return new Cache<K, V>() {
            /**
             * 对原始缓存 key 进行处理, 增加前缀, 以保证缓存的隔离性
             *
             * @param key 原始缓存 key
             * @return 处理后的缓存 key
             */
            private String makeKey(Object key) {
                return String.format(KEY_FORMAT, name, key);
            }

            /**
             * 清空当前缓存范围内的所有 key
             */
            @Override
            public void clear() throws CacheException {
                var skey = makeKey("*");
                log.debug("All cache (key={}) was deleting", skey);

                try {
                    var keys = redis.keys(skey);
                    if (keys != null) {
                        redis.delete(keys);
                    }
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot clear cache from redis", e);
                }
            }

            /**
             * 根据原始缓存 key 值获取缓存内容
             *
             * @param key 原始缓存 key 值
             * @return 缓存内容
             */
            @Override
            public V get(K key) throws CacheException {
                // 获取实际缓存 key 值
                var skey = makeKey(key);
                log.debug("Cache (key={}) was loading", skey);

                try {
                    // 获取缓存值
                    return (V) SessionUtil.stringToObject((String) redis.opsForValue().get(skey));
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot get cache from redis", e);
                    return null;
                }
            }

            /**
             * 获取当前缓存范围内的所有 key 值
             *
             * @return 当前范围内的所有缓存 key
             */
            @Override
            public Set<K> keys() {
                var skey = makeKey("*");
                log.debug("All cache keys (key={}) was loading", skey);

                try {
                    // 获取指定范围内的所有 key 值
                    return (Set<K>) redis.keys(skey);
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot get cache keys from redis", e);
                    return Set.of();
                }
            }

            /**
             * 根据所给的 key 缓存内容
             *
             * @param key   原始缓存 key 值
             * @param value 要缓存的内容
             * @return 缓存后的内容
             */
            @Override
            public V put(K key, V value) throws CacheException {
                var skey = makeKey(key);
                log.debug("Cache (key={}) was saving", skey);

                try {
                    // 根据 key 值缓存内容
                    redis.opsForValue().set(skey, SessionUtil.objectToString(value));
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot save cache into redis", e);
                }
                return value;
            }

            /**
             * 根据所给的 key 删除指定缓存内容
             *
             * @param key 要删除的缓存 key 值
             * @return 被删除的缓存值
             */
            @Override
            public V remove(K key) throws CacheException {
                var skey = makeKey(key);
                log.debug("Cache (key={}) was deleting", skey);

                try {
                    // 删除指定 key
                    return (V) SessionUtil.stringToObject((String) redis.opsForValue().getAndDelete(skey));
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot remove cache from redis", e);
                    return null;
                }
            }

            /**
             * 获取缓存集合长度
             *
             * @return 缓存数量
             */
            @Override
            public int size() {
                try {
                    return keys().size();
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot get cache size from redis", e);
                    return 0;
                }
            }

            /**
             * 获取所有缓存的值
             *
             * @return 缓存的值集合
             */
            @Override
            public Collection<V> values() {
                try {
                    var keys = redis.keys(makeKey("*"));

                    return Optional.ofNullable(redis.opsForValue().multiGet(keys))
                            .orElse(List.of())
                            .stream()
                            .map(o -> (V) SessionUtil.stringToObject((String) o))
                            .toList();
                } catch (DataAccessException | RedisException e) {
                    log.error("Cannot get cache values from redis", e);
                    return List.of();
                }
            }
        };
    }
}
