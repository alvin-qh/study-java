package alvin.study.springboot.security;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletContext;

import org.apache.ibatis.session.SqlSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec;
import org.springframework.transaction.annotation.Transactional;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import alvin.study.springboot.security.builder.Builder;
import alvin.study.springboot.security.builder.GroupBuilder;
import alvin.study.springboot.security.builder.PermissionBuilder;
import alvin.study.springboot.security.builder.RoleBuilder;
import alvin.study.springboot.security.builder.RoleGrantBuilder;
import alvin.study.springboot.security.builder.RolePermissionBuilder;
import alvin.study.springboot.security.builder.UserBuilder;
import alvin.study.springboot.security.builder.UserGroupBuilder;
import alvin.study.springboot.security.conf.BeanConfig;
import alvin.study.springboot.security.conf.TableCleaner;
import alvin.study.springboot.security.conf.TestingConfig;
import alvin.study.springboot.security.conf.TestingContextInitializer;
import alvin.study.springboot.security.conf.TestingTransaction;
import alvin.study.springboot.security.conf.TestingTransactionManager;
import alvin.study.springboot.security.core.cache.Cache;
import alvin.study.springboot.security.core.security.auth.NameAndPasswordAuthenticationToken;
import alvin.study.springboot.security.infra.entity.Group;
import alvin.study.springboot.security.infra.entity.RoleGrantType;
import alvin.study.springboot.security.infra.entity.User;
import alvin.study.springboot.security.infra.entity.UserType;
import alvin.study.springboot.security.infra.mapper.PermissionMapper;
import alvin.study.springboot.security.infra.mapper.RoleGrantMapper;
import alvin.study.springboot.security.infra.mapper.RoleMapper;
import alvin.study.springboot.security.infra.mapper.RolePermissionMapper;
import alvin.study.springboot.security.util.http.Headers;
import alvin.study.springboot.security.util.security.Jwt;

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
 *
 * <p>
 * {@link ContextConfiguration @ContextConfiguration} 注解用于指定测试上下文配置, 这里使用的
 * {@code initializers} 属性用于指定测试初始化类. 参考 {@link TestingContextInitializer} 类型
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(classes = { TestingConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { TestingContextInitializer.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
public abstract class IntegrationTest {
    /**
     * 默认测试用户的原始密码
     */
    protected static final String RAW_PASSWORD = "1234567890";
    /**
     * 注入角色 Mapper 类
     */
    @Autowired
    protected RoleMapper roleMapper;
    /**
     * 注入权限 Mapper 类
     */
    @Autowired
    protected PermissionMapper permissionMapper;
    /**
     * 注入测试用事务管理器对象
     *
     * <p>
     * 在测试时, 有时候不方便应用
     * {@link Transactional @Transactional} 注解, 此时可以使用该事务管理器手动启动和结束事务
     * </p>
     *
     * @see TestingConfig
     */
    @Autowired
    private TestingTransactionManager txManager;
    /**
     * 注入 mybatis Session 对象
     *
     * <p>
     * {@link SqlSession} 相当于数据库连接的封装
     * </p>
     */
    @Autowired
    private SqlSession sqlSession;
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
     * @see BeanConfig
     */
    @Autowired
    private Jwt jwt;
    /**
     * 预设的测试用当前用户
     *
     * <p>
     * 该对象也会同时存储在请求上下文中, 每个测试的预设值都不相同
     * </p>
     */
    private User currentUser;
    /**
     * 当前用户所在的组
     */
    private Group adminGroup;
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
     * 注入角色授予 Mapper 类
     */
    @Autowired
    private RoleGrantMapper roleGrantMapper;

    /**
     * 注入角色权限关系 Mapper 类
     */
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    // 注入缓存类
    @Autowired
    private Cache cache;

    /**
     * 在每个测试前执行
     *
     * <p>
     * 该方法中为当前请求上下文注册了
     * {@link org.springframework.security.core.context.SecurityContext
     * SecurityContext} 对象, 并在其中绑定了 {@link User} 对象, 表示当前登录的用户
     * </p>
     */
    @BeforeEach
    @Transactional
    protected void beforeEach() {
        // 将除了 schema_version 以外的表内容清空
        tableCleaner.clearAllTables("schema_version");

        // 创建测试用实体
        try (var _ = txManager.begin(false)) {
            // 当前用户对象
            currentUser = newBuilder(UserBuilder.class)
                    .withPassword(RAW_PASSWORD)
                    .withType(UserType.ADMIN)
                    .create();

            // 在线程上下文中注册当前登录用户
            SecurityContextHolder.getContext().setAuthentication(
                new NameAndPasswordAuthenticationToken(currentUser, "", List.of()));

            // 当前用户所属的用户组
            adminGroup = newBuilder(GroupBuilder.class)
                    .withName("Administrators")
                    .create();

            newBuilder(UserGroupBuilder.class)
                    .withGroupId(adminGroup.getId())
                    .withUserId(currentUser.getId())
                    .create();
        }

        cache.removeAllKeys();
    }

    /**
     * 每次测试结束, 进行清理工作
     */
    @AfterEach
    protected void afterEach() {
        // 清理线程上下文存储
        SecurityContextHolder.clearContext();
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
        return jwt.encode(currentUser.getId().toString()).getToken();
    }

    /**
     * 清空 mybatis 的一级缓存
     */
    protected void clearSessionCache() {
        sqlSession.clearCache();
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
        // 产生一个 token
        var token = makeBearerToken();

        // 写入缓存
        cache.saveUser(token, currentUser, jwt.getPeriod());

        // 设置访问 URL 地址和必要的 header 信息
        return (T) spec.uri(servletContext.getContextPath() + url, uriVariables)
                .header(Headers.AUTHORIZATION, Headers.BEARER + " " + token);
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
     * 为指定用户添加角色和权限
     *
     * @param user        指定用户对象
     * @param roleName    角色名称
     * @param permissions 权限列表
     */
    protected void addPermissions(User user, String roleName, Collection<String> permissions) {
        addPermissions(user.getId(), RoleGrantType.USER, roleName, permissions);
    }

    /**
     * 为指定用户组添加角色和权限
     *
     * @param user        指定用户组对象
     * @param roleName    角色名称
     * @param permissions 权限列表
     */
    protected void addPermissions(Group group, String roleName, Collection<String> permissions) {
        addPermissions(group.getId(), RoleGrantType.GROUP, roleName, permissions);
    }

    /**
     * 为指定的用户或用户组添加权限列表
     *
     * @param userOrGroupId 用户或用户组 id
     * @param type          权限授予类型, 用户或组
     * @param roleName      角色名称
     * @param permissions   权限列表
     */
    private void addPermissions(
            Long userOrGroupId, RoleGrantType type, String roleName, Collection<String> permissions) {
        // 查询角色名称, 若不存在, 则创建角色并绑定用户 (或组)
        var role = roleMapper.selectByName(roleName).orElseGet(
            () -> newBuilder(RoleBuilder.class)
                    .withName(roleName)
                    .create());

        // 查询角色授予情况, 如不存在, 则进行授予
        roleGrantMapper.selectByUserOrGroupIdAndType(userOrGroupId, type, role.getId()).orElseGet(
            () -> newBuilder(RoleGrantBuilder.class)
                    .withUserOrGroupId(userOrGroupId)
                    .withType(type)
                    .withRoleId(role.getId())
                    .create());

        for (var permissionStr : permissions) {
            var parts = permissionStr.split(":", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException(String.format("Permission \"%s\" is invalid", permissionStr));
            }

            // 查询权限, 若不存在则创建权限
            var permission = permissionMapper.selectByNameResourceAndAction(parts[0], parts[1], parts[2]).orElseGet(
                () -> newBuilder(PermissionBuilder.class)
                        .withName(parts[0])
                        .withResource(parts[1])
                        .withAction(parts[2])
                        .create());

            // 查询权限和角色关系, 若不存在则建立关系
            rolePermissionMapper.selectByRoleAndPermissionId(role.getId(), permission.getId()).orElseGet(
                () -> newBuilder(RolePermissionBuilder.class)
                        .withPermissionId(permission.getId())
                        .withRoleId(role.getId())
                        .create());
        }
    }

    /**
     * 获取当前用户对象
     *
     * @return 当前用户对象
     */
    protected User currentUser() {
        return currentUser;
    }

    /**
     * 获取当前用户所在的组
     *
     * @return 当前用户所在的组对象
     */
    protected Group adminGroup() {
        return adminGroup;
    }
}
