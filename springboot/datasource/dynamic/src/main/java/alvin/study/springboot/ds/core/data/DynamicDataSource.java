package alvin.study.springboot.ds.core.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.NonNull;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态数据源类型
 *
 * <p>
 * 动态数据源相当于一个 {@link DataSource} 的代理类, 其根据线程上下文的一个标识字符串, 切换到对应的数据源对象
 * </p>
 */
@Slf4j
public class DynamicDataSource extends AbstractDataSource {
    // 数据源工厂对象
    private final DataSourceFactory dataSourceFactory;

    // 默认数据源对象
    private final DataSource defaultDataSource;

    // 目标数据源集合, key 为数据源标识, Value 为实际的数据源对象
    private final Map<String, DataSource> targetDataSources;

    /**
     * 构造器
     *
     * @param defaultDBName     默认数据库名称
     * @param dataSourceFactory 数据源工厂对象
     */
    public DynamicDataSource(String defaultDBName, DataSourceFactory dataSourceFactory) {
        // 创建连接到默认数据库的数据源
        this.defaultDataSource = dataSourceFactory.build(defaultDBName);

        // 实例化数据源
        this.targetDataSources = new HashMap<>();
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * 获取当前正在生效的数据源标识
     *
     * @return 数据源标识
     */
    private String determineCurrentLookupKey() {
        // 从线程上下文中获取当前的数据源标识
        var key = DataSourceContext.current();
        log.info("Switch to database \"{}\"", key == null ? "default" : key);
        return key;
    }

    /**
     * 获取当前正在生效的数据源
     *
     * @return 数据源对象
     */
    private synchronized DataSource determineTargetDataSource() {
        // 获取当前的数据源标识
        var key = determineCurrentLookupKey();

        // 如果当前无数据源标识, 则切换到默认数据源
        if (Strings.isNullOrEmpty(key)) {
            return defaultDataSource;
        }

        // 根据数据源标识获取要切换的数据源对象, 其中如果对应的数据源不存在, 则创建该数据源
        return targetDataSources.computeIfAbsent(key, dataSourceFactory::build);
    }

    @Override
    public Connection getConnection() throws SQLException { return determineTargetDataSource().getConnection(); }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    /**
     * 获取当前所有的数据源标识 (不包括默认数据源)
     *
     * @return 所有的数据源标识
     */
    public Collection<String> getAllLookupKeys() { return new ArrayList<>(targetDataSources.keySet()); }

    /**
     * 删除数据源
     *
     * @param dbName 数据源标识
     */
    public void delete(String dbName) {
        // 从 targetDataSources 中删除标识符对应的数据源
        var dataSource = targetDataSources.remove(dbName);
        if (dataSource != null) {
            try {
                // 获取数据源的 close 方法并执行
                var method = dataSource.getClass().getMethod("close");
                method.invoke(dataSource);
            } catch (Exception e) {
                log.error("Cannot close datasource " + dataSource, e);
            }
        }
    }
}
