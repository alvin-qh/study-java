package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.api.mutation.common.BaseMutation;
import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.model.UserGroup;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.infra.entity.User;

@Controller
@RequiredArgsConstructor
public class UserMutation extends BaseMutation {
    private final UserService userService;

    /**
     * 用户输入对象类型
     */
    static record UserInput(String account, String password, UserGroup group) {
        public User toEntity(Long id) {
            var user = new User();
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
    public MutationResult<User> createUser(@Argument UserInput input) {
        User user = input.toEntity(null);
        userService.create(user);
        return MutationResult.of(user);
    }

    @MutationMapping
    public MutationResult<User> updateUser(@Argument Long id, @Argument UserInput input) {
        User user = input.toEntity(id);
        return MutationResult.of(userService.update(user));
    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id) {
        return userService.delete(id);
    }
}
