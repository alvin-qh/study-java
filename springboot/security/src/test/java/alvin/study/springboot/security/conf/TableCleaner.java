package alvin.study.springboot.security.conf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.security.util.db.SqlSessionManager;

/**
 * 在每次测试执行前, 将测试数据库的表清空
 *
 * <p>
 * 本类型对象受 Bean 容器管理, 需要注入使用. 且非单例, 即用完销毁
 * </p>
 */
@Slf4j
@Component
public class TableCleaner {
    /**
     * 注入 mybatis 的 SqlSession 管理器
     *
     * @see SqlSessionManager#build()
     * @see SqlSessionManager.SqlSessionHolder#getConnection()
     * @see SqlSessionManager.SqlSessionHolder#close()
     */
    @Autowired
    private SqlSessionManager sessionManager;

    // 获取数据库连接字符串
    @Value("spring.datasource.url")
    private String connectUrl;

    /**
     * 获取所有待清除数据的数据表
     *
     * @param schema    要清除表的 {@code schema}, 对于 MySQL 即数据库名
     * @param tableType 要清除表的类型
     * @return 要清除的数据表集合
     */
    @SneakyThrows
    private List<String> listAllTables(Connection conn, String schema, String tableType) {
        // 获取所有数据表的 sql 语句
        var sql = """
            SELECT `table_name`
            FROM `information_schema`.`tables`
            WHERE `table_schema` = ? AND `table_type` = ?
            """;

        var results = new ArrayList<String>();

        try (var stat = conn.prepareStatement(sql)) {
            // 设置参数
            stat.setString(1, schema);
            stat.setString(2, tableType);

            // 执行 sql 语句
            try (var rs = stat.executeQuery()) {
                // 将查询结果存储
                while (rs.next()) {
                    results.add(rs.getString("table_name"));
                }
            }
        }

        return results;
    }

    /**
     * 清除所有的数据表
     *
     * @param exclude 要排除的数据表名称
     * @see alvin.study.springboot.security.util.db.SqlSessionManager.SqlSessionHolder
     */
    @SneakyThrows
    @Transactional
    public void clearAllTables(String... exclude) {
        var excludeSet = Set.of(exclude);

        // 获取 SqlSessionHolder 对象
        try (var holder = sessionManager.build()) {
            // 获取数据库连接
            var conn = holder.getConnection();

            String schema;
            String tableType;
            // 根据不同的数据库连接, 获取对应的 schema 和 tableType 参数
            // h2 表类型
            if (connectUrl.startsWith("jdbc:mysql")) {
                schema = conn.getCatalog(); // MySQL schema
            } else {
                schema = "PUBLIC"; // h2 schema
            }
            tableType = "BASE TABLE"; // MySql 表类型

            // 关闭外键约束
            try (var stat = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                stat.execute();
            }

            // 获取所有待清除的数据表名并执行清除语句
            listAllTables(conn, schema, tableType)
                    .stream()
                    // 去除排除的数据表
                    .filter(t -> !excludeSet.contains(t))
                    // 对每个数据表进行清除操作
                    .forEach(t -> {
                        log.info("Clear table {}", t);
                        // 执行 truncate 操作
                        try (var stat = conn.prepareStatement(String.format("TRUNCATE TABLE `%s`", t))) {
                            stat.execute();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            // 恢复外键约束
            try (var stat = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
                stat.execute();
            }
        }
    }
}
