package alvin.study.springboot.jpa.conf;

import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.jpa.Main;

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
public class TestingContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 对所给的 {@link ConfigurableApplicationContext} 对象进行初始化操作
     */
    @Override
    @SneakyThrows
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        // 获取 Main 类中定义的额外的 Spring 配置项
        var properties = Main.getDefaultProperties(
        // Pair.of("spring.datasource.hikari.pool-name", "cp-alvin-study-test"),
        // Pair.of("spring.jpa.show-sql", "true")
        );

        // 对 Gradle 多进程测试配置不同的数据源
        // properties.putAll(setupTestDataSource(applicationContext.getEnvironment()));

        // 将配置项放入 ConfigurableApplicationContext 对象中使其生效
        TestPropertyValues.of(Map.copyOf(properties)).applyTo(applicationContext);
    }

    /**
     * 设置数据源
     *
     * <p>
     * 如果使用了 Gradle 的多进程并发测试, 为了防止数据干扰, 需要为每个进程创建一个单独的数据库实例, 该方法的作用就是以进程 id 的模为依据,
     * 为每个进程创建数据库实例, 并将当前测试连接到该数据库实例上
     * </p>
     *
     * @param env 获取当前测试环境的配置项
     * @return 新的配置项
     */
    @SuppressWarnings("unused")
    private Map<String, String> setupTestDataSource(ConfigurableEnvironment env) {
        // 从 Gradle 或 Maven 的环境变量中获取当前的 work 编号
        var worker = System.getProperty("org.gradle.test.worker");
        if (Strings.isNullOrEmpty(worker)) {
            worker = System.getProperty("org.maven.test.worker");
        }

        if (Strings.isNullOrEmpty(worker)) {
            return Map.of();
        }

        log.info("Worker number is: {}", worker);

        // 获取 cpu 数量, 即测试最大的进程数
        var cpuCount = Runtime.getRuntime().availableProcessors();

        // 将 work 编号通过进程数取模, 得到本测试的运行号
        var executionNo = Integer.parseInt(worker) % cpuCount + 1;

        log.info("Execution number is: {}", executionNo);

        // 根据测试运行号, 基于配置文件中的数据库连接, 产生新的数据库连接
        var jdbcUrl = env.getProperty("spring.datasource.url");

        // 返回新的配置项
        return Map.of("spring.datasource.url", makeJDBCUrlByExecutionNo(jdbcUrl, executionNo));
    }

    /**
     * 根据进程数创建数据库连接
     *
     * @param jdbcUrl     原数据库连接
     * @param executionNo 本次测试的运行号
     * @return 结合运行号的新数据库连接
     */
    private String makeJDBCUrlByExecutionNo(String jdbcUrl, int executionNo) {
        // 获取数据库连接的主体部分和参数部分
        var parts = Splitter.on(";")
                .omitEmptyStrings()
                .trimResults()
                .limit(2)
                .splitToList(jdbcUrl);

        var newParts = IntStream.range(0, parts.size()).mapToObj(i -> {
            if (i == 0) {
                // 修改主体部分, 表示一个新的数据库连接
                return String.format("%s_%d", parts.get(0), executionNo);
            }
            return parts.get(i);
        }).toList();

        // 将修改后的本体部分和
        return Joiner.on(";").join(newParts);
    }
}
