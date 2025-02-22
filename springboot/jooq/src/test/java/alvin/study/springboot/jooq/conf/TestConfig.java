package alvin.study.springboot.jooq.conf;

import org.jooq.DSLContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import alvin.study.springboot.jooq.core.TableCleaner;

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
public class TestConfig {
    /**
     * 配置数据表清理器, 清理相关的数据表内容
     *
     * @param dsl             {@link DSLContext} 对象
     * @param dbConnectionUrl 数据库连接 URL
     * @return {@link TableCleaner} 对象, 用于清理数据表
     */
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    TableCleaner tableCleaner(DSLContext dsl, @Value("${spring.datasource.url}") String dbConnectionUrl) {
        return new TableCleaner(dsl, dbConnectionUrl);
    }
}
