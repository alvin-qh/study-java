package alvin.study.springboot.autoconf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.autoconf.domain.model.User;
import alvin.study.springboot.autoconf.prop.ConfigProperties;
import alvin.study.springboot.autoconf.util.TimeUtil;

/**
 * 定义自动配置类, 当当前模块被加载后执行自动配置
 * <p>
 * {@link Configuration @Configuration} 是必要注解, 缺失此注解方法中的 {@link Bean @Bean} 注解会被忽略
 * <p>
 * {@link ComponentScan @ComponentScan} 注解定义了扫描指定包下的所有类, 将其添加到定位上下文中
 * <p>
 * {@link EnableConfigurationProperties @EnableConfigurationProperties} 注解定义了用于读取配置文件信息的类型
 * <p>
 * 参见 `spring.factories` 配置文件中 {@code org.springframework.boot.autoconfigure.EnableAutoConfiguration} 的定义
 */
@Configuration
@ComponentScan(basePackages = { "alvin.study.springboot.autoconf.domain" })
@RequiredArgsConstructor
@EnableConfigurationProperties({ ConfigProperties.class })
public class AutoConfiguration {
    // 配置文件信息类
    private final ConfigProperties properties;

    @Bean
    TimeUtil timeUtil() {
        return new TimeUtil(properties.getTimeZone());
    }

    /**
     * {@link ConditionalOnProperty @ConditionalOnProperty} 注解用于检查配置文件内容, 符合要求配置值后注入此方法返回的 Bean 对象
     * <p>
     * 可以修改 `autoconfig.common.use-module-user` 配置项, 查看测试的不同结果
     */
    @Bean
    @ConditionalOnProperty(name = "autoconfig.common.use-module-user", havingValue = "true", matchIfMissing = true)
    User user() {
        return new User(properties.getCommon().getName());
    }
}
