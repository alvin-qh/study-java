package alvin.study.testing.assertj.custom;

import alvin.study.testing.testcase.model.User;
import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

/**
 * 对 {@link User} 类型进行断言的断言类
 */
public class UserAssert extends AbstractAssert<UserAssert, User> {
    /**
     * 构造器, 实例化断言对象, 本构造器不公开, 必须通过 {@link Assertions#assertThat(User)} 方法创建
     *
     * @param actual 要断言的目标对象
     */
    protected UserAssert(User actual) {
        super(actual, UserAssert.class);
    }

    /**
     * 断言 {@link User} 对象的 {@code id} 属性是否符合预期
     *
     * @param expected 期待的 {@code id} 属性值
     * @return 当前对象
     */
    public UserAssert hasId(int expected) {
        if (actual.getId() != expected) {
            // 断言失败, 中断执行并报告错误
            failWithMessage("Id not matched");
        }
        return this;
    }

    /**
     * 断言 {@link User} 对象的 {@code name} 属性是否符合预期
     *
     * @param expected 期待的 {@code name} 属性值
     * @return 当前对象
     */
    public UserAssert hasName(String expected) {
        if (!Objects.equals(actual.getName(), expected)) {
            // 断言失败, 中断执行并报告错误
            failWithMessage("Name not matched");
        }
        return this;
    }
}
