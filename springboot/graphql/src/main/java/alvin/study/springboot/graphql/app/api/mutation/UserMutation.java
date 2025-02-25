package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.UserGroup;

@Controller
@RequiredArgsConstructor
public class UserMutation {
    private final UserService userService;

    /**
     * 用户输入对象
     */
    static record UserInput(String account, String password, UserGroup group) {}

    @MutationMapping
    public MutationResult<User> createUser(@Argument UserInput input) {
        User user = new User();
        user.setAccount(input.account());
        user.setPassword(input.password());
        user.setGroup(switch (input.group()) {
        case ADMIN -> UserGroup.ADMIN;
        case OPERATOR -> UserGroup.OPERATOR;
        case NORMAL -> UserGroup.NORMAL;
        });
        user.setOrgId(1L);
        userService.create(user);
        return MutationResult.of(user);
    }
}
