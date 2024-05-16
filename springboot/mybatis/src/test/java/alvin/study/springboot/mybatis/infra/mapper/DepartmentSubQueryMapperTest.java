package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.DepartmentBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link DepartmentSubQueryMapper} 类型
 */
class DepartmentSubQueryMapperTest extends IntegrationTest {
    // 注入 Mapper 对象
    @Autowired
    private DepartmentSubQueryMapper mapper;

    /**
     * 测试 {@link DepartmentSubQueryMapper#selectWithParentById(Long)} 方法,
     * 查询一个部门和其上级部门
     *
     * <p>
     * 本例演示了如何使用级联查询的方法查询主实体和关联实体
     * </p>
     *
     * <p>
     * {@link DepartmentMapper#selectByNameWithParentAndChildren(String)} 方法对应着
     * {@code classpath:/mapper/DepartmentSubQueryMapper.xml} 中
     * {@code select #id="selectWithParentById"} 节点
     * </p>
     */
    @Test
    @Transactional
    void selectWithParentById_shouldGetSelectResult() {
        // 持久化一个父部门实体
        var parent = newBuilder(DepartmentBuilder.class).create();

        // 持久化一个子部门实体
        var child = newBuilder(DepartmentBuilder.class).withParent(parent).create();

        // 清理缓存
        clearSessionCache();

        // 根据子部门 id 查询实体对象和其父部门实体
        var mayDepartment = mapper.selectWithParentById(child.getId());
        then(mayDepartment).isPresent();

        // 获取父部门实体对象
        var department = mayDepartment.get();

        // 确认父子部门实体对象
        then(department.getId()).isEqualTo(child.getId());
        then(department.getParent().getId()).isEqualTo(parent.getId());
    }
}
