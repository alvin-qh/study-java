package alvin.study.springboot.shiro.infra.entity;

import alvin.study.springboot.shiro.infra.entity.common.AuditedEntity;
import alvin.study.springboot.shiro.infra.mapper.PermissionMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应 {@code permission} 表的实体类型, 表示权限
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
 * 该实体对应的查询操作类为 {@link PermissionMapper
 * PermissionMapper} 类型
 * </p>
 */
@Data
@TableName("role_permission")
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends AuditedEntity {
    // 权限名称
    @TableField("role_id")
    private Long roleId;

    // 权限资源
    @TableField("permission_id")
    private Long permissionId;
}
