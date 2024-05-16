package alvin.study.springboot.mybatis.infra.entity;

import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import alvin.study.springboot.mybatis.infra.mapper.DepartmentMapper;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门实体, 对应 {@code department} 表
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
 * <li>
 * {@code exist = false} 不实际对应数据表的字段
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 该实体对应的查询操作类为 {@link DepartmentMapper
 * DepartmentMapper} 类型
 * </p>
 */
@Data
@TableName("department")
@EqualsAndHashCode(callSuper = true)
public class Department extends AuditedEntity {
    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 软删除标识字段
     */
    @TableLogic(value = "0", delval = "#{id}")
    private long deleted = 0L;

    /**
     * 级联的上一级部门
     */
    @TableField(exist = false)
    private Department parent;

    /**
     * 级联的子部门列表
     *
     * <p>
     * 该类成员字段并不对应到数据表字段
     * </p>
     */
    @TableField(exist = false)
    private List<Department> children;

    /**
     * 部门下的员工
     *
     * <p>
     * 该类成员字段并不对应到数据表字段
     * </p>
     */
    @TableField(exist = false)
    private List<Employee> employees;
}
