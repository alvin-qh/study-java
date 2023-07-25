package alvin.study.springboot.aop.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识被拦截方法的注解
 *
 * <p>
 * 通过注解定义的切面, 可以拦截所有被当前注解标记的方法, 参考: {@link AnnotationAdvice#point()} 方法
 * </p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {}
