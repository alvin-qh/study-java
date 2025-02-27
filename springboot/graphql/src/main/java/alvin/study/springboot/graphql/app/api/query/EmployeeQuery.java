package alvin.study.springboot.graphql.app.api.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.google.common.base.Functions;

import lombok.RequiredArgsConstructor;

import graphql.GraphQLContext;

import alvin.study.springboot.graphql.app.api.query.common.AuditedBaseQuery;
import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.core.context.ContextKey;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.entity.Employee;
import alvin.study.springboot.graphql.infra.entity.Org;

/**
 * 对应 {@link Employee} 类型的 GraphQL 查询对象
 *
 * <p>
 * 对应 {@code classpath:graphql/employee.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class EmployeeQuery extends AuditedBaseQuery<Employee> {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    /**
     * 查询雇员 {@link Employee} 类型雇员实体对象
     *
     * @param id 雇员实体 {@code ID} 值
     * @return {@link Employee} 类型雇员实体对象
     */
    @QueryMapping
    public Employee employee(@Argument long id, GraphQLContext ctx) {
        return employeeService.findById(ctx.<Org>get(ContextKey.ORG).getId(), id);
    }

    /**
     * 查询雇员所属的部门 {@link Department} 类型部门实体对象集合
     *
     * @param entity 雇员实体
     * @return {@link Department} 类型部门实体对象集合
     */
    @BatchMapping
    public Map<Employee, List<Department>> departments(List<Employee> entities) {
        var empMap = entities.stream().collect(Collectors.toMap(Employee::getId, Functions.identity()));
        var employeeDepts = departmentService.listByEmployeeIds(empMap.keySet());

        return employeeDepts.stream().collect(
            Collectors.toMap(
                e -> empMap.get(e.getEmployee().getId()),
                DepartmentEmployee::getDepartments));
    }
}
