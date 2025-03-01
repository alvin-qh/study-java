package alvin.study.springboot.graphql.app.api.rest;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.util.security.Jwt;

public class LoginControllerTest extends WebTest {
    @Autowired
    private Jwt jwt;

    @Test
    void login_shouldLoginAndGetToken() {
        client().post().uri("/login")
                .bodyValue(new LoginController.LoginForm(currentOrg().getId(), currentUser().getAccount(), "test~123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.payload.token").value(token -> {
                    var payload = jwt.verify((String) token);
                    then(Long.parseLong(payload.getAudience().getFirst())).isEqualTo(currentOrg().getId());
                    then(Long.parseLong(payload.getIssuer())).isEqualTo(currentUser().getId());
                });
    }
}
