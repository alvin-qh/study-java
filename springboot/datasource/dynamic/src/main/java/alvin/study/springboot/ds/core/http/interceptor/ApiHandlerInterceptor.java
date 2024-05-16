package alvin.study.springboot.ds.core.http.interceptor;

import alvin.study.springboot.ds.app.domain.service.ConfigNotExistException;
import alvin.study.springboot.ds.app.domain.service.ConfigService;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 对 {@code /api/**} 路径下的所有请求进行拦截, 并在执行 Controller 方法前对请求进行处理
 *
 * <p>
 * 当前类主要是拦截请求, 从请求头中获取 {@code X-Org-Code} 属性, 并将获取的值作为数据源标识
 * </p>
 *
 * <p>
 * {@link #preHandle(HttpServletRequest, HttpServletResponse, Object)}
 * 方法属于前置拦截器, 即在 Servlet 执行前, 对前置逻辑进行处理
 * </p>
 *
 * @see HandlerInterceptor#preHandle(HttpServletRequest, HttpServletResponse,
 * Object)
 * @see HandlerInterceptor#postHandle(HttpServletRequest, HttpServletResponse,
 * Object, org.springframework.web.servlet.ModelAndView)
 * HandlerInterceptor.postHandle(HttpServletRequest, HttpServletResponse,
 * Object, ModelAndView)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiHandlerInterceptor implements HandlerInterceptor {
    public static final String HEADER_ORG = "X-Org-Code";

    /**
     * 注入数据库服务
     */
    private final ConfigService configService;

    /**
     * 对请求进行拦截
     *
     * <p>
     * 从请求头中获取 {@code X-Org-Code} 属性, 从而定义用户要访问的数据库
     * </p>
     */
    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Visiting \"{}\"", request.getRequestURI());

        // 获取 X-Org-Code 请求头, 获取组织代码
        var org = request.getHeader(HEADER_ORG);
        if (Strings.isNullOrEmpty(org)) {
            throw HttpClientErrorException.create(HttpStatus.FORBIDDEN, "org_header_required", null, null, null);
        }
        log.info("Get org=\"{}\" from http header", org);

        try {
            // 根据组织代码获取配置
            var config = configService.findConfig(org);

            // 根据配置记录的数据库名切换数据源上下文
            DataSourceContext.reset(config.getDbName());

            log.info("Set datasource key=\"{}\" with org=\"{}\"", config.getDbName(), org);
        } catch (ConfigNotExistException e) {
            throw HttpClientErrorException.create(HttpStatus.FORBIDDEN, "org_not_exist", null, null, null);
        }

        return true;
    }
}
