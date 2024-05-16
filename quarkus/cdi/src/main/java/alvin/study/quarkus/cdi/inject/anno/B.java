package alvin.study.quarkus.cdi.inject.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.inject.Qualifier;

/**
 * 定义 Bean 标识注解
 *
 * <p>
 * 对于同一类型的不同 Bean, 需要进行区分才能保证明确的注入, 因为 Quarkus 中不能使用 {@link jakarta.inject.Named @Named} 注解,
 * 所以需要通过 {@link Qualifier @Qualifier} 注解为 Bean 设置标记
 * </p>
 *
 * <p>
 * {@link Qualifier @Qualifier} 注解本身不能用于标记 Bean, 需要通过具备该注解的其它注解来进行 (例如这里的 {@link B @B} 注解),
 * 其使用参考 {@link alvin.study.quarkus.cdi.inject.SimpleBeanProduces BeanProduces} 中对注解的使用
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Documented
public @interface B {
}
