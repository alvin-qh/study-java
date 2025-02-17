package alvin.study.springboot.kickstart.app.api.query;

import alvin.study.springboot.kickstart.app.api.common.BaseQuery;
import alvin.study.springboot.kickstart.app.api.schema.type.DepartmentType;
import alvin.study.springboot.kickstart.app.service.DepartmentService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Query;
import lombok.RequiredArgsConstructor;

/**
 * 组织查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/department.graphqls} 中的定义
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
 * 在 {@code classpath:graphql/department.graphqls} 中定义查询的 schema
 * </li>
 * </ul>
 * </p>
 */
@Query
@RequiredArgsConstructor
public class DepartmentQuery extends BaseQuery {
    // 注入服务对象
    private final DepartmentService departmentService;

    /**
     * 部门查询
     *
     * @param id 部门 id
     * @return 部门对象
     */
    public DepartmentType department(String id) {
        // 查询组织
        return departmentService.findById(Long.parseLong(id))
                .map(u -> map(u, DepartmentType.class))
                .orElseThrow(() -> new InputException("Invalid department id"));
    }
}
