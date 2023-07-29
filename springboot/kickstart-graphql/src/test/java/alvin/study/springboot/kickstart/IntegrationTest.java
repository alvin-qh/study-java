package alvin.study.springboot.kickstart;

import alvin.study.springboot.kickstart.builder.Builder;
import alvin.study.springboot.kickstart.builder.OrgBuilder;
import alvin.study.springboot.kickstart.builder.UserBuilder;
import alvin.study.springboot.kickstart.common.GraphQLTestTemplateBuilder;
import alvin.study.springboot.kickstart.common.TableCleaner;
import alvin.study.springboot.kickstart.common.TestingTransaction;
import alvin.study.springboot.kickstart.common.TestingTransactionManager;
import alvin.study.springboot.kickstart.conf.BeanConfig;
import alvin.study.springboot.kickstart.conf.ContextConfig;
import alvin.study.springboot.kickstart.conf.TestingConfig;
import alvin.study.springboot.kickstart.conf.TestingContextInitializer;
import alvin.study.springboot.kickstart.core.context.Context;
import alvin.study.springboot.kickstart.core.context.CustomRequestAttributes;
import alvin.study.springboot.kickstart.core.context.WebContext;
import alvin.study.springboot.kickstart.infra.entity.Org;
import alvin.study.springboot.kickstart.infra.entity.User;
import alvin.study.springboot.kickstart.infra.entity.common.AuditedEntity;
import alvin.study.springboot.kickstart.infra.entity.common.TenantedEntity;
import alvin.study.springboot.kickstart.util.http.Headers;
import alvin.study.springboot.kickstart.util.security.Jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

/**
 * 集成测试类的超类
 *
 * <p>
 * 集成测试指的是将数据库操作和业务操作集成在一起进行测试, 可以比较真实的复现业务执行的流程
 * </p>
 *
 * <p>
 * 发送 Graphql 请求需使用 {@link com.graphql.spring.boot.test.GraphQLTestTemplate
 * GraphQLTestTemplate} 对象发起请求, 需要在 {@link SpringBootTest @SpringBootTest}
 * 注解中设置 {@code webEnvironment} 属性
 * </p>
 *
 * <p>
 * 如果不进行其它的 HTTP 请求测试, 即不使用
 * {@link org.springframework.test.web.reactive.server.WebTestClient
 * WebTestClient} 对象, 则无需
 * {@link AutoConfigureWebTestClient @AutoConfigureWebTestClient} 注解
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
 * {@code classes} 属性指定了该测试相关的配置类, {@code webEnvironment} 定义了 WEB 测试环境. 注意,
 * 在每次测试开始前必须通过 {@link CustomRequestAttributes#register(Context)} 方法注册上下文, 否则
 * {@link Context} 对象无法使用, 参考: {@link ContextConfig#context()
 * ContextConfig.context()} 方法上的注解
 * </p>
 *
 * <p>
 * {@link ContextConfiguration @ContextConfiguration} 注解用于指定测试上下文配置, 这里使用的
 * {@code initializers} 属性用于指定测试初始化类. 参考 {@link TestingContextInitializer} 类型
 * </p>
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = { TestingConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { TestingContextInitializer.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @AutoConfigureWebTestClient
public abstract class IntegrationTest {
    /**
     * 注入 Jackson 对象转换器
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 注入 graphql 测试客户端
     */
    @Autowired
    private GraphQLTestTemplateBuilder testTemplateBuilder;

    /**
     * 注入测试用事务管理器对象
     *
     * <p>
     * 在测试时, 有时候不方便应用
     * {@link Transactional @Transactional} 注解, 此时可以使用该事务管理器手动启动和结束事务
     * </p>
     *
     * @see TestingConfig#testingTransactionManager(org.springframework.transaction.PlatformTransactionManager)
     * TestingConfig.testingTransactionManager(PlatformTransactionManager)
     */
    @Autowired
    private TestingTransactionManager txManager;

    /**
     * 注入 Mybatis Session 对象
     *
     * <p>
     * {@link SqlSession} 相当于数据库连接的封装
     * </p>
     */
    @Autowired
    private SqlSession sqlSession;

    // /**
    // * 请求上下文对象
    // *
    // * @see alvin.study.conf.ContextConfig#context()
    // */
    @Autowired
    private Context context;

    /**
     * Bean 工厂类
     *
     * <p>
     * {@link AutowireCapableBeanFactory} 用于从 Bean 容器中创建一个 Bean 对象或者为一个已有的 Bean
     * 对象注入所需的参数
     * </p>
     */
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    /**
     * 用于在每次测试开始前, 将测试数据表全部清空
     *
     * @see TableCleaner#clearAllTables(String...)
     */
    @Autowired
    private TableCleaner tableCleaner;

    /**
     * 注入 Jwt 对象
     *
     * @see BeanConfig#jwt() BeanConfig.jwt()
     */
    @Autowired
    private Jwt jwt;

    /**
     * 预设的测试用当前组织
     *
     * <p>
     * 该对象也会同时存储在请求上下文中, 每个测试的预设值都不相同
     * </p>
     */
    private Org currentOrg;

    /**
     * 预设的测试用当前用户
     *
     * <p>
     * 该对象也会同时存储在请求上下文中, 每个测试的预设值都不相同
     * </p>
     */
    private User currentUser;

    /**
     * <p>
     * 该方法中为当前请求上下文注册了 {@link Context} 对象, 并在对象中注册了 {@link User} 和 {@link Org} 两个对象,
     * 表示当前登录的用户和其所在的组织 (租户)
     * </p>
     *
     * @see IntegrationTest#newBuilder(Class)
     * @see TenantedEntity#orgId
     * @see AuditedEntity#createdBy
     * @see AuditedEntity#updatedBy
     */
    @BeforeEach
    @Transactional
    protected void beforeEach() {
        // 将除了 schema_version 以外的表内容清空
        tableCleaner.clearAllTables("schema_version");

        // 构建请求上下文, 之后 context 字段方能生效
        var context = CustomRequestAttributes.register(new WebContext());

        // 创建测试用实体
        try (var ignore = txManager.begin(false)) {
            // 创建 builder 构建实体对象, 构建当前组织和当前用户对象
            currentOrg = newBuilder(OrgBuilder.class).create();
            currentUser = newBuilder(UserBuilder.class).withOrgId(currentOrg.getId()).create();
        }

        // 向请求上下文中存储信息
        context.set(Context.ORG, currentOrg);
        context.set(Context.USER, currentUser);
    }

    /**
     * 每次测试结束, 进行清理工作
     */
    @AfterEach
    protected void afterEach() {
        // 清理请求上下文
        CustomRequestAttributes.unregister();
    }

    protected Org currentOrg() {
        return currentOrg;
    }

    protected User currentUser() {
        return currentUser;
    }

    protected Context currentContext() {
        return context;
    }

    /**
     * 创建实体类型的构建器对象
     *
     * @param <T>         构建器类型, 即 {@link Builder Builder} 类的子类型
     * @param builderType 构建器类型的 {@link Class} 对象
     * @return 构建器实例
     */
    @SneakyThrows
    protected <T extends Builder<?>> T newBuilder(Class<T> builderType) {
        // 创建新的 Builder 对象
        var builder = builderType.getConstructor().newInstance();

        // 对已有对象进行注入操作
        beanFactory.autowireBean(builder);
        return builder;
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
     * 清空 mybatis 的一级缓存
     */
    protected void clearSessionCache() {
        sqlSession.clearCache();
    }

    /**
     * 将对象转为指定的 {@link ObjectNode} 对象
     *
     * @param nodeName 节点名称
     * @param value    对象实例
     * @return 指定节点名称的 {@link ObjectNode} 阶段对象
     */
    protected ObjectNode valueToTree(String nodeName, Object value) {
        var node = objectMapper.createObjectNode();
        node.set(nodeName, objectMapper.valueToTree(value));
        return node;
    }

    /**
     * 将一个 {@link Map Map<String, ?>} 对象根据 Key 值转为 {@link ObjectNode} 对象
     *
     * @param variables {@link Map Map<String, ?>} 类型对象
     * @return 以 Key 为节点名称的 {@link ObjectNode} 阶段对象
     */
    protected ObjectNode mapToTree(Map<String, Object> variables) {
        var node = objectMapper.createObjectNode();
        for (var entry : variables.entrySet()) {
            node.set(entry.getKey(), objectMapper.valueToTree(entry.getValue()));
        }
        return node;
    }

    /**
     * 发起一个 Graphql 请求
     *
     * @param query     存储 Graphql Schema 的文件名
     * @param variables 对应操作的操作名
     * @param variables 对应参数
     * @return 执行结果
     */
    protected GraphQLResponse graphql(String resource, String operationName, ObjectNode variables) throws IOException {
        // 创建一个 GraphQLTestTemplate 类型对象并设置所需的 Http header 值
        var template = testTemplateBuilder.build()
            .withAdditionalHeader(Headers.AUTHORIZATION, Headers.BEARER + " " + makeBearerToken());

        try {
            // 拼装完整的资源路径和资源名, 对应 classpath:graphql/xxx.graphql 文件
            resource = "graphql/" + resource + ".graphql";
            if (variables == null) {
                // 无执行参数的调用方法
                return template.perform(resource, operationName);
            }
            // 具备执行参数的调用方法
            return template.perform(resource, operationName, variables);
        } finally {
            log.info("GraphQL={} request send, account={}, user={}",
                resource, currentOrg.getName(), currentUser.getAccount());
        }
    }

    /**
     * 发起一个 Graphql 请求
     *
     * @param query     存储 Graphql Schema 的文件名
     * @param variables 对应操作的操作名
     * @return 执行结果
     */
    protected GraphQLResponse graphql(String resource, String operationName) throws IOException {
        return graphql(resource, operationName, null);
    }

    /**
     * 发起一个 Graphql 请求
     *
     * @param query 存储 Graphql Schema 的文件名
     * @return 执行结果
     */
    protected GraphQLResponse graphql(String resource) throws IOException {
        return graphql(resource, null, null);
    }

    /**
     * 发起一个 Graphql 请求
     *
     * @param query     Graphql Schema 字符串
     * @param variables Schema 对应参数
     * @return 执行结果
     */
    protected GraphQLResponse graphql(String query, ObjectNode variables) {
        var template = testTemplateBuilder.build()
            // 设置 Http header
            .withAdditionalHeader(Headers.AUTHORIZATION, Headers.BEARER + " " + makeBearerToken());

        try {
            // 执行查询
            return template.postMultipart(query, variables.toPrettyString());
        } finally {
            log.info("GraphQL={} request send, account={}, user={}",
                query, currentOrg.getName(), currentUser.getAccount());
        }
    }

    /**
     * 创建测试用的 Bearer token
     *
     * <p>
     * 通过 {@link #beforeEach()} 方法中创建的 {@link #currentUser} 和 {@link #currentOrg}
     * 信息创建 JWT token
     * </p>
     *
     * @return JWT token
     */
    private String makeBearerToken() {
        return jwt.encode(currentOrg.getName(), currentUser.getId().toString());
    }
}
