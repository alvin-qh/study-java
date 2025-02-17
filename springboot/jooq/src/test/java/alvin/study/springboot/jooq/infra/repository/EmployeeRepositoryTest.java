package alvin.study.springboot.jooq.infra.repository;

import alvin.study.springboot.jooq.IntegrationTest;
import alvin.study.springboot.jooq.infra.model.EmployeeInfo;
import alvin.study.springboot.jooq.infra.model.Gender;
import alvin.study.springboot.jooq.infra.repository.common.BaseRepository;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link EmployeeRepository} 类型对
 * {@link alvin.study.springboot.jooq.infra.model.public_.tables.Employee#EMPLOYEE EMPLOYEE} 表进行增删查改操作
 */
class EmployeeRepositoryTest extends IntegrationTest {
    // 注入 Repository 对象
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentEmployeeRepository departmentEmployeeRepository;

    /**
     * 测试实体对象持久化操作
     *
     * <p>
     * 通过
     * {@link BaseRepository#newRecord(java.util.function.Consumer)
     * BaseRepository.newRecord(Consumer)} 方法产生一个
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord EmployeeRecord} 持久化对象
     * </p>
     *
     * <p>
     * 通过
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord#store()
     * EmployeeRecord.store()} 方法对实体对象进行持久化操作
     * </p>
     */
    @Test
    @Transactional
    void store_shouldInsertRecord() {
        // 持久化实体对象
        var rec = employeeRepository.newRecord(
            e -> e.setName("Alvin")
                    .setEmail("alvin@fakemail.com")
                    .setTitle("Manager")
                    .store());
        then(rec.getId()).isNotNull();

        // 确认实体对象已被持久化
        var mayEmployee = employeeRepository.selectById(rec.getId());
        then(mayEmployee).isPresent().get().matches(
            r -> Objects.equals(r.getName(), "Alvin")
                 && Objects.equals(r.getEmail(), "alvin@fakemail.com")
                 && Objects.equals(r.getTitle(), "Manager"));
    }

    /**
     * 测试实体对象更新操作
     *
     * <p>
     * 通过
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord#update()
     * EmployeeRecord.update()} 方法对实体对象进行更新操作
     * </p>
     */
    @Test
    @Transactional
    void update_shouldUpdateRecord() {
        // 持久化实体对象
        var rec = employeeRepository.newRecord(
            e -> e.setName("Alvin")
                    .setEmail("alvin@fakemail.com")
                    .setTitle("Manager")
                    .store());
        then(rec.getId()).isNotNull();

        // 更新实体字段
        var n = rec.setName("Emma").setEmail("emma@fakemail.com").update();
        then(n).isOne();

        // 确认实体对象已被更新
        var mayEmployee = employeeRepository.selectById(rec.getId());
        then(mayEmployee).isPresent().get().matches(
            r -> Objects.equals(r.getName(), "Emma")
                 && Objects.equals(r.getEmail(), "emma@fakemail.com")
                 && Objects.equals(r.getTitle(), "Manager"));
    }

    /**
     * 测试实体对象删除操作
     *
     * <p>
     * 通过
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord#delete()
     * EmployeeRecord.delete()} 方法对实体对象进行删除操作
     * </p>
     */
    @Test
    @Transactional
    void delete_shouldDeleteRecord() {
        // 持久化实体对象
        var rec = employeeRepository.newRecord(
            e -> e.setName("Alvin")
                    .setEmail("alvin@fakemail.com")
                    .setTitle("Manager")
                    .store());
        then(rec.getId()).isNotNull();

        // 删除实体对象
        rec.delete();

        // 确认实体对象已被删除
        var mayEmployee = employeeRepository.selectById(rec.getId());
        then(mayEmployee).isEmpty();
    }

    /**
     * 测试 {@link EmployeeRepository#selectByIdWithDepartments(Long)} 方法, 查询雇员和其所在的部门
     */
    @Test
    @Transactional
    void selectByIdWithDepartments_shouldSelectResult() {
        // 持久化雇员实体
        var employee = employeeRepository.newRecord(
            r -> r.setName("Alvin")
                    .setEmail("alvin@fakemail.com")
                    .setTitle("Manager")
                    .store());

        // 持久化部门实体
        var department1 = departmentRepository.newRecord(r -> r.setName("Department1").store());

        // 持久化部门雇员故关系
        departmentEmployeeRepository.newRecord(
            r -> r.setDepartmentId(department1.getId()).setEmployeeId(employee.getId()).store());

        // 持久化部门实体
        var department2 = departmentRepository.newRecord(r -> r.setName("Department2").store());

        // 持久化部门雇员故关系
        departmentEmployeeRepository.newRecord(
            r -> r.setDepartmentId(department2.getId()).setEmployeeId(employee.getId()).store());

        // 查询雇员以及雇员所在的部门
        var employeeWithDepartments = employeeRepository.selectByIdWithDepartments(employee.getId());
        then(employeeWithDepartments).hasSize(1);

        // 确认雇员实体查询结果
        then(employeeWithDepartments.keySet())
                .singleElement()
                .extracting("id")
                .isEqualTo(employee.getId());

        // 确认雇员所属部门结果
        then(employeeWithDepartments.values())
                .singleElement()
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .extracting("id")
                .contains(department1.getId(), department2.getId());
    }

    /**
     * 测试
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord#setInfo(EmployeeInfo)
     * EmployeeRecord.setInfo(EmployeeInfo)} 和
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.EmployeeRecord#getInfo()
     * EmployeeRecord.getInfo()} 方法, 通过 JSON 方式设置数据表字段
     */
    @Test
    @Transactional
    void employeeInfo_shouldInsertAndSelect() {
        // 持久化实体对象
        var rec = employeeRepository.newRecord(
            e -> e.setName("Alvin")
                    .setEmail("alvin@fakemail.com")
                    .setTitle("Manager")
                    // 设置 EmployeeInfo 类型字段, 该字段为 JSON 格式, 会通过 EmployeeInfoConverter 类型给予转换
                    .setInfo(new EmployeeInfo()
                            .setGender(Gender.MALE)
                            .setBirthday(LocalDate.of(1981, 3, 17))
                            .setTelephone("13999999911"))
                    .store());

        // 确认 JSON 字符串在读取时可以转为 EmployeeInfo 类型字段
        var mayEmployee = employeeRepository.selectById(rec.getId());
        then(mayEmployee).isPresent().get().matches(
            r -> r.getInfo() != null
                 && Objects.equals(r.getInfo().getGender(), Gender.MALE)
                 && Objects.equals(r.getInfo().getBirthday().toString(), "1981-03-17")
                 && Objects.equals(r.getInfo().getTelephone(), "13999999911"));
    }
}
