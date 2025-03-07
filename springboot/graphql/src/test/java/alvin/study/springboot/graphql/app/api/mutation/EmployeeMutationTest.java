package alvin.study.springboot.graphql.app.api.mutation;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.entity.Employee;
import alvin.study.springboot.graphql.infra.mapper.DepartmentEmployeeMapper;

@Slf4j
public class EmployeeMutationTest extends WebTest {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentEmployeeMapper departmentEmployeeMapper;

    @Test
    void createEmployee_shouldCreateNewEmployee() {
        Department department1, department2, department3;

        log.info("------------------------------------------------------------------------------------------");

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();
        }

        var input = new EmployeeMutation.EmployeeInput(
            "Emma",
            "emma@fakemail.com",
            "Manager",
            Map.of(
                "telephone", "1399999999",
                "birthday", "1990-01-01",
                "gender", "FEMALE"),
            List.of(
                department1.getId(),
                department2.getId(),
                department3.getId()));

        var resp = qlTester().documentName("employee")
                .operationName("createEmployee")
                .variable("input", input)
                .execute();

        var id = resp.path("createEmployee.result.id")
                .entity(Long.class)
                .get();

        var employee = employeeService.findById(id);

        resp.path("createEmployee.result")
                .matchesJson("""
                        {
                            "id": "%d",
                            "name": "%s",
                            "email": "%s",
                            "title": "%s",
                            "info": {
                                "telephone": "1399999999",
                                "birthday": "1990-01-01",
                                "gender": "FEMALE"
                            }
                        }
                    """.formatted(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getTitle()));

        var departments = departmentService.listByEmployeeIds(List.of(employee.getId()));
        then(departments.stream().map(DepartmentEmployee::getDepartmentId).toList())
                .contains(department1.getId(), department2.getId(), department3.getId());

        log.info("------------------------------------------------------------------------------------------");
    }

    @Test
    void updateEmployee_shouldUpdateExistEmployee() {
        Department department1, department2, department3;
        Employee employee;

        try (var ignore = beginTx(false)) {
            employee = newBuilder(EmployeeBuilder.class).create();

            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();
        }

        var input = new EmployeeMutation.EmployeeInput(
            "updated_" + employee.getName(),
            "updated_" + employee.getEmail(),
            "Employee",
            Map.of(
                "telephone", "13988888888",
                "birthday", "1990-03-03",
                "gender", "FEMALE"),
            List.of(
                department1.getId(),
                department2.getId(),
                department3.getId()));

        qlTester().documentName("employee")
                .operationName("updateEmployee")
                .variable("id", employee.getId())
                .variable("input", input)
                .execute()
                .path("updateEmployee.result")
                .matchesJson("""
                        {
                            "id": "%d",
                            "name": "updated_%s",
                            "email": "updated_%s",
                            "title": "Employee",
                            "info": {
                                "telephone": "13988888888",
                                "birthday": "1990-03-03",
                                "gender": "FEMALE"
                            }
                        }
                    """.formatted(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail()));

        var departments = departmentService.listByEmployeeIds(List.of(employee.getId()));
        then(departments.stream().map(DepartmentEmployee::getDepartmentId).toList())
                .contains(department1.getId(), department2.getId(), department3.getId());
    }

    @Test
    void deleteEmployee_shouldDeleteExistEmployee() {
        Department department1, department2, department3;
        Employee employee;

        try (var ignore = beginTx(false)) {
            employee = newBuilder(EmployeeBuilder.class).create();

            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withDepartmentId(department1.getId())
                    .withEmployeeId(employee.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withDepartmentId(department2.getId())
                    .withEmployeeId(employee.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withDepartmentId(department3.getId())
                    .withEmployeeId(employee.getId())
                    .create();
        }

        var deleted = qlTester().documentName("employee")
                .operationName("deleteEmployee")
                .variable("id", employee.getId())
                .execute()
                .path("deleteEmployee")
                .entity(Boolean.class)
                .get();

        then(deleted).isTrue();

        var departments = departmentService.listByIds(List.of(
            department1.getId(),
            department2.getId(),
            department3.getId()));

        then(departments.stream().map(Department::getId).toList())
                .contains(department1.getId(), department2.getId(), department3.getId());

        var departmentEmployees = departmentEmployeeMapper.selectList(
            Wrappers.lambdaQuery(DepartmentEmployee.class)
                    .in(DepartmentEmployee::getDepartmentId, List.of(
                        department1.getId(),
                        department2.getId(),
                        department3.getId())));
        then(departmentEmployees).isEmpty();
    }
}
