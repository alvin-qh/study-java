package alvin.study.springboot.graphql.app.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.UserBuilder;
import alvin.study.springboot.graphql.core.exception.ErrorCode;
import alvin.study.springboot.graphql.core.exception.NotFoundException;
import alvin.study.springboot.graphql.util.security.Jwt;

public class UserServiceTest extends IntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private Jwt jwt;

    @Test
    void create_shouldCreateUser() {
        var expectedUser = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .build();

        userService.create(expectedUser);

        var actualUser = userService.findById(currentOrg().getId(), expectedUser.getId());
        then(actualUser.getId()).isEqualTo(expectedUser.getId());
    }

    @Test
    void delete_shouldDeleteExistUser() {
        var expectedUser = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .create();

        var result = userService.delete(currentOrg().getId(), expectedUser.getId());
        then(result).isTrue();

        thenThrownBy(() -> userService.findById(currentOrg().getId(), expectedUser.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND);
    }

    @Test
    void findById_shouldFindUserById() {
        var expectedUser = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .create();

        var actualUser = userService.findById(currentOrg().getId(), expectedUser.getId());
        then(actualUser.getId()).isEqualTo(expectedUser.getId());
    }

    @Test
    void login_shouldLoginByAccountAndPassword() {
        var user = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .create();

        var token = userService.login(
            currentOrg().getId(),
            user.getAccount(),
            "test~123");

        var payload = jwt.verify(token);
        then(Long.parseLong(payload.getAudience().getFirst())).isEqualTo(currentOrg().getId());
        then(Long.parseLong(payload.getIssuer())).isEqualTo(user.getId());
    }

    @Test
    void update_shouldUpdateExistUser() {
        var user = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .create();

        var originAccount = user.getAccount();

        user.setAccount("updated_" + user.getAccount());
        userService.update(user);

        var actualUser = userService.findById(currentOrg().getId(), user.getId());
        then(actualUser.getAccount()).isEqualTo("updated_" + originAccount);
    }
}
