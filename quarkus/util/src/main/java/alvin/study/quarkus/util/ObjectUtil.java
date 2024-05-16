package alvin.study.quarkus.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 对象工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectUtil {
    /**
     * 对于空对象返回 {@code value} 参数值
     *
     * @param <R>   返回类型
     * @param o     对象参数, 如果该参数不为空, 则返回
     * @param value
     * @return
     */
    public static <R> R nullThen(Object o, R value) {
        return nullThenOrElse(o, value, null);
    }

    public static <T> T nullElse(T o, T defaultValue) {
        return Objects.requireNonNullElse(o, defaultValue);
    }

    public static <R> R nullThenOrElse(Object o, R value, R defaultValue) {
        return o == null ? defaultValue : value;
    }

    public static <T> T nullElseGet(T o, Supplier<T> otherwiseFn) {
        return Objects.requireNonNullElseGet(o, otherwiseFn);
    }

    public static <R> R nullThenOrElseGet(Object o, R value, Supplier<R> otherwiseFn) {
        return o == null ? otherwiseFn.get() : value;
    }

    public static <T, R> R nullThenMapping(T o, Function<T, R> mappingFn) {
        return nullThenMappingOrElse(o, mappingFn, null);
    }

    public static <T, R> R nullThenMappingOrElse(T o, Function<T, R> mappingFn, R defaultValue) {
        return o == null ? defaultValue : mappingFn.apply(o);
    }

    public static <T, R> R emptyThenMappingOrElseGet(T o, Function<T, R> mappingFn, Supplier<R> otherwiseFn) {
        return o == null ? otherwiseFn.get() : mappingFn.apply(o);
    }
}
