package alvin.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用程序主配置和启动类
 *
 * <p>
 * {@link SpringBootApplication @SpringBootApplication} 注解表示该类型包含为 Spring Boot
 * 应用程序的入口方法, 且所有的自动配置和 Bean 容器均从这个根类型展开
 * </p>
 *
 * <p>
 * 通过 {@code http://localhost:8080/swagger-ui/index.html} 访问文档地址
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
        SpringApplication.run(Main.class, args);
    }
}
