package alvin.study.springboot.shiro.core.shiro;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.shiro.core.handler.GlobalExceptionHandler;

/**
 * 过滤器类
 *
 * <p>
 * 该过滤器用于获取请求头中的 jwt, 产生 {@link JwtAuthenticationToken} 凭证交由后续流程处理
 * </p>
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends BasicHttpAuthenticationFilter {
    // 注入 session 处理工具类
    private final SessionUtil sessionUtil;

    // JSON 处理对象
    private final ObjectMapper objectMapper;

    /**
     * 判断当前请求是否被允许
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 若请求被允许则返回 {@code true}
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        var req = WebUtils.toHttp(request);

        // 对于跨域询问请求, 直接返回允许
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        // 获取 jwt 并进行登录验证
        var token = sessionUtil.getJwtToken(req);
        if (Strings.isNullOrEmpty(token)) {
            return false;
        }

        var subject = getSubject(request, response);
        subject.login(new JwtAuthenticationToken(token));
        return true;
    }

    /**
     * 当请求被拒绝后进行的处理
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 是否进行后续处理, 返回 {@code false} 表示不进行后续处理, 之后的过滤器不被执行, 直接返回响应
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        var resp = WebUtils.toHttp(response);

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(
            new GlobalExceptionHandler.ErrorResponseDto(HttpServletResponse.SC_UNAUTHORIZED, "No access token")));

        return false;
    }
}
