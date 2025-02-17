package alvin.study.springboot.jpa.app.domain.service;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.infra.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link DepartmentService} 对象
 */
class DepartmentServiceTest extends IntegrationTest {
    // 注入 Service 对象
    @Autowired
    private DepartmentService service;

    /**
     * 测试
     * {@link DepartmentService#searchSubDepartments(String, String, org.springframework.data.domain.Pageable)
     * DepartmentService.searchSubDepartments(String, String, Pageable)} 方法,
     * 根据已知的部门名称和子部门名称查询 {@link Department}
     * 实体对象分页结果
     *
     * <p>
     * 本测试通过部门名称查询结果
     * </p>
     */
    @Test
    void searchSubDepartments_shouldSearchChildDepartmentsByDepartmentName() {
        // 部门名称前缀
        var namePrefix = "DEPT-";

        // 创建 5 个部门, 每个部门下面创建 10 个子部门
        try (var ignore = beginTx(false)) {
            for (var i = 0; i < 5; i++) {
                // 创建部门实体对象
                var department = newBuilder(DepartmentBuilder.class)
                        .name(namePrefix + i)
                        .create();

                // 创建 10 个部门实体对象
                for (var j = 0; j < 10; j++) {
                    var subDepartment = newBuilder(DepartmentBuilder.class)
                            .name(namePrefix + i + "-" + j)
                            .create();
                    department.addSubDepartment(subDepartment);
                }
            }
        }

        var pageable = PageRequest.of(0, 10);

        // 获取名称模式为 DEPT-3* 的所有部门, 确认查询到一个结果
        var page = service.searchSubDepartments(namePrefix + 3, null, pageable);
        then(page.getNumberOfElements()).isOne();

        // 获取查询结果, 确认部门名称符合查询条件
        var department = page.getContent().get(0);
        then(department.getName()).isEqualTo("DEPT-3");

        // 获取当前部门的所有子部门, 确认子部门数量共 10 个
        var subDepartments = department.getChildren();
        // 确认子部门的名称
        then(subDepartments)
                .hasSize(10)
                .extracting("name")
                .map(name -> (String) name)
                .allMatch(name -> name.startsWith("DEPT-3-"));
    }

    /**
     * 测试
     * {@link DepartmentService#searchSubDepartments(String, String, org.springframework.data.domain.Pageable)
     * DepartmentService.searchSubDepartments(String, String, Pageable)} 方法,
     * 根据已知的部门名称和子部门名称查询 {@link Department} 实体对象分页结果
     *
     * <p>
     * 本测试通过子部门名称查询结果
     * </p>
     */
    @Test
    void searchSubDepartments_shouldSearchChildDepartmentsByChildDepartmentName() {
        // 部门名称前缀
        var namePrefix = "DEPT-";

        // 创建 5 个部门, 每个部门下面创建 10 个子部门
        try (var ignore = beginTx(false)) {
            for (var i = 0; i < 5; i++) {
                // 创建部门实体对象
                var department = newBuilder(DepartmentBuilder.class)
                        .name(namePrefix + i)
                        .create();

                // 创建 10 个部门实体对象
                for (var j = 0; j < 10; j++) {
                    var subDepartment = newBuilder(DepartmentBuilder.class)
                            .name(namePrefix + i + "-" + j)
                            .create();
                    department.addSubDepartment(subDepartment);
                }
            }
        }

        var pageable = PageRequest.of(0, 10);

        // 获取名称模式为 DEPT-3* 的所有子部门
        var page = service.searchSubDepartments(null, "DEPT-4-2", pageable);
        then(page.getNumberOfElements()).isOne();

        // 确认查询到 1 个部门, 确认部门名称
        var department = page.getContent().get(0);
        then(department.getName()).isEqualTo("DEPT-4");

        // 获取当前部门的所有子部门, 确认符合要求的子部门数量共 1 个
        var subDepartments = department.getChildren();
        // 确认子部门的名称
        then(subDepartments)
                .hasSize(1)
                .extracting("name")
                .startsWith("DEPT-4-2");
    }
}
