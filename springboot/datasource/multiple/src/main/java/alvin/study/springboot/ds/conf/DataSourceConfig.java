package alvin.study.springboot.ds.conf;

import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DataSourceTarget;
import alvin.study.springboot.ds.core.data.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据源相关配置类
 *
 * <p>
 * 本例中, 根据 {@code classpath:application.yml} 文件中定义的 {@code db1} 和 {@code db2}
 * 配置项, 创建 {@code db1DataSource} 和 {@code db2DataSource} 两个数据源对象
 * </p>
 *
 * <p>
 * {@link DataSourceBuilder#create()} 方法可以自动加载
 * {@link ConfigurationProperties @ConfigurationProperties} 注解指定的配置项, 再通过
 * {@link DataSourceBuilder#build()} 方法创建数据源对象
 * </p>
 *
 * <p>
 * {@link DynamicDataSource} 对象是一个可以动态切换的数据源, 将作为在代码中使用的数据源对象, 并在不同的情况时在内部自动切换
 * {@code db1DataSource} 和 {@code db2DataSource} 两个数据源. 切换依据于当前线程上下文的标识, 参考
 * {@link DataSourceContext} 类型
 * </p>
 *
 * <p>
 * {@link EnableTransactionManagement @EnableTransactionManagement} 表示启动默认的事务管理器
 * </p>
 */
@Configuration("conf/datasource")
@EnableTransactionManagement
public class DataSourceConfig {
    /**
     * 创建第一个数据库的数据源
     *
     * @return 数据源对象
     */
    @Bean("db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    DataSource db1DataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建第二个数据库的数据源
     *
     * @return 数据源对象
     */
    @Bean("db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    DataSource db2DataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建动态切换数据源
     *
     * @return 数据源对象
     */
    @Bean("dynamicDataSource")
    @Primary
    DataSource dynamicDataSource(
        @Qualifier("db1DataSource") DataSource db1DataSource,
        @Qualifier("db2DataSource") DataSource db2DataSource) {
        // 实例化对象
        var ds = new DynamicDataSource();

        // 设置默认的数据源, 即无法获取切换标识时使用的数据源
        ds.setDefaultTargetDataSource(db1DataSource);

        // 设置标识和数据源的关系
        ds.setTargetDataSources(Map.of(
            DataSourceTarget.db1, db1DataSource,
            DataSourceTarget.db2, db2DataSource));

        return ds;
    }
}
