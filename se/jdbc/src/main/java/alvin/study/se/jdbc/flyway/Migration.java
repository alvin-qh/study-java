package alvin.study.se.jdbc.flyway;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * 手动进行数据库 migrate 操作
 */
@RequiredArgsConstructor
public class Migration {
    private final DataSource dataSource;

    /**
     * 创建数据库
     */
    public void migrate() {
        Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .locations("classpath:migration")
            .table("schema_version")
            .load()
            .migrate();
    }
}
