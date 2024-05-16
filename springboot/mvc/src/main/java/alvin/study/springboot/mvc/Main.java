package alvin.study.springboot.mvc;

import alvin.study.springboot.mvc.util.collection.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

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

        // 简单方式: 启动 Spring Boot 服务
        // SpringApplication.run(Main.class, args);

        // 进阶方式: 通过 SpringApplication 对象设置应用配置项
        // 创建 Spring Boot 应用对象
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
     */
    @SafeVarargs
    public static Map<String, String> getDefaultProperties(Pair<String, String>... additionalProperties) {
        var properties = new HashMap<String, String>();

        // Spring Boot 默认配置
        properties.put("spring.main.banner-mode", "off"); // 不显示欢迎横幅
        properties.put("spring.main.allow-bean-definition-overriding", "true"); // 允许覆盖 bean 定义

        // 追加额外配置
        for (var property : additionalProperties) {
            properties.put(property.getFirst(), property.getSecond());
        }

        return properties;
    }
}
