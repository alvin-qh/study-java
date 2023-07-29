package alvin.study.guava.collect;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MultimapBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 演示可以存储多个值的 {@link java.util.Set Set} 和 {@link com.google.common.collect.Multimap Multimap} 集合
 *
 * <p>
 * 对于 {@link com.google.common.collect.Multiset Multiset} 集合来说, 它解决了 {@link java.util.Set Set} 集合无法存储重复值
 * 的问题, 且具备 {@link java.util.Set Set} 集合的一切优点
 * </p>
 *
 * <p>
 * 对于 {@link com.google.common.collect.Multimap Multimap} 集合来说, 它解决了 {@link java.util.Map Map} 集合每个 Key 只能对
 * 应一个 Value 的情况, 可以另一个 Key 对应一组 Value
 * </p>
 *
 * <p>
 * 注意, {@link com.google.common.collect.Multimap Multimap} 并未实现 {@link java.util.Map Map} 接口, 但可以通过
 * {@link com.google.common.collect.Multimap#asMap() Multimap.asMap()} 方法转换为 {@link java.util.Map Map} 对象
 * </p>
 */
class MultiCollectionTest {
    /**
     * 测试 {@link com.google.common.collect.Multiset Multiset} 接口类型
     *
     * <p>
     * {@link com.google.common.collect.Multiset Multiset} 继承自 {@link java.util.Collection} 接口, 具备其定义的所有行为
     * </p>
     *
     * <p>
     * {@link com.google.common.collect.Multiset Multiset} 内部通过 {@link com.google.common.collect.Multiset.Entry
     * Multiset.Entry} 对象存储元素, {@code Multiset.Entry} 对象存储元素以及元素在集合中出现的次数, 所以本质上, {@code Multiset}
     * 集合仍是存储不重复元素 ({@link com.google.common.collect.Multiset.Entry#getElement()
     * Multiset.Entry.getElement()}), 只是额外为每个元素记录了一个数量值
     * ({@link com.google.common.collect.Multiset.Entry#getCount() Multiset.Entry.getCount()})
     * </p>
     *
     * <p>
     * 相比 {@link java.util.Collection} 接口, {@link com.google.common.collect.Multiset Multiset} 接口增加了如下变化:
     * <ul>
     * <li>
     * 迭代遍历时, 会列出所有数量大于 {@code 0} 的元素, 且按照元素数量列举元素
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#count(Object) Multiset.count(T)} 方法, 可以获得指定元素的数量
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#elementSet() Multiset.elementSet()} 方法, 获取包含所有元素的
     * {@link java.util.Set Set} 集合
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#entrySet() Multiset.entrySet()} 方法, 获取包含所有
     * {@link com.google.common.collect.Multiset.Entry Multiset.Entry} 对象的集合
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#add(Object, int) Multiset.add(T, int)} 方法, 对指定元素增加其
     * 数量
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#remove(Object, int) Multiset.remove(T, int)} 方法, 对指定元素
     * 减少其数量
     * </li>
     * <li>
     * 增加了 {@link com.google.common.collect.Multiset#setCount(Object, int) Multiset.setCount(T, int)} 方法, 对指定
     * 元素设置其数量; 另一个方法 {@link com.google.common.collect.Multiset#setCount(Object, int, int)
     * Multiset.setCount(T, int, int)} 则可以对现有数量进行比较, 如果满足, 则设置成期望的数量
     * </li>
     * <li>
     * 修改了 {@link com.google.common.collect.Multiset#add(Object) Multiset.add(T)} 方法, 表示将元素的数量增加 {@code 1}
     * </li>
     * <li>
     * 修改了 {@link com.google.common.collect.Multiset#remove(Object) Multiset.remove(T)} 方法, 表示将元素的数量减去
     * {@code 1}
     * </li>
     * <li>
     * 修改了 {@link com.google.common.collect.Multiset#size() Multiset.size()} 方法, 每个元素数量的总和
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link com.google.common.collect.Multiset Multiset} 接口的实现类包括:
     * <ul>
     * <li>
     * {@link com.google.common.collect.HashMultiset HashMultiset}, 基于 {@link java.util.HashMap HashMap} 扩展的类型
     * </li>
     * <li>
     * {@link com.google.common.collect.LinkedHashMultiset LinkedHashMultiset}, 基于
     * {@link java.util.LinkedHashMap LinkedHashMap} 扩展的类型
     * </li>
     * <li>
     * {@link com.google.common.collect.TreeMultiset TreeMultiset}, 基于 {@link java.util.TreeMap TreeMap} 扩展的类型
     * </li>
     * <li>
     * {@link com.google.common.collect.ConcurrentHashMultiset ConcurrentHashMultiset}, 基于
     * {@link java.util.concurrent.ConcurrentHashMap} 扩展的类型
     * </li>
     * <li>
     * {@link com.google.common.collect.ImmutableMultiset ImmutableMultiset}, 基于
     * {@link com.google.common.collect.ImmutableMap ImmutableMap} 扩展的类型
     * </li>
     * <li>
     * {@link com.google.common.collect.ImmutableSortedMultiset ImmutableSortedMultiset}, 基于
     * {@link com.google.common.collect.ImmutableSortedMap ImmutableSortedMap} 扩展的类型
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void multiset_shouldStoreElementsWithCount() {
        // 创建一个 Multiset 类型对象, 设置预期的元素数量
        var mulSet = HashMultiset.<String>create(3);

        // 添加 6 个元素, 其中共 3 个元素不重复
        mulSet.add("A");
        // 添加重复元素会让元素数量加 1
        mulSet.add("A");
        mulSet.add("B");
        mulSet.add("B");
        mulSet.add("B");
        mulSet.add("C");

        // 确认集合长度为 6
        then(mulSet).hasSize(6);
        // 确认集合内容
        then(mulSet).containsExactly("A", "A", "B", "B", "B", "C");

        // 确认各不重复元素的数量
        then(mulSet.count("A")).isEqualTo(2);
        then(mulSet.count("B")).isEqualTo(3);
        then(mulSet.count("C")).isEqualTo(1);

        // 确认不存在的元素数量为 0
        then(mulSet.count("D")).isEqualTo(0);

        // 确认转换得到的 Set 对象包含全部不重复元素
        var set = mulSet.elementSet();
        then(set).containsExactly("A", "B", "C");

        // 获取集合中存储的 Entry 对象, 并确认每个 Entry 对象符合预期
        var entrySet = mulSet.entrySet();
        then(entrySet).extracting("element", "count")
                .containsExactly(tuple("A", 2), tuple("B", 3), tuple("C", 1));

        // 为指定元素增加数量 1
        mulSet.add("C", 2);
        // 确认元素数量符合预期
        then(mulSet.count("C")).isEqualTo(3);
        // 增加元素数量后, 集合遍历的结果会发生变化, 指定元素的数量会增加
        then(mulSet).containsExactly("A", "A", "B", "B", "B", "C", "C", "C");

        // 为指定元素减去数量 1
        mulSet.remove("A");
        // 确认元素数量符合预期
        then(mulSet.count("A")).isEqualTo(1);
        // 确认减少元素数量后, 集合遍历的结果会发生变化, 指定元素的数量会减少
        then(mulSet).containsExactly("A", "B", "B", "B", "C", "C", "C");

        // 为指定元素减去指定数量
        mulSet.remove("B", 2);
        // 确认元素数量符合预期
        then(mulSet.count("B")).isEqualTo(1);
        // 增加元素数量后, 集合遍历的结果会发生变化, 指定元素的数量会增加
        then(mulSet).containsExactly("A", "B", "C", "C", "C");

        // 为指定元素设置数量
        mulSet.setCount("B", 2);
        // 确认元素数量符合预期
        then(mulSet.count("B")).isEqualTo(2);
        // 设置元素数量后, 集合遍历的结果会发生变化, 确认集合元素
        then(mulSet).containsExactly("A", "B", "B", "C", "C", "C");
    }

    /**
     * 测试 {@link com.google.common.collect.Multimap Multimap} 接口类型
     *
     * <p>
     * {@link com.google.common.collect.Multimap Multimap} 的内部是通过一个 {@link java.util.Map Map} 集合来存储元素,
     * 根据 Key 存储的方式不同, 可以通过 {@link java.util.HashMap HashMap}, {@link java.util.LinkedHashMap
     * LinkedHashMap} 以及 {@link java.util.TreeMap TreeMap} 进行存储
     * </p>
     *
     * <p>
     * 根据 {@link com.google.common.collect.Multimap Multimap} 的存储 Values 集合的不同, 有如下接口实现:
     * <ul>
     * <li>
     * {@link com.google.common.collect.ListMultimap ListMultimap}, 以 {@link java.util.List List} 集合存储 Values;
     * 包含两个子类型 {@link com.google.common.collect.ArrayListMultimap ArrayListMultimap} 以及
     * {@link com.google.common.collect.LinkedListMultimap LinkedListMultimap} 类型
     * </li>
     * <li>
     * {@link com.google.common.collect.SetMultimap SetMultimap}, 以 {@link java.util.Set Set} 集合存储 Values;
     * 包含四个子类型 {@link com.google.common.collect.HashMultimap HashMultimap},
     * {@link com.google.common.collect.LinkedHashMultimap LinkedHashMultimap},
     * {@link com.google.common.collect.TreeMultimap TreeMultimap} 以及
     * {@link com.google.common.collect.SortedSetMultimap SortedSetMultimap} 类型
     * </li>
     * <li>
     * {@link com.google.common.collect.ImmutableMultimap ImmutableMultimap}, 表示一个不可变集合对象, 包含两个子类型:
     * {@link com.google.common.collect.ImmutableListMultimap ImmutableListMultimap} 以及
     * {@link com.google.common.collect.ImmutableSetMultimap ImmutableMultimap}
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void multimap_shouldStoreKeyWithCollectionValue() {
        // 构建一个 Multimap 对象, 其中:
        //   Key 的存储方式为 Set
        //   Value 的存储方式为 List
        var mulMap = MultimapBuilder.hashKeys().arrayListValues().<String, Integer>build();

        // 在集合中添加 5 个键值对, 其中前三个和后两个键各自重复, 所以实际存储的键为 2 个, 前一个对应 3 个 Value, 后一个对应 2 个 Value
        mulMap.put("A", 1);
        mulMap.put("A", 1);
        mulMap.put("A", 2);
        mulMap.put("B", 1);
        mulMap.put("B", 2);

        // 获取集合的长度, 长度为 5, 即相当于展开了每个键值对, 包括键重复的情况
        then(mulMap.size()).isEqualTo(5);

        // 通过键获取对应的 Value 集合
        then(mulMap.get("A")).containsExactly(1, 1, 2);
        then(mulMap.get("B")).containsExactly(1, 2);

        // 获取对应键的集合, 并添加新值
        // 这里值为 "C" 的键并不存在, 但可以为其添加值, 添加时会创建值为 "C" 的键
        mulMap.get("C").add(1);
        then(mulMap.get("C")).containsExactly(1);

        // 获取对应键的集合, 并添加一组值
        mulMap.get("C").addAll(ImmutableList.of(1, 2));
        then(mulMap.get("C")).containsExactly(1, 1, 2);

        // putAll 方法相当于 get(...).putAll(...) 方法的简化写法
        mulMap.putAll("C", ImmutableList.of(10, 20, 30));
        then(mulMap.get("C")).containsExactly(1, 1, 2, 10, 20, 30);

        // 删除指定键对应集合中特定的值
        mulMap.remove("C", 1);
        then(mulMap.get("C")).containsExactly(1, 2, 10, 20, 30);

        // 删除指定键对应的集合
        mulMap.removeAll("C");
        then(mulMap.get("C")).containsExactlyElementsOf(Collections.emptyList());

        mulMap.replaceValues("B", ImmutableList.of(10, 20, 30));
        then(mulMap.get("B")).containsExactlyElementsOf(ImmutableList.of(10, 20, 30));

        // 将 Multimap 对象转为 Map 对象, 该 Map 对象的 Value 为 List 类型
        var map = mulMap.asMap();
        then(map).contains(
                entry("A", ImmutableList.of(1, 1, 2)),
                entry("B", ImmutableList.of(10, 20, 30)));
    }
}
