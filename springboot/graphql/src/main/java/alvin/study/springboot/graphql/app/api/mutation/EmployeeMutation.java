package alvin.study.springboot.graphql.app.api.mutation;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.api.mutation.common.BaseMutation;
import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.infra.entity.Employee;
import alvin.study.springboot.graphql.infra.entity.Org;
import graphql.GraphQLContext;

@Controller
@RequiredArgsConstructor
public class EmployeeMutation extends BaseMutation {
    private final EmployeeService employeeService;

    static record EmployeeInput(
            String name,
            String email,
            String title,
            Map<String, Object> info,
            List<Long> departmentIds) {
        public Employee toEntity(GraphQLContext ctx, @Nullable Long id) {
            var employee = completeAuditedEntity(new Employee(), ctx);
            employee.setId(id);
            employee.setName(name);
            employee.setEmail(email);
            employee.setTitle(title);
            employee.setInfo(info);
            return employee;
        }
    }

    @MutationMapping
    public MutationResult<Employee> createEmployee(@Argument EmployeeInput input, GraphQLContext ctx) {
        var employee = input.toEntity(ctx, null);
        employeeService.create(employee, input.departmentIds());
        return MutationResult.of(employee);
    }

    @MutationMapping
    public MutationResult<Employee> updateEmployee(@Argument Long id, @Argument EmployeeInput input,
            GraphQLContext ctx) {
        var employee = input.toEntity(ctx, id);
        employeeService.update(employee, input.departmentIds());
        return MutationResult.of(employee);
    }

    @MutationMapping
    public boolean deleteEmployee(@Argument Long id, GraphQLContext ctx) {
        return employeeService.delete(ctx.<Org>get(ContextKey.ORG).getId(), id);
    }
}
