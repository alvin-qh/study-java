package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * 测试 {@link Iterables} 工具类
 *
 * <p>
 * {@link Iterables} 工具类提供了一组对 {@link Iterable} 可迭代接口对象进行操作的辅助方法
 * </p>
 */
class IterableUtilsTest {
    /**
     * 将一个可迭代对象内的元素添加到另一个集合中
     *
     * <p>
     * {@link Iterables#addAll(java.util.Collection, Iterable) Iterables.addAll(Collection, Iterable)} 方法用于将一个可迭代
     * 对象包含的元素添加到另一个集合中
     * </p>
     */
    @Test
    void addAll_shouldAddElementsFromIterableObjectIntoCollection() {
        // 原集合对象
        var src = ImmutableList.of(100, 200, 300);
        // 目标集合对象
        var dst = Lists.newArrayList(1, 2, 3);

        // 将可迭代对象包含的元素添加到目标集合中
        var isAdded = Iterables.addAll(dst, src);
        // 确认添加成功
        then(isAdded).isTrue();

        // 确认添加后的集合元素
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
        var isMatched = Iterables.all(list, val -> val > 0);
        then(isMatched).isTrue();

        // 确认可迭代对象中, 不是所有元素均符合"偶数"这一条件
        isMatched = Iterables.all(list, val -> val % 2 == 0);
        then(isMatched).isFalse();
    }

    /**
     * 确认一个迭代器迭代得到的任一元素满足指定条件
     *
     * <p>
     * {@link Iterables#any(Iterable, com.google.common.base.Predicate) Iterables.any(Iterable, Predicate)} 方法用于判断
     * 可迭代对象中至少有一个元素符合指定条件
     * </p>
     */
    @Test
    void any_shouldCheckAnyElementsMatchedCondition() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 确认可迭代对象中至少有一个元素均符合"偶数"这一条件
        var isMatched = Iterables.any(list, val -> val % 2 == 0);
        then(isMatched).isTrue();

        // 确认可迭代对象中至少有一个元素均符合"偶数"这一条件
        isMatched = Iterables.any(list, val -> val < 0);
        then(isMatched).isFalse();
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
        var iterable = Iterables.concat(list1, list2);
        // 确认合并后的可迭代对象包含两个集合中的所有元素
        then(iterable).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
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
    @SuppressWarnings("null")
    @Test
    void consumingIterable_shouldReadAndRemoveElementsByIterable() {
        {
            // 定义集合对象
            var list = Lists.newArrayList(1, 2, 3, 4, 5);
            // 从集合对象中获取迭代器, 并包装为 FluentIterable 类型迭代器
            var iterable = Iterables.consumingIterable(list);
            // 遍历可迭代对象, 确认包含原集合中所有元素
            then(iterable).containsExactly(1, 2, 3, 4, 5);
            // 确认迭代完成后, 对应集合内容被清空
            then(list).isEmpty();
        }
        {
            // 定义不可变集合对象
            var list = ImmutableList.of(1, 2, 3, 4, 5);
            // 将可迭代对象包装为 FluentIterable 类型
            var iterable = Iterables.consumingIterable(list);
            // 因不可变集合的迭代过程不支持 remove 操作, 故抛出异常
            thenThrownBy(() -> Iterables.getFirst(iterable, null)).isInstanceOf(UnsupportedOperationException.class);
        }
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

        var isIn = Iterables.contains(list, 3);
        then(isIn).isTrue();

        isIn = Iterables.contains(list, 0);
        then(isIn).isFalse();
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

        // 通过 List 对象产生一个循环迭代器对象
        var iterable = Iterables.cycle(src);

        // 通过 limit 方法创建一个限制迭代 10 次的可迭代对象
        var limitedIterable = Iterables.limit(iterable, 10);
        // 确认目标集合包含 10 个元素
        then(limitedIterable).hasSize(10);
        then(limitedIterable).containsExactly(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
    }

    /**
     * 对可迭代对象进行过滤
     *
     * <p>
     * {@link Iterables#filter(Iterable, com.google.common.base.Predicate)
     * Iterables.filter(Iterable, Predicate)} 方法返回一个可迭代对象, 只包含所给可迭代对象中符合条件的元素值
     * </p>
     */
    @Test
    void filter_shouldFilterIterable() {
        // 产生一个集合
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 通过 List 对象产生一个循环迭代器对象
        var iterable = Iterables.filter(list, n -> n % 2 == 0);
        then(iterable).containsExactly(2, 4);
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
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 2, 3, 4, 4, 4, 5);

        // 统计指定元素在迭代过程中出现的次数
        var count = Iterables.frequency(list, 4);
        then(count).isEqualTo(3);
    }

    /**
     * 从可迭代对象中查找符合条件的第一个元素
     *
     * <p>
     * {@link Iterables#find(Iterable, com.google.common.base.Predicate, Object)
     * Iterables.find(Iterable, Predicate, Object)} 方法遍历可迭代对象, 并找到第一个符合条件的元素, 如果可迭代对象中不包含符合
     * 条件的元素, 则返回所给的默认值
     * </p>
     *
     * <p>
     * {@link Iterables#tryFind(Iterable, com.google.common.base.Predicate) Iterables.tryFind(Iterable, Predicate)}
     * 和 {@code find} 方法类似, 返回一个 {@link com.google.common.base.Optional Optional} 类型对象作为结果, 该对象存储
     * 查找到的元素或为空
     * </p>
     */
    @SuppressWarnings("null")
    @Test
    void find_shouldFindElementFromIterableObject() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 2, 3, 4, 4, 4, 5);

        // 查找第一个出现的"偶数"元素, 确认结果为 2
        var element = Iterables.find(list, n -> n % 2 == 0, null);
        then(element).isEqualTo(2);

        // 确认当所有元素都不满足条件时, 返回所给的默认值
        element = Iterables.find(list, n -> n == 0, null);
        then(element).isNull();

        // 查找第一个出现的"偶数"元素, 确认结果为 2
        var optional = Iterables.tryFind(list, n -> n % 2 == 0);
        then(optional.get()).isEqualTo(2);

        // 确认当所有元素都不满足条件时, 返回空的 Optional 对象
        optional = Iterables.tryFind(list, n -> n == 0);
        then(optional.isPresent()).isFalse();
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
        var iterable = Iterables.partition(list, 3);
        then(iterable).hasSize(3);
        // 确认结果中每个集合都按顺序平均分配到了若干元素
        then(Iterables.get(iterable, 0)).containsExactly(1, 2, 3);
        then(Iterables.get(iterable, 1)).containsExactly(4, 5, 6);
        then(Iterables.get(iterable, 2)).containsExactly(7, 8);

        // 通过集合迭代器, 将元素存储到另外 3 个集合中
        iterable = Iterables.paddedPartition(list, 3);
        // 确认所得结果是一个数量为 3 的集合
        then(iterable).hasSize(3);
        // 确认结果中每个集合元素数量一致, 空缺的位置使用 null 填充
        then(Iterables.get(iterable, 0)).containsExactly(1, 2, 3);
        then(Iterables.get(iterable, 1)).containsExactly(4, 5, 6);
        then(Iterables.get(iterable, 2)).containsExactly(7, 8, null);
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
    @SuppressWarnings("null")
    @Test
    void getElement_shouldGetFirstElementFromIterables() {
        {
            // 创建一个空集合
            var list = ImmutableList.of();

            // 确认可迭代对象为空时, 获取第一个元素返回默认值 null
            var element = Iterables.getFirst(list, null);
            then(element).isNull();

            // 确认可迭代对象为空时, 获取最后一个元素返回默认值 null
            element = Iterables.getLast(list, null);
            then(element).isNull();

            // 确认可迭代对象为空时, 获取指定迭代次数的元素返回默认值 null
            element = Iterables.get(list, 1, null);
            then(element).isNull();

            // 确认可迭代对象为空时, 获取唯一元素时返回默认值 null
            element = Iterables.getOnlyElement(list, null);
            then(element).isNull();
        }

        {
            // 创建一个非空且元素数量多于 1 的集合
            var list = ImmutableList.of(1, 2, 3);

            // 确认获取到可迭代对象的第一个元素值
            var element = Iterables.getFirst(list, null);
            then(element).isEqualTo(1);

            // 确认获取到可迭代对象的最后一个元素值
            element = Iterables.getLast(list, null);
            then(element).isEqualTo(3);

            // 确认获取到可迭代对象指定迭代次数后的元素值
            element = Iterables.get(list, 1, null);
            then(element).isEqualTo(2);

            // 因可迭代对象包含多个元素, 获取唯一元素时抛出异常
            thenThrownBy(() -> Iterables.getOnlyElement(list, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        {
            // 定义一个只有单一元素的集合
            var singleToneList = ImmutableList.of(1);

            // 确认通过可迭代对象可以获取唯一元素
            var element = Iterables.getOnlyElement(singleToneList, null);
            then(element).isEqualTo(1);
        }
    }

    /**
     * 查找可迭代对象中符合条件的第一个元素的下标索引
     *
     * <p>
     * {@link Iterables#indexOf(Iterable, com.google.common.base.Predicate) Iterables.indexOf(Iterable, Predicate)}
     * 方法遍历迭代器, 并找到第一个符合条件的元素并返回其索引, 如果迭代器中不包含符合条件的元素, 则返回 {@code -1}
     * </p>
     */
    @Test
    void indexOf_shouldGetIndexOfElementFromIterable() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 2, 3, 4, 4, 4, 5);

        // 查找第一个出现的"偶数"元素, 确认其索引为 2
        var index = Iterables.indexOf(list, n -> n % 2 == 0);
        then(index).isEqualTo(1);

        // 确认当所有元素都不满足条件时, 返回 -1 表示元素不存在
        index = Iterables.indexOf(list, n -> n == 0);
        then(index).isEqualTo(-1);
    }

    /**
     * 在原可迭代对象基础上创建一个可迭代对象, 可以在迭代过程中把原迭代器元素进行转换
     *
     * <p>
     * {@link Iterables#transform(Iterable, com.google.common.base.Function) Iterables.transform(Iterable, Function)}
     * 方法包装一个可迭代对象, 返回的可迭代对象, 新对象在迭代过程中可以将原对象得到的元素进行转换后返回
     * </p>
     */
    @Test
    void transform_shouldTransformElementsFromIterable() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 从 list 迭代器中创建新迭代器, 将整数转换为字符串
        var iterable = Iterables.transform(list, n -> String.format("num_%d", n));
        // 确认通过新迭代器迭代得到的是转换后的字符串结果
        then(iterable).containsExactly("num_1", "num_2", "num_3", "num_4", "num_5");
    }

    /**
     * 在一个可迭代对象上跳过指定的位置, 将迭代器定位到后续元素位置上
     *
     * <p>
     * {@link Iterables#skip(Iterable, int)} 方法将一个可迭代对象跳过指定位置, 返回一个新的可迭代对象, 从跳过位置之后作为起始的迭代位置
     * </p>
     */
    @Test
    void skip_shouldSkipSomePositions() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 将可迭代对象跳过 2 个位置 (即将迭代起始位置设置到从 3 开始)
        var iterable = Iterables.skip(list, 2);
        // 确认可迭代对象从 第 3 个位置开始迭代
        then(iterable).containsExactly(3, 4, 5);
    }

    /**
     * 限制迭代器的迭代次数
     *
     * <p>
     * {@link Iterables#limit(Iterable, int) Iterables.limit(Iterable, int)} 方法包装一个可迭代对象, 返回新的可迭代对象,
     * 且返回的新可迭代对象允许被迭代的次数被限制在指定次数内
     * </p>
     */
    @Test
    void limit_shouldLimitIterateTimesOfIterableObject() {
        // 定义集合对象
        var list = ImmutableList.of(1, 2, 3, 4, 5);

        // 从 list 迭代器中创建新可迭代对象, 并限制迭代次数
        var iterable = Iterables.limit(list, 3);
        // 确认得到的可迭代对象只能迭代指定次数
        then(iterable).containsExactly(1, 2, 3);
    }

    /**
     * 对多个迭代器内容进行归并排序
     *
     * <p>
     * {@link Iterables#mergeSorted(Iterable, java.util.Comparator) Iterables.limit(Iterable, Comparator)} 方法对多个
     * 可迭代对象的内容进行归并排序, 得到一个存储归并结果且内容有序的可迭代对象
     * </p>
     *
     * <p>
     * 注意: {@code mergeSorted} 方法本身不对输入的可迭代对象排序, 所以要保证输入的每个可迭代对象内部元素有序, 且排序规则要和后面进行归并
     * 排序的规则保持一致
     * </p>
     */
    @Test
    void mergeSorted_shouldMergeIterablesAndSorted() {
        // 定义排序对象
        var ordering = Ordering.<Integer>natural().reversed();

        // 定义集合对象
        // 注意, 对于归并排序的输入集合, 每个集合本身需要有序, 且排序的比较规则要和最后归并时一致
        var list1 = ImmutableList.of(8, 6, 4, 3, 2);
        var list2 = ImmutableList.of(10, 9, 7, 5, 1);

        // 对多个可迭代对象内容进行归并排序
        var iterable = Iterables.mergeSorted(ImmutableList.of(list1, list2), ordering);
        // 确认多个可迭代对象内容归并到一起且有序
        then(iterable).containsExactly(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
    }

    /**
     * 查看一个可迭代对象是否为空 (即不包含任何元素)
     *
     * <p>
     * {@link Iterables#isEmpty(Iterable)} 用于判断一个可迭代对象是否为空
     * </p>
     */
    @Test
    void isEmpty_shouldCheckIterableObjectIfEmpty() {
        // 定义一个空集合
        var list = ImmutableList.of();
        // 确认可迭代对象为空
        then(Iterables.isEmpty(list)).isTrue();

        // 定义一个非空集合
        list = ImmutableList.of(1, 2, 3);
        // 确认可迭代对象不为空
        then(Iterables.isEmpty(list)).isFalse();
    }

    /**
     * 确认两个可迭代对象的元素完全相等
     *
     * <p>
     * {@link Iterables#elementsEqual(Iterable, Iterable)} 用于判断两个可迭代对象包含的元素完全相等, 即: 数量一致,
     * 且对应位置的元素相等
     * </p>
     */
    @Test
    void elementsEqual_shouldCheckAllElementsAreEqual() {
        // 定义两个集合
        var list1 = ImmutableList.of(1, 2, 3);
        var list2 = ImmutableList.of(1, 2, 3);

        // 确认两个可迭代对象对应的元素值相等
        then(Iterables.elementsEqual(list1, list2)).isTrue();

        // 修改其中的一个集合
        list2 = ImmutableList.<Integer>builder().addAll(list2).add(4).build();

        // 确认两个可迭代对象对应的元素不相等
        then(Iterables.elementsEqual(list1, list2)).isFalse();
    }
}
