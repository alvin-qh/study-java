package alvin.study.springboot.mybatis.infra.entity;

import alvin.study.springboot.mybatis.infra.entity.common.BaseEntity;
import alvin.study.springboot.mybatis.infra.mapper.OrgMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 组织实体, 同时表示租户, 对应 {@code org} 表
 *
 * <p>
 * 该实体对象由 {@link OrgMapper OrgMapper} 类型操作, 因为
 * {@link OrgMapper OrgMapper} 类型未继承
 * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 类型, 所以
 * mybatis-plus 的注解 (包括: {@code @TableName}, {@code @TableField} 以及
 * {@code @FieldFill} 等) 均无效
 * </p>
 *
 * <p>
 * 该实体对应的查询操作类为 {@link OrgMapper OrgMapper} 类型
 * </p>
 */
@Data
// @TableName("org")
@EqualsAndHashCode(callSuper = true)
public class Org extends BaseEntity {
    // 组织名称
    private String name;

    // 软删除标识字段
    // @TableLogic(value = "0", delval = "#{id}")
    private long deleted;

    // 记录实体创建时间
    // @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;

    // 记录实体修改时间
    // @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
