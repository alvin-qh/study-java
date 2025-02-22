package alvin.study.springboot.shiro.core.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.util.WebUtils;

import lombok.RequiredArgsConstructor;

/**
 * 会话信息管理器类型
 */
@RequiredArgsConstructor
public class RedisSessionManager implements SessionManager {
    // 注入 session 存储对象
    private final SessionDAO sessionDAO;

    // 注入 session 工具对象
    private final SessionUtil sessionUtil;

    /**
     * 会话开始, 创建一个 session 对象
     *
     * @param context 会话上下文对象
     * @return 会话对象
     */
    @Override
    public Session start(SessionContext context) {
        // 从上下文对象中获取请求对象
        var request = WebUtils.getHttpRequest(context);

        // 创建 session 对象
        var session = sessionUtil.createSession(request);
        // 存储 session 对象
        sessionDAO.create(session);
        return session;
    }

    /**
     * 获取 session 对象
     *
     * @param key 存储 session 的 key 对象
     * @return session 对象
     */
    @Override
    public Session getSession(SessionKey key) throws SessionException {
        // 获取请求上下文对象
        var request = WebUtils.getHttpRequest(key);

        // 从请求上下文对象中获取 session id
        var sessionId = sessionUtil.getSessionId(request);
        if (sessionId == null) {
            return null;
        }

        // 读取 session 对象
        var session = sessionDAO.readSession(sessionId);
        if (session != null) {
            // 更新 session 对象
            sessionDAO.update(session);
        }
        return session;
    }
}
