package alvin.study.springboot.jooq.conf;

import java.util.Map;

import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import alvin.study.springboot.jooq.Main;
import lombok.SneakyThrows;

/**
 * 初始化测试上下文
 *
 * <p>
 * 该类型在测试执行前执行一次, 用于对测试上下文进行额外的设置 (例如设置 {@code classpath:application.yml}
 * 中未设置的配置项)
 * </p>
 *
 * <p>
 * {@link ApplicationContextInitializer} 接口用于对 Spring Boot 应用程序进行初始化, 并传递
 * {@link ConfigurableApplicationContext} 上下文对象用于进行额外设置
 * </p>
 */
public class TestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 对所给的 {@link ConfigurableApplicationContext} 对象进行初始化操作
     */
    @Override
    @SneakyThrows
    public void initialize(ConfigurableApplicationContext context) {
        var properties = Main.getDefaultProperties();

        // 配置多进程下测试数据库连接
        // properties.putAll(setupTestDataSource(context.getEnvironment()));

        // 将配置项设置到 Spring Boot 上下文中
        TestPropertyValues.of(Map.copyOf(properties)).applyTo(context);

        // 关闭 Jooq 的 Banner
        System.setProperty("org.jooq.no-logo", "true");

        // 将 Jooq 的日志级别定义为 DEBUG, 可以看到输出的 SQL 语句
        // 也可以在 application.yml 中通过 logging.level 进行设置
        var lc = (Log4jLoggerFactory) LoggerFactory.getILoggerFactory();
        lc.getLogger("org.jooq.tools.LoggerListener").atLevel(Level.DEBUG);
    }
}
