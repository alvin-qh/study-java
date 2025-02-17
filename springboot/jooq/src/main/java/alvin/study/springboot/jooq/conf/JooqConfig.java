package alvin.study.springboot.jooq.conf;

import alvin.study.springboot.jooq.core.jooq.dsl.JdbcDSLContextManager;
import alvin.study.springboot.jooq.core.jooq.listener.AuditAndTentedRecordListener;
import alvin.study.springboot.jooq.core.jooq.listener.TentedVisitListener;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.RecordListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.VisitListenerProvider;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.ExceptionTranslatorExecuteListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * JOOQ 相关 Spring Boot 配置
 *
 * <p>
 * 如果未使用 {@code spring-boot-starter-jooq} 插件, 则需要下面被注释的内容来集成 Spring Boot 和 JOOQ
 * 框架; 如果已经引入 {@code spring-boot-starter-jooq} 插件, 则注释内容均已被自动化配置
 * </p>
 *
 * <p>
 * {@link EnableTransactionManagement @EnableTransactionManagement}
 * 注解表示启动默认的事务管理器
 * </p>
 */
@Configuration("conf/jooq")
@RequiredArgsConstructor
@EnableTransactionManagement
public class JooqConfig {
    // 注入 JOOQ 数据记录监听器
    private final AuditAndTentedRecordListener auditRecordListener;

    // 注入 JOOQ 数据访问监听器
    private final TentedVisitListener tentedVisitListener;

    /**
     * 配置事务管理器
     *
     * <p>
     * 由于 {@code spring-boot-jooq-starter} 插件已经处理了 Spring Boot 与 JOOQ 的集成, 故无需产生该
     * Bean 对象
     * </p>
     *
     * @param dataSource 数据源对象
     * @return 事务管理器对象
     */
    @Bean
    DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 配置 JOOQ 连接提供器对象
     *
     * <p>
     * 由于 {@code spring-boot-jooq-starter} 插件已经处理了 Spring Boot 与 JOOQ 的集成, 故无需产生该
     * Bean 对象
     * </p>
     *
     * @param dataSource 数据源对象
     * @return JOOQ 数据库连接提供器对象
     */
    // @Bean
    DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    /**
     * 配置 Jooq 异常转换器对象
     *
     * <p>
     * 由于 {@code spring-boot-jooq-starter} 插件已经处理了 Spring Boot 与 JOOQ 的集成, 故无需产生该
     * Bean 对象
     * </p>
     *
     * @return Jooq 异常转换器对象
     */
    ExceptionTranslatorExecuteListener exceptionTransformer() {
        return ExceptionTranslatorExecuteListener.DEFAULT;
    }

    /**
     * 配置 jooq 的 DSL 上下文对象
     *
     * <p>
     * 由于 {@code spring-boot-jooq-starter} 插件已经处理了 Spring Boot 与 JOOQ 的集成, 故无需产生该
     * Bean 对象
     * </p>
     *
     * @param connectionProvider 数据库连接提供器对象
     * @see #connectionProvider(DataSource)
     */
    @Bean
    DSLContext dslContext(DataSourceConnectionProvider connectionProvider) {
        var config = new DefaultConfiguration()
                .set(connectionProvider)
                .set(SQLDialect.MYSQL)
                .set((RecordListenerProvider) auditRecordListener)
                .set((VisitListenerProvider) tentedVisitListener);

        return DSL.using(config);
    }

    /**
     * 创建 {@link JdbcDSLContextManager} 对象
     *
     * <p>
     * 对于 {@code spring-boot-starter-jooq} 插件来说, 可以直接注入 {@link org.jooq.DSLContext
     * DSLContext}, 这里增加该 Bean 对象注入的目的是演示如何具体通过 JDBC 的 {@link java.sql.Connection
     * Connection} 对象来创建一个 {@link org.jooq.DSLContext DSLContext} 对象
     * </p>
     *
     * @param connectionProvider JDBC 连接供应器
     * @return {@link JdbcDSLContextManager} 对象
     * @see JdbcDSLContextManager
     */
    @Bean
    JdbcDSLContextManager jdbcDSLContextManager(DataSourceConnectionProvider connectionProvider) {
        return new JdbcDSLContextManager(
            connectionProvider,
            SQLDialect.MYSQL,
            auditRecordListener,
            tentedVisitListener);
    }
}
