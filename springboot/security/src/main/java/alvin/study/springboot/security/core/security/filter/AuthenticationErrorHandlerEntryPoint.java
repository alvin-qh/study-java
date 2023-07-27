package alvin.study.springboot.security.core.security.filter;

import alvin.study.springboot.security.conf.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 对 {@link AuthenticationException} 异常进行处理
 *
 * <p>
 * 在
 * {@link SecurityConfig#filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity)
 * SecurityConfig.filterChain(HttpSecurity)} 方法中进行注册
 * </p>
 */
@Slf4j
public class AuthenticationErrorHandlerEntryPoint implements AuthenticationEntryPoint {
    /**
     * 处理 {@link AuthenticationException} 异常
     *
     * <p>
     * 本例中使用了简单的方法返回响应内容, 生产环境中应该返回统一的 JSON 格式
     * </p>
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        {@link AuthenticationException} 异常对象
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        log.error("Authentication error caused", e);

        // 向客户端发送错误信息
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
}
