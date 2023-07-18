package alvin.study.reflect;

import com.google.common.reflect.TypeToken;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link TypeToken} 类型
 * <p>
 * {@link TypeToken} 类型可以保留泛型类型的泛型参数, 避免在编译后泛型类型被擦除
 */
public class TypeTokenTest {
    /**
     * 测试 {@link TypeToken#of(Class)} 方法用于普通类型
     * <p>
     * 通过一个 {@link Class} 对象获取一个 {@link TypeToken} 对象
     * <p>
     * {@link TypeToken#getType()} 方法用于获取 {@link TypeToken} 对象表示的类型
     * <p>
     * {@link TypeToken#getRawType()} 方法用于获取 {@link TypeToken} 对象表示的原始类型
     */
    @Test
    void of_shouldGetTypeTokenFromClassObject() {
        // 通过 Class 对象获取 TypeToken 对象
        var token = TypeToken.of(String.class);

        // 确认 TypeToken 对象表示的类型
        then(token.getType()).isEqualTo(String.class);
        // 确认 TypeToken 对象表示的原始类型
        then(token.getRawType()).isEqualTo(String.class);
    }

    /**
     * 测试 {@link TypeToken#of(Class)} 方法用于泛型类型
     */
    @Test
    void of_shouldGetTypeTokenFromGenericClassObject() {
        // 创建针对泛型类型的 TypeToken 对象
        var token = new TypeToken<List<String>>() {};

        // 确认 TypeToken 对象表示的类型是一个 ParameterizedType 类型, 即具有泛型参数的类型
        // 从而可以获取泛型参数类型
        then(token.getType())
            .isInstanceOf(ParameterizedType.class)
            .extracting(t -> (ParameterizedType) t)
            .extracting(ParameterizedType::getActualTypeArguments)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .hasSize(1)  // 确认获取的类型具有一个泛型参数, 且为 String.class
            .contains(String.class);

        // 确认 TypeToken 对象表示的原始类型的泛型参数已被擦除
        then(token.getRawType()).isEqualTo(List.class);
    }

    /**
     * 测试 {@link ReflectType#listOf(Class)} 方法, 通过一个泛型参数类型产生 {@code List<T>} 泛型类型
     */
    @Test
    void listOf_shouldCreateListTypeByGivenType() {
        // 产生一个泛型参数为 String 类型的 List<String> 类型
        var token = ReflectType.listOf(String.class);

        // 确认 TypeToken 对象表示的类型为 List<String>
        then(token.getType())
            .extracting(t -> (ParameterizedType) t)  // 确认类型为具备参数的泛型类型
            .extracting(pt -> Arrays.asList(pt.getActualTypeArguments())) // 获取泛型类型的泛型参数列表
            .asList()
            .hasSize(1) // 确认类型的泛型参数列表包含一个参数, 且为 String 类型
            .contains(String.class);

        // 确认 TypeToken 对象表示的类型是否为 List<String>
        then(token.getRawType()).isEqualTo(List.class);
    }

    /**
     * 测试 {@link ReflectType#listOf(TypeToken)} 方法, 通过一个泛型类型的 {@link TypeToken} 对象对象作为 List 泛型参数
     */
    @Test
    void listOf_shouldCreateListTypeByGivenTypeToken() {
        // 产生一个泛型参数为泛型类型的 List<List<String>> 类型
        var token = ReflectType.listOf(new TypeToken<List<String>>() {});

        // 确认 TypeToken 对象表示的类型为 List<List<String>>
        then(token.getType())
            .extracting(t -> (ParameterizedType) t) // 确认类型为具备参数的泛型类型
            .extracting(pt -> Arrays.asList(pt.getActualTypeArguments())) // 获取泛型类型的泛型参数列表
            .asList()
            .hasSize(1) // 确认类型的泛型参数列表包含一个参数, 且为 List 类型
            .element(0)
            .matches(t -> ((ParameterizedType) t).getRawType().equals(List.class))
            .extracting(t -> (ParameterizedType) t) // 获取泛型参数的类型
            .extracting(pt -> Arrays.asList(pt.getActualTypeArguments())) // 获取泛型参数的泛型列表
            .asList()
            .hasSize(1) // 确认泛型参数的泛型类型为 String 类型
            .element(0)
            .isEqualTo(String.class);
    }

    /**
     * 测试 {@link ReflectType#mapOf(Class, Class)} 方法, 通过 Key 和 Value 的类型, 创建一个 {@code Map<K, V>} 类型
     */
    @Test
    void mapOf_shouldCreateMapTypeByGivenType() {
        // 产生一个 Key 类型为 String, Value 类型为 Object 的 Map<String, Object> 类型
        var token = ReflectType.mapOf(String.class, Object.class);

        // 确认 TypeToken 对象表示的类型为 Map<String, Object>
        then(token.getType())
            .extracting(t -> (ParameterizedType) t) // 确认类型为具备参数的泛型类型
            .extracting(pt -> Arrays.asList(pt.getActualTypeArguments())) // 获取泛型参数列表
            .asList()
            .hasSize(2) // 确认类型的涛型参数列表包含两个参数, 且为 String 和 Object 类型
            .contains(String.class, Object.class);
    }

    /**
     * 测试 {@link TypeToken#isArray()} 方法, 判断一个类型是否为数组类型
     */
    @Test
    void isArray_shouldCheckTypeIsArray() {
        // 通过数组类型构造 TypeToken 对象
        var token = TypeToken.of(String[].class);

        // 确认该 TypeToken 对象表示的类型为数组类型
        then(token.isArray()).isTrue();
    }
}
