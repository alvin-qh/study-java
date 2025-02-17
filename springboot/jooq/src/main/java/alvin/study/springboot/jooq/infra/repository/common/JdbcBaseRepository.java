package alvin.study.springboot.jooq.infra.repository.common;

import alvin.study.springboot.jooq.core.jooq.dsl.JdbcDSLContextManager;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 通过 JDBC 使用 Jooq 的 Repository 类型超类
 *
 * @param <R> 对应实体的 Record 类型
 */
public abstract class JdbcBaseRepository<R extends Record> {
    /**
     * 实体对应的 {@link Table} 类型
     */
    protected final Table<? extends R> table;
    /**
     * 注入 {@link JdbcDSLContextManager} 类型, 用于演示通过 JDBC 创建并使用 Jooq 框架的方法
     */
    @Autowired
    protected JdbcDSLContextManager contextManager;

    /**
     * 构造器, 实例化当前 Repository 类型
     *
     * @param table 与当前实体类型对应的 {@link Table} 类型
     */
    protected JdbcBaseRepository(Table<? extends R> table) {
        this.table = table;
    }

    /**
     * 实例化一个和当前实体对应的 Record 对象
     *
     * @return {@link R} 类型对象
     */
    public R newRecord(Consumer<R> exec) throws SQLException {
        var rec = contextManager.get().newRecord(table);
        if (exec != null) {
            exec.accept(rec);
        }
        return rec;
    }

    /**
     * 实例化一个和当前实体对应的 Record 对象
     *
     * @param pojo 和当前实体对应的 Pojo 对象
     * @return {@link R} 类型对象
     */
    public R newRecord(Object pojo, Consumer<R> exec) throws SQLException {
        var rec = contextManager.get().newRecord(table, pojo);
        if (exec != null) {
            exec.accept(rec);
        }
        return rec;
    }

    /**
     * 将数量为 1 的 {@link List} 对象转为 {@link Optional} 对象
     *
     * @param <T>     集合元素类型
     * @param results 集合参数
     * @return {@link Optional} 对象, 如果集合为空, 则为 {@link Optional#empty()
     *         Optional.empty()}
     */
    protected <T> Optional<T> asOptional(List<T> results) {
        return results == null || results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
