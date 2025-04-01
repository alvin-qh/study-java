package alvin.study.springboot.mvc.app.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.mvc.core.context.Context;
import alvin.study.springboot.mvc.core.http.PathsHandlerInterceptor;
import alvin.study.springboot.mvc.util.security.Jwt;

/**
 * 对 {@code /api/**} 路径下的所有请求进行拦截, 并在执行 Controller 方法前对请求进行处理
 *
 * <p>
 * 当前类注意是拦截请求, 从请求头中获取 {@code Authorization} 属性, 并将获取的值存储的当前请求的上下文 ({@link Context}) 对象中
 * </p>
 *
 * <p>
 * 所有的 {@link PathsHandlerInterceptor} 接口实例都会注入到 {@code WebConfig.interceptors} 字段中
 * </p>
 *
 * <p>
 * {@link #preHandle(HttpServletRequest, HttpServletResponse, Object)} 方法属于前置拦截器, 即在 Servlet 执行前,
 * 对前置逻辑进行处理
 * </p>
 *
 * @see PathsHandlerInterceptor#preHandle(HttpServletRequest,
 *      HttpServletResponse, Object)
 * @see PathsHandlerInterceptor#postHandle(HttpServletRequest,
 *      HttpServletResponse, Object,
 *      org.springframework.web.servlet.ModelAndView)
 *      PathsHandlerInterceptor.postHandle(HttpServletRequest,
 *      HttpServletResponse, Object, ModelAndView)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiHandlerInterceptor implements PathsHandlerInterceptor {
    private static final String HEADER_AUTH = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";

    // 注入上下文对象
    private final Context context;

    // 注入 Jwt 对象
    private final Jwt jwt;

    /**
     * 指定该拦截器起作用的路径范围
     *
     * <p>
     * 返回值表示当前拦截器只对 {@code /api/**} 路径下的请求有效
     * </p>
     */
    @Override
    public String[] getPathPatterns() { return new String[] { "/api/**" }; }

    /**
     * 对请求进行拦截
     *
     * <p>
     * 从请求头中获取 {@code Authorization} 属性, 对属性值校验后存入 {@link Context} 上下文对象中
     * </p>
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(HttpServletRequest,
     *      HttpServletResponse, Object)
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        log.info("Visiting \"{}\"", request.getRequestURI());

        // 从 http 请求头中获取用户 ID
        var auth = request.getHeader(HEADER_AUTH);
        if (!Strings.isNullOrEmpty(auth)) {
            if (!auth.startsWith(TOKEN_PREFIX)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid_bearer_token");
            }

            var token = auth.substring(TOKEN_PREFIX.length()).trim();
            try {
                // 解析 token, 获取 token 负载
                var payload = jwt.verify(token);

                // 将获取的负载信息存入请求上下文对象
                context.set(Context.KEY_ORG_CODE, payload.getAudience().get(0));
                context.set(Context.KEY_USER_ID, Long.parseLong(payload.getIssuer()));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid_bearer_token");
            }
        }
        return true;
    }
}
