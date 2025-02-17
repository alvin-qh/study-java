package alvin.study.springboot.kickstart.app.api;

import alvin.study.springboot.kickstart.IntegrationTest;
import alvin.study.springboot.kickstart.app.api.mutation.EmployeeMutation;
import alvin.study.springboot.kickstart.app.api.query.EmployeeQuery;
import alvin.study.springboot.kickstart.app.api.schema.input.EmployeeInput;
import alvin.study.springboot.kickstart.builder.DepartmentBuilder;
import alvin.study.springboot.kickstart.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.kickstart.builder.EmployeeBuilder;
import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.entity.Employee;
import alvin.study.springboot.kickstart.util.collection.PathMap;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

/**
 * 测试 {@link EmployeeQuery EmployeeQuery} 和
 * {@link EmployeeMutation EmployeeMutation} 类型
 *
 * <p>
 * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-employee.graphql} 文件中定义
 * </p>
 */
class EmployeeTest extends IntegrationTest {
    /**
     * 测试 {@link EmployeeQuery#employee(String)
     * EmployeeQuery.employee(String)} 方法, 根据 id 查询雇员信息
     */
    @Test
    void employee_shouldQueryById() throws IOException {
        // 创建待查询的雇员实体
        Employee employee;
        // 创建员工所属部门
        var departments = new ArrayList<Department>();
        try (var ignore = beginTx(false)) {
            employee = newBuilder(EmployeeBuilder.class)
                    .withInfo(Map.of(
                        "gender", "M",
                        "birthday", LocalDate.parse("1981-03-17")))
                    .create();
            departments.add(newBuilder(DepartmentBuilder.class).create());
            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee.getId())
                    .withDepartmentId(departments.get(0).getId())
                    .create();

            departments.add(newBuilder(DepartmentBuilder.class).create());
            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee.getId())
                    .withDepartmentId(departments.get(1).getId())
                    .create();
        }

        // 创建查询参数
        var vars = valueToTree("id", String.valueOf(employee.getId()));

        // @formatter:off
        // 执行查询操作并确认结果
        graphql("test-employee", "queryEmployee", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.employee")
                .as(PathMap.class)
                .matches(v -> v.get("name").equals(employee.getName()), "name")
                .matches(v -> v.getByPath("org.id").equals(employee.getOrgId().toString()), "org")
                .matches(v -> v.getByPath("createdByUser.account").equals(currentUser().getAccount()), "createdByUser.account")
                .matches(v -> v.getByPath("updatedByUser.account").equals(currentUser().getAccount()), "updatedByUser.account")
                .matches(v -> v.getByPath("departments[0].id").equals(departments.get(0).getId().toString()), "departments[0].id")
                .matches(v -> v.getByPath("departments[0].name").equals(departments.get(0).getName()), "departments[0].name")
                .matches(v -> v.getByPath("departments[1].id").equals(departments.get(1).getId().toString()), "departments[1].id")
                .matches(v -> v.getByPath("departments[1].name").equals(departments.get(1).getName()), "departments[1].name");
        // @formatter:on
    }

    /**
     * 测试
     * {@link EmployeeMutation#createEmployee(EmployeeInput)
     * EmployeeMutation.createEmployee(EmployeeInput)} 方法, 创建一个雇员实体
     */
    @Test
    void createEmployee_shouldMutationExecute() throws IOException {
        // 创建员工所属部门
        var departments = new ArrayList<Department>();
        try (var ignore = beginTx(false)) {
            departments.add(newBuilder(DepartmentBuilder.class).create());
            departments.add(newBuilder(DepartmentBuilder.class).create());
        }

        // 构建输入参数对象
        var input = EmployeeInput.builder()
                .name("Alvin")
                .email("alvin-employee@fakemail.com")
                .title("Manager")
                .info(Map.of(
                    "gender", "M",
                    "birthday", LocalDate.parse("1981-03-17")))
                .departmentIds(departments.stream().map(Department::getId).toList())
                .build();

        // 构建变更参数
        var vars = valueToTree("input", input);

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-employee", "createEmployee", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.createEmployee.employee")
                .as(PathMap.class)
                .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
                .matches(v -> v.get("name").equals(input.getName()), "name")
                .matches(v -> v.get("title").equals(input.getTitle()), "title")
                .matches(v -> v.getByPath("info.gender").equals(input.getInfo().get("gender")), "info.gender")
                .matches(v -> LocalDate.parse(v.getByPath("info.birthday")).equals(LocalDate.parse("1981-03-17")), "info.birthday")
                .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
                .matches(v -> v.getByPath("createdByUser.id").equals(currentUser().getId().toString()), "createdByUser.id")
                .matches(v -> v.getByPath("updatedByUser.id").equals(currentUser().getId().toString()), "updatedByUser.id")
                .matches(v -> v.getByPath("departments[0].id").equals(departments.get(0).getId().toString()), "departments[0].id")
                .matches(v -> v.getByPath("departments[0].name").equals(departments.get(0).getName()), "departments[0].name")
                .matches(v -> v.getByPath("departments[1].id").equals(departments.get(1).getId().toString()), "departments[1].id")
                .matches(v -> v.getByPath("departments[1].name").equals(departments.get(1).getName()), "departments[1].name");
        // @formatter:on
    }

    /**
     * 测试
     * {@link EmployeeMutation#updateEmployee(String, EmployeeInput)
     * EmployeeMutation.updateEmployee(String, UserInput)} 方法, 更新一个雇员实体
     */
    @Test
    void updateEmployee_shouldMutationExecute() throws IOException {
        // 创建待更新雇员实体
        Employee employee;
        var departments = new ArrayList<Department>();
        try (var ignore = beginTx(false)) {
            // 创建员工
            employee = newBuilder(EmployeeBuilder.class).create();

            // 创建员工所属部门
            departments.add(newBuilder(DepartmentBuilder.class).create());
            departments.add(newBuilder(DepartmentBuilder.class).create());
        }

        // 构建变更输入对象
        var input = EmployeeInput.builder()
                .name("Alvin")
                .email("alvin-employee@fakemail.com")
                .title("Manager")
                .info(Map.of(
                    "gender", "M",
                    "birthday", LocalDate.parse("1981-03-17")))
                .departmentIds(departments.stream().map(Department::getId).toList())
                .build();

        // 构建变更参数
        var vars = mapToTree(Map.of(
            "id", employee.getId(),
            "input", input));

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-employee", "updateEmployee", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.updateEmployee.employee")
                .as(PathMap.class)
                .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
                .matches(v -> v.get("name").equals(input.getName()), "name")
                .matches(v -> v.get("title").equals(input.getTitle()), "title")
                .matches(v -> v.getByPath("info.gender").equals(input.getInfo().get("gender")), "info.gender")
                .matches(v -> LocalDate.parse(v.getByPath("info.birthday")).equals(LocalDate.parse("1981-03-17")), "info.birthday")
                .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
                .matches(v -> v.getByPath("createdByUser.id").equals(currentUser().getId().toString()), "createdByUser.id")
                .matches(v -> v.getByPath("updatedByUser.id").equals(currentUser().getId().toString()), "updatedByUser.id")
                .matches(v -> v.getByPath("departments[0].id").equals(departments.get(0).getId().toString()), "departments[0].id")
                .matches(v -> v.getByPath("departments[0].name").equals(departments.get(0).getName()), "departments[0].name")
                .matches(v -> v.getByPath("departments[1].id").equals(departments.get(1).getId().toString()), "departments[1].id")
                .matches(v -> v.getByPath("departments[1].name").equals(departments.get(1).getName()), "departments[1].name");
        // @formatter:on
    }

    /**
     * 测试
     * {@link EmployeeMutation#deleteEmployee(String)
     * EmployeeMutation.deleteEmployee(String)} 方法, 删除一个雇员实体
     */
    @Test
    void deleteEmployee_shouldMutationExecute() throws IOException {
        // 创建待删除雇员实体
        Employee employee;
        try (var ignore = beginTx(false)) {
            employee = newBuilder(EmployeeBuilder.class).create();
        }

        // 构建删除参数
        var vars = valueToTree("id", employee.getId());

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-employee", "deleteEmployee", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.deleteEmployee")
                .as(PathMap.class)
                .matches(v -> v.get("deleted").equals(true), "deleted");
        // @formatter:on
    }
}
