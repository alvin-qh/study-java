package alvin.study.springboot.graphql.app.api.mutation;

import jakarta.annotation.Nullable;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import graphql.GraphQLContext;

import alvin.study.springboot.graphql.app.api.mutation.common.BaseMutation;
import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.model.UserGroup;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;

@Controller
@RequiredArgsConstructor
public class UserMutation extends BaseMutation {
    private final UserService userService;

    /**
     * 用户输入对象类型
     */
    static record UserInput(String account, String password, UserGroup group) {
        public User toEntity(GraphQLContext ctx, @Nullable Long id) {
            var user = completeAuditedEntity(new User(), ctx);
            user.setId(id);
            user.setAccount(account);
            user.setPassword(password);
            user.setGroup(switch (group) {
            case ADMIN -> alvin.study.springboot.graphql.infra.entity.UserGroup.ADMIN;
            case OPERATOR -> alvin.study.springboot.graphql.infra.entity.UserGroup.OPERATOR;
            case NORMAL -> alvin.study.springboot.graphql.infra.entity.UserGroup.NORMAL;
            });
            return user;
        }
    }

    @MutationMapping
    public MutationResult<User> createUser(@Argument UserInput input, GraphQLContext ctx) {
        User user = input.toEntity(ctx, null);
        userService.create(user);
        return MutationResult.of(user);
    }

    @MutationMapping
    public MutationResult<User> updateUser(@Argument Long id, @Argument UserInput input, GraphQLContext ctx) {
        User user = input.toEntity(ctx, id);
        return MutationResult.of(userService.update(user));
    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id, GraphQLContext ctx) {
        return userService.delete(ctx.<Org>get(ContextKey.ORG).getId(), id);
    }
}
