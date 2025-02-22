package alvin.study.springboot.graphql.app.api.query;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Employee;

/**
 * 组织查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/employee.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class EmployeeQuery {
    // 注入服务对象
    private final EmployeeService employeeService;

    /**
     * 雇员查询
     *
     * @param id 雇员 id
     * @return 雇员对象
     */
    @QueryMapping
    public Employee employee(@Argument String id) {
        // 查询组织
        return employeeService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid employee id"));
    }
}
