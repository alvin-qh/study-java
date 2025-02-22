package alvin.study.springboot.testing.app.controller;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Duration;

import org.assertj.core.api.InstanceOfAssertFactories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.testing.common.ResponseWrapper;
import alvin.study.springboot.testing.common.ResponseWrapper.ErrorDetail;
import alvin.study.springboot.testing.model.TestModel;

/**
 * 通过 {@link WebTestClient} 工具类进行 HTTP 测试
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试, 其
 * {@code classes} 属性指定了该测试相关的配置类, {@code webEnvironment} 属性指定了测试 Web
 * 服务以本地随机端口启动. 这种方式会启动一个测试用的 HTTP 服务, 并通过 {@link WebTestClient} 对象进行 HTTP 请求访问,
 * 并对访问结果进行验证
 * </p>
 *
 * <p>
 * {@link AutoConfigureWebTestClient @AutoConfigureWebTestClient} 注解表示该测试会注入
 * {@link WebTestClient} 测试客户端对象, 需要对其进行自动配置
 * </p>
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebTestClientTest {
    // 用于 mock 当前时间
    private static final String CLOCK = "2022-10-01T08:00:00Z";

    // 注入测试工具对象
    @Autowired
    private WebTestClient client;

    /**
     * 测试正确的 Web 调用, 返回 200 OK
     *
     * <p>
     * {@link WebTestClient#mutate()} 方法产生一个 {@link WebTestClient.Builder} 对象, 用于在原有
     * {@link WebTestClient} 对象的基础上通过 {@link WebTestClient.Builder#build()}
     * 方法构建一个新对象
     * </p>
     *
     * <p>
     * {@link WebTestClient.Builder#responseTimeout(Duration)} 方法用于设置接收响应的超时时间
     * </p>
     *
     * <p>
     * {@link WebTestClient#get()} 方法表示将要执行一个 {@code HTTP GET} 请求操作, 返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec
     * RequestHeadersUriSpec} 对象表示一个包含 Header 和 URI 的请求对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec#uri(String, Object...)
     * RequestHeadersUriSpec.uri(String, Object...)} 方法用于为请求设置 URI 值, 即要访问的测试地址,
     * 并为地址中的请求参数设置实际的参数值
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec#exchange()
     * RequestHeadersSpec.exchange()} 方法用于将请求"转换为"响应对象, 即对指定地址进行请求, 并返回结果, 该方法返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
     * ResponseSpec} 对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec#expectStatus()
     * ResponseSpec.expectStatus()} 用于对响应的 HTTP 状态进行断言. 本例中的
     * {@link org.springframework.test.web.reactive.server.StatusAssertions#isOk()
     * StatusAssertions.isOk()} 表示期望响应状态为 {@code 200 OK}
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec#expectBody(ParameterizedTypeReference)
     * ResponseSpec.expectBody(ParameterizedTypeReference)} 方法用于对响应中的 Body 通过一个
     * {@link ParameterizedTypeReference} 对象指定的类型进行反序列化处理. 该方法返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.BodySpec
     * BodySpec} 对象, 表示转换后的 Body 结果
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.BodySpec#returnResult()
     * BodySpec.returnResult()} 方法将 Body 以
     * {@link org.springframework.test.web.reactive.server.EntityExchangeResult
     * EntityExchangeResult} 类型对象进行返回, 再通过
     * {@link org.springframework.test.web.reactive.server.EntityExchangeResult#getResponseBody()
     * EntityExchangeResult.getResponseBody()} 方法转换为期望类型的对象
     * </p>
     */
    @Test
    void webTestClient_shouldGetResponse() {
        // 定义本次请求返回的类型
        var bodyType = new ParameterizedTypeReference<ResponseWrapper<TestModel>>() {};

        // 执行测试
        var resp = client.mutate().responseTimeout(Duration.ofSeconds(30)).build()
                .get()
                .uri("/testing?name={name}&clock={clock}", "Alvin", CLOCK)
                .exchange()
                .expectStatus().isOk()
                .expectBody(bodyType).returnResult()
                .getResponseBody();

        then(resp).isNotNull()
                .extracting("path", "retCode", "retMsg")
                .containsExactly("/testing", 0, "success");

        var assertion = then(resp.getPayload()).isNotNull();
        assertion.extracting("id").isNotNull();
        assertion.extracting("name").isEqualTo("Alvin");
        assertion.extracting("timestamp").hasToString("2022-10-01T08:00:00Z");
    }

    /**
     * 测试错误的 Web 调用, 返回 400 BAD_REQUEST
     *
     * <p>
     * {@link WebTestClient#mutate()} 方法产生一个 {@link WebTestClient.Builder} 对象, 用于在原有
     * {@link WebTestClient} 对象的基础上通过 {@link WebTestClient.Builder#build()}
     * 方法构建一个新对象
     * </p>
     *
     * <p>
     * {@link WebTestClient.Builder#responseTimeout(Duration)} 方法用于设置接收响应的超时时间
     * </p>
     *
     * <p>
     * {@link WebTestClient#get()} 方法表示将要执行一个 {@code HTTP GET} 请求操作, 返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec
     * RequestHeadersUriSpec} 对象表示一个包含 Header 和 URI 的请求对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec#uri(String, Object...)
     * RequestHeadersUriSpec.uri(String, Object...)} 方法用于为请求设置 URI 值, 即要访问的测试地址,
     * 并为地址中的请求参数设置实际的参数值
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec#exchange()
     * RequestHeadersSpec.exchange()} 方法用于将请求"转换为"响应对象, 即对指定地址进行请求, 并返回结果, 该方法返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
     * ResponseSpec} 对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec#expectStatus()
     * ResponseSpec.expectStatus()} 用于对响应的 HTTP 状态进行断言. 本例中的
     * {@link org.springframework.test.web.reactive.server.StatusAssertions#isBadRequest()
     * StatusAssertions.isBadRequest()} 表示期望响应状态为 {@code 400 BAD_REQUEST}
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec#expectBody(ParameterizedTypeReference)
     * ResponseSpec.expectBody(ParameterizedTypeReference)} 方法用于对响应中的 Body 通过一个
     * {@link ParameterizedTypeReference} 对象指定的类型进行反序列化处理. 该方法返回一个
     * {@link org.springframework.test.web.reactive.server.WebTestClient.BodySpec
     * BodySpec} 对象, 表示转换后的 Body 结果
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.reactive.server.WebTestClient.BodySpec#returnResult()
     * BodySpec.returnResult()} 方法将 Body 以
     * {@link org.springframework.test.web.reactive.server.EntityExchangeResult
     * EntityExchangeResult} 类型对象进行返回, 再通过
     * {@link org.springframework.test.web.reactive.server.EntityExchangeResult#getResponseBody()
     * EntityExchangeResult.getResponseBody()} 方法转换为期望类型的对象
     * </p>
     */
    @Test
    void webTestClient_shouldGetResponseWithoutAnyQueryParameters() {
        // 定义本次请求返回的类型
        var bodyType = new ParameterizedTypeReference<ResponseWrapper<ErrorDetail>>() {};

        // 执行测试
        var resp = client.mutate().responseTimeout(Duration.ofSeconds(30)).build()
                .get()
                .uri("/testing")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(bodyType).returnResult()
                .getResponseBody();

        then(resp).isNotNull()
                .extracting("path", "retCode", "retMsg")
                .contains("/testing", 400, "missing_request_args");

        var payload = resp.getPayload();
        then(payload.getErrorParameters())
                .extractingByKey("name")
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly("Required request parameter 'name' for method parameter type String is not present");
    }
}
