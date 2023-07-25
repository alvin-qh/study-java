package alvin.study.testing.assertj.custom;

import alvin.study.testing.testcase.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 定义断言入口类
 *
 * <p>
 * 如果工程中包含多个自定义断言, 则需要定义一个入口类, 通过重载方式避免 {@code assertThat} 或 {@code then} 方法重复定义
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Assertions {
    /**
     * 通过 {@link User} 类型重载 {@code assertThat} 方法
     *
     * @param user 要进行断言的对象
     * @return {@link UserAssert} 断言对象
     */
    @Contract("_ -> new")
    public static @NotNull UserAssert assertThat(User user) {
        return new UserAssert(user);
    }

    /**
     * 通过 {@link User} 类型重载 {@code then} 方法
     *
     * @param user 要进行断言的对象
     * @return {@link UserAssert} 断言对象
     */
    @Contract("_ -> new")
    public static @NotNull UserAssert then(User user) {
        return assertThat(user);
    }
}
