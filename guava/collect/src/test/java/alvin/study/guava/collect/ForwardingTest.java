package alvin.study.guava.collect;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.Test;

import alvin.study.guava.collect.IdMap.IdMapEntry;

/**
 * 为了简化创建符合 Java 标准的各种集合, Guava 提供了一系列代理类超类
 *
 * <p>
 * 这些类已经通过一个代理集合对象实现了大部分集合方法, 只需要覆盖所需的部分方法即可, 参考 {@link StringList} 类型
 * </p>
 *
 * <p>
 * Guava 提供的代理超类主要包括:
 * <ul>
 * <li>
 * {@link com.google.common.collect.ForwardingCollection ForwardingCollection} 代理
 * {@link java.util.Collection Collection} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingList ForwardingList} 代理 {@link java.util.List List} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingSet ForwardingSet} 代理 {@link java.util.Set Set} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingSortedSet ForwardingSortedSet} 代理 {@link java.util.SortedSet
 * SortedSet} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingMap ForwardingMap} 代理 {@link java.util.Map Map} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingSortedMap ForwardingSortedMap} 代理 {@link java.util.SortedMap
 * SortedMap} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingConcurrentMap ForwardingConcurrentMap} 代理
 * {@link java.util.concurrent.ConcurrentMap ConcurrentMap} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingMapEntry ForwardingMapEntry} 代理 {@link java.util.Map.Entry
 * Map.Entry} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingQueue ForwardingQueue} 代理 {@link java.util.Queue Queue} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingIterator ForwardingIterator} 代理 {@link java.util.Iterator
 * Iterator} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingListIterator ForwardingListIterator} 代理
 * {@link java.util.ListIterator ListIterator} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingMultiset ForwardingMultiset} 代理
 * {@link com.google.common.collect.Multiset Multiset} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingMultimap ForwardingMultimap} 代理
 * {@link com.google.common.collect.Multimap Multimap} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ForwardingMultiset ForwardingMultiset} 代理
 * {@link com.google.common.collect.Multiset Multiset} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.ListMultimap ListMultimap} 代理 {@link com.google.common.collect.ListMultimap
 * ListMultimap} 类型
 * </li>
 * <li>
 * {@link com.google.common.collect.SetMultimap SetMultimap} 代理
 * {@link com.google.common.collect.ForwardingSetMultimap ForwardingSetMultimap} 类型
 * </li>
 * </ul>
 * </p>
 */
class ForwardingTest {
    /**
     * 测试通过 {@link ForwardingList} 类型代理一个 {@link java.util.ArrayList ArrayList} 类型集合
     *
     * <p>
     * 参考 {@link StringList} 类型, 该类型继承自 {@link ForwardingList}, 且重写了部分必要方法
     * </p>
     */
    @Test
    void stringList_shouldCustomTypeCanDelegateOtherListType() {
        // 实例化一个代理类对象
        var list = new StringList();

        // 测试代理对象的 add 方法
        list.add("A");
        then(list).hasSize(1).containsExactly("A");

        // 确认代理对象的 add 方法无法添加空字符串或 null 对象
        thenThrownBy(() -> list.add("")).isInstanceOf(IllegalArgumentException.class);
        thenThrownBy(() -> list.add(null)).isInstanceOf(IllegalArgumentException.class);

        // 测试代理对象的 addAll 方法
        list.addAll(ImmutableList.of("B", "C"));
        then(list).hasSize(3).containsExactly("A", "B", "C");

        // 确认代理对象的 addAll 方法无法添加包含空字符串或 null 元素的集合对象
        thenThrownBy(() -> list.addAll(ImmutableList.of("", ""))).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * 测试通过 {@link ForwardingMap} 类型代理一个 {@link java.util.LinkedHashMap LinkedHashMap} 类型集合
     *
     * <p>
     * 参考 {@link IdMap} 类型, 该类型继承自 {@link ForwardingMap} 类型
     * </p>
     */
    @Test
    void idMap_shouldCustomTypeCanDelegateOtherMapType() {
        // 实例化一个代理类对象
        var map = new IdMap();

        // 测试代理对象的 put 方法
        map.put(1L, "A");
        then(map).hasSize(1).containsExactly(entry(1L, "A"));

        // 确认代理对象的 put 方法无法将空字符串或 null 对象作为 value 参数
        thenThrownBy(() -> map.put(2L, "")).isInstanceOf(IllegalArgumentException.class);
        thenThrownBy(() -> map.put(2L, null)).isInstanceOf(IllegalArgumentException.class);

        // 测试代理对象的 putAll 方法
        map.putAll(ImmutableMap.of(2L, "B", 3L, "C"));
        then(map).hasSize(3).containsExactly(
            entry(1L, "A"),
            entry(2L, "B"),
            entry(3L, "C"));

        // 确认代理对象的 putAll 方法无法添加包含空字符串或 null 元素为 value 的 Map 集合对象
        thenThrownBy(() -> map.putAll(ImmutableMap.of(4L, "", 5L, "")))
                .isInstanceOf(IllegalArgumentException.class);

        // 确认通过 entrySet 方法获取的为 IdMap.IdMapEntry 类型的 Set 集合
        then(map.entrySet()).containsExactly(
            IdMapEntry.entry(1L, "A"),
            IdMapEntry.entry(2L, "B"),
            IdMapEntry.entry(3L, "C"))
                .map(e -> (Object) e.getClass())
                .containsExactly(IdMapEntry.class, IdMapEntry.class, IdMapEntry.class);
    }
}
