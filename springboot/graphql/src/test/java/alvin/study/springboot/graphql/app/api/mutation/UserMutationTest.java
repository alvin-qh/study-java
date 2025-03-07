package alvin.study.springboot.graphql.app.api.mutation;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.app.model.UserGroup;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.builder.UserBuilder;
import alvin.study.springboot.graphql.core.exception.NotFoundException;
import alvin.study.springboot.graphql.util.security.PasswordUtil;

public class UserMutationTest extends WebTest {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordUtil passwordUtil;

    @Test
    void createUser_shouldCreateNewUser() {
        var input = new UserMutation.UserInput(
            "emma",
            "test~123",
            UserGroup.NORMAL);

        var resp = qlTester().documentName("user")
                .operationName("createUser")
                .variable("input", input)
                .execute();

        var id = resp.path("createUser.result.id")
                .entity(Long.class).get();

        var user = userService.findById(id);

        resp.path("createUser.result")
                .matchesJson("""
                    {
                        "id": "%d",
                        "account": "%s",
                        "group": "%s"
                    }
                    """.formatted(
                    user.getId(),
                    user.getAccount(),
                    user.getGroup().name()));
    }

    @Test
    void deleteUser_shouldDeleteExistUser() {
        var user = newBuilder(UserBuilder.class).create();

        var result = qlTester().documentName("user")
                .operationName("deleteUser")
                .variable("id", user.getId())
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .get();

        then(result).isTrue();

        thenThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUser_shouldUpdateExistUser() throws Exception {
        var expectedUser = newBuilder(UserBuilder.class).create();

        var input = new UserMutation.UserInput(
            "updated_" + expectedUser.getAccount(),
            "test~123",
            UserGroup.NORMAL);

        var resp = qlTester().documentName("user")
                .operationName("updateUser")
                .variable("id", expectedUser.getId())
                .variable("input", input)
                .execute();

        resp.path("updateUser.result")
                .matchesJson("""
                    {
                        "id": "%d",
                        "account": "updated_%s"
                    }
                    """.formatted(
                    expectedUser.getId(),
                    expectedUser.getAccount()));

        var actualUser = userService.findById(expectedUser.getId());
        then(actualUser.getAccount()).isEqualTo("updated_" + expectedUser.getAccount());
        then(actualUser.getPassword()).isEqualTo(passwordUtil.encrypt("test~123"));
    }
}
