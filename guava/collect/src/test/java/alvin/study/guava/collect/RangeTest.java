package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;

import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link Range} 集合
 *
 * <p>
 * {@link Range} 类型集合并不存储实际的元素值, 而是存储集合的边界以及边界是否开闭,
 * 及从数学定义上的"开区间"或"闭区间", 例如 {@code [1..3]}, {@code [2..1000)} 等
 * </p>
 *
 * <p>
 * {@link Range} 对象包括一系列方法对区间进行操作, 包括:
 * <ul>
 * <li>
 * 创建开闭区间: {@code open}, {@code closed}, {@code closedOpen},
 * {@code openClosed}, {@code greaterThan}, {@code lessThan}, {@code atMost},
 * {@code atLeast} 以及 {@code all}
 * </li>
 * <li>
 * 指定区间的开闭性: {@code range}, {@code downTo} 以及 {@code upTo},
 * 通过区间边界值和 {@link BoundType} 枚举来定义区间
 * </li>
 * <li>
 * 判断一个数值是否包含在指定的区间内: {@code contains} 和
 * {@code containsAll} 方法
 * </li>
 * <li>
 * 判断两区间的关系: {@code encloses} 和 {@code isConnected} 方法
 * </li>
 * <li>
 * 对两区间进行运算: {@code intersection} 和 {@code span} 方法
 * </li>
 * <li>
 * 对区间进行迭代: 通过 {@link ContiguousSet} 类配合 {@link DiscreteDomain} 类,
 * 可以将区间转为一个 {@link com.google.common.collect.ImmutableSortedSet
 * ImmutableSortedSet}, 通过该集合对象进行操作
 * </li>
 * </ul>
 * </p>
 */
class RangeTest {
    /**
     * 测试创建一个区间对象
     *
     * <p>
     * {@link Range#closedOpen(Comparable, Comparable)} 方法用于创建一个半开区间
     * (左闭右开), 类似的方法还有:
     *
     * <ul>
     * <li>
     * {@link Range#closed(Comparable, Comparable)}, 创建闭区间
     * </li>
     * <li>
     * {@link Range#open(Comparable, Comparable)}, 创建开区间
     * </li>
     * <li>
     * {@link Range#openClosed(Comparable, Comparable)}, 创建半开区间 (左开右闭)
     * </li>
     * <li>
     * {@link Range#lessThan(Comparable)}, 创建小于指定值的无限区间, 即 {@code (-∞..n)}
     * </li>
     * <li>
     * {@link Range#greaterThan(Comparable)}, 创建大于指定值的无限区间, 即 {@code (n..∞)}
     * </li>
     * <li>
     * {@link Range#atLeast(Comparable)}, 创建大于指定值的无限区间, 即 {@code [n..∞)}
     * </li>
     * <li>
     * {@link Range#atMost(Comparable)}, 创建大于指定值的无限区间, 即 {@code (-∞..n]}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 除上述方法外, 还可以指定区间的开闭性:
     * <ul>
     * <li>
     * {@link Range#range(Comparable, BoundType, Comparable, BoundType)},
     * 通过区间的上下限和开闭性参数创建区间</li>
     * <li>
     * {@link Range#upTo(Comparable, BoundType)}, 创建并设定区间的上限和其开闭性, 区间下限为 {@code -∞};
     * 如果上限为开, 则相当于 {@link Range#lessThan(Comparable)} 方法, 如果上限为闭, 则相当于
     * {@link Range#atMost(Comparable)} 方法
     * </li>
     * <li>
     * {@link Range#downTo(Comparable, BoundType)}, 创建并设定区间的下限和其开闭性, 区间上限为
     * {@code ∞}; 如果下限为开, 则相当于 {@link Range#greaterThan(Comparable)} 方法,
     * 如果下限为闭, 则相当于 {@link Range#atLeast(Comparable)} 方法
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void range_shouldCreateRangeNumber() {
        // 创建一个 [1..5) 的区间
        var range = Range.closedOpen(1, 5);

        // 确认区间不为空
        then(range.isEmpty()).isFalse();

        // 确认区间具备下限, 如果区间下限为 ∞, 则该方法返回 false
        then(range.hasLowerBound()).isTrue();
        // 确认区间的下限为关闭
        then(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);
        // 确认区间的上限
        then(range.lowerEndpoint()).isEqualTo(1);

        // 确认区间具备上限, 如果区间上限为 ∞, 则该方法返回 false
        then(range.hasUpperBound()).isTrue();
        // 确认区间的上限
        then(range.upperBoundType()).isEqualTo(BoundType.OPEN);
        // 确认区间的上限为开放
        then(range.upperEndpoint()).isEqualTo(5);

        // 创建一个 (-∞, 1] 的区间
        range = Range.upTo(1, BoundType.CLOSED);
        // 确认区间下限为 -∞
        then(range.hasLowerBound()).isFalse();
        // 确认区间上限为 1
        then(range.upperEndpoint()).isEqualTo(1);
        // 确认区间上限为关闭
        then(range.upperBoundType()).isEqualTo(BoundType.CLOSED);

        // 确认 upTo 方法的第二个参数为 OPEN, 则相当于 lessThan 方法
        then(Range.upTo(1, BoundType.OPEN))
                .isEqualTo(Range.lessThan(1));
        // 确认 upTo 方法的第二个参数为 CLOSED, 则相当于 atMost 方法
        then(Range.upTo(1, BoundType.CLOSED))
                .isEqualTo(Range.atMost(1));

        // 创建一个 [1, ∞) 的区间
        range = Range.downTo(1, BoundType.CLOSED);
        // 确认区间下限为 1
        then(range.lowerEndpoint()).isEqualTo(1);
        // 确认区间下限为关闭
        then(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);
        // 确认区间上限为 ∞
        then(range.hasUpperBound()).isFalse();

        // 确认 downTo 方法的第二个参数为 OPEN, 则相当于 greaterThan 方法
        then(Range.downTo(1, BoundType.OPEN))
                .isEqualTo(Range.greaterThan(1));
        // 确认 downTo 方法的第二个参数为 CLOSED, 则相当于 atLeast 方法
        then(Range.downTo(1, BoundType.CLOSED))
                .isEqualTo(Range.atLeast(1));
    }

    /**
     * 测试确认一个值或一个集合是否包含在指定区间内
     *
     * <p>
     * 通过 {@link Range#contains(Comparable)} 方法可以判断一个值是否在指定的区间范围内
     * </p>
     *
     * <p>
     * 通过 {@link Range#containsAll(Iterable)}
     * 方法可以判断一个集合的所有值是否在指定区间的范围内, 即区间是否包含该集合
     * </p>
     */
    @Test
    void contains_shouldCheckIfRangeContainsValue() {
        // 创建一个 [1..5) 的区间
        var range = Range.range(1, BoundType.CLOSED, 5, BoundType.OPEN);

        // 确认指定值是否包含在区间内
        then(range.contains(1)).isTrue();
        then(range.contains(5)).isFalse();

        // 确认指定的集合是否包含在区间内
        then(range.containsAll(ImmutableList.of(1, 2, 3, 4))).isTrue();
    }

    /**
     * 对两个区间进行计算
     *
     * <p>
     * {@link Range#encloses(Range)} 方法可以判断一个区间是否包含另一个区间,
     * 即后者的所有值都包含在前者的范围内
     * </p>
     *
     * <p>
     * {@link Range#isConnected(Range)} 方法可以判断两个区间是否有重叠部分,
     * 即是否存在一个子区间同时被指定的两个区间包括
     * </p>
     *
     * <p>
     * {@link Range#intersection(Range)} 方法返回一个 {@link Range} 对象,
     * 表示两个区间共同的子区间
     * </p>
     *
     * <p>
     * {@link Range#span(Range)} 方法返回一个 {@link Range} 对象,
     * 表示一个可以恰好同时包含两个所给区间的区间
     * </p>
     */
    @Test
    void operations_shouldOperateWithTwoRanges() {
        // 创建一个 [-∞..10) 的区间
        var range1 = Range.upTo(10, BoundType.OPEN);
        // 创建一个 [10..100) 的区间
        var range2 = Range.range(10, BoundType.CLOSED, 100, BoundType.OPEN);

        // 确认区间 1 不包含区间 2, 即区间 2 内至少有一个值不在区间 1 中
        then(range1.encloses(range2)).isFalse();

        // 确认区间 1 和区间 2 连续, 即有一组值可以即在区间 1 内, 同时也在区间 2 内
        then(range1.isConnected(range2)).isTrue();

        // 获取两个区间共同的子区间
        var range = range1.intersection(range2);
        // 确认子区间为 [10 .. 10)
        then(range.lowerEndpoint()).isEqualTo(10);
        then(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);
        then(range.upperEndpoint()).isEqualTo(10);
        then(range.upperBoundType()).isEqualTo(BoundType.OPEN);

        // 获取可以恰好同时包含两个区间的区间
        range = range1.span(range2);
        // 确认子区间为 [-∞ .. 100)
        then(range.hasLowerBound()).isFalse();
        then(range.upperEndpoint()).isEqualTo(100);
        then(range.upperBoundType()).isEqualTo(BoundType.OPEN);
    }

    /**
     * 将区间转化为集合, 即将一个 "连续的" 区间类型转化为 "离散的"
     * {@code Set} 集合类型
     *
     * <p>
     * {@link ContiguousSet} 集合实现了 {@link com.google.common.collect.ImmutableSortedSet
     * ImmutableSortedSet} 接口, 具备 {@link java.util.SortedSet SortedSet}
     * 接口除修改集合元素外的其它行为
     * </p>
     *
     * <p>
     * {@link ContiguousSet} 集合内部并不存储实际的元素, 只是存储了一个 {@link Range}
     * 对象的副本, 通过该 {@link Range} 对象以及 {@link DiscreteDomain} 对象指定的元素类型,
     * 通过运算的方式产生所需的集合元素, 这也是 {@link ContiguousSet} 无法修改的原因
     * </p>
     *
     * <p>
     * {@link ContiguousSet} 只能将区间转为整数元素集合, 且区间必须具有明确的上限和下限
     * </p>
     */
    @Test
    void discrete_shouldConvertRangeToDiscretelySet() {
        var range = Range.closedOpen(-3, 3);

        // 将区间转化为集合
        var set = ContiguousSet.create(range, DiscreteDomain.integers());
        then(set).containsExactly(-3, -2, -1, 0, 1, 2);
    }
}
