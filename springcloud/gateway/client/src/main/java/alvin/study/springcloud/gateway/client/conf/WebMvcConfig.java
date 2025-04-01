package alvin.study.springcloud.gateway.client.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

import alvin.study.springcloud.gateway.client.core.http.interceptor.LoggingInterceptor;

/**
 * 配置 WEB 应用服务
 *
 * <p>
 * {@link WebMvcConfigurer} 接口提供了 Web MVC 相关的配置项接口, 本例中覆盖其添加拦截器的接口, 参考
 * {@link #addInterceptors(InterceptorRegistry)} 方法
 * </p>
 *
 * <p>
 * 由于本例中将多个框架纳入一个工程内, 所以在执行 {@code web-server} 等 Profile 下代码时, 需要将 Spring Cloud
 * Gateway 的自动配置类排除, 本例中通过
 * {@link EnableAutoConfiguration @EnableAutoConfiguration} 注解排除指定的自动配置类
 * </p>
 */
@Configuration("conf/web")
// 如果依赖中同时包含了 spring-boot-web 和 spring-cloud-gateway, 则配置 spring-boot-web 时需要禁止 spring-cloud-gateway
// 的自动配置, 这只在通过 @Profile 注解将当前项目在不同配置下分别启动为 Web 应用和 Gateway 时适用
// @EnableAutoConfiguration(exclude = {
// GatewayAutoConfiguration.class,
// GatewayClassPathWarningAutoConfiguration.class
// })
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    // 注入拦截器对象
    private final LoggingInterceptor loggingInterceptor;

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册对象
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 添加日志拦截器
        registry.addInterceptor(loggingInterceptor)
                // 设置拦截器要拦截的访问路径
                .addPathPatterns("/**")
                // 设置拦截器不拦截的访问路径
                .excludePathPatterns("/static/**");
    }
}
