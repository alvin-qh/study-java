package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

import com.google.common.collect.ImmutableList;
import com.google.common.io.MoreFiles;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link com.google.common.io.CharSink CharSink} 类型用于写入字符数据
 *
 * <p>
 * {@link com.google.common.io.CharSink CharSink} 类型相当于一个字符内容输出的抽象,
 * 理论上, 所有可以输出字符内容的目标都可以抽象为 {@code CharSink} 类型对象
 * </p>
 *
 * <p>
 * Guava 默认提供了三种产生 {@link com.google.common.io.CharSink CharSink}
 * 对象的方法, 分别为:
 * <ul>
 * <li>
 * {@link com.google.common.io.Files#asCharSink(java.io.File,
 * java.nio.charset.Charset, com.google.common.io.FileWriteMode...)
 * Files.asCharSink(File, Charset, FileWriteMode...)} 方法, 用于通过一个
 * {@code File} 对象创建 {@code ByteSink} 对象
 * </li>
 * <li>
 * {@link MoreFiles#asByteSink(java.nio.file.Path, java.nio.file.OpenOption...)
 * MoreFiles.asByteSink(Path, OpenOption...)} 方法, 用于通过一个 {@code Path}
 * 对象创建 {@code ByteSink} 对象
 * 对象
 * </li>
 * <li>
 * {@link com.google.common.io.ByteSink#asCharSink(java.nio.charset.Charset)
 * ByteSink.asCharSink(Charset)} 方法, 用于从一个 {@code ByteSink} 对象中创建
 * {@code CharSink} 对象
 * </li>
 * </ul>
 * </p>
 */
class CharSinkTest {
    /**
     * 测试字符内容的写入
     *
     * <p>
     * 通过 {@link com.google.common.io.CharSink#write(CharSequence)
     * CharSink.write(CharSequence)} 方法可以将字符序列通过指定的
     * {@code CharSink} 对象写入目标中
     * </p>
     */
    @Test
    void write_shouldWriteCharSequenceIntoCharSink() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rw-------"));

        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 CharSink 和 CharSource 对象
            var sink = MoreFiles.asCharSink(path, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            var source = MoreFiles.asCharSource(path, StandardCharsets.UTF_8, StandardOpenOption.READ);

            // 通过 CharSink 对象将数据写入文件
            sink.write("Hello Guava");

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo("Hello Guava");
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 测试从 {@link java.io.Reader Reader} 写入字符内容
     *
     * <p>
     * 通过 {@link com.google.common.io.CharSink#writeFrom(Readable)
     * CharSink.writeFrom(Readable)} 方法可以从 {@link java.io.Reader Reader}
     * 对象中读取数据并写入 {@code ByteSink} 对象中
     * </p>
     */
    @Test
    void writeFrom_shouldWriteIntoCharSinkFromReader() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rw-------"));

        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 CharSink 和 CharSource 对象
            var sink = MoreFiles.asCharSink(path, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            var source = MoreFiles.asCharSource(path, StandardCharsets.UTF_8, StandardOpenOption.READ);

            // 通过 Reader 对象将数据写入 CharSink 对象
            var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
            try (var reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8)) {
                sink.writeFrom(reader);
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo("Hello Guava");
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 测试写入多行文本
     *
     * <p>
     * 通过 {@link com.google.common.io.CharSink#writeLines(Iterable)
     * CharSink.writeLines(Iterable)} 方法用于向 {@code CharSink} 中写入多行数据
     * </p>
     */
    @Test
    void writeLines_shouldWriteMultiLinesIntoCharSink() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rw-------"));

        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        var lines = ImmutableList.of("Line1", "Line2", "Line3");

        try {
            // 通过临时文件创建 CharSink 和 CharSource 对象
            var sink = MoreFiles.asCharSink(
                path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            var source = MoreFiles.asCharSource(
                path, StandardCharsets.UTF_8, StandardOpenOption.READ);

            // 向 CharSink 中写入多行数据, 确认写入的数据正确
            sink.writeLines(lines);
            then(source.readLines()).containsExactly("Line1", "Line2", "Line3");

            // 向 CharSink 中写入多行数据, 并以 "\r\n" 字符为行分隔符, 确认写入的数据正确
            sink.writeLines(lines, "\r\n");
            then(source.readLines()).containsExactly("Line1", "Line2", "Line3");

            // 向 CharSink 中写入字符串流, 确认写入的数据正确
            sink.writeLines(lines.stream());
            then(source.readLines()).containsExactly("Line1", "Line2", "Line3");

            // 向 CharSink 中写入字符串流, 并以 "\r\n" 字符为行分隔符, 确认写入的数据正确
            sink.writeLines(lines.stream(), "\r\n");
            then(source.readLines()).containsExactly("Line1", "Line2", "Line3");
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 测试从 {@link com.google.common.io.CharSink CharSink} 对象上打开一个
     * {@link java.io.OutputStream OutputStream} 对象, 用于写入字符内容
     *
     * <p>
     * 通过 {@link com.google.common.io.CharSink#openStream() CharSink.openStream()}
     * 方法从 {@code CharSink} 对象上打开一个 {@code OutputStream} 对象, 并通过该
     * {@code OutputStream} 对象进行数据写入
     * </p>
     *
     * <p>
     * 通过 {@link com.google.common.io.CharSink#openBufferedStream()
     * CharSink.openBufferedStream()} 方法从 {@code CharSink} 对象上打开一个
     * {@code BufferedOutputStream} 对象, 并通过该 {@code BufferedOutputStream}
     * 对象进行数据写入
     * </p>
     */
    @Test
    void openStream_shouldOpenOutputStreamFromCharSink() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rw-------"));

        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 CharSink 和 CharSource 对象
            var sink = MoreFiles.asCharSink(
                path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            var source = MoreFiles.asCharSource(
                path, StandardCharsets.UTF_8, StandardOpenOption.READ);

            // 通过 CharSink 对象获取 Writer 对象, 并通过该对象写入数据
            try (var writer = sink.openStream()) {
                writer.write("Hello Guava");
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo("Hello Guava");

            // 通过 CharSink 对象获取 BufferedWriter 对象, 并通过该对象写入数据
            try (var write = sink.openBufferedStream()) {
                write.write("Hello Guava");
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo("Hello Guava");
        } finally {
            Files.delete(path);
        }
    }
}
