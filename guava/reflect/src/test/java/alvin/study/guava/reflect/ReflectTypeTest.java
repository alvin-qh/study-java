package alvin.study.guava.reflect;

import static org.assertj.core.api.BDDAssertions.then;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.InstanceOfAssertFactories;

import com.google.common.reflect.TypeToken;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link ReflectType} 类型, 产生具备泛型参数的类型
 */
class ReflectTypeTest {
    /**
     * 测试 {@link ReflectType#listOf(Class)} 方法, 产生一个指定泛型参数的 {@link List} 集合
     */
    @Test
    void listOf_shouldGetListTypeWithGenericTypeParameter() {
        // 产生一个 List<String> 类型, 泛型参数为 String
        var type = ReflectType.listOf(String.class);

        then(type).isNotNull();

        // 确认类型为 List 类型
        then(type).extracting(TypeToken::getRawType).isEqualTo(List.class);

        // 确认 List 的泛型参数为 String
        then(type).extracting(TypeToken::getType)
                .isInstanceOf(ParameterizedType.class) // 确认得到的类型为 ParameterizedType 类型
                .extracting(t -> (ParameterizedType) t)
                .extracting(t -> t.getActualTypeArguments()[0]) // 确认泛型参数的第一个参数为 String
                .isEqualTo(String.class);
    }

    /**
     * 测试 {@link ReflectType#listOf(TypeToken)} 方法, 产生一个指定泛型参数的 {@link List} 集合
     */
    @Test
    void listOf_shouldGetListTypeWithNestGenericTypeParameter() {
        // 产生一个 List<Set<String>> 类型, 泛型参数为 Set<String>
        var type = ReflectType.listOf(new TypeToken<Set<String>>() {});

        then(type).isNotNull();

        // 确认类型为 List 类型
        then(type).extracting(TypeToken::getRawType).isEqualTo(List.class);

        // 确认 List 的泛型参数为 Set<String>
        then(type).extracting(TypeToken::getType)
                .extracting(t -> ((ParameterizedType) t).getActualTypeArguments()[0])
                .matches(t -> ((ParameterizedType) t).getRawType().equals(Set.class))
                .extracting(t -> ((ParameterizedType) t).getActualTypeArguments()[0])
                .isEqualTo(String.class);
    }

    /**
     * 测试 {@link ReflectType#mapOf(Class, Class)} 方法, 产生一个指定泛型参数的 {@link Map} 集合
     */
    @Test
    void mapOf_shouldGetMapTypeWithGenericTypeParameter() {
        // 产生一个 Map<String, Object> 类型, 泛型参数为 String 和 Object
        var type = ReflectType.mapOf(String.class, Object.class);

        then(type).isNotNull();

        // 确认类型为 Map 类型
        then(type).extracting(TypeToken::getRawType).isEqualTo(Map.class);

        // 确认 Map 的泛型参数为 String 和 Object
        then(type).extracting(TypeToken::getType)
                .extracting(t -> ((ParameterizedType) t).getActualTypeArguments())
                .asInstanceOf(InstanceOfAssertFactories.array(Type[].class))
                .contains(String.class, Object.class);
    }
}
