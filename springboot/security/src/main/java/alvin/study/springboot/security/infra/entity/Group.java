package alvin.study.springboot.security.infra.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import alvin.study.springboot.security.infra.entity.common.AuditedEntity;
import alvin.study.springboot.security.infra.mapper.GroupMapper;

/**
 * 对应 {@code group} 表的实体类型, 表示用户组
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
 * 该实体对应的查询操作类为 {@link GroupMapper GroupMapper} 类型
 * </p>
 */
@Data
@TableName("`group`")
@EqualsAndHashCode(callSuper = true)
public class Group extends AuditedEntity {
    // 组名称
    @TableField("name")
    private String name;
}
