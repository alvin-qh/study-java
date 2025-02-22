package alvin.study.springboot.autoconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import alvin.study.springboot.autoconf.domain.model.User;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        SpringApplication.run(Main.class, args);
    }

    /**
     * {@link ConditionalOnMissingBean @ConditionalOnMissingBean} 检查定位上下文中是否存在指定的 Bean 对象,
     * 如果不存在, 则会创建新的 Bean 对象
     * <p>
     * 本例中, 通过修改 `autoconfig.common.use-module-user` 配置项可以选择子模块中的 {@link User} 对象是否注入,
     * 如果子模块未注入 {@link User} 对象, 则由本方法注入默认的 {@link User} 对象
     */
    @Bean
    @ConditionalOnMissingBean(User.class)
    public User defaultUser() {
        return new User("Alvin");
    }
}
