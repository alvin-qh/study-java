package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import graphql.GraphQLContext;

import alvin.study.springboot.graphql.app.api.mutation.common.BaseMutation;
import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.infra.entity.Department;

@Controller
@RequiredArgsConstructor
public class DepartmentMutation extends BaseMutation {
    private final DepartmentService departmentService;

    static record DepartmentInput(String name, Long parentId) {
        public Department toEntity(Long id) {
            var department = new Department();
            department.setId(id);
            department.setName(name);
            department.setParentId(parentId);
            return department;
        }
    }

    @MutationMapping
    public MutationResult<Department> createDepartment(@Argument DepartmentInput input) {
        var department = input.toEntity(null);
        departmentService.create(department);
        return MutationResult.of(department);
    }

    @MutationMapping
    public MutationResult<Department> updateDepartment(@Argument Long id, @Argument DepartmentInput input) {
        var department = input.toEntity(id);
        departmentService.update(department);
        return MutationResult.of(department);
    }

    @MutationMapping
    public boolean deleteDepartment(@Argument Long id, GraphQLContext ctx) {
        return departmentService.delete(id);
    }
}
