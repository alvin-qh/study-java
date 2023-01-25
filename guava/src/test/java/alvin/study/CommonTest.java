package alvin.study;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

@SuppressWarnings("java:S5961")
class CommonTest {
    /**
     * 演示 {@link Optional} 的使用
     *
     * <p>
     * {@link Optional} 类型用于包装一个引用, 并提供了一系列方法来判断引用是否为 {@code null} 值
     * </p>
     *
     * <p>
     * JDK 8 以后的版本具备类似功能, 即 {@link java.util.Optional} 类型, 其功能较 Guava 库提供的更为丰富, 推荐使用 JDK 自带的
     * {@link java.util.Optional} 工具类
     * </p>
     */
    @Test
    void optional_shouldWrapperObject() {
        var obj = (Object) new Object();

        // 通过一个非 null 引用构建 Optional 对象
        var validOpt = Optional.of(obj);
        // 判断保存的引用值不为 null
        then(validOpt.isPresent()).isTrue();
        // 获取保存的引用值, 如果引用值为 null, 则抛出异常
        then(validOpt.get()).isSameAs(obj);
        // 获取保存保的引用值, null 即返回 null 值
        then(validOpt.orNull()).isSameAs(obj);
        // 得到一个单一元素的 Set 集合, 包含保持的引用值
        then(validOpt.asSet()).hasSize(1).containsExactly(obj);

        // 将 Guava Optional 类型转为 Java Optional 类型
        var javaOpt = validOpt.toJavaUtil();
        then(javaOpt.get()).isSameAs(obj);

        obj = null;
        // 如果 Optional.of 的参数为 null, 则引发 NullPointException 异常
        thenThrownBy(() -> Optional.of(null)).isInstanceOf(NullPointerException.class);

        // 通过一个可能为 null 的引用构建 Optional 对象
        var emptyOpt = Optional.fromNullable(obj);
        // 判断保存的引用值为 null
        then(emptyOpt.isPresent()).isFalse();
        // 获取保存的引用值, 由于引用值为 null, 所以会抛出 IllegalStateException 异常
        thenThrownBy(() -> then(emptyOpt.get())).isInstanceOf(IllegalStateException.class);
        // 获取保存保的引用值, null 即返回 null 值
        then(emptyOpt.orNull()).isNull();
        // 得到一个空的 Set 集合
        then(emptyOpt.asSet()).isEmpty();

        // 获取当前保存的引用值, 如果为 null, 则返回 or 方法参数定义的另一个引用值
        then(emptyOpt.or("Other")).isEqualTo("Other");
        // 获取当前保存的引用值, 如果为 null, 则返回 or 方法 Lambda 表达式返回的另一个引用值
        then(emptyOpt.or(() -> "Other")).isEqualTo("Other");
        // 获取当前 Optional 对象, 如果其保存的引用值为 null, 则返回 or 方法参数定义的另一个 Optional 对象
        then(emptyOpt.or(Optional.of("Other").get())).isEqualTo("Other");

        // 产生一个保存 null 值的 Optional 对象
        then(Optional.absent().isPresent()).isFalse();

        // 将集合中的 Optional 对象展开
        var ints = Optional.presentInstances(List.of(Optional.of(1), Optional.of(2)));
        then(ints).containsExactly(1, 2);
    }

    /**
     * 演示 {@link Preconditions} 类, 进行代码执行前置条件的检查
     *
     * <p>
     * 所谓代码执行前置条件, 无非包括: 方法参数是否合法, 变量是否为 {@code null}, 集合长度是否足够
     * </p>
     *
     * <p>
     * {@link Preconditions} 类的某些方法已经被 JDK 类似方法取代, 例如 {@link java.util.Objects#requireNonNull(Object)
     * Objects.requireNonNull(Object)}, {@link java.util.Objects#checkIndex(int, int) Objects.checkIndex(int, int)}
     * 以及 {@link java.util.Objects#checkFromIndexSize(int, int, int) Objects.checkFromIndexSize(int, int, int)} 等
     * </p>
     */
    @Test
    void preconditions_shouldCheckValuesByPreconditions() {
        var n = 10;
        // 检查参数是否符合要求, 即通过一个 boolean 表达式对参数进行检查,
        // 并通过一个错误描述以便于参数不合法时抛出 IllegalArgumentException 异常
        // 注意: 错误描述字符串中只支持 %s 占位符
        thenThrownBy(() -> Preconditions.checkArgument(n > 10)).isInstanceOf(IllegalArgumentException.class);
        thenThrownBy(() -> Preconditions.checkArgument(n > 10, "Expect n > 10, but n = %s", n))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expect n > 10, but n = %s", n);

        // 检查状态是否符合要求, 即通过一个 boolean 表达式对参数进行检查,
        // 并通过一个错误描述以便于状态不符合要求时抛出 IllegalStateException 异常
        // 注意: 错误描述字符串中只支持 %s 占位符
        thenThrownBy(() -> Preconditions.checkState(n == 0)).isInstanceOf(IllegalStateException.class);
        thenThrownBy(() -> Preconditions.checkState(n == 0, "Expect n is zero"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expect n is zero");

        var nonNullObj = new Object();
        // 检查一个引用是否不为 null, 如果不为 null, 返回该引用本身
        then(Preconditions.checkNotNull(nonNullObj)).isSameAs(nonNullObj);

        var nullObj = (Object) null;
        // 检查一个引用是否不为 null, 如果为 null, 则通过一个错误描述以便于状态不符合要求时抛出 NullPointerException 异常
        // 注意: 错误描述字符串中只支持 %s 占位符
        thenThrownBy(() -> Preconditions.checkNotNull(nullObj)).isInstanceOf(NullPointerException.class);
        thenThrownBy(() -> Preconditions.checkNotNull(nullObj, "Expect obj not null"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Expect obj not null");

        var list = List.of(1, 2, 3, 4);
        // 检查一个元素索引值是否正确 (即匹配集合的长度), 如果正确, 则返回该索引值本身
        // 注意: 元素索引的取值范围为 0 <= index < size
        then(Preconditions.checkElementIndex(3, list.size())).isEqualTo(3);

        // 检查一个元素索引值是否正确 (即匹配集合的长度), 如果不正确, 则抛出 IndexOutOfBoundsException 异常
        // 可以指定索引的名称以便追踪错误信息
        thenThrownBy(() -> Preconditions.checkElementIndex(4, list.size()))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("index (%s) must be less than size (%s)", 4, list.size());
        thenThrownBy(() -> Preconditions.checkElementIndex(4, list.size(), "Argument n"))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Argument n (%s) must be less than size (%s)", 4, list.size());

        // 检查一个位置索引值是否正确 (即匹配集合的长度), 如果正确, 则返回该索引值本身
        // 注意: 位置索引的取值范围为 0 <= index <= size
        then(Preconditions.checkPositionIndex(4, list.size())).isEqualTo(4);

        // 检查一个位置索引值是否正确 (即匹配集合的长度), 如果不正确, 则抛出 IndexOutOfBoundsException 异常
        // 可以指定索引的名称以便追踪错误信息
        thenThrownBy(() -> Preconditions.checkPositionIndex(5, list.size()))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("index (%s) must not be greater than size (%s)", 5, list.size());
        thenThrownBy(() -> Preconditions.checkPositionIndex(5, list.size(), "Argument n"))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Argument n (%s) must not be greater than size (%s)", 5, list.size());
    }

    /**
     * 测试校验方法
     *
     * <p>
     * {@link Verify} 类型的 {@code verify} 方法可以对一个条件表达式进行校验, 如果表达式为 {@code true} 表示通过校验, 如果为
     * {@code false} 则抛出 {@link VerifyException} 异常
     * </p>
     */
    @Test
    void verify_shouldVerifyRuntimeConditions() {
        var a = 10;

        // 条件表达式为 false 时, 抛出异常
        thenThrownBy(() -> Verify.verify(a > 10)).isInstanceOf(VerifyException.class);

        // 条件表达式为 false 时, 抛出异常且定义异常信息
        thenThrownBy(() -> Verify.verify(a > 10, "a must great than %s", 10))
                .isInstanceOf(VerifyException.class)
                .hasMessage("a must great than %s", 10);
    }

    /**
     * 演示对象操作工具类
     *
     * <p>
     * {@link Objects#equal(Object, Object)} 用于比较两个对象是否相等, 内部调用 {@link Object#equals(Object)} 方法, 和直接
     * 使用 {@link Object#equals(Object)} 方法不同, {@link Objects#equal(Object, Object)} 方法可以比较左值为 {@code null}
     * 的情况
     * <div>
     * 对于 JDK8 以上版本, 应使用 JDK 的 {@link java.util.Objects#equals(Object, Object) Objects.equals(Object, Object)}
     * 方法和 {@link java.util.Objects#deepEquals(Object, Object) Objects.deepEquals(Object, Object)} 方法
     * </div>
     * </p>
     *
     * <p>
     * {@link MoreObjects#firstNonNull(Object, Object)} 用于对两个值进行是否为 {@code null} 判断, 行为如下:
     * <ul>
     * <li>如果两个参数都不为 {@code null}, 则返回第一个参数的值</li>
     * <li>如果第一个参数为 {@code null}, 则返回第二个参数的值</li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link MoreObjects#toStringHelper(Class)} 方法用于辅助将对象转为字符串表达, 返回一个 {@link MoreObjects.ToStringHelper}
     * 类型对象, 通过该对象的 {@code add} 方法和 {@code addValue} 方法生成字符串结果
     * </p>
     *
     * <p>
     * {@link ComparisonChain#start()} 方法用于返回一个 {@link ComparisonChain} 对象, 用于通过多个比较方法组织一个比较链, 规则为:
     * 链条上优先比较的结果如果非 {@code 0}, 则整体返回该结果, 否则执行链条上下一级的比较方法. 比较时可以使用
     * {@link java.util.Comparator} 对象或 {@link Ordering} 对象指定比较规则
     * </p>
     */
    @Test
    void objects_shouldOperateObjects() {
        var obj1 = Integer.valueOf(100);
        var obj2 = Integer.valueOf(100);

        // 比较两个对象是否相等
        then(Objects.equal(obj1, obj2)).isTrue();
        then(Objects.equal(null, null)).isTrue();
        then(Objects.hashCode(obj1, obj2)).isEqualTo(4161);

        // 当第一个参数不为 null 时, 返回第一个参数值; 当地一个参数为 null 时, 返回第二个参数值
        then(MoreObjects.firstNonNull(obj1, null)).isEqualTo(obj1);
        then(MoreObjects.firstNonNull(null, obj2)).isEqualTo(obj2);
        then(MoreObjects.firstNonNull(obj1, obj2)).isEqualTo(obj1);

        // 产生一个 Integer 类型, 特定值的字符串结果
        var s = MoreObjects.toStringHelper(Integer.class)
                .add("name", "Alvin")
                .addValue(obj1)
                .toString();
        then(s).isEqualTo("Integer{name=Alvin, 100}");

        var obj3 = Integer.valueOf(200);

        // 通过一系列值的比较, 明确最终的比较结果
        var r = ComparisonChain.start()
                .compare(obj1, obj2)
                .compare(obj2, obj3, Ordering.explicit(obj3, obj2))
                .result();
        then(r).isEqualTo(1);
    }

    /**
     * 测试排序方法
     *
     * <p>
     * {@link Ordering} 类提供了一系列和排序相关的方法, 其对象既可以用于 {@link Collections#sort(List, java.util.Comparator)
     * Collections.sort(List, Comparator)} 方法 (或 {@link List#sort(java.util.Comparator) List.sort(Comparator)} 方法)
     * 进行排序, 也可以通过 {@link Ordering#isOrdered(Iterable)} 方法验证集合是否有序
     * </p>
     */
    @Test
    void ordering_shouldCheckCollectionWithOrder() {
        var list = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        // 通过 nature 方法产生一个自然排序 (对于数值即指从小到大) 的排序对象
        var ordering = Ordering.<Integer>natural();
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 确认排序结果
        then(list).containsExactly(1, 2, 3, 4, 5).isSorted();
        // 确认集合有序 (集合的后一个元素大于或等于前一个元素)
        then(ordering.isOrdered(list)).isTrue();
        // 确认集合严格有序 (集合的后一个元素大于前一个元素)
        then(ordering.isStrictlyOrdered(list)).isTrue();
        // 获取最小的 n 个值, 返回一个集合
        then(ordering.leastOf(list, 1)).containsExactly(1);
        // 获取最大的 n 个值, 返回一个集合
        then(ordering.greatestOf(list, 1)).containsExactly(5);
        // 通过定义的排序对象对从指定的几个值中找到最小值
        then(ordering.min(10, 20)).isEqualTo(10);
        // 通过定义的排序对象对从指定的几个值中找到最大值
        then(ordering.max(10, 20)).isEqualTo(20);
        // 将一个集合拷贝并排序后返回新 List 集合
        then(ordering.sortedCopy(list)).isNotSameAs(list).containsAll(list);
        // 将一个集合拷贝并排序后返回一个不可修改的新 List 集合
        then(ordering.immutableSortedCopy(list)).isNotSameAs(list).containsAll(list);

        // 通过 nature 方法产生一个自然排序的逆序排序对象
        ordering = Ordering.<Integer>natural().reverse();
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 确认排序结果 (逆序)
        then(list).containsExactly(5, 4, 3, 2, 1).isSortedAccordingTo((l, r) -> r - l);
        // 确认集合有序 (逆序, 集合的后一个元素大于前一个元素)
        then(ordering.isOrdered(list)).isTrue();
        // 获取最小的 n 个值, 返回一个集合, 因为是逆序, 所以得到的实际上是最大的值
        then(ordering.leastOf(list, 1)).containsExactly(5);
        // 获取最大的 n 个值, 返回一个集合, 因为是逆序, 所以得到的实际上是最小的值
        then(ordering.greatestOf(list, 1)).containsExactly(1);
        // 通过定义的排序对象对从指定的几个值中找到最小值, 因为是逆序, 所以得到的实际上是最大值
        then(ordering.min(10, 20)).isEqualTo(20);
        // 通过定义的排序对象对从指定的几个值中找到最大值, 因为是逆序, 所以得到的实际上是最小值
        then(ordering.max(10, 20)).isEqualTo(10);

        // 通过一个 mapping 函数将集合元素转换后进行排序 (根据集合元素的余数进行排序)
        ordering = Ordering.<Integer>natural().<Integer>onResultOf(n -> n % 2);
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 确认集合有序 (余数小的在前)
        then(list).containsExactly(4, 2, 5, 3, 1);
        // 确认集合有序
        then(ordering.isOrdered(list)).isTrue();

        // 通过元素的 toString 方法转为字符串, 并对字符串进行排序
        Collections.sort(list, Ordering.usingToString());
        // 确认排序结果
        then(list).containsExactly(1, 2, 3, 4, 5);

        // 设定两级排序规则
        // 所谓两级排序规则, 即在第一级排序规则的作用下, 如果两个元素相等, 则使用第二级排序规则进行排序
        // 第一级排序按元素值的余数排序 (逆序), 第二级排序按元素值的大小排序 (自然序)
        ordering = Ordering.<Integer>from((l, r) -> r % 2 - l % 2).compound((l, r) -> l - r);
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 确认排序结果
        then(list).containsExactly(1, 3, 5, 2, 4);

        // 设定多级排序规则 (两级排序, 规则和前一个例子一致)
        ordering = Ordering.<Integer>compound(List.of(
            (l, r) -> r % 2 - l % 2,
            (l, r) -> l - r));
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 确认排序结果
        then(list).containsExactly(1, 3, 5, 2, 4);

        // 根据一个给定的列表, 以列表元素的顺序作为排序依据, 得到一个排序对象
        // 注意: 该排序对象只能对所设定列表中存在的元素进行排序
        ordering = Ordering.explicit(List.of(4, 2, 1, 3, 5, 0, 7, 6));
        // 对集合进行排序
        Collections.sort(list, ordering);
        // 查看排序结果 (和设定的集合顺序一致)
        then(list).containsExactly(4, 2, 1, 3, 5);
        // 通过排序对象获取若干值的最大值 (比较结果和所给列表一致, 在列表中 1 在 4 之后, 1 较大)
        then(ordering.max(4, 1)).isEqualTo(1);
        // 通过排序对象获取若干值的最小值 (比较结果和所给列表一致, 在列表中 4 在 1 之前, 4 较小)
        then(ordering.min(4, 1)).isEqualTo(4);

        // 进行一次无意义排序 (即排序结果不具备任何有意义的顺序), 且排序结果可复现 (即同一个集合的排序结果是无序且稳定的)
        Collections.sort(list, Ordering.arbitrary());
        then(list).containsExactlyInAnyOrder(4, 5, 3, 2, 1);

        // 进行一次无效排序 (即排序结果和未排序前一致)
        Collections.sort(list, Ordering.allEqual());
        then(list).containsExactlyInAnyOrder(4, 5, 3, 2, 1);

        // 在集合中添加一个 null 值
        list.add(null);

        // 自然排序, 且 null 值在排序结果最前面
        ordering = Ordering.natural().nullsFirst();
        // 进行排序
        Collections.sort(list, ordering);
        // 确认排序结果
        then(list).containsExactly(null, 1, 2, 3, 4, 5);
        // 确认集合在指定规则下有序
        then(ordering.isOrdered(list)).isTrue();

        // 自然排序, 且 null 值在排序结果最后面
        ordering = Ordering.natural().nullsLast();
        // 进行排序
        Collections.sort(list, ordering);
        // 确认排序结果
        then(list).containsExactly(1, 2, 3, 4, 5, null);
        // 确认集合在指定规则下有序
        then(ordering.isOrdered(list)).isTrue();
    }

    /**
     * 测试异常处理辅助类
     *
     * <p>
     * 通过 {@link Throwables} 类可以简化异常的抛出和判断
     * </p>
     *
     * <p>
     * {@link Throwables} 类提供了三类方法简化异常的抛出
     * <ul>
     * <li>
     * {@link Throwables#throwIfInstanceOf(Throwable, Class)} 方法表示当异常为指定类型异常时抛出该异常
     * </li>
     * <li>
     * {@link Throwables#throwIfUnchecked(Throwable)} 方法表示当异常为 {@link RuntimeException} 或 {@link Error} 类型时
     * 抛出该异常
     * </li>
     * <li>
     * {@link Throwables#propagateIfPossible(Throwable, Class, Class)} 方法表示当异常为 {@link RuntimeException} 或
     * {@link Error} 类型, 又抑或是指定的异常类型时, 抛出该异常
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link Throwables} 类提供了三类方法简化异常判断和处理
     * <ul>
     * <li>{@link Throwables#getCauseAs(Throwable, Class)} 可以从异常链上获取期望类型的异常</li>
     * <li>{@link Throwables#getRootCause(Throwable)} 可以获取异常链的根异常, 即最初抛出的异常</li>
     * <li></li>
     * </ul>
     * </p>
     */
    @Test
    void throwable_shouldThrowExceptionWithThrowables() {
        var exp = new IOException();
        var rtExp = new RuntimeException();

        // 抛出指定类型异常, 最终抛出 exp 为 IOException 类型
        thenThrownBy(() -> {
            Throwables.throwIfInstanceOf(exp, NullPointerException.class);
            Throwables.throwIfInstanceOf(exp, IOException.class);
        }).isInstanceOf(IOException.class);

        // 抛出 RuntimeException 或 Error 类型异常, 最终抛出 rtExp 为 RuntimeException 类型
        thenThrownBy(() -> {
            Throwables.throwIfUnchecked(exp);
            Throwables.throwIfUnchecked(rtExp);
        }).isInstanceOf(RuntimeException.class);

        // 抛出 RuntimeException 或 Error 类型异常, 或者指定类型异常, 最终抛出 exp 为 IOException 类型
        thenThrownBy(() -> {
            Throwables.propagateIfPossible(exp, NullPointerException.class, IOException.class);
        }).isInstanceOf(IOException.class);

        // 抛出 RuntimeException 或 Error 类型异常, 或者指定类型异常, 最终抛出 rtExp 为 RuntimeException 类型
        thenThrownBy(() -> {
            Throwables.propagateIfPossible(rtExp, NullPointerException.class, IOException.class);
        }).isInstanceOf(RuntimeException.class);

        // 抛出一个异常链
        try {
            try {
                try {
                    // 抛出根异常
                    throw new IOException();
                } catch (IOException e) {
                    // 根异常包装后抛出
                    throw new IllegalStateException(e);
                }
            } catch (Exception e) {
                // 异常包装后抛出
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            // 确认最终捕获到的是外层异常
            then(e).isInstanceOf(RuntimeException.class);

            // 在异常链上获取 IllegalStateException 类型异常
            var cause = Throwables.getCauseAs(e, IllegalStateException.class);
            then(cause).isInstanceOf(IllegalStateException.class);

            // 在异常链上获取根异常
            var rootCause = Throwables.getRootCause(e);
            then(rootCause).isInstanceOf(IOException.class);

            // 获取异常链
            var chain = Throwables.getCausalChain(e);
            then(chain).hasSize(3);
            then(chain.get(0).getClass()).isSameAs(RuntimeException.class);
            then(chain.get(1).getClass()).isSameAs(IllegalStateException.class);
            then(chain.get(2).getClass()).isSameAs(IOException.class);

            // 将异常堆栈转化为字符串形式
            var stack = Throwables.getStackTraceAsString(e);
            then(stack).startsWith("java.lang.RuntimeException: java.lang.IllegalStateException: java.io.IOException");
        }
    }
}
