package alvin.study.guava.reflect;

import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    /**
     * 产生 {@code List<T>} 泛型类型, 通过 {@link TypeToken} 对象来指定 {@code T} 泛型参数类型,
     * 即泛型参数本身也可以为泛型类型
     *
     * @param elementType 通过 {@link TypeToken} 对象表示的泛型参数类型
     * @param <T>         泛型参数
     * @return {@code List<T>} 泛型类型
     */
    public static <T> TypeToken<List<T>> listOf(TypeToken<T> elementType) {
        return (new TypeToken<List<T>>() {})
                .where(new TypeParameter<>() {}, elementType);
    }

    /**
     * 产生 {@code Map<K, V>} 泛型类型, 指定 {@code K, V} 泛型参数类型
     * <p>
     * 和 {@link #listOf(TypeToken)} 方法类似, 也可以编写参数为 {@link TypeToken} 类型的参数
     *
     * @param keyType   Key 的泛型类型
     * @param valueType Value 的泛型类型
     * @param <K>       Key 泛型参数
     * @param <V>       Value 泛型参数
     * @return {@code Map<K, V>} 泛型类型
     */
    public static <K, V> TypeToken<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
        return (new TypeToken<Map<K, V>>() {})
                .where(new TypeParameter<>() {}, keyType)
                .where(new TypeParameter<>() {}, valueType);
    }
}
