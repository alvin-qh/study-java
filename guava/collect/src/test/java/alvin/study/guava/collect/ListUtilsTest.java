package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * 演示 Guava 列表集合工具类
 *
 * <p>
 * {@link Lists} 类提供了一系列静态方法用于创建和操作 {@link ArrayList} 和 {@link LinkedList} 类型对象
 * </p>
 */
class ListUtilsTest {
    /**
     * 通过指定元素和数组创建 {@link java.util.List List} 集合对象
     *
     * <p>
     * 该方法有两个重载 {@link Lists#asList(Object, Object[])} 和 {@link Lists#asList(Object, Object, Object[])} 方法
     * </p>
     *
     * <p>
     * 该方法主要用途在于对不定参数进行处理, 例如某方法 {@code foo(String a, String...b)} 的参数, 就可以通过
     * {@link Lists#asList(Object, Object[])} 方法生成列表集合对象
     * </p>
     *
     * <p>
     * 返回列表集合对象为 {@link ImmutableList} 类型
     * </p>
     */
    @Test
    void asList_shouldCreateListObject() {
        {
            var list = Lists.asList(1, new Integer[]{ 2, 3, 4 });
            then(list).containsExactly(1, 2, 3, 4);
        }
        {
            var list = Lists.asList(1, 2, new Integer[]{ 3, 4 });
            then(list).containsExactly(1, 2, 3, 4);
        }
    }

    /**
     * 计算多个列表集合的笛卡尔积
     *
     * <p>
     * {@link Lists#cartesianProduct(java.util.List...) Lists.cartesianProduct(List...)} 方法用于计算多个集合的笛卡尔积, 例如:
     * <code>{[1, 2], [10, 20, 30]} => {[1, 10], [1, 20], [1, 30], [2, 10], [2, 20], [2, 30]}</code>
     * </p>
     */
    @Test
    void cartesianProduct_shouldBuildCartesianProductOfSeveralLists() {
        // 计算多个列表集合的笛卡尔积
        var list = Lists.cartesianProduct(
                ImmutableList.of(1, 2),
                ImmutableList.of(11, 22, 33),
                ImmutableList.of(111, 222, 333));

        // 确认笛卡尔积结果
        then(list).containsExactly(
                ImmutableList.of(1, 11, 111),
                ImmutableList.of(1, 11, 222),
                ImmutableList.of(1, 11, 333),
                ImmutableList.of(1, 22, 111),
                ImmutableList.of(1, 22, 222),
                ImmutableList.of(1, 22, 333),
                ImmutableList.of(1, 33, 111),
                ImmutableList.of(1, 33, 222),
                ImmutableList.of(1, 33, 333),
                ImmutableList.of(2, 11, 111),
                ImmutableList.of(2, 11, 222),
                ImmutableList.of(2, 11, 333),
                ImmutableList.of(2, 22, 111),
                ImmutableList.of(2, 22, 222),
                ImmutableList.of(2, 22, 333),
                ImmutableList.of(2, 33, 111),
                ImmutableList.of(2, 33, 222),
                ImmutableList.of(2, 33, 333));
    }

    /**
     * 将一个字符序列 (或字符串) 转为字符元素的 {@link java.util.List List} 对象
     *
     * <p>
     * 将 {@link Lists#charactersOf(CharSequence)} 或 {@link Lists#charactersOf(String)} 方法将字符序列或字符串转为字符集合
     * </p>
     *
     * <p>
     * 返回列表集合对象为 {@link ImmutableList} 类型
     * </p>
     */
    @Test
    void charactersOf_shouldSplitCharSequenceIntoCharList() {
        // 将字符序列转为字符元素的列表集合
        var chars = Lists.charactersOf("Hello World");

        // 确认集合元素值
        then(chars).containsExactly('H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd');
    }

    /**
     * 创建 {@link java.util.List List} 集合对象
     *
     * <p>
     * 创建 {@link ArrayList} 集合对象
     * <ul>
     * <li>
     * 通过 {@link {@link Lists#newArrayList()} 方法可以创建一个 {@link ArrayList} 集合对象. 该方法有一系列重载,
     * 通过不同类型参数构建 {@link ArrayList} 集合对象, 包括:
     * <ul>
     * <li>
     * {@link Lists#newArrayList()}, 构建一个空的 {@link ArrayList} 集合对象, 相当于直接执行 {@code new ArrayList()}.
     * 注意: 如果只是为了得到一个空集合且不会修改它, 则应该使用 {@link ImmutableList#of()} 方法, 或在 JDK 9 之后使用
     * {@link java.util.List#of() List.of()} 方法
     * </li>
     * <li>
     * {@link Lists#newArrayList(Object...) Lists.newArrayList(T...)}, 通过一系列元素值构建 {@link ArrayList} 集合对象
     * </li>
     * <li>
     * {@link Lists#newArrayList(Iterable) Lists.newArrayList(Iterable)}, 将一个可迭代对象通过迭代获取元素, 构建
     * {@link ArrayList} 集合对象
     * </li>
     * <li>
     * {@link Lists#newArrayList(java.util.Iterator) Lists.newArrayList(Iterator)}, 将一个迭代器对象通过迭代获取元素, 构建
     * {@link ArrayList} 集合对象
     * </li>
     * </ul>
     * </li>
     * <li>
     * 除上述重载方法外, Guava 还提供了和集合元素个数设定相关的几个方法, 可以提高代码执行效率, 包括:
     * <ul>
     * <li>
     * {@link Lists#newArrayListWithCapacity(int)} 方法用于产生一个空 {@link ArrayList} 集合对象, 并设置预期的 {@code capacity}
     * 属性, 该集合对象可以在元素数量到达预期值前, 避免内存重新分配. 注意, 该方法已标记为过期, 应该直接使用 {@code new ArrayList(int)}
     * 方法, 通过参数设置 {@code capacity} 值
     * </li>
     * <li>
     * {@link Lists#newArrayListWithExpectedSize(int)} 方法用于产生一个空 {@link ArrayList} 集合对象, 并设置预期元素个数,
     * 并通过预期元素个数计算应该设置的 {@code capacity} 值, 这样即便集合中的元素个数达到预期值, 继续添加元素也不会立即引发内存重分配.
     * 注意, 该方法已标记为过期, 应该直接使用 {@code new ArrayList(int)} 方法, 通过参数设置 {@code capacity} 值
     * </li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link CopyOnWriteArrayList} 对象
     * <ul>
     * <li>
     * 所谓 {@link CopyOnWriteArrayList}, 即一个 {@link java.util.List List} 类型集合, 和 {@link ArrayList} 类型的区别在于:
     * <ul>
     * <li>
     * {@link ArrayList} 通过一个数组存储元素, 且数组长度有冗余, 在数组被占满前, 元素都是添加到该数组中, 直到数组占满后,
     * 会重新分配更大的数组继续进行存储; 删除元素则直接在原数组上进行, 多出一个冗余的元素空间
     * </li>
     * <li>
     * {@link CopyOnWriteArrayList} 也是通过一个数组存储元素, 每次添加或删除元素, 都会在原数组的基础上创建一个新数组, 将元素添加
     * (或复制元素时忽略) 到新数组
     * </li>
     * </ul>
     * </li>
     * <li>
     * {@link CopyOnWriteArrayList} 会在操作数组元素时 (例如添加和删除元素) 加锁, 以保证线程安全性
     * </li>
     * <li>
     * {@link CopyOnWriteArrayList} 的宗旨是"读写分离", 即读的是集合中的原数组, 写的是从原数组中创建的新数组, 写完毕后, 再把原数组丢弃,
     * 换成新数组. 所以 {@link CopyOnWriteArrayList} 在读的时候无需加锁
     * </li>
     * <li>
     * {@link CopyOnWriteArrayList} 不能进行大规模写操作, 其效率非常低, 但一旦构建好, 即可进行高效率读操作; 一些特殊的情况 (必须写,
     * 但读多写少) 下, 可以使用该类型集合, 但更多时候, 应该使用 {@link ImmutableList} 来保证更好的效率和范式
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link LinkedList} 集合对象
     * <ul>
     * <li>
     * {@link Lists#newLinkedList()} 方法可以创建 {@link LinkedList} 类型集合对象, 和直接使用 {@link LinkedList} 构造器一致
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void new_shouldCreateList() {
        var iterable = ContiguousSet.closedOpen(0, 10).asList();

        // 构建 ArrayList 对象
        {
            // 构建空 ArrayList 集合对象
            var list = Lists.newArrayList();
            // 确认集合为空
            then(list).isInstanceOf(ArrayList.class).isEmpty();
        }
        {
            // 构建根据所给元素值构建 ArrayList 对象
            var list = Lists.newArrayList(1, 2, 3, 4);
            // 确认集合元素值
            then(list).isInstanceOf(ArrayList.class).containsExactly(1, 2, 3, 4);
        }
        {
            // 构建根据所给可迭代对象构建 ArrayList 对象
            var list = Lists.newArrayList(iterable);
            // 确认集合元素值
            then(list).isInstanceOf(ArrayList.class).containsExactlyElementsOf(iterable);
        }
        {
            // 构建根据所给迭代器对象构建 ArrayList 对象
            var list = Lists.newArrayList(iterable.iterator());
            // 确认集合元素值
            then(list).isInstanceOf(ArrayList.class).containsExactlyElementsOf(iterable);
        }
        {
            // 初始化 ArrayList 集合并设置其 capacity 值
            var list = Lists.newArrayListWithCapacity(5);

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);
            // 元素个数超出可能会影响到重新分配内存
            list.add(6);
            then(list).isInstanceOf(ArrayList.class).containsExactly(1, 2, 3, 4, 5, 6);
        }
        {
            // 初始化 ArrayList 集合并设置预期可能的元素个数
            var list = Lists.newArrayListWithExpectedSize(5);

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);
            // 元素个数超出并不会立即导致内存重分配
            list.add(6);
            then(list).isInstanceOf(ArrayList.class).containsExactly(1, 2, 3, 4, 5, 6);
        }

        // 构建 CopyOnWriteArrayList 集合对象
        {
            // 创建一个空的集合对象
            var list = Lists.newCopyOnWriteArrayList();

            // 添加元素
            list.add(1);
            list.add(2);
            list.add(3);
            // 确认集合中的元素值
            then(list).containsExactly(1, 2, 3);
        }
        {
            // 通过一个可迭代对象内容初始化集合对象
            var list = Lists.newCopyOnWriteArrayList(iterable);

            // 确认集合中的元素值
            then(list).isInstanceOf(CopyOnWriteArrayList.class).containsExactlyElementsOf(iterable);
        }

        // 构建 LinkedList 集合对象
        {
            // 构建空 LinkedList 集合对象
            var list = Lists.newLinkedList();
            // 确认集合为空
            then(list).isInstanceOf(LinkedList.class).isEmpty();
        }
        {
            // 构建根据所给可迭代对象构建 LinkedList 对象
            var list = Lists.newLinkedList(iterable);
            // 确认集合元素值
            then(list).isInstanceOf(LinkedList.class).containsExactlyElementsOf(iterable);
        }
    }

    /**
     * 将一个列表集合进行分割
     *
     * <p>
     * {@link Lists#partition(java.util.List, int) Lists.partition(List, int)} 方法将一个列表集合分割成给定长度的若干个分片
     * </p>
     *
     * <p>
     * 例如: 将 {@code [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]} 分割为 {@code 3} 个元素一组的分片, 结果为
     * {@code [[0, 1, 2], [3, 4, 5], [6, 7, 8], [9]]}
     * </p>
     */
    @Test
    void partition_shouldSplitListIntoSlices() {
        // 产生一个元素值为 0~9 的列表集合
        var list = ContiguousSet.closedOpen(0, 10).asList();

        // 按照 3 个元素一组进行分片
        var slices = Lists.partition(list, 3);
        // 确认分片结果
        then(slices).containsExactly(
                ImmutableList.of(0, 1, 2),
                ImmutableList.of(3, 4, 5),
                ImmutableList.of(6, 7, 8),
                ImmutableList.of(9));
    }

    /**
     * 将一个列表集合元素进行反向排列
     *
     * <p>
     * {@link Lists#reverse(java.util.List) Lists.reverse(List)} 方法方法返回一个 {@code List} 对象, 其中的元素排列和原
     * {@code List} 集合相反
     * </p>
     *
     * <p>
     * 注意: 如果参数类型是 {@link ImmutableList}, 则返回 {@link ImmutableList#reverse()} 方法的返回值, 否则返回一个
     * {@code Lists.ReverseList} 类型结果. {@code ReverseList} 类型并不产生新的集合, 只是将对原 {@code List}
     * 对象的访问调转了一个方向, 例如下标和迭代器. 所以对原 {@code List} 集合的改变仍会影响到反转后的 {@code List} 对象
     * </p>
     */
    @Test
    void reverse_shouldReverseList() {
        // 产生一个元素值为 0~9 的列表集合
        var list = Lists.newArrayList(1, 2, 3);

        // 将集合进行反转, 得到反转的 List 集合
        var reversedList = Lists.reverse(list);
        // 确认集合反转
        then(reversedList).containsExactly(3, 2, 1);

        // 在原集合中添加元素
        list.add(4);
        // 确认反转集合中也增加了一个元素, 且位置被反转
        then(reversedList).containsExactly(4, 3, 2, 1);
    }

    /**
     * 将一个 {@link java.util.List List} 集合的全部元素进行转换
     *
     * <p>
     * {@link Lists#transform(java.util.List, com.google.common.base.Function) Lists.transform(List, Function)}
     * 方法方法返回一个 {@code Lists.TransformingRandomAccessList} 或 {@code Lists.TransformingSequentialList} 类型对象,
     * 存储转换后的元素集合
     * </p>
     *
     * <p>
     * 注意: 转换并不是在 {@code transform} 方法执行的过程中完成的, 事实上, {@code transform} 返回的
     * {@code TransformingRandomAccessList} 或 {@code TransformingSequentialList} 类型只是一个原集合类型的代理类型,
     * 在获取集合元素时, 才会将集合元素从原集合中获取, 并转换为所需的结果返回
     * </p>
     */
    @Test
    void transform_shouldTransformEachElementInList() {
        // 产生一个元素值为 0~9 的列表集合
        var list = Lists.newArrayList(1, 2, 3);

        // 执行转换方法, 获取一个转换元素值后的集合类型
        var transformedList = Lists.transform(list, e -> String.format("0%d", e));
        // 确认集合中包含转换后的元素
        then(transformedList).containsExactly("01", "02", "03");

        // 在原集合中添加一个元素
        list.add(4);
        // 确认转换结果集合中也包含对应转换后的元素
        then(transformedList).containsExactly("01", "02", "03", "04");
    }
}
