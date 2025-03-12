package alvin.study.testing.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import alvin.study.testing.testcase.model.User;

/**
 * 测试 junit 框架的断言方法
 */
class AssertionTest {
    /**
     * 演示输出"断言信息"
     *
     * <p>
     * {@link org.junit.jupiter.api.Assertions Assertions} 类中的所有断言方法,
     * 都具备两种设置"断言信息"的方式
     * <ul>
     * <li>
     * 可以直接在断言方法的最后一个参数传入 {@link String} 对象, 表示当断言失败后显示的错误信息
     * </li>
     * <li>
     * 也可以在断言方法的最后一个参数传入一个 {@link java.util.function.Supplier Supplier}
     * 类型的参数, 表示当断言失败后获取错误信息的 lambda 表达式
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void assert_shouldAssertWithMessage() {
        assertTrue(true, "n must equals to 0");
        assertFalse(false, () -> String.format("%s is not falsely", false));
    }

    /**
     * 无条件断言
     *
     * <p>
     * 只要执行到 {@link org.junit.jupiter.api.Assertions#fail() Assertions.fail()} 方法,
     * 测试即失败
     * </p>
     */
    @Test
    void fail_shouldFailedTestProcess() {
        try (var ignored = new ByteArrayOutputStream()) {
            // do io operation
        } catch (IOException e) {
            fail("cannot perform here");
        }
    }

    /**
     * 对值为 {@code true} 和 {@code false} 的情况进行断言
     *
     * <p>
     * {@link Assertions#assertTrue(boolean)} 和
     * {@link Assertions#assertFalse(boolean)} 方法用于对一个 {@code boolean} 类型值进行断言
     * </p>
     */
    @Test
    void trueOrFalse_shouldAssertCondition() {
        var n = 0;

        assertTrue(n == 0);
        assertFalse(n != 0);
    }

    /**
     * 对对象引用是否为 {@code null} 的情况进行断言
     *
     * <p>
     * {@link Assertions#assertNull(Object)} 和
     * {@link Assertions#assertNotNull(Object)} 方法用于对一个对象引用是否为
     * {@code null} 进行断言
     * </p>
     */
    @Test
    void nullOrNotNull_shouldAssertReferenceIsNullOrNonnullValue() {
        var x = (Object) null;
        assertNull(x);

        x = "x";
        assertNotNull(x);
    }

    /**
     * 对两个值是否相等进行断言
     *
     * <p>
     * 对于值类型, 是否相等是通过 {@code ==} 运算符返回的; 对于引用类型,
     * 是否相等的结果是通过 {@link Object#equals(Object)} 方法返回的
     * </p>
     *
     * <p>
     * {@link Assertions#assertEquals} 和 {@link Assertions#assertNotEquals}
     * 方法拥有大量的重载, 为所有可能需要比较的类型均提供了对应的方法
     * </p>
     */
    @Test
    void equalsOrNot_shouldAssertValueEquals() {
        var user = new User(1, "Alvin");

        assertEquals(1, user.getId());
        assertNotEquals("Emma", user.getName());
    }

    /**
     * 对集合内容是否相等进行断言
     *
     * <p>
     * {@link Assertions#assertArrayEquals(Object[], Object[])}
     * 用于对两个数组内容进行比较, 对其是否一致进行断言
     * </p>
     *
     * <p>
     * {@link Assertions#assertIterableEquals(Iterable, Iterable)}
     * 用于对两迭代器进行比较, 对其迭代的集合元素是否一致进行断言
     * </p>
     */
    @Test
    void equalsOrNot_shouldAssertArrayOrIterableEquals() {
        var nums = new Integer[] { 1, 2, 3 };
        assertArrayEquals(new Integer[] { 1, 2, 3 }, nums);

        var chars = List.of('A', 'B', 'C');
        assertIterableEquals(List.of('A', 'B', 'C'), chars);
    }

    /**
     * 对对象引用是否相同进行断言
     *
     * <p>
     * {@link Assertions#assertSame(Object, Object)} 用于比较两个引用是否相同,
     * 对其比较结果进行断言
     * </p>
     *
     * <p>
     * {@link Assertions#assertNotSame(Object, Object)} 用于比较两个引用是否不同,
     * 对其比较结果进行断言
     * </p>
     */
    @Test
    void sameOrNot_shouldAssertReferencesAreSame() {
        var obj1 = new Object();

        var obj2 = obj1;
        assertSame(obj2, obj1);

        obj2 = new Object();
        assertNotSame(obj1, obj2);
    }

    /**
     * 对执行的代码是否抛出异常进行断言
     *
     * <p>
     * {@link Assertions#assertThrows(Class, org.junit.jupiter.api.function.Executable)
     * Assertions.assertThrows(Class, Executable)} 用于执行
     * {@link org.junit.jupiter.api.function.Executable Executable} 中的代码,
     * 并断言是否抛出了指定异常
     * </p>
     *
     * <p>
     * {@link Assertions#assertDoesNotThrow(org.junit.jupiter.api.function.Executable)
     * Assertions.assertDoesNotThrow(Executable)} 用于执行
     * {@link org.junit.jupiter.api.function.Executable Executable} 中的代码,
     * 并断言执行过程中不会抛出异常
     * </p>
     */
    @Test
    void throwsOrNot_shouldAssertExceptionThrowsOrNot() {
        assertThrows(
            IOException.class,
            () -> throwException(IOException.class));

        assertDoesNotThrow(() -> throwException(null));
    }

    /**
     * 用于测试
     * {@link Assertions#assertThrows(Class, org.junit.jupiter.api.function.Executable)
     * Assertions.assertDoesNotThrow(Class, Executable)} 方法, 抛出指定的异常
     *
     * @param <E>           异常类型
     * @param exceptionType 要抛出异常的 {@link Class} 类型, {@code null} 表示不抛出异常
     */
    @SneakyThrows
    private <E extends Throwable> void throwException(Class<E> exceptionType) {
        if (exceptionType != null) {
            throw exceptionType.getDeclaredConstructor().newInstance();
        }
    }

    /**
     * 对执行是否超时进行断言
     *
     * <p>
     * {@link Assertions#assertTimeout(Duration, org.junit.jupiter.api.function.Executable)
     * Assertions.assertTimeout(Duration, Executable)} 用于执行
     * {@link org.junit.jupiter.api.function.Executable Executable} 中的代码,
     * 并断言执行时间不会超过 {@link Duration} 定义的时间范围
     * </p>
     */
    @Test
    void timeout_shouldAssertProcessTimeout() {
        // 断言执行时间是否在 100ms 以内
        assertTimeout(Duration.ofMillis(100), () -> Thread.sleep(90));

        // 具有一个返回值, 可以对返回值做进一步的断言
        var result = assertTimeout(Duration.ofMillis(100), () -> {
            Thread.sleep(90);
            return 90;
        });
        assertEquals(90, result);
    }

    /**
     * 组合多个断言, 当所有断言都成功则整体成功
     *
     * <p>
     * {@link Assertions#assertAll(org.junit.jupiter.api.function.Executable...)
     * Assertions#assertAll(Executable...)} 断言方法可以组合多个
     * {@link org.junit.jupiter.api.function.Executable Executable} 对象, 每个
     * {@link org.junit.jupiter.api.function.Executable Executable} 对象都为一段包含断言的代码
     * </p>
     *
     * <p>
     * 和独立使用多个断言不同,
     * {@link Assertions#assertAll(org.junit.jupiter.api.function.Executable...)
     * Assertions#assertAll(Executable...)} 方法会执行完所有的
     * {@link org.junit.jupiter.api.function.Executable Executable} 中的断言, 给出整体报告,
     * 而不会在某个断言失败后停止
     * </p>
     */
    @Test
    void all_shouldAssertAllConditions() {
        var user = new User(1, "Alvin");
        assertAll(
            () -> assertTrue(user.getId() == 1),
            () -> assertEquals("Alvin", user.getName()));
    }
}
