package alvin.study.springboot.graphql.app.api.mutation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

public class EmployeeMutationTest extends WebTest {
    @Autowired
    private EmployeeService employeeService;

    @Test
    void createEmployee_shouldCreateNewEmployee() {
        Department department1, department2, department3;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            department2 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            department3 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();
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

        var employee = employeeService.findById(currentOrg().getId(), id);

        resp.path("createEmployee.result")
                .matchesJson("""
                        {
                            "id": "%d",
                            "name": "%s",
                            "email": "%s",
                            "title": "%s",
                            "info": {
                                "telephone": "%s",
                                "birthday": "%s",
                                "gender": "%s"
                            },
                            "departments": [
                               {
                                    "id": "%d",
                                    "name": "%s"
                                },
                                {
                                    "id": "%d",
                                    "name": "%s"
                                },
                                {
                                    "id": "%d",
                                    "name": "%s"
                                }
                            ]
                        }
                    """.formatted(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getTitle(),
                    employee.getInfo().get("telephone"),
                    employee.getInfo().get("birthday"),
                    employee.getInfo().get("gender"),
                    department1.getId(),
                    department1.getName(),
                    department2.getId(),
                    department2.getName(),
                    department3.getId(),
                    department3.getName()));
    }

    @Test
    void deleteEmployee_shouldDeleteExistEmployee() {
        Department department1, department2, department3;
        Employee employee;

        try (var ignore = beginTx(false)) {
            employee = newBuilder(EmployeeBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            department1 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            department2 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();

            department3 = newBuilder(DepartmentBuilder.class)
                    .withAuditorId(currentUser().getId())
                    .withOrgId(currentOrg().getId())
                    .create();
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
                            },
                            "departments": [
                               {
                                   "id": "%d",
                               }
                            ]
                        }
                    """.formatted(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    department1.getId(),
                    department1.getName(),
                    department2.getId(),
                    department2.getName(),
                    department3.getId(),
                    department3.getName(),
                    formatDatetime(employee.getCreatedAt()),
                    formatDatetime(employee.getUpdatedAt()),
                    currentUser().getId(),
                    currentUser().getId()));
    }

    @Test
    void updateEmployee_shouldUpdateExistEmployee() {

    }
}
