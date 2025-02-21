package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

/**
 * {@link RangeMap} 存储了区间和指定值的对应关系
 *
 * <p>
 * {@link RangeMap} 接口存储了 {@link Range} 与指定类型组成的键值对, 随后即可通过区间内的值查找对应的键值
 * </p>
 *
 * <p>
 * Guava 中, {@link RangeMap} 接口了实现类: {@link TreeRangeMap}, 相当于基于 {@link java.util.TreeMap TreeMap} 类型实现的类型
 * </p>
 */
class RangeMapTest {
    /**
     * 实例化一个 {@link RangeMap} 对象, 并向其中添加若干 {@link Range} => {@link String} 键值对
     *
     * <p>
     * {@link TreeRangeMap#create()} 方法用于创建一个空对象
     * </p>
     *
     * <p>
     * {@link TreeRangeMap#put(Range, Object)} 向对象中添加新的键值对, 如果新添加的区间和原有的某个区间相交, 则原区间重叠的那部分会
     * 划给新区间. 例如添加两个键值对: {@code (1..10] => "A"} 及 {@code (5..20) => "B"}, 则添加后的键值对为:
     * {@code (1..5] => "A"} 及 {@code (5..20) => "B"}
     * </p>
     *
     * <p>
     * 也可以通过 {@link TreeRangeMap#putAll(RangeMap)} 方法进行两个 {@link RangeMap} 对象直接的合并操作
     * </p>
     *
     * @return {@link RangeMap} 对象, 内部包含 {@code (1..5] => "A", (5..20) => "B", (30..∞) => "C"} 三个键值对
     */
    private RangeMap<Integer, String> createRangeMap() {
        var rangeMap = TreeRangeMap.<Integer, String>create();

        // 存储后结果为 (1..10] => "A"
        rangeMap.put(Range.openClosed(1, 10), "A");

        // 存储后结果为 (1..5] => "A", (5..20) => "B", 新添加的区间覆盖了部分原区间
        rangeMap.put(Range.open(5, 20), "B");

        // 存储后结果为 (1..5] => "A", (5..20) => "B", [30..∞) => "C"
        rangeMap.put(Range.atLeast(30), "C");

        return rangeMap;
    }

    /**
     * 获取 {@code Map<Range, Object>} 类型的对象
     *
     * <p>
     * {@link RangeMap#asMapOfRanges()} 返回一个 {@link java.util.Map Map} 对象, 以 {@link Range} 对象为 Key, 以
     * {@link RangeMap} 指定类型值为 Value
     * </p>
     *
     * <p>
     * 返回的 {@link java.util.Map Map} 对象的 Key 按照区间定义从小到大排序
     * </p>
     */
    @Test
    void asMapOfRanges_shouldConvertToMap() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 获取 Map 对象, 确认包含的键值对, 且键集合以区间定义从小到大排列
        then(rangeMap.asMapOfRanges()).containsExactly(
            entry(Range.openClosed(1, 5), "A"),
            entry(Range.open(5, 20), "B"),
            entry(Range.atLeast(30), "C"));
    }

    /**
     * 获取 {@code Map<Range, Object>} 类型的对象
     *
     * <p>
     * {@link RangeMap#asDescendingMapOfRanges()} 返回一个 {@link java.util.Map Map} 对象, 以 {@link Range} 对象为 Key,
     * 以 {@link RangeMap} 指定类型值为 Value
     * </p>
     *
     * <p>
     * 返回的 {@link java.util.Map Map} 对象的 Key 按照区间定义从大到小排序
     * </p>
     */
    @Test
    void asDescendingMapOfRanges_shouldConvertToMapByDescendingKeySet() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 获取 Map 对象, 确认包含的键值对, 且键集合以区间定义从大到小排列
        then(rangeMap.asDescendingMapOfRanges()).containsExactly(
            entry(Range.atLeast(30), "C"),
            entry(Range.open(5, 20), "B"),
            entry(Range.openClosed(1, 5), "A"));
    }

    /**
     * 添加一个键值对
     *
     * <p>
     * {@link RangeMap#putCoalescing(Range, Object)} 方法和 {@link RangeMap#put(Range, Object)} 方法类似, 都是添加一个以
     * {@link Range} 对象为 Key 的键值对
     * </p>
     *
     * <p>
     * {@link RangeMap#putCoalescing(Range, Object)} 方法的特殊之处在于, 如果添加的键值对的 Key 和已有的 Key 区间连续, 且 Value
     * 相同, 则会合并 Key 区间, 将两个键值对合并为一个
     * </p>
     *
     * <p>
     * 大部分情况下, 不合并 Key 表示的区间并不会影响 {@link RangeMap} 对象根据区间查询的能力, 而且合并区间会带来额外的计算量,
     * 所以除非有特殊需求, 一般情况下使用 {@link RangeMap#put(Range, Object)} 方法即可
     * </p>
     */
    @Test
    void putCoalescing_shouldPutRangeEntity() {
        {
            // 创建对象
            var rangeMap = createRangeMap();

            // 通过 put 方法添加键值对, 确认新的键值对添加到对象中
            // 新的键值对中, 键区间和已有键区间连接, 且值已存在, 但仍作为单独一项添加到对象
            rangeMap.put(Range.closed(20, 30), "B");
            then(rangeMap.asMapOfRanges()).containsExactly(
                entry(Range.openClosed(1, 5), "A"),
                entry(Range.open(5, 20), "B"),
                entry(Range.closed(20, 30), "B"),
                entry(Range.greaterThan(30), "C"));
        }
        {
            // 创建对象
            var rangeMap = createRangeMap();

            // 通过 putCoalescing 方法添加键值对, 确认新的键值对和已有的键值对合并
            // 新的键值对中, 键区间和已有键区间连接, 且值已存在, 和已存在的对应键合并
            rangeMap.putCoalescing(Range.closed(20, 30), "B");
            then(rangeMap.asMapOfRanges()).containsExactly(
                entry(Range.openClosed(1, 5), "A"),
                entry(Range.openClosed(5, 30), "B"),
                entry(Range.greaterThan(30), "C"));
        }
    }

    /**
     * 合并一个键值对
     *
     * <p>
     * {@link RangeMap#merge(Range, Object, java.util.function.BiFunction) RangeMap.merge(Range, Object, BiFunction)}
     * 方法将一个区间合并到 {@link RangeMap} 对象中
     * </p>
     *
     * <p>
     * 若待合并的区间的某个部分已存在, 会拆分新添加的区间 Key, 区间重合部分通过 {@code BiFunction} 参数确定以该部分区间为键的键值;
     * 不重合部分以新的键值进行添加
     * </p>
     *
     * <p>
     * 例如: 在 {@code (1..5] => "A", (5..20) => "B", [30..∞) => "C"} 基础上添加新键值对 {@code [15..30] => "D"}, 所得结果为
     * {@code (1..5] => "A", (5..15) => "B", [15..20) => "D", [20..30) => "D", [30..30] => "D", (30..∞) => "C"}
     * </p>
     *
     * <p>
     * 注意: 对于跨越边界的分割会在区间的边界处
     * </p>
     *
     * <p>
     * 如果 {@code BiFunction} 参数返回 {@code null} 值, 则在合并时, 会删除重复区间部分的 Key (拆分后删除重复区间部分)
     * </p>
     */
    @Test
    void merge_shouldMergeRangeEntry() {
        {
            // 创建对象
            var rangeMap = createRangeMap();

            // 在已有对象上合并新的键值对, 会导致对区间的拆分
            rangeMap.merge(Range.closed(15, 30), "D", (v1, v2) -> v2);
            // 确认合并结果包含的键值对, 包括拆分的区间
            then(rangeMap.asMapOfRanges()).containsExactly(
                entry(Range.openClosed(1, 5), "A"),
                entry(Range.open(5, 15), "B"),
                entry(Range.closedOpen(15, 20), "D"),
                entry(Range.closedOpen(20, 30), "D"),
                entry(Range.closed(30, 30), "D"),
                entry(Range.greaterThan(30), "C"));

            // 确认集合边界值的归属, 重复部分归属于新的键值对区间
            then(rangeMap.get(15)).isEqualTo("D");
            then(rangeMap.get(30)).isEqualTo("D");
            then(rangeMap.get(20)).isEqualTo("D");
        }
        {
            // 创建对象
            var rangeMap = createRangeMap();

            // 在已有对象上合并新的键值对, 会导致对区间的拆分
            // 在键区间重复时, 返回 null 值, 则会将重复部分拆分出来后删除
            rangeMap.merge(Range.closed(15, 30), "D", (v1, v2) -> null);
            // 确认合并结果包含的键值对, 其中重复的部分已被删除, 不同的部分得以保留
            then(rangeMap.asMapOfRanges()).containsExactly(
                entry(Range.openClosed(1, 5), "A"),
                entry(Range.open(5, 15), "B"),
                entry(Range.closedOpen(20, 30), "D"),
                entry(Range.greaterThan(30), "C"));

            // 确认边界值的归属, 由于重复部分被删除
            then(rangeMap.get(15)).isNull();
            then(rangeMap.get(30)).isNull();
            then(rangeMap.get(20)).isEqualTo("D");
        }
    }

    /**
     * 通过区间中的某个值获取对应的键值
     *
     * <p>
     * {@link RangeMap#get(Comparable)} 方法通过区间内的某个值, 获取 {@link RangeMap} 对象中存储的键值对的值
     * </p>
     *
     * <p>
     * 例如: 在 {@code (1..5] => "A", (5..20) => "B", [30..∞) => "C"} 中, 通过 {@code 3} 可以获得键值对
     * {@code (1..5] => "A"} 对应的值 {@code "A"}
     * </p>
     */
    @Test
    void get_shouldFindValueByGivenNumberInRange() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 通过 3 从键值对 (1..5] => "A" 中得到 "A"
        then(rangeMap.get(3)).isEqualTo("A");
        // 通过 5 从键值对 (1..5] => "A" 中得到 "A"
        then(rangeMap.get(5)).isEqualTo("A");

        // 通过 8 从键值对 (5..20) => "B" 中得到 "B"
        then(rangeMap.get(8)).isEqualTo("B");
        // 通过 30 从键值对 [30..∞) => "C" 中得到 "C"
        then(rangeMap.get(30)).isEqualTo("C");
        // 通过 100000 从键值对 [30..∞) => "C" 中得到 "C"
        then(rangeMap.get(100000)).isEqualTo("C");

        // 25 无对应区间的键
        then(rangeMap.get(25)).isNull();
    }

    /**
     * 通过区间中的某个值获取对应的键值
     *
     * <p>
     * {@link RangeMap#getEntry(Comparable)} 方法通过区间内的某个值, 获取 {@link RangeMap} 对象中存储的键值对
     * </p>
     *
     * <p>
     * 例如: 在 {@code (1..5] => "A", (5..20) => "B", [30..∞) => "C"} 中, 通过 {@code 3} 可以获得键值对
     * {@code (1..5] => "A"}
     * </p>
     */
    @Test
    void getEntry_shouldGetEntryByGivenNumberInRange() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 通过 3 获取键值对 (1..5] => "A"
        then(rangeMap.getEntry(3)).isEqualTo(entry(Range.openClosed(1, 5), "A"));
        // 通过 5 获取键值对 (1..5] => "A"
        then(rangeMap.getEntry(5)).isEqualTo(entry(Range.openClosed(1, 5), "A"));

        // 通过 8 获取键值对 (5..20) => "B"
        then(rangeMap.getEntry(8)).isEqualTo(entry(Range.open(5, 20), "B"));
        // 通过 30 获取键值对 [30..∞) => "C"
        then(rangeMap.getEntry(30)).isEqualTo(entry(Range.atLeast(30), "C"));
        // 通过 30 获取键值对 [30..∞) => "C"
        then(rangeMap.getEntry(100000)).isEqualTo(entry(Range.atLeast(30), "C"));

        // 25 无对应区间的键值对
        then(rangeMap.get(25)).isNull();
    }

    /**
     * 获取所给区间和 {@link RangeMap} 相交的结果
     *
     * <p>
     * {@link RangeMap#subRangeMap(Range)} 方法通过区间内的某个值, 获取 {@link RangeMap} 对象中存储的键值对
     * </p>
     *
     * <p>
     * 例如: 在 {@code (1..5] => "A", (5..20) => "B", [30..∞) => "C"} 中, 获取其与区间 {@code [2..25]} 相交的部分, 结果为
     * {@code [2..5], (5..20)}
     * </p>
     */
    @Test
    void subRangeMap_shouldGetMapOfSubRangesByGivenRange() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 获取与指定区间相交的结果
        var subRangeMap = rangeMap.subRangeMap(Range.closed(2, 25));
        // 确认交集的结果
        then(subRangeMap.asMapOfRanges()).containsExactly(
            entry(Range.closed(2, 5), "A"),
            entry(Range.open(5, 20), "B"));
    }

    /**
     * 获取所给区间和 {@link RangeMap} 结果差集结果
     *
     * <p>
     * {@link RangeMap#remove(Range)} 方法和指定区间的差集
     * </p>
     *
     * <p>
     * 例如: 在 {@code (1..5] => "A", (5..20) => "B", [30..∞) => "C"} 中, 获取其与区间 {@code [2..25]} 的差集, 结果为
     * {@code (1..2), [30..∞)}
     * </p>
     */
    @Test
    void remove_shouldRemoveRange() {
        // 创建对象
        var rangeMap = createRangeMap();

        // 获取与指定区间相交的结果
        rangeMap.remove(Range.closed(2, 25));
        // 确认交集的结果
        then(rangeMap.asMapOfRanges()).containsExactly(
            entry(Range.open(1, 2), "A"),
            entry(Range.atLeast(30), "C"));
    }
}
