package alvin.study.springboot.ds.core.flyway;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * 手动进行数据库 migrate 操作
 *
 * <p>
 * 需要关闭 flyway 的自动 migration, 参考 {@code classpath:application.yml} 中的
 * {@code spring.flyway.enabled = false} 配置
 * </p>
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class Migration {
    // 注入系统中所有的 datasource 对象
    private final List<DataSource> dataSources;

    /**
     * 为每个 datasource 执行 migrate 操作
     */
    public void migrate() {
        for (var ds : dataSources) {
            // 注意, 这里要过滤掉 AbstractRoutingDataSource 这个动态数据源, 因为它不是实际的数据源
            if (!(ds instanceof AbstractRoutingDataSource)) {
                Flyway.configure()
                    .dataSource(ds)
                    .baselineOnMigrate(true)
                    .locations("classpath:/migration")
                    .table("schema_version")
                    .load()
                    .migrate();
            }
        }
    }
}
