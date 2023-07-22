package alvin.study.testing.conf;

import alvin.study.Main;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

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
@Slf4j
public class TestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 对所给的 {@link ConfigurableApplicationContext} 对象进行初始化操作
     */
    @Override
    @SneakyThrows
    public void initialize(@NotNull ConfigurableApplicationContext context) {
        var properties = Main.getDefaultProperties();

        // 配置多进程下测试数据库连接
        // properties.putAll(setupTestDataSource(context.getEnvironment()));

        // 将配置项设置到 Spring Boot 上下文中
        TestPropertyValues.of(Map.copyOf(properties)).applyTo(context);

        // 关闭 Jooq 的 Banner
        System.setProperty("org.jooq.no-logo", "true");

        // 将 Jooq 的日志级别定义为 DEBUG, 可以看到输出的 SQL 语句
        // 也可以在 application.yml 中通过 logging.level 进行设置
        var lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.getLogger("org.jooq.tools.LoggerListener").setLevel(Level.DEBUG);
    }

    /**
     * 配置多进程下数据库连接
     *
     * @param env Spring Boot 配置内容
     * @return 数据库连接配置 key/value
     */
    @SuppressWarnings("unused")
    private Map<String, String> setupTestDataSource(ConfigurableEnvironment env) {
        // 获取 worker 名称, 每个 worker 即一个测试进程
        var worker = System.getProperty("org.gradle.test.worker");
        if (Strings.isNullOrEmpty(worker)) {
            // 若没有 org.gradle.test.worker 变量, 表示测试为单进程, 返回空表示使用默认的数据库连接
            return Map.of();
        }

        // 获取进程数
        var cpuCount = Runtime.getRuntime().availableProcessors();

        // 获取当前 worker 编号
        var executionNo = (Integer.parseInt(worker) % cpuCount) + 1;
        log.info("Work number is: {}", (Integer.parseInt(worker) % cpuCount) + 1);

        // 获取 jdbc 连接 url
        var jdbcUrl = env.getProperty("spring.datasource.url");
        var splitter = jdbcUrl.indexOf(";");

        // 在数据库名称后追加 worker 编号, 以此保证每个测试进程均使用不同的数据库实例
        jdbcUrl = String.format(
            "%s_%s;%s",
            jdbcUrl.substring(0, splitter),
            executionNo,
            jdbcUrl.substring(splitter + 1));
        log.info("Make connection url is: {}", jdbcUrl);

        // 返回数据库连接配置
        return Map.of("spring.datasource.url", jdbcUrl);
    }
}
