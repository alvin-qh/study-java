package alvin.study.se.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义注解
 *
 * <p>
 * 定义注解通过 {@code @interface} 关键字
 * </p>
 *
 * <p>
 * {@link Documented @Documented} 注解表示当使用当前注解的类再生成 Java Doc 时, 需要将该注解一同生成到文档中
 * </p>
 *
 * <p>
 * {@link Target @Target} 注解指定当前注解的使用范围 (可以指定多个范围), 由 {@link ElementType} 枚举指定,
 * 本例中 {@link ElementType#METHOD} 表示当前注解只能用于方法
 * </p>
 *
 * <p>
 * {@link Retention @Retention} 注解表示当前注解的使用时机, {@link RetentionPolicy#RUNTIME}
 * 表示当前注解是在运行时起作用
 * </p>
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface C {
    /**
     * 定义注解的属性
     *
     * <p>
     * {@code value} 为注解的默认属性, 使用时可以省略属性名, 本例中属性值类型为数组类型, 则可以通过如下方式设置
     * <code>@C(value = { "value1", "value2" })</code> 或
     * <code>@C({ "value1", "value2" })</code>
     * </p>
     *
     * <p>
     * 如果值只有一个, 则也可以这样写: {@code @C(value = "value")} 或 {@code @C("value")}
     * </p>
     */
    String[] value();
}
