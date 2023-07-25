package alvin.study.testing.assertj;


import alvin.study.testing.assertj.custom.SoftAssertions;
import alvin.study.testing.testcase.model.User;
import org.junit.jupiter.api.Test;

import static alvin.study.testing.assertj.custom.Assertions.assertThat;
import static alvin.study.testing.assertj.custom.Assertions.then;

/**
 * 测试自定义断言
 *
 * <p>
 * 自定义断言可以更简单和更精确的测试目标类型, 参考 {@link alvin.study.assertj.custom.UserAssert UserAssert} 以及
 * {@link SoftAssertions} 类型
 * </p>
 */
class CustomAssertionTest {
    // 实例化待测试对象
    private static User user = new User(1001, "Alvin");

    /**
     * 测试标准格式测试, 即通过 {@code assertThat} 开始的测试
     *
     * <p>
     * 参考: {@link alvin.study.assertj.custom.Assertions Assertions} 类型
     * </p>
     */
    @Test
    void standard_shouldCustomAssertWorked() {
        assertThat(user)
            .isNotNull()
            .hasId(1001)
            .hasName("Alvin");
    }

    /**
     * 测试 BDD 格式测试, 即通过 {@code then} 开始的测试
     *
     * <p>
     * 参考: {@link alvin.study.assertj.custom.Assertions Assertions} 类型
     * </p>
     */
    @Test
    void bdd_shouldCustomAssertWorked() {
        then(user)
            .isNotNull()
            .hasId(1001)
            .hasName("Alvin");
    }

    /**
     * 测试 Soft 格式测试
     *
     * <p>
     * 参考: {@link SoftAssertions} 类型
     * </p>
     */
    @Test
    void softly_shouldCustomAssertWorked() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(user)
                .isNotNull()
                .hasName("Alvin")
                .hasId(1001);
        });
    }
}
