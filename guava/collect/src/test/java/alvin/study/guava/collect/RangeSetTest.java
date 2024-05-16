package alvin.study.guava.collect;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * {@link RangeSet} 相当于是多个区间的组合
 *
 * <p>
 * {@link RangeSet} 接口表示一个 {@link Range} 对象的集合, 由零或多个非空的, 不连续的 {@link Range} 对象组成
 * </p>
 *
 * <p>
 * 可以向 {@link RangeSet} 中添加 {@link Range} 对象, 如果新添加的对象和集合中已有的对象连续, 则会合并这两个对象; 如果新添加的对象为空
 * (即 {@link Range#isEmpty()} 为 {@code true}), 则忽略该对象; 如果添加的对象和集合中所有对象都不连续, 则添加该对象
 * </p>
 *
 * <p>
 * {@link RangeSet} 集合可以把其包含的多个区间作为一个整体, 和另一个区间 (或 {@link RangeSet} 集合) 进行交
 * ({@code intersects}/{@code intersection}), 并 ({@code add}/{@code addAll}), 差 ({@code remove}/{@code RemoveAll})
 * 集的计算, 以及求自身补 ({@code complement}) 集的计算
 * </p>
 *
 * <p>
 * Guava 中, {@link RangeSet} 接口提供了两个实现类: {@link TreeRangeSet} 以及 {@link ImmutableRangeSet}, 前者基于
 * {@link java.util.TreeMap TreeMap} 实现; 后者为一个不可变的 {@link RangeSet} 类型
 * </p>
 */
class RangeSetTest {
    /**
     * 实例化一个 {@link RangeSet} 集合对象, 并向其中添加若干 {@link Range} 对象
     *
     * <p>
     * {@link TreeRangeSet#create()} 方法用于创建一个空的集合对象
     * </p>
     *
     * <p>
     * {@link TreeRangeSet#add(Range)} 方法将集合和给定区间求并集. 即: 如果是包含关系, 取区间范围较大的; 如果是连接关系,
     * 则连接为一个区间; 如果不是连接关系, 则添加该给定区间
     * </p>
     *
     * <p>
     * 也可以通过 {@link TreeRangeSet#addAll(RangeSet)} 方法进行两个区间集合直接的并集操作
     * </p>
     *
     * @return {@link RangeSet} 集合对象, 内部包含 {@code [1..20), (50..100]} 两部分区间的 {@link Range} 对象
     */
    private static RangeSet<Integer> createRangeSet() {
        // 创建一个 RangeSet 集合对象
        var rangeSet = TreeRangeSet.<Integer>create();

        // 添加 [1..10]
        rangeSet.add(Range.closed(1, 10));
        // 添加 [5..8), 但因为其被包含在 [1..10] 中, 所以被忽略
        rangeSet.add(Range.closedOpen(5, 8));
        // 添加的 Range 为空, 被忽略
        rangeSet.add(Range.openClosed(0, 0));
        // 添加 (10..20), 和 [1..10] 形成连续, 合并为 [1..20)
        rangeSet.add(Range.open(10, 20));
        // 添加 (50..100], 与现有的均不连续
        rangeSet.add(Range.openClosed(50, 100));

        // 所以最终结果中包含两个部分: [1..20) 与 (50..100]
        return rangeSet;
    }

    /**
     * 检查指定的值是否包含在 {@link RangeSet} 定义的区间内
     *
     * <p>
     * {@link RangeSet#contains(Comparable)} 方法表示, 一个给定值, 只要被 {@link RangeSet} 中的任意一个 {@link Range} 对象包含,
     * 则返回 {@code true}
     * </p>
     */
    @Test
    void contains_shouldQueryThatIfContainsSpecifiedValues() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 0 不包含在任何范围内
        then(rangeSet.contains(0)).isFalse();

        // 1 包含在 [1..20) 范围内
        then(rangeSet.contains(1)).isTrue();
        // 10 包含在 [1..20) 范围内
        then(rangeSet.contains(10)).isTrue();
        // 15 包含在 [1..20) 范围内
        then(rangeSet.contains(15)).isTrue();

        // 20 不包含在任何范围内
        then(rangeSet.contains(20)).isFalse();
        // 20 不包含在任何范围内
        then(rangeSet.contains(30)).isFalse();

        // 50 不包含在任何范围内
        then(rangeSet.contains(50)).isFalse();
        // 80 包含在 (50..100] 范围内
        then(rangeSet.contains(80)).isTrue();
        // 100 包含在 (50..100] 范围内
        then(rangeSet.contains(100)).isTrue();
    }

    /**
     * 从一个 {@link RangeSet} 对象中, 获取包含给定值的 {@link Range} 对象
     *
     * <p>
     * 对于一个给定的值, {@link RangeSet#rangeContaining(Comparable)} 方法返回包含该值的 {@link Range} 对象,
     * 如果给定的值不属于任何一个范围, 则返回 {@code null} 值
     * </p>
     *
     * <p>
     * 注意, 返回的 {@link Range} 对象是经过 {@link RangeSet} 计算合并后的对象, 参考: {@link #createRangeSet()} 方法, 本例中的
     * {@link RangeSet} 仅包含 {@code [1..20), (50..100]} 两个 {@link Range} 区间
     * </p>
     */
    @Test
    void rangeContains_shouldQueryRangeThatIfContainsSpecifiedValues() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 0 不包含在任何范围内
        then(rangeSet.rangeContaining(0)).isNull();

        // 5 在 [1..20) 范围内
        then(rangeSet.rangeContaining(5)).isEqualTo(Range.closedOpen(1, 20));

        // 20 不包含在任何范围内
        then(rangeSet.rangeContaining(20)).isNull();

        // 80 包含在 (50..100] 范围内
        then(rangeSet.rangeContaining(80)).isEqualTo(Range.openClosed(50, 100));
    }

    /**
     * 确认 {@link RangeSet} 对象中是否包含指定的区间值
     *
     * <p>
     * {@link RangeSet#encloses(Range)} 方法如果返回 {@code true} 则表示该对象包含参数指定的区间
     * </p>
     *
     * <p>
     * 本例中, {@link RangeSet} 包含内 {@code [1..20), (50..100]} 两个区间
     * </p>
     */
    @Test
    void encloses_shouldQueryThatIfEnclosedOtherRange() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // [1..20) 区间包含 [1..10] 区间
        then(rangeSet.encloses(Range.closed(1, 10))).isTrue();
        // [1..20) 区间包含 [3..6] 区间
        then(rangeSet.encloses(Range.closed(3, 6))).isTrue();
        // [1..20) 区间包含 [5..8] 区间
        then(rangeSet.encloses(Range.closed(5, 8))).isTrue();

        // [1..20) 区间包含 [10..15] 区间
        then(rangeSet.encloses(Range.closed(10, 15))).isTrue();
        // 不包含 [15..20] 区间
        then(rangeSet.encloses(Range.closed(15, 20))).isFalse();
        // [1..20) 区间包含 [15..20) 区间
        then(rangeSet.encloses(Range.closedOpen(15, 20))).isTrue();

        // 不包含 [20..50] 区间
        then(rangeSet.encloses(Range.closed(20, 50))).isFalse();
        // (50..100] 区间包含 [51..100] 区间
        then(rangeSet.encloses(Range.closed(51, 100))).isTrue();
    }

    /**
     * 获取一个包含 {@link RangeSet} 中所有区间的区间
     *
     * <p>
     * {@link RangeSet#span()} 方法返回一个 {@link Range} 对象, 可以包含 {@link RangeSet} 中的任意区间
     * </p>
     *
     * <p>
     * 在本例中, 返回的结果要能恰好包含 {@code [1..20), (50..100]} 这两个区间
     * </p>
     */
    @Test
    void span_shouldGetSpanOfAllRanges() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 获取包含 RangeSet 中所有区间的区间对象
        var spanRange = rangeSet.span();
        // 确认返回的区间为 [1..100], 恰好可以包含包含 [1..20) 和 (50..100] 两个区间
        then(spanRange).isEqualTo(Range.closed(1, 100));
    }

    /**
     * 获取 {@link RangeSet} 中所有区间的补集
     *
     * <p>
     * {@link RangeSet#complement()} 方法返回一个新的 {@link RangeSet} 对象, 其中包含的 {@link Range} 对象恰好是原
     * {@link RangeSet} 集合中范围的补集, 即返回的 {@link RangeSet} 和原始的 {@link RangeSet} 合并的结果为 {@code (-∞..∞)}
     * </p>
     *
     * <p>
     * 在本例中, 返回的结果要包含 {@code [1..20), (50..100]} 这两个区间的补集区间
     * </p>
     */
    @Test
    void complement_shouldGetViewTheComplementOfViewSet() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 获取原 RangeSet 对象的补集集合
        var compRangeSet = rangeSet.complement();

        // 确认补集 RangeSet 中包含 (-∞, 1), [20, 50] 以及 (100, ∞) 这三部分区间, 和 [1..20), (50..100] 形成补集
        then(compRangeSet.asRanges()).containsExactly(
                Range.lessThan(1),
                Range.closed(20, 50),
                Range.greaterThan(100));
    }

    /**
     * 获取 {@link RangeSet} 对象和指定区间的交集结果
     *
     * <p>
     * {@link RangeSet#subRangeSet(Range)} 方法返回其所有区间和所给区间相交的结果, 所有交集组成一个新的 {@link RangeSet} 对象
     * </p>
     *
     * <p>
     * 如果 {@link RangeSet} 对象中不存在能够包含参数 {@link Range} 定义的区间的情况, 则返回 {@code null} 值
     * </p>
     *
     * <p>
     * 在本例中, 要么返回 {@code [1..20), (50..100]} 这两个区间的一个, 要么返回 {@code null} 值
     * </p>
     */
    @Test
    void subRangeSet_shouldGetViewOfSubRanges() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // [1..20), (50..100] 和 [10..20) 的交集为 [10..20)
        var subRangeSet = rangeSet.subRangeSet(Range.closedOpen(10, 20));
        then(subRangeSet.asRanges()).contains(Range.closedOpen(10, 20));

        // [1..20), (50..100] 和 [50..100) 的交集为 [50..100)
        subRangeSet = rangeSet.subRangeSet(Range.closedOpen(50, 100));
        then(subRangeSet.asRanges()).contains(Range.open(50, 100));

        // [1..20), (50..100] 和 (15..60) 的交集为 (15..20), (50, 60)
        subRangeSet = rangeSet.subRangeSet(Range.open(15, 60));
        then(subRangeSet.asRanges()).containsExactly(Range.open(15, 20), Range.open(50, 60));

        // [1..20), (50..100] 和 [30, 40] 不存在交集
        subRangeSet = rangeSet.subRangeSet(Range.closed(30, 40));
        then(subRangeSet.isEmpty()).isTrue();
    }

    /**
     * 判断 {@link RangeSet} 是否和所给区间相交
     *
     * <p>
     * {@link RangeSet#intersects(Range)} 方法返回 {@code true}, 就相当于 {@link RangeSet#subRangeSet(Range)} 返回了非空结果
     * </p>
     */
    @Test
    void intersects_shouldCheckIfIntersectByGivenRange() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // [1..20), (50..100] 和 [10..20) 相交
        then(rangeSet.intersects(Range.closedOpen(10, 20))).isTrue();

        // [1..20), (50..100] 和 [50..100) 相交
        then(rangeSet.intersects(Range.closedOpen(50, 100))).isTrue();

        // [1..20), (50..100] 和 (15..60) 相交
        then(rangeSet.intersects(Range.open(15, 60))).isTrue();

        // [1..20), (50..100] 和 [30, 40] 不相交
        then(rangeSet.intersects(Range.closed(30, 40))).isFalse();
    }

    /**
     * 将 {@link RangeSet} 对象包含的区间组成集合, 按区间定义从小到大排序
     *
     * <p>
     * {@link RangeSet#asRanges()} 返回一个 {@link java.util.Set Set} 集合, 包含所有的区间, 且顺序为区间定义较小的在前
     * </p>
     *
     * <p>
     * 在本例中, 返回的集合中按顺序包含 {@code [1..20), (50..100]} 这两个区间元素
     * </p>
     */
    @Test
    void asRanges_shouldViewRangesAsSetCollection() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 获取对象中包含的区间集合
        var ranges = rangeSet.asRanges();
        then(ranges).containsExactly(
                Range.closedOpen(1, 20),
                Range.openClosed(50, 100));
    }

    /**
     * 将 {@link RangeSet} 对象包含的区间组成集合, 按区间定义从大到小排序
     *
     * <p>
     * {@link RangeSet#asDescendingSetOfRanges()} 返回一个 {@link java.util.Set Set} 集合, 包含所有的区间, 这一点和
     * {@link RangeSet#asRanges()} 类似, 不同的是, {@link RangeSet#asDescendingSetOfRanges()} 返回的结果中, 区间定义较大的在前
     * </p>
     *
     * <p>
     * 在本例中, 返回的集合中按顺序包含 {@code (50..100], [1..20)} 这两个区间元素
     * </p>
     */
    @Test
    void asDescendingSetOfRanges_shouldViewRangesAsSetCollection() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 获取对象中包含的区间集合
        var ranges = rangeSet.asDescendingSetOfRanges();
        then(ranges).containsExactly(
                Range.openClosed(50, 100),
                Range.closedOpen(1, 20));
    }

    /**
     * 对 {@link RangeSet} 集合和给定区间求差集
     *
     * <p>
     * {@link RangeSet#remove(Range)} 方法将区间集合和所给区间求差集. 即: 如果区间为包含关系, 则拆分区间, 去除相差的部分;
     * 如果区间不存在关系, 则不进行操作
     * </p>
     *
     * <p>
     * 在本例中, 求区间 {@code [1..20), (50..100]} 和 {@code [5..8), (80..90)} 的差集, 结果为 {@code 4} 部分, 分别为:
     * {@code [1..5), [8..20), (50..80], [80..100]}
     * </p>
     */
    @Test
    void remove_shouldComputeDifferenceRange() {
        // 创建集合对象
        var rangeSet = createRangeSet();

        // 从中删除指定区间
        rangeSet.remove(Range.closedOpen(5, 8));
        rangeSet.remove(Range.open(80, 90));

        // 确认删除区间后, 原区间被拆分的结果
        then(rangeSet.asRanges()).containsExactly(
                Range.closedOpen(1, 5),
                Range.closedOpen(8, 20),
                Range.openClosed(50, 80),
                Range.closed(90, 100));
    }

    /**
     * 将 {@link RangeSet} 中的区间定义清空
     *
     * <p>
     * {@link RangeSet#clear()} 将对象中存储的区间情况, 另 {@link RangeSet#isEmpty()} 返回 {@code true}
     * </p>
     *
     * <p>
     * 删除操作会导致将原本一个区间分割为两个区间, 且这两个区间不在连接
     * </p>
     *
     * <p>
     * 在本例中, 从原区间 {@code [1..20), (50..100]} 中删除 {@code [5..8), (80..90)} 两个区间, 将原区间分割为 {@code 4} 部分,
     * 分别为: {@code [1..5), [8..20), (50..80], [80..100]}
     * </p>
     */
    @Test
    void clear_shouldDeleteSubRange() {
        // 创建集合对象
        var rangeSet = createRangeSet();
        then(rangeSet.isEmpty()).isFalse();

        // 清空集合
        rangeSet.clear();
        // 确认集合为空
        then(rangeSet.isEmpty()).isTrue();
        // 确认转为 Set 集合后结果为空
        then(rangeSet.asRanges()).isEmpty();
    }

    /**
     * 获取 {@link ImmutableRangeSet} 中所有区间包含的值
     *
     * <p>
     * {@link ImmutableRangeSet#asSet(DiscreteDomain)} 根据 {@link DiscreteDomain} 参数, 返回区间内的所有数值
     * </p>
     *
     * <p>
     * 在本例中, 区间 {@code [1..20), (50..100]} 包含的数值包括 {@code [1, 2, ..., 19], [51, 52, ..., 100]}
     * </p>
     */
    @Test
    void asSet_shouldViewRangesAsSetCollection() {
        // 创建集合对象, 转为不变类型
        var rangeSet = ImmutableRangeSet.copyOf(createRangeSet());

        // 获取所有区间包含的整数值, 确认数值为 [1, 2, ..., 19], [51, 52, ..., 100]
        var nums = rangeSet.asSet(DiscreteDomain.integers());
        then(nums).containsExactlyElementsOf(
                Iterables.concat(
                        IntStream.range(1, 20).boxed().toList(),
                        IntStream.range(51, 101).boxed().toList()));
    }

    /**
     * 求 {@link ImmutableRangeSet} 和另一个 {@link RangeSet} 对象的并集
     *
     * <p>
     * {@link ImmutableRangeSet#union(RangeSet)} 方法返回一个新的 {@link ImmutableRangeSet} 对象, 包括两个区间集合的并集结果
     * </p>
     *
     * <p>
     * {@link ImmutableRangeSet#union(RangeSet)} 方法相当于将两个对象中包含的区间添加 ({@code addAll}) 到另一个集合中, 参考
     * {@link #createRangeSet()} 方法
     * </p>
     *
     * <p>
     * 在本例中, 区间 {@code [1..20), (50..100]} 和区间 {@code (5..15), [30..40], [50..100)} 求并集的结果为 {@code [1, 20),
     * [30..40], [50..100]}
     * </p>
     */
    @Test
    void union_shouldViewRangesAsSetCollection() {
        // 创建第一个集合对象, 转为不变类型
        var rangeSet1 = ImmutableRangeSet.copyOf(createRangeSet());

        // 创建第二个集合对象
        var rangeSet2 = ImmutableRangeSet.<Integer>builder()
                .add(Range.open(5, 15))
                .add(Range.closed(30, 40))
                .add(Range.closedOpen(50, 100))
                .build();

        // 求两个集合的并集
        var unionRangeSet = rangeSet1.union(rangeSet2);
        then(unionRangeSet.asRanges()).containsExactly(
                Range.closedOpen(1, 20),
                Range.closed(30, 40),
                Range.closed(50, 100));

        // 确认 union 和 addAll 方法的行为类似
        var rangeSet = TreeRangeSet.create(rangeSet1);
        rangeSet.addAll(rangeSet2);
        then(rangeSet.asRanges()).containsExactly(
                Range.closedOpen(1, 20),
                Range.closed(30, 40),
                Range.closed(50, 100));
    }

    /**
     * 求 {@link ImmutableRangeSet} 和另一个 {@link RangeSet} 对象的差集
     *
     * <p>
     * {@link ImmutableRangeSet#difference(RangeSet)} 方法返回一个新的 {@link ImmutableRangeSet} 对象,
     * 包括两个区间集合的差集结果
     * </p>
     *
     * <p>
     * {@link ImmutableRangeSet#difference(RangeSet)} 方法相当于从一个集合中删除 ({@code removeAll}) 另一个集合, 参考
     * {@link #remove_shouldComputeDifferenceRange()} 方法
     * </p>
     *
     * <p>
     * 在本例中, 区间 {@code [1..20), (50..100]} 和区间 {@code (5..15), [30..40], [50..100)} 求差集的结果为 {@code [1, 5],
     * [15..20), [100..100]}
     * </p>
     */
    @Test
    void difference_shouldViewRangesAsSetCollection() {
        // 创建第一个集合对象, 转为不变类型
        var rangeSet1 = ImmutableRangeSet.copyOf(createRangeSet());

        // 创建第二个集合对象
        var rangeSet2 = ImmutableRangeSet.<Integer>builder()
                .add(Range.open(5, 15))
                .add(Range.closed(30, 40))
                .add(Range.closedOpen(50, 100))
                .build();

        // 求两个集合的差集
        var dffRangeSet = rangeSet1.difference(rangeSet2);
        then(dffRangeSet.asRanges()).containsExactly(
                Range.closed(1, 5),
                Range.closedOpen(15, 20),
                Range.closed(100, 100));

        // 确认 difference 和 removeAll 方法的行为类似
        var rangeSet = TreeRangeSet.create(rangeSet1);
        rangeSet.removeAll(rangeSet2);
        then(rangeSet.asRanges()).containsExactly(
                Range.closed(1, 5),
                Range.closedOpen(15, 20),
                Range.closed(100, 100));
    }

    /**
     * 求 {@link ImmutableRangeSet} 和另一个 {@link RangeSet} 对象的交集
     *
     * <p>
     * {@link ImmutableRangeSet#intersection(RangeSet)} 方法返回一个新的 {@link ImmutableRangeSet} 对象,
     * 包括两个区间集合的交集结果
     * </p>
     *
     * <p>
     * {@link ImmutableRangeSet#intersection(RangeSet)} 方法相当于从一个集合中删除 ({@code removeAll}) 另一个集合的补集,
     * 即 {@code rangeSet1.removeAll(rangeSet2.complement())}
     * </p>
     *
     * <p>
     * 在本例中, 区间 {@code [1..20), (50..100]} 和区间 {@code (5..15), [30..40], [50..100)} 求交集的结果为
     * {@code [5..15], (50, 100)}
     * </p>
     */
    @Test
    void intersection_shouldViewRangesAsSetCollection() {
        // 创建第一个集合对象, 转为不变类型
        var rangeSet1 = ImmutableRangeSet.copyOf(createRangeSet());

        // 创建第二个集合对象
        var rangeSet2 = ImmutableRangeSet.<Integer>builder()
                .add(Range.open(5, 15))
                .add(Range.closed(30, 40))
                .add(Range.closedOpen(50, 100))
                .build();

        // 求两个集合的交集
        var dffRangeSet = rangeSet1.intersection(rangeSet2);
        then(dffRangeSet.asRanges()).containsExactly(
                Range.open(5, 15),
                Range.open(50, 100));

        // 确认 intersection 和 removeAll(rangeSet.complement()) 方法的行为类似
        var rangeSet = TreeRangeSet.create(rangeSet1);
        rangeSet.removeAll(rangeSet2.complement());
        then(dffRangeSet.asRanges()).containsExactly(
                Range.open(5, 15),
                Range.open(50, 100));
    }
}
