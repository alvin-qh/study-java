package alvin.study.springcloud.gateway.client;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.Test;

import alvin.study.springcloud.gateway.client.conf.TestingConfig;
import alvin.study.springcloud.gateway.client.core.model.ResponseWrapper;
import alvin.study.springcloud.gateway.client.endpoint.model.AppInfoDto;
import alvin.study.springcloud.gateway.client.endpoint.model.AuthDto;
import alvin.study.springcloud.gateway.client.util.http.Headers;

/**
 * 测试网关断言
 */
@ActiveProfiles("test")
@SpringBootTest(classes = TestingConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class PredicatesTest {
    // cspell: disable
    // 定义 JWT 测试凭证
    private static final String JWT_VALUE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ0aGlyZC1wYXJ0Iiwic3ViIjoi"
                                            + "dV9iOTg5YzllYS1lMDdiLTQyNjMtYWU5OC0xMWQ2YjFiMGUzMjciLCJpc3MiOiJBbHZpbiIsI"
                                            + "nN1Yl91c2VyX3R5cGUiOiJlbXBsb3llZSIsImV4cCI6OTk5OTk5OTk5OSwic3ViX29yZ19jb2"
                                            + "RlIjoib19hNGVmMzAiLCJpYXQiOjE1MDI5MzkxNzB9.JxVRbYIyAJisOwncRaisEvL8ge51HD"
                                            + "hqfd45SfxLW2I";
    // cspell: enable

    /**
     * 注入 Rest 请求模板对象
     *
     * <p>
     * 该对象启动了服务发现和负载均衡功能, 参见 {@code TestingConfig.restTemplate()} 方法
     * </p>
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试 {@code Path} 断言
     *
     * <p>
     * 参考 {@code classpath:application.yml} 中 {@code id} 为 {@code path_router} 的路由配置
     * </p>
     *
     * <p>
     * 本例中, 网关会将对网关服务 {@code /backend/api/info} 的访问转发到 WEB 服务的 {@code /api/info} 地址上
     * </p>
     */
    @Test
    void path_shouldPredicatesWorked() {
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            try {
                // 发起请求
                var resp = Objects.requireNonNull(
                    restTemplate.exchange(
                        "http://gateway/backend/api/info",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<AppInfoDto>>() {}).getBody());

                // 确认响应正确
                then(resp).extracting(ResponseWrapper::getRetCode, ResponseWrapper::getPath)
                        .contains(0, "/api/info");

                // 确认相应内容符合预期
                var payload = resp.getPayload();
                then(payload.getApplicationName()).isEqualTo("gateway-backend");
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    /**
     * 测试 {@code JWT} 断言
     *
     * <p>
     * 参考 {@code classpath:application.yml} 中 {@code id} 为 {@code jwt_router} 的路由配置
     * </p>
     *
     * <p>
     * 参考 {@code JWTRoutePredicateFactory} 断言工厂类
     * </p>
     *
     * <p>
     * 本例中, 网关会将对网关服务 {@code /auth/**} 且符合如下要求的访问转发到 WEB 服务的 {@code /auth/**} 地址上
     * <ol>
     * <li>
     * 具备 {@code Authorization} 请求头
     * </li>
     * <li>
     * {@code Authorization} 请求头内容为 JWT 凭证且可以正确解码
     * </li>
     * <li>
     * JWT 凭证中包含值为 {@code third-part} 的 {@code audience} 字段
     * </li>
     * </ol>
     * </p>
     */
    @Test
    void jwt_shouldPredicatesWorked() {
        // 设置所需的请求头
        var headers = new HttpHeaders();
        headers.put(Headers.AUTHORIZATION, List.of(Headers.BEARER + " " + JWT_VALUE));
        headers.put(Headers.ACCEPT, List.of(MediaType.APPLICATION_JSON_VALUE));

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            try {
                // 发起请求
                var resp = Objects.requireNonNull(
                    restTemplate.exchange(
                        "http://gateway/auth",
                        HttpMethod.GET,
                        new HttpEntity<>(null, headers),
                        new ParameterizedTypeReference<ResponseWrapper<AuthDto>>() {}).getBody());

                // 确认响应正确
                then(resp).extracting(ResponseWrapper::getRetCode, ResponseWrapper::getPath)
                        .contains(0, "/auth");

                // 确认相应内容符合预期
                var payload = resp.getPayload();
                then(payload.getIssuer()).isEqualTo("Alvin");
                then(payload.getAudience()).isEqualTo("third-part");
                then(payload.getSubject()).isEqualTo("u_b989c9ea-e07b-4263-ae98-11d6b1b0e327");
                then(payload.getSubjectOrgCode()).isEqualTo("o_a4ef30");
                then(payload.getSubjectUserType()).isEqualTo("employee");
                then(payload.getIssuedAt()).isEqualTo(Instant.parse("2017-08-17T03:06:10Z"));
                then(payload.getExpiresAt()).isEqualTo(Instant.parse("2286-11-20T17:46:39Z"));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }
}
