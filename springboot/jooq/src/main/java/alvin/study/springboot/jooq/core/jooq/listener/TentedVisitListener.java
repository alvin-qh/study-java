package alvin.study.springboot.jooq.core.jooq.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.RecordContext;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jooq.core.context.Context;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.OrgRecord;

/**
 * 数据访问处理监听器
 *
 * <p>
 * {@link VisitListener} 提供若干方法用于监听各类数据访问行为, 包括:
 * <ul>
 * <li>
 * {@link VisitListener#visitStart(VisitContext)} 和
 * {@link VisitListener#visitEnd(VisitContext)} 方法, 在执行 {@link QueryPart}
 * 的前后进行监听
 * </li>
 * <li>
 * {@link VisitListener#clauseStart(VisitContext)} 和
 * {@link VisitListener#clauseEnd(VisitContext)} 方法, 在执行查询子句的前后进行监听
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 另外, {@link VisitListener} 还提供了一组以 {@code on} 开头的静态方法, 返回一个
 * {@link org.jooq.impl.CallbackVisitListener} 对象, 可以以 lambda 的方式产生一个监听器对象
 * </p>
 *
 * <p>
 * {@link VisitListenerProvider} 接口用来将当前的 {@link VisitListener} 作为配置项注入到
 * {@link org.jooq.DSLContext DSLContext} 对象中
 * </p>
 */
@Component
@RequiredArgsConstructor
public class TentedVisitListener implements VisitListener, VisitListenerProvider {
    /**
     * 记录所需字段对象的缓存
     */
    private final Map<Table<?>, FieldWrapper> tableFieldsMap = new ConcurrentHashMap<>();

    /**
     * 请求上下文对象, 用于获取 {@code orgId} 值
     */
    private final Context context;

    /**
     * 在执行 {@link QueryPart} 后添加额外的 {@code where} 条件
     *
     * <p>
     * 对于 {@code from} 字句中包含的表中包含 {@code org_id} 字段的查询, 会在 {@code where} 中额外包含
     * {@code org_id = :orgId} 查询条件
     * </p>
     *
     * @param context 数据访问上下文对象, 对每一条 SQL 的所有监听方法, 共享同一个 {@link RecordContext} 对象
     */
    @Override
    public void visitEnd(VisitContext context) {
        walkQueryPars(context);
    }

    /**
     * 遍历 SQL 执行中所有被处理的 {@link QueryPart} 对象, 找到所需的 {@link Field} 对象
     *
     * @param context 数据访问上下文对象
     */
    @SuppressWarnings("unchecked")
    private void walkQueryPars(VisitContext context) {
        // 从请求上下文中获取 orgId 值
        var mayOrgId = Optional.ofNullable(this.context.<OrgRecord>getOrDefault(Context.ORG, null))
                .map(OrgRecord::getId);

        if (mayOrgId.isEmpty()) {
            // 如果请求上下文中没有 orgId, 则无需添加查询条件
            return;
        }

        // 从数据访问上下文对象获取 Set 对象, 防止同一个 QueryPart 对象多次处理时, 重复添加 where 条件
        var fieldsSet = loadUsedFieldSet(context);

        // 遍历查询中包含的所有 QueryPart 对象
        for (var queryPart : context.queryParts()) {
            // 对类型为 SelectQuery 的 QueryPart 对象进行处理
            if (queryPart instanceof SelectQuery<?> select) {
                // 遍历 QueryPart 中 from 的目标表对象
                for (var field : walkTables(select)) {
                    // 如果条件未被添加过, 则添加条件
                    if (!fieldsSet.contains(field)) {
                        fieldsSet.add(field);
                        select.addConditions(((Field<Long>) field).eq(mayOrgId.get()));
                    }
                }
            }
        }
    }

    /**
     * 从数据访问上下文对象获取 Set 对象, 防止同一个 {@link QueryPart} 对象多次处理时, 重复添加 {@code where} 条件
     *
     * @param context 数据访问上下文对象
     * @return 保存 {@link Field} 对象的 {@link Set} 集合
     */
    @SuppressWarnings("unchecked")
    private Set<Field<?>> loadUsedFieldSet(VisitContext context) {
        // 从数据访问上下文对象中获取 Set 对象
        var fieldSet = (Set<Field<?>>) context.data("fieldSet");
        if (fieldSet == null) {
            // 如果当前上下文中不包含指定 Set 对象, 则创建 Set 对象并写入上下文
            fieldSet = new HashSet<>();
            context.data("fieldSet", fieldSet);
        }
        return fieldSet;
    }

    /**
     * 遍历 {@link SelectQuery} 中 {@code from} 的目标表
     *
     * @param select {@link SelectQuery} 类型的 {@link QueryPart} 对象
     * @return 表对象中包含哦指定字段列表
     */
    private List<? extends Field<?>> walkTables(SelectQuery<?> select) {
        // 从 SelectQuery 的 from 部分获取对应的表对象, 遍历表对象, 获取指定字段集合
        return select.$from().stream()
                .map(table -> this.findTableField(table, "org_id"))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 在表对象中查找指定的字段
     *
     * @param table     表对象
     * @param fieldName 字段名
     * @return 字段对象
     */
    private Field<?> findTableField(Table<?> table, String fieldName) {
        // 从缓存 Map 中获取指定表对应的字段对象
        return tableFieldsMap.computeIfAbsent(table, t -> {
            // 若缓存 Map 中不包含指定的字段对象, 则从表对象中查询并加入 Map 缓存对象中
            for (var field : t.fields()) {
                if (fieldName.equalsIgnoreCase(field.$name().last())) {
                    return new FieldWrapper(field);
                }
            }
            // 表对象中不包含指定字段, 返回一个空包装对象, 以便 ConcurrentHashMap 中可以正常存储
            return FieldWrapper.EMPTY;
        }).field();
    }

    /**
     * 实现 {@link VisitListenerProvider} 接口, 为 {@link DSLContext} 注入当前对象作为配置项
     *
     * @return {@link VisitListener} 对象
     */
    @Override
    public VisitListener provide() {
        return this;
    }

    /**
     * {@link Field} 对象的包装类
     *
     * <p>
     * 为解决 {@link ConcurrentHashMap} 无法以 {@code null} 作为 Value 的限制, 通过一个非
     * {@code null} 的对象类型对其进行包装
     * </p>
     *
     * @param field 包装的 {@link Field} 字段
     */
    private record FieldWrapper(Field<?> field) {
        /**
         * 表示 "空" 的常量
         */
        public static final FieldWrapper EMPTY = new FieldWrapper(null);
    }
}
