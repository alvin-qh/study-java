package alvin.study.jdbc.datasource;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSourceBuilder {
    private String url;
    private String username;
    private String password;

    private DataSourceBuilder() {}

    public DataSourceBuilder url(String url) {
        this.url = url;
        return this;
    }

    public DataSourceBuilder username(String username) {
        this.username = username;
        return this;
    }

    public DataSourceBuilder password(String password) {
        this.password = password;
        return this;
    }

    public DataSource build() {
        var conf = new HikariConfig();
        conf.setPoolName("jdbc-hikari-pool");
        conf.setMaximumPoolSize(50);
        conf.setConnectionTimeout(30000);
        conf.setIdleTimeout(30000);
        conf.setMaxLifetime(590000);
        conf.setMinimumIdle(1);
        conf.setConnectionTestQuery("select 1");

        conf.setJdbcUrl(url);
        conf.setUsername(username);
        conf.setPassword(password);

        return new HikariDataSource(conf);
    }

    public static DataSourceBuilder newBuilder() {
        return new DataSourceBuilder();
    }
}
