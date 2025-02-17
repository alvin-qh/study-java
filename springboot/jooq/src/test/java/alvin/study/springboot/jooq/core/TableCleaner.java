package alvin.study.springboot.jooq.core;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jooq.DSLContext;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据表清理器
 */
@Slf4j
@RequiredArgsConstructor
public class TableCleaner {
    private static volatile boolean tableCleaned = false;

    // jooq context 对象
    private final DSLContext dsl;

    // 数据库连接地址
    private final String dbConnectionUrl;

    /**
     * 列出所有需清理数据表的名称
     *
     * <p>
     * 这里通过 Jooq 来执行 SQL 语句, 在未指定数据表实体类型的情况下, 通过字符串也可以完成 SQL 的生成, 主要通过
     * {@link org.jooq.impl.DSL#table(String) DSL.table(String)} 方法和
     * {@link org.jooq.impl.DSL#field(String) DSL.field(String)} 方法, 前者通过字符串产生一个
     * {@link org.jooq.Table Table} 类型对象, 后者通过字符串产生一个 {@link org.jooq.Field Field}
     * 类型对象
     * </p>
     *
     * @param schema    数据表所在的 schema
     * @param tableType 要清理的数据表类型, 不同的数据库值不同
     * @return schema 下所有指定类型的表名称
     */
    @SneakyThrows
    public List<String> listAllTables(String schema, String tableType) {
        // 查询元数据表, 获取所需数据表名称
        return dsl.select(field("table_name"))
                .from(table("information_schema.tables"))
                .where(field("table_schema").eq(schema))
                .and(field("table_type").eq(tableType))
                .fetch()
                .into(String.class);
    }

    /**
     * 执行清理数据表操作
     *
     * @param exclude 无需清理的数据表列表
     */
    public synchronized void clearAllTables(String... exclude) {
        if (tableCleaned) {
            return;
        }

        var excludeSet = Set.of(exclude);

        // 进入 jooq 事务
        dsl.transaction(conf -> {
            final String schema;
            final String tableType;

            // 根据数据库连接 url, 对不同类型的数据库做不同的处理
            if (dbConnectionUrl.startsWith("jdbc:mysql")) {
                var conn = Objects.requireNonNull(conf.connectionProvider().acquire());
                schema = conn.getCatalog(); // 获取 schema
                tableType = "BASE TABLE"; // mysql 表类型
            } else if (dbConnectionUrl.startsWith("jdbc:h2")) {
                schema = "PUBLIC"; // h2 schema
                tableType = "BASE TABLE"; // h2 表类型
            } else {
                throw new IllegalArgumentException("unknown database connection");
            }

            // 禁用外键约束
            dsl.execute("SET FOREIGN_KEY_CHECKS = 0");

            // 列出所有待清理的数据表
            listAllTables(schema, tableType).stream()
                    .filter(t -> { // 排除无需清理的数据表
                        var ex = excludeSet.contains(t);
                        if (ex) {
                            log.debug(String.format("table \"%s\" did not need clean, ignored", t));
                        }
                        return !ex;
                    })
                    .forEach(t -> { // 清理所需的数据表
                        dsl.execute(String.format("TRUNCATE TABLE `%s`", t));
                        log.debug(String.format("table \"%s\" was cleaned", t));
                    });

            // 恢复外键约束
            dsl.execute("SET FOREIGN_KEY_CHECKS = 1");
        });

        tableCleaned = true;
    }
}
