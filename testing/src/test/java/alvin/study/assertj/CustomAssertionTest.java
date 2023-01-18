package alvin.study.assertj;

import static alvin.study.assertj.custom.Assertions.assertThat;
import static alvin.study.assertj.custom.Assertions.then;

import org.junit.jupiter.api.Test;

import alvin.study.assertj.custom.SoftAssertions;
import alvin.study.model.User;

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
        assertThat(user).isNotNull();
        assertThat(user).hasId(1001);
        assertThat(user).hasName("Alvin");
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
        assertThat(user).isNotNull();
        then(user).hasId(1001);
        then(user).hasName("Alvin");
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
            softly.assertThat(user).isNotNull();
            softly.assertThat(user).hasId(1001);
            softly.assertThat(user).hasName("Alvin");
        });
    }
}
