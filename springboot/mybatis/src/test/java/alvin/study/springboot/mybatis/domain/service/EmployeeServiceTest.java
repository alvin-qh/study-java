package alvin.study.springboot.mybatis.domain.service;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.DepartmentBuilder;
import alvin.study.springboot.mybatis.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.mybatis.builder.EmployeeBuilder;
import alvin.study.springboot.mybatis.infra.entity.Gender;

/**
 * 测试 {@link EmployeeService} 类型
 */
class EmployeeServiceTest extends IntegrationTest {
    // 注入 EmployeeService 对象
    @Autowired
    private EmployeeService service;

    /**
     * 测试 {@link EmployeeService#findEmployeeByName(String)} 方法
     */
    @Test
    @Transactional
    void shouldFindEmployeeByName() {
        // 创建 Employee 实体对象
        var employee = newBuilder(EmployeeBuilder.class).withName("Alvin101").create();

        // 创建第一个 Department 实体对象
        var department1 = newBuilder(DepartmentBuilder.class).create();
        // 设置 Employee 和 Department 对应关系
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department1.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 创建第二个 Department 实体对象
        var department2 = newBuilder(DepartmentBuilder.class).create();
        // 设置 Employee 和 Department 对应关系
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department2.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 清除查询缓存
        clearSessionCache();

        // 根据名称查询 EmployeeDto 对象
        var mayEmployeeDto = service.findEmployeeByName("Alvin101");
        // 确认查询正确
        then(mayEmployeeDto).isPresent();
        then(mayEmployeeDto.get().getId()).isEqualTo(employee.getId());

        // 获取雇员信息字段, 该字段从数据表中的 JSON 字符串反序列化而来
        var info = mayEmployeeDto.get().getInfo();
        then(info.getGender()).isEqualTo(Gender.MALE);
        then(info.getBirthday()).hasToString("1981-03-17");
        then(info.getTelephone()).isEqualTo("13999999011");

        // 获取相关的 departmentDto 对象
        var departmentDtos = mayEmployeeDto.get().getDepartments();
        then(departmentDtos)
            .extracting("id")
            .containsExactly(department1.getId(), department2.getId());
    }
}
