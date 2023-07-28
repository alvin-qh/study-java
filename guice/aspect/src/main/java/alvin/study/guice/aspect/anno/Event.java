package alvin.study.guice.aspect.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个方法需要事件处理的注解
 * <p>
 * 参考 {@code EventDemo} 类
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    /**
     * 定义要处理事件的 {@link Handler @Handler} 注解的 {@code name} 属性
     *
     * @return 名称字符串, 表示 {@link Handler @Handler} 注解的 {@code name} 属性
     */
    String handler();
}
