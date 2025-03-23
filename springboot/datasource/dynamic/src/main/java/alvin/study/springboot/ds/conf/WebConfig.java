package alvin.study.springboot.ds.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import alvin.study.springboot.ds.core.http.interceptor.ApiHandlerInterceptor;

@Configuration("core/web")
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ApiHandlerInterceptor apiHandlerInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(apiHandlerInterceptor)
                // 添加拦截器要拦截的 url 范围
                .addPathPatterns("/api/**")
                // 添加拦截器要排除的 url 范围
                .excludePathPatterns("/static/**");
    }
}
