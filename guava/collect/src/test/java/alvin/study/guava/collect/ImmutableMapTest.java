package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 演示不变 Map 的使用
 *
 * <p>
 * 和 {@link java.util.Collections#unmodifiableMap(java.util.Map) Collections.unmodifiableMap(Map)} 等方法包装的不可修改
 * Map 不同, 它本质上是对现有集合对象的一个代理, 只是禁用了修改集合元素的一系列方法, 作用是保障代码安全; 而 Guava 提供的不变 Map 则是从数
 * 据结构上不支持修改, 除保障代码安全外, 还可以提供更好的执行效率和内存空间使用率, 且无需担心线程安全问题
 * </p>
 *
 * <p>
 * 本例中以 {@link ImmutableMap} 类型为例, 演示不变 Map 类型的使用
 * </p>
 *
 * <p>
 * 注意, 基于设计原因, {@link ImmutableMap} 不允许使用 {@code null} 作为 Key 和 Value 值
 * </p>
 *
 * <p>
 * JDK 9 以后, JDK 本身提供了 {@link java.util.Map#of(Object, Object) Map.of(K1, V1, ..., Vn, Vn)} 方法, 可以取代 Guava 库的使用
 * </p>
 */
@SuppressWarnings("deprecation")
class ImmutableMapTest {
    /**
     * 通过指定键值对构建不变 Map 对象
     *
     * <p>
     * {@link ImmutableMap#of(Object, Object)} 方法具备若干重载形式, 通过一系列参数, 按照 {@code K1, V1, K2, V2, ..., Kn, Vn}
     * 的顺序, 交替传递键值, 构建 Map 对象
     * </p>
     */
    @SuppressWarnings("null")
    @Test
    void of_shouldCreateImmutableMap() {
        var map = ImmutableMap.of("A", 100, "B", 200);

        then(map).containsExactly(
            entry("A", 100),
            entry("B", 200));

        // 确认 ImmutableMap 对象不支持修改操作
        thenThrownBy(() -> map.put("C", 300)).isInstanceOf(UnsupportedOperationException.class);

        // 确认 ImmutableMap 对象不能包含值为 null 的 Key
        thenThrownBy(() -> ImmutableMap.of(null, 100)).isInstanceOf(NullPointerException.class);
        // 确认 ImmutableMap 对象不能包含值为 null 的 Value
        thenThrownBy(() -> ImmutableMap.of("C", null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * 通过指定 {@link Map.Entry} 对象构建不变 Map 对象
     *
     * <p>
     * {@link ImmutableMap#ofEntries(Map.Entry...)} 通过一系列 {@link Map.Entry} 类型参数构建 Map 对象
     * </p>
     */
    @Test
    void ofEntries_shouldCreateImmutableMap() {
        var map = ImmutableMap.ofEntries(
            Map.entry("A", 100),
            Map.entry("B", 200));

        then(map).containsExactly(
            entry("A", 100),
            entry("B", 200));

        // 确认 ImmutableMap 中不能包含 Key 为 null 的 Entry
        thenThrownBy(() -> ImmutableMap.ofEntries(Map.entry(null, 300))).isInstanceOf(NullPointerException.class);
        // 确认 ImmutableMap 中不能包含 Value 为 null 的 Entry
        thenThrownBy(() -> ImmutableMap.ofEntries(Map.entry("C", null))).isInstanceOf(NullPointerException.class);
    }

    /**
     * 测试创建 Map 对象的不变副本
     *
     * <p>
     * {@link ImmutableMap#copyOf(Map)} 方法创建一个 {@link java.util.Map Map} 对象的不变副本
     * </p>
     */
    @Test
    void copyOf_shouldCreateImmutableMap() {
        // 创建原 Map 对象
        var src = Maps.newHashMap();
        src.put("A", 100);
        src.put("B", 200);

        // 根据原 Map 对象创建不变副本
        var result = ImmutableMap.copyOf(src);

        // 确认副本被创建且和原 Map 对象包含相同的内容
        then(result).isNotSameAs(src);
        then(result).containsExactlyEntriesOf(src);

        // 确认原 Map 中如果包含值为 null 的 Key, 则无法创建副本
        src.put(null, 300);
        thenThrownBy(() -> ImmutableMap.copyOf(src)).isInstanceOf(NullPointerException.class);

        // 确认原 Map 中如果包含值为 null 的 Value, 则无法创建副本
        src.remove(null);
        src.put("C", null);
        thenThrownBy(() -> ImmutableMap.copyOf(src)).isInstanceOf(NullPointerException.class);
    }

    /**
     * 通过 {@link ImmutableMap.Builder} 构建不变 Map 对象
     *
     * <p>
     * {@link ImmutableMap#builder()} 可以创建一个 {@link ImmutableMap.Builder} 对象, 通过该对象可以分批设置键值对, 最终构建
     * 不变 Map 对象
     * </p>
     *
     * <p>
     * 另外, {@link ImmutableMap#builderWithExpectedSize(int)} 可以设置一个预期的键值对数量, 提前分批足够的内存, 减少构建过程中
     * 重建存储内存的性能影响. 预期的键值对数量不会导致实际添加键值对时发生失败, 不当的设置只会导致内存浪费或出现存储区重建情况
     * </p>
     *
     * <p>
     * 注意: {@link ImmutableMap.Builder#build()} 方法内部调用了 {@link ImmutableMap.Builder#buildOrThrow()} 方法, 在添加
     * 键值对时, 如果出现重复的键值, 则会导致抛出异常
     * </p>
     */
    @SuppressWarnings("null")
    @Test
    void builder_shouldBuildImmutableMap() {
        // 创建一个 Builder 对象, 分批添加键值对, 构建不变 Map 对象
        var map = ImmutableMap.<String, Integer>builder()
                .put("A", 100)
                .put(Map.entry("B", 200))
                .putAll(ImmutableMap.of("C", 300))
                .build();
        // 确认创建的 Map 包含正确的键值对
        then(map).containsExactly(
            entry("A", 100),
            entry("B", 200),
            entry("C", 300));

        // 确认如果在构建过程中添加了重复的键值, 会导致异常抛出
        thenThrownBy(() -> ImmutableMap.<String, Integer>builder()
                .put("A", 100)
                .put("A", 400)
                .build())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Multiple entries with same key: A=400 and A=100");

        // 确认如果在构建过程中添加了值为 null 的 key, 会导致异常抛出
        thenThrownBy(() -> ImmutableMap.builder()
                .put(null, 100).build())
                        .isInstanceOf(NullPointerException.class);

        // 确认如果在构建过程中添加了值为 null 的 Value, 会导致异常抛出
        thenThrownBy(() -> ImmutableMap.builder()
                .put("A", null).build())
                        .isInstanceOf(NullPointerException.class);
    }

    /**
     * 测试解决 {@link ImmutableMap.Builder} 在构建过程中出现键冲突的情况
     *
     * <p>
     * {@link ImmutableMap.Builder#buildKeepingLast()} 执行的策略是: 如果在构建过程中出现重复的键, 则保留最后一次添加的键值对, 覆盖
     * 之前添加的键值对
     * </p>
     *
     * <p>
     * {@link ImmutableMap.Builder#buildKeepingLast()} 不会在构建过程中产生键重复异常
     * </p>
     */
    @SuppressWarnings("null")
    @Test
    void builder_shouldBuildImmutableMapAndKeepLastValueIfKeyDuplicate() {
        // 通过 buildKeepingLast 方法, 不会因为添加了重复键导致异常
        var map = ImmutableMap.<String, Integer>builderWithExpectedSize(3)
                .put("A", 100)
                .put(Map.entry("B", 200))
                .putAll(ImmutableMap.of("C", 300))
                .put("B", 400)
                .buildKeepingLast();

        // 确认重复键对应为最后一次添加的键值对
        then(map).contains(
            entry("A", 100),
            entry("B", 400),
            entry("C", 300));

        // 确认如果在构建过程中添加了值为 null 的 key, 会导致异常抛出
        thenThrownBy(() -> ImmutableMap.builder()
                .put(null, 100).buildKeepingLast())
                        .isInstanceOf(NullPointerException.class);

        // 确认如果在构建过程中添加了值为 null 的 Value, 会导致异常抛出
        thenThrownBy(() -> ImmutableMap.builder()
                .put("A", null).buildKeepingLast())
                        .isInstanceOf(NullPointerException.class);
    }
}
