package alvin.study.testing.junit.tag;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 定义一个具备标签功能的测试注解
 *
 * <p>
 * 该注解同时具备 {@code @Tag("<tag-name>")} 和 {@code @Test}
 * 注解的功能
 * </p>
 *
 * <p>
 * 如果去掉 {@code @Test} 注解, 则在测试方法上需要加上此标签
 * </p>
 */
@Tag("important")
@Test
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImportantTagTest {}
