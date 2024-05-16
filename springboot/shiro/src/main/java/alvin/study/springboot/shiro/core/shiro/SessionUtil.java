package alvin.study.springboot.shiro.core.shiro;

import alvin.study.springboot.shiro.util.http.Headers;
import alvin.study.springboot.shiro.util.security.Jwt;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * Session 工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionUtil {
    private static final String ATTR_SESSION_ID = "_ATTR_SESSION_ID";
    private static final String SESSION_ID_TEMPLATE = "user-%s";

    private final Jwt jwt;

    /**
     * 将对象序列化为字符串
     *
     * @param obj 对象, 需要实现 {@link java.io.Serializable Serializable} 接口
     * @return 序列化结果
     */
    public static String objectToString(Object obj) {
        if (obj == null) {
            return "";
        }

        // 创建一个内存输出流
        try (var bo = new ByteArrayOutputStream()) {
            // 创建一个对象输出流
            try (var oo = new ObjectOutputStream(bo)) {
                // 将对象进行序列化
                oo.writeObject(obj);
            }
            bo.flush();

            // 对序列化结果进行 Base64 编码并返回
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        } catch (IOException e) {
            log.error("Cannot deserialize object", e);
            return null;
        }
    }

    /**
     * 将字符串反序列化为对象
     *
     * @param value 字符串值, 该字符串是通过 {@link #objectToString(Object)} 得到
     * @return 原对象
     */
    public static Object stringToObject(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        // 将字符串进行 Base64 解码, 并输入内存流进行读取
        try (var bi = new ByteArrayInputStream(Base64.getDecoder().decode(value))) {
            // 从输入流中读取对象并返回
            try (var oi = new ObjectInputStream(bi)) {
                return oi.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Cannot deserialize object", e);
            return null;
        }
    }

    /**
     * 从请求中获取 JWT 字符串
     *
     * @param request 请求对象
     * @return JWT 字符串
     */
    public String getJwtToken(HttpServletRequest request) {
        // 从 header 中获取指定的值
        var bearer = request.getHeader(Headers.AUTHORIZATION);
        // 对 header 值进行校验
        if (Strings.isNullOrEmpty(bearer) || !bearer.startsWith(Headers.BEARER)) {
            return null;
        }

        // 返回 token 值
        return bearer.substring(Headers.BEARER.length()).trim();
    }

    /**
     * 获取 session 存储的 id 值
     *
     * @param request 请求对象
     * @return session id 值
     */
    public String getSessionId(HttpServletRequest request) {
        // 从请求上下文中尝试获取 session id
        var sessionId = (String) request.getAttribute(ATTR_SESSION_ID);
        if (Strings.isNullOrEmpty(sessionId)) {
            // 如果请求上下文中不存在 session id, 则获取 jwt 并从中解码出 user id
            var token = getJwtToken(request);
            if (!Strings.isNullOrEmpty(token)) {
                var payload = jwt.decode(token);
                // 将 user id 包装为 session id
                sessionId = String.format(SESSION_ID_TEMPLATE, payload.getIssuer());
                // 将 session id 缓存到请求上下文中
                request.setAttribute(ATTR_SESSION_ID, sessionId);
            }
        }
        return sessionId;
    }

    /**
     * 创建 session 对象
     *
     * @param request 请求对象
     * @return session 对象
     */
    public Session createSession(HttpServletRequest request) {
        // 获取 session id
        var sessionId = getSessionId(request);
        if (Strings.isNullOrEmpty(sessionId)) {
            return null;
        }

        // 创建 session 对象并设置 id
        var session = new SimpleSession(request.getRemoteHost());
        session.setId(sessionId);

        return session;
    }
}
