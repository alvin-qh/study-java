package alvin.study.springboot.ds.conf;

import alvin.study.springboot.ds.core.data.DataSourceFactory;
import alvin.study.springboot.ds.core.data.DynamicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 配置数据源
 *
 * <p>
 * {@link EnableTransactionManagement @EnableTransactionManagement}
 * 注解表示启动默认的事务管理器
 * </p>
 */
@Configuration("conf/datasource")
@EnableTransactionManagement
public class DataSourceConfig {
    /**
     * 创建动态数据源对象
     *
     * <p>
     * 动态数据源 {@link DynamicDataSource} 类型是一个能够根据线程上下文中存储的数据库名称动态进行数据源切换的特殊数据源类型
     * </p>
     *
     * <p>
     * 动态数据源中的实际数据源对象是通过 {@link DataSourceFactory} 对象创建的
     * </p>
     *
     * <p>
     * 本例中有两类数据源, 默认数据源存储配置信息, 即不同组织代码所要访问的目标数据库, 该数据库共有一个; 业务数据库是根据组织代码对应的数据库,
     * 该数据库有多个
     * </p>
     *
     * @param defaultDbName     默认的数据库名称
     * @param dataSourceFactory 数据源工厂对象
     * @return 动态数据源对象
     */
    @Bean
    @Primary
    DataSource dynamicDataSource(
        @Value("${spring.datasource-template.default-db-name}") String defaultDbName,
        DataSourceFactory dataSourceFactory) {
        return new DynamicDataSource(defaultDbName, dataSourceFactory);
    }
}
