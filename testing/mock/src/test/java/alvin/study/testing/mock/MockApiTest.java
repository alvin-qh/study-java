package alvin.study.testing.mock;

import alvin.study.testing.testcase.controller.UserController;
import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 使用 {@link org.mockito.Mockito Mockito} API 模仿对象
 */
class MockApiTest {

    // 注入控制器对象
    private UserController userController;

    /**
     * 在测试执行前执行
     *
     * <p>
     * 通过 {@link org.mockito.Mockito#mock(Class) Mockito.mock(Class)} 方法仿冒一个对象
     * </p>
     */
    @BeforeEach
    void beforeEach() {
        // 产生一个模仿对象
        // 注入服务类对象
        var userService = mock(UserService.class);

        // 设置仿冒对象的行为
        when(userService.findByName("Alvin")).thenReturn(Optional.of(new User(1, "Alvin")));

        // 将模仿后的对象作为参数传递到其它对象
        userController = new UserController(userService);
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
