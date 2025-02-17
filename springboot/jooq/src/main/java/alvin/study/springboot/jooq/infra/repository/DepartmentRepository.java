package alvin.study.springboot.jooq.infra.repository;

import static alvin.study.springboot.jooq.infra.model.public_.Tables.DEPARTMENT;
import static alvin.study.springboot.jooq.infra.model.public_.Tables.DEPARTMENT_EMPLOYEE;
import static alvin.study.springboot.jooq.infra.model.public_.Tables.EMPLOYEE;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord;
import alvin.study.springboot.jooq.infra.repository.common.BaseRepository;

/**
 * 操作 {@link alvin.study.springboot.jooq.infra.model.public_.tables.Department} 部门实体
 */
@Repository
public class DepartmentRepository extends BaseRepository<DepartmentRecord> {
    /**
     * 构造器, 设置对应的 {@link org.jooq.Table Table} 对象
     */
    protected DepartmentRepository() {
        super(DEPARTMENT);
    }

    /**
     * 根据 {@code id} 查询部门实体对象
     *
     * @param id 实体主键
     * @return {@link Optional} 对象, 内部为 {@link DepartmentRecord} 类型对象
     */
    public Optional<DepartmentRecord> selectById(Long id) {
        // 查询员工记录所有字段
        var departments = dsl()
                .select()
                .from(DEPARTMENT)
                // 查询条件
                .where(DEPARTMENT.ID.eq(id))
                .fetch()
                // 返回结果转换
                .into(DepartmentRecord.class);

        return asOptional(departments);
    }

    /**
     * 通过名称查询部门和子部门
     *
     * <p>
     * 该方法演示了一个自连接查询, 通过 {@code id} 和 {@code parent_id} 字段形成部门的上下级关系
     * </p>
     *
     * @param name 要查询的部门名称
     * @return 查询结果, 为一个 Map 对象, Key 是 1 对 n 关系中的 1, Value 是 n
     */
    public Map<DepartmentRecord, List<DepartmentRecord>> selectByNameWithChildren(String name) {
        // 为要进行连接的两个表设置别名
        var p1 = DEPARTMENT.as("p1");
        var p2 = DEPARTMENT.as("p2");

        return dsl()
                // 设置要查询的字段
                .select(p1.fields())
                .select(p2.fields())
                // 设置要查询的表, 连接表和连接条件
                .from(p1)
                .join(p2).on(p1.ID.eq(p2.PARENT_ID))
                // 设置查询条件
                .where(p1.NAME.eq(name))
                // 设置排序方式
                .orderBy(p1.ID.desc())
                // 设置 Mapping 方式
                .fetchGroups(
                    // Key 的 mapping 方式
                    r -> r.into(p1).into(DepartmentRecord.class),
                    // Value 的 mapping 方式
                    r -> r.into(p2).into(DepartmentRecord.class));
    }

    /**
     * 通过 ID 查询部门实体以及部门下面的雇员集合
     *
     * @param id 要查询的部门 ID
     * @return 查询结果, 为一个 Map 对象, Key 是 1 对 n 关系中的 1, Value 是 n
     */
    public Map<DepartmentRecord, List<EmployeeRecord>> selectByIdWithEmployees(Long id) {
        // 为要进行连接的两个表设置别名
        var d = DEPARTMENT.as("d");
        var e = EMPLOYEE.as("e");
        var de = DEPARTMENT_EMPLOYEE.as("de");

        return dsl()
                // 设置要查询的字段
                .select(d.fields())
                .select(e.fields())
                // 设置要查询的表, 连接表和连接条件
                .from(d)
                .join(de).on(d.ID.eq(de.DEPARTMENT_ID))
                .join(e).on(e.ID.eq(de.EMPLOYEE_ID))
                // 设置查询条件
                .where(d.ID.eq(id))
                // 设置排序方式
                // 设置 Mapping 方式
                .fetchGroups(
                    // Key 的 mapping 方式
                    r -> r.into(d).into(DepartmentRecord.class),
                    // Value 的 mapping 方式
                    r -> r.into(e).into(EmployeeRecord.class));
    }
}
