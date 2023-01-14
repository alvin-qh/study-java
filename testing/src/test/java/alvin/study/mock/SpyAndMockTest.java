package alvin.study.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import alvin.study.model.User;

/**
 * 演示 {@link Spy @Spy} 和 {@link Mock @Mock} 注解的异同
 *
 * <p>
 * {@link Spy @Spy} 注解相当于执行了 {@link org.mockito.Mockito#spy(Object) Mockito.spy(Object)} 方法, 得到一个仿冒对象
 * </p>
 *
 * <p>
 * 和 {@link Mock @Mock} 得到的仿冒对象不同, {@link Spy @Spy} 注解的仿冒对象在调用其方法会执行原对象的方法, 除非重新设置了方法行为;
 * 而 {@link Mock @Mock} 得到的仿冒对象在调用其方法时, 会返回 {@code null}, {@code 0} 等值, 除非重新设置了方法的返回值
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SpyAndMockTest {
    // 仿冒对象
    @Spy
    private User spiedUser = new User(1, "Alvin");

    // 仿冒对象
    @Mock
    private User mockedUser = new User(1, "Alvin");

    /**
     * 测试通过 {@link Spy @Spy} 注解仿冒的对象
     *
     * <p>
     * 默认情况下, 通过 {@link Spy @Spy} 注解仿冒的对象, 在调用其方法时, 执行原对象方法, 返回原对象方法返回值
     * </p>
     *
     * <p>
     * 改变方法行为后, 再调用仿冒对象的方法, 则按设定的方法行为执行
     * </p>
     */
    @Test
    void spy_shouldMockObject() {
        // 默认情况下, spy 后的对象方法仍返回对象原本的值
        assertThat(spiedUser.getId(), is(1));
        assertThat(spiedUser.getName(), is("Alvin"));

        // 重新设定方法的返回值
        when(spiedUser.getId()).thenReturn(2);
        when(spiedUser.getName()).thenReturn("Emma");

        // 改变对象行为后, 按照定义返回指定值
        assertThat(spiedUser.getId(), is(equalTo(2)));
        assertThat(spiedUser.getName(), is(equalTo("Emma")));
    }

    /**
     * 测试通过 {@link Mock @Mock} 注解仿冒的对象
     *
     * <p>
     * 默认情况下, 通过 {@link Mock @Mock} 注解仿冒的对象, 在调用其方法时, 返回 {@code null} 或 {@code 0} 值
     * </p>
     *
     * <p>
     * 改变方法行为后, 再调用仿冒对象的方法, 则按设定的方法行为执行
     * </p>
     */
    @Test
    void mock_shouldMockObject() {
        // 默认情况下, mock 后的对象方法返回 0, false 或 null
        assertThat(mockedUser.getId(), is(0));
        assertThat(mockedUser.getName(), is(nullValue()));

        // 重新设定方法的返回值
        when(mockedUser.getId()).thenReturn(2);
        when(mockedUser.getName()).thenReturn("Emma");

        // 改变对象行为后, 按照定义返回指定值
        assertThat(mockedUser.getId(), is(2));
        assertThat(mockedUser.getName(), is("Emma"));
    }
}
