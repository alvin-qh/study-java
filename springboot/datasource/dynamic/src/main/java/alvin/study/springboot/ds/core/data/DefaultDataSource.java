package alvin.study.springboot.ds.core.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于将数据源强行切换到默认数据源的注解
 *
 * <p>
 * 该注解通过 AOP 拦截执行方法, 在方法执行前切换到默认数据源并在方法执行后恢复到之前的数据源, 具体切换步骤参考
 * {@link DefaultDataSourceAnnotationAdvice} 切面类型
 * </p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultDataSource { }
