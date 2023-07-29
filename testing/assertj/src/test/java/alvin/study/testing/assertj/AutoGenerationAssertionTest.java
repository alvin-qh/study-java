package alvin.study.testing.assertj;

import alvin.study.testing.assertj.assertion.SoftAssertions;
import alvin.study.testing.testcase.model.User;
import org.junit.jupiter.api.Test;

import static alvin.study.testing.assertj.assertion.Assertions.assertThat;
import static alvin.study.testing.assertj.assertion.BddAssertions.then;

/**
 * 测试自动生成的断言类
 *
 * <p>
 * 可以通过 Maven 插件或者 Gradle 插件自动生成断言类, 参考 {@link alvin.study.model.UserAssert UserAssert} 类以及
 * {@link alvin.study.assertion.Assertions Assertions}, {@link alvin.study.assertion.BddAssertions BddAssertions} 和
 * {@link alvin.study.assertion.SoftAssertions SoftAssertions} 类型
 * </p>
 */
class AutoGenerationAssertionTest {
    private static final User user = new User(1001, "Alvin");

    /**
     * 测试标准格式测试, 即通过 {@code assertThat} 开始的测试
     *
     * <p>
     * 参考: {@link alvin.study.assertion.Assertions Assertions} 类型
     * </p>
     */
    @Test
    void standard_shouldStandardAssertionGenerated() {
        assertThat(user).isNotNull();
        assertThat(user).hasId(1001);
        assertThat(user).hasName("Alvin");
    }

    /**
     * 测试 BDD 格式测试, 即通过 {@code then} 开始的测试
     *
     * <p>
     * 参考: {@link alvin.study.assertion.BddAssertions BddAssertions} 类型
     * </p>
     */
    @Test
    void bdd_shouldBDDAssertionGenerated() {
        then(user).isNotNull();
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
    void standard_shouldSoftlyAssertionGenerated() {
        var softly = new SoftAssertions();

        softly.assertThat(user).isNotNull();
        softly.assertThat(user).hasId(1001);
        softly.assertThat(user).hasName("Alvin");

        softly.assertAll();
    }
}
