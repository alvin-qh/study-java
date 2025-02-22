package alvin.study.springboot.jpa.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.builder.OrgBuilder;
import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.Org;

/**
 * 测试 {@link DepartmentRepository} 的增删查改操作
 */
class DepartmentRepositoryTest extends IntegrationTest {
    // 注入存储操作对象
    @Autowired
    private DepartmentRepository repository;

    /**
     * 测试对 {@link Department Department} 对象进行存储操作
     *
     * @see DepartmentBuilder#build()
     * @see org.springframework.data.repository.CrudRepository#save(Object)
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     */
    @Test
    void create_shouldCreateEntity() {
        // 产生一个实体对象
        var expected = newBuilder(DepartmentBuilder.class).build();

        // 启动测试事务
        try (var ignore = beginTx(false)) {
            // 持久化实体对象
            repository.save(expected);
        }
        // 确认实体已经持久化, 产生 id 属性值
        then(expected.getId()).isNotNull();

        // 通过 id 属性查询实体对象
        var mayActual = repository.findById(expected.getId());
        // 确认查询结果和持久化对象一致
        then(mayActual).isPresent().get().isEqualTo(expected);
    }

    /**
     * 测试更新实体对象
     *
     * @see DepartmentBuilder#create()
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     */
    @Test
    void update_shouldUpdateEntity() {
        long id;
        try (var ignored = beginTx(false)) {
            // 创建一个实体对象并保存其 id 属性
            id = newBuilder(DepartmentBuilder.class).name("RD").create().getId();
        }

        try (var ignore = beginTx(false)) {
            // 根据 id 属性查询实体对象
            var mayDepartment = repository.findById(id);
            // 确认 name 属性为修改前的值
            then(mayDepartment).isPresent().get().extracting("name").isEqualTo("RD");

            // 修改 name 属性
            mayDepartment.get().setName("Sales");
        }

        try (var ignore = beginTx(true)) {
            // 根据 id 属性查询实体对象
            var mayDepartment = repository.findById(id);

            // 确认 name 属性为修改后的值
            then(mayDepartment).isPresent().get().extracting("name").isEqualTo("Sales");
        }
    }

    /**
     * 测试删除实体
     *
     * @see DepartmentBuilder#create()
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     * @see org.springframework.data.repository.CrudRepository#delete(Object)
     */
    @Test
    void delete_shouldDeleteEntity() {
        long id;

        try (var ignored = beginTx(false)) {
            // 创建一个实体对象并保存其 id 属性
            id = newBuilder(DepartmentBuilder.class).create().getId();
        }

        try (var ignore = beginTx(false)) {
            // 根据 id 属性查询实体对象
            var mayDepartment = repository.findById(id);

            // 确认实体可以被查询到
            then(mayDepartment).isPresent();

            // 删除实体对象
            repository.delete(mayDepartment.get());
        }

        try (var ignore = beginTx(true)) {
            // 根据 id 属性查询实体对象
            var mayDepartment = repository.findById(id);

            // 确认实体对象无法被查询到 (已被删除)
            then(mayDepartment).isEmpty();
        }
    }

    /**
     * 测试 {@link DepartmentRepository#findAll()} 方法
     *
     * <p>
     * 该方法的语义是获取所有的 {@link Department Department} 实体对象,
     * 但在多租户 Filter 的作用下会在查询上增加 {@code org_id=:orgId} 条件, 结果是只会查询到当前租户下的所有实体对象
     * </p>
     */
    @Test
    @Transactional
    void findAll_shouldFindAllEntities() {
        // 创建一个新的组织实体作为新租户
        Org org = newBuilder(OrgBuilder.class).create();

        // 在新建的租户下创建 10 个部门实体
        for (var i = 0; i < 10; i++) {
            newBuilder(DepartmentBuilder.class).name("DEP_" + i).withOrgId(org.getId()).create();
        }
        flushEntityManager();

        // 上下文切换到新租户
        try (var ignore = switchContext(org, null)) {
            // 查询所有的部门实体对象
            var departments = repository.findAll();
            // 确认当前租户下有 10 个部门实体对象
            then(departments).hasSize(10);

            // 确认查询到的 10 个部门实体对象是按 id 顺序升序排序
            long lastId = 0;
            for (var department : departments) {
                then(department.getId()).isGreaterThan(lastId);
                lastId = department.getId();
            }
        }

        // 切换回原租户后, 在此查询所有的部门实体对象
        var departments = repository.findAll();
        // 确认此次查询未查询到任何实体对象
        then(departments).isEmpty();
    }
}
