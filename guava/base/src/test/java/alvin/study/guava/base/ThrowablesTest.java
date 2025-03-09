package alvin.study.guava.base;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.io.IOException;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import org.junit.jupiter.api.Test;

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
 * {@link Throwables#throwIfInstanceOf(Throwable, Class)}
 * 方法表示当异常为指定类型异常时抛出该异常
 * </li>
 * <li>
 * {@link Throwables#throwIfUnchecked(Throwable)} 方法表示当异常为
 * {@link RuntimeException} 或 {@link Error} 类型时抛出该异常
 * </li>
 * <li>
 * {@link Throwables#propagateIfPossible(Throwable, Class, Class)}
 * 方法表示当异常为 {@link RuntimeException} 或 {@link Error} 类型,
 * 又抑或是指定的异常类型时, 抛出该异常
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link Throwables} 类提供了三类方法简化异常判断和处理
 * <ul>
 * <li>
 * {@link Throwables#getCauseAs(Throwable, Class)}
 * 可以从异常链上获取期望类型的异常
 * </li>
 * <li>
 * {@link Throwables#getRootCause(Throwable)} 可以获取异常链的根异常,
 * 即最初抛出的异常
 * </li>
 * <li></li>
 * </ul>
 * </p>
 */
class ThrowablesTest {
    /**
     * 根据异常类型选择抛出异常
     *
     * <p>
     * 通过 {@link Throwables#throwIfInstanceOf(Throwable, Class)} 方法,
     * 如果第一个参数的异常和第二个参数的类型匹配, 则抛出该异常, 否则不抛出任何异常
     * </p>
     */
    @Test
    void throwIfInstanceOf_shouldThrowSpecifiedException() {
        var exception = new IOException();

        // 对于类型不匹配的情况, 不抛出异常
        thenCode(() -> Throwables.throwIfInstanceOf(exception, NullPointerException.class))
                .doesNotThrowAnyException();

        // 抛出指定类型异常, 最终抛出 exp 为 IOException 类型
        thenThrownBy(() -> Throwables.throwIfInstanceOf(exception, IOException.class))
                .isInstanceOf(IOException.class);
    }

    /**
     * 抛出 {@link RuntimeException} 类型异常
     *
     * <p>
     * 通过 {@link Throwables#throwIfUnchecked(Throwable)} 方法,
     * 如果指定异常为 {@link RuntimeException} 类型, 则抛出该异常,
     * 否则不抛出任何异常
     * </p>
     */
    @Test
    void throwIfUnchecked_shouldThrowUncheckedException() {
        var exception = new IOException();

        // 对于不是 RuntimeException 的情况, 不抛出异常
        thenCode(() -> Throwables.throwIfUnchecked(exception))
                .doesNotThrowAnyException();

        var runtimeException = new IllegalArgumentException();

        // 抛出 RuntimeException 类型的异常
        thenThrownBy(() -> Throwables.throwIfUnchecked(runtimeException))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * 抛出指定类型, {@link RuntimeException} 或 {@link Error} 类型异常
     *
     * <p>
     * 对于 {@link Throwables#propagateIfPossible(Throwable, Class, Class)}
     * 方法, 其第一个参数为异常对象, 且在符合如下三个条件之一时, 抛出该异常
     * <ul>
     * <li>
     * 异常参数的类型和指定的异常类型一致
     * </li>
     * <li>
     * 异常参数为 {@link RuntimeException} 类型
     * </li>
     * <li>
     * 异常参数为 {@link Error} 类型
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void propagateIfPossible_shouldThrowSpecifiedExceptionOrUncheckedException() {
        var exception = new IOException();

        // 如果异常类型和指定异常类型不匹配, 则不抛出任何异常
        thenCode(() -> Throwables.throwIfInstanceOf(exception, IndexOutOfBoundsException.class))
                .doesNotThrowAnyException();

        // 如果异常类型和指定异常类型匹配, 则抛出该异常
        thenThrownBy(() -> Throwables.throwIfInstanceOf(exception, IOException.class))
                .isInstanceOf(IOException.class);

        var runtimeException = new IllegalArgumentException();

        // 如果异常类型为 RuntimeException 类型, 则抛出该异常
        thenThrownBy(() -> Throwables.throwIfUnchecked(runtimeException))
                .isInstanceOf(RuntimeException.class);

        var error = new OutOfMemoryError();

        // 如果异常类型为 Error 类型, 则抛出该异常
        thenThrownBy(() -> Throwables.throwIfUnchecked(error))
                .isInstanceOf(OutOfMemoryError.class);
    }

    /**
     * 根据所给的异常类型抛出异常
     *
     * @param rootType    根异常类型
     * @param passingType 传递过程中异常类型
     * @param raiseType   最后返回的异常类型
     * @return {@code raiseType} 参数指定类型的异常对象
     */
    Throwable makeException(
            Class<? extends Throwable> rootType,
            Class<? extends Throwable> passingType,
            Class<? extends Throwable> raiseType) {

        Throwable result;

        // 抛出一个异常链
        try {
            try {
                try {
                    // 抛出根异常
                    throw rootType.getConstructor(String.class)
                            .newInstance("ROOT");
                } catch (Throwable e) {
                    if (passingType == null) {
                        throw e;
                    }
                    // 根异常包装后抛出
                    throw passingType.getConstructor(String.class, Throwable.class)
                            .newInstance("PASSING", e);
                }
            } catch (Exception e) {
                if (raiseType == null) {
                    throw e;
                }
                // 异常包装后抛出
                throw raiseType.getConstructor(String.class, Throwable.class)
                        .newInstance("RAISE", e);
            }
        } catch (Throwable e) {
            result = e;
        }

        // 返回捕获的异常
        return Preconditions.checkNotNull(result);
    }

    /**
     * 根据一个异常对象, 获取导致该异常的上一级异常对象
     *
     * <p>
     * 在构造一个异常对象时, 可以指定引发该异常的前一个异常, 即 {@code cause},
     * 表示引发当前异常的原因. 这通常是通过 {@link Exception#Exception(Throwable)}
     * 构造器在实例化异常对象时指定, 从而组成异常链
     * </p>
     *
     * <p>
     * {@link Throwables#getCauseAs(Throwable, Class)} 方法可以获取一个异常对象的原因,
     * 即导致该异常对象的前一个异常对象
     * </p>
     *
     * <p>
     * {@link Throwables#getCauseAs(Throwable, Class)} 方法的第二个参数是一个异常类型,
     * 即如果当前异常的原因不是预期类型的异常, 则无法完成操作
     * </p>
     *
     * <p>
     * 特别的, 如果一个异常没有引发原因, 即没有上一级异常, 则
     * {@link Throwables#getCauseAs(Throwable, Class)} 方法返回 {@code null} 值
     * </p>
     */
    @Test
    void getCauseAs_shouldGetExceptionByCauses() {
        // 构造一个异常对象
        var exception = makeException(
            IllegalArgumentException.class,
            IOException.class,
            IllegalStateException.class);
        then(exception).isInstanceOf(IllegalStateException.class);

        // 获取异常的异常原因, 应为 IOException 类型, 确认可以正确获取到
        Throwable cause = Throwables.getCauseAs(
            exception, IOException.class);
        then(cause).isInstanceOf(IOException.class)
                .hasMessage("PASSING");

        // 获取上一步返回值的异常原因, 应为 IllegalArgumentException 类型, 确认可以正确获取到
        cause = Throwables.getCauseAs(
            Objects.requireNonNull(cause),
            IllegalArgumentException.class);
        then(cause).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ROOT");

        // 确认上一步返回值无异常原因
        cause = Throwables.getCauseAs(
            Objects.requireNonNull(cause),
            Exception.class);
        then(cause).isNull();

        // 如果期待的类型和实际异常原因类型不符, 则抛出类型转换失败异常
        thenThrownBy(() -> Throwables.getCauseAs(exception, IllegalStateException.class))
                .isInstanceOf(ClassCastException.class);
    }

    /**
     * 根据一个异常对象, 获取该异常链上所有的异常
     *
     * <p>
     * 在构造一个异常对象时, 可以指定引发该异常的前一个异常, 即 {@code cause},
     * 表示引发当前异常的原因. 这通常是通过 {@link Exception#Exception(Throwable)}
     * 构造器在实例化异常对象时指定, 从而组成异常链
     * </p>
     *
     * <p>
     * {@link Throwables#getCausalChain(Throwable)} 方法可以获取到异常链上的所有异常,
     * 返回一个异常列表集合
     * </p>
     */
    @Test
    void getCausalChain_shouldGetAllCausesOnExceptionChain() {
        // 构造一个异常对象
        var exception = makeException(
            IllegalArgumentException.class,
            IOException.class,
            IllegalStateException.class);
        then(exception).isInstanceOf(IllegalStateException.class);

        // 获取异常链
        var chain = Throwables.getCausalChain(exception);
        then(chain).map(c -> (Object) c.getClass())
                .containsExactly(
                    IllegalStateException.class,
                    IOException.class,
                    IllegalArgumentException.class);
    }

    /**
     * 根据一个异常对象, 将其异常链以字符串形式
     *
     * <p>
     * 在构造一个异常对象时, 可以指定引发该异常的前一个异常, 即 {@code cause},
     * 表示引发当前异常的原因. 这通常是通过 {@link Exception#Exception(Throwable)}
     * 构造器在实例化异常对象时指定, 从而组成异常链
     * </p>
     *
     * <p>
     * {@link Throwables#getStackTraceAsString(Throwable)}
     * 方法可以获取到异常链上的所有异常, 以字符串形式返回
     * </p>
     */
    @Test
    void throwable_shouldGetExceptionStackAsString() {
        // 构造一个异常对象
        var exception = makeException(
            IllegalArgumentException.class,
            IOException.class,
            IllegalStateException.class);
        then(exception).isInstanceOf(IllegalStateException.class);

        // 将异常堆栈转化为字符串形式
        var stack = Throwables.getStackTraceAsString(exception);
        then(stack)
                .contains("java.lang.IllegalArgumentException")
                .contains("java.io.IOException")
                .contains("java.lang.IllegalStateException");
    }
}
