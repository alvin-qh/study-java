package alvin.study.springboot.shiro.infra.entity;

import alvin.study.springboot.shiro.infra.entity.common.AuditedEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应 {@code role_grant} 表的实体类型, 表示用户 (或者组) 和角色之间的关系
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
 * 该实体对应的查询操作类为 {@link alvin.study.infra.mapper.RoleGrantGroupMapper
 * RoleGrantGroupMapper}
 * 类型
 * </p>
 */
@Data
@TableName("role_grant")
@EqualsAndHashCode(callSuper = true)
public class RoleGrant extends AuditedEntity {
    // 用户或组 id
    @TableField("user_or_group_id")
    private Long userOrGroupId;

    // 授权类型
    @TableField("type")
    private RoleGrantType type;

    // 角色 id
    @TableField("role_id")
    private Long roleId;
}
