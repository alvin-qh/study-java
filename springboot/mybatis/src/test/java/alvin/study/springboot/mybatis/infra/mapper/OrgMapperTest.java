package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.OrgBuilder;
import alvin.study.springboot.mybatis.infra.entity.Org;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 对 {@link OrgMapper} 类型进行测试
 */
class OrgMapperTest extends IntegrationTest {
    // 注入 Mapper 对象
    @Autowired
    private OrgMapper mapper;

    /**
     * 测试 {@link OrgMapper#selectByName(String)} 方法
     *
     * <p>
     * {@link Transactional @Transactional} 注解表示当前方法要启动事务
     * </p>
     */
    @Test
    @Transactional
    void shouldSelectByNameWorked() {
        var name = "Alvin Study Co.,";

        // 创建一个 Org 实体对象
        var expected = newBuilder(OrgBuilder.class).withName(name).create();
        then(expected.getId()).isNotNull();

        // 清除一级缓存
        clearSessionCache();

        // 根据 name 属性查询 Org 实体对象
        var mayActual = mapper.selectByName(name);
        // 确认查询成功
        then(mayActual).isPresent();

        // 获取查询结果
        var actual = mayActual.get();
        // 确认查询到的实体对象和创建的实体对象一致
        then(actual.getId()).isEqualTo(expected.getId());
    }

    /**
     * 测试 {@link OrgMapper#update(Org)
     * OrgMapper.update(Org)} 方法
     *
     * <p>
     * {@link Transactional @Transactional} 注解表示当前方法要启动事务
     * </p>
     */
    @Test
    @Transactional
    void shouldUpdateEntity() {
        // 创建一个 Org 对象
        var org = newBuilder(OrgBuilder.class).withName("Alvin Study Co.,").create();
        then(org.getId()).isNotNull();

        // 清除一级缓存
        clearSessionCache();

        // 修改 Org 实体对象的 name 属性
        org.setName("Emma Study Co.,");

        // 执行更新操作
        mapper.update(org);

        // 根据 id 重新查询 Org 实体对象
        var mayOrg = mapper.selectById(org.getId());
        then(mayOrg).isPresent();

        // 确认查询结果的 name 属性已经被修改
        org = mayOrg.get();
        then(org.getName()).isEqualTo("Emma Study Co.,");
    }

    /**
     * 测试 {@link OrgMapper#delete(Org)
     * OrgMapper.delete(Org)} 方法
     *
     * <p>
     * {@link Transactional @Transactional} 注解表示当前方法要启动事务
     * </p>
     */
    @Test
    @Transactional
    void shouldDeleteEntity() {
        // 创建一个 Org 对象
        var org = newBuilder(OrgBuilder.class).withName("Alvin Study Co.,").create();
        then(org.getId()).isNotNull();

        // 清除一级缓存
        clearSessionCache();

        // 删除 Org 实体对象
        mapper.delete(org);

        // 确认实体对象已被删除
        var mayOrg = mapper.selectById(org.getId());
        then(mayOrg).isEmpty();
    }
}
