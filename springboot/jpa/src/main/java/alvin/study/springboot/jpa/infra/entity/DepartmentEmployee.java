package alvin.study.springboot.jpa.infra.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import alvin.study.springboot.jpa.infra.entity.common.AuditedEntity;

/**
 * 部门职员关系实体, 对应 {@code department_employee} 表
 *
 * <p>
 * 该实体同时和 {@link Employee} 及 {@link Department} 实体组成多对一关系, 令 {@link Employee} 和
 * {@link Department} 两个实体组成了多对多关系
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "department_employee")
public class DepartmentEmployee extends AuditedEntity {
    /**
     * <p>
     * 这里的 {@link ManyToOne @ManyToOne} 注解表示和一个 {@link Department} 对象对应的多对一关系的自连接,
     * 即若干 {@link Department} 对象可以对应一个 {@link DepartmentEmployee} 对象关联关系
     * </p>
     *
     * <p>
     * {@link JoinColumn @JoinColumn} 注解用来指定负责数据连接的字段, 这里为 {@code department_id} 字段,
     * 即每个 {@link DepartmentEmployee} 对象通过 {@code department_id} 字段指向一个
     * {@link Department} 对象
     * </p>
     *
     * <p>
     * 多对一的多指的是多个当前类型对象只能对应一个对应类型的对象
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * <p>
     * 这里的 {@link ManyToOne @ManyToOne} 注解表示和一个 {@link Employee} 对象对应的多对一关系的自连接,
     * 即若干 {@link Employee} 对象可以对应一个 {@link DepartmentEmployee} 对象关联关系
     * </p>
     *
     * <p>
     * {@link JoinColumn @JoinColumn} 注解用来指定负责数据连接的字段, 这里为 {@code employee_id} 字段,
     * 即每个 {@link DepartmentEmployee} 对象通过 {@code employee_id} 字段指向一个
     * {@link Employee} 对象
     * </p>
     *
     * <p>
     * 多对一的多指的是多个当前类型对象只能对应一个对应类型的对象
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
