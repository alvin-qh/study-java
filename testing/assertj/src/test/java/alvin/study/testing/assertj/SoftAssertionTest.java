package alvin.study.testing.assertj;

import org.assertj.core.api.AutoCloseableBDDSoftAssertions;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * 测试软断言的使用
 *
 * <p>
 * 所谓"软断言", 即在测试执行过程中, 即使断言失败也不会中断测试.
 * 在一个测试方法执行完毕后, 报告完整的测试结果, 包括所有未通过的断言情况
 * </p>
 */
@Disabled
class SoftAssertionTest {
    /**
     * 软断言的基本使用
     */
    @Nested
    class BasicUsageTest {
        /**
         * 测试通过 {@link SoftAssertions#assertAll()} 方法完成断言报告
         */
        @Test
        void standard_shouldSoftAssertionWorked() {
            var x = 99;
            var y = 100;

            // 生成软断言对象
            var softly = new SoftAssertions();
            try {
                // 通过软断言对象进行断言
                softly.assertThat(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.assertThat(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.assertThat(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            } finally {
                // 测试方法结束前产生测试报告
                softly.assertAll();
            }
        }

        /**
         * 测试通过 {@link AutoCloseableSoftAssertions#close()} 方法完成断言报告
         */
        @Test
        void autoClose_shouldSoftAssertionWorked() {
            var x = 99;
            var y = 100;

            // 通过 try ... resource 语法产生一个结束后自动产生测试报告的软断言对象
            try (var softly = new AutoCloseableSoftAssertions()) {
                // 通过软断言对象进行断言
                softly.assertThat(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.assertThat(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.assertThat(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            }
        }

        /**
         * 测试通过 {@link SoftAssertions#assertSoftly(java.util.function.Consumer)
         * SoftAssertions.assertSoftly(Consumer)} 方法完成断言报告
         */
        @Test
        void lambda_shouldSoftAssertionWorked() {
            var x = 99;
            var y = 100;

            // 通过一个 Lambda 表达式进行软断言, 表达式执行完毕后自动生成测试报告
            SoftAssertions.assertSoftly(softly -> {
                // 通过软断言对象进行断言
                softly.assertThat(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.assertThat(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.assertThat(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            });
        }

        /**
         * 通过 BDD 格式进行断言, 并通过 {@link BDDSoftAssertions#assertAll()}
         * 方法完成断言报告
         */
        @Test
        void bdd_shouldSoftAssertionWorked() {
            var x = 99;
            var y = 100;

            // 生成软断言对象
            var softly = new BDDSoftAssertions();
            try {
                // 通过软断言对象进行断言
                softly.then(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.then(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.then(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            } finally {
                // 测试方法结束前产生测试报告
                softly.assertAll();
            }
        }

        /**
         * 通过 BDD 格式进行断言, 并通过 {@link AutoCloseableBDDSoftAssertions#close()}
         * 方法完成断言报告
         */
        @Test
        void bdd_shouldAutoClosedSoftAssertionWorked() {
            var x = 99;
            var y = 100;

            // 通过 try ... resource 语法产生一个结束后自动产生测试报告的软断言对象
            try (var softly = new AutoCloseableBDDSoftAssertions()) {
                // 通过软断言对象进行断言
                softly.then(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.then(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.then(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            }
        }

        /**
         * 通过 BDD 格式进行断言, 并通过
         * {@link BDDSoftAssertions#thenSoftly(java.util.function.Consumer)
         * BDDSoftAssertions.thenSoftly(Consumer)} 方法完成断言报告
         */
        @Test
        void bdd_shouldSoftAssertionWorkedByLambda() {
            var x = 99;
            var y = 100;

            // 通过 try ... resource 语法产生一个结束后自动产生测试报告的软断言对象
            BDDSoftAssertions.thenSoftly(softly -> {
                // 通过软断言对象进行断言
                softly.then(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.then(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.then(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            });
        }
    }

    /**
     * 通过注入方式使用软断言对象
     */
    @Nested
    @ExtendWith(SoftAssertionsExtension.class)
    class InjectUsageTest {
        // 注入软断言对象
        @InjectSoftAssertions
        private SoftAssertions softly;

        // 注入 BDD 格式的软断言对象
        @InjectSoftAssertions
        private BDDSoftAssertions bddSoftly;

        /**
         * 通过 {@link SoftAssertions#assertAll()} 方法完成断言报告
         */
        @Test
        void soft_shouldSoftAssertionWork() {
            var x = 99;
            var y = 100;

            try {
                // 通过软断言对象进行断言
                softly.assertThat(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                softly.assertThat(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                softly.assertThat(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            } finally {
                // 测试方法结束前产生测试报告
                softly.assertAll();
            }
        }

        /**
         * 通过 BDD 格式进行断言, 并通过 {@link BDDSoftAssertions#assertAll()}
         * 方法完成断言报告
         */
        @Test
        void softBdd_shouldSoftAssertionWork() {
            var x = 99;
            var y = 100;

            try {
                // 通过软断言对象进行断言
                bddSoftly.then(x)
                        .as("should x is equal to y")
                        .isEqualTo(y);
                bddSoftly.then(x)
                        .as("should x is great than y")
                        .isGreaterThan(y);
                bddSoftly.then(y)
                        .as("should x is less and equal to than y")
                        .isLessThanOrEqualTo(x);
            } finally {
                // 测试方法结束前产生测试报告
                bddSoftly.assertAll();
            }
        }
    }
}
