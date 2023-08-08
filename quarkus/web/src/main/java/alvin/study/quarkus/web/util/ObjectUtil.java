package alvin.study.quarkus.web.util;

import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectUtil {
    public static <R> R nullElse(R value, R otherwise) {
        return value == null ? otherwise : value;
    }

    public static <R> R nullElse(R value, Supplier<R> otherwise) {
        return value == null ? otherwise.get() : value;
    }
}
