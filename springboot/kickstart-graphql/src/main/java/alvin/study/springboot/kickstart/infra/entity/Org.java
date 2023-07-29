package alvin.study.springboot.kickstart.infra.entity;

import alvin.study.springboot.kickstart.infra.entity.common.BaseEntity;
import alvin.study.springboot.kickstart.infra.mapper.OrgMapper;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 组织实体, 同时表示租户, 对应 {@code org} 表
 *
 * <p>
 * 该实体对应的查询操作类为 {@link OrgMapper OrgMapper} 类型
 * </p>
 */
@Data
@TableName("org")
@EqualsAndHashCode(callSuper = true)
public class Org extends BaseEntity {
    // 组织名称
    @TableField("name")
    private String name;

    // 软删除标识字段
    @TableLogic(value = "0", delval = "#{id}")
    private long deleted = 0L;

    // 记录实体创建时间
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;

    // 记录实体修改时间
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
