package alvin.study.testing.awaitility;

import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.AsyncService;
import com.google.common.base.Objects;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.given;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * 测试 Awaitility 库, 对并发处理进行断言
 *
 * <p>
 * Awaitility 框架通过轮询的方式对指定的方法进行反复执行 (或对指定的字段进行反复读取), 如果在规定的超时时间内, 被调用方法返回了预期的结果
 * (或指定的字段设置了预期的值), 则成功, 否则引发断言
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
     * {@link Awaitility#await()} 方法返回一个 {@link ConditionFactory ConditionFactory} 类型对象, 用于
     * 构建异步测试过程
     * </p>
     *
     * <p>
     * {@link AsyncService#initialize()} 异步执行, 所以在执行完该方法后, 通过轮询 {@link AsyncService#isInitialized()} 方法
     * 的结果判断操作是否结束
     * </p>
     *
     * <p>
     * {@link ConditionFactory#atLeast(long, TimeUnit) ConditionFactory.atLeast(long, TimeUnit)}
     * 方法用于设置必要执行时间, 整个过程不能在这个时间之前完成;
     * {@link ConditionFactory#atMost(long, TimeUnit) ConditionFactory.atMost(long, TimeUnit)}
     * 方法用于设置最大执行时间, 整个过程不能超过该时间
     * </p>
     *
     * <p>
     * {@link ConditionFactory#until(java.util.concurrent.Callable)
     * ConditionFactory.until(Callable)} 方法用于设置要执行的过程
     * </p>
     */
    @Test
    void initialize_shouldWaitUnitInitializedAsync() {
        var service = new AsyncService();

        // 执行异步方法
        service.initialize();
        // 在 1.5 ~ 2.5 秒内不断轮询 isInitialized 方法的结果, 确保在指定时间内完成操作
        await()
                .atLeast(1500, TimeUnit.MILLISECONDS)
                .atMost(2500, TimeUnit.MILLISECONDS)
                .until(service::isInitialized);
    }

    /**
     * 演示如何在执行过程中修改 Awaitility 的初始设置
     *
     * <p>
     * {@link Awaitility#with()} 用于在执行过程中对 Awaitility 初始设置进行修改, 包括:
     * <ul>
     * <li>
     * {@link ConditionFactory#pollDelay(long, TimeUnit)
     * ConditionFactory.pollDelay(long, TimeUnit)} 方法, 用于设置轮询延迟时间
     * </li>
     * <li>
     * {@link ConditionFactory#pollInterval(long, TimeUnit)
     * ConditionFactory.pollInterval(long, TimeUnit)} 方法, 用于设置轮询的间隔时间
     * </li>
     * <li>
     * {@link ConditionFactory#timeout(long, TimeUnit)
     * ConditionFactory.timeout(long, TimeUnit)} 方法, 用于设置等待超时时间
     * </li>
     * <li>
     * {@link ConditionFactory#and() ConditionFactory.and()} 方法以及
     * {@link ConditionFactory#with() ConditionFactory.with()} 方法返回
     * {@link ConditionFactory ConditionFactory} 对象本身, 其作用是为了增加代码的可读性
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void initialize_shouldChangeSettingsWhileRuntime() {
        var service = new AsyncService();

        service.initialize();
        with()
                .pollDelay(100, TimeUnit.MILLISECONDS) // 设置轮询延迟时间
                .and()
                .with().pollInterval(1000, TimeUnit.MILLISECONDS) // 设置轮询间隔时间
                .and()
                .with().timeout(1, TimeUnit.SECONDS) // 设置等待超时时间
                .await()
                .atLeast(1, TimeUnit.MILLISECONDS)
                .atMost(3, TimeUnit.SECONDS)
                .until(service::isInitialized);
    }

    /**
     * 演示等待一个 Atomic 值变成预期值
     *
     * <p>
     * {@link ConditionFactory#until(java.util.concurrent.Callable, org.hamcrest.Matcher)
     * ConditionFactory.until(Callable, Matcher)} 等待一个返回值, 并通过一个 Hamcrest Matcher 对象和期待的值进行匹配
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setValue(long)} 和 {@link AsyncService#getValue()} 方法
     * </p>
     */
    @Test
    void getValue_shouldSetAndGetValueAsync() {
        var service = new AsyncService();

        service.setValue(1234L);
        await()
                .atMost(2, TimeUnit.SECONDS)
                .until(service::getValue, is(equalTo(1234L)));
    }

    /**
     * 演示等待一个 Atomic 值变成预期值
     *
     * <p>
     * {@link ConditionFactory#untilAtomic(AtomicInteger, org.hamcrest.Matcher)
     * ConditionFactory.untilAtomic(AtomicInteger, Matcher)} 方法用于等待一个 Atomic 对象的值变为期待值, 通过一个
     * Hamcrest Matcher 对象进行匹配
     * </p>
     */
    @Test
    void atomic_shouldWaitAtomBecomeToExpectedValue() {
        var atom = new AtomicInteger(10);

        // 异步在 2 秒后将 Atomic 对象的值改为期待值
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                atom.set(20);
            } catch (InterruptedException ignore) {}
        }).start();

        // 在 2 秒内等待 Atomic 对象的值变为期待值
        await()
                .atMost(2, TimeUnit.SECONDS)
                .untilAtomic(atom, is(equalTo(20)));
    }

    /**
     * 演示获取一个对象字段的值并和期待的值进行比较
     *
     * <p>
     * {@link Awaitility#fieldIn(Object)} 方法方法用于从指定对象获取字段, 返回一个
     * {@link org.awaitility.core.FieldSupplierBuilder FieldSupplierBuilder} 对象, 其中:
     * <ul>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder#ofType(Class) FieldSupplierBuilder.ofType(Class)} 方法用于指定
     * 字段的类型, 返回 {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier
     * NameAndAnnotationFieldSupplier} 类型对象
     * </li>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier#andWithName(String)
     * NameAndAnnotationFieldSupplier.andWithName(String)} 方法用于指定字段的名称
     * </li>
     * <li>
     * {@link org.awaitility.core.FieldSupplierBuilder.NameAndAnnotationFieldSupplier#andAnnotatedWith(Class)
     * NameAndAnnotationFieldSupplier.andAnnotatedWith(Class)} 方法用于指定字段上标记的注解
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 获取的字段值通过
     * {@link ConditionFactory#until(java.util.concurrent.Callable, java.util.function.Predicate)
     * ConditionFactory.until(Callable, Predicate)} 方法, 通过一个 {@link java.util.function.Predicate Predicate} 对象
     * 进行匹配
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
        await()
                .atMost(3, TimeUnit.SECONDS)
                .until(
                    fieldIn(service)
                            .ofType(AtomicLong.class)
                            .andWithName("value"),
                    atom -> atom.get() == 1234L);
    }

    /**
     * 演示和 AssertJ 断言框架配合使用
     *
     * <p>
     * 除了和 Hamcrest 库配合进行断言外, 还可以通过 AssertJ 库进行断言
     * </p>
     *
     * <p>
     * 获取的字段值通过 {@link ConditionFactory#untilAsserted(org.awaitility.core.ThrowingRunnable)
     * ConditionFactory.untilAsserted(ThrowingRunnable)} 方法, 其参数 {@link org.awaitility.core.ThrowingRunnable
     * ThrowingRunnable} 接口可以在 Lambda 中写任意断言方法
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setValue(long)} 和 {@link AsyncService#getValue()} 方法
     * </p>
     */
    @Test
    void assertj_shouldMatchExpectedResult() {
        var service = new AsyncService();

        service.setValue(1234L);
        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> then(service)
                        .extracting("value")
                        .isEqualTo(1234L));
    }

    /**
     * 忽略指定类型的异常
     *
     * <p>
     * 在轮询异步方法时, 有可能会引发一些表示异步方法尚未结束的异常, 例如 {@link AsyncService#getUser()} 方法中, 当 {@link User} 字段
     * 尚未设置时, 会引发异常
     * </p>
     *
     * <p>
     * {@link ConditionFactory#ignoreException(Class) ConditionFactory.ignoreException(Class)} 方法
     * 可以忽略掉指定的异常, 防止测试中断
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setUser(User)} 和 {@link AsyncService#getUser()} 方法
     * </p>
     */
    @Test
    void exception_shouldIgnoreExceptionThrownByType() {
        var service = new AsyncService();

        service.setUser(new User(1001, "Alvin"));
        given()
                .ignoreException(IllegalStateException.class)
                .await()
                .atMost(2, TimeUnit.SECONDS)
                .until(
                    service::getUser,
                    user -> Objects.equal(user.getId(), 1001) && Objects.equal(user.getName(), "Alvin"));
    }

    /**
     * 忽略符合指定条件的异常
     *
     * <p>
     * 和 {@link #exception_shouldIgnoreExceptionThrownByType()} 测试类似, 本例通过一个 Lambda 表达式对异常进行匹配
     * </p>
     *
     * <p>
     * 参考 {@link AsyncService#setUser(User)} 和 {@link AsyncService#getUser()} 方法
     * </p>
     */
    @Test
    void exception_shouldIgnoreExceptionThrownByMatcher() {
        var service = new AsyncService();

        service.setUser(new User(1001, "Alvin"));

        given()
                .ignoreExceptionsMatching(e -> e.getMessage().startsWith("Object not ready"))
                .await()
                .atMost(2, TimeUnit.SECONDS)
                .until(
                    service::getUser,
                    user -> Objects.equal(user.getId(), 1001) && Objects.equal(user.getName(), "Alvin"));
    }
}
