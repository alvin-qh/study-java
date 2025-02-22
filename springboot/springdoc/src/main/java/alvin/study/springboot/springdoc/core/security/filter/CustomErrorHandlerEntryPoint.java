package alvin.study.springboot.springdoc.core.security.filter;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.springdoc.core.http.ResponseWrapper;

/**
 * 对 {@link AuthenticationException} 异常进行处理
 *
 * <p>
 * 在
 * {@code SecurityConfig.filterChain(HttpSecurity)} 方法中进行注册
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomErrorHandlerEntryPoint implements AuthenticationEntryPoint {
    // 注入 JSON 处理对象
    private final ObjectMapper objectMapper;

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
        var error = objectMapper.writeValueAsString(
            ResponseWrapper.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()));

        // 向客户端发送错误信息
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var writer = response.getWriter();
        writer.write(error);
        writer.flush();
    }
}
