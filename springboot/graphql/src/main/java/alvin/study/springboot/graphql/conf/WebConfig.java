package alvin.study.springboot.graphql.conf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import alvin.study.springboot.graphql.core.context.ContextHolder;

@Configuration("conf/web")
public class WebConfig implements WebMvcConfigurer {
    @Value("${application.env}")
    private String env;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void afterCompletion(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull Object handler,
                    @Nullable Exception ex) {
                if (!"test".equals(env)) {
                    ContextHolder.reset();
                }
            }
        });
    }
}
