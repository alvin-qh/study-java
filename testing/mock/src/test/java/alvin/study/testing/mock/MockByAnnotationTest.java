package alvin.study.testing.mock;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import alvin.study.testing.testcase.controller.UserController;
import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.UserService;

/**
 * 使用 {@link Mock @Mock} 注解模仿对象
 *
 * <p>
 * 需要使用 {@link MockitoExtension} 类对测试类型进行扩展
 * </p>
 *
 * <p>
 * {@link Mock @Mock} 注解相当于执行了 {@link org.mockito.Mockito#mock(Class)
 * Mockito.mock(Class)} 方法, 得到一个仿冒对象
 * </p>
 *
 * <p>
 * {@link InjectMocks @InjectMocks} 注解的作用是将注解了 {@link Mock @Mock}
 * 的对象注入到指定对象中
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MockByAnnotationTest {
    // 对 UserService 类型进行仿冒, 得到一个对象
    @Mock
    private UserService userService;

    // 向目标对象中注入被模仿的对象
    @InjectMocks
    private UserController userController;

    /**
     * 在每次测试执行前执行
     */
    @BeforeEach
    void beforeEach() {
        // 设置仿冒对象的行为
        when(userService.findByName("Alvin"))
                .thenReturn(Optional.of(new User(1, "Alvin")));
    }

    /**
     * 测试 {@link UserController#getUser(String)} 方法
     *
     * <p>
     * 该方法内部使用了仿冒的 {@link UserService#findByName(String)} 方法
     * </p>
     */
    @Test
    void getUser_shouldUserServiceMocked() {
        var json = userController.getUser("Alvin");
        then(json).isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }
}
