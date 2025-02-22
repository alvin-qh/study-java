package alvin.study.springboot.shiro.core.shiro;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.shiro.app.domain.service.SessionService;
import io.lettuce.core.RedisException;

/**
 * session 存储类型
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSessionDAO implements SessionDAO {
    // 缓存 session 的 key 模板
    private static final String KEY_PREFIX = "shiro:session:%s";

    // 操作 redis 的类型
    private final RedisTemplate<String, Object> redis;

    // session 的有效时间
    private final Duration period;

    // 在数据库中进行 session 存储的服务类
    private final SessionService sessionService;

    /**
     * 根据 session 的原始 key 创建缓存存储 key 值
     *
     * @param key session 的原始 key 值
     * @return 缓存存储 key
     */
    private static String makeKey(Object key) {
        return String.format(KEY_PREFIX, key);
    }

    /**
     * 更新 session
     *
     * @param session session 对象
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            return;
        }

        // 查看 session 是否有效
        if (session instanceof ValidatingSession vSession && !vSession.isValid()) {
            try {
                // 对于无效的 session 进行删除
                delete(session);

                // 异步同步数据库中的 session
                sessionService.persistSession((String) session.getId(), this, period);
            } catch (DataAccessException | RedisException e) {
                log.error("Cannot delete session from redis, delete it from database");
                // 如果缓存操作失败, 则直接在数据库中删除 session
                sessionService.deleteSession((String) session.getId());
            }
            return;
        }
        try {
            // 更新缓存中的 session
            redis.opsForValue().set(
                makeKey(session.getId()),
                SessionUtil.objectToString(session),
                period);

            // 将缓存内容同步到数据库
            sessionService.persistSession((String) session.getId(), this, period);
        } catch (DataAccessException | RedisException e) {
            log.error("Cannot update session to redis, update it to database");
            // 如果缓存操作失败, 则直接更新数据库数据
            sessionService.updateSession((String) session.getId(), session, period);
        }
    }

    /**
     * 删除 session
     *
     * @param session session 对象
     */
    @Override
    public void delete(Session session) {
        if (session == null) {
            return;
        }

        try {
            // 从缓存中删除 session
            redis.delete(makeKey(session.getId()));

            // 将缓存内容同步到数据库
            sessionService.persistSession((String) session.getId(), this, period);
        } catch (DataAccessException | RedisException e) {
            log.error("Cannot delete session from redis, delete it from database");
            // 如果缓存操作失败, 则直接删除数据库数据
            sessionService.deleteSession((String) session.getId());
        }
    }

    /**
     * 获取活跃的 session 集合
     *
     * @return 活跃的 session 集合
     */
    @Override
    public Collection<Session> getActiveSessions() {
        List<Object> values;
        try {
            // 获取缓存 key 集合
            var keys = redis.keys(makeKey("*"));
            if (keys == null || keys.isEmpty()) {
                return List.of();
            }

            // 获取所有的缓存值
            values = redis.opsForValue().multiGet(keys);
            if (values == null) {
                values = List.of();
            }
        } catch (DataAccessException | RedisException e) {
            log.error("Cannot load sessions from redis, load them from database");
            values = sessionService.loadAllSessions().stream()
                    .map(s -> (Object) s.getValue()).toList();
        }

        // 将缓存值转为 session 对象
        return values.stream()
                .map(v -> (Session) SessionUtil.stringToObject((String) v))
                .toList();
    }

    /**
     * 创建一个 session
     *
     * @param session 要创建的 session 对象
     * @return session id
     */
    @Override
    public Serializable create(Session session) {
        if (session == null) {
            return null;
        }

        try {
            // 在缓存中创建 session 存储
            redis.opsForValue()
                    .set(
                        makeKey(session.getId()),
                        Objects.requireNonNull(SessionUtil.objectToString(session)),
                        period);

            // 将缓存内容同步到数据库
            sessionService.persistSession((String) session.getId(), this, period);
        } catch (DataAccessException | RedisException e) {
            log.error("Cannot create sessions into redis, create it into database");
            // 如果缓存操作失败, 则直接创建数据库数据
            sessionService.createSession((String) session.getId(), session, period);
        }
        return session.getId();
    }

    /**
     * 读取 session 值
     *
     * @param sessionId 要获取 session 的 id 值
     * @return session 对象
     */
    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        if (sessionId == null) {
            return null;
        }
        String sessionValue = null;
        try {
            // 从缓存中获取 session 值
            sessionValue = (String) redis.opsForValue().get(makeKey(sessionId));
        } catch (DataAccessException | RedisException e) {
            log.error("Cannot read sessions from redis, read it from database");

            // 如果缓存读取失败, 则直接从数据库中读取 session 值
            var session = sessionService.loadSession((String) sessionId);
            if (session != null) {
                sessionValue = session.getValue();
            }
        }

        // 将 session 值反序列化为对象返回
        return (Session) SessionUtil.stringToObject(sessionValue);
    }
}
