package alvin.study.springboot.security.core.cache;

import alvin.study.springboot.security.infra.entity.Menu;
import alvin.study.springboot.security.infra.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Cache {
    private static final String KEY_USER = "user:token:";
    private static final String KEY_MENU = "user:%d:menu";
    private static final String KEY_PERMISSION = "user:%d:permission";

    // 注入 RedisTemplate 对象
    private final RedisTemplate<String, Object> redis;

    /**
     * 删除所有的 redis key
     */
    public void removeAllKeys() {
        try {
            var keys = redis.keys("*");
            if (keys != null) {
                redis.delete(keys);
            }
            log.debug("Remove all keys from cache");
        } catch (Exception e) {
            // log.error("Cannot delete keys from cache", e);
        }
    }

    /**
     * 存储用户对象
     *
     * @param token   登录的 Token
     * @param user    用户对象
     * @param expired 存储有效时间
     */
    public User saveUser(String token, User user, Duration expired) {
        try {
            redis.opsForValue().set(KEY_USER + token, user, expired);
            log.debug("Save user(id = {}) into cache", user.getId());
        } catch (Exception e) {
            // log.error("Cannot save user into cache", e);
        }
        return user;
    }

    /**
     * 读取用户对象
     *
     * @param token 登录的 Token
     * @return 用户对象
     */
    public Optional<User> loadUser(String token) {
        var key = KEY_USER + token;
        try {
            var user = (User) redis.opsForValue().get(key);
            if (user == null) {
                log.debug("No expected user in cache");
                return Optional.empty();
            }
            log.debug("Load user(id = {}) from cache", user.getId());
            return Optional.of(user);
        } catch (Exception e) {
            // log.error("Cannot load user from cache", e);
            return Optional.empty();
        }
    }

    /**
     * 存储菜单数据
     *
     * @param userId 菜单相关的用户 ID
     * @param menus  菜单集合
     * @return 菜单集合
     */
    public Collection<Menu> saveMenus(Long userId, Collection<Menu> menus) {
        var key = String.format(KEY_MENU, userId);
        try {
            redis.opsForValue().set(key, menus);
            log.debug("Save menus(userId = {}, size = {}) into cache", userId, menus.size());
        } catch (Exception e) {
            // log.error("Cannot save menus for user", e);
        }
        return menus;
    }

    /**
     * 读取用户对象
     *
     * @param userId 菜单相关的用户 ID
     * @return 用户对象
     */
    @SuppressWarnings("unchecked")
    public Optional<Collection<Menu>> loadMenus(Long userId) {
        var key = String.format(KEY_MENU, userId);
        try {
            var menus = (Collection<Menu>) redis.opsForValue().get(key);
            if (menus == null) {
                log.debug("No expected menus in cache");
                return Optional.empty();
            }
            log.debug("Load menus(userId = {}, size = {}) from cache", userId, menus.size());
            return Optional.of(menus);
        } catch (Exception e) {
            // log.error("Cannot load menus from cache", e);
            return Optional.empty();
        }
    }

    /**
     * 缓存用户权限集合
     *
     * @param userId      用户 id
     * @param authorities 用户权限集合
     * @return 用户权限集合
     */
    public Collection<GrantedAuthority> saveAuthorities(Long userId, Collection<GrantedAuthority> authorities) {
        var key = String.format(KEY_PERMISSION, userId);
        var permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        try {
            redis.opsForValue().set(key, permissions);
            log.debug("Save permissions(userId = {}, size = {}) into cache", userId, permissions.size());
        } catch (Exception e) {
            // log.error("Cannot save permissions into cache", e);
        }
        return authorities;
    }

    /**
     * 读取用户权限
     *
     * @param userId 用户 id
     * @return 用户权限集合
     */
    @SuppressWarnings("unchecked")
    public Optional<Collection<GrantedAuthority>> loadAuthorities(Long userId) {
        var key = String.format(KEY_PERMISSION, userId);

        try {
            var permissions = (Collection<String>) redis.opsForValue().get(key);
            if (permissions == null) {
                log.debug("No expected permissions in cache");
                return Optional.empty();
            }
            log.debug("Load permissions(userId = {}, size = {}) from cache", userId, permissions.size());
            return Optional.of(permissions.stream()
                    .map(p -> (GrantedAuthority) new SimpleGrantedAuthority(p))
                    .toList());
        } catch (Exception e) {
            // log.error("Cannot load permissions from cache", e);
            return Optional.empty();
        }
    }
}
