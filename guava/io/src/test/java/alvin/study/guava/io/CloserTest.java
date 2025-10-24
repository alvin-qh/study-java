package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Closer;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试 Guava 的流关闭器
 *
 * <p>
 * 通过 {@link Closer} 类对象可以对实现了 {@link java.io.Closeable Closeable}
 * 接口的对象进行统一的关闭操作, 具体流程如下:
 * <ul>
 * <li>
 * 通过 {@link Closer#register(java.io.Closeable) Closer.register(Closeable)}
 * 方法注册所有相关的 {@code Closeable} 接口对象
 * </li>
 * <li>
 * 当代码执行过程中出现异常, 需要通过 {@link Closer#rethrow(Throwable)
 * Closer.rethrow(Throwable)} 方法对异常进行抛出处理, 抛出方式参见
 * {@link com.google.common.base.Throwables#propagateIfPossible(Throwable, Class)
 * Throwables.propagateIfPossible(Throwable, Class)} 方法以及
 * {@code ThrowablesTest.propagateIfPossible_shouldThrowSpecifiedExceptionOrUncheckedException()}
 * 演示方法
 * </li>
 * </ul>
 * </p>
 */
class CloserTest {
    /**
     * 演示通过 {@link Closer} 类型对 {@link java.io.Closeable Closeable}
     * 接口对象进行统一关闭
     */
    @Test
    @SneakyThrows
    void closer_shouldCloseCloseableObject() {
        // 创建 Closer 对象
        var closer = Closer.create();

        try {
            // 将需要关闭的 Closeable 对象进行注册
            var out = closer.register(new ByteArrayOutputStream());
            out.write("Hello Guava".getBytes(StandardCharsets.UTF_8));

            var data = out.toByteArray();

            // 将需要关闭的 Closeable 对象进行注册
            var in = closer.register(new ByteArrayInputStream(data));
            then(in.readNBytes(data.length)).isEqualTo(data);
        } catch (Exception e) {
            // 出现异常, 通过 Closer 对象统一抛出异常
            closer.rethrow(e);
        } finally {
            // 对 Closer 对象进行关闭, 同时关闭注册的其它 Closeable 对象
            closer.close();
        }
    }
}
