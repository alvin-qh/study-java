package alvin.study.springboot.jpa.app.domain.service;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.UserBuilder;
import alvin.study.springboot.jpa.infra.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 对 UserService 进行测试
 */
class UserServiceTest extends IntegrationTest {
    @Autowired
    private UserService service;

    /**
     * 测试
     * {@link UserService#searchUsers(String, String, org.springframework.data.domain.Pageable)
     * UserService.searchUsers(String, String, Pageable)} 方法, 根据已知的 account 和
     * password 值查询 {@link User User} 实体对象分页结果
     */
    @Test
    void shouldSearchUserByConditions() {
        var account = "Alvin";
        var password = "aabbccdd"; // cspell: disable-line

        User user;
        try (var ignore = beginTx(false)) {
            // 根据所给的 account 和 password 创建 User 对象
            user = newBuilder(UserBuilder.class)
                    .withAccount(account)
                    .withPassword(password)
                    .create();
        }

        // 创建分页对象, 当前页为第 1 页 (页码从 0 开始), 每页 20 条记录
        var pageable = PageRequest.of(0, 20);

        // 根据所给条件查询 User 结果
        var page = service.searchUsers(account, password, pageable);

        // 确认查询结果页面为第 1 页 (页码从 0 开始)
        then(page.getNumber()).isZero();

        // 确认查询结果每页预期包含 20 条记录 (每页预设大小)
        then(page.getSize()).isEqualTo(20);

        // 确认查询结果包含 1 条记录
        then(page.getNumberOfElements()).isOne();

        // 确认数据表中总共包含 1 条符合查询条件的结果 (总记录数)
        then(page.getTotalElements()).isOne();

        // 确认数据表中总共包含 1 页查询结果 (总页数)
        then(page.getTotalPages()).isOne();

        // 获取查询结果记录, 确认和写入的记录一致
        then(page.getContent()).containsExactly(user);
    }
}
