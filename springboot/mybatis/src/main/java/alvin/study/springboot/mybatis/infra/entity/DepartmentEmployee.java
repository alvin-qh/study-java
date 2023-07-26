package alvin.study.springboot.mybatis.infra.entity;

import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    /**
     * 乐观锁版本字段
     *
     * <p>
     * 首先, 数据表中要有对应的 {@code INT} 类型 {@code version} 字段
     * </p>
     *
     * <p>
     * 其次, 需要通过 {@link Version @Version} 注解在实体类中注解对应的 {@code version} 字段
     * </p>
     *
     * <p>
     * 最后, 需要开启 MyBatis 的 Interceptor, 参考: {@code MyBatisConfig.interceptor()} 方法
     * </p>
     */
    @Version
    @TableField("version")
    private int version = 0;
}
