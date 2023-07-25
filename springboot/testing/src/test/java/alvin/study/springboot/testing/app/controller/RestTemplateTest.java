package alvin.study.springboot.testing.app.controller;

import alvin.study.springboot.testing.common.ResponseWrapper;
import alvin.study.springboot.testing.model.TestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static alvin.study.springboot.testing.common.ResponseWrapper.ErrorDetail;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 通过 {@link TestRestTemplate} 工具类进行 HTTP 测试
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试, 其
 * {@code classes} 属性指定了该测试相关的配置类, {@code webEnvironment} 属性指定了测试 Web
 * 服务以本地随机端口启动. 这种方式会启动一个测试用的 HTTP 服务, 并通过一个类似
 * {@link org.springframework.web.client.RestTemplate RestTemplate} 的测试客户端
 * {@link TestRestTemplate} 对象进行 HTTP 请求访问, 并对访问结果进行验证
 * </p>
 *
 * <p>
 * 启动服务使用的随机端口可以通过注入 {@link LocalServerPort} 类型对象获得
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
class RestTemplateTest {
    // 用于 mock 当前时间
    private static final String CLOCK = "2022-10-01T08:00:00Z";

    // 获取本次测试的随机端口号
    @LocalServerPort
    private int port;

    // 注入测试工具对象
    @Autowired
    private TestRestTemplate restTemplate;

    // 用于 Map 转 Pojo 对象
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试正确的 Web 调用, 返回 200 OK
     *
     * @see TestRestTemplate#getForObject(String, Class, Object...)
     * @see ObjectMapper#convertValue(Object, Class)
     */
    @Test
    void restTemplate_shouldGetResponse() {
        // 拼装要测试 Controller 的访问地址
        var url = String.format("http://localhost:%d/testing?name={name}&clock={clock}", this.port);

        // 请求地址, 执行 Controller 方法, 期待返回 ResponseWrapper 类型对象
        var resp = this.restTemplate.getForObject(url, ResponseWrapper.class, "Alvin", CLOCK);
        then(resp.getPath()).isEqualTo("/testing");
        then(resp.getRetCode()).isZero();
        then(resp.getRetMsg()).isEqualTo("success");
        then(resp.getPayload()).isInstanceOf(Map.class); // payload 字段以 Map 形式返回

        // 将返回的 payload 字段转为 TestModel 类型对象
        var payload = objectMapper.convertValue(resp.getPayload(), TestModel.class);
        then(payload.getId()).isNotNull();
        then(payload.getName()).isEqualTo("Alvin");
        then(payload.getTimestamp()).hasToString("2022-10-01T08:00:00Z");
    }

    /**
     * 测试错误的 Web 调用, 返回 400 BAD_REQUEST
     *
     * @see TestRestTemplate#getForObject(String, Class, Object...)
     * @see ObjectMapper#convertValue(Object, Class)
     */
    @Test
    void restTemplate_shouldGetResponseWithoutAnyQueryParameters() {
        // 拼装要测试 Controller 的访问地址
        var url = String.format("http://localhost:%d/testing", this.port);

        // 请求地址, 执行 Controller 方法, 期待返回 ResponseWrapper 类型对象
        var resp = this.restTemplate.getForObject(url, ResponseWrapper.class);
        then(resp.getPath()).isEqualTo("/testing");
        then(resp.getRetCode()).isEqualTo(400);
        then(resp.getRetMsg()).isEqualTo("missing_request_args");
        then(resp.getPayload()).isInstanceOf(Map.class);

        // 将返回的 payload 字段转为 ErrorDetail 类型对象
        var payload = objectMapper.convertValue(resp.getPayload(), ErrorDetail.class);
        then(payload.getErrorParameters())
            .extractingByKey("name")
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly("Required request parameter 'name' for method parameter type String is not present");
    }
}
