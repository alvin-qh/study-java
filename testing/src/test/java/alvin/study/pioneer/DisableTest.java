package alvin.study.pioneer;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junitpioneer.jupiter.DisableIfTestFails;
import org.junitpioneer.jupiter.DisabledUntil;

/**
 * 测试通过 Pioneer 库对测试方法进行临时禁用
 *
 * <p>
 * {@link DisabledUntil @DisabledUntil} 注解可以在指定日期前禁用测试
 * </p>
 *
 * <p>
 * {@link DisableIfTestFails @DisableIfTestFails} 可以在某个前置测试失败后, 禁止执行后续的测试. 一般而言, 如果一系列测试通过
 * {@link Order @Order} 注解指定了执行顺序, 则前置的测试会作为后续测试正确的条件,如果前置测试失败, 则后续的测试也无必要执行, 此时可通过
 * {@link DisableIfTestFails @DisableIfTestFails} 达成此目标
 * </p>
 *
 * <p>
 * {@link DisableIfTestFails @DisableIfTestFails} 注解的 {@code with} 属性可以指定一个异常类型, 表示当前置测试因为此异常失败, 则
 * 该注解生效
 * </p>
 *
 * <p>
 * {@link DisableIfTestFails @DisableIfTestFails} 注解的 {@code onAssertion} 属性可以指定前置测试是否是因为断言导致的失败, 默认为
 * {@code true}, 如果设置为 {@code false}, 则断言失败并不会导致后续测试禁用
 * </p>
 */
@DisableIfTestFails
@TestMethodOrder(OrderAnnotation.class)
class DisableTest {
    /**
     * 通过 {@link DisabledUntil} 可以在指定日期前禁用测试方法
     */
    @Test
    @DisabledUntil(date = "2090-12-31", reason = "Disable test before I die")
    void disable_shouldDisableTestBeforeTheSpecifiedDate() {
        fail();
    }

    /**
     * 本例中, 该测试作为 {@link #disable_shouldSubsequentTestDisabled()} 测试的前置测试, 应当失败
     *
     * <p>
     * 演示时需去掉 {@link Disabled @Disabled} 注解
     * </p>
     */
    @Test
    @Order(1)
    @Disabled("Just a demo")
    void disable_thePrepositiveTestMustFailed() {
        fail();
    }

    /**
     * 由于 {@link #disable_thePrepositiveTestMustFailed()} 前置测试失败, 所以该测试不会被执行
     *
     * <p>
     * 演示时需去掉 {@link Disabled @Disabled} 注解
     * </p>
     */
    @Test
    @Order(2)
    @Disabled("Just a demo")
    void disable_shouldSubsequentTestDisabled() {
        fail();
    }
}
