package alvin.study.se.jdbc;

import alvin.study.se.jdbc.datasource.ConnectionManager;
import alvin.study.se.jdbc.datasource.DataSourceBuilder;
import alvin.study.se.jdbc.flyway.Migration;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * JDBC 测试超类类型
 */
public abstract class JDBCBaseTest {
    // 全局数据源对象
    private static DataSource dataSource;

    // 数据库连接管理对象
    private final ConnectionManager connectionManager = new ConnectionManager();

    /**
     * 在所有测试执行前执行, 初始化数据源并合并创建数据表
     */
    @BeforeAll
    static void beforeAll() {
        // 创建数据源对象
        dataSource = DataSourceBuilder.newBuilder()
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;NON_KEYWORDS=USER")
                .username("dev")
                .password("password")
                .build();

        // 通过脚本创建数据表
        new Migration(dataSource).migrate();

    }

    /**
     * 在所有测试执行后执行, 关闭数据源
     */
    @AfterAll
    static void afterAll() {
        if (dataSource instanceof HikariDataSource ds && !ds.isClosed()) {
            ds.close();
        }
    }

    /**
     * 在每次测试执行前执行, 初始化数据库连接, 并准备数据表
     */
    @BeforeEach
    protected void beforeEach() throws SQLException {
        // 初始化连接管理器对象
        connectionManager.initialize(dataSource);
    }

    /**
     * 在每次测试执行后执行, 关闭数据库连接
     */
    @AfterEach
    protected void afterEach() throws SQLException {
        connectionManager.close();
    }

    public ConnectionManager getConnectionManager() { return connectionManager; }
}
