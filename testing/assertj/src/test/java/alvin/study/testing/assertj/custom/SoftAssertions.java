package alvin.study.testing.assertj.custom;

import alvin.study.testing.testcase.model.User;
import org.assertj.core.api.AbstractSoftAssertions;
import org.assertj.core.api.SoftAssertionsProvider;
import org.assertj.core.api.StandardSoftAssertionsProvider;

import java.util.function.Consumer;

/**
 * 定义 Softly 断言入口类
 *
 * <p>
 * 从 {@link AbstractSoftAssertions} 类型继承, 通过 {@link AbstractSoftAssertions#proxy(Class, Class, Object)} 方法
 * 将断言类包装为 Softly 断言对象
 * </p>
 */
public class SoftAssertions extends AbstractSoftAssertions implements StandardSoftAssertionsProvider {
    /**
     * 通过 lambda 使用 Softly 断言
     *
     * <p>
     * 通过如下格式调用
     *
     * <pre>
     * SoftAssertions.assertSoftly(softly -> {
     *      softly.assertThat(...).isNotNull();
     * });
     * </pre>
     * </p>
     *
     * @param softly 一个 {@link Consumer} 回调函数对象
     */
    public static void assertSoftly(Consumer<SoftAssertions> softly) {
        SoftAssertionsProvider.assertSoftly(SoftAssertions.class, softly);
    }

    /**
     * 产生一个 Softly 断言对象
     *
     * @param actual 要断言的目标对象
     */
    public UserAssert assertThat(User actual) {
        return proxy(UserAssert.class, User.class, actual);
    }
}
