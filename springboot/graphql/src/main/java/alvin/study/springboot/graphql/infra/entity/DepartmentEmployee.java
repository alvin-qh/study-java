package alvin.study.springboot.graphql.infra.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import alvin.study.springboot.graphql.infra.entity.common.AuditedEntity;

/**
 * 部门职员关系实体, 对应 {@code department_employee} 表
 *
 * <p>
 * 通过该实体, 让 {@link Department} 实体和 {@link Employee} 实体形成了 n:n 关系
 * </p>
 */
@Data
@TableName("department_employee")
@EqualsAndHashCode(callSuper = true)
public class DepartmentEmployee extends AuditedEntity {
    /**
     * 部门 id
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 职员 id
     */
    @TableField("employee_id")
    private Long employeeId;
}
