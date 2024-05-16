package alvin.study.springboot.shiro.app.endpoint;

import alvin.study.springboot.shiro.IntegrationTest;
import alvin.study.springboot.shiro.app.endpoint.model.LoginForm;
import alvin.study.springboot.shiro.app.endpoint.model.TokenDto;
import alvin.study.springboot.shiro.app.endpoint.model.UserDto;
import alvin.study.springboot.shiro.util.security.Jwt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link AuthController} 控制器类型
 */
class AuthControllerTest extends IntegrationTest {
    @Autowired
    private Jwt jwt;

    /**
     * 测试 {@link AuthController#getMe()} 方法, 获取当前登录用户信息
     */
    @Test
    void getMe_shouldReturn200Ok() {
        for (var i = 0; i < 3; i++) {
            // 发起 GET 请求
            var resp = getJson("/auth/me")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserDto.class).returnResult()
                .getResponseBody();

            // 确认响应结果为当前登录用户信息
            then(resp).isNotNull()
                .extracting(UserDto::getAccount, UserDto::getType)
                .contains(currentUser().getAccount(), currentUser().getType());
        }
    }

    /**
     * 测试 {@link AuthController#postLogin(LoginForm)} 方法, 进行用户登录
     */
    @Test
    void postLogin_shouldReturn200Ok() {
        // 发起 POST 请求
        var resp = postJson("/auth/login")
            .bodyValue(new LoginForm(currentUser().getAccount(), RAW_PASSWORD))
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(TokenDto.class).returnResult()
            .getResponseBody();

        // 对返回的 JWT token 字符串进行校验
        var payload = jwt.verify(Objects.requireNonNull(resp).getToken());

        // 确认返回的结果正确
        // 确认正确的用户 ID
        then(payload.getIssuer()).isEqualTo(currentUser().getId().toString());

        // 确认正确的超时时间
        then(resp.getExpiredAt().getEpochSecond()).isEqualTo(payload.getExpiresAtAsInstant().getEpochSecond());
    }
}
