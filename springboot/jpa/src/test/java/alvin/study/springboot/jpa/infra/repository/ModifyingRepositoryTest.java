package alvin.study.springboot.jpa.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.UserBuilder;
import alvin.study.springboot.jpa.util.security.PasswordUtil;

/**
 * 测试通过显式声明的 HQL 语句完成查询, 修改和删除操作
 *
 * @see ModifyingRepository
 */
class ModifyingRepositoryTest extends IntegrationTest {
    // 注入数据存储对象
    @Autowired
    private ModifyingRepository repository;

    // 注入密码处理对象
    @Autowired
    private PasswordUtil passwordUtil;

    /**
     * 测试通过 HQL 进行数据修改操作
     *
     * @see ModifyingRepository#updatePasswordById(Long,
     *      String) ModifyingRepository.updatePasswordById(Long, String)
     */
    @Test
    @SneakyThrows
    void updatePasswordById_shouldUpdateEntity() {
        long id;
        try (var _ = beginTx(false)) {
            // 创建 User 对象并获取其 id 属性
            id = newBuilder(UserBuilder.class).create().getId();
        }

        try (var _ = beginTx(false)) {
            // 将 id 匹配的实体对象 password 属性改为新值 cspell: disable-next-line
            repository.updatePasswordById(id, passwordUtil.encrypt("xxxyyyzzz"));
        }

        // 根据 id 属性查询实体对象
        var mayUser = repository.findById(id);
        then(mayUser).isPresent();

        // 确认查询结果的 password 属性已被更新 cspell: disable-next-line
        then(passwordUtil.verify("xxxyyyzzz", mayUser.get().getPassword())).isTrue();
    }

    /**
     * 测试通过 HQL 语句查询和删除实体对象
     *
     * @see ModifyingRepository#findAllByAccount(String) ModifyingRepository.findAllByAccount(String)
     * @see ModifyingRepository#deleteAllByAccount(String) ModifyingRepository.deleteAllByAccount(String)
     */
    @Test
    void deleteAllByAccount_shouldDeleteEntities() {
        String account;

        try (var _ = beginTx(false)) {
            // 创建 User 对象并获取 account 属性值
            account = newBuilder(UserBuilder.class).create().getAccount();
        }

        // 通过 account 属性值查询符合条件的所有 User 对象, 确认结果包含一个实体
        var users = repository.findAllByAccount(account);
        then(users).hasSize(1);

        try (var _ = beginTx(false)) {
            // 通过 account 属性删除所有对应的 User 对象
            repository.deleteAllByAccount(account);
        }

        // 再次根据 account 属性值进行查询, 无法查询对应的 User 对象
        users = repository.findAllByAccount(account);
        then(users).isEmpty();
    }
}
