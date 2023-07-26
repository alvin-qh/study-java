package alvin.study.springboot.kickstart.app.api;

import alvin.study.springboot.kickstart.IntegrationTest;
import alvin.study.springboot.kickstart.app.api.mutation.DepartmentMutation;
import alvin.study.springboot.kickstart.app.api.query.DepartmentQuery;
import alvin.study.springboot.kickstart.app.api.schema.input.DepartmentInput;
import alvin.study.springboot.kickstart.builder.DepartmentBuilder;
import alvin.study.springboot.kickstart.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.kickstart.builder.EmployeeBuilder;
import alvin.study.springboot.kickstart.core.graphql.relay.Cursors;
import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.entity.Employee;
import alvin.study.springboot.kickstart.util.collection.PathMap;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 测试 {@link DepartmentQuery DepartmentQuery} 和
 * {@link DepartmentMutation DepartmentMutation}
 * 类型
 *
 * <p>
 * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-department.graphql} 文件中定义
 * </p>
 */
class DepartmentTest extends IntegrationTest {
    /**
     * 测试 {@link DepartmentQuery#department(String)
     * DepartmentQuery.department(String)} 方法
     */
    @Test
    void department_shouldQueryById() throws IOException {
        // 构造测试数据
        Department department, parent;
        var children = new ArrayList<Department>();
        var employees = new ArrayList<Employee>();
        try (var ignore = beginTx(false)) {
            // 持久化上级部门
            parent = newBuilder(DepartmentBuilder.class).create();
            // 持久化当前部门, 指定上级部门
            department = newBuilder(DepartmentBuilder.class).withParentId(parent.getId()).create();
            // 持久化 10 个当前部门的下级部门
            for (var i = 0; i < 10; i++) {
                children.add(newBuilder(DepartmentBuilder.class).withParentId(department.getId()).create());
            }
            // 持久化 10 个部门雇员
            for (var i = 0; i < 10; i++) {
                var employee = newBuilder(EmployeeBuilder.class).create();
                newBuilder(DepartmentEmployeeBuilder.class)
                    .withDepartmentId(department.getId())
                    .withEmployeeId(employee.getId())
                    .create();
                employees.add(employee);
            }
        }

        // 定义查询参数
        var vars = mapToTree(Map.of(
            "id", department.getId(), // 要查询的部门 id
            "childrenAfter", Cursors.makeCursor(1), // 起始位置索引 (查询该索引之后的数据)
            "employeesAfter", Cursors.makeCursor(2), // 起始位置索引 (查询该索引之后的数据)
            "first", 5 // 查询数量
        ));

        // @formatter:off
        // 确认返回结果符合预期
        graphql("test-department", "queryDepartment", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.department")
                .as(PathMap.class)
                .matches(v -> v.get("id").equals(department.getId().toString()), "id")
                .matches(v -> v.getByPath("org.id").equals(department.getOrgId().toString()), "org.id")
                .matches(v -> v.get("name").equals(department.getName()), "name")
                .matches(v -> v.getByPath("parent.id").equals(parent.getId().toString()), "parent.id")
                .matches(v -> ((List<?>) v.getByPath("children.edges")).size() == 5, "children.edges.size()")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("children.edges[0].cursor"))).equals(1), "children.edges[0].cursor")
                .matches(v -> v.getByPath("children.edges[0].node.__typename").equals("Department"), "children.edges[0].node.__typename")
                .matches(v -> v.getByPath("children.edges[0].node.id").equals(children.get(1).getId().toString()), "children.edges[0].node.id")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("children.pageInfo.startCursor"))).equals(1), "children.pageInfo.startCursor")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("children.pageInfo.endCursor"))).equals(6), "children.pageInfo.endCursor")
                .matches(v -> v.getByPath("children.pageInfo.hasNextPage").equals(true), "children.pageInfo.hasNextPage")
                .matches(v -> v.getByPath("children.pageInfo.hasPreviousPage").equals(false), "children.pageInfo.hasPreviousPage")
                .matches(v -> v.getByPath("children.totalCount").equals(10), "children.totalCount")
                .matches(v -> v.getByPath("employees.totalCount").equals(10), "totalCount.totalCount")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("employees.edges[0].cursor"))).equals(2), "employees.edges[0].cursor")
                .matches(v -> v.getByPath("employees.edges[0].node.__typename").equals("Employee"), "employees.edges[0].node.__typename")
                .matches(v -> v.getByPath("employees.edges[0].node.id").equals(employees.get(2).getId().toString()), "employees.edges[0].node.id")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("employees.pageInfo.startCursor"))).equals(2), "employees.pageInfo.startCursor")
                .matches(v -> Objects.requireNonNull(Cursors.parseCursor(v.getByPath("employees.pageInfo.endCursor"))).equals(7), "employees.pageInfo.endCursor");
        // @formatter:on
    }

    /**
     * 测试
     * {@link DepartmentMutation#createDepartment(DepartmentInput)
     * DepartmentMutation.createDepartment(DepartmentInput)} 方法
     *
     * <p>
     * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-department.graphql} 文件中定义
     * </p>
     */
    @Test
    void create_shouldCreateDepartment() throws IOException {
        // 构造测试数据
        Department parent;
        try (var ignore = beginTx(false)) {
            parent = newBuilder(DepartmentBuilder.class).create();
        }

        // 定义执行参数
        var input = DepartmentInput.builder()
            .name("RD-X")
            .parentId(parent.getId())
            .build();

        var vars = valueToTree("input", input);

        // 执行创建语句
        graphql("test-department", "createDepartment", vars)
            .assertThatNoErrorsArePresent()
            .assertThatField("$.data.createDepartment.department")
            .as(PathMap.class)
            .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
            .matches(v -> v.get("name").equals("RD-X"), "name")
            .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
            .matches(v -> v.getByPath("parent.id").equals(parent.getId().toString()), "parent.id");
    }

    /**
     * 测试
     * {@link DepartmentMutation#updateDepartment(String, DepartmentInput)
     * DepartmentMutation.updateDepartment(String, DepartmentInput)} 方法
     *
     * <p>
     * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-department.graphql} 文件中定义
     * </p>
     */
    @Test
    void update_shouldUpdateDepartment() throws IOException {
        // 构造测试数据
        Department parent, department;
        try (var ignore = beginTx(false)) {
            parent = newBuilder(DepartmentBuilder.class).create();
            department = newBuilder(DepartmentBuilder.class).create();
        }

        // 定义执行参数
        var input = DepartmentInput.builder()
            .name("RD-X")
            .parentId(parent.getId())
            .build();

        var vars = mapToTree(Map.of(
            "id", department.getId(),
            "input", input));

        // 执行创建语句
        graphql("test-department", "updateDepartment", vars)
            .assertThatNoErrorsArePresent()
            .assertThatField("$.data.updateDepartment.department")
            .as(PathMap.class)
            .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
            .matches(v -> v.get("name").equals("RD-X"), "name")
            .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
            .matches(v -> v.getByPath("parent.id").equals(parent.getId().toString()), "parent.id");
    }

    /**
     * 测试
     * {@link DepartmentMutation#deleteDepartment(String)
     * DepartmentMutation.deleteDepartment(String)} 方法, 删除一个部门实体
     */
    @Test
    void deleteDepartment_shouldMutationExecute() throws IOException {
        // 创建待删除部门实体
        Department department;
        try (var ignore = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();
        }

        // 构建删除参数
        var vars = valueToTree("id", department.getId());

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-department", "deleteDepartment", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.deleteDepartment")
                .as(PathMap.class)
                .matches(v -> v.get("deleted").equals(true), "deleted");
        // @formatter:on
    }
}
