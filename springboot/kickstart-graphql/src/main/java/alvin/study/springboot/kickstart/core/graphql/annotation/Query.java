package alvin.study.springboot.kickstart.core.graphql.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Component @Component} 注解的别名注解, 表示被注解类型为一个 Query
 */
@Documented
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Query {
    /**
     * {@link #value()} 属性转到 {@link Component#value()} 属性上
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
