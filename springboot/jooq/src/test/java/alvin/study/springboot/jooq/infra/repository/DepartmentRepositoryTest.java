package alvin.study.springboot.jooq.infra.repository;

import alvin.study.springboot.jooq.IntegrationTest;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord;
import alvin.study.springboot.jooq.infra.repository.common.BaseRepository;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static alvin.study.springboot.jooq.infra.model.public_.Tables.DEPARTMENT;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link DepartmentRepository} 类型对
 * {@link alvin.study.springboot.jooq.infra.model.public_.Tables#DEPARTMENT DEPARTMENT} 表进行增删查改操作
 */
class DepartmentRepositoryTest extends IntegrationTest {
    // 注入 Repository 对象
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentEmployeeRepository departmentEmployeeRepository;

    /**
     * 测试实体对象持久化操作
     *
     * <p>
     * 通过 {@link BaseRepository#newRecord(java.util.function.Consumer) BaseRepository.newRecord(Consumer)} 方法产生一个
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord DepartmentRecord} 持久化对象
     * </p>
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord#store()
     * DepartmentRecord.store()} 方法对实体对象进行持久化操作
     * </p>
     */
    @Test
    @Transactional
    void store_shouldInsertRecord() {
        // 持久化实体对象
        var rec = departmentRepository.newRecord(r -> r.setName("RD").store());
        then(rec.getId()).isNotNull();

        // 确认实体对象已被持久化
        var mayDepartment = departmentRepository.selectById(rec.getId());
        then(mayDepartment).isPresent().get().extracting("name").isEqualTo("RD");
    }

    /**
     * 测试实体对象更新操作
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord#update()
     * DepartmentRecord.update()} 方法对实体对象进行更新操作
     * </p>
     */
    @Test
    @Transactional
    void update_shouldUpdateRecord() {
        // 持久化实体对象
        var rec = departmentRepository.newRecord(r -> r.setName("RD").store());
        then(rec.getId()).isNotNull();

        // 更新实体字段
        var n = rec.setName("RD-New").update();
        then(n).isOne();

        // 确认实体对象已被更新
        var mayDepartment = departmentRepository.selectById(rec.getId());
        then(mayDepartment).isPresent().get().extracting("name").isEqualTo("RD-New");
    }

    /**
     * 测试实体对象删除操作
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord#delete()
     * DepartmentRecord.delete()} 方法对实体对象进行删除操作
     * </p>
     */
    @Test
    @Transactional
    void delete_shouldDeleteRecord() {
        // 持久化实体对象
        var rec = departmentRepository.newRecord(r -> r.setName("RD").store());
        then(rec.getId()).isNotNull();

        // 删除实体对象
        rec.delete();

        // 确认实体对象已被删除
        var mayDepartment = departmentRepository.selectById(rec.getId());
        then(mayDepartment).isEmpty();
    }

    /**
     * 测试编程式事务处理方法
     *
     * <p>
     * {@link DSLContext#transaction(org.jooq.TransactionalRunnable)
     * DSLContext.transaction(TransactionalRunnable)} 方法和
     * {@link DSLContext#transactionResult(org.jooq.TransactionalCallable)
     * DSLContext.transactionResult(TransactionalCallable)} 用于启动事务, 这两种方法都是在
     * lambda
     * 表达式中包含需要事务的代码, 后者可以在完成后返回一个结果. 在一些需要局部事务的情况下可以这样使用, 一般情况下在方法上使用
     * {@link Transactional @Transactional} 注解即可
     * </p>
     */
    @Test
    void transactionResult_shouldDSLContextTransactionWorked() {
        // 启动事务
        var rec = dsl.transactionResult(
            c -> {
                var r = c.dsl().newRecord(DEPARTMENT).setName("RD");
                r.store();
                return r;
            });

        var mayDepartment = departmentRepository.selectById(rec.getId());
        then(mayDepartment).isPresent().get().extracting("name").isEqualTo("RD");
    }

    /**
     * 测试 {@link DepartmentRepository#selectByNameWithChildren(String)} 方法,
     * 查询指定名称的部门对象 ({@link alvin.study.springboot.jooq.infra.model.public_.tables.records.DepartmentRecord
     * DepartmentRecord}) 以及关联的子部门实体对象
     */
    @Test
    @Transactional
    void selectByNameWithChildren_shouldSelectResult() {
        // 插入 1 个上级 Department 实体对象
        var parent = departmentRepository.newRecord(r -> r.setName("PARENT").store());
        var parentId = parent.getId();

        // 插入 10 个下级 Department 实体对象
        for (var i = 0; i < 10; i++) {
            var index = i;
            departmentRepository.newRecord(r -> r.setName("SUB-" + index).setParentId(parentId).store());
        }

        // 根据名称查询 Department 对象以及与其相关的 10 个下级 Department 对象
        var group = departmentRepository.selectByNameWithChildren(parent.getName());
        then(group).hasSize(1);

        // 确认返回 Map 的 Key 为上级 Department 对象
        then(group.keySet())
                .singleElement()
                .extracting("name")
                .isEqualTo("PARENT");

        // 确认返回 Map 的 Value 为下级 Department 对象集合
        then(group.values())
                .singleElement()
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .map(DepartmentRecord.class::cast)
                .hasSize(10)
                .allMatch(r -> r.getName().startsWith("SUB-") && r.getParentId().equals(parentId));
    }

    /**
     * 测试 {@link (Long)} 方法, 查询雇员和其所在的部门
     */
    @Test
    @Transactional
    void selectByIdWithEmployees_shouldSelectResult() {
        // 持久化部门实体
        var department = departmentRepository.newRecord(
            r -> r.setName("RD").store());

        // 持久化雇员实体
        var employees = new ArrayList<EmployeeRecord>();
        for (var i = 0; i < 10; i++) {
            var index = i;

            // 持久化雇员实体
            var employee = employeeRepository.newRecord(
                r -> r.setName("Employee-" + index)
                        .setEmail("alvin" + index + "@fakemail.com")
                        .setTitle("Normal")
                        .store());

            // 持久化部门雇员故关系
            departmentEmployeeRepository.newRecord(r -> r.setDepartmentId(department.getId())
                    .setEmployeeId(employee.getId())
                    .store());

            employees.add(employee);
        }

        // 查询部门以及其下雇员
        var departmentWithEmployees = departmentRepository.selectByIdWithEmployees(department.getId());
        then(departmentWithEmployees).hasSize(1);

        // 确认部门实体查询结果
        then(departmentWithEmployees.keySet())
                .singleElement()
                .extracting("id")
                .isEqualTo(department.getId());

        // 确认部门下属雇员结果
        then(departmentWithEmployees.values())
                .singleElement()
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .hasSize(10)
                .map(EmployeeRecord.class::cast)
                .extracting("id")
                .containsAll(employees.stream().map(EmployeeRecord::getId).toList());
    }
}
