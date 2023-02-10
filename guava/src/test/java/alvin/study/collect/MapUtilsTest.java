package alvin.study.collect;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

import java.time.Month;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

import com.google.common.base.Equivalence;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

/**
 * 演示 Guava {@link java.util.Map Map} 对象工具类
 *
 * <p>
 * {@link Maps} 类提供了一系列静态方法用于创建和操作 {@link HashMap}, {@link TreeMap}, {@link LinkedHashMap}, {@link EnumMap},
 * 以及 {@link ConcurrentHashMap} 等 {@link java.util.Map Map} 类型对象的方法
 * </p>
 */
class MapUtilsTest {
    /**
     * 通过一个 Key 集合创建 {@link java.util.Map Map} 对象
     *
     * <p>
     * {@link Maps#asMap(java.util.Set, com.google.common.base.Function) Maps#asMap(Set, Function)} 通过一个 Key 的
     * {@link java.util.Set Set} 集合以及一个映射函数创建 {@link java.util.Map Map} 对象, 该 {@code Map} 对象的 Value 值是通过
     * Key 值映射得到的
     * </p>
     */
    void asMap_shouldBuildMapByKeyAndMappingFunction() {
        var keys = ContiguousSet.closedOpen(0, 10);

        // 根据 Key 值生成 Map 对象, Value 值从 Key 值映射而来
        var map = Maps.asMap(keys, n -> String.format("0%d", n));

        // 确认得到的 Map 对象符合预期
        then(map).containsExactly(
            entry(0, "01"),
            entry(1, "01"),
            entry(2, "02"),
            entry(3, "03"),
            entry(4, "04"),
            entry(5, "05"),
            entry(6, "06"),
            entry(7, "07"),
            entry(8, "08"),
            entry(9, "09"));
    }

    /**
     * 通过一个 Value 集合创建 {@link java.util.Map Map} 对象
     *
     * <p>
     * {@link Maps#uniqueIndex(Iterable, com.google.common.base.Function) Maps#uniqueIndex(Iterable, Function)} 通过一个
     * Value 的可迭代对象以及一个映射函数创建 {@link java.util.Map Map} 对象, 该 {@code Map} 对象的 Key 值是通过 Value 值映射得到的
     * </p>
     */
    void uniqueIndex_shouldBuildMapByValueAndMappingFunction() {
        var values = ContiguousSet.closedOpen(0, 10).asList();

        // 根据 Value 值生成 Map 对象, Key 值从 Value 值映射而来
        var map = Maps.uniqueIndex(values, n -> String.format("0%d", n));

        // 确认得到的 Map 对象符合预期
        then(map).containsExactly(
            entry("01", 0),
            entry("01", 1),
            entry("02", 2),
            entry("03", 3),
            entry("04", 4),
            entry("05", 5),
            entry("06", 6),
            entry("07", 7),
            entry("08", 8),
            entry("09", 9));
    }

    /**
     * 创建各种类型 {@link java.util.Map Map} 对象
     *
     * <p>
     * 创建 {@link HashMap} 对象
     * <ul>
     * <li>
     * 通过 {@link {@link Maps#newHashMap()} 方法可以创建一个 {@link HashMap} 对象. 该方法有一系列重载, 通过不同类型参数构建
     * {@link HashMap} 对象, 包括:
     * <ul>
     * <li>
     * {@link Maps#newHashMap()}, 构建一个空的 {@link HashMap} 对象, 相当于直接执行 {@link HashMap#HashMap()} 构造器.
     * 注意: 如果只是为了得到一个空集合且不会修改它, 则应该使用 {@link ImmutableMap#of()} 方法, 或在 JDK 9 之后使用
     * {@link java.util.Map#of() Map.of()} 方法
     * </li>
     * <li>
     * {@link Maps#newHashMap(java.util.Map) Maps.newHashMap(Map)}, 通过一系列键值对构建 {@link HashMap} 对象
     * </li>
     * </ul>
     * </li>
     * <li>
     * 除上述重载方法外, Guava 还提供了和集合元素个数设定相关的方法, 可以提高代码执行效率, 包括:
     * <ul>
     * <li>
     * {@link Maps#newHashMapWithExpectedSize(int)} 方法用于产生一个空 {@link HashMap} 对象, 并设置预期的 {@code capacity}
     * 属性, 该对象可以在元素数量到达预期值前, 避免内存重新分配. 注意, 该方法已标记为过期, 应该直接使用 {@code new HashMap(int)}
     * 构造方法, 通过参数设置 {@code capacity} 值
     * </li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link LinkedHashMap} 对象
     * <ul>
     * <li>
     * {@link Maps#newLinkedHashMap()} 方法可以创建空 {@link LinkedHashMap} 类型对象, 和直接使用
     * {@link LinkedHashMap#LinkedHashMap()} 构造器一致
     * </li>
     * <li>
     * {@link Maps#newLinkedHashMap(java.util.Map) Maps.newLinkedHashMap(Map)} 方法通过一系列键值对创建
     * {@link LinkedHashMap} 类型对象
     * </li>
     * <li>
     * {@link Maps#newLinkedHashMapWithExpectedSize(int)} 方法创建具备元素个数预设值的 {@link LinkedHashMap} 类型对象,
     * 类似于 {@link LinkedHashMap#LinkedHashMap(int)} 方法
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link TreeMap} 对象
     * <ul>
     * <li>
     * {@link Maps#newTreeMap()} 方法可以创建空 {@link TreeMap} 类型对象, 和直接使用 {@link TreeMap#TreeMap()} 构造器一致
     * </li>
     * <li>
     * {@link Maps#newTreeMap(java.util.SortedMap) Maps.newTreeMap(SortedMap)} 方法通过一系列有序键值对来构建 {@link TreeMap}
     * 类型对象
     * </li>
     * <li>
     * {@link Maps#newTreeMap(java.util.Comparator) Maps.newTreeMap(Comparator)} 方法创建 {@link TreeMap} 类型对象,
     * 并设置 Key 比较的方法, 该比较方法会影响到集合内部构建二叉树时元素的排列顺序
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link ConcurrentHashMap} 对象
     * <ul>
     * <li>
     * {@link Maps#newConcurrentMap()} 方法可以创建空 {@link ConcurrentHashMap} 类型对象, 相当于调用
     * {@link ConcurrentHashMap#ConcurrentHashMap()} 构造器
     * </li>
     * </p>
     *
     * <p>
     * 创建 {@link EnumMap} 对象
     * <ul>
     * <li>
     * {@link Maps#newEnumMap(Class)} 通过包含枚举对象的可迭代对象即枚举类型产生一个 {@link EnumMap} 对象, 内部存储不重复的枚举对象
     * </li>
     * <li>
     * {@link Maps#newEnumMap(java.util.Map) Maps.newEnumMap(Map)} 通过一组 Key 为枚举的键值对构建 {@link EnumMap} 对象
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@code IdentityHashMap} 对象
     * <ul>
     * <li>
     * {@link Maps#newIdentityHashMap()} 返回 {@link IdentityHashMap} 类型对象
     * </li>
     * <li>
     * 在其它的 {@code Map} 集合中, Key 的比较是通过 {@link Object#equals(Object)} 方法进行的, 而 {@link IdentityHashMap}
     * 对象中, Key 的比较是通过 {@code ==} 运算符进行的, 即 {@link IdentityHashMap} 允许存储"值"相同, 但"引用"不同的多个 Key, 例如:
     * 若干个 {@code new String("A")} 对象
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void create_shouldCreateNewMap() {
        // 构建 hashMap 对象
        {
            // 构建空 HashMap 对象
            var map = Maps.<String, Integer>newHashMap();
            then(map).isInstanceOf(HashMap.class).isEmpty();

            // 通过现有的 Map 对象构建 HashMap 对象
            map = Maps.<String, Integer>newHashMap(ImmutableMap.of("A", 1, "B", 2));
            then(map).isInstanceOf(HashMap.class).containsExactly(
                entry("A", 1),
                entry("B", 2));

            // 通过一个预期的键值对数量值构建 HashMap 对象
            map = Maps.<String, Integer>newHashMapWithExpectedSize(2);
            map.putAll(ImmutableMap.of("A", 1, "B", 2));
            then(map).isInstanceOf(HashMap.class).containsExactly(
                entry("A", 1),
                entry("B", 2));
        }

        // 构建 LinkedHashMap 对象
        {
            // 构建空 LinkedHashMap 对象
            var map = Maps.<String, Integer>newLinkedHashMap();
            then(map).isInstanceOf(LinkedHashMap.class).isEmpty();

            // 通过现有的 Map 对象构建 LinkedHashMap 对象
            map = Maps.<String, Integer>newLinkedHashMap(ImmutableMap.of("A", 1, "B", 2));
            then(map).isInstanceOf(LinkedHashMap.class).containsExactly(
                entry("A", 1),
                entry("B", 2));

            // 通过一个预期的键值对数量值构建 LinkedHashMap 对象
            map = Maps.<String, Integer>newLinkedHashMapWithExpectedSize(2);
            map.putAll(ImmutableMap.of("A", 1, "B", 2));
            then(map).isInstanceOf(LinkedHashMap.class).containsExactly(
                entry("A", 1),
                entry("B", 2));
        }

        // 构建 TreeMap 对象
        {
            // 构建空 TreeMap 对象
            var map = Maps.<String, Integer>newTreeMap();
            then(map).isInstanceOf(TreeMap.class).isEmpty();

            // 通过现有的 Map 对象构建 TreeMap 对象
            map = Maps.<String, Integer>newTreeMap(ImmutableSortedMap.of("A", 1, "B", 2));
            then(map).isInstanceOf(TreeMap.class).containsExactly(
                entry("A", 1),
                entry("B", 2));
        }

        // 构建 ConcurrentHashMap 对象
        {
            // 构建空 ConcurrentHashMap 对象
            var map = Maps.<String, Integer>newConcurrentMap();
            then(map).isInstanceOf(ConcurrentHashMap.class).isEmpty();
        }

        // 构建 EnumMap 对象
        {
            // 通过枚举类型构建空 EnumMap 对象
            var map = Maps.<Month, String>newEnumMap(Month.class);
            then(map).isInstanceOf(EnumMap.class).isEmpty();

            // 通过一个现有的 Key 为枚举类型的 Map 对象构建 EnumMap 对象
            map = Maps.<Month, String>newEnumMap(ImmutableMap.of(
                Month.JANUARY, "01",
                Month.FEBRUARY, "02",
                Month.MARCH, "03"));
            then(map).isInstanceOf(EnumMap.class).containsExactly(
                entry(Month.JANUARY, "01"),
                entry(Month.FEBRUARY, "02"),
                entry(Month.MARCH, "03"));
        }

        // 构建 IdentityHashMap 对象
        {
            // 构建空 IdentityHashMap 对象
            var map = Maps.<String, Integer>newIdentityHashMap();
            then(map).isInstanceOf(IdentityHashMap.class).isEmpty();

            // 在 IdentityHashMap 对象中存储两个值相同但引用不同的 Key
            map.put(new String("A"), 100);
            map.put(new String("A"), 200);

            // 确认值相同但引用不同可以作为不同的 Key
            then(map).hasSize(2);
        }
    }

    /**
     * 计算两个 {@code Map} 集合的差异
     *
     * <p>
     * {@link Maps#difference(java.util.Map, java.util.Map, com.google.common.base.Equivalence)
     * Maps.difference(Map, Map, Equivalence)} 方法用于计算两个 {@code Map} 对象的差异, 返回
     * {@link com.google.common.collect.MapDifference MapDifference} 对象
     * </p>
     *
     * <p>
     * 两个 {@code Map} 对象的差异包括:
     * <ul>
     * <li>
     * {@link com.google.common.collect.MapDifference#areEqual() MapDifference.areEqual()}, 返回 {@code true} 表示两个
     * {@code Map} 对象相同, 否则表示两个 {@code Map} 对象不同
     * </li>
     * <li>
     * {@link com.google.common.collect.MapDifference#entriesInCommon() MapDifference.entriesInCommon()}, 返回一个
     * {@code Map} 对象, 表示两个 {@code Map} 对象中相同的键值对
     * </li>
     * <li>
     * {@link com.google.common.collect.MapDifference#entriesDiffering() MapDifference.entriesDiffering()}, 返回一个
     * {@code Map<K, ValueDifference>} 对象, 表示两个 {@code Map} 对象中 Key 相同但 Value 不同的键值对, 其中:
     * <ul>
     * <li>
     * {@link com.google.common.collect.MapDifference.ValueDifference#leftValue() ValueDifference.leftValue()}
     * 方法返回第一个 {@code Map} 参数中 Key 对应的 Value 值
     * </li>
     * <li>
     * {@link com.google.common.collect.MapDifference.ValueDifference#rightValue() ValueDifference.rightValue()}
     * 方法返回第二个 {@code Map} 参数中 Key 对应的 Value 值
     * </li>
     * </ul>
     * </li>
     * <li>
     * {@link com.google.common.collect.MapDifference#entriesOnlyOnLeft() MapDifference.entriesOnlyOnLeft()}
     * 表示包含在第一个 {@code Map} 参数但不包含在第二个 {@code Map} 参数的键值对
     * </li>
     * <li>
     * {@link com.google.common.collect.MapDifference#entriesOnlyOnRight() MapDifference.entriesOnlyOnRight()}
     * 表示包含在第二个 {@code Map} 参数但不包含在第一个 {@code Map} 参数的键值对
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link Equivalence} 对象定义 Value 比较方法, 用于比较两个 Value 值是否相同
     * </p>
     */
    @Test
    void difference_shouldMakeDifferenceOfTwoMaps() {
        var map1 = ImmutableMap.of("A", 100, "B", 200, "C", 300);
        var map2 = ImmutableMap.of("A", 100, "B", 201, "D", 400);

        // 对两个 Map 对象进行比较, 得到 MapDifference 类型对象
        var difference = Maps.difference(map1, map2, new Equivalence<Integer>() {
            /**
             * 比较两个参数值是否相同
             *
             * @return {@code true} 表示 {@code a} 和 {@code b} 参数相同
             */
            @Override
            protected boolean doEquivalent(Integer a, Integer b) {
                return a.intValue() == b.intValue();
            }

            /**
             * 获取值的 Hash
             *
             * @return 给定 {@code t} 参数的 Hash 值
             */
            @Override
            protected int doHash(Integer t) {
                return Ints.hashCode(t);
            }
        });

        // 确认两个 Map 对象不同
        then(difference.areEqual()).isFalse();

        // 获取两个 Map 中相同的键值对
        then(difference.entriesInCommon()).containsExactly(entry("A", 100));

        // 获取两个 Map 中 Key 相同但 Value 不同的键值对
        // 确认键值对在参数 1 中的键值
        then(difference.entriesDiffering().get("B").leftValue()).isEqualTo(200);
        // 确认键值对在参数 2 中的键值
        then(difference.entriesDiffering().get("B").rightValue()).isEqualTo(201);

        // 确认包含在参数 1 但不包含在参数 2 中的键值对
        then(difference.entriesOnlyOnLeft()).containsExactly(entry("C", 300));
        // 确认包含在参数 2 但不包含在参数 1 中的键值对
        then(difference.entriesOnlyOnRight()).containsExactly(entry("D", 400));
    }

    /**
     * 将 {@link Properties} 对象转为 {@link ImmutableMap} 对象
     *
     * <p>
     * {@link Maps#fromProperties(Properties)} 方法将一个 {@link Properties} 对象转为 {@link ImmutableMap} 对象,
     * 即一个不变 {@code Map} 对象, Key 和 Value 均为 {@link String} 类型
     * </p>
     */
    @Test
    void fromProperties_shouldCreateMapFromPropertiesObject() {
        // 创建一个 Properties 对象, 存入一些项目
        var props = new Properties();
        props.put("user.name", "Alvin");
        props.put("user.password", "ok~123");
        props.put("user.email", "alvin.qh@outlook.com");

        // 将 Properties 对象转为 Map 对象
        var map = Maps.fromProperties(props);
        // 确认返回的 Map 对象不可修改
        then(map).isUnmodifiable();

        // 确认返回的 Map 包含预期的键值对
        then(map.get("user.name")).isEqualTo("Alvin");
        then(map.get("user.password")).isEqualTo("ok~123");
        then(map.get("user.email")).isEqualTo("alvin.qh@outlook.com");
    }
}
