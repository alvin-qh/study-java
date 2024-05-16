package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.DepartmentBuilder;
import alvin.study.springboot.mybatis.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.mybatis.builder.EmployeeBuilder;
import alvin.study.springboot.mybatis.infra.entity.Department;
import alvin.study.springboot.mybatis.infra.entity.Employee;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link DepartmentMapper} 类型
 */
class DepartmentMapperTest extends IntegrationTest {
    // 注入 Mapper 对象
    @Autowired
    private DepartmentMapper mapper;

    // 注入 ModelMapper 对象
    @Autowired
    private ModelMapper modelMapper;

    /**
     * 测试 {@link DepartmentMapper#selectByNameWithParentAndChildren(String)} 方法,
     * 查询部门和部门的上下级部门
     *
     * <p>
     * {@link DepartmentMapper#selectByNameWithParentAndChildren(String)} 方法对应着
     * {@code classpath:/mapper/DepartmentMapper.xml} 中
     * {@code select #id="selectByNameWithParentAndChildren"} 节点
     * </p>
     */
    @Test
    @Transactional
    void selectByNameWithParentAndChildren_shouldGetSelectResult() {
        // 创建一个上级部门
        var parent = newBuilder(DepartmentBuilder.class)
            .withName("Parent")
            .create();

        // 创建一个下级部门
        var child = newBuilder(DepartmentBuilder.class)
            .withName("Child")
            .withParent(parent)
            .create();

        // 清除一级缓存
        clearSessionCache();

        // 通过部门名称为 "Parent" 进行查询
        var mayDepartment = mapper.selectByNameWithParentAndChildren("Parent");
        then(mayDepartment).isPresent();

        var department = mayDepartment.get();

        then(department.getId()).isEqualTo(parent.getId());
        // 确认该部门没有上级部门
        then(department.getParent()).isNull();
        // 确认该部门有一个下级部门
        then(department.getChildren()).hasSize(1);
        // 确认该下级部门
        then(department.getChildren()).extracting("id").containsExactly(child.getId());

        // 通过部门名称为 "Child" 进行查询
        mayDepartment = mapper.selectByNameWithParentAndChildren("Child");
        then(mayDepartment).isPresent();

        department = mayDepartment.get();
        // 确认查询的部门
        then(department.getId()).isEqualTo(child.getId());
        // 确认该部门有一个上级部门
        then(department.getParent().getId()).isEqualTo(parent.getId());
        // 确认该部门没有下级部门
        then(department.getChildren()).isEmpty();
    }

    /**
     * 测试 {@link DepartmentMapper#selectByNameWithEmployees(String)} 方法,
     * 查询部门和部门包含的员工列表
     *
     * <p>
     * {@link DepartmentMapper#selectByName(String)} 方法对应着
     * {@code classpath:/mapper/DepartmentMapper.xml} 中
     * {@code select #id="selectByNameWithEmployees"} 节点
     * </p>
     */
    @Test
    @Transactional
    void selectByNameWithEmployees_shouldGetSelectResult() {
        // 创建一个部门
        var department = newBuilder(DepartmentBuilder.class)
            .withName("DEPT-1")
            .create();

        for (var i = 0; i < 10; i++) {
            // 创建一个员工
            var employee = newBuilder(EmployeeBuilder.class)
                .withName("EMP-" + i)
                .create();

            // 将员工和部门关联
            newBuilder(DepartmentEmployeeBuilder.class)
                .withDepartmentId(department.getId())
                .withEmployeeId(employee.getId())
                .create();
        }

        // 清除一级缓存
        clearSessionCache();

        // 根据名称查询部门及其相关员工
        var mayDepartment = mapper.selectByNameWithEmployees("DEPT-1");
        then(mayDepartment).isPresent();
        then(mayDepartment.get().getId()).isEqualTo(department.getId());

        department = mayDepartment.get();

        // 查询部门相关的员工
        then(department.getEmployees()).hasSize(10);
        for (var i = 0; i < department.getEmployees().size(); i++) {
            then(department.getEmployees().get(i).getName()).isEqualTo("EMP-" + i);
        }
    }

    /**
     * 测试 {@link DepartmentMapper#selectByName(String)} 方法
     *
     * <p>
     * 该方法演示了如何配合 {@link org.apache.ibatis.annotations.Select @Select} 注解配合
     * {@link org.apache.ibatis.annotations.ResultMap @ResultMap} 注解通过
     * {@code mapper/DepartmentMapper.xml} 中定义的 {@code resultMap} 标签进行字段映射
     * </p>
     */
    @Test
    @Transactional
    void selectByName_shouldGetSelectResult() {
        // 创建一个部门
        var department = newBuilder(DepartmentBuilder.class)
            .withName("DEPT-1")
            .create();

        // 根据部门名称进行查询
        var mayDepartment = mapper.selectByName("DEPT-1");
        then(mayDepartment).isPresent();

        // 确认查询结果
        department = mayDepartment.get();
        then(department.getId()).isEqualTo(department.getId());
        then(department.getName()).isEqualTo("DEPT-1");
    }

    /**
     * 测试
     * {@link DepartmentMapper#selectByEmployee(Employee)
     * DepartmentMapper.selectByEmployee(Employee)} 方法
     *
     * <p>
     * 该方法演示了如何通过 {@link com.baomidou.mybatisplus.extension.toolkit.SqlRunner
     * SqlRunner} 类型配合 {@link org.apache.ibatis.jdbc.SQL SQL} 类型进行查询
     * </p>
     *
     * <p>
     * {@link DepartmentMapper#selectByEmployee(Employee)
     * DepartmentMapper.selectByEmployee(Employee)} 方法返回结果为
     * {@code Map<String, Object>} 类型对象, 本测试中通过 {@link ModelMapper} 将 {@code Map}
     * 对象转为 {@link Department} 对象, 参考 {@code ModelMapperConfig.modelMapper()} 方法中对于 {@code ModelMapper}
     * 对象的设置
     * </p>
     */
    @Test
    @Transactional
    @SuppressWarnings("unchecked")
    void selectByEmployee_shouldGetSelectResult() {
        // 创建一个员工实体
        var employee = newBuilder(EmployeeBuilder.class).create();

        // 创建第一个部门实体
        var department1 = newBuilder(DepartmentBuilder.class).create();
        // 关联员工和部门实体
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department1.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 创建第二个部门实体
        var department2 = newBuilder(DepartmentBuilder.class).create();
        // 关联员工和部门实体
        newBuilder(DepartmentEmployeeBuilder.class)
            .withDepartmentId(department2.getId())
            .withEmployeeId(employee.getId())
            .create();

        // 根据员工查询相关的部门信息 Map 对象列表
        var results = mapper.selectByEmployee(employee);
        then(results).hasSize(2);

        var resultType = new TypeToken<List<Department>>() { }.getType();
        // 通过 ModelMapper 将 Map 对象转化为 Department 对象
        var departments = (List<Department>) modelMapper.map(results, resultType);

        // 确认获取的对象正确
        then(departments).extracting("id").containsExactly(department1.getId(), department2.getId());
    }
}
