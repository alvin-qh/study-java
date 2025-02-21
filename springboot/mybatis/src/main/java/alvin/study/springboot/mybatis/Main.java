package alvin.study.springboot.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import alvin.study.springboot.mybatis.util.collection.Pair;

/**
 * 应用程序主配置和启动类
 *
 * <p>
 * {@link SpringBootApplication @SpringBootApplication} 注解表示该类型包含为 Spring Boot
 * 应用程序的入口方法, 且所有的自动配置和 Bean 容器均从这个根类型展开
 * </p>
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        // 创建 Spring Boot Application 对象
        var app = new SpringApplication(Main.class);
        // 设置默认应用属性
        app.setDefaultProperties(Map.copyOf(getDefaultProperties()));
        // 启动 Application
        app.run(args);
    }

    /**
     * 获取默认应用属性
     *
     * @param additionalProperties 扩展属性列表
     * @return 包含默认属性的 Map 对象
     *
     * @formatter:off
     */
    @SafeVarargs
    public static Map<String, String> getDefaultProperties(Pair<String, String>... additionalProperties) {
        var properties = new HashMap<String, String>();

        // 如下配置用于覆盖 application.yml 中的相关配置

        // Spring Boot 默认配置
        // properties.put("spring.main.banner-mode", "off"); // 不显示欢迎横幅
        // properties.put("spring.main.allow-bean-definition-overriding", "true"); // 允许覆盖 bean 定义

        // hikari 连接池默认配置
        // properties.put("spring.datasource.hikari.pool-name", "cp-study-springboot"); // 数据库连接池名称
        // properties.put("spring.datasource.hikari.auto-commit", "false"); // 仅用事务自动提交

        // flyway db migration 默认配置
        // properties.put("spring.flyway.locations", "classpath:migration"); // 设定 migration 文件存放路径
        // properties.put("spring.flyway.baseline-on-migrate", "true");
        // properties.put("spring.flyway.table", "schema_version"); // 设定存储 migration 信息的表名称

        // 追加额外配置
        for (var property : additionalProperties) {
            properties.put(property.getFirst(), property.getSecond());
        }

        return properties;
    }
}
