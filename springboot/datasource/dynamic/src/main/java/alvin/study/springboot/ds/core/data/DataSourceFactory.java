package alvin.study.springboot.ds.core.data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.SneakyThrows;

/**
 * 数据源工厂类
 *
 * <p>
 * 该工厂类创建一个连接到指定数据库的数据源, 如果该数据库不存在, 会创建该数据库并进行数据库的初始化操作
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource-template")
public class DataSourceFactory extends HikariConfig {
    /**
     * 记录 {@link HikariConfig} 类型 {@code get/set} 方法对的 Map 对象
     */
    private static final Map<Method, Method> PROPERTIES_METHODS = new HashMap<>();

    /**
     * 将 {@link HikariConfig} 类型的所有方法进行遍历, 对成对的 get/set 方法进行记录
     */
    static {
        // 记录方法名和方法对应关系的 Map 对象
        var methodMap = new HashMap<String, Method>();

        // 遍历 HikariConfig 类的所有方法
        for (var method : HikariConfig.class.getMethods()) {
            // 获取方法名
            var name = method.getName();

            // 如果是 set 方法, 则在 methodMap 中查找对应的 get 方法, 如果成对, 则记录到 PROPERTIES_METHODS 中
            if (name.startsWith("set")) {
                var getter = methodMap.get("get" + name.substring(3));
                if (getter != null) {
                    PROPERTIES_METHODS.put(getter, method);
                }
                methodMap.putIfAbsent(name, method);
            }

            // 如果是 get 方法, 则在 methodMap 中查找对应的 set 方法, 如果成对, 则记录到 PROPERTIES_METHODS 中
            if (name.startsWith("get")) {
                var setter = methodMap.get("set" + name.substring(3));
                if (setter != null) {
                    PROPERTIES_METHODS.put(method, setter);
                }
                methodMap.putIfAbsent(name, method);
            }
        }
    }

    /**
     * 构建一个数据源对象
     *
     * @param dbName 要连接的数据库名称
     * @return 数据源对象
     */
    public DataSource build(String dbName) {
        var config = copyConfig();

        // 将 jdbcUrl 属性变更为正确的目标数据库连接
        config.setJdbcUrl(String.format(config.getJdbcUrl(), dbName));
        // 更改连接池的名称
        config.setPoolName(String.format("pool-%s", dbName));

        // 创建新的数据源对象
        return new HikariDataSource(config);
    }

    /**
     * 将当前对象的部分属性复制到一个新的 {@link HikariConfig} 对象中
     *
     * @return 完成属性赋值的 {@link HikariConfig} 对象
     */
    @SneakyThrows
    private HikariConfig copyConfig() {
        // 创建一个新对象
        var newConfig = new HikariConfig();

        // 从 PROPERTIES_METHODS 获取所有的 get/set 方法对
        for (var pair : PROPERTIES_METHODS.entrySet()) {
            // 从当前对象通过 get 方法获取属性值
            var value = pair.getKey().invoke(this);
            if (value != null) {
                // 到目标对象通过对应的 set 方法设置属性值
                pair.getValue().invoke(newConfig, value);
            }
        }
        return newConfig;
    }
}
