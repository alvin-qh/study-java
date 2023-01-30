package alvin.study.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * 测试 {@link Iterators} 工具类
 *
 * <p>
 * {@link Iterators} 工具类提供了一组对 {@link java.util.Iterator Iterator} 迭代器对象进行操作的辅助方法
 * </p>
 */
class IteratorsTest {
    /**
     * 将一个迭代器对象内的元素添加到另一个集合中
     *
     * <p>
     * {@link Iterators#addAll(java.util.Collection, java.util.Iterator) Iterators.addAll(Collection, Iterator)} 方法用
     * 于将一个迭代器进行迭代, 并把得到的元素添加到指定的集合对象中
     * </p>
     */
    @Test
    void addAll_shouldAddElementsOfIteratorIntoCollection() {
        // 原集合对象
        var src = ImmutableList.of(100, 200, 300);
        // 目标集合对象
        var dst = Lists.newArrayList(1, 2, 3);

        // 通过原集合的迭代器, 将其元素添加到目标集合中
        var result = Iterators.addAll(dst, src.iterator());
        // 确认添加成功
        then(result).isTrue();
        then(dst).containsExactly(1, 2, 3, 100, 200, 300);
    }

    /**
     * 确认一个迭代器迭代得到的所有元素都满足指定条件
     *
     * <p>
     * {@link Iterators#all(java.util.Iterator, com.google.common.base.Predicate) Iterators.all(Iterator, Predicate)}
     * 方法用于判断迭代器中的所有元素均符合指定条件
     * </p>
     */
    @Test
    void all_shouldCheckAllElementsMatchedCondition() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 从集合对象获取迭代器, 并确认所有元素均符合"大于0"这一条件
        var result = Iterators.all(list.iterator(), val -> val > 0);
        then(result).isTrue();

        // 从集合对象获取迭代器, 并确认不是所有元素均符合"偶数"这一条件
        result = Iterators.all(list.iterator(), val -> val % 2 == 0);
        then(result).isFalse();
    }

    /**
     * 确认一个迭代器迭代得到的任一元素满足指定条件
     *
     * <p>
     * {@link Iterators#any(java.util.Iterator, com.google.common.base.Predicate) Iterators.any(Iterator, Predicate)}
     * 方法用于判断迭代器中至少有一个元素符合指定条件
     * </p>
     */
    @Test
    void any_shouldCheckAnyElementsMatchedCondition() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 从集合对象获取迭代器, 并确认至少有一个元素均符合"偶数"这一条件
        var result = Iterators.any(list.iterator(), val -> val % 2 == 0);
        then(result).isTrue();

        // 从集合对象获取迭代器, 并确认所有元素均不符合"小于0"这一条件
        result = Iterators.any(list.iterator(), val -> val < 0);
        then(result).isFalse();
    }

    /**
     * 将多个迭代器合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterators#concat(java.util.Iterator...) Iterators.concat(Iterator...)} 方法将多个迭代器对象合并成一个, 从而可以
     * 通过一次迭代, 遍历多个迭代器内容
     * </p>
     */
    @Test
    void concat_shouldConcatIteratorsToOne() {
        // 定义两个集合对象
        var list1 = ImmutableList.of(1, 2, 3, 4);
        var list2 = ImmutableList.of(5, 6, 7, 8);

        // 将两个集合对象迭代器合并为一个
        var result = Iterators.concat(list1.iterator(), list2.iterator());
        // 确认合并后的迭代器对象包含两个集合中的所有元素
        then(ImmutableList.copyOf(result)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    /**
     * 将多个迭代器合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterators#consumingIterator(java.util.Iterator) Iterators.consumingIterator(Iterator)} 方法用于包装一个迭代器
     * 对象, 得到代理迭代器 ({@link com.google.common.collect.UnmodifiableIterator UnmodifiableIterator} 类型) 对象在迭代过程
     * 中会删除被迭代元素, 会导致对应集合中相应元素删除, 在迭代完毕后, 对应集合会被清空
     * </p>
     *
     * <p>
     * 注意, 被包装的迭代器必须支持 {@link java.util.Iterator#remove() Iterator.remove()} 方法, 否则在调用
     * {@link com.google.common.collect.UnmodifiableIterator#next() UnmodifiableIterator.next()} 时会抛出异常
     * </p>
     */
    @Test
    void consumingIterator_shouldReadAndRemoveElementsByIterator() {
        // 定义集合对象
        var list = Lists.newArrayList(1, 2, 3, 4, 5);
        // 从集合对象中获取迭代器, 并包装为 UnmodifiableIterator 类型迭代器
        var iter = Iterators.consumingIterator(list.iterator());
        // 遍历迭代器, 确认迭代结果符合原集合元素
        then(ImmutableList.copyOf(iter)).containsExactly(1, 2, 3, 4, 5);
        // 确认迭代完成后, 对应集合内容被清空
        then(list).isEmpty();

        // 定义不可变集合对象
        var immList = ImmutableList.of(1, 2, 3, 4, 5);
        // 从集合对象中获取迭代器, 并包装为 UnmodifiableIterator 类型迭代器
        var immIter = Iterators.consumingIterator(immList.iterator());
        // 因不可变集合的迭代器不支持 remove 操作, 故抛出异常
        thenThrownBy(() -> ImmutableList.copyOf(immIter)).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * 将多个迭代器合并成一个, 以便可以一次性进行迭代
     *
     * <p>
     * {@link Iterators#contains(java.util.Iterator, Object) Iterators.contains(Iterator, Object)} 方法用于从一个迭代器中
     * 确认是否包含指定的元素值
     * </p>
     */
    @Test
    void contains_shouldCheckIteratorContainsSpecificElement() {
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        var result = Iterators.contains(list.iterator(), 3);
        then(result).isTrue();

        result = Iterators.contains(list.iterator(), 0);
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
        var cycleIter = Iterators.cycle(src);
        // 通过循环迭代器, 进行 10 次迭代 (List 集合只包含 5 个元素)
        for (var i = 0; i < 10; i++) {
            // 将迭代结果加入到目标数组
            dst.add(cycleIter.next());
        }
        // 确认目标集合包含 10 个元素
        then(dst).hasSize(10);
        then(dst).containsExactly(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
    }

    /**
     * 对迭代器进行过滤
     *
     * <p>
     * {@link Iterators#filter(java.util.Iterator, com.google.common.base.Predicate)
     * Iterators.filter(Iterator, Predicate)} 方法返回一个结果迭代器对象, 只包含所给迭代器中符合条件的元素值
     * </p>
     */
    @Test
    void filter_shouldFilterIterator() {
        // 产生一个集合
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 通过 List 对象产生一个循环迭代器对象
        var result = ImmutableList.copyOf(Iterators.filter(list.iterator(), n -> n % 2 == 0));
        then(result).containsExactly(2, 4);
    }

    /**
     * 通过迭代器, 统计指定元素出现的次数
     *
     * <p>
     * {@link Iterators#frequency(java.util.Iterator, Object) Iterators.frequency(Iterator, Object)} 方法用于将迭代过程中
     * 获得的元素逐一和目标对象进行比较, 并统计和目标对象相等的元素个数
     * </p>
     */
    @Test
    void frequency_shouldCountElementsByIterator() {
        // 定义集合元素
        var list = ImmutableList.of(1, 2, 2, 3, 4, 4, 4, 5);

        // 统计指定元素在迭代过程中出现的次数
        var count = Iterators.frequency(list.iterator(), 4);
        then(count).isEqualTo(3);
    }

    /**
     * 通过迭代器, 将得到的元素按顺序平均分配到指定数量的其它集合中
     *
     * <p>
     * {@link Iterators#partition(java.util.Iterator, int) Iterators.partition(Iterator, int)} 方法遍历迭代器, 并将得到的
     * 元素按顺序存入到另外的指定数量的集合中. 例如: 将集合 {@code [1, 2, 3]} 分到两个数组中, 结果为 {@code [[1, 2], [3]]}
     * </p>
     *
     * <p>
     * {@link Iterators#paddedPartition(java.util.Iterator, int) Iterators.paddedPartition(Iterator, int)} 方法与
     * {@link Iterators#partition(java.util.Iterator, int) Iterators.partition(Iterator, int)} 方法基本类似, 但要求结果中
     * 每个数组的元素个数都是一致的, 如果元素数量不足, 则以 {@code null} 填充. 例如: 将集合 {@code [1, 2, 3]} 分到两个数组中, 结果为
     * {@code [[1, 2], [3, null]]}
     * </p>
     */
    @Test
    void partition_shouldSplitIteratorIntoPartitions() {
        // 定义集合
        var list = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8);

        // 通过集合迭代器, 将元素存储到另外 3 个集合中
        var result = ImmutableList.copyOf(Iterators.partition(list.iterator(), 3));
        // 确认所得结果是一个数量为 3 的集合
        then(result).hasSize(3);
        // 确认结果中每个集合都按顺序平均分配到了若干元素
        then(result.get(0)).containsExactly(1, 2, 3);
        then(result.get(1)).containsExactly(4, 5, 6);
        then(result.get(2)).containsExactly(7, 8);

        // 通过集合迭代器, 将元素存储到另外 3 个集合中
        result = ImmutableList.copyOf(Iterators.paddedPartition(list.iterator(), 3));
        // 确认所得结果是一个数量为 3 的集合
        then(result).hasSize(3);
        // 确认结果中每个集合元素数量一致, 空缺的位置使用 null 填充
        then(result.get(0)).containsExactly(1, 2, 3);
        then(result.get(1)).containsExactly(4, 5, 6);
        then(result.get(2)).containsExactly(7, 8, null);
    }

    /**
     * 测试从迭代器中获取指定元素值
     *
     * <p>
     * {@link Iterators#getNext(java.util.Iterator, Object) Iterators.getNext(Iterator, Object)} 方法获取迭代器的下一个元素
     * 值, 如果迭代器已迭代完毕, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterators#getLast(java.util.Iterator, Object) Iterators.getLast(Iterator, Object)} 方法获取迭代器的最后一个元
     * 素, 如果迭代器已迭代完毕, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterators#get(java.util.Iterator, int, Object) Iterators.get(Iterator, int, Object)} 方法获取迭代指定次数后的
     * 元素值, 如果迭代器在指定次数前已经迭代完毕, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterators#getOnlyElement(java.util.Iterator, Object) Iterators.getOnlyElement(Iterator, Object)} 方法获取
     * 迭代器中唯一的元素, 即迭代器只包含一个元素时, 返回该元素值; 如果迭代器不包含元素, 则返回默认值; 如果迭代器包含多个元素, 则抛出异常
     * </p>
     */
    @Test
    void getElement_shouldGetNeededElementFromIterator() {
        // 创建一个空集合
        var emptyList = ImmutableList.of();

        // 确认迭代器已迭代完毕时, 获取下一个元素返回默认值 null
        var result = Iterators.getNext(emptyList.iterator(), null);
        then(result).isNull();

        // 确认迭代器已迭代完毕时, 通过迭代器获取最后一个元素返回默认值 null
        result = Iterators.getLast(emptyList.iterator(), null);
        then(result).isNull();

        // 确认迭代器已迭代完毕时, 通过迭代器获取指定迭代次数的元素返回默认值 null
        result = Iterators.get(emptyList.iterator(), 1, null);
        then(result).isNull();

        // 确认迭代器已迭代完毕时, 通过迭代器获取唯一元素时返回默认值 null
        result = Iterators.getOnlyElement(emptyList.iterator(), null);
        then(result).isNull();

        // 创建一个非空且元素数量多于 1 的集合
        var list = ImmutableList.of(1, 2, 3);

        // 确认通过迭代器获取到下一个元素值
        result = Iterators.getNext(list.iterator(), null);
        then(result).isEqualTo(1);

        // 确认通过迭代器获取到最后一个元素值
        result = Iterators.getLast(list.iterator(), null);
        then(result).isEqualTo(3);

        // 确认通过迭代器获取到指定迭代次数后的元素值
        result = Iterators.get(list.iterator(), 1, null);
        then(result).isEqualTo(2);

        // 因迭代器包含多个元素, 获取唯一元素时抛出异常
        thenThrownBy(() -> Iterators.getOnlyElement(list.iterator(), null))
                .isInstanceOf(IllegalArgumentException.class);

        // 定义一个只有单一元素的集合
        var singleToneList = ImmutableList.of(1);
        // 确认通过迭代器可以获取唯一元素
        result = Iterators.getOnlyElement(singleToneList.iterator(), null);
        then(result).isEqualTo(1);
    }
}
