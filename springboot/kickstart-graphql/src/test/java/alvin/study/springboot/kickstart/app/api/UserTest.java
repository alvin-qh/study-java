package alvin.study.springboot.kickstart.app.api;

import alvin.study.springboot.kickstart.IntegrationTest;
import alvin.study.springboot.kickstart.app.api.mutation.UserMutation;
import alvin.study.springboot.kickstart.app.api.query.UserQuery;
import alvin.study.springboot.kickstart.app.api.schema.input.UserInput;
import alvin.study.springboot.kickstart.app.api.schema.type.UserGroup;
import alvin.study.springboot.kickstart.builder.UserBuilder;
import alvin.study.springboot.kickstart.infra.entity.User;
import alvin.study.springboot.kickstart.util.collection.PathMap;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

/**
 * 测试 {@link UserQuery UserQuery} 和
 * {@link UserMutation UserMutation} 类型
 *
 * <p>
 * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-user.graphql} 文件中定义
 * </p>
 */
class UserTest extends IntegrationTest {
    /**
     * 测试 {@link UserQuery#user(String)
     * UserQuery.user(String)} 方法, 根据 id 查询用户信息
     */
    @Test
    void user_shouldQueryById() throws IOException {
        // 创建待查询的用户实体
        User user;
        try (var ignore = beginTx(false)) {
            user = newBuilder(UserBuilder.class).create();
        }

        // 创建查询参数
        var vars = valueToTree("id", String.valueOf(user.getId()));

        // @formatter:off
        // 执行查询操作并确认结果
        graphql("test-user", "queryUser", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.user")
                .as(PathMap.class)
                .matches(v -> v.get("account").equals(user.getAccount()), "account")
                .matches(v -> v.getByPath("org.id").equals(user.getOrgId().toString()), "org")
                .matches(v -> v.getByPath("createdByUser.account").equals(currentUser().getAccount()), "createdByUser.account")
                .matches(v -> v.getByPath("updatedByUser.account").equals(currentUser().getAccount()), "updatedByUser.account");
        // @formatter:on
    }

    /**
     * 测试 {@link UserMutation#createUser(UserInput)
     * UserMutation.createUser(UserInput)} 方法, 创建一个用户实体
     */
    @Test
    void createUser_shouldMutationExecute() throws IOException {
        // 构建输入参数对象
        var input = UserInput.builder()
                .account("Alvin")
                .password("12345678")
                .group(UserGroup.ADMIN)
                .build();

        // 构建变更参数
        var vars = valueToTree("input", input);

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-user", "createUser", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.createUser.user")
                .as(PathMap.class)
                .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
                .matches(v -> v.get("account").equals(input.getAccount()), "account")
                .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
                .matches(v -> v.getByPath("createdByUser.id").equals(currentUser().getId().toString()), "createdByUser.id")
                .matches(v -> v.getByPath("updatedByUser.id").equals(currentUser().getId().toString()), "updatedByUser.id");
        // @formatter:on
    }

    /**
     * 测试
     * {@link UserMutation#updateUser(String, UserInput)
     * UserMutation.updateUser(String, UserInput)} 方法, 更新一个用户实体
     */
    @Test
    void updateUser_shouldMutationExecute() throws IOException {
        // 创建待更新用户实体
        User user;
        try (var ignore = beginTx(false)) {
            user = newBuilder(UserBuilder.class).create();
        }

        // 构建变更输入对象
        var input = UserInput.builder()
                .account("Alvin-Updated")
                .password("12345678")
                .group(UserGroup.ADMIN)
                .build();

        // 构建变更参数
        var vars = mapToTree(Map.of(
            "id", user.getId(),
            "input", input));

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-user", "updateUser", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.updateUser.user")
                .as(PathMap.class)
                .matches(v -> v.get("account").equals(input.getAccount()), "account")
                .matches(v -> v.getByPath("org.id").equals(currentOrg().getId().toString()), "org.id")
                .matches(v -> v.getByPath("createdByUser.id").equals(currentUser().getId().toString()), "createdByUser.id")
                .matches(v -> v.getByPath("updatedByUser.id").equals(currentUser().getId().toString()), "updatedByUser.id");
        // @formatter:on
    }

    /**
     * 测试
     * {@link UserMutation#deleteUser(String)
     * UserMutation.deleteUser(String)} 方法, 删除一个用户实体
     */
    @Test
    void deleteUser_shouldMutationExecute() throws IOException {
        // 创建待删除用户实体
        User user;
        try (var ignore = beginTx(false)) {
            user = newBuilder(UserBuilder.class).create();
        }

        // 构建删除参数
        var vars = valueToTree("id", user.getId());

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-user", "deleteUser", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.deleteUser")
                .as(PathMap.class)
                .matches(v -> v.get("deleted").equals(true), "deleted");
        // @formatter:on
    }
}
