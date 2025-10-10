package alvin.study.springboot.jpa.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.builder.EmployeeBuilder;
import alvin.study.springboot.jpa.builder.OrgBuilder;
import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.Employee;
import alvin.study.springboot.jpa.infra.entity.Org;
import alvin.study.springboot.jpa.infra.repository.ext.EmployeeRepositoryExt;

/**
 * 测试 {@link EmployeeRepository} 的增删查改操作
 */
class EmployeeRepositoryTest extends IntegrationTest {
    // 注入存储操作对象
    @Autowired
    private EmployeeRepository repository;

    /**
     * 测试 {@link EmployeeRepository#findAll()} 方法
     *
     * <p>
     * 该方法的语义是获取所有的 {@link Employee Employee} 实体对象,
     * 但在多租户 Filter 的作用下会在查询上增加 {@code org_id=:orgId} 条件, 结果是只会查询到当前租户下的所有实体对象
     * </p>
     */
    @Test
    @Transactional
    void findAll_shouldFindAllEntities() {
        // 创建一个新的组织实体作为新租户
        Org org = newBuilder(OrgBuilder.class).create();

        // 在新建的租户下创建 10 个雇员实体
        for (var i = 0; i < 10; i++) {
            newBuilder(EmployeeBuilder.class).withOrgId(org.getId()).create();
        }
        flushEntityManager();

        // 上下文切换到新租户
        try (var _ = switchContext(org, null)) {
            var employees = repository.findAll();
            then(employees).hasSize(10);

            // 确认查询到的 10 个雇员实体对象是按 id 顺序升序排序
            long lastId = 0;
            for (var employee : employees) {
                then(employee.getId()).isGreaterThan(lastId);
                lastId = employee.getId();
            }
        }

        // 切换回原租户后, 在此查询所有的雇员实体对象
        var employees = repository.findAll();
        // 确认此次查询未查询到任何实体对象
        then(employees).isEmpty();
    }

    /**
     * 测试 {@link EmployeeRepository#findEmployeesByDepartment(Department)} 方法, 该方法通过
     * JPQL 完成查询
     *
     * <p>
     * 该方法由 {@link EmployeeRepositoryExt EmployeeRepositoryExt} 接口引入, 且该接口被
     * {@link EmployeeRepositoryExt.EmployeeRepositoryExtImpl
     * EmployeeExtensionRepository.EmployeeExtensionRepositoryImpl} 类实现, JPA 可以根据引入的接口, 自动找寻到实现类,
     * 并执行其中定义的方法
     * </p>
     *
     * <p>
     * 这种引入接口和实现类的方式可以扩充 JPA 根据接口自动生成实现类的方式, 可以定义一些复杂的, 甚至原生 SQL 的实现
     * </p>
     */
    @Test
    @Transactional
    void findEmployeesByDepartment_shouldFindEntities() {
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
        // 确认查询到 1 个雇员, 且确实在该部门
        then(results).containsExactly(employees.get(n));
    }

    /**
     * 测试 {@link EmployeeRepository#findEmployeesByDepartmentNative(Department)} 方法,
     * 该方法通过 NativeSQL 完成查询
     *
     * <p>
     * 该方法由 {@link EmployeeRepositoryExt
     * EmployeeExtensionRepository} 接口引入, 且该接口被
     * {@link EmployeeRepositoryExt.EmployeeRepositoryExtImpl
     * EmployeeExtensionRepository.EmployeeExtensionRepositoryImpl} 类实现, JPA
     * 可以根据引入的接口, 自动找寻到实现类, 并执行其中定义的方法
     * </p>
     *
     * <p>
     * 这种引入接口和实现类的方式可以扩充 JPA 根据接口自动生成实现类的方式, 可以定义一些复杂的, 甚至原生 SQL 的实现
     * </p>
     */
    @Test
    @Transactional
    void findEmployeesByDepartmentNative_shouldFindEntities() {
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
        var results = repository.findEmployeesByDepartmentNative(departments.get(n));
        // 确认查询到 1 个雇员, 且确实在该部门
        then(results).containsExactly(employees.get(n));
    }
}
