package alvin.study.springboot.mvc.app.api.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.assertj.core.api.InstanceOfAssertFactories;

import org.springframework.core.ParameterizedTypeReference;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.mvc.WebTest;
import alvin.study.springboot.mvc.app.api.model.HelloDto;
import alvin.study.springboot.mvc.app.api.model.HelloForm;
import alvin.study.springboot.mvc.core.http.ResponseWrapper;

/**
 * 测试 {@link HelloController} 类
 */
class HelloControllerTest extends WebTest {
    // 定义正确的响应类型
    private static final ParameterizedTypeReference<ResponseWrapper<HelloDto>> SUCCESS_TYPE
        = new ParameterizedTypeReference<>() {};

    /**
     * 测试 {@link HelloController#get(String)} 方法
     *
     * <p>
     * 正常请求, 期待 200 正确结果
     * </p>
     */
    @Test
    void get_shouldReturn200Successful() {
        // 发起 GET 测试请求
        var resp = getJson("/api/hello?name={name}", "Alvin")
                .exchange()
                .expectStatus().isOk() // 确认请求成功, 返回 200 OK
                .expectBody(SUCCESS_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(0);
        then(resp.retMsg()).isEqualTo("success");

        // 确认 payload 的信息, 为 HelloDto 类型
        var payload = resp.payload();
        then(payload.name()).isEqualTo("Alvin");
        then(payload.greeting()).isEqualTo("Hello");
    }

    /**
     * 测试 {@link HelloController#get(String)} 方法
     *
     * <p>
     * 传入错误请求参数, 期待 400 错误响应
     * </p>
     */
    @Test
    void get_shouldReturn400Error() {
        // 发起 GET 测试请求
        var resp = getJson("/api/hello?name={name}", "Al")
                .exchange()
                .expectStatus().is4xxClientError() // 返回 400 错误
                .expectBody(ERROR_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(400);
        then(resp.retMsg()).isEqualTo("invalid_request_args");

        // 确认 payload 的信息, 为 ErrorDetail 类型
        var payload = resp.payload();
        then(payload.errorParameters())
                .extractingByKey("name")
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly("length must be between 3 and 10");
    }

    /**
     * 测试 {@link HelloController#post(HelloForm)} 方法
     *
     * <p>
     * 正常请求, 期待 200 结果
     * </p>
     */
    @Test
    void post_shouldReturn200Successful() {
        // 请求的 form 类型
        var form = new HelloForm("Alvin");

        // 发起 POST 测试请求
        var resp = postJson("/api/hello").bodyValue(form).exchange()
                .expectStatus().isOk()
                .expectBody(SUCCESS_TYPE)
                .returnResult()
                .getResponseBody();

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(0);
        then(resp.retMsg()).isEqualTo("success");

        // 确认 payload 的信息, 为 HelloDto 类型
        var payload = resp.payload();
        then(payload.name()).isEqualTo("Alvin");
        then(payload.greeting()).isEqualTo("Welcome");
    }

    /**
     * 测试 {@link HelloController#post(HelloForm)} 方法
     *
     * <p>
     * 传递错误表单项, 期待 400 错误结果
     * </p>
     */
    @Test
    void post_shouldReturn400Error() {
        // 请求的 form 类型
        var form = new HelloForm("Al");

        // 发起 POST 测试请求
        var resp = postJson("/api/hello")
                .bodyValue(form).exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ERROR_TYPE).returnResult()
                .getResponseBody();

        // 确认返回响应内容
        then(resp).isNotNull();

        // 确认 response 的返回代码和信息
        then(resp.retCode()).isEqualTo(400);
        then(resp.retMsg()).isEqualTo("invalid_request_args");

        // 确认 payload 的信息, 为 ErrorDetail 类型
        var payload = resp.payload();
        then(payload.errorFields())
                .extractingByKey("name")
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly("length must be between 3 and 10");
    }
}
