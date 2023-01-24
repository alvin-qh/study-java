package alvin.study;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * 代理 {@link ArrayList} 类型的类型
 *
 * <p>
 * 参考 {@link CollectionTest.ExtCollectionTest#forwarding_shouldCustomTypeCanDelegateOtherCollectionType()
 * forwarding_shouldCustomTypeCanDelegateOtherCollectionType} 方法
 * </p>
 */
class CustomList<T> extends ForwardingList<T> {
    // 设置被代理的对象
    private final List<T> delegatedList = new ArrayList<>();

    /**
     * 获取被代理的对象
     *
     * @return 被代理的 {@link List} 类型对象
     */
    @Override
    protected List<T> delegate() {
        return delegatedList;
    }

    /**
     * 重写 {@link List#add(Object)} 方法
     *
     * @param element 要添加的元素
     * @return 如果添加成功则返回 {@code true}, 反之返回 {@code false}
     * @throws NullPointerException 如果 {@code element} 参数为 {@code null} 时, 抛出该异常
     */
    @Override
    public boolean add(T element) {
        return this.delegatedList.add(
            Preconditions.checkNotNull(element, "element argument cannot be null"));
    }

    /**
     * 通过 {@link ForwardingIterator} 接口代理 {@link #delegatedList} 对象的 {@link List#iterator()} 方法返回值
     *
     * @return {@link ForwardingIterator} 类型代理对象
     */
    @Override
    public Iterator<T> iterator() {
        // 通过匿名类创建对象并设置被代理对象
        return new ForwardingIterator<T>() {
            // 设置要代理的迭代器对象
            private final Iterator<T> delegatedIterator = CustomList.this.delegatedList.iterator();

            /**
             * 获取被代理的迭代器对象
             *
             * @return 被代理的迭代器对象, 即 {@link #delegatedIterator} 字段的值
             */
            @Override
            protected Iterator<T> delegate() {
                return delegatedIterator;
            }
        };
    }

    /**
     * 通过 {@link ForwardingListIterator} 接口代理 {@link #delegatedList} 对象的 {@link List#listIterator()} 方法返回值
     *
     * @return {@link ForwardingListIterator} 类型代理对象
     */
    public ListIterator<T> listIterator() {
        // 通过匿名类创建对象并设置被代理对象
        return new ForwardingListIterator<T>() {
            // 设置要代理的迭代器对象
            private final ListIterator<T> delegatedIterator = CustomList.this.delegatedList.listIterator();

            /**
             * 获取代理的迭代器对象
             *
             * @return 被代理的迭代器对象, 即 {@link #delegatedIterator} 字段的值
             */
            @Override
            protected ListIterator<T> delegate() {
                return delegatedIterator;
            }
        };
    }
}

/**
 * 演示 Guava 集合工具类和增加的集合类
 */
@SuppressWarnings("deprecation")
class CollectionTest {
    @Nested
    class ExtCollectionTest {
        /**
         * 为了简化创建符合 Java 标准的各种集合, Guava 提供了一系列代理类超类
         *
         * <p>
         * 这些类已经通过一个代理集合对象实现了大部分集合方法, 只需要覆盖所需的部分方法即可, 参考 {@link CustomList} 类型
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
        @Test
        void forwarding_shouldCustomTypeCanDelegateOtherCollectionType() {
            // 实例化一个代理类对象
            var list = new CustomList<String>();

            // 调用代理对象的 add 方法
            list.add("A");
            then(list).hasSize(1).containsExactly("A");

            // 确认代理对象的 add 方法无法添加 null 对象
            thenThrownBy(() -> list.add(null)).isInstanceOf(NullPointerException.class);
        }

        /**
         * 支持 {@code peek} 操作的迭代器类型
         *
         * <p>
         * 所谓 {@code peek} 操作即获取迭代器的下一个元素值, 但不进行迭代 (即不移动迭代器的元素指针)
         * </p>
         *
         * <p>
         * 下面的例子中, 通过遍历迭代器, 且跳过重复元素, 将一个集合的元素不重复的复制到另一个集合对象中
         * </p>
         */
        @Test
        void peekingIterator_shouldWrapIteratorAsPeekingIterator() {
            // 源集合对象, 元素必须有序
            var src = ImmutableList.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
            // 目标集合对象
            var dst = Lists.newArrayList();

            // 将迭代器包装为 PeekingIterator 对象
            var srcIter = Iterators.peekingIterator(src.iterator());
            while (srcIter.hasNext()) {
                // 获取迭代器的当前元素
                var current = srcIter.next();

                // 如果当前元素和下一个元素相等, 则继续循环
                while (srcIter.hasNext() && srcIter.peek().equals(current)) {
                    // 跳过重复元素
                    srcIter.next();
                }

                // 将当前元素添加到目标集合
                dst.add(current);
            }

            // 确认目标集合中不包含重复项
            then(dst).containsExactly(1, 2, 3, 4, 5);
        }

        /**
         * 测试对迭代器类型进行扩展
         *
         * <p>
         * {@link AbstractIterator} 类型可以协助扩展 {@link java.util.Iterator Iterator} 类型, 无需重写
         * {@link java.util.Iterator Iterator} 类型的全部方法, 只需重写一个 {@link AbstractIterator#computeNext()} 方法即可
         * </p>
         *
         * <p>
         * {@link AbstractIterator#next()} 方法和 {@link AbstractIterator#hasNext()} 方法均会调用
         * {@link AbstractIterator#computeNext()} 方法, 如果迭代器有下一个元素, 则 {@link AbstractIterator#computeNext()} 方法
         * 返回元素值, 否则返回 {@link AbstractIterator#endOfData()} 结果表示迭代完成
         * </p>
         *
         * <p>
         * 本例演示了通过代理 {@link java.util.Iterator} 类型对象, 产生一个会过滤掉偶数元素的迭代器对象
         * </p>
         */
        @Test
        void abstractIterator_shouldExtendIteratorType() {
            // 源集合对象, 元素必须有序
            var src = ImmutableList.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);

            // 通过匿名类创建一个 AbstractIterator 类型迭代器对象
            var iter = new AbstractIterator<Integer>() {
                // 被代理的迭代器对象
                private final Iterator<Integer> delegate = src.iterator();

                /**
                 * 计算当前迭代器对象的下一个元素值
                 *
                 * @return 如果下一个元素存在, 则返回元素值, 且迭代器向前迭代一次, 否则返回 {@link #endOfData()} 结果表示迭代结束
                 */
                @Override
                protected Integer computeNext() {
                    if (!delegate.hasNext()) {
                        // 如果没有下一个元素, 则迭代结束
                        return endOfData();
                    }

                    // 获取下一个元素
                    var elem = delegate.next();
                    while (elem % 2 == 0) {
                        // 如果元素是偶数, 则继续迭代, 直到得到一个奇数或者迭代结束
                        elem = delegate.hasNext() ? delegate.next() : endOfData();
                    }
                    return elem;
                }
            };

            // 通过迭代器对象创建一个集合对象
            var dist = ImmutableList.copyOf(iter);
            // 确认集合对象的元素不包括偶数
            then(dist).containsExactly(1, 1, 3, 3, 5, 5);
        }

        /**
         * {@link com.google.common.collect.Multiset Multiset} 接口提供了一种可以存储重复元素的 {@link java.util.Set Set} 集合
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
         * {@link com.google.common.collect.Multimap Multimap} 集合是一个类似 {@link java.util.Map Map} 的 Key/Values 集合,
         * 和 {@code Map} 不同的是, {@code Multimap} 的 Value 永远是一个集合 ({@code List} 或 {@code Set}), 即一个 Key 将对应
         * 一组值
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
            var mulMap = MultimapBuilder.hashKeys(3).arrayListValues().<String, Integer>build();
            mulMap.put("A", 1);
            mulMap.put("A", 1);
            mulMap.put("A", 2);
            mulMap.put("B", 1);
            mulMap.put("B", 2);

            then(mulMap.get("A")).containsExactly(1, 1, 2);
            then(mulMap.get("B")).containsExactly(1, 2);

            mulMap.get("C").add(1);
            then(mulMap.get("C")).containsExactly(1);

            mulMap.get("C").addAll(ImmutableList.of(1, 2));
            then(mulMap.get("C")).containsExactly(1, 1, 2);

            mulMap.putAll("C", ImmutableList.of(10, 20, 30));
            then(mulMap.get("C")).containsExactly(1, 1, 2, 10, 20, 30);

            mulMap.remove("C", 1);
            then(mulMap.get("C")).containsExactly(1, 2, 10, 20, 30);

            mulMap.removeAll("C");
            then(mulMap.get("C")).containsExactlyElementsOf(ImmutableList.of());

            mulMap.replaceValues("B", ImmutableList.of(10, 20, 30));
            then(mulMap.get("B")).containsExactlyElementsOf(ImmutableList.of(10, 20, 30));

            var map = mulMap.asMap();
            then(map).contains(
                entry("A", ImmutableList.of(1, 1, 2)),
                entry("B", ImmutableList.of(10, 20, 30)));
        }
    }

    /**
     * 演示不变集合的使用
     *
     * <p>
     * 不变集合即只读集合, 无法改变集合内的元素以及集合的长度. 不变集和可以一次性分配连续内存空间进行存储, 读取效率更高, 内存结构更好, 且
     * 无需担心线程安全问题, 在并发场景下具有更好的执行效率
     * </p>
     */
    @Nested
    class ImmutableCollectionTest {
        /**
         * 通过指定元素构建不变集合
         */
        @Test
        void of_shouldCreateImmutableListByElements() {
            var list = ImmutableList.of(1, 2, 3, 4);
            then(list).containsExactly(1, 2, 3, 4);
            thenThrownBy(() -> list.add(5)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void copyOf_shouldCreateImmutableListFromOtherCollection() {
            var list = ImmutableList.copyOf(Sets.newHashSet(1, 2, 3, 4));
            then(list).containsExactly(1, 2, 3, 4);
        }

        @Test
        void copyOf_shouldCreateSortedImmutableListFromOtherCollection() {
            var list = ImmutableList.sortedCopyOf(
                Ordering.natural().reversed(),
                Sets.newHashSet(1, 2, 3, 4));

            then(list).containsExactly(4, 3, 2, 1);
        }

        @Test
        void builder_shouldCreateImmutableListByBuilder() {
            var list = ImmutableList.builder()
                    .add(1, 2, 3)
                    .addAll(Sets.newHashSet(4, 5, 6))
                    .build();

            then(list).containsExactly(1, 2, 3, 4, 5, 6);
        }

        @Test
        void builder_shouldCreateImmutableListByBuilderWithCapacity() {
            var list = ImmutableList.builderWithExpectedSize(5)
                    .add(1, 2, 3)
                    .addAll(Sets.newHashSet(4, 5))
                    .build();
            then(list).containsExactly(1, 2, 3, 4, 5);

            list = ImmutableList.builderWithExpectedSize(5)
                    .add(1, 2, 3)
                    .addAll(Sets.newHashSet(4, 5, 6))
                    .build();
            then(list).containsExactly(1, 2, 3, 4, 5, 6);
        }

        @Test
        void map_shouldCreateImmutableMap() {
            var map = ImmutableMap.of("A", 100, "B", 200);
            then(map).contains(entry("A", 100), entry("B", 200));

            map = ImmutableMap.ofEntries(Map.entry("A", 100), Map.entry("B", 200));
            then(map).contains(entry("A", 100), entry("B", 200));

            map = ImmutableMap.<String, Integer>builder()
                    .put("A", 100)
                    .put(Map.entry("B", 200))
                    .putAll(Maps.newHashMap("C", 300))
                    .build();
            then(map).contains(entry("A", 100), entry("B", 200), entry("C", 300));

            map = ImmutableMap.<String, Integer>builder()
                    .put("A", 100)
                    .put(Map.entry("B", 200))
                    .putAll(Maps.newHashMap("C", 300))
                    .put("B", 400)
                    .buildKeepingLast();
            then(map).contains(entry("A", 100), entry("B", 400), entry("C", 300));

            thenThrownBy(() -> ImmutableMap.<String, Integer>builder()
                    .put("A", 100)
                    .put(Map.entry("B", 200))
                    .putAll(Maps.newHashMap("C", 300))
                    .put("B", 400)
                    .buildOrThrow())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("Multiple entries with same key: B=400 and B=200");
        }
    }
}
