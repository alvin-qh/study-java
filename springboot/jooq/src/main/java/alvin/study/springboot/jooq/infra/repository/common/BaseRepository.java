package alvin.study.springboot.jooq.infra.repository.common;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.jooq.core.context.Context;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.OrgRecord;

/**
 * 通过 Spring 框架集成 Jooq 的 Repository 类型超类
 *
 * @param <R> 实体对象类型
 */
public abstract class BaseRepository<R extends Record> {
    /**
     * 当前 Repository 类型要操作的数据表
     */
    private final Table<? extends R> table;
    /**
     * 注入 {@link DSLContext} 对象
     */
    @Autowired
    private DSLContext dsl;

    /**
     * 构造器
     *
     * @param table 数据表对象
     */
    protected BaseRepository(Table<? extends R> table) {
        this.table = table;
    }

    /**
     * 实例化一个和当前实体对应的 Record 对象
     *
     * @param exec 回调函数, 用于处理实体对象
     * @return {@link R} 类型对象
     */
    public R newRecord(Consumer<R> exec) {
        var rec = dsl.newRecord(table);
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
    public R newRecord(Object pojo, Consumer<R> exec) {
        var rec = dsl.newRecord(table, pojo);
        if (exec != null) {
            exec.accept(rec);
        }
        return rec;
    }

    /**
     * 获取组织 ID
     *
     * @return 当前登录用户所在的组织 ID
     */
    protected Long currentOrgId() {
        return Optional.ofNullable(Context.current().<OrgRecord>get(Context.ORG))
                .map(OrgRecord::getId)
                .orElse(null);
    }

    /**
     * 获取 {@link DSLContext} 对象
     *
     * @return {@link DSLContext} 对象
     */
    protected DSLContext dsl() {
        return dsl;
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
