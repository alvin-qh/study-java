package alvin.study.guava.ordering;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.junit.jupiter.api.Test;

/**
 * 测试排序对象
 *
 * <p>
 * {@link Ordering} 类提供了一系列和排序相关的方法, 其对象既可以用于
 * {@link Collections#sort(List, java.util.Comparator)
 * Collections.sort(List, Comparator)} 方法
 * (或 {@link List#sort(java.util.Comparator) List.sort(Comparator)} 方法)
 * 进行排序, 也可以通过 {@link Ordering#isOrdered(Iterable)} 方法验证集合是否有序
 * </p>
 *
 * <p>
 * {@link ComparisonChain#start()} 方法用于返回一个 {@link ComparisonChain}
 * 对象, 用于通过多个比较方法组织一个比较链, 规则为: 链条上优先比较的结果如果非
 * {@code 0}, 则整体返回该结果, 否则执行链条上下一级的比较方法. 比较时可以使用
 * {@link java.util.Comparator} 对象或 {@link Ordering} 对象指定比较规则
 * </p>
 */
class OrderingTest {
    /**
     * 测试自然序的排序对象
     *
     * <p>
     * 通过 {@link Ordering#natural()} 方法创建一个自然序的排序对象
     * </p>
     *
     * <p>
     * 得到的 {@link Ordering} 排序对象可以用于
     * {@link Collections#sort(List, java.util.Comparator) Collections.sort(List, Comparator)}
     * 方法以及 {@link List#sort(java.util.Comparator) List#sort(Comparator)} 等排序
     * 方法
     * </p>
     */
    @Test
    void natural_shouldSortList() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 确认排序结果
        then(list).containsExactly(1, 2, 3, 4, 5).isSorted();
    }

    /**
     * 进行一次无效排序
     *
     * <p>
     * {@link Ordering#allEqual()} 排序对象内部在比较任何两个对象值时都返回相等,
     * 通过这种规则排序集合, 排序前后的元素排列不发生变化
     * </p>
     */
    @Test
    void allEqual_shouldKeepOriginalOrder() {
        // 通过 allEqual 方法产生一个无效排序对象
        var ordering = Ordering.allEqual();

        // 对集合进行排序
        var list = Lists.newArrayList(1, 3, 2, 5, 4);
        Collections.sort(list, ordering);

        // 确认排序结果, 元素顺序未发生变化
        then(list).containsExactly(1, 3, 2, 5, 4);
    }

    /**
     * 在排序结果中让 {@code null} 值在最前
     *
     * <p>
     * 通过 {@link Ordering#nullsFirst()} 方法创建的排序对象, 可以在排序结果中令
     * {@code null} 值在最前面
     * </p>
     *
     * <p>
     * 根据比较规则, 在这种情况下, {@code null} 值被认为时最小值
     * </p>
     */
    @Test
    void nullsFirst_shouldPutNullValueAtFirstOfResult() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        // 通过 nullFirst 方法要求将集合内的 null 元素放在排序结果最前面
        var ordering = Ordering.natural().nullsFirst();

        // 对集合进行排序
        var list = Lists.newArrayList(1, null, 3, 2, 5, 4, null);
        Collections.sort(list, ordering);

        // 确认排序结果, null 值在最前面, 即最小值为 null
        then(list).containsExactly(null, null, 1, 2, 3, 4, 5);
    }

    /**
     * 在排序结果中让 {@code null} 值在最后
     *
     * <p>
     * 通过 {@link Ordering#nullsLast()} 方法创建的排序对象, 可以在排序结果中令
     * {@code null} 值在最后面
     * </p>
     *
     * <p>
     * 根据比较规则, 在这种情况下, {@code null} 值被认为时最大值
     * </p>
     */
    @Test
    void nullsLast_shouldPutNullValueAtLastOfResult() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        // 通过 nullFirst 方法要求将集合内的 null 元素放在排序结果最后面
        var ordering = Ordering.natural().nullsLast();

        // 对集合进行排序
        var list = Lists.newArrayList(1, null, 3, 2, 5, 4, null);
        Collections.sort(list, ordering);

        // 确认排序结果, null 值在最后面, 即最大值为 null
        then(list).containsExactly(1, 2, 3, 4, 5, null, null);
    }

    /**
     * 改变现有的排序规则为逆序
     *
     * <p>
     * {@link Ordering#reverse()} 方法可以将一个 {@link Ordering}
     * 对象的排序规则改为逆序
     * </p>
     */
    @Test
    void reverse_shouldSortListOrderByReverse() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural().reversed();

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 确认排序结果为逆序
        then(list).containsExactly(5, 4, 3, 2, 1);
    }

    /**
     * 判断一个集合是否在指定的排序对象上有序
     *
     * <p>
     * 通过 {@link Ordering#isOrdered(Iterable)} 方法可以判断集合对于该排序对象是否有序,
     * 是否有序的规则是由该排序对象的比较方法来确定的
     * </p>
     *
     * <p>
     * 通过 {@link Ordering#isStrictlyOrdered(Iterable)} 为严格判断集合有序性,
     * 即集合中不能包含相等的元素
     * </p>
     */
    @Test
    void isOrdered_shouldCheckListIfOrdered() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();

        // 创建一个有序集合
        var list = Lists.newArrayList(1, 2, 3, 4, 5);

        // 确认集合有序 (集合的后一个元素大于或等于前一个元素)
        then(ordering.isOrdered(list)).isTrue();

        // 确认集合严格有序 (集合的后一个元素大于前一个元素)
        then(ordering.isStrictlyOrdered(list)).isTrue();

        // 在集合后添加一个元素, 和集合原本最后一个元素相等, 此时集合具备了两个相同元素
        list.add(5);

        // 确认具备相同元素的集合仍有序
        then(ordering.isOrdered(list)).isTrue();

        // 确认具备相同后, 不能算严格有序
        then(ordering.isStrictlyOrdered(list)).isFalse();

        // 在集合中加入元素, 破坏其有序性
        list.add(0);

        // 确认集合为无序集合
        then(ordering.isOrdered(list)).isFalse();
    }

    /**
     * 根据排序规则获取集合中最大 (或最小) 的若干值
     *
     * <p>
     * 通过 {@link Ordering#greatestOf(Iterable, int)} 方法可以从一个集合中获取最大的若干值,
     * 通过 {@link Ordering#leastOf(Iterable, int)} 方法可以从集合中获取最小的若干值
     * </p>
     *
     * <p>
     * 所谓"最大"和"最小", 都是相对于 {@link Ordering} 对象而言的, 即基于其排序规则定义的最大最小值,
     * 简言之, 就是通过该 {@link Ordering} 对象对集合排序后, 集合中位于前面的值被认为是较小值,
     * 位于后面的值被认为较大
     * </p>
     */
    @Test
    void greatestOf_leastOf_shouldGetValuesByOrdering() {
        // 创建一个有序集合
        var list = Lists.newArrayList(3, 1, 2, 5, 4);

        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();

        // 获取最小的 2 个值, 返回一个集合
        then(ordering.leastOf(list, 2)).containsExactly(1, 2);

        // 获取最大的 2 个值, 返回一个集合
        then(ordering.greatestOf(list, 2)).containsExactly(5, 4);

        // 将排序规则反转
        ordering = ordering.reverse();

        // 获取最小的 2 个值, 返回一个集合
        then(ordering.leastOf(list, 2)).containsExactly(5, 4);

        // 获取最大的 2 个值, 返回一个集合
        then(ordering.greatestOf(list, 2)).containsExactly(1, 2);
    }

    /**
     * 根据排序规则计算几个值中最大 (最小) 的值
     *
     * <p>
     * 通过 {@link Ordering#max(Object, Object, Object, Object...)}
     * 方法可以从若干值中计算最大值若干值, 通过
     * {@link Ordering#min(Object, Object, Object, Object...)}
     * 方法可以从集合中获取最小的若干值
     * </p>
     *
     * <p>
     * 所谓"最大"和"最小", 都是相对于 {@link Ordering} 对象而言的, 即基于其排序规则定义的最大最小值,
     * 简言之, 就是通过该 {@link Ordering} 对象对集合排序后, 集合中位于前面的值被认为是较小值,
     * 位于后面的值被认为较大
     * </p>
     */
    @Test
    void min_max_shouldFindMinOrMaxValueByOrdering() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();

        // 通过定义的排序对象对从指定的几个值中找到最小值
        then(ordering.min(10, 20)).isEqualTo(10);

        // 通过定义的排序对象对从指定的几个值中找到最大值
        then(ordering.max(10, 20)).isEqualTo(20);

        ordering = ordering.reverse();

        // 通过定义的排序对象对从指定的几个值中找到最小值
        then(ordering.min(10, 20)).isEqualTo(20);

        // 通过定义的排序对象对从指定的几个值中找到最大值
        then(ordering.max(10, 20)).isEqualTo(10);

        // 定义一个基于集合顺序的排序对象
        ordering = Ordering.explicit(ImmutableList.of(20, 10, 15));

        // 通过定义的排序对象对从指定的几个值中找到最小值
        then(ordering.min(10, 15, 20)).isEqualTo(20);
        then(ordering.max(10, 15, 20)).isEqualTo(15);
    }

    /**
     * 从原集合中拷贝一份有序的集合副本
     *
     * <p>
     * 使用 {@link Collections#sort(List, java.util.Comparator)
     * Collections.sort(List, Comparator)} 或者 {@link List#sort(java.util.Comparator)
     * List.sort(Comparator)} 方法均是在现有集合上进行排序, 即会改变现有集合
     * </p>
     *
     * <p>
     * 如果希望排序后仍能保留原始集合, 可以将原集合 copy 一份后对副本进行排序,
     * {@link Ordering#sortedCopy(Iterable)} 方法就是上述操作的简化方法
     * </p>
     */
    @Test
    void copy_shouldCopyListToOrdered() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();

        // 创建一个集合
        var list = Lists.newArrayList(2, 1, 3, 5, 4);

        // 将所给 List 集合对象拷贝为一个有序的新 List 集合对象
        var orderedList = ordering.sortedCopy(list);

        // 确认拷贝结果
        then(orderedList).containsExactly(1, 2, 3, 4, 5).isSorted();

        // 确认原集合不受影响
        then(list).containsExactly(2, 1, 3, 5, 4);
    }

    /**
     * 对 mapping 后的结果进行排序
     *
     * <p>
     * 某些时候, 集合元素的顺序并不是由集合元素本身的值来决定, 而是将元素值进行一次转换,
     * 基于转换后的结果进行排序
     * </p>
     *
     * <p>
     * {@link Ordering#onResultOf(com.google.common.base.Function)
     * Ordering.onResultOf(Function)} 方法用于对待排序的元素进行 一次 mapping,
     * 并基于 mapping 的结果对元素进行排序
     * </p>
     */
    @Test
    void onResultOf_shouldSortListByMappedValue() {
        // 通过一个 mapping 函数将集合元素转换后进行排序 (根据集合元素的余数进行排序)
        // Mapping 的规则为取元素除以 2 的余数, 通过余数进行排序
        var ordering = Ordering.natural().<Integer>onResultOf(n -> n % 2);

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 确认集合有序 (余数小的在前)
        then(list).containsExactly(2, 4, 1, 3, 5);

        // 确认在当前规则下集合有序
        then(ordering.isOrdered(list)).isTrue();
    }

    /**
     * 通过集合元素的 {@link Object#toString()} 结果进行排序
     *
     * <p>
     * 该方法相当于 {@code Ordering.natural().<Integer>onResultOf(n -> n.toString())}
     * 调用的简化形式
     * </p>
     */
    @Test
    void usingToString_shouldSortListByResultOfToStringMethod() {
        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象, 并进行逆序
        var ordering = Ordering.usingToString().reversed();

        // 产生一个整数集合
        var list = Lists.newArrayList(1, 2, 3, 10, 200, 300);

        // 通过元素的 toString 方法转为字符串, 并对字符串进行排序 (逆序)
        Collections.sort(list, ordering);

        // 确认排序结果, 由于是通过字符串进行比较哦, 所以结果和直接进行数值比较的结果差异较大
        then(list).containsExactly(300, 3, 200, 2, 10, 1);
    }

    /**
     * 自定义排序规则, 且采用两级排序规则
     *
     * <p>
     * {@link Ordering#from(java.util.Comparator) Ordering.from(Comparator)}
     * 可以根据一个比较规则建立排序对象
     * </p>
     *
     * <p>
     * 如果要对通过定义的比较规则得出相等结果的元素通过其它规则再做一次排序, 则可以通过
     * {@link Ordering#compound(java.util.Comparator) Ordering.compound(Comparator)}
     * 方法定义第二级排序规则
     * </p>
     *
     * <p>
     * 即通过第一级排序规则为不等的, 按照第一级规则得出大小结果, 如果第一级规则为相等的,
     * 通过第二级规则得出大小结果
     * </p>
     */
    @Test
    void compound_shouldSortListByTwoLevelOrderingRule() {
        // 设定两级排序规则
        // 所谓两级排序规则, 即在第一级排序规则的作用下, 如果两个元素相等,
        // 则使用第二级排序规则进行排序
        // 第一级排序按元素值的余数排序 (逆序), 第二级排序按元素值的大小排序 (自然序)
        var ordering
            = Ordering.<Integer>from((l, r) -> r % 2 - l % 2).compound(Comparator.comparingInt((Integer l) -> l));

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 确认排序结果, 余数小的在前, 且相同余数下, 元素值小的在前
        then(list).containsExactly(1, 3, 5, 2, 4);
    }

    /**
     * 定义更多层级的排序规则
     *
     * <p>
     * 如果两级排序仍无法满足需求, 则可以通过 {@link Ordering#compound(Iterable)} 方法,
     * 通过一个 {@link java.util.Comparator} 对象集合定义多级比较
     * </p>
     */
    @Test
    void compound_shouldSortListByMultiOrderingRule() {
        // 设定多级排序规则 (两级排序, 规则和前一个例子一致)
        var ordering = Ordering.<Integer>compound(ImmutableList.of(
            (l, r) -> r % 2 - l % 2,
            Comparator.comparingInt(l -> l)));

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 确认排序结果, 余数小的在前, 且相同余数下, 元素值小的在前
        then(list).containsExactly(1, 3, 5, 2, 4);
    }

    /**
     * 某些时候, 元素的大小比较规则是固定的, 即指定 A > B, 而不是通过某种算法来计算
     *
     * <p>
     * {@link Ordering#explicit(List)} 方法通过一个集合来指定固定的排序规则,
     * 规则为: 集合中前面的值小于后面的值
     * </p>
     *
     * <p>
     * 之后如果要为规则集合中任意元素组成的集合进行排序, 则排序结果会遵守规则集合中对元素大小的定义
     * </p>
     *
     * <p>
     * 这种排序方式, 无法对规则集合以外的元素值进行排序
     * </p>
     *
     * <p>
     * 注意: 规则集合中不能出现相同值的元素
     * </p>
     */
    @Test
    void explicit_shouldSortListByGivenOrdered() {
        // 根据一个给定的列表, 以列表元素的顺序作为排序依据, 得到一个排序对象
        // 注意: 该排序对象只能对所设定列表中存在的元素进行排序
        var ordering = Ordering.explicit(List.of(4, 2, 1, 3, 5, 0, 7, 6));

        // 对集合进行排序
        var list = Lists.newArrayList(2, 1, 3, 5, 4);
        Collections.sort(list, ordering);

        // 查看排序结果 (和设定的集合顺序一致)
        then(list).containsExactly(4, 2, 1, 3, 5);

        // 通过排序对象获取若干值的最大值 (比较结果和所给列表一致, 在列表中 1 在 4 之后, 1 较大)
        then(ordering.max(4, 1)).isEqualTo(1);

        // 通过排序对象获取若干值的最小值 (比较结果和所给列表一致, 在列表中 4 在 1 之前, 4 较小)
        then(ordering.min(4, 1)).isEqualTo(4);
    }

    /**
     * 进行一次伪随机排序
     *
     * <p>
     * {@link Ordering#arbitrary()} 方法通过为元素计算一个 UID,
     * 并通过该 UID 对元素进行排序, 所以排序结果无法预期
     * </p>
     *
     * <p>
     * 但该算法保证在同一个 JDK 进程中, 对相同元素的集合, 无论进行多少次此类排序,
     * 排序结果均为一致
     * </p>
     */
    @Test
    void arbitrary_shouldSortListArbitrary() {
        var ordering = Ordering.arbitrary();

        var list = Lists.newArrayList(2, 1, 3, 5, 4);

        // 进行一次任意顺序排序 (即排序结果不具备任何有意义的顺序), 且排序结果可复现
        // (即同一个集合的排序结果是无序且稳定的)
        var result1 = ordering.sortedCopy(list);

        // 确认在定义的 ordering 对象上, 排序结果有序
        then(ordering.isOrdered(result1)).isTrue();
        then(result1).containsExactlyInAnyOrderElementsOf(list);

        // 确认通过同一个排序对象, 对集合再次排序, 排序结果和前一次一致
        var result2 = ordering.sortedCopy(list);
        then(result2).containsExactlyElementsOf(result1);
    }

    /**
     * 测试通过比较链对对象进行比较
     *
     * <p>
     * 在对象比较大小时, 往往需要比较对象的多个属性. {@link ComparisonChain}
     * 类可以产生一个比较链, 以简化
     * {@link java.util.Comparator#compare(Object, Object)} 方法的重写步骤
     * </p>
     *
     * <p>
     * 比较链的基本规则是, 先按链条上优先的规则进行比较, 如果返回相等的结果,
     * 则再通过之后的规则进行比较, 直到返回非相等结果或链条上所有的规则都比较完毕,
     * 类似于如下语句:
     * </p>
     *
     * <pre>
     * var result = compare1(a, b);
     * if (result == 0) {
     *     result = compare2(c, d);
     *     if (result == 0) {
     *         result = compare3(a, d);
     *     }
     * }
     * return result;
     * </pre>
     */
    @Test
    void compare_shouldUseChainToCompareObjects() {
        var obj1 = Integer.valueOf(100);
        var obj2 = Integer.valueOf(100);
        var obj3 = Integer.valueOf(200);

        // 定义两级比较条件, 因为 obj1 和 obj2 相等, 所以结果为 obj2 和 obj3 的比较结果
        var r = ComparisonChain.start()
                .compare(obj1, obj2)
                .compare(obj2, obj3, Ordering.explicit(obj3, obj2))
                .result();

        // 确认最终结果为 obj2 和 obj3 比较的结果
        then(r).isEqualTo(1);
    }
}
