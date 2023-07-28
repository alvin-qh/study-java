package alvin.study.springboot.shiro.infra.entity;

import alvin.study.springboot.shiro.infra.entity.common.AuditedEntity;
import alvin.study.springboot.shiro.infra.mapper.SessionMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 对应 {@code session} 表的实体类型, 表示会话
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
 * 该实体对应的查询操作类为 {@link SessionMapper SessionMapper} 类型
 * </p>
 */
@Data
@TableName("session")
@EqualsAndHashCode(callSuper = true)
public class Session extends AuditedEntity {
    // session 的 key 值
    @TableField("`key`")
    private String key;

    // session 对象序列化字符串值
    @TableField("`value`")
    private String value;

    // session 超时时间
    @TableField("expired_at")
    private Instant expiredAt;
}
