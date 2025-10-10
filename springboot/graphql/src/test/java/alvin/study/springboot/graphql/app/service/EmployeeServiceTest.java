package alvin.study.springboot.graphql.app.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.core.exception.NotFoundException;
import alvin.study.springboot.graphql.core.graphql.relay.Pagination;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.entity.Employee;

class EmployeeServiceTest extends IntegrationTest {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private Pagination pagination;

    @Test
    void create_shouldCreateEmployee() {
        Department department;
        try (var _ = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();
        }

        var expectedEmployee = new EmployeeBuilder().build();

        employeeService.create(expectedEmployee, List.of(department.getId()));

        var actualEmployee = employeeService.findById(expectedEmployee.getId());

        then(actualEmployee.getId()).isEqualTo(expectedEmployee.getId());
    }

    @Test
    void delete_shouldDeleteExistEmployee() {
        var employee = newBuilder(EmployeeBuilder.class).create();

        var result = employeeService.delete(employee.getId());
        then(result).isTrue();

        thenThrownBy(() -> employeeService.findById(employee.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldFindExistEmployee() {
        var expectedEmployee = newBuilder(EmployeeBuilder.class).create();

        var actualEmployee = employeeService.findById(expectedEmployee.getId());
        then(actualEmployee.getId()).isEqualTo(expectedEmployee.getId());
    }

    @Test
    void listByDepartmentId_shouldListEmployeesInDepartmentWithDepartmentId() {
        Department department;
        Employee employee1, employee2, employee3, employee4;

        try (var _ = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();

            employee1 = newBuilder(EmployeeBuilder.class).create();
            employee2 = newBuilder(EmployeeBuilder.class).create();
            employee3 = newBuilder(EmployeeBuilder.class).create();

            employee4 = newBuilder(EmployeeBuilder.class).create();

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
        }

        var page = pagination.<Employee>newBuilder()
                .withOffset(0)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = employeeService.listByDepartmentId(page, department.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(4);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(1);
        then(page.getRecords().stream().map(Employee::getId)).containsExactly(
            employee4.getId(),
            employee3.getId(),
            employee2.getId());

        page = pagination.<Employee>newBuilder()
                .withOffset(3)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = employeeService.listByDepartmentId(page, department.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(4);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(2);
        then(page.getRecords().stream().map(Employee::getId)).containsExactly(employee1.getId());
    }

    @Test
    void update_shouldUpdateExistEmployee() {
        Department department1, department2;

        try (var _ = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
        }

        var employee = newBuilder(EmployeeBuilder.class).create();

        var originEmployeeName = employee.getName();

        employee.setName("updated_" + employee.getName());

        employeeService.update(employee, List.of(department1.getId(), department2.getId()));

        employee = employeeService.findById(employee.getId());
        then(employee.getName()).isEqualTo("updated_" + originEmployeeName);

        var departmentEmployees = departmentService.listByEmployeeIds(List.of(employee.getId()));

        then(departmentEmployees.get(0))
                .extracting(DepartmentEmployee::getDepartmentId).isEqualTo(department1.getId());
        then(departmentEmployees.get(0))
                .extracting(DepartmentEmployee::getEmployeeId).isEqualTo(employee.getId());

        then(departmentEmployees.get(1))
                .extracting(DepartmentEmployee::getDepartmentId).isEqualTo(department2.getId());
        then(departmentEmployees.get(1))
                .extracting(DepartmentEmployee::getEmployeeId).isEqualTo(employee.getId());
    }
}
