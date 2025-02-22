package alvin.study.springboot.autoconf.bootstrap;

import java.time.Duration;
import java.util.TimeZone;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.autoconf.prop.ConfigProperties;

/**
 * 应用程序启动监听, 监听整个 Spring 容器的启动过程
 * <p>
 * 参见 `spring.factories` 配置文件中 {@code org.springframework.boot.SpringApplicationRunListener} 的定义
 */
@Slf4j
public class SpringApplicationListener implements SpringApplicationRunListener {
    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        var props = context.getBean(ConfigProperties.class);

        TimeZone.setDefault(TimeZone.getTimeZone(props.getTimeZone()));
        System.setProperty("user.timezone", props.getTimeZone());

        log.info("Application was started, timezone={}", props.getTimeZone());
    }
}
