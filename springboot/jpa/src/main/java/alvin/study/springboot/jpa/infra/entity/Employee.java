package alvin.study.springboot.jpa.infra.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import alvin.study.springboot.jpa.infra.entity.common.AuditedEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 雇员实体, 对应 {@code employee} 表
 *
 * <p>
 * 当前类继承自 {@link AuditedEntity} 类, 引入租户和审计字段
 * </p>
 *
 * <p>
 * 当前类支持软删除: 即并不从数据表中实际删除数据, 而是通过一个标记字段表示一条数据是否可用. 软删除通过 {@link SQLRestriction @SQLRestriction}
 * 注解和 {@link SQLDelete @SQLDelete} 注解共同实现, 前者表示当前实体类型对应的查询 SQL 必须附加的查询条件,
 * 后者表示当删除当前实体对象时, 实际执行的 SQL 语句
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "employee")
@SQLRestriction("deleted = 0") // 增加逻辑删除查询条件
@SQLDelete(sql = "UPDATE employee SET deleted = id WHERE id = ?") // 增加逻辑删除 SQL 语句
public class Employee extends AuditedEntity {
    /**
     * 职员名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 职员电子邮件地址
     */
    @Column(name = "email")
    private String email;

    /**
     * 职员职称
     */
    @Column(name = "title")
    private String title;

    /**
     * 雇员和部门的关系
     *
     * <p>
     * 这里的 {@link OneToMany @OneToMany} 注解表示一个和部门员工关系实体 {@link DepartmentEmployee}
     * 对象对应的多对一的外连接, 即一个 {@link Department} 对象可以对应多个 {@link DepartmentEmployee}
     * 对象作为部门和员工的对应关系
     * </p>
     *
     * <p>
     * {@link DepartmentEmployee} 实体表示部门和员工的归属关系, 借助这个实体, {@link Department} 实体和
     * {@link Employee} 实体实际组成了多对多关系, 即 {@link DepartmentEmployee} 实体和
     * {@link Department} 实体和 {@link Employee} 实体均组成了多对一关系
     * </p>
     *
     * <p>
     * 当前字段和 {@link DepartmentEmployee#employee} 字段对应, 即注解 {@code mappedBy}
     * 参数所指向的字段名
     * </p>
     *
     * <p>
     * 一对多指的是一个当前对象可以对应多个相关类型的对象, 对应的多个对象组成 {@link List} (或者 {@link java.util.Set
     * Set}) 集合对象
     * </p>
     *
     * <p>
     * 注解的 {@code cascade} 参数表示级联方法, 可以为如下选项:
     * <ul>
     * <li>
     * {@link CascadeType#DETACH}, 级联分类, 即将当前对象从 JPA 容器中分离, 同时集合中的对象也会随之分离
     * </li>
     * <li>
     * {@link CascadeType#MERGE}, 级联更新, 若在集合中的对象发生修改, 则当前对象保存时也会保存集合中的对象
     * </li>
     * <li>
     * {@link CascadeType#PERSIST}, 级联保存, 若当前对象持久化, 则集合中的所有对象都进行持久化
     * </li>
     * <li>
     * {@link CascadeType#REFRESH}, 级联刷新, 若当前对象进行刷新, 则集合中的对象也会被刷新
     * </li>
     * <li>
     * {@link CascadeType#REMOVE}, 级联删除, 即当前对象删除, 集合中的对象也会跟随删除
     * </li>
     * <li>
     * {@link CascadeType#ALL}, 表示包括上述所有级联操作
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 注解的 {@code orphanRemoval} 参数表示是否删除"孤立对象", 需要和 {@link CascadeType#REMOVE}
     * 选项配合使用
     * </p>
     *
     * <p>
     * {@link OrderBy @OrderBy} 注解表示查询级联集合时采用的排序规则
     * </p>
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OrderBy("id asc")
    private List<DepartmentEmployee> departmentEmployees = new ArrayList<>();

    /**
     * 获取部门职员列表
     *
     * <p>
     * 当前实体存储了 {@link DepartmentEmployee} 集合, 从该集合的每一项中可以获取 {@link Department} 实体,
     * 该方法即通过一个 mapping 将 {@link DepartmentEmployee} 集合转换为 {@link Department} 集合
     * </p>
     *
     * @return 返回 {@link Department} 类型的 {@link List} 集合对象
     */
    public List<Department> getDepartments() {
        return departmentEmployees.stream().map(DepartmentEmployee::getDepartment).toList();
    }
}
