package alvin.study.springboot.kickstart.app.api.query;

import alvin.study.springboot.kickstart.app.api.common.BaseQuery;
import alvin.study.springboot.kickstart.app.api.schema.type.EmployeeType;
import alvin.study.springboot.kickstart.app.service.EmployeeService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Query;
import lombok.RequiredArgsConstructor;

/**
 * 组织查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/employee.graphqls} 中的定义
 * </p>
 *
 * <p>
 * Kickstart 框架定义的查询类需要包含如下几个条件:
 * <ul>
 * <li>
 * 实现 {@link graphql.kickstart.tools.GraphQLQueryResolver GraphQLQueryResolver}
 * 接口
 * </li>
 * <li>
 * 具备 {@link org.springframework.stereotype.Component @Component} 注解, 本例中用
 * {@link Query @Query} 注解替代
 * </li>
 * <li>
 * 在 {@code classpath:graphql/employee.graphqls} 中定义查询的 schema
 * </li>
 * </ul>
 * </p>
 */
@Query
@RequiredArgsConstructor
public class EmployeeQuery extends BaseQuery {
    // 注入服务对象
    private final EmployeeService employeeService;

    /**
     * 雇员查询
     *
     * @param id 雇员 id
     * @return 雇员对象
     */
    public EmployeeType employee(String id) {
        // 查询组织
        return employeeService.findById(Long.parseLong(id))
            .map(u -> map(u, EmployeeType.class))
            .orElseThrow(() -> new InputException("Invalid employee id"));
    }
}
