package alvin.study.testing.junit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

/**
 * 根据条件跳过测试
 *
 * <p>
 * {@link org.junit.jupiter.api.Assumptions Assumptions} 类中的方法用来跳过测试,
 * 即在指定条件满足的情况下, 达到和 {@link org.junit.jupiter.api.Disabled @Disabled} 注解达到类似的效果
 * </p>
 *
 * <p>
 * 跳过的测试会在测试报告中标记为 {@code SKIP}
 * </p>
 */
class AssumptionsTest {
    /**
     * {@link org.junit.jupiter.api.Assumptions#assumeTrue(boolean)
     * Assumptions.assumeTrue(boolean)} 和
     * {@link org.junit.jupiter.api.Assumptions#assumeTrue(java.util.function.BooleanSupplier)
     * Assumptions.assumeTrue(BooleanSupplier)} 方法可以在所给参数表达为 {@code false} 时跳出当前测试
     */
    @Test
    @DisplayName("😱")
    void assumeTrue_shouldSkipTestWhenAssumeTrueButGivenFalse() {
        assumeTrue(false, "skip when value is false");
        fail("cannot run to here");
    }

    /**
     * {@link org.junit.jupiter.api.Assumptions#assumeFalse(boolean)
     * Assumptions.assumeFalse(boolean)} 和
     * {@link org.junit.jupiter.api.Assumptions#assumeFalse(java.util.function.BooleanSupplier)
     * Assumptions.assumeFalse(BooleanSupplier)} 方法可以在所给参数表达为 {@code true} 时跳出当前测试
     */
    @Test
    void assumeFalse_shouldSkipTestWhenAssumeFalseButGivenTrue() {
        assumeFalse(true, () -> "skip when value is false");
        fail("cannot run to here");
    }

    /**
     * {@link org.junit.jupiter.api.Assumptions#assumingThat(boolean, org.junit.jupiter.api.function.Executable)
     * Assumptions.assumingThat(boolean, Executable)} 和
     * {@link org.junit.jupiter.api.Assumptions#assumingThat(java.util.function.BooleanSupplier, org.junit.jupiter.api.function.Executable)
     * assumingThat(BooleanSupplier, Executable)} 方法可以在前一个参数表达为 {@code true} 时,
     * 执行后一个参数传入的 lambda 表达式
     */
    @Test
    void assumingThat_shouldSkipTestByCondition() {
        assumingThat(() -> LocalTime.now().isBefore(LocalTime.of(12, 0, 0)), () -> {
            then(LocalTime.now()).isBefore(LocalTime.of(12, 0, 0));
        });
    }
}
