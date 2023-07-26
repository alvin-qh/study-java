package alvin.study.springboot.mybatis.infra.entity.common;

import alvin.study.springboot.mybatis.infra.handler.EntityFieldHandler;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 多租户实体类的超类
 *
 * <p>
 * 为所有的子类定义了 {@code org_id} 字段, 表示租户 {@code id}
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
public abstract class TenantedEntity extends BaseEntity {
    // 租户字段
    @TableField(value = "org_id", fill = FieldFill.INSERT)
    private Long orgId;
}
