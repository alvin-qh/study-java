package alvin.study.guava.common;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.base.Verify;
import com.google.common.base.VerifyException;

/**
 * 测试运行时条件检验方法
 *
 * <p>
 * {@link Verify} 类型的 {@code verify} 方法可以对一个条件表达式进行检验, 如果表达式为 {@code true} 表示通过检验, 如果为
 * {@code false} 则抛出 {@link VerifyException} 异常
 * </p>
 */
class VerifyTest {
    /**
     * 对条件表达式进行检验
     *
     * <p>
     * {@link Verify#verify(boolean, String, Object...)} 方法的第一个参数为条件表达式, 后续参数为一个错误信息模板
     * 以及模板参数. 如果所给的条件表达式为 {@code false}, 则抛出 {@link VerifyException} 异常, 并携带所给的错误信息
     * </p>
     *
     * <p>
     * 注意: 错误信息模板字符串中只支持 {@code %s} 占位符
     * </p>
     */
    @Test
    void verify_shouldVerifyByConditionsAtRuntime() {
        var a = 10;

        // 通过检验, 此时错误信息参数被忽略
        Verify.verify(a == 10, "a must equal to %s", 10);

        // 条件表达式为 false 时, 检验失败, 抛出异常
        thenThrownBy(() -> Verify.verify(a > 10)).isInstanceOf(VerifyException.class);

        // 条件表达式为 false 时, 检验失败 抛出异常且定义异常信息
        thenThrownBy(() -> Verify.verify(a > 10, "a must great than %s", 10))
                .isInstanceOf(VerifyException.class)
                .hasMessage("a must great than %s", 10);
    }
}
