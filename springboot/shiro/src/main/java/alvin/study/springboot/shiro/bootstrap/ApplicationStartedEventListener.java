package alvin.study.springboot.shiro.bootstrap;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用处理启动监听器
 *
 * <p>
 * 在处理启动事件时, 从 {@code application.yml} 文件中读取时区配置, 设置为系统时区
 * </p>
 *
 * @see ApplicationListener
 * @see ApplicationStartedEvent
 */
@Slf4j
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {
    private final String timezone;

    /**
     * 构造器, 获取配置中定义的时区
     */
    public ApplicationStartedEventListener(@Value("${application.zone}") String timezone) {
        this.timezone = Strings.isNullOrEmpty(timezone) ? "UTC" : timezone;
    }

    /**
     * 应用程序启动事件处理
     *
     * <p>
     * 在处理启动事件时, 从 {@code application.yml} 文件中读取时区配置, 设置为系统时区
     * </p>
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        // 设置上下文环境的时区变量
        TimeZone.setDefault(TimeZone.getTimeZone(this.timezone));
        System.setProperty("user.timezone", this.timezone);

        log.info("Application was started");
    }
}
