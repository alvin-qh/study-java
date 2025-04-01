package alvin.study.springboot.graphql.bootstrap;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import io.micrometer.context.ContextRegistry;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.core.context.ContextAccessor;

/**
 * 应用处理启动监听器
 *
 * <p>
 * 在应用启动后进行一些对象的初始化操作
 * </p>
 *
 * @see ApplicationListener
 * @see ApplicationStartedEvent
 */
@Slf4j
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {
    private final String timezone;
    private final ObjectMapper objectMapper;
    private final ContextRegistry contextRegistry;

    /**
     * 构造器, 注入所需的值
     */
    public ApplicationStartedEventListener(
            @Value("${application.zone}") String timezone,
            ObjectMapper objectMapper,
            ContextRegistry contextRegistry) {
        this.timezone = Strings.isNullOrEmpty(timezone) ? "UTC" : timezone;
        this.objectMapper = objectMapper;
        this.contextRegistry = contextRegistry;
    }

    /**
     * 应用程序启动事件处理
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        // 设置上下文环境的时区变量
        TimeZone.setDefault(TimeZone.getTimeZone(this.timezone));
        System.setProperty("user.timezone", this.timezone);

        // 为类型转换器设置 Jackson 对象
        JacksonTypeHandler.setObjectMapper(objectMapper);

        // 注册上下文环境变量
        contextRegistry.registerThreadLocalAccessor(new ContextAccessor());

        log.info("Application was started, timezone={}", this.timezone);
    }
}
