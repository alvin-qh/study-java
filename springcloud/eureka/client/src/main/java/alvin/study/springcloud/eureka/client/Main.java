package alvin.study.springcloud.eureka.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 应用程序主配置和启动类
 *
 * <p>
 * {@link SpringBootApplication @SpringBootApplication} 注解表示该类型包含为 Spring Boot
 * 应用程序的入口方法, 且所有的自动配置和 Bean 容器均从这个根类型展开
 * </p>
 *
 * <p>
 * {@link EnableFeignClients @EnableEurekaClient} 注解表示当前应用启动时需要扫描具备
 * {@link org.springframework.cloud.openfeign.FeignClient @FeignClient} 类型, 注册
 * OpenFeign 的服务类型
 * </p>
 *
 * <p>
 * 注意, {@link EnableFeignClients @EnableFeignClients} 注解必须位于入口方法类型上, 位于其它配置类无效
 * </p>
 */
@EnableFeignClients(basePackages = "alvin.study.springcloud")
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
