package alvin.study.testing.testng;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * 测试 TestNG 的断言库
 *
 * <p>
 * TestNG 是一个和 JUnit 相似的测试框架, 适用范围也较为广泛
 * </p>
 */
public class AssertionTest {
    /**
     * 测试 TestNG 的断言
     *
     * <p>
     * {@link org.testng.Assert Assert} 类中的所有断言方法都具备设置"断言信息"的方式
     * </p>
     *
     * <p>
     * 可以直接在断言方法的最后一个参数传入 {@link String} 值, 表示当断言失败后显示的错误信息
     * </p>
     */
    @Test
    void assert_shouldAssertionWithMessage() {
        assertTrue(true, "n must equals to 0");
        assertFalse(false, String.format("%s is not falsely", false));
    }

    /**
     * 无条件断言
     *
     * <p>
     * {@link org.testng.Assert#fail() Assert.fail()} 方法会令测试无条件失败
     * </p>
     */
    @Test
    void fail_shouldCauseUnconditionalFail() {
        try (var out = new ByteArrayOutputStream()) {
            // 执行测试代码
        } catch (IOException e) {
            fail("cannot perform here");
        }
    }

    /**
     * 对一个 {@code boolean} 结果的条件表达式进行断言
     */
    @Test
    void condition_shouldAssertTrueOrFalse() {
        var n = 0;

        assertTrue(n == 0);
        assertFalse(n != 0);
    }

    /**
     * 对 {@code null} 或非 {@code null} 进行断言
     */
    @Test
    void null_shouldAssertNullOrNonnullValue() {
        Object x = null;
        assertNull(x);

        x = "x";
        assertNotNull(x);
    }

    /**
     * 对等值和非等值情况进行断言
     */
    @Test
    void equal_shouldAssertByEqual() {
        var name = "Emma";

        assertEquals(name, "Emma");
        assertNotEquals(name, "Alvin");
    }

    /**
     * 对数组内容进行等值断言
     *
     * <p>
     * {@link org.testng.Assert#assertEquals(Object[], Object[]) Assert.assertEquals(Object[], Object[])}
     * 方法可用于断言两个集合是否等值
     * </p>
     */
    @Test
    void equal_shouldAssertArrayEqual() {
        var nums = new Integer[]{ 1, 2, 3 };

        assertEquals(nums, new Integer[]{ 1, 2, 3 });
        assertNotEquals(nums, new Integer[]{ 1, 2, 3, 4 });
    }

    /**
     * 对集合内容进行等值断言
     *
     * <p>
     * {@link org.testng.Assert#assertEquals(java.util.Collection, java.util.Collection)
     * Assert.assertEquals(Collection, Collection)} 方法可用于断言两个集合是否等值
     * </p>
     */
    @Test
    void equal_shouldAssertCollectionEqual() {
        var chars = List.of('A', 'B', 'C');

        assertEquals(chars, Arrays.asList('A', 'B', 'C'));
        assertNotEquals(chars, Arrays.asList('A', 'B', 'C', 'D'));
    }

    /**
     * 对引用是否相等进行断言
     */
    @Test
    void same_shouldAssertObjectReference() {
        var obj1 = new Object();

        var obj2 = obj1;
        assertSame(obj2, obj1);

        obj2 = new Object();
        assertNotSame(obj1, obj2);
    }

    /**
     * 对是否抛出异常进行断言
     */
    @Test
    void shouldAssertWhenExceptionThrows() {
        // 断言指定的代码是否抛出了期待的异常
        assertThrows(IOException.class, () -> {
            throw new IOException();
        });
    }

    /**
     * 对执行是否超时进行断言
     *
     * <p>
     * {@link Test @Test} 注解的 {@code timeOut} 属性指定整个测试方法的执行时间 (毫秒), 超出该时间范围, 则表示测试失败
     * </p>
     */
    @Test(timeOut = 100)
    void timeout_shouldTestExecutionTimeout() throws InterruptedException {
        // 断言执行时间是否在 100ms 以内
        Thread.sleep(90);
    }
}
