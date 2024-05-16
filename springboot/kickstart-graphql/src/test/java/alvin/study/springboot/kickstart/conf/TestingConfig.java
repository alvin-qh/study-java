package alvin.study.springboot.kickstart.conf;

import alvin.study.springboot.kickstart.common.TestingTransactionManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 用于测试的自动配置类
 *
 * <p>
 * {@link Profile @Profile} 注解指定了和当前配置类相关的配置文件, {@code test} 值表示应为
 * {@code application-test.yml} 文件
 * </p>
 */
@Profile("test")
@TestConfiguration("conf/test")
public class TestingConfig {
    /**
     * 创建测试用事务管理器对象
     *
     * <p>
     * 在测试时, 有时候不方便应用
     * {@link org.springframework.transaction.annotation.Transactional @Transactional}
     * 注解, 此时可以使用 {@link TestingTransactionManager} 作为手动事务管理器对象使用
     * </p>
     *
     * @param txManager {@link PlatformTransactionManager} 类型对象, 即 JPA 事务管理器对象
     * @return {@link TestingTransactionManager} 事务管理器对象
     */
    @Bean
    TestingTransactionManager testingTransactionManager(PlatformTransactionManager txManager) {
        return new TestingTransactionManager(txManager);
    }
}
