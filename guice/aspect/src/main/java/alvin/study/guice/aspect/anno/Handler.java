package alvin.study.guice.aspect.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个方法为事件处理方法的注解
 * <p>
 * 参考 {@code HandlerDemo} 类
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    /**
     * 指定事件处理的名称
     *
     * @return 名称字符串, 标识一个事件处理
     */
    String name();
}
