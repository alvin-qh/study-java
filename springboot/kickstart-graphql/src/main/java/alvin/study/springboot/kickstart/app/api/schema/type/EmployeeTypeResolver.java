package alvin.study.springboot.kickstart.app.api.schema.type;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;

import alvin.study.springboot.kickstart.app.api.schema.type.common.AuditedResolver;
import alvin.study.springboot.kickstart.app.api.schema.type.common.TenantedResolver;
import alvin.study.springboot.kickstart.app.service.DepartmentService;
import alvin.study.springboot.kickstart.core.graphql.annotation.Resolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;

/**
 * 对 {@link EmployeeType} 类型补充 {@code createdByUser}, {@code updatedByUser} 以及
 * {@code org} 字段
 *
 * <p>
 * 参考 {@code classpath:graphql/employee.graphqls} 中的 schema 定义
 * </p>
 */
@Resolver
@RequiredArgsConstructor
public class EmployeeTypeResolver implements AuditedResolver<EmployeeType>, TenantedResolver<EmployeeType> {
    // 部门服务类
    private final DepartmentService departmentService;

    /**
     * 查询雇员所属的部门
     *
     * @param instance 雇员对象
     * @return 部门列表集合
     */
    public List<@NotNull DepartmentType> departments(EmployeeType instance, DataFetchingEnvironment env) {
        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        return departmentService.listByEmployeeId(instance.getId()).stream()
            .map(d -> mapper.map(d, DepartmentType.class))
            .toList();
    }
}
