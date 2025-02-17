package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * 演示不变集合的使用
 *
 * <p>
 * 和 {@link java.util.Collections#unmodifiableCollection(java.util.Collection)
 * Collections.unmodifiableCollection(Collection)} 等方法包装的不可修改集合不同,
 * 它本质上是对现有集合对象的一个代理, 只是禁用了
 * 修改集合元素的一系列方法, 作用是保障代码安全; 而 Guava 提供的不变集合则是从数据结构上不支持修改, 除保障代码安全外, 还可以提供更好的执行
 * 效率和内存空间使用率, 且无需担心线程安全问题
 * </p>
 *
 * <p>
 * 本例中以 {@link ImmutableList} 类型为例, 演示不变集合类型的使用,
 * {@link com.google.common.collect.ImmutableSet
 * ImmutableSet} 等集合类型和其使用方法基本类似
 * </p>
 *
 *
 *
 * <p>
 * 注意, 基于设计原因, {@link ImmutableList} 等集合类型不允许使用 {@code null} 作为元素值
 * </p>
 *
 * <p>
 * JDK 9 以后, JDK 本身提供了 {@link java.util.List#of(Object) List.of(T...)} 方法, 可以取代
 * Guava 库的使用
 * </p>
 */
class ImmutableListTest {
    /**
     * 通过指定元素构建不变集合
     *
     * <p>
     * {@link ImmutableList#of(Object) ImmutableList.of(Object...)} 方法可以指定多个元素,
     * 构建一个不变 {@code List} 集合
     * </p>
     *
     * <p>
     * {@code ImmutableList.of(...)} 方法具备多个重载形式, 可以支持 1 ~ n 个参数的情况
     * </p>
     */
    @SuppressWarnings({ "deprecation", "null" })
    @Test
    void of_shouldCreateImmutableListByElements() {
        var list = ImmutableList.of(1, 2, 3, 4);

        then(list).containsExactly(1, 2, 3, 4);

        // 确认集合不支持修改操作
        thenThrownBy(() -> list.add(5)).isInstanceOf(UnsupportedOperationException.class);

        // 确认元素不能包含 null 值
        thenThrownBy(() -> ImmutableList.of(1, null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * 创建一个集合的不变集合副本
     *
     * <p>
     * {@link ImmutableList#copyOf(Iterable)} 可以创建现有集合的一个不变副本, 该方法也具备多个重载形式,
     * 可以支持集合的多种形式参数
     * </p>
     */
    @Test
    void copyOf_shouldCreateImmutableListFromOtherCollection() {
        // 创建原集合
        var src = Sets.newHashSet(1, 2, 3, 4);

        // 确认创建了原集合的副本, 且具备相同的集合元素
        var result = ImmutableList.copyOf(src);
        then(result).isNotSameAs(src);
        then(result).containsExactlyElementsOf(src);

        // 确认无法创建包含 null 元素的集合副本
        src.add(null);
        thenThrownBy(() -> ImmutableList.copyOf(src)).isInstanceOf(NullPointerException.class);
    }

    /**
     * 创建一个集合的不变集合副本
     *
     * <p>
     * {@link ImmutableList#sortedCopyOf(java.util.Comparator, Iterable)}
     * 可以创建现有集合的一个不变副本, 且副本元素经过排序
     * </p>
     */
    @Test
    void copyOf_shouldCreateSortedImmutableListFromOtherCollection() {
        // 创建原集合
        var src = Sets.newHashSet(1, 2, 3, 4);
        // 创建排序对象
        var ordering = Ordering.natural().reversed();

        // 确认创建原集合的副本, 且副本中包含原集合的所有元素, 且元素有序
        var result = ImmutableList.sortedCopyOf(ordering, src);
        then(result).isNotSameAs(src);
        then(result).containsExactlyInAnyOrderElementsOf(src).isSortedAccordingTo((l, r) -> r - l);
    }

    /**
     * 通过 {@link ImmutableList.Builder} 构建不变集合对象
     *
     * <p>
     * {@link ImmutableList#builder()} 可以创建一个 {@link ImmutableList.Builder} 对象,
     * 通过该对象可以分批设置集合元素, 最终构建
     * 不变集合对象
     * </p>
     */
    @Test
    void builder_shouldCreateImmutableListByBuilder() {
        // 创建 Builder 对象, 逐步添加元素, 构建不变集合对象
        var list = ImmutableList.builder()
                .add(1, 2, 3)
                .addAll(Sets.newHashSet(4, 5, 6))
                .build();

        then(list).containsExactly(1, 2, 3, 4, 5, 6);
    }

    /**
     * 通过 {@link ImmutableList.Builder} 构建不变集合对象
     *
     * <p>
     * {@link ImmutableList#builderWithExpectedSize(int)} 可以创建一个
     * {@link ImmutableList.Builder} 对象, 且通过设置预期元素
     * 个数预留足够的内存空间, 减少在添加元素过程中因内存预留不足导致的存储区重建消耗
     * </p>
     *
     * <p>
     * 预期元素个数无需精确, 如果最终添加的元素个数超出预期个数, 也不会导致错误, 只是会导致存储区重建
     * </p>
     */
    @Test
    void builder_shouldCreateImmutableListByBuilderWithCapacity() {
        // 预期 5 个元素, 实际添加元素个数 <= 5, 构建不变集合
        var list = ImmutableList.builderWithExpectedSize(5)
                .add(1, 2, 3)
                .addAll(Sets.newHashSet(4, 5))
                .build();
        then(list).containsExactly(1, 2, 3, 4, 5);

        // 预期 5 个元素, 实际添加元素个数 > 5, 构建不变集合
        list = ImmutableList.builderWithExpectedSize(5)
                .add(1, 2, 3)
                .addAll(Sets.newHashSet(4, 5, 6))
                .build();
        then(list).containsExactly(1, 2, 3, 4, 5, 6);
    }
}
