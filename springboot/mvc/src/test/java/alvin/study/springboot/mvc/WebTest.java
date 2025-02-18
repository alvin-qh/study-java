package alvin.study.springboot.mvc;

import alvin.study.springboot.mvc.conf.TestConfig;
import alvin.study.springboot.mvc.core.http.ResponseWrapper;
import alvin.study.springboot.mvc.core.http.ResponseWrapper.ErrorDetail;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec;

import java.time.Duration;

/**
 * Web 测试的超类
 *
 * <p>
 * Spring Boot 提供了 {@link WebTestClient} 类型对 Controller 进行测试
 * </p>
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试, 其
 * {@code classes} 属性指定了该测试相关的配置类, {@code webEnvironment} 属性指定了测试 Web
 * 服务以本地随机端口启动
 * </p>
 *
 * <p>
 * {@link AutoConfigureWebTestClient @AutoConfigureWebTestClient} 注解表示该测试会注入
 * {@link WebTestClient} 测试客户端对象, 需要对其进行自动配置
 * </p>
 *
 * <p>
 * {@link RequestHeadersSpec} 类型用于处理通过 URL 进行的请求, 是所有请求类型的超类, 包括 {@code get},
 * {@code post}, {@code put} 和 {@code delete} 等
 * </p>
 *
 * <p>
 * {@link RequestHeadersUriSpec} 类型用于处理包含 Headers 信息的请求, 如 {@code get} 和
 * {@code delete} 请求
 * </p>
 *
 * <p>
 * {@link RequestBodySpec} 类型用于处理包含 Headers 信息的请求, 如 {@code post} 和 {@code put}
 * 请求
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(classes = { TestConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class WebTest {
    // 定义错误的响应类型
    protected static final ParameterizedTypeReference<ResponseWrapper<ErrorDetail>> ERROR_TYPE
        = new ParameterizedTypeReference<>() {};

    // 测试客户端, 模拟发送请求
    @Autowired
    private WebTestClient client;

    // Servlet 上下文对象
    @Autowired
    private ServletContext servletContext;

    /**
     * 实例化一个测试客户端
     *
     * @return {@link WebTestClient} 类型对象
     */
    protected WebTestClient client() {
        return client
                // 对 client 字段进行更新操作, 返回 org.springframework.test.web.reactive.server.WebTestClient.Builder 对象
                .mutate()
                // 设置请求超时
                .responseTimeout(Duration.ofMinutes(1))
                // 创建新的 WebTestClient 对象
                .build();
    }

    /**
     * 设置测试客户端
     *
     * @param <T>          Response 类型
     * @param <R>          Request 类型
     * @param spec         请求对象
     * @param url          请求地址
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestHeadersSpec} 对象, 用于发送测试请求
     */
    @SuppressWarnings("unchecked")
    private <T extends RequestHeadersSpec<?>, R extends RequestHeadersUriSpec<?>> T setup(
            R spec, String url, Object... uriVariables) {
        // 设置访问 URL 地址和必要的 header 信息
        return (T) spec.uri(servletContext.getContextPath() + url, uriVariables);
    }

    /**
     * 发送 json 类型的 {@code get} 请求
     *
     * @param url          请求地址
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestHeadersSpec} 对象, 用于发送测试请求
     */
    protected RequestHeadersSpec<?> getJson(String url, Object... uriVariables) {
        return setup(client().get(), url, uriVariables)
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * 发送 json 类型的 {@code post} 请求
     *
     * @param url          请求地址
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestBodySpec} 请求类型
     */
    protected RequestBodySpec postJson(String url, Object... uriVariables) {
        return ((RequestBodySpec) setup(client().post(), url, uriVariables))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }
}
