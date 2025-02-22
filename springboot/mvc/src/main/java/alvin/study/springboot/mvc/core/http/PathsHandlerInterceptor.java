package alvin.study.springboot.mvc.core.http;

import org.springframework.web.servlet.HandlerInterceptor;

import alvin.study.springboot.mvc.conf.WebConfig;

/**
 * 拦截器接口
 *
 * <p>
 * 拦截器是一种针对于 Controller 的 AOP 对象, 在 Controller 方法执行前后进行预处理和后续处理
 * </p>
 *
 * <p>
 * 拦截器对象通过三个拦截方法完成上述目的:
 * <ul>
 * <li>
 * {@link HandlerInterceptor#preHandle(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, Object)
 * HandlerInterceptor.preHandle(HttpServletRequest, HttpServletResponse,
 * Object)}, 在 Controller 方法执行前进行处理
 * </li>
 * <li>
 * {@link HandlerInterceptor#postHandle(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, Object, org.springframework.web.servlet.ModelAndView)
 * HandlerInterceptor.postHandle(HttpServletRequest, HttpServletResponse,
 * Object, ModelAndView)}, 在 Controller 方法执行后进行处理
 * </li>
 * <li>
 * {@link HandlerInterceptor#afterCompletion(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, Object, Exception)
 * HandlerInterceptor.afterCompletion(HttpServletRequest, HttpServletResponse,
 * Object, Exception)}, 在整个请求处理完成后进行处理
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 本接口从 {@link HandlerInterceptor HandlerInterceptor} 接口继承, 为拦截器增加了 Path 的处理模式,
 * 包括:
 * {@link PathsHandlerInterceptor#getPathPatterns()} 和
 * {@link PathsHandlerInterceptor#getExcludePathPatterns()}, 已告知拦截器注册程序该拦截器的作用范围
 * </p>
 *
 * <p>
 * 当前接口的所有实现类, 需要添加 {@link org.springframework.stereotype.Component @Component}
 * 注解, 以便自动注入到 {@code WebConfig.interceptors} 字段中, 并通过
 * {@link WebConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 * WebConfig.addInterceptors(InterceptorRegistry)} 方法中进行注册
 * </p>
 */
public interface PathsHandlerInterceptor extends HandlerInterceptor {
    /**
     * 获取允许当前拦截器生效的 url 路径模式
     *
     * <p>
     * 一个拦截器通过
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistry#addInterceptor(HandlerInterceptor)
     * InterceptorRegistry.addInterceptor(HandlerInterceptor)} 注册完后, 会返回一个
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistration
     * InterceptorRegistration} 类型的对象, 通过该对象可以设置注册拦截器的生效范围. 参考:
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistration#addPathPatterns(String...)
     * InterceptorRegistration.addPathPatterns(String...)} 方法
     * </p>
     */
    default String[] getPathPatterns() { return new String[] { "/**" }; }

    /**
     * 获取不允许当前拦截器生效的 url 路径模式
     *
     * <p>
     * 一个拦截器通过
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistry#addInterceptor(HandlerInterceptor)
     * InterceptorRegistry.addInterceptor(HandlerInterceptor)} 注册完后, 会返回一个
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistration
     * InterceptorRegistration} 类型的对象, 通过该对象可以设置注册拦截器的无效的范围. 参考:
     * {@link org.springframework.web.servlet.config.annotation.InterceptorRegistration#excludePathPatterns(String...)
     * InterceptorRegistration.excludePathPatterns(String...)} 方法
     * </p>
     */
    default String[] getExcludePathPatterns() { return new String[] {}; }
}
