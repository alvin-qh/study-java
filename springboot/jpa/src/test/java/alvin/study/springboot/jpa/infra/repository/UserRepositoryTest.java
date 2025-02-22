package alvin.study.springboot.jpa.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.OrgBuilder;
import alvin.study.springboot.jpa.builder.UserBuilder;
import alvin.study.springboot.jpa.infra.entity.Org;
import alvin.study.springboot.jpa.infra.entity.User;

class UserRepositoryTest extends IntegrationTest {
    @Autowired
    private UserRepository repository;

    /**
     * 测试 {@link UserRepository#findAll()} 方法
     *
     * <p>
     * 该方法的语义是获取所有的 {@link User User} 实体对象,
     * 但在多租户 Filter 的作用下会在查询上增加 {@code org_id=:orgId} 条件, 结果是只会查询到当前租户下的所有实体对象
     * </p>
     */
    @Test
    @Transactional
    void findAll_shouldFindEntities() {
        // 创建一个新的组织实体作为新租户
        Org org = newBuilder(OrgBuilder.class).create();

        // 在新建的租户下创建 10 个用户实体
        for (var i = 0; i < 10; i++) {
            newBuilder(UserBuilder.class).withOrgId(org.getId()).create();
        }
        flushEntityManager();

        // 上下文切换到新租户
        try (var ignore = switchContext(org, null)) {
            var users = repository.findAll();
            then(users).hasSize(10);

            // 确认查询到的 10 个用户实体对象是按 id 顺序升序排序
            long lastId = 0;
            for (var user : users) {
                then(user.getId()).isGreaterThan(lastId);
                lastId = user.getId();
            }
        }

        // 切换回原租户后, 在此查询所有的用户实体对象
        var users = repository.findAll();
        // 查询到一个实体, 是在 IntegrationTest 类中创建的
        then(users).hasSize(1);
    }

    /**
     * 测试 {@link UserRepository#findByAccount()} 方法
     */
    @Test
    @Transactional
    void findByAccount_shouldFindEntity() {
        // 构建一个用户实体
        var expected = newBuilder(UserBuilder.class).withAccount("alvin").create();
        flushEntityManager();

        // 根据账号查询用户实体
        var mayActual = repository.findByAccount("alvin");
        // 确认查询到实体
        then(mayActual).isPresent().get().isEqualTo(expected);
    }
}
