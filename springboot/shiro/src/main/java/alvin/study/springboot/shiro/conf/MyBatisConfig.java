package alvin.study.springboot.shiro.conf;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis 框架相关配置
 *
 * <p>
 * {@link MapperScan @MapperScan} 注解用于指定 Mybatis Mapper 类所在的包, Spring
 * 启动时会扫描这个包下所有的 Mapper 类, 并进行注册
 * </p>
 *
 * <p>
 * {@link EnableTransactionManagement @EnableTransactionManagement}
 * 注解表示启动默认的事务管理器
 * </p>
 */
@Configuration("conf/mybatis")
@MapperScan(basePackages = { "alvin.study.springboot.shiro.infra.mapper" })
@EnableTransactionManagement
public class MyBatisConfig {}
