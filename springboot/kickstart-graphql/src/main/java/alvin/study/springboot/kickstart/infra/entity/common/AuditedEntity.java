package alvin.study.springboot.kickstart.infra.entity.common;

import alvin.study.springboot.kickstart.infra.handler.EntityFieldHandler;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 包含审计字段的实体超类
 *
 * <p>
 * 为所有的子类增加审计字段包括:
 * <ol>
 * <li>
 * 操作人员字段: {@code created_by} 和 {@code updated_by}, 均为 {@link Long} 类型,
 * 表示操作人的 {@code id}
 * </li>
 * <li>
 * 操作时间字段: {@code created_at} 和 {@code updated_at}, 均为 {@link Instant}
 * 类型, 表示操作时间
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * {@link TableField @TableField} 注解表示数据表字段的定义, 包括:
 * <ul>
 * <li>
 * {@code value} 属性表示对应的自定名称
 * </li>
 * <li>
 * {@code fill} 属性表示字段自动填充的规则, 参考: {@link FieldFill} 枚举. 字段填充由
 * {@link EntityFieldHandler EntityFieldHandler} 类实现
 * </li>
 * </ul>
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AuditedEntity extends TenantedEntity {
    // 记录实体创建者
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    // 记录实体更新者
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    // 记录实体创建时间
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;

    // 记录实体更新时间
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
