package alvin.study.testing.awaitility;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.given;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.awaitility.Awaitility;

import com.google.common.base.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.AsyncService;

/**
 * 测试 Awaitility 库, 对并发处理进行断言
 *
 * <p>
 * Awaitility 框架通过轮询的方式对指定的方法进行反复执行 (或对指定的字段进行反复读取),
 * 如果在规定的超时时间内, 被调用方法返回了预期的结果 (或指定的字段设置了预期的值),
 * 则成功, 否则引发断言
 * </p>
 */
class AsyncServiceTest {
    /**
     * 在所有测试执行前执行, 对 Awaitility 库进行初始设置
     */
    @BeforeAll
    static void setupAwaitility() {
        // 设置轮询状态的间隔时间
        Awaitility.setDefaultPollInterval(10, TimeUnit.MICROSECONDS);

        // 设置轮询一次的延迟时间
        Awaitility.setDefaultPollDelay(Duration.ZERO);

        // 设置等待处理结果的超时时间
        Awaitility.setDefaultTimeout(1, TimeUnit.MINUTES);
    }

    /**
     * 测试 Awaitility 库的一般用法
     *
     * <p>
     * {@link Awaitility#await()} 方法返回一个
     * {@link org.awaitility.core.ConditionFactory ConditionFactory}
     * 类型对象, 用于构建异步测试过程
     * </p>
     *
     * <p>
     * {@link AsyncService#initialize()} 异步执行, 所以在执行完该方法后,
     * 通过轮询 {@link AsyncService#isInitialized()} 方法的结果判断操作是否结束
     * </p>
     *
     * <p>
     * {@link org.awaitility.core.ConditionFactory#atLeast(long, TimeUnit)
     * ConditionFactory.atLeast(long, TimeUnit)} 方法用于设置必要执行时间,
     * 整个过程不能在这个时间之前完成;
     * {@link org.awaitility.core.ConditionFactory#atMost(long, TimeUnit)
     * ConditionFactory.atMost(long, TimeUnit)} 方法用于设置最大执行时间,
     * 整个过程不能超过该时间
     * </p>
     *
     * <p>
     * {@link org.awaitility.core.ConditionFactory#until(
     * java.util.concurrent.Callable) ConditionFactory.until(Callable)}
     * 方法用于设置要执行的过程
     * </p>
     */
    @Test
    void initialize_shouldWaitUnitInitializedAsync() {
        var service = new AsyncService();

        // 执行异步方法
        service.initialize();

        // 在 90 ~ 120ms 内不断轮询 isInitialized 方法的结果,
        // 确保在指定时间内完成操作
        await().atLeast(90, TimeUnit.MILLISECONDS)
                .atMost(120, TimeUnit.MILLISECONDS)
                .until(service::isInitialized);
    }

    /**
     * 演示如何在执行过程中修改 Awaitility 的初始设置
     *
     * <p>
     * {@link Awaitility#with()} 用于在执行过程中对 Awaitility
     * 初始设置进行修改, 包括:
     * <ul>
     * <li>
     * {@link org.awaitility.core.ConditionFactory#pollDelay(
     * long, TimeUnit) ConditionFactory.pollDelay(long, TimeUnit)}
     * 方法, 用于设置轮询延迟时间
     * </li>
     * <li>
     * {@link org.awaitility.core.ConditionFactory#pollInterval(
     * long, TimeUnit) ConditionFactory.pollInterval(long, TimeUnit)}
     * 方法, 用于设置轮询的间隔时间
     * </li>
     * <li>
     * {@link org.awaitility.core.ConditionFactory#timeout(
     * long, TimeUnit) ConditionFactory.timeout(long, TimeUnit)}
     * 方法, 用于设置等待超时时间
     * </li>
     * <li>
     * {@link org.awaitility.core.ConditionFactory#and()
     * ConditionFactory.and()} 方法以及
     * {@link org.awaitility.core.ConditionFactory#with()
     * ConditionFactory.with()} 方法返回
     * {@link org.awaitility.core.ConditionFactory ConditionFactory}
     * 对象本身, 其作用是为了增加代码的可读性
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void initialize_shouldChangeSettingsWhileRuntime() {
        var service = new AsyncService();

        service.initialize();

        with().pollDelay(100, TimeUnit.MILLISECONDS) // 设置轮询延迟时间
                .and().with().pollInterval(
                    50, TimeUnit.MILLISECONDS) // 设置轮询间隔时间
                .and().with().timeout(
                    100, TimeUnit.MILLISECONDS) // 设置等待超时时间
                .await()
                .atLeast(90, TimeUnit.MILLISECONDS)
                .atMost(120, TimeUnit.MILLISECONDS)
                .until(service::isInitialized);
    }

    /**
     * 演示等待一个 Atomic 值变成预期值
     *
     * <p>
     * {@link org.awaitility.core.ConditionFactory#until(
     * java.util.concurrent.Callable, org.hamcrest.Matcher)
     * ConditionFactory.until(Callable, Matcher)} 等待一个返回值,
     * 并通过一个 Hamcrest Matcher 对象和期待的值进行匹配
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setValue(long)} 和
     * {@link AsyncService#getValue()} 方法
     * </p>
     */
    @Test
    void getValue_shouldSetAndGetValueAsync() {
        var service = new AsyncService();

        service.setValue(1234L);

        // 按最长 100ms 进行等待
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(service::getValue, is(equalTo(1234L)));
    }

    /**
     * 演示等待一个 Atomic 值变成预期值
     *
     * <p>
     * {@link org.awaitility.core.ConditionFactory#untilAtomic(
     * AtomicInteger, org.hamcrest.Matcher)
     * ConditionFactory.untilAtomic(AtomicInteger, Matcher)}
     * 方法用于等待一个 Atomic 对象的值变为期待值, 通过一个 Hamcrest Matcher
     * 对象进行匹配
     * </p>
     */
    @Test
    void atomic_shouldWaitAtomBecomeToExpectedValue() {
        var atom = new AtomicInteger(10);

        // 异步在 100ms 后将 Atomic 对象的值改为期待值
        new Thread(() -> {
            try {
                Thread.sleep(100);
                atom.set(20);
            } catch (InterruptedException ignore) {}
        }).start();

        // 在 2 秒内等待 Atomic 对象的值变为期待值
        await().atMost(120, TimeUnit.MILLISECONDS)
                .untilAtomic(atom, is(equalTo(20)));
    }

    /**
     * 演示获取一个对象字段的值并和期待的值进行比较
     *
     * <p>
     * {@link Awaitility#fieldIn(Object)} 方法方法用于从指定对象获取字段,
     * 返回一个 {@link org.awaitility.core.FieldSupplierBuilder
     * FieldSupplierBuilder} 对象, 其中:
     * <ul>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder#ofType(Class)
     * FieldSupplierBuilder.ofType(Class)} 方法用于指定字段的类型, 返回
     * {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier
     * NameAndAnnotationFieldSupplier} 类型对象
     * </li>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier#andWithName(String)
     * NameAndAnnotationFieldSupplier.andWithName(String)}
     * 方法用于指定字段的名称
     * </li>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier#andAnnotatedWith(Class)
     * NameAndAnnotationFieldSupplier.andAnnotatedWith(Class)}
     * 方法用于指定字段上标记的注解
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 获取的字段值通过
     * {@link org.awaitility.core.ConditionFactory#until(
     * java.util.concurrent.Callable, java.util.function.Predicate)
     * ConditionFactory.until(Callable, Predicate)} 方法, 通过一个
     * {@link java.util.function.Predicate Predicate} 对象进行匹配
     * </p>
     *
     * <p>
     * 参考 {@code AsyncService.value} 字段
     * </p>
     */
    @Test
    void getValue_shouldMatchFieldValue() {
        var service = new AsyncService();

        service.setValue(1234L);

        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(fieldIn(service)
                        .ofType(AtomicLong.class)
                        .andWithName("value"),
                    atom -> atom.get() == 1234L);
    }

    /**
     * 演示和 AssertJ 断言框架配合使用
     *
     * <p>
     * 除了和 Hamcrest 库配合进行断言外, 还可以通过 AssertJ
     * 库进行断言
     * </p>
     *
     * <p>
     * 获取的字段值通过 {@link org.awaitility.core.ConditionFactory#untilAsserted(
     * org.awaitility.core.ThrowingRunnable)
     * ConditionFactory.untilAsserted(ThrowingRunnable)} 方法, 其参数
     * {@link org.awaitility.core.ThrowingRunnable
     * ThrowingRunnable} 接口可以在 Lambda 中写任意断言方法
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setValue(long)} 和
     * {@link AsyncService#getValue()} 方法
     * </p>
     */
    @Test
    void assertj_shouldMatchExpectedResult() {
        var service = new AsyncService();

        service.setValue(1234L);

        await().atMost(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> then(service)
                        .extracting("value")
                        .isEqualTo(1234L));
    }

    /**
     * 忽略指定类型的异常
     *
     * <p>
     * 在轮询异步方法时, 有可能会引发一些表示异步方法尚未结束的异常, 例如
     * {@link AsyncService#getUser()} 方法中, 当 {@link User}
     * 字段尚未设置时, 会引发异常
     * </p>
     *
     * <p>
     * {@link org.awaitility.core.ConditionFactory#ignoreException(Class)
     * ConditionFactory.ignoreException(Class)} 方法可以忽略掉指定的异常,
     * 防止测试中断
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setUser(User)} 和
     * {@link AsyncService#getUser()} 方法
     * </p>
     */
    @Test
    void exception_shouldIgnoreExceptionThrownByType() {
        var service = new AsyncService();

        service.setUser(new User(1001, "Alvin"));

        given().ignoreException(IllegalStateException.class)
                .await()
                .atMost(100, TimeUnit.MILLISECONDS)
                .until(
                    service::getUser,
                    user -> Objects.equal(user.getId(), 1001) &&
                            Objects.equal(user.getName(), "Alvin"));
    }

    /**
     * 忽略符合指定条件的异常
     *
     * <p>
     * 和 {@link #exception_shouldIgnoreExceptionThrownByType()}
     * 测试类似, 本例通过一个 Lambda 表达式对异常进行匹配
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setUser(User)} 和
     * {@link AsyncService#getUser()} 方法
     * </p>
     */
    @Test
    void exception_shouldIgnoreExceptionThrownByMatcher() {
        var service = new AsyncService();

        service.setUser(new User(1001, "Alvin"));

        given().ignoreExceptionsMatching(
            e -> e.getMessage().startsWith("Object not ready"))
                .await()
                .atMost(100, TimeUnit.MILLISECONDS)
                .until(
                    service::getUser,
                    user -> Objects.equal(user.getId(), 1001) &&
                            Objects.equal(user.getName(), "Alvin"));
    }
}
