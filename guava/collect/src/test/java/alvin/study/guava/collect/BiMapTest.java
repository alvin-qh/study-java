package alvin.study.guava.collect;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.HashBiMap;

/**
 * {@link com.google.common.collect.BiMap BiMap} 接口可以通过 Key 查找 Value, 同时也可以通过 Value 查找 Key
 *
 * <p>
 * 一个 {@link com.google.common.collect.BiMap BiMap} 对象相当于同时创建了两个 {@link java.util.Map Map} 对象, 且两个
 * {@code Map} 对象互相以对方的 Key 为 Value, Value 为 Key
 * </p>
 *
 * <p>
 * 所以和普通的 {@link java.util.Map} 对象相比, {@link com.google.common.collect.BiMap BiMap} 不仅要求 Key 唯一, 且 Value 也
 * 必须唯一, 这样才能在调转 Key 和 Value 时, 仍旧是一个正确的 {@code Map} 对象
 * </p>
 *
 * <p>
 * 实现了 {@link com.google.common.collect.BiMap BiMap} 接口的类包括:
 * <ul>
 * <li>{@link HashBiMap}, 对应 {@link java.util.HashMap HashMap} 类型, 即通过散列表结构存储键值对</li>
 * <li>
 * {@link com.google.common.collect.ImmutableBiMap ImmutableBiMap}, 对应 {@link com.google.common.collect.ImmutableMap
 * ImmutableMap} 类型, 表示一个不可变的 {@link com.google.common.collect.BiMap BiMap} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.EnumBiMap EnumBiMap}, 对应 {@link java.util.EnumMap EnumMap} 类型, 表示一个 <b>Key 和
 * Value 均为枚举类型</b>的 {@link com.google.common.collect.BiMap BiMap} 类型,
 * </li>
 * <li>
 * {@link com.google.common.collect.EnumHashBiMap EnumHashBiMap}, 对应 {@link java.util.EnumMap EnumMap} 和
 * {@link java.util.HashMap HashMap} 类型, 其反转前, Key 为枚举类型, 通过 {@link java.util.EnumMap EnumMap} 存储, 反转后
 * Key 为 {@link Object} 类型, 通过 {@link java.util.HashMap HashMap} 存储
 * </li>
 * </ul>
 * </p>
 */
class BiMapTest {
    /**
     * 测试对调 {@link com.google.common.collect.BiMap BiMap} 对象的 Key 和 Value
     *
     * <p>
     * {@link com.google.common.collect.BiMap BiMap} 相比 {@link java.util.Map Map} 类型, 多出一个
     * {@link com.google.common.collect.BiMap#inverse() BiMap.inverse()} 方法, 该方法可以返回一个 Key 和 Value 对调的
     * {@link java.util.Map Map} 对象
     * </p>
     *
     * <p>
     * {@link com.google.common.collect.BiMap#put(Object, Object) BiMap.put(key, value)} 方法不能添加 Value 重复的键值对,
     * 会引发异常; 通过 {@link com.google.common.collect.BiMap#forcePut(Object, Object) BiMap.forcePut(key, value)} 方法
     * 可以规避异常, 但同时也会覆盖之前添加的相同 Value 的键值对
     * </p>
     */
    @Test
    void inverse_shouldInverseKeyAndValue() {
        // 创建一个 BiMap 对象, 并设置 Key 和 Value
        var map = HashBiMap.<String, Integer>create();
        map.put("A", 100);
        map.put("B", 200);

        // 确认该 Map 对象包含指定的 Key 和 Value
        then(map).containsExactly(
            entry("A", 100),
            entry("B", 200));

        // 对调 BiMap 对象的 Key 和 Value
        var rMap = map.inverse();

        // 确认对调后, Map 对象将以原本的 Value 作为 Key, Key 作为 Value
        then(rMap).containsExactly(
            entry(100, "A"),
            entry(200, "B"));

        // 在 BiMap 中, 如果存储重复 Value 的键值对, 则会引发异常
        thenThrownBy(() -> map.put("C", 200)).isInstanceOf(IllegalArgumentException.class);

        // 可以强行存储重复 Value 的键值对, 此时后添加的键值对会取代之前的键值对
        map.forcePut("C", 200);
        // 确认相同 Value 的键值对 ("B" = 200) 已经被取代 ("C" = 200)
        then(map).containsExactly(
            entry("A", 100),
            entry("C", 200));
    }
}
