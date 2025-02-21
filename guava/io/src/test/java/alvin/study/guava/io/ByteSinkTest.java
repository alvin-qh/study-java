package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Test;

import com.google.common.io.MoreFiles;

/**
 * 测试 {@link com.google.common.io.ByteSink ByteSink} 类型用于写入字节数据
 *
 * <p>
 * {@link com.google.common.io.ByteSink ByteSink} 类型相当于一个字节内容输出的抽象, 理论上,
 * 所有可以输出字节数据的目标都可以抽象为
 * {@code ByteSink} 类型对象
 * </p>
 *
 * <p>
 * Guava 默认提供了两种产生 {@link com.google.common.io.ByteSink ByteSink} 对象的方法, 分别为:
 * <ul>
 * <li>
 * {@link com.google.common.io.Files#asByteSink(java.io.File, com.google.common.io.FileWriteMode...)
 * Files.asByteSink(File, FileWriteMode...)} 方法, 用于通过一个 {@code File} 对象创建
 * {@code ByteSink} 对象
 * </li>
 * <li>
 * {@link MoreFiles#asByteSink(java.nio.file.Path, java.nio.file.OpenOption...)
 * MoreFiles.asByteSink(Path, OpenOption...)} 方法, 用于通过一个 {@code Path} 对象创建
 * {@code ByteSink} 对象
 * </li>
 * </ul>
 * </p>
 */
class ByteSinkTest {
    /**
     * 测试字节数据的写入
     *
     * <p>
     * 通过 {@link com.google.common.io.ByteSink#write(byte[]) ByteSink.write(byte[])}
     * 方法可以将字节数据通过指定的
     * {@code ByteSink} 对象写入目标中
     * </p>
     */
    @Test
    void write_shouldWriteByteArrayIntoByteSink() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 ByteSink 和 ByteSource 对象
            var sink = MoreFiles.asByteSink(path, StandardOpenOption.WRITE);
            var source = MoreFiles.asByteSource(path, StandardOpenOption.READ);

            // 通过 ByteSink 对象将数据写入文件
            var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
            sink.write(data);

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo(data);
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 测试从 {@link java.io.InputStream InputStream} 写入字节数据
     *
     * <p>
     * 通过 {@link com.google.common.io.ByteSink#writeFrom(java.io.InputStream)
     * ByteSink.writeFrom(InputStream)}
     * 方法可以从 {@link java.io.InputStream InputStream} 对象中读取数据并写入 {@code ByteSink} 对象中
     * </p>
     */
    @Test
    void writeFrom_shouldWriteIntoByteSinkFromInputStream() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 ByteSink 和 ByteSource 对象
            var sink = MoreFiles.asByteSink(path, StandardOpenOption.WRITE);
            var source = MoreFiles.asByteSource(path, StandardOpenOption.READ);

            // 通过 InputStream 对象将数据写入 ByteSink 对象
            var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
            try (var is = new ByteArrayInputStream(data)) {
                sink.writeFrom(is);
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo(data);
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 测试从 {@link com.google.common.io.ByteSink ByteSink} 对象上打开一个
     * {@link java.io.OutputStream OutputStream} 对象,
     * 用于写入字节数据
     *
     * <p>
     * 通过 {@link com.google.common.io.ByteSink#openStream() ByteSink.openStream()}
     * 方法从
     * {@code ByteSink} 对象上打开一个 {@code OutputStream} 对象, 并通过该 {@code OutputStream}
     * 对象进行数据写入
     * </p>
     *
     * <p>
     * 通过 {@link com.google.common.io.ByteSink#openBufferedStream()
     * ByteSink.openBufferedStream()} 方法从
     * {@code ByteSink} 对象上打开一个 {@code BufferedOutputStream} 对象, 并通过该
     * {@code BufferedOutputStream} 对象进行数据写入
     * </p>
     */
    @Test
    void openStream_shouldOpenOutputStreamFromByteSink() throws IOException {
        // 创建一个临时文件
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var path = Files.createTempFile("guava-sink", ".tmp", attrs);

        try {
            // 通过临时文件创建 ByteSink 和 ByteSource 对象
            var sink = MoreFiles.asByteSink(path, StandardOpenOption.WRITE);
            var source = MoreFiles.asByteSource(path, StandardOpenOption.READ);

            // 通过 ByteSink 对象获取 OutputStream 对象, 并通过该对象写入数据
            var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
            try (var os = sink.openStream()) {
                os.write(data);
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo(data);

            // 通过 ByteSink 对象获取 BufferedOutputStream 对象, 并通过该对象写入数据
            try (var os = sink.openBufferedStream()) {
                os.write(data);
            }

            // 确认读取的数据和写入的数据一致
            then(source.read()).isEqualTo(data);
        } finally {
            Files.delete(path);
        }
    }
}
