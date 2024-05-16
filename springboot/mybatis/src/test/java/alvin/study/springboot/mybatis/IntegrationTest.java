package alvin.study.springboot.mybatis;

import alvin.study.springboot.mybatis.builder.Builder;
import alvin.study.springboot.mybatis.builder.OrgBuilder;
import alvin.study.springboot.mybatis.builder.UserBuilder;
import alvin.study.springboot.mybatis.conf.ContextConfig;
import alvin.study.springboot.mybatis.conf.TestingConfig;
import alvin.study.springboot.mybatis.conf.TestingContextInitializer;
import alvin.study.springboot.mybatis.core.TableCleaner;
import alvin.study.springboot.mybatis.core.TestingTransaction;
import alvin.study.springboot.mybatis.core.TestingTransactionManager;
import alvin.study.springboot.mybatis.core.context.Context;
import alvin.study.springboot.mybatis.core.context.ContextSwitcher;
import alvin.study.springboot.mybatis.core.context.WebContext;
import alvin.study.springboot.mybatis.infra.entity.Org;
import alvin.study.springboot.mybatis.infra.entity.User;
import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import alvin.study.springboot.mybatis.infra.entity.common.TenantedEntity;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
@SpringBootTest(classes = { TestingConfig.class })
@ContextConfiguration(initializers = { TestingContextInitializer.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {
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
     * 注入 mybatis Session 对象
     *
     * <p>
     * {@link SqlSession} 相当于数据库连接的封装
     * </p>
     */
    @Autowired
    private SqlSession sqlSession;

    /**
     * 请求上下文对象
     *
     * @see ContextConfig#context()
     */
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
     * 预设的测试用当前组织
     *
     * <p>
     * 该对象也会同时存储在请求上下文中, 每个测试的预设值都不相同
     * </p>
     */
    private Org org;

    /**
     * 预设的测试用当前用户
     *
     * <p>
     * 该对象也会同时存储在请求上下文中, 每个测试的预设值都不相同
     * </p>
     */
    private User user;

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

        // 创建测试用实体
        try (var ignore = txManager.begin(false)) {
            // 创建 builder 构建实体对象, 构建当前组织和当前用户对象
            org = newBuilder(OrgBuilder.class).create();
            user = newBuilder(UserBuilder.class).withOrgId(org.getId()).create();
        }

        // 向请求上下文中存储信息
        context.set(Context.ORG, org);
        context.set(Context.USER, user);
    }

    /**
     * 每次测试结束, 进行清理工作
     */
    @AfterEach
    protected void afterEach() { }

    protected Org currentOrg() {
        return org;
    }

    protected User currentUser() {
        return user;
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
     * 切换请求上下文对象
     *
     * <p>
     * 该操作会在当前请求上下文中设置一个新的 {@link Context} 对象以替换原有的, 并在操作完毕后切换回之前的对象
     * </p>
     *
     * @param org  要切换的组织实体对象
     * @param user 要切换的用户实体对象
     * @return 上下文切换对象
     * @see ContextSwitcher#doSwitch(Context, java.util.function.Consumer)
     */
    protected ContextSwitcher switchContext(Org org, User user) {
        // 产生一个新的请求上下文对象
        var context = new WebContext();
        if (org != null) {
            // 在新的上下文对象中存储要切换的 Org 对象
            context.set(Context.ORG, org);
        }

        if (user != null) {
            // 在新的上下文对象中存储要切换的 User 对象
            context.set(Context.USER, user);
        }

        // 切换请求上下文对象, 并返回 ContextSwitcher 对象用于后续恢复之前的请求上下文对象
        return ContextSwitcher.doSwitch(context);
    }
}
