package alvin.study.testing.pioneer;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

import org.junitpioneer.jupiter.RetryingTest;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * 在测试失败时进行重试
 *
 * <p>
 * {@link RetryingTest @RetryingTest} 注解可以在测试失败后重新执行测试, 其中:
 *
 * <ul>
 * <li>
 * {@code value} 属性表示重试次数, 在测试失败达到此次数前成功一次则算测试通过, 否则测试失败
 * </li>
 * <li>
 * {@code maxAttempts} 含义同 {@code value} 属性, 表示测试总次数
 * </li>
 * <li>
 * {@code minSuccess} 表示测试至少要成功的次数, 该值必须小于 {@code value} 属性
 * </li>
 * <li>
 * {@code suspendForMs} 测试失败后, 和下次重试之间间隔的时间
 * </li>
 * <li>
 * {@code onExceptions} 一个异常类型, 表示只有抛出此类型异常才进行重试
 * </li>
 * </ul>
 * </p>
 */
@TestInstance(Lifecycle.PER_CLASS)
class RetryingFailingTest {
    private int retryTimes1 = 1;
    private int retryTimes2 = 1;
    private long lastTimestamp = 0;
    private int retryTimes3 = 1;

    /**
     * 测试在测试失败时进行重试
     *
     * <p>
     * 本例共执行 {@code 3} 次测试 (重试最大值), 最后一次成功
     * </p>
     */
    @RetryingTest(3)
    void retry_shouldSuccessOnce() {
        if (retryTimes1++ < 3) {
            // 令前两次测试失败
            fail();
        }

        // 最后一次测试成功
    }

    /**
     * 测试至少需要成功两次
     *
     * <p>
     * 本例共执行 {@code 4} 次测试, 后两次成功
     * </p>
     */
    @RetryingTest(maxAttempts = 4, minSuccess = 2)
    void retry_shouldSuccessTwice() {
        if (retryTimes2++ < 3) {
            // 令前两次测试失败
            fail();
        }

        // 后两次测试成功
    }

    /**
     * 测试重试间隔
     *
     * <p>
     * 本例共执行 {@code 2} 次测试, 间隔 {@code 300} 毫秒
     * </p>
     */
    @RetryingTest(maxAttempts = 2, suspendForMs = 300)
    void retry_shouldSuspendBeforeEachTest() {
        // 记录上次测试时间
        var timestamp = lastTimestamp;
        // 记录本次测试时间
        lastTimestamp = System.currentTimeMillis();

        // 确认测试时间间隔, 第一次测试会失败,
        // 第二次测试和第一次间隔 300 毫秒左右 (误差为 50 ms)
        then(System.currentTimeMillis() - timestamp)
                .isGreaterThanOrEqualTo(300)
                .isLessThan(350);
    }

    /**
     * 测试以特定异常作为失败依据
     *
     * <p>
     * 本例共执行 {@code 3} 次测试, 前两次抛出
     * {@link IllegalAccessException} 异常
     * </p>
     */
    @RetryingTest(maxAttempts = 3, onExceptions = IllegalArgumentException.class)
    void failsFirstWithExpectedThenWithUnexpectedException() {
        if (retryTimes3++ < 3) {
            // 前两次抛出异常
            throw new IllegalArgumentException();
        }

        // 最后一次成功
    }
}
