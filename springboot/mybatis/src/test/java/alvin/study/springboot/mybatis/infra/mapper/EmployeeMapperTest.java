package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.DepartmentBuilder;
import alvin.study.springboot.mybatis.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.mybatis.builder.EmployeeBuilder;
import alvin.study.springboot.mybatis.infra.entity.Gender;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link EmployeeMapper} 类型
 */
class EmployeeMapperTest extends IntegrationTest {
    // 注入 Mapper 对象
    @Autowired
    private EmployeeMapper mapper;

    /**
     * 测试 {@link EmployeeMapper#selectByNameWithDepartments(String)} 方法, 查询员工和所属部门
     *
     * <p>
     * {@link EmployeeMapper#selectByNameWithDepartments(String)} 方法对应着
     * {@code classpath:/mapper/EmployeeMapper.xml} 中
     * {@code select #id="selectByNameWithDepartments"} 节点
     * </p>
     */
    @Test
    @Transactional
    void selectByNameWithDepartments_shouldGetSelectResult() {
        // 创建一个员工
        var employee = newBuilder(EmployeeBuilder.class)
            .withName("EMP-001")
            .create();

        // 创建部门 1
        var department1 = newBuilder(DepartmentBuilder.class).create();

        // 创建部门员工关系
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department1.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 创建部门 2
        var department2 = newBuilder(DepartmentBuilder.class).create();

        // 创建部门员工关系
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department2.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 清除一级缓存
        clearSessionCache();

        // 通过员工名称查询员工实体
        var mayEmployee = mapper.selectByNameWithDepartments("EMP-001");
        then(mayEmployee).isPresent();
        then(mayEmployee.get().getId()).isEqualTo(employee.getId());

        employee = mayEmployee.get();

        // 获取雇员信息字段, 该字段从数据表中的 JSON 字符串反序列化而来
        var info = employee.getInfo();
        then(info.getGender()).isEqualTo(Gender.MALE);
        then(info.getBirthday()).hasToString("1981-03-17");
        then(info.getTelephone()).isEqualTo("13999999011");

        // 获取员工所属部门列表
        var departments = employee.getDepartments();
        // 获取员工所属部门
        then(departments).extracting("id").containsExactly(department1.getId(), department2.getId());
    }

    /**
     * 测试 {@link EmployeeMapper#selectBatchNames(java.util.Collection)} 方法,
     * 根据一组员工姓名查询员工实体对象
     */
    @Test
    @Transactional
    void selectBatchNames_shouldGetSelectResults() {
        var namePrefix = "EMP-N-";

        // 创建 5 个员工实体
        for (var i = 0; i < 5; i++) {
            newBuilder(EmployeeBuilder.class).withName(namePrefix + i).create();
        }

        clearSessionCache();

        // 通过 3 个员工名称查询实体
        var employees = mapper.selectBatchNames(List.of("EMP-N-2", "EMP-N-3", "EMP-N-4"));

        // 确认查询结果包含指定的三个员工实体
        then(employees).extracting("name").containsOnly("EMP-N-2", "EMP-N-3", "EMP-N-4");
    }

    /**
     * 测试 {@link EmployeeMapper#selectBatchNames(java.util.Collection)} 方法,
     * 根据一组员工姓名查询员工实体对象
     *
     * <p>
     * 本例演示了如何进行动态 SQL 查询, 同时通过分页插件进行自动分页, 参考:
     * {@link EmployeeMapper#selectBySearch(com.baomidou.mybatisplus.core.metadata.IPage, String, String, String, String)
     * EmployeeMapper.selectBySearch(IPage, String, String, String, String)} 方法
     * </p>
     */
    @Test
    @Transactional
    void selectBySearch_shouldGetSelectResults() {
        // 持久化一个部门实体
        var department = newBuilder(DepartmentBuilder.class).withName("RD-X").create();

        var namePrefix = "EMP-N-";

        // 创建 100 个员工实体
        for (var i = 0; i < 100; i++) {
            var builder = newBuilder(EmployeeBuilder.class)
                .withName(namePrefix + i)
                .withEmail(namePrefix + i + "@fakemail.com");

            var isLeader = i % 2 == 0;
            if (isLeader) {
                // 每隔一个员工设置一个为 Leader
                builder = builder.withTitle("Leader");
            }

            // 创建员工实体
            var employee = builder.create();

            if (!isLeader) {
                // 对于非 Leader 员工, 设定其和部门的关系
                newBuilder(DepartmentEmployeeBuilder.class)
                    .withDepartmentId(department.getId())
                    .withEmployeeId(employee.getId())
                    .create();
            }
        }

        clearSessionCache();

        // 进行搜索, 取结果从第 2 页, 每页 10 条记录
        var page = mapper.selectBySearch(
            Page.of(2, 10), namePrefix, "fakemail.com", "Staff", "RD-X");

        // 确认分页结果
        then(page.getTotal()).isEqualTo(50L); // 确认查询总记录数
        then(page.getCurrent()).isEqualTo(2L); // 确认当前页码
        then(page.getPages()).isEqualTo(5L); // 确认总页数
        then(page.getSize()).isEqualTo(10L); // 确认每页记录数
        then(page.getRecords()).hasSize(10); // 确认查询结果
        then(page.getRecords().get(0).getName()).isEqualTo(namePrefix + 21);
        then(page.getRecords().get(9).getName()).isEqualTo(namePrefix + 39);
    }
}
