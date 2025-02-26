package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.context.ContextKey;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.UserGroup;
import graphql.GraphQLContext;

@Controller
@RequiredArgsConstructor
public class UserMutation {
    private final UserService userService;

    /**
     * 用户输入对象类型
     */
    static record UserInput(String account, String password, UserGroup group) {}

    @MutationMapping
    public MutationResult<User> createUser(@Argument UserInput input, GraphQLContext ctx) {
        User user = new User();
        user.setAccount(input.account());
        user.setPassword(input.password());
        user.setOrgId(ctx.<Org>get(ContextKey.ORG).getId());
        user.setGroup(switch (input.group()) {
        case ADMIN -> UserGroup.ADMIN;
        case OPERATOR -> UserGroup.OPERATOR;
        case NORMAL -> UserGroup.NORMAL;
        });

        userService.create(user);
        return MutationResult.of(user);
    }

    @MutationMapping
    public MutationResult<User> updateUser(@Argument Long id, @Argument UserInput input, GraphQLContext ctx) {
        var user = new User();
        user.setAccount(input.account());
        user.setPassword(input.password());
        user.setGroup(switch (input.group()) {
        case ADMIN -> UserGroup.ADMIN;
        case OPERATOR -> UserGroup.OPERATOR;
        case NORMAL -> UserGroup.NORMAL;
        });

        ;
        return MutationResult.of(userService.update(ctx.<Org>get(ContextKey.ORG).getId(), id, user));
    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id, GraphQLContext ctx) {
        return userService.delete(ctx.<Org>get(ContextKey.ORG).getId(), id);
    }
}
