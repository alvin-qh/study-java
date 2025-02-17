package alvin.study.springboot.ds.core.flyway;

import alvin.study.springboot.ds.core.data.DataSourceContext;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 手动进行数据库 migrate 操作
 *
 * <p>
 * 需要关闭 flyway 的自动 migration, 参考 {@code classpath:application.yml} 中的
 * {@code spring.flyway.enabled = false} 配置
 * </p>
 */
@Component
@RequiredArgsConstructor
public class Migration {
    private final DataSource dataSource;

    /**
     * 对指定的数据库执行 migrate 操作
     *
     * @param dbName 要执行 migrate 操作的数据库名
     */
    public void migrateBusinessDB(String dbName) {
        try (var ignore = DataSourceContext.switchTo(dbName)) {
            Flyway.configure()
                    .dataSource(dataSource)
                    .baselineOnMigrate(true)
                    .locations("classpath:/migration/business")
                    .table("schema_version")
                    .load()
                    .migrate();
        }
    }

    /**
     * 对默认数据源对应的库进行 migrate 操作
     */
    public void migrateCommonDB() {
        try (var ignore = DataSourceContext.switchToDefault()) {
            Flyway.configure()
                    .dataSource(dataSource)
                    .baselineOnMigrate(true)
                    .locations("classpath:/migration/common")
                    .table("schema_version")
                    .load()
                    .migrate();
        }
    }
}
