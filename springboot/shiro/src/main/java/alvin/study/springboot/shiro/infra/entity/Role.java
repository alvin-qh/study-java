package alvin.study.springboot.shiro.infra.entity;

import alvin.study.springboot.shiro.infra.entity.common.AuditedEntity;
import alvin.study.springboot.shiro.infra.mapper.RoleMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应 {@code role} 表的实体类型, 表示角色
 *
 * <p>
 * 当前类继承自 {@link AuditedEntity} 类, 引入主键和审计字段
 * </p>
 *
 * <p>
 * {@link TableField @TableField} 注解表示数据表字段的定义, 包括:
 * <ul>
 * <li>
 * {@code value} 属性表示对应的自定名称
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 该实体对应的查询操作类为 {@link RoleMapper RoleMapper} 类型
 * </p>
 */
@Data
@TableName("role")
@EqualsAndHashCode(callSuper = true)
public class Role extends AuditedEntity {
    // 角色名称
    @TableField("name")
    private String name;
}
