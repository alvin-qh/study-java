package alvin.study.se.annotation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.Nonnull;

/**
 * 注解相关的工具类
 *
 * <p>
 * 一般情况下, 注解主要是通过反射来获取. 通过 {@link Class#getAnnotation(Class)} 对象,
 * {@link Method#getAnnotation(Class)} 对象或者
 * {@link java.lang.reflect.Field#getAnnotation(Class)
 * Field.getAnnotation(Class)} 等方法即可获取注解对象, 进而获取注解上的属性值
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationUtil {
    /**
     * 获取类型上的注解对象
     *
     * @param <T>            注解类型
     * @param clazz          被注解的类型
     * @param annotationType 注解类型
     * @return 对象中所有被注解方法以及注解组成的 {@link Map} 对象
     */
    public static <T extends Annotation> Optional<T> getClassAnnotation(@Nonnull Class<?> clazz,
            Class<T> annotationType) {
        return Optional.ofNullable(clazz.getAnnotation(annotationType));
    }

    /**
     * 获取字段上的注解对象
     *
     * @param <T>            注解类型
     * @param clazz          方法所在的类
     * @param fieldName      被注解字段的名称
     * @param annotationType 注解类型
     * @return 所有符合条件的注解对象的集合
     */
    public static <T extends Annotation> List<T> getFieldAnnotation(
            @Nonnull Class<?> clazz,
            String fieldName,
            Class<T> annotationType) {

        return Arrays.stream(clazz.getDeclaredFields())
                // 按字段名称过滤
                .filter(f -> f.getName().equals(fieldName))
                // 按字段上是否具备注解过滤
                .filter(f -> f.isAnnotationPresent(annotationType))
                // 转为注解对象
                .map(f -> f.getAnnotation(annotationType))
                // 转为集合
                .toList();
    }

    /**
     * 获取方法上的注解对象
     *
     * @param <T>            注解类型
     * @param clazz          方法所在的类
     * @param methodName     被注解方法的名称
     * @param annotationType 注解类型
     * @return 所有符合条件的注解对象的集合
     */
    public static <T extends Annotation> List<T> getMethodAnnotation(
            @Nonnull Class<?> clazz,
            String methodName,
            Class<T> annotationType) {
        // 遍历对象上的所有方法
        return Arrays.stream(clazz.getDeclaredMethods())
                // 按方法名称过滤
                .filter(m -> m.getName().equals(methodName))
                // 按方法上是否具备注解过滤
                .filter(m -> m.isAnnotationPresent(annotationType))
                // 转为注解对象
                .map(m -> m.getAnnotation(annotationType))
                // 转为集合
                .toList();
    }
}
