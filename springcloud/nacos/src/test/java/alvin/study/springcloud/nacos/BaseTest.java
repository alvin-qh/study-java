package alvin.study.springcloud.nacos;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import jakarta.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec;

import com.alibaba.cloud.commons.io.IOUtils;

import lombok.SneakyThrows;

import alvin.study.springcloud.nacos.conf.TestingConfig;

/**
 * 集成测试类的超类
 *
 * <p>
 * 集成测试指的是将数据库操作和业务操作集成在一起进行测试, 可以比较真实的复现业务执行的流程
 * </p>
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(classes = TestingConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
abstract class BaseTest {
    protected static final String CONFIG_DATA_ID = "alvin-study-config.yml";
    protected static final String CONFIG_GROUP = "STUDY_CONFIG";
    protected static final String NAMING_GROUP = "STUDY_DISCOVERY";

    /**
     * 测试客户端, 模拟发送请求
     */
    @Autowired
    private WebTestClient client;

    /**
     * Servlet 上下文对象
     */
    @Autowired
    private ServletContext servletContext;

    /**
     * 实例化一个测试客户端
     *
     * @return {@link WebTestClient} 类型对象
     */
    protected WebTestClient client() {
        return client
                // 对 client 字段进行更新操作, 返回
                // org.springframework.test.web.reactive.server.WebTestClient.Builder 对象
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

    /**
     * 读取测试用例
     *
     * @return 测试用例结果
     */
    @SneakyThrows
    protected String loadTestConfig() {
        try (var input = getClass().getResourceAsStream("/testcase/test_config.yml")) {
            return IOUtils.toString(input, StandardCharsets.UTF_8);
        }
    }

    /**
     * 读取变更测试用例
     *
     * @return 测试用例结果
     */
    @SneakyThrows
    protected String loadTestChangedConfig() {
        try (var input = getClass().getResourceAsStream("/testcase/test_changed_config.yml")) {
            return IOUtils.toString(input, StandardCharsets.UTF_8);
        }
    }
}
