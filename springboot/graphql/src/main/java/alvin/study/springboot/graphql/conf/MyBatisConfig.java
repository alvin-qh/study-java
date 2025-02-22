package alvin.study.springboot.graphql.conf;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;

/**
 * MyBatis 框架相关配置
 *
 * <p>
 * {@link MapperScan @MapperScan} 注解用于指定 mybatis Mapper 类所在的包, Spring
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
    "alvin.study.springboot.kickstart.infra.mapper"
})
@EnableTransactionManagement
public class MyBatisConfig extends DefaultSqlInjector {
    /**
     * 开启所需的 Interceptor 拦截器, 从而通过拦截器启动各类功能
     *
     * @return {@link MybatisPlusInterceptor} 对象, 表示拦截器集合
     */
    @Bean
    MybatisPlusInterceptor interceptor(@Value("${spring.data.web.pageable.max-page-size:null}") Long maxPageSize) {
        var interceptor = new MybatisPlusInterceptor();

        // 添加内置拦截器, 用于启动分页控制
        var paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setDialect(new MySqlDialect()); // 设置方言 (可选, 如已经设置 DbType 可忽略)
        paginationInterceptor.setOptimizeJoin(true); // 优化 count 语句时 left join 部分
        if (maxPageSize != null) {
            paginationInterceptor.setMaxLimit(maxPageSize); // 设置最大分页数量
        }
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 添加内置拦截器, 用于启动乐观锁版本控制
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }
}
