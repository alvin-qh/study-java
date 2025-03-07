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
import alvin.study.springboot.graphql.infra.entity.Employee;

public class DepartmentServiceTest extends IntegrationTest {
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private Pagination pagination;

    @Test
    void create_shouldCreateDepartment() {
        var expectedDepartment = newBuilder(DepartmentBuilder.class).build();

        departmentService.create(expectedDepartment);

        var actualDepartment = departmentService.findById(expectedDepartment.getId());
        then(actualDepartment.getId()).isEqualTo(expectedDepartment.getId());
    }

    @Test
    void delete_shouldDeleteExistDepartment() {
        var department = newBuilder(DepartmentBuilder.class).create();

        var result = departmentService.delete(department.getId());
        then(result).isTrue();

        thenThrownBy(() -> departmentService.findById(department.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldFindUserById() {
        var expectedDepartment = newBuilder(DepartmentBuilder.class).create();

        var actualDepartment = departmentService.findById(expectedDepartment.getId());
        then(actualDepartment.getId()).isEqualTo(expectedDepartment.getId());
    }

    @Test
    void listByEmployeeIds_shouldFindDepartmentByEmployeeId() {
        Department department1, department2, department3;
        Employee employee1, employee2;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();

            employee1 = newBuilder(EmployeeBuilder.class).create();
            employee2 = newBuilder(EmployeeBuilder.class).create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee1.getId())
                    .withDepartmentId(department1.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee1.getId())
                    .withDepartmentId(department2.getId())
                    .create();

            newBuilder(DepartmentEmployeeBuilder.class)
                    .withEmployeeId(employee2.getId())
                    .withDepartmentId(department3.getId())
                    .create();
        }

        var departmentEmployees
            = departmentService.listByEmployeeIds(List.of(employee1.getId(), employee2.getId()));

        // 验证结果
        then(departmentEmployees).hasSize(3);

        then(departmentEmployees.get(0).getDepartmentId()).isEqualTo(department1.getId());
        then(departmentEmployees.get(0).getEmployeeId()).isEqualTo(employee1.getId());

        then(departmentEmployees.get(1).getDepartmentId()).isEqualTo(department2.getId());
        then(departmentEmployees.get(1).getEmployeeId()).isEqualTo(employee1.getId());

        then(departmentEmployees.get(2).getDepartmentId()).isEqualTo(department3.getId());
        then(departmentEmployees.get(2).getEmployeeId()).isEqualTo(employee2.getId());

        then(departmentEmployees.get(0).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(0).getDepartmentId());
        then(departmentEmployees.get(0).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(0).getDepartmentId());

        then(departmentEmployees.get(1).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(1).getDepartmentId());
        then(departmentEmployees.get(1).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(1).getDepartmentId());

        then(departmentEmployees.get(2).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(2).getDepartmentId());
        then(departmentEmployees.get(2).getDepartment().getId())
                .isEqualTo(departmentEmployees.get(2).getDepartmentId());
    }

    @Test
    void listByIds_shouldFindDepartmentById() {
        Department department1, department2, department3;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();
        }

        var departments = departmentService.listByIds(List.of(
            department1.getId(),
            department2.getId(),
            department3.getId()));

        then(departments.stream().map(Department::getId).toList()).containsExactly(
            department1.getId(),
            department2.getId(),
            department3.getId());
    }

    @Test
    void listChildren_shouldListChildrenDepartment() {
        Department parent;
        Department child1, child2, child3, child4, child5;

        try (var ignore = beginTx(false)) {
            parent = newBuilder(DepartmentBuilder.class).create();

            child1 = newBuilder(DepartmentBuilder.class)
                    .withParent(parent.getId())
                    .create();

            child2 = newBuilder(DepartmentBuilder.class)
                    .withParent(parent.getId())
                    .create();

            child3 = newBuilder(DepartmentBuilder.class)
                    .withParent(parent.getId())
                    .create();

            child4 = newBuilder(DepartmentBuilder.class)
                    .withParent(parent.getId())
                    .create();

            child5 = newBuilder(DepartmentBuilder.class)
                    .withParent(parent.getId())
                    .create();
        }

        var page = pagination.<Department>newBuilder()
                .withOffset(0)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = departmentService.listChildren(page, parent.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(5);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(1);
        then(page.getRecords().stream().map(Department::getId)).containsExactly(
            child5.getId(),
            child4.getId(),
            child3.getId());

        page = pagination.<Department>newBuilder()
                .withOffset(3)
                .withLimit(3)
                .withOrder("-id")
                .build();

        page = departmentService.listChildren(page, parent.getId());
        then(page.getPages()).isEqualTo(2);
        then(page.getTotal()).isEqualTo(5);
        then(page.getSize()).isEqualTo(3);
        then(page.getCurrent()).isEqualTo(2);
        then(page.getRecords().stream().map(Department::getId)).containsExactly(
            child2.getId(),
            child1.getId());
    }

    @Test
    void update_shouldUpdateDepartment() {
        var department = newBuilder(DepartmentBuilder.class).create();

        var originDepartmentName = department.getName();

        department.setName("updated_" + department.getName());

        departmentService.update(department);

        department = departmentService.findById(department.getId());
        then(department.getName()).isEqualTo("updated_" + originDepartmentName);
    }
}
