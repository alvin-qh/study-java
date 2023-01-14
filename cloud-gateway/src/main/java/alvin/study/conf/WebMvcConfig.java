package alvin.study.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayClassPathWarningAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import alvin.study.core.http.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;

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
 *
 * <p>
 * 通过 {@link Profile @Profile} 注解表明, 当前配置类只有当
 * {@code spring.profiles.active=web-server} 时生效, 即当前工程以 WEB 应用模式运行时生效
 * </p>
 */
@Profile({ "web-server", "test" })
@Configuration("conf/web")
@EnableAutoConfiguration(exclude = {
    GatewayAutoConfiguration.class,
    GatewayClassPathWarningAutoConfiguration.class })
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
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加日志拦截器
        registry.addInterceptor(loggingInterceptor)
                // 设置拦截器要拦截的访问路径
                .addPathPatterns("/**")
                // 设置拦截器不拦截的访问路径
                .excludePathPatterns("/static/**");
    }
}
