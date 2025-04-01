package alvin.study.springboot.security.conf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

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
public class TestingContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 对所给的 {@link ConfigurableApplicationContext} 对象进行初始化操作
     */
    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {}
}
