package alvin.study.springboot.graphql.app.dataloader;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.DepartmentBuilder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.mapper.DepartmentMapper;

class DepartmentLoaderTest extends IntegrationTest {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    void apply_shouldLoadDepartments() {
        Department department1, department2, department3;

        try (var ignore = beginTx(false)) {
            department1 = newBuilder(DepartmentBuilder.class).create();
            department2 = newBuilder(DepartmentBuilder.class).create();
            department3 = newBuilder(DepartmentBuilder.class).create();
        }

        var loader = new DepartmentLoader(departmentMapper);
        var result = loader.apply(
            Set.of(department1.getId(), department2.getId(), department3.getId()), null);

        var map = result.block();
        then(map.get(department1.getId()).getId()).isEqualTo(department1.getId());
        then(map.get(department2.getId()).getId()).isEqualTo(department2.getId());
        then(map.get(department3.getId()).getId()).isEqualTo(department3.getId());
    }
}
