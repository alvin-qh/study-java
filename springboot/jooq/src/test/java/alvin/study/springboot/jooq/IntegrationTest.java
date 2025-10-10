package alvin.study.springboot.jooq;

import static alvin.study.springboot.jooq.infra.model.public_.Tables.ORG;
import static alvin.study.springboot.jooq.infra.model.public_.Tables.USER;

import java.util.concurrent.atomic.AtomicInteger;

import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import alvin.study.springboot.jooq.conf.TestConfig;
import alvin.study.springboot.jooq.conf.TestContextInitializer;
import alvin.study.springboot.jooq.core.TableCleaner;
import alvin.study.springboot.jooq.core.context.Context;
import alvin.study.springboot.jooq.core.context.CustomRequestAttributes;
import alvin.study.springboot.jooq.core.context.WebContext;
import alvin.study.springboot.jooq.core.jooq.dsl.JdbcDSLContextManager;
import alvin.study.springboot.jooq.infra.model.UserType;
import alvin.study.springboot.jooq.util.security.PasswordUtil;

/**
 * 集成测试超类, 所有需要数据库集成测试, 需要继承此类
 */
@ActiveProfiles("test")
@SpringBootTest(classes = { TestConfig.class })
@ContextConfiguration(initializers = { TestContextInitializer.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {
    // 用于产生不重复名称的原子整数对象
    protected static final AtomicInteger SEQUENCE = new AtomicInteger();
    // DSLContext 管理器
    @Autowired
    protected JdbcDSLContextManager contextManager;
    /**
     * Jooq DSL Context 对象
     */
    @Autowired
    protected DSLContext dsl;
    /**
     * 密码加密计算工具对象
     */
    @Autowired
    protected PasswordUtil passwordUtil;
    // 注入数据表清理器对象
    @Autowired
    private TableCleaner tableCleaner;

    /**
     * 每次测试执行前执行
     */
    @BeforeEach
    void beforeEach() {
        // 测试开始前, 清理数据表
        tableCleaner.clearAllTables("schema_version", "flyway_schema_history");

        // 注册请求上下文对象
        var context = CustomRequestAttributes.register(new WebContext());

        // 构建 ORG, USER 对象存入上下文
        dsl.transaction(_ -> {
            var org = dsl.newRecord(ORG);
            org.setName(makeUniqueName("alvin.edu")).store();
            context.set(Context.ORG, org);

            var user = dsl.newRecord(USER);
            user.setAccount("admin")
                    .setPassword(passwordUtil.encrypt("12345678"))
                    .setType(UserType.ADMIN)
                    .store();
            context.set(Context.USER, user);
        });
    }

    /**
     * 每次测试完成后执行
     */
    @AfterEach
    void afterEach() {
        // 清理 DSLContext 和数据库连接
        contextManager.clear();
    }

    /**
     * 产生一个不重复的名称
     *
     * @param name 原名称
     * @return 不重复的名称
     */
    protected String makeUniqueName(String name) {
        return name + ":" + SEQUENCE.incrementAndGet();
    }
}
