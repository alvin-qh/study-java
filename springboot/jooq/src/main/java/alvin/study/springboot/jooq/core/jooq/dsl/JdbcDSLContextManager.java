package alvin.study.springboot.jooq.core.jooq.dsl;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.RecordListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.TransactionalCallable;
import org.jooq.TransactionalRunnable;
import org.jooq.VisitListenerProvider;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jooq.core.jooq.listener.AuditAndTentedRecordListener;
import alvin.study.springboot.jooq.core.jooq.listener.TentedVisitListener;

/**
 * DSLContext 管理器类型
 *
 * <p>
 * 该类型演示了如何通过 {@link java.sql.Connection Connection} 对象创建和管理 {@link DSLContext}
 * 对象. 如果引入了 Spring Boot 框架, 一般情况下无需如此操作 JOOQ, 直接使用
 * {@code spring-boot-starter-jooq} 插件即可直接注入 {@link DSLContext} 对象
 * </p>
 */
@RequiredArgsConstructor
public class JdbcDSLContextManager {
    /**
     * 线程本地存储, 用于在同一个线程的上下文中存储 JDBC 的 {@link Connection} 对象
     */
    private final ThreadLocal<Connection> contextLocal = new ThreadLocal<>();

    /**
     * 注入 JOOQ 数据源管理器对象
     *
     * <p>
     * 通过 {@link DataSourceConnectionProvider#acquire()} 方法可以获取一个数据库连接, 通过
     * {@link DataSourceConnectionProvider#release(Connection)} 方法可以释放一个数据库连接
     * </p>
     *
     * <p>
     * 参考 {@code JooqConfig.connectionProvider(DataSource)} 方法
     * </p>
     */
    private final DataSourceConnectionProvider connectionProvider;

    /**
     * SQL 语句方言对象
     */
    private final SQLDialect sqlDialect;

    /**
     * 记录处理监听器, 用于对处理数据表记录动作进行监听
     */
    private final AuditAndTentedRecordListener auditAndTentedRecordListener;

    /**
     * 数据访问监听器, 用于对 SQL 执行动作进行监听
     */
    private final TentedVisitListener tentedVisitListener;

    /**
     * 获取 JDBC 数据库连接
     *
     * @return 数据库连接
     */
    protected Connection getConnection() throws SQLException {
        // 获取数据库 jdbc 连接对象
        // var conn = DriverManager.getConnection(databaseUrl, username, password);

        // 从连接提供器获取数据库连接
        var conn = connectionProvider.acquire();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * 获取 {@link DSLContext} 对象
     *
     * @return {@link DSLContext} 对象
     */
    public DSLContext get() throws SQLException {
        // 从本地线程存储中获取连接
        var conn = contextLocal.get();
        if (conn == null) {
            // 创建数据库连接并存入本地线程上下文中
            conn = createThreadLocal();
        }
        // return DSL.using(conn, sqlDialect);

        // 创建 JOOQ 配置对象
        var config = new DefaultConfiguration()
                .set(conn)
                .set(sqlDialect)
                .set((RecordListenerProvider) auditAndTentedRecordListener)
                .set((VisitListenerProvider) tentedVisitListener);

        // 创建 DSLContext 对象
        return DSL.using(config);
    }

    /**
     * 创建本地线程存储, 存储数据库连接对象
     *
     * @return 数据库连接对象
     */
    private Connection createThreadLocal() throws SQLException {
        // 如果本地存储不存在, 则创建连接和 DSLContext 对象
        var conn = getConnection();
        // 设置线程本地存储
        contextLocal.set(conn);
        return conn;
    }

    /**
     * 启动事务, 在事务内执行所需代码
     *
     * <p>
     * 参考 {@link DSLContext#transaction(TransactionalRunnable)} 方法, 在传入的回调方法内无需返回值
     * </p>
     *
     * @param runnable 回调接口, 在事务内执行代码
     */
    public void tx(TransactionalRunnable runnable) throws SQLException {
        get().transaction(runnable);
    }

    /**
     * 启动事务, 在事务内执行所需代码, 返回结果
     *
     * <p>
     * 参考 {@link DSLContext#transactionResult(TransactionalCallable)} 方法,
     * 在传入的回调方法内可以返回一个值
     * </p>
     *
     * @param <T>      返回类型
     * @param callable 回调接口, 在事务内执行代码
     * @return 返回值
     */
    public <T> T txr(TransactionalCallable<T> callable) throws SQLException {
        return get().transactionResult(callable);
    }

    /**
     * 清理连接
     */
    public void clear() {
        var conn = contextLocal.get();
        if (conn != null) {
            contextLocal.remove();
            connectionProvider.release(conn);
        }
    }
}
