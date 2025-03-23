package alvin.study.guava.base;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;

import com.google.common.base.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 演示 {@link Optional} 的使用
 *
 * <p>
 * {@link Optional} 类型用于包装一个引用, 并提供了一系列方法来判断引用是否为
 * {@code null} 值
 * </p>
 *
 * <p>
 * JDK 8 以后的版本具备类似功能, 即 {@link java.util.Optional} 类型,
 * 其功能较 Guava 库提供的更为丰富, 推荐使用 JDK 自带的
 * {@link java.util.Optional} 工具类
 * </p>
 */
class OptionalTest {
    /**
     * 将一个非 {@code null} 对象包装为 {@link Optional} 对象
     *
     * <p>
     * {@link Optional#of(Object)} 方法可以将一个非 {@code null} 的引用包装为
     * {@link Optional} 对象
     * </p>
     *
     * <p>
     * {@link Optional} 对象的 {@link Optional#isPresent()}
     * 方法返回其包装的引用是否不为 {@code null} 值
     * </p>
     *
     * <p>
     * {@link Optional} 对象的 {@link Optional#get()} 方法返回其包装的引用
     * </p>
     */
    @Test
    void of_shouldWrapNonNullReference() {
        var obj = new Object();

        // 通过一个非 null 引用构建 Optional 对象
        var opt = Optional.of(obj);

        // 确认 Optional 对象包含了非 null 引用
        then(opt.isPresent()).isTrue();

        // 获取 Optional 对象中存储的引用, 确认引用正确
        then(opt.get()).isSameAs(obj);

        // 如果参数为 null, 则抛出异常
        thenThrownBy(() -> Optional.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    /**
     * 将一个 {@code null} 对象包装为 {@link Optional} 对象
     *
     * <p>
     * {@link Optional#fromNullable(Object)} 方法可以将一个可能 {@code null}
     * 的引用包装为 {@link Optional} 对象
     * </p>
     *
     * <p>
     * {@link Optional#absent()} 方法直接产生一个包含 {@code null} 引用的
     * {@link Optional} 对象
     * </p>
     *
     * <p>
     * 如果 {@link Optional} 对象存储的引用为 {@code null}, 则 {@link Optional#get()}
     * 方法会引发异常
     * </p>
     *
     * <p>
     * {@link Optional#orNull()} 方法可以在 {@link Optional} 对象包含 {@code null}
     * 引用时返回 {@code null} 值, 否则返回其存储的引用值, 在不强制要求从 {@link Optional}
     * 中获取的引用是否为 {@code null} 时, 可采用此方法
     * </p>
     *
     * <p>
     * {@link Optional} 对象的 {@code or} 系列方法提供了在对象存储引用为 {@code null} 时,
     * 如何返回另一个值, 包括:
     * <ul>
     * <li>
     * {@link Optional#or(Object)} 方法, 返回非 {@code null} 引用值或另一个指定的引用值
     * </li>
     * <li>
     * {@link Optional#or(Optional)} 方法, 返回当前对象本身或另一个 {@link Optional} 对象
     * </li>
     * <li>
     * {@link Optional#or(com.google.common.base.Supplier) Optional.or(Supplier)} 方法,
     * 返回非 {@code null} 引用值或所给 Lambda 表达式的返回值
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void fromNullable_shouldWrapNullableReference() {
        // 通过一个 null 引用构建 Optional 对象
        var opt = Optional.fromNullable(null);

        // 确认 Optional 对象包含了 null 引用
        then(opt.isPresent()).isFalse();

        // 确认通过 get 方法获取引用时会引发异常
        thenThrownBy(() -> opt.get()).isInstanceOf(IllegalStateException.class);

        // 通过 orNull 方法在 Optional 对象存储的引用为 null 时也返回 null 值
        then(opt.orNull()).isNull();

        // or 方法将在 Optional 对象存储 null 引用时返回的默认值
        then(opt.or("Other")).isEqualTo("Other");

        // or 方法将在 Optional 对象存储 null 引用时返回另一个 Optional 对象
        then(opt.or(Optional.of("Other")).get()).isEqualTo("Other");

        // or 方法将在 Optional 对象存储 null 引用时返回一个 Lambda 表达式的结果
        then(opt.or(() -> "Other")).isEqualTo("Other");

        // 通过 absent 方法返回一个包含 null 引用的 Optional 对象
        then(Optional.absent().isPresent()).isFalse();
    }

    /**
     * 将 {@link Optional} 对象中的引用放入一个 {@link java.util.Set} 集合中
     *
     * <p>
     * {@link Optional#asSet()} 方法返回一个只读的, 只包含一个元素的集合,
     * 该方法的主要作用是可以通过迭代循环来保证循环体内的代码只有在 {@link Optional}
     * 对象中存储的引用不为 {@code null} 时执行
     * </p>
     *
     * <p>
     * 对于 JDK 8 及以上的版本, 可以通过
     * {@link java.util.Optional#ifPresent(java.util.function.Consumer)
     * Optional.ifPresent(Consumer)} 方法来达到同样的目的
     * </p>
     *
     * <p>
     * 对于 JDK 9 及以上的版本, 可以通过 {@link java.util.Optional#stream()
     * Optional.stream()} 方法得到一个 {@link java.util.stream.Stream Stream}
     * 对象, 通过该对象达成同样的目的
     * </p>
     *
     * <p>
     * 可以通过 {@link Optional#toJavaUtil()} 方法将对象转为 JDK 的
     * {@link java.util.Optional Optional} 类型对象
     * </p>
     */
    @Test
    void asSet_shouldConvertToSingletonSet() {
        // 在 Optional 中存入一个整数对象
        var opt = Optional.of(100);

        var n = 0;

        // 通过 asSet 方法和循环, 确保只有在 Optional 对象中存储非 null 引用时, 才会执行循环体
        for (var val : opt.asSet()) {
            then(val).isEqualTo(100);
            n++;
        }
        // 确认循环执行了一次
        then(n).isEqualTo(1);

        // 将 Guava Optional 对象转为 Java Optional 对象
        // 通过 ifPresentOrElse 方法根据 Optional 对象中存储引用的情况执行不同的 Lambda 表达式
        opt.toJavaUtil().ifPresentOrElse(
            val -> then(val).isEqualTo(100),
            Assertions::fail);

        // 将 Guava Optional 对象转为 Java Optional 对象
        // 通过将 Optional 对象转为 Stream 对象, 利用 Stream 对象的能力完成后续操作
        opt.toJavaUtil().ifPresent(val -> then(val).isEqualTo(100));
    }

    /**
     * 将一个集合中的 {@link Optional} 元素展开为其包含的引用值
     *
     * <p>
     * 通过 {@link Optional#presentInstances(Iterable)} 将一个集合进行展开
     * </p>
     *
     * <p>
     * 所谓展开, 即将一个 {@code Collection<Optional<T>>} 类型的集合展开为
     * {@code Collection<T>} 类型
     * </p>
     *
     * <p>
     * 在 JDK 8 及以上版本, 可以通过
     * {@link java.util.stream.Stream#map(java.util.function.Function)
     * Stream.map(Function)} 完成同类操作
     * </p>
     */
    @Test
    void presentInstances_shouldFlatOptionalInCollection() {
        // 将集合中的 Optional 对象展开
        var ints = Optional.presentInstances(
            List.of(Optional.of(1), Optional.of(2)));
        then(ints).containsExactly(1, 2);
    }
}
