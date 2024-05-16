package alvin.study.quarkus.util;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 字符串工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {
    public static boolean isNullOrEmpty(CharSequence s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotNullOrEmpty(CharSequence s) {
        return !isNullOrEmpty(s);
    }

    public static String emptyElse(String s, String defaultValue) {
        return isNullOrEmpty(s) ? defaultValue : s;
    }

    public static <R> R emptyThen(String s, R value) {
        return emptyThenOrElse(s, value, null);
    }

    public static <R> R emptyThenOrElse(String s, R value, R defaultValue) {
        return StringUtil.isNullOrEmpty(s) ? defaultValue : value;
    }

    public static String emptyElseGet(String s, Supplier<String> otherwiseFn) {
        return isNullOrEmpty(s) ? otherwiseFn.get() : s;
    }

    public static <R> R emptyThenOrElseGet(String s, R value, Supplier<R> otherwiseFn) {
        return StringUtil.isNullOrEmpty(s) ? otherwiseFn.get() : value;
    }

    public static <R> R emptyThenMapping(String s, Function<String, R> mappingFn) {
        return emptyThenMappingOrElse(s, mappingFn, (R) null);
    }

    public static <R> R emptyThenMappingOrElse(String s, Function<String, R> mappingFn, R defaultValue) {
        return StringUtil.isNullOrEmpty(s) ? defaultValue : mappingFn.apply(s);
    }

    public static <R> R emptyThenMappingOrElseGet(String s, Function<String, R> mappingFn, Supplier<R> otherwiseFn) {
        return StringUtil.isNullOrEmpty(s) ? otherwiseFn.get() : mappingFn.apply(s);
    }
}
