package alvin.study.springboot.ds.core.data;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 拦截设置了 {@link DefaultDataSource @DefaultDataSource} 注解的方法, 将数据源强行切换到默认数据源,
 * 即访问配置数据库
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class DefaultDataSourceAnnotationAdvice {
    /**
     * 对标记 {@link DefaultDataSource @DefaultDataSource} 注解的方法进行拦截
     *
     * @param jp 连接点对象, 表示被拦截的目标方法信息
     * @throws Throwable 抛出目标方法可能会抛出的异常
     */
    @Around("@annotation(alvin.study.springboot.ds.core.data.DefaultDataSource)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        try (var ignore = DataSourceContext.switchTo(null)) {
            return jp.proceed();
        }
    }
}
