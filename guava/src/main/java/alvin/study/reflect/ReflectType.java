package alvin.study.reflect;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 反射类型工具类
 * <p>
 * 本类通过 {@link TypeToken} 类型来产生指定泛型参数的泛型类型
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectType {
    /**
     * 产生 {@code List<T>} 泛型类型, 指定 {@code T} 泛型参数类型
     *
     * @param elementType {@code T} 泛型参数类型
     * @param <T>         泛型参数
     * @return {@code List<T>} 泛型类型
     */
    public static <T> TypeToken<List<T>> listOf(Class<T> elementType) {
        return (new TypeToken<List<T>>() {})
            .where(new TypeParameter<>() {}, elementType);
    }
}
