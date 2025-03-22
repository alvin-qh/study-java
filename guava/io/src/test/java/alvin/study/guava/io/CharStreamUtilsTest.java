package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.annotation.Nonnull;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link CharStreams} 工具类
 *
 * <p>
 * {@link CharStreams} 工具类集合了对字符流操作的一系列方法, 以简化字符流操作的复杂性
 * </p>
 */
class CharStreamUtilsTest {
    /**
     * 将一个字符串包装为 {@link Reader} 类型对象
     *
     * <p>
     * 从包装后的 {@link Reader} 对象中可以读取到被包装的字符串
     * </p>
     *
     * @param s 被包装的字符串
     * @return 包装了 {@code s} 字符串参数的 {@link Reader} 对象
     */
    private static Reader asReader(String s) {
        return new InputStreamReader(
            new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 从 {@link Readable} 类型对象中读取字符串
     *
     * <p>
     * 通过 {@link CharStreams#toString(Readable)} 方法可以将一个
     * {@link Readable} 对象中的所有字符读出, 组成字符串对象返回
     * </p>
     */
    @Test
    void toString_shouldReadCharactersFromReadableIntoString() throws IOException {
        try (var reader = asReader("Hello Guava")) {
            // 从 Readable 对象中读取字符串
            var s = CharStreams.toString(reader);
            // 确认读取的字符串正确
            then(s).isEqualTo("Hello Guava");
        }
    }

    /**
     * 从 {@link Readable} 类型对象中读取所有的行
     *
     * <p>
     * 通过 {@link CharStreams#readLines(Readable)} 方法可以将一个
     * {@link Readable} 对象中的内容按行读出, 返回的字符串集合保存了每行字符串
     * </p>
     *
     * <p>
     * 通过 {@link CharStreams#readLines(Readable, LineProcessor)}
     * 方法可以将一个 {@link Readable} 对象中的内容按行读出, 并在读取完毕后,
     * 通过给定的 {@link LineProcessor} 对象转换成所需的对象返回
     * </p>
     */
    @Test
    void readLines_shouldReadLinesFromReadable() throws IOException {
        // 测试通过 Reader 读取行字符串集合
        try (var reader = asReader("""
            Line1
            Line2
            Line3
            """)) {
            // 读取所有的行, 返回行集合
            var lines = CharStreams.readLines(reader);
            // 确认读取的行集合
            then(lines).containsExactly("Line1", "Line2", "Line3");
        }

        // 测试通过 Reader 读取行字符串集合, 并将结果进行转化
        try (var reader = asReader("""
            Line1
            Line2
            Line3
            """)) {
            var lines = CharStreams.readLines(reader, new LineProcessor<String>() {
                // 保存读取结果
                private final List<String> lines = Lists.newArrayList();

                @Override
                public boolean processLine(@Nonnull String line) {
                    // 将读取的结果进行保存
                    return lines.add(line);
                }

                @Override
                public String getResult() {
                    // 将保存的结果通过 >> 符号进行连接
                    return Joiner.on(">>").join(lines);
                }
            });
            then(lines).isEqualTo("Line1>>Line2>>Line3");
        }
    }

    /**
     * 通过一个 {@link java.io.Writer Writer} 对象向指定的 {@link Appendable}
     * 类型对象写入字符串内容
     *
     * <p>
     * 通过 {@link CharStreams#asWriter(Appendable)} 方法可以创建一个
     * {@link java.io.Writer Writer} 类型对象, 并通过该 {@code Writer}
     * 对象将字符串内容写入 {@code Appendable} 对象
     * </p>
     */
    @Test
    void asWriter_shouldWrapAppendableIntoWriterObject() throws IOException {
        // 创建 Appendable 类型对象
        var builder = new StringBuilder();

        try (var writer = CharStreams.asWriter(builder)) {
            // 通过 Writer 对象写入字符串或字符
            writer.write("Hello Guava");
            writer.write('.');
        }
        // 确认通过 Writer 对象将字符串写入 Appendable 对象
        then(builder).hasToString("Hello Guava.");
    }

    /**
     * 创建一个空 {@link java.io.Writer Writer} 对象
     *
     * <p>
     * 通过 {@link CharStreams#nullWriter()} 方法可以创建一个 "空"
     * {@link java.io.Writer Writer} 对象, 具备 {@code Writer} 接口的所有行为,
     * 但并不会做实际工作
     * </p>
     */
    @Test
    void nullWriter_shouldCreateWriterForWriteNothing() throws IOException {
        try (var writer = CharStreams.nullWriter()) {
            writer.write("Hello Guava");
            writer.write('.');
        }
    }

    /**
     * 将字符串内容从 {@link Reader} 对象拷贝到 {@link Appendable} 对象
     *
     * <p>
     * 通过 {@link CharStreams#nullWriter()} 方法可以创建一 个"空"
     * {@link java.io.Writer Writer} 对象, 具备 {@code Writer} 接口的所有行为,
     * 但并不会做实际工作
     * </p>
     */
    @Test
    void copy_shouldCopyCharactersFromReadableIntoAppendable() throws IOException {
        try (var reader = asReader("Hello Guava")) {
            var builder = new StringBuilder();
            var len = CharStreams.copy(reader, builder);

            then(len).isEqualTo(11);
            then(builder).hasToString("Hello Guava");
        }
    }

    /**
     * 从 {@link Reader} 对象中跳过指定个数的字符
     *
     * <p>
     * 通过 {@link CharStreams#skipFully(Reader, long)} 方法可以跳过所给
     * {@link Reader} 的指定字符 (相当于读取这些字符并丢弃), 以便可以读取后续的字符内容
     * </p>
     *
     * <p>
     * 如果 {@link Reader} 对象中暂时无法提供足够的字符被跳过, {@code skipFully}
     * 方法会阻塞, 直到读到指定长度字符, 或者因为 {@link Reader} 对象关闭等原因抛出异常
     * </p>
     */
    @Test
    void skipFully_shouldSkipCharactersFromReadable() throws IOException {
        // 定义一个管道, 获取读取管道数据的输入流
        try (var pi = new PipedInputStream()) {
            // 将一个输出流连接到管道, 用于向管道写入数据
            try (var po = new PipedOutputStream(pi)) {

                // 定义一个线程对象, 异步对管道的另一端进行操作
                var inputThread = new Thread(() -> {
                    try {
                        // 分三次向管道写入数据, 每次间隔 50ms
                        for (int i = 0; i < 3; i++) {
                            Thread.sleep(50);
                            po.write("Hello Guava".getBytes(StandardCharsets.UTF_8));
                        }
                    } catch (IOException | InterruptedException ignored) {}
                });

                // 启动线程, 开始管道操作
                inputThread.start();

                try (var reader = new InputStreamReader(pi, StandardCharsets.UTF_8)) {
                    var timestamp = System.currentTimeMillis();

                    // 跳过指定的字符数, 如果 Reader 中的数据暂时不足, 则进入阻塞直到有足够的祖父被跳过
                    CharStreams.skipFully(reader, 22);

                    // 在跳过数据的基础上进行读取, 确认读取到指定的内容
                    var buf = CharBuffer.allocate(11);
                    reader.read(buf);
                    buf.flip();

                    // 确认整个数据读取持续了 150 毫秒, 即全部三次写入管道的整体花费时间
                    then(System.currentTimeMillis() - timestamp).isGreaterThan(150);
                    then(buf.toString()).isEqualTo("Hello Guava");
                }
            }
        }
    }
}
