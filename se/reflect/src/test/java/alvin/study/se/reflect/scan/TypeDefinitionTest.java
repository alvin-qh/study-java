package alvin.study.se.reflect.scan;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.jupiter.api.Test;

import alvin.study.se.reflect.generic.TypeDefinition;

/**
 * 测试 {@link TypeDefinition} 类型, 获取泛型参数类型
 */
class TypeDefinitionTest {
    /**
     * 测试获取泛型参数的类型
     *
     * <p>
     * 通过 {@link TypeDefinition#getTypeName()}, {@link TypeDefinition#getRawType()}
     * 以及 {@link TypeDefinition#getActualTypeArguments()} 三个方法进行泛型参数的获取
     * </p>
     */
    @Test
    void genericArgumentType_shouldGetGenericType() {
        // 实例化具备泛型类型参数的对象
        var def = new TypeDefinition<List<String>>() {};

        // 确认获取泛型参数类型名正确
        then(def.getTypeName()).isEqualTo("java.util.List<java.lang.String>");

        // 确认获取泛型参数的原始类型正确
        then(def.getRawType()).isSameAs(List.class);

        // 确认获取泛型参数类型的泛型参数正确
        then(def.getActualTypeArguments())
                .hasSize(1)
                .containsExactly(String.class);
    }
}
