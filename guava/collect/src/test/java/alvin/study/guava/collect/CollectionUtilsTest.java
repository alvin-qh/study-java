package alvin.study.guava.collect;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * {@link Collections2} 类型是 Guava 对 {@link java.util.Collections} 工具类的补充
 *
 * <p>
 * 该类的作用是辅助集合操作, 为集合操作提供便利
 * </p>
 *
 * <p>
 * 如果使用的是 JDK 8 以上的版本, Guava 推荐使用 Java 本身提供的 Stream 库, {@link Collections2} 类的方法不会过期停用,
 * 但已经不推荐在 JDK 8 及以上版本使用
 * </p>
 */
class CollectionUtilsTest {
    /**
     * 对集合进行过滤, 返回保存过滤结果的集合对象
     *
     * <p>
     * {@link Collections2#filter(java.util.Collection, com.google.common.base.Predicate)
     * Collections2.filter(Collection, Predicate)} 方法返回一个 {@code Collections2.FilteredCollection} 类型对象,
     * 该对象代理了原集合对象, 在获取元素时根据过滤条件返回符合条件的结果, 所以如果原集合发生变化, 可能会导致过滤结果也发生变化
     * </p>
     */
    @Test
    void filter_shouldFindElementsMatchedConditionInCollection() {
        var coll = Lists.newArrayList(1, 2, 3, 4, 5, 6);

        // 对集合进行过滤, 获取过滤结果
        var filteredList = Collections2.filter(coll, n -> n % 2 == 0);
        // 确认过滤结果符合过滤条件
        then(filteredList).containsExactly(2, 4, 6);

        // 在原集合中添加元素
        coll.addAll(ImmutableList.of(7, 8));
        // 确认原集合的变化会导致过滤结果发生变化
        then(filteredList).containsExactly(2, 4, 6, 8);
    }

    /**
     * 获取一个集合所有元素的全部排列组合
     *
     * <p>
     * {@link Collections2#permutations(java.util.Collection) Collections2.permutations(Collection)} 方法返回一个
     * {@code Collections2.PermutationCollection} 类型对象,
     * 该对象代理了原集合对象, 在获取元素时根据过滤条件返回符合条件的结果, 所以如果原集合发生变化, 可能会导致过滤结果也发生变化
     * </p>
     *
     * <p>
     * 计算全排列的算法如下:
     * <ol>
     * <li>
     * {@link Collections2#permutations(java.util.Collection) Collections2.permutations(Collection)} 方法返回
     * {@code Collections2.PermutationCollection} 类型对象, 该对象通过 {@code Collections2.PermutationIterator}
     * 类型迭代器来产生集合元素的各种排列组合, 迭代器每次迭代返回一个 {@code List<T>} 集合, 表示一种排列组合
     * </li>
     * <li>
     * {@code Collections2.PermutationIterator} 迭代器每次迭代, 交换集合中两个位置位置的元素, 以产生一种排列组合,
     * 通过一组标记记录交换的下标情况, 最终产生所有排列组合
     * </li>
     * </ol>
     * </p>
     */
    @Test
    void permutations_shouldFindElementsMatchedConditionInCollection() {
        var coll = Lists.newArrayList(1, 2, 3);

        // 对集合进行过滤, 获取过滤结果
        var permutationList = Collections2.permutations(coll);

        // 确认过滤结果符合过滤条件
        then(permutationList).containsExactlyInAnyOrder(
                ImmutableList.of(1, 2, 3),
                ImmutableList.of(1, 3, 2),
                ImmutableList.of(2, 1, 3),
                ImmutableList.of(2, 3, 1),
                ImmutableList.of(3, 1, 2),
                ImmutableList.of(3, 2, 1));
    }

    /**
     * 获取一个集合所有元素的全部排列组合
     *
     * <p>
     * {@link Collections2#orderedPermutations(Iterable, java.util.Comparator)
     * Collections2.orderedPermutations(Iterable, Comparator)} 方法与
     * {@link Collections2#permutations(java.util.Collection) Collections2.permutations(Collection)} 方法基本类似,
     * 区别在于:
     * <ul>
     * <li>
     * 前者通过 {@link Iterable} 对象传递集合, 即所有可迭代对象都可以进行处理
     * </li>
     * <li>
     * 前者通过 {@link Comparator} 比较器来控制产生的排列组合结果顺序
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void orderedPermutations_shouldFindElementsMatchedConditionInIterables() {
        var coll = Lists.newArrayList(1, 2, 3);

        // 对集合进行过滤, 获取过滤结果
        var permutationList = Collections2.orderedPermutations(coll, Ordering.natural().reversed());

        // 确认过滤结果符合过滤条件
        then(permutationList).containsExactlyInAnyOrder(
                ImmutableList.of(1, 2, 3),
                ImmutableList.of(1, 3, 2),
                ImmutableList.of(2, 1, 3),
                ImmutableList.of(2, 3, 1),
                ImmutableList.of(3, 1, 2),
                ImmutableList.of(3, 2, 1));
    }

    /**
     * 将集合元素进行转换, 得到一个新集合对象
     *
     * <p>
     * {@link Collections2#transform(java.util.Collection, com.google.common.base.Function)
     * Collections2.transform(Collection, Function)} 方法通过一个转换函数对集合中的元素进行转换, 返回包含转换后元素的集合对象
     * </p>
     *
     * <p>
     * 该方法返回结果为 {@code Collections2.TransformedCollection} 类型, 是一个原集合对象的代理对象,
     * 在获取元素时才会对原集合元素进行转换处理, 所以对于原集合的修改, 可能会引起结果集合的变化
     * </p>
     */
    @Test
    void transform_shouldTransformElementsToOther() {
        var coll = Lists.newArrayList(1, 2, 3);

        // 对集合进行转换, 得到包含转换后元素的新集合对象
        var transformedList = Collections2.transform(coll, n -> String.format("0%d", n));
        // 确认全部元素已被转换
        then(transformedList).containsExactly("01", "02", "03");

        // 在原集合中添加新元素
        coll.add(4);
        // 确认转换结果集合也发生的改变, 包含被转换后的新元素值
        then(transformedList).containsExactly("01", "02", "03", "04");
    }
}
