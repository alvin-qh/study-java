package alvin.study.springboot.graphql.app.api.query;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Department;

/**
 * 组织查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/department.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class DepartmentQuery {
    // 注入服务对象
    private final DepartmentService departmentService;

    /**
     * 部门查询
     *
     * @param id 部门 id
     * @return 部门对象
     */
    @QueryMapping
    public Department department(@Argument String id) {
        // 查询组织
        return departmentService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid department id"));
    }
}
