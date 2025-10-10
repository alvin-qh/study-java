package alvin.study.springboot.graphql.infra.mapper;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.core.graphql.relay.Pagination;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

class EmployeeMapperTest extends IntegrationTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private Pagination pagination;

    @Test
    void selectByDepartmentId_shouldGetEmployeeListByDepartmentId() {
        Department department;
        Employee employee1, employee2, employee3, employee4, employee5;

        try (var _ = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();

            employee1 = newBuilder(EmployeeBuilder.class).create();
            employee2 = newBuilder(EmployeeBuilder.class).create();
            employee3 = newBuilder(EmployeeBuilder.class).create();
            employee4 = newBuilder(EmployeeBuilder.class).create();
            employee5 = newBuilder(EmployeeBuilder.class).create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee1.getId())
                    .withDepartmentId(department.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee2.getId())
                    .withDepartmentId(department.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee3.getId())
                    .withDepartmentId(department.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee4.getId())
                    .withDepartmentId(department.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee5.getId())
                    .withDepartmentId(department.getId())
                    .create();
        }

        var page = pagination.<Employee>newBuilder()
                .withOffset(0)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = employeeMapper.selectByDepartmentId(page, currentOrg().getId(), department.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(5);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(1);
        then(page.getRecords().stream().map(Employee::getId)).containsExactly(
            employee5.getId(),
            employee4.getId(),
            employee3.getId());

        page = pagination.<Employee>newBuilder()
                .withOffset(3)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = employeeMapper.selectByDepartmentId(page, currentOrg().getId(), department.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(5);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(2);
        then(page.getRecords().stream().map(Employee::getId)).containsExactly(
            employee2.getId(),
            employee1.getId());
    }
}
