package alvin.study.springboot.ds;

import alvin.study.springboot.ds.conf.TestingConfig;
import alvin.study.springboot.ds.core.TableCleaner;
import alvin.study.springboot.ds.core.TestingTransaction;
import alvin.study.springboot.ds.core.TestingTransactionManager;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DataSourceTarget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
@SpringBootTest(classes = { TestingConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {
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
     * 在每次测试前执行
     */
    @BeforeEach
    protected void beforeEach() {
        for (var target : DataSourceTarget.values()) {
            try (var ignore = DataSourceContext.switchTo(target)) {
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
}
