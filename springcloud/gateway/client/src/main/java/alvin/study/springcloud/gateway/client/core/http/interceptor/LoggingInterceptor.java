package alvin.study.springcloud.gateway.client.core.http.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Web MVC 拦截器, 在控制器执行前后对请求进行拦截操作
 *
 * <p>
 * 本拦截器用于在收到请求后输出日志, 需要实现 {@link HandlerInterceptor} 接口, 其中
 * {@link HandlerInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
 * 表示前置拦截器, 即控制器执行前进行拦截;
 * {@link HandlerInterceptor#postHandle(HttpServletRequest, HttpServletResponse, Object, org.springframework.web.servlet.ModelAndView)
 * HandlerInterceptor.postHandle(HttpServletRequest, HttpServletResponse,
 * Object, ModelAndView)} 表示后置拦截器, 即在控制器执行后进行拦截
 * </p>
 *
 * <p>
 * 通过 {@link org.springframework.core.annotation.Order @Order} 注释或者实现
 * {@link Ordered} 接口可以指定过滤器在过滤器链 (Filter Chain) 中的顺序, 数字越小位置越靠前, 越会优先被执行
 * </p>
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor, Ordered {
    /**
     * 返回拦截器在拦截器链上的优先级
     *
     * <p>
     * {@link Ordered#HIGHEST_PRECEDENCE} 表示最高优先级, 即该拦截器最先被执行
     * </p>
     *
     * @return 优先级
     */
    @Override
    public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }

    /**
     * 在控制器执行前进行拦截处理
     *
     * @return {@code true} 表示继续执行后续操作, {@code false} 表示停止处理, 向客户端返回结果
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        recordLog(request);
        return true;
    }

    /**
     * 记录请求日志
     *
     * @param request 当前请求对象
     */
    private void recordLog(HttpServletRequest request) {
        log.info("""
            New request coming:
            Request Path:
              {}

            Headers:
              {}

            Remote Host:
              {}:{}
            """,
            request.getRequestURI(),
            resolveHeaders(request),
            request.getRemoteHost(),
            request.getRemotePort());
    }

    /**
     * 解析 HTTP 请求头包含的内容
     *
     * @param request 当前请求对象
     * @return 解析后信息组成的字符串
     */
    private String resolveHeaders(HttpServletRequest request) {
        var sb = new StringBuilder();

        // 获取请求头所有名称的迭代器
        var headerNames = request.getHeaderNames();

        // 遍历所有请求头名称
        while (headerNames.hasMoreElements()) {
            // 获取一个名称
            var headerName = headerNames.nextElement();

            if (!sb.isEmpty()) {
                sb.append("\n  ");
            }
            // 根据名称获取请求头对应属性, 拼装到字符串中
            sb.append("  ").append(headerName).append(": ").append(request.getHeader(headerName));
        }

        return sb.toString();
    }
}
