package alvin.study.guava.common;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * 演示 {@link Preconditions} 类, 进行代码执行前置条件的检查
 *
 * <p>
 * 所谓代码执行前置条件包括: 方法参数是否合法, 变量是否为 {@code null}, 集合索引值是否正确, 包括:
 * <ul>
 * <li>{@link Preconditions#checkArgument(boolean, String, Object...)}, 检查参数是否合法</li>
 * <li>{@link Preconditions#checkState(boolean, String, Object...)}, 检查状态值是否有效</li>
 * <li>{@link Preconditions#checkNotNull(Object, String, Object...)}, 检查引用是否不为 {@code null}</li>
 * <li>{@link Preconditions#checkElementIndex(int, int, String)}, 检查下标索引是否在集合长度允许范围内</li>
 * <li>{@link Preconditions#checkPositionIndex(int, int, String)}, 检查位置索引是否在集合长度允许范围内</li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link Preconditions} 类的某些方法已经被 JDK 类似方法取代, 例如 {@link java.util.Objects#requireNonNull(Object)
 * Objects.requireNonNull(Object)}, {@link java.util.Objects#checkIndex(int, int) Objects.checkIndex(int, int)}
 * 以及 {@link java.util.Objects#checkFromIndexSize(int, int, int) Objects.checkFromIndexSize(int, int, int)} 等
 * </p>
 */
class PreconditionsTest {
    /**
     * 通过一个条件表达式, 对参数值进行检查
     *
     * <p>
     * {@link Preconditions#checkArgument(boolean, String, Object...)} 方法的第一个参数为条件表达式, 后续参数为一个错误信息模板
     * 以及模板参数. 如果所给的条件表达式为 {@code false}, 则抛出 {@link IllegalArgumentException} 异常, 并携带所给的错误信息
     * </p>
     *
     * <p>
     * 注意: 错误信息模板字符串中只支持 {@code %s} 占位符
     * </p>
     */
    @Test
    void argument_shouldCheckArgumentByCondition() {
        var arg = 10;

        // 检查参数是否符合要求, 即通过一个条件表达式对参数进行检查,
        thenThrownBy(() -> Preconditions.checkArgument(arg > 10)).isInstanceOf(IllegalArgumentException.class);
        thenThrownBy(() -> Preconditions.checkArgument(arg > 10, "Expect n > 10, but n = %s", arg))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expect n > 10, but n = %s", arg);
    }

    /**
     * 通过一个条件表达式, 对指定状态值进行检查
     *
     * <p>
     * 所谓"状态", 就是一个值, 通过一个条件表达式查看该值是否符合程序执行的要求
     * </p>
     *
     * <p>
     * {@link Preconditions#checkState(boolean, String, Object...)} 方法的第一个参数为条件表达式, 后续参数为一个错误信息模板
     * 以及模板参数. 如果所给的条件表达式为 {@code false}, 则抛出 {@link IllegalStateException} 异常, 并携带所给的错误信息
     * </p>
     *
     * <p>
     * 注意: 错误信息模板字符串中只支持 {@code %s} 占位符
     * </p>
     */
    @Test
    void state_shouldCheckStateByCondition() {
        var state = 10;

        // 检查状态是否符合要求, 即通过一个 boolean 表达式对参数进行检查,
        thenThrownBy(() -> Preconditions.checkState(state == 0)).isInstanceOf(IllegalStateException.class);
        thenThrownBy(() -> Preconditions.checkState(state == 0, "Expect n is zero"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expect n is zero");
    }

    /**
     * 对引用进行非 {@code null} 检查
     *
     * <p>
     * {@link Preconditions#checkNotNull(Object, String, Object...))} 方法的第一个参数为条件表达式, 后续参数为一个错误信息模板以及
     * 模板参数. 如果所给参数引用为 {@code null}, 则抛出 {@link NullPointerException} 异常, 并携带所给的错误信息
     * </p>
     *
     * <p>
     * 如果所给参数不为 {@code null}, 则 {@link Preconditions#checkNotNull(Object)} 方法的返回值是所给参数引用本身
     * </p>
     *
     * <p>
     * 注意: 错误信息模板字符串中只支持 {@code %s} 占位符
     * </p>
     */
    @Test
    void nullCheck_shouldCheckReferenceIfNullOrNot() {
        var nullValue = (Object) null;

        // 检查一个引用是否不为 null
        thenThrownBy(() -> Preconditions.checkNotNull(nullValue)).isInstanceOf(NullPointerException.class);
        thenThrownBy(() -> Preconditions.checkNotNull(nullValue, "Expect obj not null"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Expect obj not null");

        // 检查一个引用是否不为 null, 如果不为 null, 返回该引用本身
        var nonNullValue = new Object();
        then(Preconditions.checkNotNull(nonNullValue)).isSameAs(nonNullValue);

    }

    /**
     * 检查所给的下标索引值是否超出集合的元素索引范围
     *
     * <p>
     * 下标索引值 {@code index} 的正确取值范围为 {@code 0 <= index < size},
     * </p>
     *
     * <p>
     * {@link Preconditions#checkElementIndex(int, int, String)} 方法的第一个参数为索引值, 第二个参数为集合元素个数, 第三个参数为
     * 所检查索引值的简单描述. 如果第一个参数的值不在所给集合元素个数范围内, 则抛出 {@link IndexOutOfBoundsException} 异常
     * </p>
     *
     * <p>
     * 如果所给的索引值正确, 则 {@link Preconditions#checkElementIndex(int, int, String)} 方法返回索引值本身
     * </p>
     */
    @CsvSource({
            "0,valid",
            "3,valid",
            "4,invalid"
    })
    @ParameterizedTest
    void elementIndex_shouldCheckElementIndexOfList(int index, String state) {
        var list = ImmutableList.of(1, 2, 3, 4);

        if ("valid".equals(state)) {
            // 检查一个元素索引值是否正确, 如果正确, 则返回该索引值本身
            then(Preconditions.checkElementIndex(index, list.size())).isEqualTo(index);
        } else {
            // 检查一个元素索引值是否正确, 如果不正确, 则抛出 IndexOutOfBoundsException 异常
            // 使用默认索引描述 (默认为 "index")
            thenThrownBy(() -> Preconditions.checkElementIndex(index, list.size()))
                    .isInstanceOf(IndexOutOfBoundsException.class)
                    .hasMessage("index (%s) must be less than size (%s)", index, list.size());
            // 自定义索引描述
            thenThrownBy(() -> Preconditions.checkElementIndex(index, list.size(), "Argument n"))
                    .isInstanceOf(IndexOutOfBoundsException.class)
                    .hasMessage("Argument n (%s) must be less than size (%s)", index, list.size());
        }
    }

    /**
     * 检查所给的位置索引值是否超出集合的元素索引范围
     *
     * <p>
     * 位置索引值 {@code index} 的正确取值范围为 {@code 0 <= index <= size},
     * </p>
     *
     * <p>
     * {@link Preconditions#checkPositionIndex(int, int, String)} 方法的第一个参数为索引值, 第二个参数为集合元素个数, 第三个参数为
     * 所检查索引值的简单描述. 如果第一个参数的值不在所给集合元素个数范围内, 则抛出 {@link IndexOutOfBoundsException} 异常
     * </p>
     *
     * <p>
     * 如果所给的索引值正确, 则 {@link Preconditions#checkPositionIndex(int, int, String)} 方法返回索引值本身
     * </p>
     */
    @CsvSource({
            "0,valid",
            "4,valid",
            "5,invalid"
    })
    @ParameterizedTest
    void preconditions_shouldCheckValuesByPreconditions(int index, String state) {
        var list = ImmutableList.of(1, 2, 3, 4);

        if ("valid".equals(state)) {
            // 检查一个元素索引值是否正确, 如果正确, 则返回该索引值本身
            then(Preconditions.checkPositionIndex(index, list.size())).isEqualTo(index);
        } else {
            // 检查一个元素索引值是否正确, 如果不正确, 则抛出 IndexOutOfBoundsException 异常
            // 使用默认索引描述 (默认为 "index")
            thenThrownBy(() -> Preconditions.checkPositionIndex(index, list.size()))
                    .isInstanceOf(IndexOutOfBoundsException.class)
                    .hasMessage("index (%s) must not be greater than size (%s)", index, list.size());
            // 自定义索引描述
            thenThrownBy(() -> Preconditions.checkPositionIndex(index, list.size(), "Argument n"))
                    .isInstanceOf(IndexOutOfBoundsException.class)
                    .hasMessage("Argument n (%s) must not be greater than size (%s)", index, list.size());
        }
    }
}
