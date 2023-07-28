package alvin.study.springboot.shiro.infra.entity;

import alvin.study.springboot.shiro.infra.entity.common.BaseEntity;
import alvin.study.springboot.shiro.infra.mapper.MenuMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应 {@code group} 表的实体类型, 表示用户组
 *
 * <p>
 * 当前类继承自 {@link BaseEntity} 类, 引入主键
 * </p>
 *
 * <p>
 * {@link TableField @TableField} 注解表示数据表字段的定义, 包括:
 * <ul>
 * <li>
 * {@code value} 属性表示对应的自定名称
 * </li>
 * <li>
 * {@code exist = false} 表示该字段不在数据表中对应
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 该实体对应的查询操作类为 {@link MenuMapper MenuMapper} 类型
 * </p>
 */
@Data
@TableName("menu")
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseEntity {
    // 菜单序列
    @TableField("`order`")
    private int order;

    // 菜单文本
    @TableField("text")
    private String text;

    // 菜单图标
    @TableField("icon")
    private String icon;

    // 上级菜单 id
    @TableField("parent_id")
    private Long parentId;

    // 菜单对应的角色 id
    @TableField("role_id")
    private Long roleId;

    // 菜单对应的权限 id
    @TableField("permission_id")
    private Long permissionId;

    // 菜单角色
    @TableField(exist = false)
    private Role role;

    // 菜单权限
    @TableField(exist = false)
    private Permission permission;
}
