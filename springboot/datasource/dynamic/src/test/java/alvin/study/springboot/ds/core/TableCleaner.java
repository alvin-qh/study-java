package alvin.study.springboot.ds.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在每次测试执行前, 将测试数据库的表清空
 *
 * <p>
 * 本类型对象受 Bean 容器管理, 需要注入使用. 且非单例, 即用完销毁
 * </p>
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TableCleaner {
    // 注入 JDBC 模板对象
    @Autowired
    private JdbcTemplate template;

    /**
     * 获取所有待清除数据的数据表
     *
     * @param schema    要清除表的 {@code schema}, 对于 MySQL 即数据库名
     * @param tableType 要清除表的类型
     * @return 要清除的数据表集合
     */
    private List<Map<String, Object>> listAllTables(String schema, String tableType) {
        return template.queryForList("""
            SELECT `table_name`
            FROM `information_schema`.`tables`
            WHERE `table_schema`=? AND `table_type`=?
            """, schema, tableType);
    }

    /**
     * 清除所有的数据表
     *
     * @param exclude 要排除的数据表名称
     */
    @SneakyThrows
    @Transactional
    public void clearAllTables(String... exclude) {
        var excludeSet = Set.of(exclude);

        // 获取数据库连接地址
        var connectUrl = template.getDataSource().getConnection().getMetaData().getURL();

        String schema;
        String tableType;
        // 根据不同的数据库连接, 获取对应的 schema 和 tableType 参数
        if (connectUrl.startsWith("jdbc:mysql")) {
            schema = template.getDataSource().getConnection().getCatalog(); // MySQL schema
            tableType = "BASE TABLE"; // MySql 表类型
        } else {
            schema = "PUBLIC"; // h2 schema
            tableType = "BASE TABLE"; // h2 表类型
        }

        try {
            // 关闭外键约束
            template.execute("SET FOREIGN_KEY_CHECKS = 0");
            // 清空指定数据表
            listAllTables(schema, tableType).stream()
                .map(m -> m.get("table_name"))
                .filter(n -> !excludeSet.contains(n))
                .forEach(n -> {
                    log.info("Clear table {}", n);
                    template.execute(String.format("TRUNCATE TABLE `%s`", n));
                });
        } finally {
            // 恢复外键约束
            template.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
}
