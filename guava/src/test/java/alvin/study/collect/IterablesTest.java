package alvin.study.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 测试 {@link Iterables} 工具类
 *
 * <p>
 * {@link Iterables} 工具类提供了一组对 {@link Iterable} 可迭代接口对象进行操作的辅助方法
 * </p>
 */
class IterablesTest {
    /**
     * 将一个可迭代对象内的元素添加到另一个集合中
     *
     * <p>
     * {@link Iterables#addAll(java.util.Collection, Iterable) Iterables.addAll(Collection, Iterable)} 方法用于将一个可迭代
     * 对象包含的元素添加到另一个集合中
     * </p>
     */
    @Test
    void addAll_shouldAddElementsOfIteratorIntoCollection() {
        // 原集合对象
        var src = ImmutableList.of(100, 200, 300);
        // 目标集合对象
        var dst = Lists.newArrayList(1, 2, 3);

        // 将可迭代对象包含的元素添加到目标集合中
        var result = Iterables.addAll(dst, src);
        // 确认添加成功
        then(result).isTrue();
        then(dst).containsExactly(1, 2, 3, 100, 200, 300);
    }

    /**
     * 确认一个迭代器迭代得到的所有元素都满足指定条件
     *
     * <p>
     * {@link Iterables#all(Iterable, com.google.common.base.Predicate) Iterables.all(Iterable, Predicate)} 方法用于判断
     * 一个可迭代对象包含的所有元素均符合指定条件
     * </p>
     */
    @Test
    void all_shouldCheckAllElementsMatchedCondition() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 确认可迭代对象包含的所有元素均符合"大于0"这一条件
        var result = Iterables.all(list, val -> val > 0);
        then(result).isTrue();

        // 确认可迭代对象中, 不是所有元素均符合"偶数"这一条件
        result = Iterables.all(list, val -> val % 2 == 0);
        then(result).isFalse();
    }

    /**
     * 确认一个迭代器迭代得到的任一元素满足指定条件
     *
     * <p>
     * {@link Iterables#any(Iterable, com.google.common.base.Predicate) Iterables.any(Iterator, Predicate)} 方法用于判断
     * 可迭代对象中至少有一个元素符合指定条件
     * </p>
     */
    @Test
    void any_shouldCheckAnyElementsMatchedCondition() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 确认可迭代对象中至少有一个元素均符合"偶数"这一条件
        var result = Iterables.any(list, val -> val % 2 == 0);
        then(result).isTrue();

        // 确认可迭代对象中至少有一个元素均符合"偶数"这一条件
        result = Iterables.any(list, val -> val < 0);
        then(result).isFalse();
    }

    /**
     * 将多个可迭代对象合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterables#concat(Iterable...) Iterables.concat(Iterable...)} 方法将多个可迭代对象合并成一个, 从而可以通过一次迭代,
     * 遍历多个可迭代对象的内容
     * </p>
     */
    @Test
    void concat_shouldConcatIterableToOne() {
        // 定义两个集合对象
        var list1 = ImmutableList.of(1, 2, 3, 4);
        var list2 = ImmutableList.of(5, 6, 7, 8);

        // 将两个可迭代对象合并为一个
        var result = Iterables.concat(list1, list2);
        // 确认合并后的可迭代对象包含两个集合中的所有元素
        then(ImmutableList.copyOf(result)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    /**
     * 将多个迭代器合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterables#consumingIterable(Iterable) Iterables.consumingIterable(Iterable)} 方法用于包装一个可迭代对象, 得到的
     * 代理对象 ({@link com.google.common.collect.FluentIterable FluentIterable} 类型) 在迭代过程中会删除对应集合中的元素, 在迭代
     * 完毕后, 对应集合会被清空
     * </p>
     *
     * <p>
     * 注意, 被包装的可迭代对象, 其迭代器属性必须支持 {@link java.util.Iterator#remove() Iterator.remove()} 方法, 否则在迭代过程中
     * 会抛出异常
     * </p>
     */
    @Test
    void consumingIterator_shouldReadAndRemoveElementsByIterable() {
        // 定义集合对象
        var list = Lists.newArrayList(1, 2, 3, 4, 5);
        // 从集合对象中获取迭代器, 并包装为 UnmodifiableIterator 类型迭代器
        var iter = Iterables.consumingIterable(list);
        // 遍历可迭代对象, 确认包含原集合中所有元素
        then(ImmutableList.copyOf(iter)).containsExactly(1, 2, 3, 4, 5);
        // 确认迭代完成后, 对应集合内容被清空
        then(list).isEmpty();

        // 定义不可变集合对象
        var immList = ImmutableList.of(1, 2, 3, 4, 5);
        // 将可迭代对象包装为 FluentIterable 类型
        var immIter = Iterables.consumingIterable(immList);
        // 因不可变集合的迭代过程不支持 remove 操作, 故抛出异常
        thenThrownBy(() -> ImmutableList.copyOf(immIter)).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * 将多个迭代器合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterables#contains(Iterable, Object) Iterables.contains(Iterable, Object)} 方法用于从一个迭代器中确认是否包含指
     * 定的元素值
     * </p>
     */
    @Test
    void contains_shouldCheckIterableContainsSpecificElement() {
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        var result = Iterables.contains(list, 3);
        then(result).isTrue();

        result = Iterables.contains(list, 0);
        then(result).isFalse();
    }

    /**
     * 循环迭代
     *
     * <p>
     * 循环迭代是一个没有头尾的迭代器, 即可以一直迭代没有尽头
     * </p>
     *
     * <p>
     * 循环迭代内部为一个可迭代对象的引用, 并从该对象获取迭代器对象, 一旦获取的迭代器迭代结束, 则重新获取迭代器对象重新开始迭代
     * </p>
     *
     * <p>
     * 本例中, 将一个长度为 {@code 5} 的 {@link java.util.List List} 对象进行包装, 并对其进行 {@code 10} 次迭代, 实际上是将
     * {@code List} 对象通过迭代器进行了两轮迭代
     * </p>
     */
    @Test
    void cycle_shouldRepeatReadFromAnIterable() {
        // 产生一个长度为 5 的集合
        var src = ImmutableList.of(1, 2, 3, 4, 5);
        // 存放迭代结果的集合
        var dst = Lists.newArrayList();

        // 通过 List 对象产生一个循环迭代器对象
        var cycleIter = Iterables.cycle(src);

        var times = 0;
        // 通过循环迭代器, 进行 10 次迭代 (List 集合只包含 5 个元素)
        for (var n : cycleIter) {
            // 将迭代结果加入到目标数组
            dst.add(n);
            if (++times == 10) {
                break;
            }
        }

        // 确认目标集合包含 10 个元素
        then(dst).hasSize(10);
        then(dst).containsExactly(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
    }

    /**
     * 对可迭代对象进行过滤
     *
     * <p>
     * {@link Iterables#filter(Iterable, com.google.common.base.Predicate)
     * Iterators.filter(Iterable, Predicate)} 方法返回一个可迭代对象, 只包含所给可迭代对象中符合条件的元素值
     * </p>
     */
    @Test
    void filter_shouldFilterIterable() {
        // 产生一个集合
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 通过 List 对象产生一个循环迭代器对象
        var result = ImmutableList.copyOf(Iterables.filter(list, n -> n % 2 == 0));
        then(result).containsExactly(2, 4);
    }

    /**
     * 通过迭代器, 统计指定元素出现的次数
     *
     * <p>
     * {@link Iterables#frequency(Iterable, Object) Iterables.frequency(Iterable, Object)} 方法用于将一个可迭代对象中包含的
     * 元素逐一和目标对象进行比较, 并统计和目标对象相等的元素个数
     * </p>
     */
    @Test
    void frequency_shouldCountElementsByIterable() {
        // 定义集合元素
        var list = ImmutableList.of(1, 2, 2, 3, 4, 4, 4, 5);

        // 统计指定元素在迭代过程中出现的次数
        var count = Iterables.frequency(list, 4);
        then(count).isEqualTo(3);
    }

    /**
     * 通过迭代器, 将得到的元素按顺序平均分配到指定数量的其它集合中
     *
     * <p>
     * {@link Iterables#partition(Iterable, int) Iterables.partition(Iterable, int)} 方法遍历可迭代对象, 并将得到的元素按顺序
     * 存入到另外的指定数量的集合中. 例如: 将集合 {@code [1, 2, 3]} 分到两个数组中, 结果为 {@code [[1, 2], [3]]}
     * </p>
     *
     * <p>
     * {@link Iterables#paddedPartition(Iterable, int) Iterables.paddedPartition(Iterable, int)} 方法与
     * {@link Iterables#partition(Iterable, int) Iterables.partition(Iterable, int)} 方法基本类似, 但要求结果中每个数组的元素
     * 个数都是一致的, 如果元素数量不足, 则以 {@code null} 填充. 例如: 将集合 {@code [1, 2, 3]} 分到两个数组中, 结果为
     * {@code [[1, 2], [3, null]]}
     * </p>
     */
    @Test
    void partition_shouldSplitIterableIntoPartitions() {
        // 定义集合
        var list = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8);

        // 通过集合迭代器, 将元素存储到另外 3 个集合中
        var result = ImmutableList.copyOf(Iterables.partition(list, 3));
        then(result).hasSize(3);
        // 确认结果中每个集合都按顺序平均分配到了若干元素
        then(result.get(0)).containsExactly(1, 2, 3);
        then(result.get(1)).containsExactly(4, 5, 6);
        then(result.get(2)).containsExactly(7, 8);

        // 通过集合迭代器, 将元素存储到另外 3 个集合中
        result = ImmutableList.copyOf(Iterables.paddedPartition(list, 3));
        // 确认所得结果是一个数量为 3 的集合
        then(result).hasSize(3);
        // 确认结果中每个集合元素数量一致, 空缺的位置使用 null 填充
        then(result.get(0)).containsExactly(1, 2, 3);
        then(result.get(1)).containsExactly(4, 5, 6);
        then(result.get(2)).containsExactly(7, 8, null);
    }

    /**
     * 测试从可迭代对象中获取指定元素值
     *
     * <p>
     * {@link Iterables#getFirst(Iterable, Object) Iterables.getFirst(Iterable, Object)} 方法获取可迭代对象的第一个元素值,
     * 如果迭可迭代对象为空, 则返回默认值
     * </p>
     *
     * <p>
     * {@link Iterables#getLast(Iterable, Object) Iterables.getLast(Iterable, Object)} 方法获取可迭代对象的最后一个元素,
     * 如果可迭代对象为空, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterables#get(Iterable, int, Object) Iterables.get(Iterable, int, Object)} 方法获取迭代指定次数后的
     * 元素值, 如果迭代器在指定次数前已经迭代完毕, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterables#getOnlyElement(Iterable, Object) Iterables.getOnlyElement(Iterable, Object)} 方法获取可迭代对象中
     * 唯一的元素, 即可迭代对象只包含一个元素时, 返回该元素值; 如果不包含元素, 则返回默认值; 如果包含多个元素, 则抛出异常
     * </p>
     */
    @Test
    void getElement_shouldGetFirstElementFromIterables() {
        // 创建一个空集合
        var emptyList = ImmutableList.of();

        // 确认可迭代对象为空时, 获取第一个元素返回默认值 null
        var result = Iterables.getFirst(emptyList, null);
        then(result).isNull();

        // 确认可迭代对象为空时, 获取最后一个元素返回默认值 null
        result = Iterables.getLast(emptyList, null);
        then(result).isNull();

        // 确认可迭代对象为空时, 获取指定迭代次数的元素返回默认值 null
        result = Iterables.get(emptyList, 1, null);
        then(result).isNull();

        // 确认可迭代对象为空时, 获取唯一元素时返回默认值 null
        result = Iterables.getOnlyElement(emptyList, null);
        then(result).isNull();

        // 创建一个非空且元素数量多于 1 的集合
        var list = ImmutableList.of(1, 2, 3);

        // 确认获取到可迭代对象的第一个元素值
        result = Iterables.getFirst(list, null);
        then(result).isEqualTo(1);

        // 确认获取到可迭代对象的最后一个元素值
        result = Iterables.getLast(list, null);
        then(result).isEqualTo(3);

        // 确认获取到可迭代对象指定迭代次数后的元素值
        result = Iterables.get(list, 1, null);
        then(result).isEqualTo(2);

        // 因可迭代对象包含多个元素, 获取唯一元素时抛出异常
        thenThrownBy(() -> Iterables.getOnlyElement(list, null))
                .isInstanceOf(IllegalArgumentException.class);

        // 定义一个只有单一元素的集合
        var singleToneList = ImmutableList.of(1);
        // 确认通过可迭代对象可以获取唯一元素
        result = Iterables.getOnlyElement(singleToneList, null);
        then(result).isEqualTo(1);
    }
}
