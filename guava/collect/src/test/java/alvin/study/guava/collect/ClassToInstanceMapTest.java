package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * 维护 {@link Class} 与其实例之间对应关系的 {@link java.util.Map Map} 类型
 *
 * <p>
 * 如果需要快速通过一个类型获取其"单例"对象, Guava 提供了 {@link com.google.common.collect.ClassToInstanceMap
 * ClassToInstanceMap} 接口来协助这项工作
 * </p>
 *
 * <p>
 * 该接口本质上是一个 {@code Map<Class<?>, Object>} 类型接口, 维护对象类型和对象实例之间的关系
 * </p>
 *
 * <p>
 * 与 {@link java.util.Map Map} 不同, {@code ClassToInstanceMap} 接口提供了额外的方法, 在设置对应关系时, 会检查实例是否和所给的类型
 * 匹配, 这些方法包括: {@link com.google.common.collect.ClassToInstanceMap#putInstance(Class, Object)
 * ClassToInstanceMap.putInstance(Class, Object)} 和
 * {@link com.google.common.collect.ClassToInstanceMap#getInstance(Class) ClassToInstanceMap.getInstance(Class)}
 * </p>
 *
 * <p>
 * 另外, 也可以提供泛型参数, 将类型限制在指定的类型的子类范围内
 * </p>
 *
 * <p>
 * Guava 提供了 {@code ClassToInstanceMap} 接口的两个实现类型:
 * <ul>
 * <li>{@link MutableClassToInstanceMap}, 表示一个可变的 {@code Map} 对象, 即可以随时修改</li>
 * <li>{@link ImmutableClassToInstanceMap}, 表示一个不可变的 {@code Map} 对象, 即对象构建完毕后, 无法修改其存储的内容</li>
 * </ul>
 * </p>
 */
class ClassToInstanceMapTest {
    /**
     * 测试 {@link MutableClassToInstanceMap} 类型
     *
     * <p>
     * 该类型存储指定类型和实例之间的对应关系, 且可以随时进行改变
     * </p>
     */
    @Test
    void mutableMapping_shouldMappingClassToObjectValue() {
        // 创建对象, 指定对象类型和对象实例必须为 Number 类型的子类型
        var map = MutableClassToInstanceMap.<Number>create();

        // 添加类型和实例的对应关系
        map.putInstance(Double.class, 0.1);
        map.putInstance(Integer.class, 100);

        // 通过类型获取对应的实力, 确定和存储的实例一致
        then(map.getInstance(Double.class)).isEqualTo(0.1);
        then(map.getInstance(Integer.class)).isEqualTo(100);
    }

    /**
     * 测试 {@link ImmutableClassToInstanceMap} 类型
     *
     * <p>
     * 该类型存储指定类型和实例之间的对应关系, 且实例化之后无法改变其存储内容
     * </p>
     */
    @Test
    void immutableMapping_shouldMappingClassToObjectValue() {
        // 创建对象, 指定对象类型和对象实例必须为 Number 类型的子类型
        // 因为创建的为不可变对象, 所以需要通过 Builder 对象进行创建
        var map = ImmutableClassToInstanceMap.<Number>builder()
                // 添加类型和实例的对应关系
                .put(Double.class, 0.1)
                .put(Integer.class, 100)
                .build();

        // 通过类型获取对应的实力, 确定和存储的实例一致
        then(map.getInstance(Double.class)).isEqualTo(0.1);
        then(map.getInstance(Integer.class)).isEqualTo(100);
    }
}
