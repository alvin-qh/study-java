package alvin.study.reflect;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

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

    @Test
    void of_shouldGetTypeTokenFromGenericClassObject2() {
        var token = ReflectType.listOf(String.class);

        then(token.getType())
            .extracting(t -> (ParameterizedType) t)
            .extracting(ParameterizedType::getActualTypeArguments)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .hasSize(1)
            .contains(String.class);

        // 确认 TypeToken 对象表示的类型是否为 List<String>
        then(token.getRawType()).isEqualTo(List.class);
    }

    @Test
    void isArray_shouldCheckTypeIsArray() {
        var token = TypeToken.of(String[].class);

        then(token.isArray()).isTrue();
    }
}
