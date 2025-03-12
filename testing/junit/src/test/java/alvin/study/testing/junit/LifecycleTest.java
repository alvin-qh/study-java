package alvin.study.testing.junit;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 测试生命周期
 *
 * <p>
 * 为了允许隔离执行单个的测试方法, 并避免由于可变测试实例状态而产生的意外副作用,
 * JUnit 在执行每个测试方法之前创建每个测试类的新实例
 * </p>
 *
 * <p>
 * {@code PER_METHOD} 测试实例生命周期是 JUnit Jupiter 中的默认行为
 * </p>
 *
 * <p>
 * 如果希望 JUnit Jupiter 在同一个测试实例上执行所有测试方法, 需使用
 * {@code @TestInstance(Lifecycle.PER_CLASS)} 对测试类进行注解.
 * 当使用这种模式时, 只创建测试类的一个实例并在测试之间重用它. 因此,
 * 如果测试方法依赖于存储在实例变量中的状态, 则可能需要在 {@code @BeforeEach}
 * 或 {@code @AfterEach} 方法中重置该状态
 * </p>
 *
 * <p>
 * 如果测试类是一个内部类, 正常情况下是不会执行其中注解为 {@link Test @Test}
 * 的测试方法的, 需要在内部类上注解 {@link Nested @Nested}
 * </p>
 */
class LifecycleTest {
    /**
     * 测试 {@code PER_METHOD} 生命周期
     *
     * <p>
     * 此时每个测试方法都会独立创建一个测试对象来执行, 测试之间完全隔离,
     * 一个测试方法对当前测试类对象的影响不会传递给另一个测试方法
     * </p>
     */
    @Nested
    class PerMethodLifecycleTest {
        private boolean value = true;

        /**
         * 测试类字段 {@code value} 的值, 并改变其值, 确认对其它测试方法不会产生影响
         */
        @Test
        void lifecycle_shouldFieldValueChanged1() {
            then(value).isTrue();

            value = false;
        }

        /**
         * 测试类字段 {@code value} 的值, 并改变其值, 确认对其它测试方法不会产生影响
         */
        @Test
        void lifecycle_shouldFieldValueChanged2() {
            then(value).isTrue();

            value = false;
        }
    }

    /**
     * 测试 {@code PER_CLASS} 生命周期
     *
     * <p>
     * 此时每个每个测试类会创建一个测试对象, 类中的方法共享此对象. 此时任一测试方法对类字段的改变都会影响到当前类的其它测试方法
     * </p>
     */
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @TestMethodOrder(OrderAnnotation.class)
    class PerClassLifecycleTest {
        private boolean value = true;

        /**
         * 测试类字段 {@code value} 的值, 并改变其值, 会对之后的测试方法产生影响
         */
        @Test
        @Order(1)
        void lifecycle_shouldFieldValueChanged1() {
            then(value).isTrue();

            value = false;
        }

        /**
         * 测试类字段 {@code value} 的值, 由于 {@link #lifecycle_shouldFieldValueChanged1()} 方法已经改变该字段的值, 所以
         * 本方法中看到的是改变后的值
         */
        @Test
        @Order(2)
        void lifecycle_shouldFieldValueChanged2() {
            then(value).isFalse();
        }
    }
}
