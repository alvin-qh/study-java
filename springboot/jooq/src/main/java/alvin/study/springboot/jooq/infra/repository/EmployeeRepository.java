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
 * 操作 {@code EMPLOYEE} 表实体的 Repository 类型
 */
@Repository
public class EmployeeRepository extends BaseRepository<EmployeeRecord> {
    /**
     * 构造器, 指定当前实体对应的 {@link org.jooq.Table Table} 类型
     */
    public EmployeeRepository() {
        super(EMPLOYEE);
    }

    /**
     * 根据 {@code id} 查询实体对象
     *
     * @param id 实体主键
     * @return {@link Optional} 对象, 内部为 {@link EmployeeRecord} 类型对象
     */
    public Optional<EmployeeRecord> selectById(Long id) {
        // 查询员工记录所有字段
        var employees = dsl()
                .select()
                .from(EMPLOYEE)
                // 查询条件
                .where(EMPLOYEE.ID.eq(id))
                .fetch()
                // 返回结果转换
                .into(EmployeeRecord.class);

        return asOptional(employees);
    }

    /**
     * 根据 {@code id} 查询实体对象
     *
     * @param id 实体主键
     * @return {@link Optional} 对象, 内部为 {@link EmployeeRecord} 类型对象
     */
    public Map<EmployeeRecord, List<DepartmentRecord>> selectByIdWithDepartments(Long id) {
        var e = EMPLOYEE.as("e");
        var d = DEPARTMENT.as("d");
        var de = DEPARTMENT_EMPLOYEE.as("de");

        // 查询员工部门关系表
        return dsl()
                .select(e.fields())
                .select(d.fields())
                .from(e)
                // 设置连接表和连接条件
                .join(de).on(e.ID.eq(de.EMPLOYEE_ID))
                .join(d).on(d.ID.eq(de.DEPARTMENT_ID))
                // 查询条件
                .where(e.ID.eq(id))
                .fetchGroups(
                    // Key 的 mapping 方式
                    r -> r.into(e).into(EmployeeRecord.class),
                    // Value 的 mapping 方式
                    r -> r.into(d).into(DepartmentRecord.class));
    }
}
