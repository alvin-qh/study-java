package alvin.study.springboot.mvc.app.api.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import alvin.study.springboot.mvc.WebTest;
import alvin.study.springboot.mvc.app.api.model.ContextDto;
import alvin.study.springboot.mvc.core.http.ResponseWrapper;
import alvin.study.springboot.mvc.util.security.Jwt;

/**
 * 测试 {@link ContextController}, 请求上下文的获取
 */
class ContextControllerTest extends WebTest {
    // 定义正确的响应类型
    private static final ParameterizedTypeReference<ResponseWrapper<ContextDto>> SUCCESS_TYPE
        = new ParameterizedTypeReference<>() {};

    @Autowired
    private Jwt jwt;

    /**
     * 测试 {@link ContextController#get()} 方法, 返回 token 中包含的信息
     *
     * <p>
     * 测试在请求 Header 中包含 {@code Authorization} 时, 返回正确响应
     * </p>
     */
    @Test
    void get_shouldAuthInfoFromContext() {
        var token = jwt.encode("alvin.org", "1001");

        // 发起 GET 测试请求
        var resp = getJson("/api/context")
                .header("Authorization", "Bearer " + token).exchange()
                .expectStatus().isOk() // 返回成功
                .expectBody(SUCCESS_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 payload 的信息, 为 ContextDto 类型
        var payload = resp.payload();
        then(payload.orgCode()).isEqualTo("alvin.org");
        then(payload.userId()).isEqualTo(1001L);
    }

    /**
     * 测试 {@link ContextController#get()} 方法
     *
     * <p>
     * 测试在请求 Header 中未包含 {@code Authorization} 时, 返回 400 错误响应
     * </p>
     */
    @Test
    void get_shouldGet400ErrorWhenTokenMissed() {
        // 发起 GET 测试请求
        var resp = getJson("/api/context")
                .exchange()
                .expectStatus().is4xxClientError() // 返回 400 错误
                .expectBody(ERROR_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(400);
        then(resp.retMsg()).isEqualTo("no_org_code");
    }

    /**
     * 测试 {@link ContextController#get()} 方法
     *
     * <p>
     * 测试在请求 Header 中包含无效的 {@code Authorization} 时, 返回 400 错误响应
     * </p>
     */
    @Test
    void get_shouldGet400ErrorWhenTokenIsInvalid() {
        // 发起 GET 测试请求
        var resp = getJson("/api/context")
                .header("Authorization", "abcd")
                .exchange()
                .expectStatus().is4xxClientError() // 返回 400 错误
                .expectBody(ERROR_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(400);
        then(resp.retMsg()).isEqualTo("invalid_bearer_token");
    }
}
