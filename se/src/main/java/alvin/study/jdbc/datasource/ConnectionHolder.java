package alvin.study.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class ConnectionHolder implements AutoCloseable {
    private static final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    public void initialize(DataSource dataSource) throws SQLException {
        CONN_LOCAL.set(dataSource.getConnection());
    }

    public Connection get() {
        return CONN_LOCAL.get();
    }

    public void beginTransaction() throws SQLException {
        var conn = get();
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }

    public void commit() throws SQLException {
        var conn = get();
        if (conn != null) {
            conn.commit();
        }
    }

    public void rollback() throws SQLException {
        var conn = get();
        if (conn != null) {
            conn.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        var conn = CONN_LOCAL.get();
        if (conn != null) {
            if (!conn.isClosed()) {
                conn.close();
            }
            CONN_LOCAL.remove();
        }
    }
}
