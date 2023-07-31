package alvin.study.springcloud.gateway.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

        SpringApplication.run(Main.class, args);
    }
}
