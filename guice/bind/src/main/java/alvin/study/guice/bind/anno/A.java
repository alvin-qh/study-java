package alvin.study.guice.bind.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;

/**
 * 该注解用于区分同一类型的不同绑定关系
 *
 * <p>
 * {@link BindingAnnotation @BindingAnnotation}
 * 注解表示当前注解的作用是用于标识绑定关系
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface A {}
