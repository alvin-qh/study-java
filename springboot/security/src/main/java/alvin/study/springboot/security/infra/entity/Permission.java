package alvin.study.springboot.security.infra.entity;

import alvin.study.springboot.security.infra.entity.common.AuditedEntity;
import alvin.study.springboot.security.infra.mapper.PermissionMapper;
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
@TableName("permission")
@EqualsAndHashCode(callSuper = true)
public class Permission extends AuditedEntity {
    // 权限名称
    @TableField("name")
    private String name;

    // 权限资源
    @TableField("resource")
    private String resource;

    // 权限行为
    @TableField("action")
    private String action;

    /**
     * 获取权限全称
     *
     * @return 权限全称
     */
    public String getPermission() { return String.format("%s:%s:%s", name, resource, action); }

    /**
     * 获取权限目标
     *
     * @return 权限目标
     */
    public String getTarget() { return String.format("%s:%s", name, resource); }
}
