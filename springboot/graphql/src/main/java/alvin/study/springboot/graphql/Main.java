package alvin.study.springboot.graphql;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import alvin.study.springboot.graphql.util.collection.Pair;

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
    /**
     * 启动应用程序的主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        var app = new SpringApplication(Main.class);
        app.setDefaultProperties(Map.copyOf(Main.getDefaultProperties()));
        app.run(args);
    }

    /**
     * 获取默认应用属性
     *
     * @param additionalProperties 扩展属性列表
     * @return 包含默认属性的 Map 对象
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

        // 数据库访问设置
        // properties.put("spring.jpa.open-in-view", "false");

        // 追加额外配置
        for (var property : additionalProperties) {
            properties.put(property.getFirst(), property.getSecond());
        }
        return properties;
    }
}
