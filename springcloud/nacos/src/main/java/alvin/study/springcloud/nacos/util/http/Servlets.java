package alvin.study.springcloud.nacos.util.http;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servlet 相关工具类
 */
public final class Servlets {
    /**
     * 获取 {@link HttpServletRequest} 对象
     *
     * @return 当前请求的 {@link HttpServletRequest} 对象
     */
    public static HttpServletRequest getHttpServletRequest() {
        // 获取请求参数
        var attr = RequestContextHolder.currentRequestAttributes();
        if (!(attr instanceof ServletRequestAttributes reqAttr)) {
            throw new ClassCastException("invalid request type");
        }

        // 获取请求对象
        return reqAttr.getRequest();
    }
}
