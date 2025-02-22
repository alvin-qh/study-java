package alvin.study.springboot.jpa.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.builder.EmployeeBuilder;
import alvin.study.springboot.jpa.builder.OrgBuilder;
import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.DepartmentEmployee;
import alvin.study.springboot.jpa.infra.entity.Employee;
import alvin.study.springboot.jpa.infra.entity.Org;

/**
 * 测试 {@link DepartmentRepository} 的增删查改操作
 */
class DepartmentEmployeeRepositoryTest extends IntegrationTest {
    @Autowired
    private DepartmentEmployeeRepository repository;

    /**
     * 测试 {@link DepartmentEmployeeRepository#findAll()} 方法
     *
     * <p>
     * 该方法的语义是获取所有的 {@link DepartmentEmployee
     * DepartmentEmployee} 实体对象, 但在多租户 Filter 的作用下会在查询上增加 {@code org_id=:orgId} 条件,
     * 结果是只会查询到当前租户下的所有实体对象
     * </p>
     */
    @Test
    @Transactional
    void findAll_shouldFindAllEntities() {
        // 创建一个新的组织实体作为新租户
        Org org = newBuilder(OrgBuilder.class).create();

        var employees = new HashSet<Employee>();
        var departments = new HashSet<Department>();

        // 在新建的租户下创建 10 个雇员实体和 10 个部门实体, 并建立两个实体的关系, 产生 DepartmentEmployee 实体对象
        for (var i = 0; i < 10; i++) {
            var employee = newBuilder(EmployeeBuilder.class).name("EMP_" + i).withOrgId(org.getId()).create();
            var department = newBuilder(DepartmentBuilder.class).name("DEP_" + i).withOrgId(org.getId()).create();

            // 建立 employee 和 department 的关系并返回 DepartmentEmployee 实体对象, 为其设置 orgId 属性
            department.addEmployee(employee).setOrgId(org.getId());

            employees.add(employee);
            departments.add(department);
        }
        flushEntityManager();

        // 上下文切换到新租户
        try (var ignore = switchContext(org, null)) {
            var departmentEmployees = repository.findAll();
            then(departmentEmployees).hasSize(10);

            for (var departmentEmployee : departmentEmployees) {
                // 确认查询到的 DepartmentEmployee 对象的 employee 和 department 在所设置的范围内
                then(departmentEmployee.getDepartment()).isIn(departments);
                then(departmentEmployee.getEmployee()).isIn(employees);
            }
        }

        // 切换回原租户后, 在此查询所有的 DepartmentEmployee 实体对象
        var departmentEmployees = repository.findAll();
        // 确认此次查询未查询到任何实体对象
        then(departmentEmployees).isEmpty();
    }

    /**
     * 测试 {@link DepartmentEmployeeRepository#findEmployeesByDepartment(Department)}
     * 方法
     *
     * <p>
     * 该方法通过 JPQL 而非 JPA 自动生成的语句执行查询
     * </p>
     */
    @Test
    @Transactional
    void findEmployeesByDepartment_shouldFindEntity() {
        // 产生两个列表集合, 在相同下标处存储对应的部门和雇员实体
        var employees = new ArrayList<Employee>();
        var departments = new ArrayList<Department>();

        // 在新建的租户下创建 10 个雇员实体
        for (var i = 0; i < 10; i++) {
            var employee = newBuilder(EmployeeBuilder.class).name("EMP_" + i).create();
            var department = newBuilder(DepartmentBuilder.class).name("DEP_" + i).create();
            // 建立 employee 和 department 的关系并返回 DepartmentEmployee 实体对象, 为其设置 orgId 属性
            department.addEmployee(employee);

            employees.add(employee);
            departments.add(department);
        }
        flushEntityManager();

        // 产生一个随机数, 用于随机挑选相关的部门和雇员
        var n = new Random().nextInt(employees.size());

        // 根据随机挑选的部门查询相关的雇员
        var results = repository.findEmployeesByDepartment(departments.get(n));
        // 确认查询到 1 个雇员
        then(results).hasSize(1);
        // 确认查询到的雇员确实在该部门
        then(results.get(0)).isEqualTo(employees.get(n));
    }
}
