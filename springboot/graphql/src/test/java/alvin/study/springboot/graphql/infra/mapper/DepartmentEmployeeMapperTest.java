package alvin.study.springboot.graphql.infra.mapper;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

public class DepartmentEmployeeMapperTest extends IntegrationTest {
    @Autowired
    private DepartmentEmployeeMapper departmentEmployeeMapper;

    @Test
    void selectByEmployeeIds_shouldFindDepartmentByEmployee() {
        Department department1, department2, department3;
        Employee employee1, employee2;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class)
                    .withOrgId(currentOrg().getId())
                    .create();

            department2 = newBuilder(DepartmentBuilder.class)
                    .withOrgId(currentOrg().getId())
                    .create();

            department3 = newBuilder(DepartmentBuilder.class)
                    .withOrgId(currentOrg().getId())
                    .create();

            employee1 = newBuilder(EmployeeBuilder.class)
                    .withOrgId(currentOrg().getId())
                    .create();

            employee2 = newBuilder(EmployeeBuilder.class)
                    .withOrgId(currentOrg().getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee1.getId())
                    .withDepartmentId(department1.getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee1.getId())
                    .withDepartmentId(department2.getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee2.getId())
                    .withDepartmentId(department3.getId())
                    .withOrgId(currentOrg().getId())
                    .create();
        }

        var departmentEmployees = departmentEmployeeMapper.selectByEmployeeIds(
            currentOrg().getId(),
            List.of(employee1.getId(), employee2.getId()));

        // 验证结果
        then(departmentEmployees).hasSize(3);

        then(departmentEmployees.get(0).getDepartmentId()).isEqualTo(department1.getId());
        then(departmentEmployees.get(0).getEmployeeId()).isEqualTo(employee1.getId());

        then(departmentEmployees.get(1).getDepartmentId()).isEqualTo(department2.getId());
        then(departmentEmployees.get(1).getEmployeeId()).isEqualTo(employee1.getId());

        then(departmentEmployees.get(2).getDepartmentId()).isEqualTo(department3.getId());
        then(departmentEmployees.get(2).getEmployeeId()).isEqualTo(employee2.getId());
    }
}
