package alvin.study.springboot.mybatis.infra.entity;

import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import alvin.study.springboot.mybatis.infra.mapper.UserMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体, 对应 {@code user} 表
 *
 * <p>
 * 当前类继承自 {@link AuditedEntity} 类, 引入租户和审计字段
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
 * {@link TableLogic @TableLogic} 注解的字段表示一个软删除字段, 其 {@code value} 属性表示删除前的字段值, 其
 * {@code delval} 属性表示删除后的值, 本例中表示删除实体对象时, 会将 {@code deleted} 字段设置为 {@code id}
 * 字段的值
 * </p>
 *
 * <p>
 * 该实体对应的查询操作类为 {@link UserMapper UserMapper} 类型
 * </p>
 */
@Data
@TableName("user")
@EqualsAndHashCode(callSuper = true)
public class User extends AuditedEntity {
    // 用户登录账号
    @TableField("account")
    private String account;

    // 用户登录密码
    @TableField("password")
    private String password;

    // 用户类型
    @TableField("type")
    private UserType type;

    // 软删除标识字段
    @TableLogic(value = "0", delval = "#{id}")
    private long deleted = 0L;
}
