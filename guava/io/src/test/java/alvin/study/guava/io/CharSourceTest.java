package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;

import org.junit.jupiter.api.Test;

/**
 * 测试通过 {@link CharSource} 类型读取字符内容
 *
 * <p>
 * {@link CharSource} 类型对象是 Guava 针对一系列读取字符的数据源 (例如文件或网络)
 * 的抽象, 以保障下游在读取数据时无需关注数据源本身的特点和细节
 * </p>
 *
 * <p>
 * Guava 默认提供了四种产生 {@link CharSource} 对象的方法, 分别为:
 * <ul>
 * <li>
 * {@link com.google.common.io.Files#asCharSource(java.io.File,
 * java.nio.charset.Charset) Files.asCharSource(File, Charset)} 方法,
 * 用于通过一个 {@link java.io.File File} 对象创建 {@link CharSource} 对象
 * </li>
 * <li>
 * {@link MoreFiles#asCharSource(java.nio.file.Path,
 * java.nio.charset.Charset, java.nio.file.OpenOption...)
 * MoreFiles.asCharSource(Path, Charset, OpenOption...)} 方法, 用于通过一个
 * {@link java.nio.file.Path Path} 对象创建 {@link CharSource} 对象
 * </li>
 * <li>
 * {@link Resources#asCharSource(java.net.URL, java.nio.charset.Charset)
 * Resources.asCharSource(URL)} 方法, 用于通过一个
 * {@link java.net.URL URL} 对象创建 {@link CharSource} 对象
 * </li>
 * <li>
 * {@link com.google.common.io.ByteSource#asCharSource(java.nio.charset.Charset)
 * ByteSource.asCharSource(Charset)} 方法, 将一个 {@code ByteSource}
 * 类型对象包装为 {@code CharSource} 对象
 * </li>
 * </ul>
 * </p>
 */
class CharSourceTest {
    /**
     * 将一个字符序列对象包装为 {@link CharSource} 类型对象
     *
     * <p>
     * 通过 {@link CharSource#isEmpty()} 方法可以确定对象中是否包含字符,
     * 返回值为 {@code true} 表示一个空的 {@link CharSource} 对象
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#wrap(CharSequence)} 方法可以将一个字符序列包装为
     * {@link CharSource} 类型对象, 通过包装后的对象可以读取到被包装的字符序列内容
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#length()} 方法可以获取 {@link CharSource}
     * 包含字符序列的长度. 但如果当前 {@link CharSource}
     * 实现类无法获取到自身包含字符序列的长度, 则返回空的
     * {@link com.google.common.base.Optional Optional} 对象
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#lengthIfKnown()} 方法先通过
     * {@link CharSource#length()} 方法获取字符序列长度, 如果当前
     * {@link CharSource} 类型不支持, 则通过完全读取 {@link CharSource}
     * 中的内容来求长度
     * </p>
     */
    @Test
    void wrap_shouldWrapCharSequenceToCharSource() throws IOException {
        // 将字符串包装为 CharSource 对象
        var source = CharSource.wrap("Hello Guava");

        // 确认 CharSource 中包含字符内容
        then(source.isEmpty()).isFalse();

        // 确认可以获取到 CharSource 中字符序列的长度
        then(source.length()).isEqualTo(11);

        // 确认可以获取到 CharSource 中字符序列的长度
        then(source.lengthIfKnown().orNull()).isEqualTo(11);
    }

    /**
     * 从 {@link CharSource} 中读取字符内容
     *
     * <p>
     * 通过 {@link CharSource#read()} 方法读取 {@link CharSource}
     * 中包含的全部字符内容, 返回一个字符序列组成的字符串
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#readFirstLine()} 方法读取 {@link CharSource}
     * 中包含的字符内容的第一行
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#readLines()} 方法读取 {@link CharSource}
     * 中包含的所有行, 返回一个包含行字符串的集合
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#readLines(LineProcessor)} 方法读取
     * {@link CharSource} 中包含的所有行, 并通过 {@link LineProcessor}
     * 对象将读取的行集合转为所需类型的对象
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#forEachLine(java.util.function.Consumer)
     * CharSource.forEachLine(Consumer)} 方法读取 {@link CharSource}
     * 中包含的所有行, 并通过 {@link java.util.function.Consumer Consumer}
     * 对象对每一行字符串进行回调
     * </p>
     */
    @Test
    void read_shouldReadStringsFromCharSources() throws IOException {
        // 将多行字符串包装为 CharSource 对象
        var source = CharSource.wrap("""
            Line1
            Line2
            Line3
            """);

        // 确认读取了 CharSource 中的全部字符内容
        then(source.read()).isEqualTo("Line1\nLine2\nLine3\n");

        // 确认读取了 CharSource 中的第一行字符内容
        then(source.readFirstLine()).isEqualTo("Line1");

        // 确认读取了 CharSource 中的所有行
        var lines = source.readLines();
        then(lines).containsExactly("Line1", "Line2", "Line3");

        // 确认读取了 CharSource 中的所有行, 并处理为一个字符串对象返回
        then(source.readLines(new LineProcessor<String>() {
            private final List<String> lines = Lists.newArrayList();

            @Override
            public boolean processLine(@Nonnull String line) {
                // 将 CharSource 中读取到的行字符串进行保存
                return lines.add(line);
            }

            @Override
            public String getResult() {
                // 将行集合连接为一个字符串返回
                return Joiner.on(">>").join(lines);
            }
        })).isEqualTo("Line1>>Line2>>Line3");

        var build = new StringBuilder();
        // 确认对从 CharSource 中读取到的每一行进行回调
        source.forEachLine(line -> {
            if (!build.isEmpty()) {
                build.append("::");
            }
            build.append(line);
        });
        then(build).hasToString("Line1::Line2::Line3");
    }

    /**
     * 将 {@link CharSource} 对象的内容拷贝到 {@link Appendable} 对象中
     *
     * <p>
     * 通过 {@link CharSource#copyTo(Appendable) CharSource.copyTo(OutputStream)}
     * 方法可以将 {@link CharSource} 对象中包含的字符内容全部复制到 {@link Appendable}
     * 对象中
     * </p>
     */
    @Test
    void copyTo_shouldCopyCharSourceIntoAppendableObject() throws IOException {
        // 将字符串包装为 CharSource 对象
        var source = CharSource.wrap("Hello Guava");

        // 产生一个 Appendable 对象
        var builder = new StringBuilder();

        // 对字符内容进行拷贝
        source.copyTo(builder);
        // 确认拷贝完成后, Appendable 中包含原 CharSource 中的所有字符
        then(builder).hasToString("Hello Guava");
    }

    /**
     * 将 {@link CharSource} 对象的内容拷贝到 {@link com.google.common.io.CharSink
     * CharSink} 对象中
     *
     * <p>
     * 通过 {@link CharSource#copyTo(com.google.common.io.CharSink)
     * CharSource.copyTo(CharSink)} 方法可以将
     * {@link CharSource} 对象的字符内容全部复制到 {@link com.google.common.io.CharSink
     * CharSink} 对象中
     * </p>
     */
    @Test
    void copyTo_shouldCopyFromCharSourceToCharSink() throws IOException {
        // 将字符串包装为 CharSource 对象
        var source = CharSource.wrap("Hello Guava");

        // 创建一个临时文件用于写入
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var file = Files.createTempFile("guava", ".tmp", attrs);

        try {
            // 将 Path 对象包装为 CharSink 对象
            var sink = MoreFiles.asCharSink(file, StandardCharsets.UTF_8, StandardOpenOption.WRITE);

            // 将 CharSource 的内容拷贝到 CharSink 对象
            source.copyTo(sink);

            // 将 Path 对象包装为 CharSource 对象
            source = MoreFiles.asCharSource(file, StandardCharsets.UTF_8, StandardOpenOption.READ);
            // 确认文件存储的内容和预期一致
            then(source.read()).isEqualTo("Hello Guava");
        } finally {
            Files.delete(file);
        }
    }

    /**
     * 将多个 {@link CharSource} 对象连接为一个 {@link CharSource} 对象
     *
     * <p>
     * {@link CharSource#concat(CharSource...)} 方法可以将多个 {@link CharSource}
     * 对象连接为一个, 读取数据的时候按照链接是的顺序, 依次读取每个 {@link CharSource}
     * 中的内容, 让这些 {@link CharSource} 对象看起来如同一个
     * </p>
     */
    @Test
    void concat_shouldJoinCharResourcesToOne() throws IOException {
        // 创建 3 个 CharSource 对象
        var source1 = CharSource.wrap("abc");
        var source2 = CharSource.wrap("def");
        var source3 = CharSource.wrap("ghi");

        // 将这些 CharSource 对象连接为一个
        var source = CharSource.concat(source1, source2, source3);
        // 确认可以通过连接后的对象一次性读取被连接的 CharSource 对象
        then(source.read()).isEqualTo("abcdefghi"); // cspell: disable-line
    }

    /**
     * 测试创建"空" {@link CharSource} 对象
     *
     * <p>
     * 通过 {@link CharSource#empty()} 方法可以创建一个空的 {@link CharSource} 对象
     * </p>
     */
    @Test
    void empty_shouldCreateAnEmptyCharSource() throws IOException {
        var source = CharSource.empty();

        // 确认 CharSource 为空
        then(source.isEmpty()).isTrue();
        then(source.lengthIfKnown().orNull()).isZero();

        // 确认读取的数据为空
        then(source.read()).isEmpty();
    }

    /**
     * 测试通过 {@link CharSource} 对象创建 {@link java.io.Reader Reader} 对象
     *
     * <p>
     * 通过 {@link CharSource#openStream()} 方法可以建立一个用于从 {@link CharSource}
     * 读取字符内容的 {@link java.io.Reader Reader} 对象
     * </p>
     *
     * <p>
     * 通过 {@link CharSource#openBufferedStream()} 方法可以建立一个用于从
     * {@link CharSource} 读取字符内容的 {@link java.io.BufferedReader
     * BufferedReader} 对象
     * </p>
     */
    @Test
    void openStream_shouldOpenCharSourceAsInputStream() throws IOException {
        // 将字符串包装为 CharSource 对象
        var source = CharSource.wrap("Hello Guava");

        try (var reader = source.openStream()) {
            var buffer = CharBuffer.allocate(100);

            reader.read(buffer);
            buffer.flip();

            then(buffer.toString()).isEqualTo("Hello Guava");
        }

        try (var reader = source.openBufferedStream()) {
            then(reader.readLine()).isEqualTo("Hello Guava");
        }
    }

    /**
     * 将 {@link CharSource} 类型对象转为 {@link com.google.common.io.ByteSource
     * ByteSource} 类型对象
     *
     * <p>
     * 通过 {@link CharSource#asByteSource(java.nio.charset.Charset)
     * CharSource.asByteSource(Charset)}
     * 方法可以将一个字符数据源 ({@link CharSource}) 转为字节数据源
     * ({@link com.google.common.io.ByteSource ByteSource}),
     * 以便直接对数据源存储的字符编码数据以字节进行读取
     * </p>
     */
    @Test
    void asCharSource_shouldConvertByteSourceToCharSource() throws IOException {
        var cSource = CharSource.wrap("Hello Guava");

        // 将字符数据源转为字节数据源对象
        var bSource = cSource.asByteSource(StandardCharsets.UTF_8);
        // 确认从数据源读取到预期的字节数据
        then(bSource.read()).isEqualTo("Hello Guava".getBytes(StandardCharsets.UTF_8));
    }
}
