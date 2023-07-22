package alvin.study.core.security.filter;

import alvin.study.core.security.auth.JwtAuthenticationToken;
import alvin.study.util.http.Headers;
import com.google.common.base.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 拦截器, 对请求中的 JWT token 进行处理
 *
 * <p>
 * 注意, 如果拦截时发现用户已经登录过, 或者请求中未携带 JWT token, 则需要放行 (而不是抛出异常), 否则无需验证的那部分请求也会失败
 * </p>
 *
 * <p>
 * 当前过滤器类型在 {@link alvin.study.conf.SecurityConfig#filterChain(HttpSecurity)
 * SecurityConfig.filterChain(HttpSecurity)} 方法中进行注册
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    /**
     * 进行拦截操作
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链, 用于调用下一个过滤器
     */
    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain chain) throws ServletException, IOException {
        // 判断安全上下文中是否已经存储用户信息
        var context = SecurityContextHolder.getContext();

        // 判断是否未进行验证
        if (context.getAuthentication() == null) {
            // 从 Header 中获取 token
            var token = request.getHeader(Headers.AUTHORIZATION);

            // 如果此次请求不携带 JWT token, 则放行, 否则对 token 进行处理
            if (!Strings.isNullOrEmpty(token) && token.startsWith(Headers.BEARER)) {
                // 创建 Authentication 对象, 存储 JWT 字符串
                var auth = new JwtAuthenticationToken(token.substring(Headers.BEARER.length()).trim());
                // 设置登录详细信息
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 在安全上下文中存储 Authentication 对象
                context.setAuthentication(auth);
            }
        }

        // 通过过滤器链调用下一个过滤器
        chain.doFilter(request, response);
    }
}
