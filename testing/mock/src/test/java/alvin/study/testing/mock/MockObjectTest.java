package alvin.study.testing.mock;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import alvin.study.testing.testcase.controller.UserController;
import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.UserService;

/**
 * 测试对仿冒对象进行行为设置
 *
 * <p>
 * 仿冒方法时需要指定方法的参数和返回值 (或取代方法的执行体)
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MockObjectTest {
    // 仿冒对象
    @Mock
    private UserService userService;

    // 将仿冒的对象按需注入目标对象中
    @InjectMocks
    private UserController userController;

    /**
     * 在每次测试结束后调用
     */
    @AfterEach
    void afterEach() {
        // 重置仿冒对象, 回复其默认的行为
        reset(userService);
    }

    /**
     * 测试为仿冒参数指定确定的参数值
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenReturn(Object)
     * OngoingStubbing.thenReturn(Object)} 表示仿冒方法的返回值
     * </p>
     *
     * <p>
     * 为仿冒方法指定确定的参数值后, 在调用该仿冒方法时, 如果传递的参数和预设一致,
     * 则调用成功, 返回预设的返回值
     * </p>
     *
     * <p>
     * 在仿冒方法调用时, 如果指定了确认的参数, 则在调用该函数时也期待传递相同参数,
     * 否则会引发 {@link PotentialStubbingProblem} 异常
     * </p>
     */
    @Test
    void thenReturn_shouldMockMethodWithCertainArgument() {
        // 仿冒指定方法, 指定确定的参数值
        when(userService.findByName("Emma"))
                .thenReturn(Optional.of(new User(2, "Emma")));

        // 调用仿冒方法, 传入期待参数, 返回预设结果
        then(userController.getUser("Emma"))
                .isEqualTo("{\"id\":2,\"name\":\"Emma\"}");
    }

    /**
     * 调用仿冒方法时传递非预设的参数值
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个
     * {@link org.mockito.stubbing.OngoingStubbing OngoingStubbing}
     * 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenReturn(Object)
     * OngoingStubbing.thenReturn(Object)} 表示仿冒方法的返回值
     * </p>
     *
     * <p>
     * 在仿冒方法调用时, 如果指定了确认的参数, 则在调用该函数时也期待传递相同参数,
     * 否则会引发 {@link PotentialStubbingProblem} 异常
     * </p>
     */
    @Test
    void thenReturn_shouldMockMethodWithWrongArgument() {
        // 仿冒指定方法, 指定确定的参数值
        when(userService.findByName("Emma"))
                .thenReturn(Optional.of(new User(2, "Emma")));

        // 调用仿冒方法, 传入非期待的参数, 确认抛出异常
        thenThrownBy(() -> userController.getUser("Alvin"))
                .isInstanceOf(PotentialStubbingProblem.class);
    }

    /**
     * 测试设定仿冒对象的参数和返回值
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenReturn(Object)
     * OngoingStubbing.thenReturn(Object)} 表示仿冒方法的返回值
     * </p>
     *
     * <p>
     * {@link org.mockito.ArgumentMatchers#anyString()
     * ArgumentMatchers.anyString()} 方法表示仿冒对象的参数表示"任意字符串"
     * </p>
     */
    @Test
    void thenReturn_shouldMockMethodWithAnyStringArgument() {
        // 仿冒指定方法, 指定任意字符串类型参数, 且返回期待对象
        when(userService.findByName(anyString()))
                .thenReturn(Optional.of(new User(1, "Alvin")));

        // 确认仿冒方法返回期待对象
        then(userController.getUser("any"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }

    /**
     * 测试设定仿冒对象的参数和返回值
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenReturn(Object)
     * OngoingStubbing.thenReturn(Object)} 表示仿冒方法的返回值
     * </p>
     *
     * <p>
     * {@link org.mockito.ArgumentMatchers#any() ArgumentMatchers.any()}
     * 方法表示仿冒对象的参数表示"任意对象"
     * </p>
     */
    @Test
    void thenReturn_shouldMockMethodWithAnyArgument() {
        // 仿冒指定方法, 指定任意类型参数, 且返回期待对象
        when(userService.findByName(any()))
                .thenReturn(Optional.of(new User(1, "Alvin")));

        // 确认仿冒方法返回期待对象
        then(userController.getUser("anystring"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }

    /**
     * 测试设定仿冒对象的参数和返回值
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenReturn(Object)
     * OngoingStubbing.thenReturn(Object)} 表示仿冒方法的返回值
     * </p>
     *
     * <p>
     * {@link org.mockito.ArgumentMatchers#any(Class)
     * ArgumentMatchers.any(Class)} 方法表示仿冒对象的参数表示 "任意指定类型对象"
     * </p>
     */
    @Test
    void thenReturn_shouldMockMethodWithAnyStringTypeArgument() {
        // 仿冒指定方法, 指定任意类型参数, 且返回期待对象
        when(userService.findByName(any(String.class)))
                .thenReturn(Optional.of(new User(1, "Alvin")));

        // 确认仿冒方法返回期待对象
        then(userController.getUser("anystring"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }

    /**
     * 为仿冒对象指定抛出的异常
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenThrow(Class)
     * OngoingStubbing.thenThrow(Class)} 指定在调用仿冒方法时抛出指定异常
     * </p>
     */
    @Test
    void thenThrow_shouldMockMethodAndThrowException() {
        // 仿冒指定方法, 指定任意类型参数, 且指定调用时的方法
        when(userService.findByName(anyString()))
                .thenThrow(RuntimeException.class);

        // 确认调用仿冒函数后捕获到期待的异常
        thenThrownBy(() -> userController.getUser("Alvin"))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * 为仿冒对象指定一个调用方法
     *
     * <p>
     * {@link org.mockito.Mockito#when(Object) Mockito.when(Object)}
     * 表示要仿冒的方法, 返回一个 {@link org.mockito.stubbing.OngoingStubbing
     * OngoingStubbing} 对象, 表示仿冒方法调用的存根
     * </p>
     *
     * <p>
     * {@link org.mockito.stubbing.OngoingStubbing#thenAnswer(
     * org.mockito.stubbing.Answer) OngoingStubbing.thenAnswer(Answer)}
     * 指定一个函数对象, 该对象将取代被仿冒方法来执行
     * </p>
     */
    @Test
    void thenAnswer_shouldMethodBeMockedWithDifferentReturnValue() {
        // 仿冒指定方法, 指定任意类型参数, 且指定调用时的方法
        when(userService.findByName(anyString())).thenAnswer(invocation -> {
            // 获取被仿冒的对象
            then(invocation.getMock()).isSameAs(userService);
            // 获取被取代的行为
            then(invocation.getMethod().getName()).isEqualTo("findByName");
            // 获取传入的参数
            then((String) invocation.getArgument(0)).isEqualTo("Alvin");

            // 取代被仿冒行为的返回值
            return Optional.of(new User(1, "Alvin"));
        });

        then(userController.getUser("Alvin"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }

    /**
     * 另一种格式设置方面对象的行为
     */
    @Test
    void doSomething_shouldMethodMockedByOtherKindOfForm() {
        // 先设定返回值, 再设定要仿冒的方法
        doReturn(Optional.of(new User(1, "Alvin")))
                .when(userService).findByName(anyString());
        then(userController.getUser("Emma"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");

        reset(userService);

        // 先设定异常类型, 再设定要仿冒的方法
        doThrow(RuntimeException.class)
                .when(userService).findByName(anyString());
        thenThrownBy(() -> userController.getUser("Alvin"))
                .isInstanceOf(RuntimeException.class);

        reset(userService);

        // 先指定仿冒方法的执行, 再指定要仿冒的方法
        doAnswer(invocation -> {
            // 获取被仿冒的对象
            then(invocation.getMock()).isSameAs(userService);
            // 获取被取代的行为
            then(invocation.getMethod().getName()).isEqualTo("findByName");
            // 获取传入的参数
            then((String) invocation.getArgument(0)).isEqualTo("Alvin");

            return Optional.of(new User(1, "Alvin"));
        }).when(userService).findByName(anyString());

        then(userController.getUser("Alvin"))
                .isEqualTo("{\"id\":1,\"name\":\"Alvin\"}");
    }
}
