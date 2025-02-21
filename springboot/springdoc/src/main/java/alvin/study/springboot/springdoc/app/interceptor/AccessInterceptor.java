package alvin.study.springboot.springdoc.app.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.springdoc.infra.entity.AccessLog;
import alvin.study.springboot.springdoc.infra.entity.User;
import alvin.study.springboot.springdoc.infra.repository.AccessLogRepository;

/**
 * Spring 拦截器, 拦截请求, 记录访问时间
 *
 * <p>
 * {@link #postHandle(HttpServletRequest, HttpServletResponse, Object, ModelAndView)}
 * 方法属于后置拦截器, 即所有的 Servlet 处理完毕后, 逐一处理后续的逻辑
 * </p>
 *
 * @see HandlerInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
 * @see HandlerInterceptor#postHandle(HttpServletRequest, HttpServletResponse, Object, ModelAndView)
 */
@Component
@RequiredArgsConstructor
public class AccessInterceptor implements HandlerInterceptor {
    // 注入访问日志持久化对象
    private final AccessLogRepository accessLogRepository;

    /**
     * 在请求处理完毕后, 记录访问日志
     */
    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {
        // 获取登录信息
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return;
        }

        // 获取登录用户, 记录访问日志
        var user = (User) auth.getPrincipal();
        try {
            accessLogRepository.insert(AccessLog.forAccess(user.getUsername()));
        } catch (Exception ignore) {}
    }
}
