package alvin.study.springboot.mybatis.conf;

import alvin.study.springboot.mybatis.infra.mapper.BaseMapper;
import alvin.study.springboot.mybatis.infra.mapper.method.DeleteAllMethod;
import alvin.study.springboot.mybatis.infra.mapper.method.InsertAllBatchMethod;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

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
@MapperScan(basePackages = {
    "alvin.study.springboot.mybatis.infra.mapper"
})
@EnableTransactionManagement
public class MyBatisConfig extends DefaultSqlInjector {
    /**
     * 获取主键生成器对象
     *
     * <p>
     * 主键生成器一般用于自定义主键, 或 Oracle, DB2, PostgreSQL, H2 这类支持从序列 (Sequence)
     * 中获取值作为主键的数据库. 本例中使用 MySQL, 所以无需特殊的主键生成规则, 所以注释掉 {@link Bean @Bean} 注解,
     * 令该方法不生效
     * </p>
     *
     * @return {@link IKeyGenerator} 接口对象, 用于生成主键, 例如 {@link H2KeyGenerator} 对象
     */
    // @Bean
    IKeyGenerator keyGenerator() {
        return new H2KeyGenerator();
    }

    /**
     * 开启所需的 Interceptor 拦截器, 从而通过拦截器启动各类功能
     *
     * @return {@link MybatisPlusInterceptor} 对象, 表示拦截器集合
     */
    @Bean
    MybatisPlusInterceptor interceptor() {
        var interceptor = new MybatisPlusInterceptor();

        // 添加内置拦截器, 用于启动分页控制
        var paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setDialect(new MySqlDialect()); // 设置方言 (可选, 如已经设置 DbType 可忽略)
        paginationInterceptor.setOptimizeJoin(true); // 优化 count 语句时 left join 部分
        paginationInterceptor.setMaxLimit(10000L); // 设置最大分页数量
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 添加内置拦截器, 用于启动乐观锁版本控制
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 通过代码方式对 Mybatis 框架进行配置, 基本功能和 {@code classpath:application.yml} 文件中的
     * {@code mybatis-plus} 配置项相同
     *
     * @return {@link ConfigurationCustomizer} 对象
     */
    @Bean
    ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 对 Mybatis 进行设置
        };
    }

    /**
     * 通过代码方式对 Mybatis 框架进行配置, 基本功能和 {@code classpath:application.yml} 文件中的
     * {@code mybatis-plus} 配置项相同
     *
     * <p>
     * {@link com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties#setTypeHandlersPackage(String)
     * MybatisPlusProperties.setTypeHandlersPackage(String)} 用于设置
     * {@link org.apache.ibatis.type.TypeHandler TypeHandler} 类型转换器所在的包
     * </p>
     *
     * @return {@link MybatisPlusPropertiesCustomizer} 对象, 表示自定义配置信息
     */
    @Bean
    MybatisPlusPropertiesCustomizer propertiesCustomizer() {
        return properties -> {
            // 设置 MyBatis 类型转换器所在的包名称
            properties.setTypeHandlersPackage("alvin.study.infra.handler");

            // 获取全局配置对象
            var config = properties.getGlobalConfig();
            // 关闭 MyBatis 启动横幅
            config.setBanner(false);
        };
    }

    /**
     * 添加自定义方法
     *
     * <p>
     * 通过重写 {@link DefaultSqlInjector#getMethodList(Class, TableInfo)}
     * 方法即可以添加自定义通用方法
     * </p>
     *
     * <p>
     * 自定义方法需要编写一个从 {@link com.baomidou.mybatisplus.core.injector.AbstractMethod
     * AbstractMethod} 类型继承的子类, 参考
     * {@link DeleteAllMethod DeleteAllMethod} 类型
     * </p>
     *
     * <p>
     * 注入的方法需要在所有 Mapper 类型的超类中定义, 参考 {@link BaseMapper
     * BaseMapper} 中定义的通用方法
     * </p>
     *
     * @see DeleteAllMethod
     * @see InsertAllBatchMethod
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        var methods = super.getMethodList(mapperClass, tableInfo);
        methods.add(new DeleteAllMethod());
        methods.add(new InsertAllBatchMethod());
        return methods;
    }
}
