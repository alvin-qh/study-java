package alvin.study.springboot.graphql.app.api.query;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.graphql.builder.EmployeeBuilder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

class EmployeeQueryTest extends WebTest {
    @Test
    void query_shouldFindEmployeeById() {
        Department department1, department2, department3;
        Employee employee;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();

            employee = newBuilder(EmployeeBuilder.class).create();

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

        qlTester().documentName("employee")
                .operationName("queryEmployee")
                .variable("id", employee.getId())
                .execute()
                .path("employee")
                .matchesJson("""
                        {
                            "id": "%d",
                            "orgId": "%d",
                            "org": {
                                "id": "%d",
                                "name": "%s"
                            },
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
                            ],
                            "createdBy": "%d",
                            "updatedBy": "%d",
                            "createdAt": "%s",
                            "updatedAt": "%s",
                            "createdByUser": {
                                "id": "%d",
                                "account": "%s"
                            },
                            "updatedByUser": {
                                "id": "%d",
                                "account": "%s"
                            }
                        }
                    """.formatted(
                    employee.getId(),
                    employee.getOrgId(),
                    employee.getOrgId(),
                    currentOrg().getName().toUpperCase(),
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
                    department3.getName(),
                    currentUser().getId(),
                    currentUser().getId(),
                    formatDatetime(employee.getCreatedAt()),
                    formatDatetime(employee.getUpdatedAt()),
                    currentUser().getId(),
                    currentUser().getAccount(),
                    currentUser().getId(),
                    currentUser().getAccount()));
    }
}
