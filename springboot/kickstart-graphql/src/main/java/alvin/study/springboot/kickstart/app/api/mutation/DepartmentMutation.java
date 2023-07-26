package alvin.study.springboot.kickstart.app.api.mutation;

import alvin.study.springboot.kickstart.app.api.common.BaseMutation;
import alvin.study.springboot.kickstart.app.api.schema.input.DepartmentInput;
import alvin.study.springboot.kickstart.app.api.schema.payload.CreateDepartmentPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.DeleteDepartmentPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.UpdateDepartmentPayload;
import alvin.study.springboot.kickstart.app.api.schema.type.DepartmentType;
import alvin.study.springboot.kickstart.app.service.DepartmentService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Mutation;
import alvin.study.springboot.kickstart.infra.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 部门实体变更类型
 *
 * <p>
 * Kickstart 框架定义的变更类需要包含如下几个条件:
 * <ul>
 * <li>
 * 实现 {@link graphql.kickstart.tools.GraphQLMutationResolver
 * GraphQLMutationResolver} 接口
 * </li>
 * <li>
 * 具备 {@link org.springframework.stereotype.Component @Component} 注解, 本例中用
 * {@link Mutation @Mutation} 注解替代
 * </li>
 * <li>
 * 在 {@code classpath:graphql/department.graphqls} 中定义查询的 Mutation schema
 * </li>
 * </ul>
 * </p>
 */
@Mutation
@Validated
@RequiredArgsConstructor
public class DepartmentMutation extends BaseMutation {
    // 注入部门服务类对象
    private final DepartmentService departmentService;

    /**
     * 创建部门实体
     *
     * @param input 部门信息输入对象
     * @return 部门创建结果
     */
    public CreateDepartmentPayload createDepartment(@Valid DepartmentInput input) {
        // DepartmentInput => Department
        var department = map(input, Department.class);

        // 存储部门实体
        departmentService.create(department);

        // User => CreateUserPayload
        return new CreateDepartmentPayload(map(department, DepartmentType.class));
    }

    /**
     * 更新部门实体
     *
     * @param id    要更新的部门实体 id
     * @param input 部门信息输入对象
     * @return 部门更新结果
     */
    public UpdateDepartmentPayload updateDepartment(String id, @Valid DepartmentInput input) {
        // DepartmentInput => Department
        var department = map(input, Department.class);

        // 更新部门实体
        return departmentService.update(Long.parseLong(id), department)
            .map(u -> new UpdateDepartmentPayload(map(u, DepartmentType.class)))
            .orElseThrow(() -> new InputException("Update Department not found"));
    }

    /**
     * 删除部门实体
     *
     * @param id 组织 ID
     * @return 组织实体删除结果
     */
    public DeleteDepartmentPayload deleteDepartment(String id) {
        return new DeleteDepartmentPayload(departmentService.delete(Long.parseLong(id)));
    }
}
