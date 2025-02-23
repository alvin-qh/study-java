package alvin.study.springboot.graphql.app.api.query;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.api.query.common.AuditedBaseQuery;
import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

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
    public Employee employee(@Argument String id) {
        // 查询组织
        return employeeService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid employee id"));
    }

    /**
     * 查询雇员所属的部门 {@link Department} 类型部门实体对象集合
     *
     * @param entity 雇员实体
     * @return {@link Department} 类型部门实体对象集合
     */
    @SchemaMapping
    public List<Department> departments(Employee entity) {
        return departmentService.listByEmployeeId(entity.getId());
    }
}
