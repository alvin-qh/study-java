package alvin.study.springcloud.nacos.core.model.annotation;

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
 * 用于定义 Mapper 类型的注解类型
 *
 * <p>
 * 该注解本质上是一个 {@code SCOPE_PROTOTYPE} 的 {@link Component @Component} 注解
 * </p>
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Target(ElementType.TYPE)
@Component
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {
    /**
     * 代理 {@link Component @Component} 注解的 {@code value} 属性
     *
     * @return {@link Component @Component} 注解的 {@code value} 属性
     */
    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
