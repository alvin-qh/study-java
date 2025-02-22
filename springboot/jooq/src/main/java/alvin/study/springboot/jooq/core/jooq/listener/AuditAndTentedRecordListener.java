package alvin.study.springboot.jooq.core.jooq.listener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.RecordContext;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jooq.core.context.Context;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.OrgRecord;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord;

/**
 * 数据记录处理监听器
 *
 * <p>
 * {@link RecordListener} 提供若干方法用于监听各类数据记录处理行为, 包括:
 * <ul>
 * <li>
 * {@link RecordListener#insertStart(RecordContext)} 和
 * {@link RecordListener#insertEnd(RecordContext)} 方法, 在执行 {@code insert}
 * 语句的前后进行监听
 * </li>
 * <li>
 * {@link RecordListener#updateStart(RecordContext)} 和
 * {@link RecordListener#updateEnd(RecordContext)} 方法, 在执行 {@code update}
 * 语句的前后进行监听
 * </li>
 * <li>
 * {@link RecordListener#deleteStart(RecordContext)} 和
 * {@link RecordListener#deleteEnd(RecordContext)} 方法, 在执行 {@code delete}
 * 语句的前后进行监听
 * </li>
 * <li>
 * {@link RecordListener#loadStart(RecordContext)} 和
 * {@link RecordListener#loadEnd(RecordContext)} 方法, 在读取数据前后进行监听
 * </li>
 * <li>
 * {@link RecordListener#mergeStart(RecordContext)} 和
 * {@link RecordListener#mergeEnd(RecordContext)} 方法, 在合并数据前后进行监听
 * </li>
 * <li>
 * {@link RecordListener#storeStart(RecordContext)} 和
 * {@link RecordListener#storeEnd(RecordContext)} 方法, 在存储数据 ({@code insert} 或
 * {@code update}) 前后进行监听
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 另外, {@link RecordListener} 还提供了一组以 {@code on} 开头的静态方法, 返回一个
 * {@link org.jooq.impl.CallbackRecordListener} 对象, 可以以 lambda 的方式产生一个监听器对象
 * </p>
 *
 * <p>
 * {@link RecordListenerProvider} 接口用来将当前的 {@link RecordListener} 作为配置项注入到
 * {@link org.jooq.DSLContext DSLContext} 对象中
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AuditAndTentedRecordListener implements RecordListener, RecordListenerProvider {
    /**
     * 请求上下文对象, 用于获取 {@code orgId} 值
     */
    private final Context context;

    /**
     * 在执行 {@code insert} 语句前对要插入的 {@code Record} 对象字段进行处理
     *
     * <p>
     * 本例中处理的字段包括:
     * <ul>
     * <li>
     * {@code org_id} 租户 ID 字段, 即 {@link OrgRecord} 实体的 {@code id} 属性
     * </li>
     * <li>
     * {@code created_at}, {@code updated_at}, {@code created_by},
     * {@code updated_by} 审计字段, 即一条记录的创建, 更新时间和创建, 更新人
     * </li>
     * <li>
     * {@code deleted} 软删除字段, 在插入数据时, 设置其值为默认值 {@code 0}
     * </li>
     * </ul>
     * </p>
     *
     * @param ctx 记录操作上下文对象, 对每一条 SQL 操作的所有监听方法, 共享同一个 {@link RecordContext} 对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public void insertStart(RecordContext ctx) {
        // 获取要插入的实体对象
        var rec = ctx.record();

        // 获取租户和审计字段用户
        var org = (OrgRecord) context.getOrDefault(Context.ORG, null);
        var user = (UserRecord) context.getOrDefault(Context.USER, null);

        // 遍历实体对象的所有字段
        for (var field : rec.fields()) {
            // 获取字段名, 字段名由 'schema', 'table name' 和 'field name' 三部分组成, 获取最后一部分名称
            var name = field.$name().last();

            // 根据字段名处理不同字段,
            switch (Objects.requireNonNull(name).toLowerCase()) {
            case "org_id" -> {
                if (rec.getValue(field) == null && org != null) {
                    rec.setValue((Field<Long>) field, org.getId());
                }
            }
            case "created_at", "updated_at" -> rec.setValue((Field<LocalDateTime>) field,
                LocalDateTime.now(ZoneOffset.UTC));
            case "created_by", "updated_by" -> {
                if (user != null) {
                    rec.setValue((Field<Long>) field, user.getId());
                }
            }
            case "deleted" -> rec.setValue((Field<Long>) field, 0L);
            default -> {}
            }
        }
    }

    /**
     * 在执行 {@code update} 语句前对要更新的 {@code Record} 对象字段进行处理
     *
     * <p>
     * 本例中处理的字段包括: {@code updated_at} 和 {@code updated_by} 审计字段, 即一条记录的更新时间和更新人
     * </p>
     *
     * @param ctx 记录操作上下文对象, 对每一条记录的一次操作的所有监听方法, 共享同一个 {@link RecordContext} 对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateStart(RecordContext ctx) {
        // 获取要更新的实体对象
        var rec = ctx.record();

        // 获取审计用户
        var user = (UserRecord) context.getOrDefault(Context.USER, null);

        // 遍历实体对象的所有字段
        for (var field : rec.fields()) {
            // 获取字段名, 字段名由 'schema', 'table name' 和 'field name' 三部分组成, 获取最后一部分名称
            var name = field.$name().last();

            // 根据字段名处理不同字段
            switch (Objects.requireNonNull(name).toLowerCase()) {
            case "updated_at" -> rec.setValue((Field<LocalDateTime>) field, LocalDateTime.now(ZoneOffset.UTC));
            case "updated_by" -> {
                if (user != null) {
                    rec.setValue((Field<Long>) field, user.getId());
                }
            }
            default -> {}
            }
        }
    }

    /**
     * 实现 {@link RecordListenerProvider} 接口, 为 {@link DSLContext} 注入当前对象作为配置项
     *
     * @return {@link RecordListener} 对象
     */
    @Override
    public RecordListener provide() {
        return this;
    }
}
