package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Month;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

/**
 * 演示 Guava {@link java.util.Set Set} 集合工具类
 *
 * <p>
 * {@link Sets} 类提供了一系列静态方法用于创建和操作 {@link HashSet}, {@link TreeSet}, {@link LinkedHashSet}, {@link EnumSet},
 * {@link ConcurrentHashMap.KeySetView} 以及 {@link CopyOnWriteArraySet} 等 {@link java.util.Set Set} 类型集合的方法
 * </p>
 */
class SetUtilsTest {
    /**
     * 创建 {@link java.util.Set Set} 集合对象
     *
     * <p>
     * 创建 {@link HashSet} 集合对象
     * <ul>
     * <li>
     * 通过 {@link {@link Sets#newHashSet()} 方法可以创建一个 {@link HashSet} 集合对象. 该方法有一系列重载, 通过不同类型参数构建
     * {@link HashSet} 集合对象, 包括:
     * <ul>
     * <li>
     * {@link Sets#newHashSet()}, 构建一个空的 {@link HashSet} 集合对象, 相当于直接执行 {@link HashSet#HashSet()} 构造器.
     * 注意: 如果只是为了得到一个空集合且不会修改它, 则应该使用 {@link ImmutableSet#of()} 方法, 或在 JDK 9 之后使用
     * {@link java.util.Set#of() Set.of()} 方法
     * </li>
     * <li>
     * {@link Sets#newHashSet(Object...) Sets.newHashSet(T...)}, 通过一系列元素值构建 {@link HashSet} 集合对象
     * </li>
     * <li>
     * {@link Sets#newHashSet(Iterable)}, 将一个可迭代对象通过迭代获取元素, 构建 {@link HashSet} 集合对象
     * </li>
     * <li>
     * {@link Sets#newHashSet(java.util.Iterator) Sets.newHashSet(Iterator)}, 将一个迭代器对象通过迭代获取元素, 构建
     * {@link HashSet} 集合对象
     * </li>
     * </ul>
     * </li>
     * <li>
     * 除上述重载方法外, Guava 还提供了和集合元素个数设定相关的方法, 可以提高代码执行效率, 包括:
     * <ul>
     * <li>
     * {@link Sets#newHashSetWithExpectedSize(int)} 方法用于产生一个空 {@link HashSet} 集合对象, 并设置预期的 {@code capacity}
     * 属性, 该集合对象可以在元素数量到达预期值前, 避免内存重新分配. 注意, 该方法已标记为过期, 应该直接使用 {@link HashSet#HashSet(int)}
     * 构造方法, 通过参数设置 {@code capacity} 值
     * </li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link LinkedHashSet} 集合对象
     * <ul>
     * <li>
     * {@link Sets#newLinkedHashSet()} 方法可以创建空 {@link LinkedHashSet} 类型集合对象, 和直接使用 {@link LinkedHashSet}
     * 类型构造器一致
     * </li>
     * <li>
     * {@link Sets#newLinkedHashSet(Iterable)} 方法通过一个可迭代对象的元素创建 {@link LinkedHashSet} 类型集合对象
     * </li>
     * <li>
     * {@link Sets#newLinkedHashSetWithExpectedSize(int)} 方法创建具备元素个数预设值的 {@link LinkedHashSet} 类型集合对象,
     * 类似于 {@link LinkedHashSet#LinkedHashSet(int)} 方法
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link TreeSet} 集合对象
     * <ul>
     * <li>
     * {@link Sets#newTreeSet()} 方法可以创建空 {@link TreeSet} 类型集合对象, 和直接使用 {@link TreeSet} 类型构造器一致
     * </li>
     * <li>
     * {@link Sets#newTreeSet(Iterable)} 方法通过一个可迭代对象的元素创建 {@link TreeSet} 类型集合对象
     * </li>
     * <li>
     * {@link Sets#newTreeSet(java.util.Comparator) Sets.newTreeSet(Comparator)} 方法创建 {@link TreeSet} 类型集合,
     * 并设置元素比较的方法, 该比较方法会影响到集合内部构建二叉树时元素的排列顺序
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link ConcurrentHashMap.KeySetView} 集合对象
     * <ul>
     * <li>
     * {@link ConcurrentHashMap.KeySetView} 是 {@link ConcurrentHashMap} 的 Key 集合存储类型, 可以看作是
     * {@code ConcurrentHashSet} 类型
     * </li>
     * <li>
     * {@link Sets#newConcurrentHashSet()} 方法可以创建空 {@code ConcurrentHashSet} 类型集合对象
     * </li>
     * <li>
     * {@link Sets#newConcurrentHashSet(Iterable)} 方法通过一个可迭代对象的元素创建 {@code ConcurrentHashSet} 类型集合对象
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link CopyOnWriteArraySet} 对象
     * <ul>
     * <li>
     * 所谓 {@link CopyOnWriteArraySet}, 即一个 {@link java.util.Set Set} 类型集合, 内部通过
     * {@link java.util.concurrent.CopyOnWriteArrayList CopyOnWriteArrayList} 类型实现:
     * <li>
     * 和 {@link java.util.concurrent.CopyOnWriteArrayList CopyOnWriteArrayList} 类似, {@link CopyOnWriteArraySet}
     * 也是在修改元素 (添加或删除) 时复制内部的数组, 即读取和修改在不同的数组上进行
     * </li>
     * <li>
     * 和 {@link java.util.concurrent.CopyOnWriteArrayList CopyOnWriteArrayList} 性质类似, {@link CopyOnWriteArraySet}
     * 在读取过程中无需加锁,具有较好的并发性能
     * </li>
     * <li>
     * {@link CopyOnWriteArraySet} 会在操作数组元素时 (例如添加和删除元素) 加锁, 以保证线程安全性
     * </li>
     * <li>
     * {@link CopyOnWriteArraySet} 不利于大量元素操作 (相比于 {@link java.util.Set} 集合), 其集合操作均是通过内部的
     * {@link java.util.concurrent.CopyOnWriteArrayList CopyOnWriteArrayList} 集合来实现, 包括
     * {@link CopyOnWriteArraySet#contains(Object)} 方法
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@link EnumSet} 对象
     * <ul>
     * <li>
     * {@link Sets#newEnumSet(Iterable, Class)} 通过包含枚举对象的可迭代对象即枚举类型产生一个 {@link EnumSet} 集合对象,
     * 内部存储不重复的枚举对象
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 创建 {@code IdentityHashSet} 对象
     * <ul>
     * <li>
     * {@link Sets#newIdentityHashSet()} 返回 {@link java.util.IdentityHashMap} 类型的 Key 集合, 相当于
     * {@code IdentityHashSet} 类型集合对象
     * </li>
     * <li>
     * 在其它的 {@code Set} 集合中, 元素的比较是通过 {@link Object#equals(Object)} 方法进行的, 而所谓 {@code IdentityHashSet}
     * 集合中, 元素的比较是通过 {@code ==} 运算符进行的, 即 {@code IdentityHashSet} 允许存储"值"相同, 但"引用"不同的多个元素, 例如:
     * 若干个 {@code new String("A")} 对象
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void new_shouldCreateSet() {
        var iterable = ContiguousSet.closedOpen(0, 10).asList();

        // 测试产生 HashSet 类型集合对象
        {
            // 创建空集合
            var set = Sets.newHashSet();
            // 确认集合类型并确认集合为空
            then(set).isInstanceOf(HashSet.class).isEmpty();
        }

        {
            // 通过元素值创建集合
            var set = Sets.newHashSet(1, 2, 3, 4);
            // 确认集合中的元素值
            then(set).isInstanceOf(HashSet.class).containsExactlyInAnyOrder(1, 2, 3, 4);
        }

        {
            // 通过一个迭代器对象创建集合
            var set = Sets.newHashSet(iterable.iterator());
            // 确认集合包含的元素和迭代器对象一致
            then(set).isInstanceOf(HashSet.class).containsExactlyInAnyOrderElementsOf(iterable);
        }

        {
            // 通过一个可迭代对象创建集合
            var set = Sets.newHashSet(iterable);
            // 确认集合包含的元素和可迭代对象包含的元素一致
            then(set).isInstanceOf(HashSet.class).containsExactlyInAnyOrderElementsOf(iterable);
        }

        {
            var set = Sets.newHashSetWithExpectedSize(10);
            set.addAll(iterable);

            then(set).isInstanceOf(HashSet.class).containsExactlyInAnyOrderElementsOf(iterable);
        }

        // 测试产生 LinkedHashSet 类型集合对象
        {
            var set = Sets.newLinkedHashSet();
            then(set).isInstanceOf(LinkedHashSet.class).isEmpty();
        }

        {
            var set = Sets.newLinkedHashSet(iterable);
            then(set).isInstanceOf(LinkedHashSet.class).containsExactlyElementsOf(iterable);
        }

        {
            var set = Sets.newLinkedHashSetWithExpectedSize(10);
            set.addAll(iterable);

            then(set).isInstanceOf(LinkedHashSet.class).containsExactlyElementsOf(iterable);
        }

        // 测试产生 TreeSet 类型集合对象
        {
            // 构建一个空的 TreeSet 集合
            var set = Sets.newTreeSet();
            // 确认对象类型和集合为空
            then(set).isInstanceOf(TreeSet.class).isEmpty();
        }

        {
            // 通过一个可迭代对象创建 TreeSet 集合
            var set = Sets.newTreeSet(iterable);
            // 确认集合元素
            then(set).isInstanceOf(TreeSet.class).containsExactlyElementsOf(iterable);
        }

        {
            // 创建一个空的 TreeSet 集合, 并设置元素比较函数
            var set = Sets.<Integer>newTreeSet((l, r) -> r - l);
            // 添加元素
            set.addAll(iterable);

            // 确认集合元素
            then(set).isInstanceOf(TreeSet.class).containsExactlyElementsOf(Lists.reverse(iterable));
        }

        // 测试创建 ConcurrentHashMap.KeySetView 类型集合对象
        {
            // 创建空集合对象
            var set = Sets.<Integer>newConcurrentHashSet();
            // 确认对象类型
            then(set).isInstanceOf(ConcurrentHashMap.KeySetView.class).isEmpty();
        }
        {
            // 通过一个可迭代对象构建集合对象
            var set = Sets.newConcurrentHashSet(iterable);
            // 确认集合元素
            then(set).isInstanceOf(ConcurrentHashMap.KeySetView.class).containsExactlyInAnyOrderElementsOf(iterable);
        }

        // 测试创建 CopyOnWriteArraySet 类型集合对象
        {
            // 创建空集合对象
            var set = Sets.newCopyOnWriteArraySet();
            // 确认对象类型
            then(set).isInstanceOf(CopyOnWriteArraySet.class).isEmpty();
        }

        {
            // 创建空集合对象
            var set = Sets.newCopyOnWriteArraySet(iterable);
            // 确认对象类型
            then(set).isInstanceOf(CopyOnWriteArraySet.class).containsExactlyInAnyOrderElementsOf(iterable);
        }

        // 测试 EnumSet 类型集合对象
        {
            var set = Sets.newEnumSet(ImmutableSet.copyOf(Month.values()), Month.class);
            then(set).isInstanceOf(EnumSet.class).containsExactlyInAnyOrder(Month.values());
        }

        // 测试创建 IdentityHashSet 集合对象
        {
            // 创建集合对象
            var set = Sets.newIdentityHashSet();
            // 添加两个引用相同的变量值
            set.add("A");
            set.add("A");

            // 确认最终集合只包含一个元素 A
            then(set).containsOnly("A");
        }

        {
            // 创建集合对象
            var set = Sets.newIdentityHashSet();
            // 添加两个值相同但引用不同的变量值
            set.add(new String("A"));
            set.add(new String("A"));

            // 确认最终集合包含两个元素 A 和 A, 它们的引用不同
            then(set).containsExactly("A", "A");
        }
    }

    /**
     * 计算多个 {@code Set} 集合的笛卡尔积
     *
     * <p>
     * {@link Sets#cartesianProduct(java.util.Set...) Sets.cartesianProduct(Set...)} 方法用于计算多个集合的笛卡尔积, 例如:
     * <code>{[1, 2], [10, 20, 30]} => {[1, 10], [1, 20], [1, 30], [2, 10], [2, 20], [2, 30]}</code>
     * </p>
     */
    @Test
    void cartesianProduct_shouldBuildCartesianProductOfSeveralSets() {
        // 计算多个列表集合的笛卡尔积
        var set = Sets.cartesianProduct(
                ImmutableSet.of(1, 2),
                ImmutableSet.of(11, 22, 33),
                ImmutableSet.of(111, 222, 333));

        // 确认笛卡尔积结果
        then(set).containsExactly(
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
     * 计算集合元素的全排列结果 (不改变元素顺序)
     *
     * <p>
     * {@link Sets#combinations(java.util.Set, int) Sets.combinations(Set, int)} 方法返回一个 {@link java.util.Set Set}
     * 集合的集合的全排列结果
     * </p>
     *
     * <p>
     * 第二个参数值不得大于第一个参数表示的集合长度, 当其等于集合长度时, 返回结果和集合值相同 (例如
     * {@code ([1, 2, 3], 3) => [[1, 2, 3]]}); 当其小于集合长度时, 返回结果为集合按照该数值全排列的结果 (例如
     * {@code ([1, 2, 3], 2) => [[1, 2], [1, 3], [2, 3]]})
     * </p>
     */
    @Test
    void combinations_shouldGetCombinationsOfSet() {
        // 将 3 个元素按 3 个元素进行排列, 得到唯一排列结果, 和原 3 个元素排列相同
        {
            // 对集合进行排列
            var sets = Sets.combinations(ImmutableSet.of(1, 2, 3), 3);
            // 确认排列结果
            then(sets).containsExactly(ImmutableSet.of(1, 2, 3));
        }

        // 将 3 个元素按 2 个元素进行排列, 得到 3 中排列结果
        {
            // 对集合进行排列
            var sets = Sets.combinations(ImmutableSet.of(1, 2, 3), 2);
            // 确认排列结果
            then(sets).containsExactly(
                    ImmutableSet.of(1, 2),
                    ImmutableSet.of(1, 3),
                    ImmutableSet.of(2, 3));
        }
    }

    /**
     * 计算一个集合所有可能的子集
     *
     * <p>
     * {@link Sets#powerSet(java.util.Set) Sets.powerSet(Set)} 方法返回指定集合的所有可能的子集结果
     * </p>
     *
     * <p>
     * 注意: "空集"也算一个子集, 另外, 所求子集不关心顺序, 即 {@code [1, 2] 和 [2, 1]} 被认为是同一个子集
     * </p>
     *
     * <p>
     * 例如 {@code [1, 2, 3] => [[], [1], [2], [3], [1, 2], [1, 3], [2, 3], [1, 2, 3]]}
     * </p>
     */
    @Test
    void powerSet_shouldGetAllSubSet() {
        // 创建一个集合
        var set = ImmutableSet.of(1, 2, 3);

        // 求集合的所有子集
        var subSets = Sets.powerSet(set);

        // 确认所得子集
        then(subSets).containsExactlyInAnyOrder(
                ImmutableSet.of(),
                ImmutableSet.of(1),
                ImmutableSet.of(2),
                ImmutableSet.of(3),
                ImmutableSet.of(1, 2),
                ImmutableSet.of(1, 3),
                ImmutableSet.of(2, 3),
                ImmutableSet.of(1, 2, 3));
    }

    /**
     * 创建一个包含指定类型枚举值的 {@link java.util.Set Set} 集合对象
     *
     * <p>
     * {@link Sets#complementOf(java.util.Collection, Class) Sets.complementOf(Collection, Class)}
     * 方法用于创建一个保存指定枚举类型枚举值的 {@link java.util.Set Set} 集合对象. 第一个参数为要排除的枚举值, 这部分值不会包含在结果中,
     * 第二个参数为枚举类型. 该方法第一个参数可以为空集合, 表示不排除任何值, 结果集合将包含枚举类型定义的全部值
     * </p>
     *
     * <p>
     * {@link Sets#complementOf(java.util.Collection) Sets.complementOf(Collection)} 方法的参数不能为空集合,
     * 必须指定要排除的枚举值
     * </p>
     */
    @Test
    void complementOf_createEnumSetExceptGivenElements() {
        // 测试创建包含枚举类型全部值的 Set 集合
        {
            // 创建包含 Month 枚举全部值的集合, 第一个参数为"空"集合, 表示不排除任何枚举值
            var set = Sets.complementOf(ImmutableSet.of(), Month.class);
            // 确认结果中包含 Month 枚举的全部值
            then(set).isInstanceOf(EnumSet.class).containsExactly(Month.values());
        }

        // 测试排除指定枚举值, 将其余值放入集合中返回
        {
            // 创建包含 Month 枚举值的集合, 并排除 'M' 字符开头的那部分枚举值
            var set = Sets.complementOf(ImmutableSet.of(Month.MARCH, Month.MAY), Month.class);
            // 确认结果中不包含 'M' 开头的枚举值
            then(set).isInstanceOf(EnumSet.class).hasSize(10).allMatch(m -> !m.name().startsWith("M"));
        }
    }

    /**
     * 创建一个包含枚举值的 {@link ImmutableSet} 集合对象
     *
     * <p>
     * {@link Sets#immutableEnumSet(Enum, Enum...) Sets.immutableEnumSet(Enum, Enum...)} 方法用于创建一个包含指定枚举值的
     * {@link ImmutableSet} 集合对象
     * </p>
     *
     * <p>
     * {@link Sets#immutableEnumSet(Iterable) Sets.immutableEnumSet(Iterable)} 方法是上述方法的另一个重载形式, 功能一致
     * </p>
     */
    @Test
    void immutableEnumSet_createEnumSetByGivenEnumValue() {
        // 测试创建包含指定枚举值的 Set 集合
        {
            // 创建包含 Month 枚举全部值的集合,
            var set = Sets.immutableEnumSet(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
            // 确认结果中包含 Month 枚举的全部值
            then(set).isInstanceOf(ImmutableSet.class).containsExactly(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
        }

        // 测试创建包含指定枚举值的 Set 集合
        {
            // 创建包含 Month 枚举值的集合, 并排除 'M' 字符开头的那部分枚举值
            var set = Sets.immutableEnumSet(ImmutableSet.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH));
            // 确认结果中不包含 'M' 开头的枚举值
            then(set).isInstanceOf(ImmutableSet.class).containsExactly(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
        }
    }

    /**
     * 从 {@link java.util.NavigableSet NavigableSet} 类型集合中获取指定的范围内包括的值
     *
     * <p>
     * {@link Sets#subSet(java.util.NavigableSet, Range)} 方法可以从一个 {@link java.util.NavigableSet NavigableSet}
     * 类型的集合参数中获取在指定 {@link Range} 定义范围内的值
     * </p>
     *
     * <p>
     * 注: {@link java.util.NavigableSet NavigableSet} 是一个扩展的 {@link java.util.SortedSet SortedSet} 类型,
     * 且具备一系列查询元素的能力, 例如 {@link java.util.NavigableSet#higher(Object) NavigableSet.higher(T)},
     * {@link java.util.NavigableSet#lower(Object) NavigableSet.lower(T)} 等, {@link TreeSet} 即一个典型的
     * {@link java.util.NavigableSet NavigableSet} 类型
     * </p>
     */
    @Test
    void subSet_shouldGetSubSetInGivenRange() {
        // 创建一个元素为 'A'~'Z' 的 TreeSet 集合, 且不包含 'E' 和 'G' 两个元素
        var set = ContiguousSet.closed('A', 'Z')
                .stream()
                .map(n -> (char) (n.intValue()))
                .filter(c -> c != 'G' && c != 'E')
                .collect(Collectors.toCollection(TreeSet::new));

        // 从集合中获取范围为 'D'~'H' 的元素值
        var subSet = Sets.subSet(set, Range.closed('D', 'H'));
        // 确认获取的元素值, 从 'D'~'H', 且不包括 'E' 和 'G'
        then(subSet).containsExactly('D', 'F', 'H');
    }

    /**
     * 对 {@link java.util.Set Set} 集合元素进行过滤, 得到包含过滤后结果的新 {@code Set} 对象
     *
     * <p>
     * {@link Sets#filter(java.util.Set, com.google.common.base.Predicate) Sets.filter(Set, Predicate)} 方法根据一个
     * Lambda 表达式对集合元素进行过滤, 将符合条件的结果作为一个新的 {@link java.util.Set Set} 集合对象返回
     * </p>
     */
    @Test
    void filter_shouldFilterElements() {
        // 创建一个元素为 'A'~'Z' 的 TreeSet 集合
        var set = ContiguousSet.closed('A', 'Z')
                .stream()
                .map(n -> (char) (n.intValue()))
                .collect(Collectors.toCollection(TreeSet::new));

        // 过滤掉集合中所有的元音字母, 并返回过滤后的结果
        var filteredSet = Sets.filter(set, c -> switch (c) {
            case 'A', 'E', 'I', 'O', 'U' -> false;
            default -> true;
        });

        // 确认结果集合中不包含元音字母元素
        then(filteredSet).hasSize(21).doesNotContain('A', 'E', 'I', 'O', 'U');
    }

    /**
     * 求两个集合的并集 (即 {@code s1 ∪ s2})
     *
     * <p>
     * {@link Sets#union(java.util.Set, java.util.Set) Sets.union(Set, Set)} 方法用于计算两个集合的并集, 返回包含并集结果的
     * {@link Sets.SetView} 结果
     * </p>
     *
     * <p>
     * 注意, {@link Sets.SetView} 接口对象并未真实存储元素, 而是存储了参与运算的两个集合的引用, 在访问集合元素时 (例如
     * {@link java.util.Set#contains(Object) Set.contains(T)} 方法或 {@link java.util.Set#iterator() Set.iterator()}
     * 方法时) 才会去计算并集结果
     * </p>
     */
    @Test
    void union_shouldUnionTwoSets() {
        var set1 = ImmutableSet.of(1, 2, 3, 4);
        var set2 = Sets.newLinkedHashSet(ImmutableSet.of(3, 4, 5, 6));

        // 求两个集合的并集
        var unionSet = Sets.union(set1, set2);
        // 确认并集结果
        then(unionSet).containsExactly(1, 2, 3, 4, 5, 6);

        // 在原集合中添加若干元素
        Collections.addAll(set2, 7, 8);
        // 确认并集结果也被改变, 多出新增部分的元素
        then(unionSet).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    /**
     * 求两个集合的交集 (即 {@code s1 ∩ s2})
     *
     * <p>
     * {@link Sets#intersection(java.util.Set, java.util.Set) Sets.intersection(Set, Set)} 方法用于计算两个集合的交集,
     * 返回包含交集结果的 {@link Sets.SetView} 结果
     * </p>
     *
     * <p>
     * 注意, {@link Sets.SetView} 接口对象并未真实存储元素, 而是存储了参与运算的两个集合的引用, 在访问集合元素时 (例如
     * {@link java.util.Set#contains(Object) Set.contains(T)} 方法或 {@link java.util.Set#iterator() Set.iterator()}
     * 方法时) 才会去计算交集结果
     * </p>
     */
    @Test
    void intersection_shouldIntersectTwoSets() {
        var set1 = ImmutableSet.of(1, 2, 3, 4);
        var set2 = Sets.newLinkedHashSet(ImmutableSet.of(3, 4, 5, 6));

        // 求两个集合的交集
        var intersectSet = Sets.intersection(set1, set2);
        // 确认交集结果
        then(intersectSet).containsExactly(3, 4);

        // 在原集合中添加元素
        set2.add(2);
        // 确认交集结果也被改变, 多出新增部分的元素
        then(intersectSet).containsExactly(2, 3, 4);
    }

    /**
     * 求两个集合的差集 (即 {@code s1 - s2}),
     *
     * <p>
     * {@link Sets#difference(java.util.Set, java.util.Set) Sets.difference(Set, Set)} 方法用于计算是包含在"参数1",
     * 且不包含在"参数2"中的集合元素, 返回包含差集结果的 {@link Sets.SetView} 结果
     * </p>
     *
     * <p>
     * 注意, {@link Sets.SetView} 接口对象并未真实存储元素, 而是存储了参与运算的两个集合的引用, 在访问集合元素时 (例如
     * {@link java.util.Set#contains(Object) Set.contains(T)} 方法或 {@link java.util.Set#iterator() Set.iterator()}
     * 方法时) 才会去计算差集结果
     * </p>
     */
    @Test
    void difference_shouldDifferenceTwoSets() {
        var set1 = ImmutableSet.of(1, 2, 3, 4);
        var set2 = Sets.newLinkedHashSet(ImmutableSet.of(3, 4));

        // 计算包含在 set1
        var diffSet = Sets.difference(set1, set2);
        // 确认差集结果
        then(diffSet).containsExactly(1, 2);

        // 在原集合中添加元素
        set2.add(2);
        // 确认差集结果也被改变, 多出新增部分的元素
        then(diffSet).containsExactly(1);
    }

    /**
     * 求两个集合的对称差集 (即 {@code (s1 ∩ s2)} 的补集或 {@code (s1 - s2) ∪ (s2 - s1)})
     *
     * <p>
     * 所谓对称差集, 即获取两个集合交集的补集, 即不同时存在于两个集合中的结果
     * </p>
     *
     * <p>
     * {@link Sets#symmetricDifference(java.util.Set, java.util.Set) Sets.symmetricDifference(Set, Set)}
     * 方法用于计算两个集合的对称差集, 返回包含差集结果的 {@link Sets.SetView} 结果
     * </p>
     *
     * <p>
     * 注意, {@link Sets.SetView} 接口对象并未真实存储元素, 而是存储了参与运算的两个集合的引用, 在访问集合元素时 (例如
     * {@link java.util.Set#contains(Object) Set.contains(T)} 方法或 {@link java.util.Set#iterator() Set.iterator()}
     * 方法时) 才会去计算差集结果
     * </p>
     */
    @Test
    void symmetricDifference_shouldDifferenceTwoSets() {
        var set1 = ImmutableSet.of(1, 2, 3, 4);
        var set2 = Sets.newLinkedHashSet(ImmutableSet.of(3, 4, 5, 6));

        // 求两个集合的对称差集
        var diffSet = Sets.symmetricDifference(set1, set2);
        // 确认对称差集结果
        then(diffSet).containsExactly(1, 2, 5, 6);

        // 在原集合中添加元素
        set2.add(2);
        // 确认对称差集结果也被改变, 多出新增部分的元素
        then(diffSet).containsExactly(1, 5, 6);
    }
}
