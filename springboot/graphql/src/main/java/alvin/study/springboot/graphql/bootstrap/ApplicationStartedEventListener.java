package alvin.study.springboot.graphql.bootstrap;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

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
    // application.yml 中定义的时区信息
    private final String timezone;

    // Jackson 对象
    private final ObjectMapper objectMapper;

    /**
     * 构造器, 注入所需的值
     */
    public ApplicationStartedEventListener(@Value("${application.zone}") String timezone, ObjectMapper objectMapper) {
        this.timezone = Strings.isNullOrEmpty(timezone) ? "UTC" : timezone;
        this.objectMapper = objectMapper;
    }

    /**
     * 应用程序启动事件处理
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // 设置上下文环境的时区变量
        TimeZone.setDefault(TimeZone.getTimeZone(this.timezone));
        System.setProperty("user.timezone", this.timezone);

        // 为类型转换器设置 Jackson 对象
        JacksonTypeHandler.setObjectMapper(objectMapper);

        log.info("Application was started, timezone={}", this.timezone);
    }
}
