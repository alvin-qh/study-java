package alvin.study.springboot.graphql.app.api.query;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.api.query.common.AuditedBaseQuery;
import alvin.study.springboot.graphql.app.model.UserGroup;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.User;

/**
 * 对应 {@link User} 类型的 GraphQL 查询对象
 *
 * <p>
 * 对应 {@code classpath:graphql/user.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class UserQuery extends AuditedBaseQuery<User> {
    private final UserService userService;

    /**
     * 根据 {@code id} 查询用户 {@link User} 类型对象
     *
     * @param id 用户 id
     * @return {@link User} 类型用户对象
     */
    @QueryMapping
    public User user(@Argument String id) {
        // 查询用户
        return userService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid user id"));
    }

    @SchemaMapping
    public UserGroup group(User user) {
        return UserGroup.of(user.getGroup());
    }
}
