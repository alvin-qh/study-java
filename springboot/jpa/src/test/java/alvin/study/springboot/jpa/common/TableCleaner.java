package alvin.study.springboot.jpa.common;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

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
    private static volatile boolean tableCleaned = false;

    // 获取 JPA 实体管理器
    @PersistenceContext
    private EntityManager em;

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
    private List<String> listAllTables(String schema, String tableType) {
        return em.createNativeQuery("""
                SELECT `table_name`
                FROM `information_schema`.`tables`
                WHERE `table_schema`=:schema AND `table_type`=:typeType
                """)
            .setParameter("schema", schema)
            .setParameter("typeType", tableType)
            .getResultList();
    }

    /**
     * 清除所有的数据表
     *
     * @param exclude 要排除的数据表名称
     */
    @Transactional
    public synchronized void clearAllTables(String... exclude) {
        if (tableCleaned) {
            return;
        }

        var excludeSet = Set.of(exclude);

        // 从 EntityManager 对象中获取 Hibernate 的 Session 对象
        var session = em.unwrap(Session.class);

        // 执行数据库操作
        session.doWork(conn -> {
            String schema;
            String tableType;
            // 根据不同的数据库连接, 获取对应的 schema 和 tableType 参数
            // h2 表类型
            if (connectUrl.startsWith("jdbc:mysql")) {
                schema = conn.getCatalog(); // MySQL schema
            } else {
                schema = "PUBLIC"; // h2 schema
            }
            tableType = "BASE TABLE"; // 表类型

            // 关闭外键约束
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            // 获取所有待清除的数据表名并执行清除语句
            listAllTables(schema, tableType).stream()
                // 去除排除的数据表
                .filter(t -> !excludeSet.contains(t))
                // 对每个数据表进行清除操作
                .forEach(t -> {
                    log.info("Clear table {}", t);
                    // 执行 truncate 操作
                    em.createNativeQuery(String.format("TRUNCATE TABLE `%s`", t)).executeUpdate();
                });

            // 恢复外键约束
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        });

        tableCleaned = true;
    }
}
