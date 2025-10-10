package alvin.study.springboot.ds;

import java.time.Duration;

import jakarta.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import alvin.study.springboot.ds.conf.TestingConfig;
import alvin.study.springboot.ds.core.TableCleaner;
import alvin.study.springboot.ds.core.TestingTransaction;
import alvin.study.springboot.ds.core.TestingTransactionManager;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DynamicDataSource;
import alvin.study.springboot.ds.core.http.interceptor.ApiHandlerInterceptor;

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
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试, 其
 * {@code classes} 属性指定了该测试相关的配置类
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(classes = { TestingConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {
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
     * 注入测试用事务管理器对象
     *
     * <p>
     * 在测试时, 有时候不方便应用
     * {@link org.springframework.transaction.annotation.Transactional @Transactional}
     * 注解, 此时可以使用该事务管理器手动启动和结束事务
     * </p>
     *
     * <p>
     * 该对象由 {@code TestingConfig.testingTransactionManager(PlatformTransactionManager)} 方法产生
     * </p>
     */
    @Autowired
    private TestingTransactionManager txManager;

    /**
     * 用于在每次测试开始前, 将测试数据表全部清空
     *
     * @see TableCleaner#clearAllTables(String...)
     */
    @Autowired
    private TableCleaner tableCleaner;

    /**
     *
     */
    @Autowired
    private DynamicDataSource dynamicDataSource;

    /**
     * 在每次测试前执行
     */
    @BeforeEach
    protected void beforeEach() {
        // 清理默认数据源对应的数据库
        try (var _ = DataSourceContext.switchTo(null)) {
            // 将除了 schema_version 以外的表内容清空
            tableCleaner.clearAllTables("schema_version");
        }

        // 清理其它数据源对应的数据库
        for (var lookupKey : dynamicDataSource.getAllLookupKeys()) {
            try (var _ = DataSourceContext.switchTo(lookupKey)) {
                // 将除了 schema_version 以外的表内容清空
                tableCleaner.clearAllTables("schema_version");
            }
        }
    }

    /**
     * 每次测试结束, 进行清理工作
     */
    @AfterEach
    protected void afterEach() {
        DataSourceContext.clear();
    }

    /**
     * 开启事务
     *
     * @param readOnly 事务的只读性
     * @return 用于测试的事务管理器对象
     */
    protected TestingTransaction beginTx(boolean readOnly) {
        return txManager.begin(readOnly);
    }

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
     * @param org          组织代码
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestHeadersSpec} 对象, 用于发送测试请求
     */
    protected RequestHeadersSpec<?> getJson(String url, String org, Object... uriVariables) {
        return setup(client().get(), url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .header(ApiHandlerInterceptor.HEADER_ORG, org);
    }

    /**
     * 发送 json 类型的 {@code post} 请求
     *
     * @param url          请求地址
     * @param org          组织代码
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestBodySpec} 请求类型
     */
    protected RequestBodySpec postJson(String url, String org, Object... uriVariables) {
        return ((RequestBodySpec) setup(client().post(), url, uriVariables))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(ApiHandlerInterceptor.HEADER_ORG, org);
    }

    /**
     * 发送 json 类型的 {@code delete} 请求
     *
     * @param url          请求地址
     * @param org          组织代码
     * @param uriVariables 在 URL 中包含的请求参数值
     * @return {@link RequestHeadersSpec} 请求类型
     */
    protected RequestHeadersSpec<?> deleteJson(String url, String org, Object... uriVariables) {
        return setup(client().delete(), url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .header(ApiHandlerInterceptor.HEADER_ORG, org);
    }
}
