package alvin.study.springboot.shiro.app.domain.service;

import alvin.study.springboot.shiro.conf.BeanConfig;
import alvin.study.springboot.shiro.core.shiro.SessionUtil;
import alvin.study.springboot.shiro.infra.entity.Session;
import alvin.study.springboot.shiro.infra.mapper.SessionMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.util.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 对用户 Session 在数据库中进行持久化操作的服务类型
 *
 * <p>
 * 整个流程里, 用户 Session 分两级存储, 在 Redis 中缓存和在数据库中持久化
 * </p>
 *
 * <p>
 * 为不影响业务正常运行, 对 Redis 缓存操作完毕后, 通过异步方式将 Redis 的内容写入数据库, 异步通过 {@link Executor}
 * 来执行, 具体线程池的配置参考 {@link BeanConfig#fixedThreadPoolExecutor()
 * BeanConfig.fixedThreadPoolExecutor()} 方法
 * </p>
 *
 * <p>
 * 如果 Redis 服务失效, 无法写入或读取缓存, 则系统会直接从数据库中读取, 之后在和 Redis 进行同步
 * </p>
 *
 * <p>
 * 要将对象写入数据表, 需要对对象进行序列化操作, 本例中采用的是序列化 + {@code Base64} 的方式, 参考
 * {@link SessionUtil#objectToString(Object)} 和
 * {@link SessionUtil#stringToObject(String)} 方法
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionService {
    // 注入 Session 实体数据库操作对象
    private final SessionMapper sessionMapper;

    // 注入线程池执行对象
    private final Executor executor;

    // 注入事务管理器对象, 进行手动事务处理
    private final PlatformTransactionManager txManager;

    /**
     * 在数据库中持久化一个 Session 对象
     *
     * @param key    Session 的标识符, 对于 Shiro
     *               {@link org.apache.shiro.session.Session Session} 来说, 是其
     *               {@link org.apache.shiro.session.Session#getId()
     *               Session.getId()} 属性
     * @param value  Session 值, 对于 {@link org.apache.shiro.session.Session
     *               Session} 来说, 是其对象本身
     * @param period Session 的超时时间
     */
    @Transactional
    public void createSession(String key, Object value, Duration period) {
        // 根据 key 查询 session 对象
        sessionMapper.selectByKey(key).ifPresentOrElse(
            session -> {
                // 对于 session 已经存在, 则对其进行更新操作
                session.setKey(key);
                session.setValue(SessionUtil.objectToString(value));
                session.setExpiredAt(Instant.now().plusSeconds(period.getSeconds()));

                sessionMapper.updateById(session);
            }, () -> {
                // 对于 session 不存在, 则插入新记录
                var session = new Session();
                session.setKey(key);
                session.setValue(SessionUtil.objectToString(value));
                session.setExpiredAt(Instant.now().plusSeconds(period.getSeconds()));

                sessionMapper.insert(session);
            });
    }

    /**
     * 根据 key 更新 Session
     *
     * @param key    Session 的标识符, 对于 Shiro
     *               {@link org.apache.shiro.session.Session Session} 来说, 是其
     *               {@link org.apache.shiro.session.Session#getId()
     * @param value  Session 值, 对于 {@link org.apache.shiro.session.Session
     *               Session} 来说, 是其对象本身
     * @param period Session 的超时时间
     */
    @Transactional
    public void updateSession(String key, Object value, Duration period) {
        // 查询 session 并对其进行更像
        sessionMapper.selectByKey(key).ifPresent(session -> {
            session.setValue(SessionUtil.objectToString(value));
            session.setExpiredAt(Instant.now().plusSeconds(period.getSeconds()));
        });
    }

    /**
     * 根据 key 读取 session
     *
     * @param key Session 的标识符, 对于 Shiro
     *            {@link org.apache.shiro.session.Session Session} 来说, 是其
     *            {@link org.apache.shiro.session.Session#getId()
     * @return Session 值, 对于 {@link org.apache.shiro.session.Session
     * Session} 来说, 是其对象本身
     */
    @Transactional
    public Session loadSession(String key) {
        // 从数据库中查询 session
        var session = sessionMapper.selectByKey(key).orElse(null);
        // 判断 session 是否过期, 过期则删除该 session
        if (session != null && session.getExpiredAt().isBefore(Instant.now())) {
            sessionMapper.deleteById(session.getId());
            session = null;
        }
        return session;
    }

    /**
     * 获取所有 session
     *
     * @return 所有 session 实体对象集合
     */
    @Transactional(readOnly = true)
    public List<Session> loadAllSessions() {
        return sessionMapper.selectList(null);
    }

    /**
     * 删除指定的 session 实体
     *
     * @param key Session 的标识符, 对于 Shiro
     *            {@link org.apache.shiro.session.Session Session} 来说, 是其
     *            {@link org.apache.shiro.session.Session#getId()
     */
    @Transactional
    public void deleteSession(String key) {
        sessionMapper.delete(Wrappers.lambdaQuery(Session.class).eq(Session::getKey, key));
    }

    /**
     * 异步持久化 session
     *
     * @param key        Session 的标识符, 对于 Shiro
     *                   {@link org.apache.shiro.session.Session Session} 来说, 是其
     *                   {@link org.apache.shiro.session.Session#getId()
     * @param sessionDAO 在缓存中操作 session 的对象
     * @param period     session 超时时间
     */
    public void persistSession(String key, SessionDAO sessionDAO, Duration period) {
        // 在主线程中获取 subject
        var subject = ThreadContext.getSubject();

        // 启动新的线程, 异步写入数据库
        executor.execute(() -> {
            // 将主线程获取的 subject 对象绑定在子线程中
            ThreadContext.bind(subject);

            // 启动事务
            var tx = txManager.getTransaction(null);
            try {
                // 查询指定的 session 是否存在
                sessionMapper.selectByKey(key).ifPresentOrElse(
                    session -> {
                        // 从缓存中获取最新的 session
                        var value = sessionDAO.readSession(key);
                        if (value == null) {
                            // 对于数据库中存在但缓存中不存在的情况, 删除该 session
                            sessionMapper.deleteById(session.getId());
                            log.debug("Flush session into database, session invalid, deleted it");
                        } else {
                            // 对于数据库和缓存都有的情况, 更新此 session
                            session.setValue(SessionUtil.objectToString(value));
                            session.setExpiredAt(Instant.now().plusSeconds(period.getSeconds()));
                            sessionMapper.updateById(session);
                            log.debug("Flush session into database, session valid, updated it");
                        }
                    },
                    () -> {
                        var value = sessionDAO.readSession(key);
                        if (value == null) {
                            // 对于缓存没有, 数据库中也没有的情况, 忽略此次操作
                            log.debug("Flush session into database, session not exist, ignore it");
                        } else {
                            // 对于缓存有, 数据库中没有的情况, 创建 session 记录
                            var session = new Session();
                            session.setKey(key);
                            session.setValue(SessionUtil.objectToString(value));
                            session.setExpiredAt(Instant.now().plusSeconds(period.getSeconds()));
                            sessionMapper.insert(session);
                            log.debug("Flush session into database, session not exist, created it");
                        }
                    });

                // 成功执行, 提交事务
                txManager.commit(tx);
                log.debug("Session(key={}) was flush into database", key);
            } catch (Exception e) {
                // 失败回滚事务
                txManager.rollback(tx);
                log.error("Cannot flush session to database", e);
            } finally {
                // 清除在子线程中绑定的对象
                ThreadContext.remove();
            }
        });
    }
}
