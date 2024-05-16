package alvin.study.springboot.kickstart.app.api.mutation;

import alvin.study.springboot.kickstart.app.api.common.BaseMutation;
import alvin.study.springboot.kickstart.app.api.schema.input.EmployeeInput;
import alvin.study.springboot.kickstart.app.api.schema.payload.CreateEmployeePayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.DeleteEmployeePayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.UpdateEmployeePayload;
import alvin.study.springboot.kickstart.app.api.schema.type.EmployeeType;
import alvin.study.springboot.kickstart.app.service.EmployeeService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Mutation;
import alvin.study.springboot.kickstart.infra.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 雇员实体变更类型
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
 * 在 {@code classpath:graphql/employee.graphqls} 中定义查询的 Mutation schema
 * </li>
 * </ul>
 * </p>
 */
@Mutation
@Validated
@RequiredArgsConstructor
public class EmployeeMutation extends BaseMutation {
    // 注入雇员服务类对象
    private final EmployeeService employeeService;

    /**
     * 创建雇员实体
     *
     * @param input 雇员信息输入对象
     * @return 雇员创建结果
     */
    public CreateEmployeePayload createEmployee(@Valid EmployeeInput input) {
        // EmployeeInput => Employee
        var employee = map(input, Employee.class);

        // 存储雇员实体
        employeeService.create(employee, input.getDepartmentIds());

        // Employee => CreateEmployeePayload
        return new CreateEmployeePayload(map(employee, EmployeeType.class));
    }

    /**
     * 更新雇员实体
     *
     * @param id    要更新的雇员实体 id
     * @param input 雇员信息输入对象
     * @return 雇员更新结果
     */
    public UpdateEmployeePayload updateEmployee(String id, @Valid EmployeeInput input) {
        // DepartmentInput => Department
        var employee = map(input, Employee.class);

        // 更新雇员实体
        return employeeService.update(Long.parseLong(id), employee, input.getDepartmentIds())
            .map(u -> new UpdateEmployeePayload(map(u, EmployeeType.class)))
            .orElseThrow(() -> new InputException("Update Employee not found"));
    }

    /**
     * 删除雇员实体
     *
     * @param id 组织 ID
     * @return 组织实体删除结果
     */
    public DeleteEmployeePayload deleteEmployee(String id) {
        return new DeleteEmployeePayload(employeeService.delete(Long.parseLong(id)));
    }
}
