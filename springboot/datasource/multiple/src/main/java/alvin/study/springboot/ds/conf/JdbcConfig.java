package alvin.study.springboot.ds.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 配置 JDBC Template 对象
 *
 * <p>
 * 由于本例中配置了多个数据源 Bean, 所以需要明确指定 {@link JdbcTemplate} 对象使用的数据源为
 * {@code dynamicDataSource} 这个数据源
 * </p>
 */
@Configuration("core/jdbc")
public class JdbcConfig {
    /**
     * 创建 {@link JdbcTemplate} 对象并设置数据源
     *
     * @param dataSource 名为 {@code dynamicDataSource} 的数据源对象
     * @return {@link JdbcTemplate} 对象
     */
    @Bean
    JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
