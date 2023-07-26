package alvin.study.springboot.kickstart.app.api.mutation;

import alvin.study.springboot.kickstart.app.api.common.BaseMutation;
import alvin.study.springboot.kickstart.app.api.schema.input.UserInput;
import alvin.study.springboot.kickstart.app.api.schema.payload.CreateUserPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.DeleteUserPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.UpdateUserPayload;
import alvin.study.springboot.kickstart.app.api.schema.type.UserType;
import alvin.study.springboot.kickstart.app.service.UserService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Mutation;
import alvin.study.springboot.kickstart.infra.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 用户实体变更类型
 *
 * <p>
 * Kickstart 框架定义的变更类需要包含如下几个条件:
 * <ul>
 * <li>
 * 实现 {@link graphql.kickstart.tools.GraphQLMutationResolver
 * GraphQLMutationResolver} 接口
 * </li>
 * <li>
 * 具备 {@link org.springframework.stereotype.Component @Component} 注解, 本例中用
 * {@link Mutation @Mutation} 注解替代
 * </li>
 * <li>
 * 在 {@code classpath:graphql/user.graphqls} 中定义查询的 Mutation schema
 * </li>
 * </ul>
 * </p>
 */
@Mutation
@Validated
@RequiredArgsConstructor
public class UserMutation extends BaseMutation {
    /**
     * 注入用户服务类对象
     */
    private final UserService userService;

    /**
     * 创建用户实体
     *
     * @param input 用户信息输入对象
     * @return 用户创建结果
     */
    public CreateUserPayload createUser(@Valid UserInput input) {
        // UserInput => User
        var user = map(input, User.class);

        // 存储用户实体
        userService.create(user);

        // User => CreateUserPayload
        return new CreateUserPayload(map(user, UserType.class));
    }

    /**
     * 更新用户实体
     *
     * @param id    要更新的用户实体 id
     * @param input 用户信息输入对象
     * @return 用户更新结果
     */
    public UpdateUserPayload updateUser(String id, @Valid UserInput input) {
        // UserInput => User
        var user = map(input, User.class);

        // 更新用户实体
        return userService.update(Long.parseLong(id), user)
            .map(u -> new UpdateUserPayload(map(u, UserType.class)))
            .orElseThrow(() -> new InputException("Update User not found"));
    }

    /**
     * 删除用户实体
     *
     * @param id 用户 ID
     * @return 用户删除结果
     */
    public DeleteUserPayload deleteUser(String id) {
        return new DeleteUserPayload(userService.delete(Long.parseLong(id)));
    }
}
