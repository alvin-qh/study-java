package alvin.study.se.jdbc.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接管理器类型, 基于线程本地存储管理数据库连接
 */
public class ConnectionManager implements AutoCloseable {
    // 保存数据库连接的线程本地存储对象
    private static final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    /**
     * 初始化
     *
     * @param dataSource 数据源对象
     */
    public void initialize(DataSource dataSource) throws SQLException {
        // 从数据源中获取一个连接对象
        var conn = dataSource.getConnection();

        // 将连接对象存入当前线程的本地存储中
        CONN_LOCAL.set(conn);
    }

    /**
     * 获取数据库连接对象
     *
     * @return 数据库连接对象
     */
    public Connection get() throws SQLException {
        // 从当前线程本地存储中获取数据库连接对象
        var conn = CONN_LOCAL.get();
        if (conn == null) {
            throw new SQLException("No JDBC Connect in current Thread local");
        }
        return conn;
    }

    /**
     * 在当前线程数据库连接上启动事物
     */
    public void beginTransaction() throws SQLException {
        var conn = get();
        if (conn != null) {
            // 启动事物
            conn.setAutoCommit(false);
        }
    }

    /**
     * 在当前线程数据库连接上提交事物
     */
    public void commit() throws SQLException {
        var conn = get();
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * 在当前线程数据库连接上回滚事物
     */
    public void rollback() throws SQLException {
        var conn = get();
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * 关闭当前对象, 释放数据库连接
     */
    @Override
    public void close() throws SQLException {
        // 从本地线程存储中获取到数据库连接对象
        var conn = CONN_LOCAL.get();
        if (conn != null) {
            // 关闭未关闭的连接对象
            if (!conn.isClosed()) {
                conn.close();
            }
            // 清理线程本地存储
            CONN_LOCAL.remove();
        }
    }
}
