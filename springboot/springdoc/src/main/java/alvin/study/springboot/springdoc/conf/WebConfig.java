package alvin.study.springboot.springdoc.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.springdoc.app.interceptor.AccessInterceptor;

/**
 * 配置和 Web 访问相关的配置
 *
 * <p>
 * {@link WebMvcConfigurer#addInterceptors(InterceptorRegistry)} 用于注册拦截器
 * </p>
 */
@Configuration("conf/web")
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AccessInterceptor accessInterceptor;

    /**
     * 为所有访问 {@code /api/**} 的请求添加拦截器
     *
     * @param registry Spring 拦截器注册对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry
                .addInterceptor(accessInterceptor)
                // 添加拦截器要拦截的 url 范围
                .addPathPatterns("/api/**");
    }
}
