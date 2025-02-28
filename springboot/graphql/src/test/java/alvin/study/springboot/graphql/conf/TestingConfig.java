package alvin.study.springboot.graphql.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import alvin.study.springboot.graphql.core.TestingTransactionManager;

/**
 * 测试环境配置
 *
 * <p>
 * {@link Profile @Profile} 注解表示该配置类型仅在测试中生效
 * </p>
 *
 * <p>
 * {@link TestConfiguration @TestConfiguration} 注解和
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * 功能类似, 但专用于测试
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
