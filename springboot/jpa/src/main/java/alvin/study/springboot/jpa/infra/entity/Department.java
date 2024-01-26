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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 部门实体, 对应 {@code department} 表
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
@Table(name = "department")
@SQLRestriction("deleted = 0")
@SQLDelete(sql = "UPDATE department SET deleted = id WHERE id = ?")
public class Department extends AuditedEntity {
    /**
     * 部门名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 级联的上一级部门
     *
     * <p>
     * 这里的 {@link ManyToOne @ManyToOne} 注解表示一个和上一级 {@link Department}
     * 对象对应的多对一关系的自连接, 即若干 {@link Department} 对象可以对应一个 {@link Department} 对象作为上级部门
     * </p>
     *
     * <p>
     * {@link JoinColumn @JoinColumn} 注解用来指定负责数据连接的字段, 这里为 {@code parent_id} 字段, 即每个
     * {@link Department} 对象通过 {@code parent_id} 字段指向上一级 {@link Department} 对象
     * </p>
     *
     * <p>
     * 多对一的多指的是多个当前类型对象只能对应一个对应类型的对象, 因为是自连接, 所以两边的类型都为 {@link Department} 类型
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    /**
     * 级联的子部门列表
     *
     * <p>
     * 这里的 {@link OneToMany @OneToMany} 注解表示一个和下一级 {@link Department} 对象对应的多对一的自联结,
     * 即一个 {@link Department} 对象可以对应多个 {@link Department} 对象作为下级部门
     * </p>
     *
     * <p>
     * 当前字段和 {@link Department#parent} 字段对应, 即注解 {@code mappedBy} 参数所指向的字段名
     * </p>
     *
     * <p>
     * 一对多指的是一个当前对象可以对应多个相关类型的对象, 对应的多个对象组成 {@link List} (或者 {@link java.util.Set})
     * 集合对象
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
    @OrderBy("id asc")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<Department> children = new ArrayList<>();

    /**
     * 部门和雇员的关系
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
     * 当前字段和 {@code DepartmentEmployee.department} 字段对应, 即注解 {@code mappedBy}
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
    @OrderBy("id asc")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<DepartmentEmployee> departmentEmployees = new ArrayList<>();

    /**
     * 获取部门职员列表
     *
     * <p>
     * 当前实体存储了 {@link DepartmentEmployee} 集合, 从该集合的每一项中可以获取 {@link Employee} 实体,
     * 该方法即通过一个 mapping 将 {@link DepartmentEmployee} 集合转换为 {@link Employee} 集合
     * </p>
     *
     * @return 返回 {@link Employee} 类型的 {@link List} 集合对象
     */
    public List<Employee> getEmployees() {
        return departmentEmployees.stream()
            // 将集合的每一项 mapping 为 Employee 类型实体对象
            .map(DepartmentEmployee::getEmployee)
            // 将 mapping 的结果重新转为 List 集合
            .toList();
    }

    /**
     * 给当前表示部门的实体添加一个子部门实体
     *
     * <p>
     * 向 {@link Department#children} 集合中添加一个 {@link Department} 实体对象,
     * 被添加的实体表示当前实体的子部门
     * </p>
     *
     * <p>
     * 由于 {@link Department#children} 集合上的
     * {@link OneToMany @OneToMany} 注解的 {@code cascade} 参数包含
     * {@link CascadeType#PERSIST} 选项, 所以添加到集合的实体对象会被级联持久化
     * </p>
     *
     * @param subDepartment 子部门实体对象
     */
    public void addSubDepartment(Department subDepartment) {
        // 设置子部门的上一级部门为当前实体对象
        subDepartment.setParent(this);

        // 将子部门添加到当前对象作为下一级部门
        this.children.add(subDepartment);
    }

    /**
     * 添加部门职员
     *
     * @param employee 表示职员 {@link Employee} 的实体对象
     */
    public DepartmentEmployee addEmployee(Employee employee) {
        // 创建中间实体, 表示部门和职员的对应关系
        var relationship = new DepartmentEmployee();

        // 为中间实体设置关联关系
        relationship.setEmployee(employee);
        relationship.setDepartment(this);

        // 将关联关系添加到集合
        departmentEmployees.add(relationship);
        return relationship;
    }
}
