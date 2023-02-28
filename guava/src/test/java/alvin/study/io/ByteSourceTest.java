package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.primitives.Bytes;

/**
 * 测试通过 {@link ByteSource} 类型读取数据
 *
 * <p>
 * {@link ByteSource} 是 Guava 库提供的针对于文件等
 * </p>
 */
class ByteSourceTest {
    /**
     * 将一个 {@code byte} 数组包装为 {@link ByteSource} 类型对象
     *
     * <p>
     * 通过 {@link ByteSource#wrap(byte[])} 方法可以将一个 {@code byte} 数组包装为 {@link ByteSource} 类型对象,
     * 通过包装后的对象可以读取到被包装的 {@link byte} 数组内容
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#sizeIfKnown()} 方法可以获取 {@link ByteSource} 包含数据的长度. 但如果当前 {@link ByteSource}
     * 实现类无法获取到自身包含数据的长度, 则返回空的 {@link com.google.common.base.Optional Optional} 对象
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#size()} 方法先通过 {@link ByteSource#sizeIfKnown()} 方法获取长度, 如果当前 {@link ByteSource}
     * 类型不支持, 则通过读取读取一次 {@link ByteSource} 中的内容求长度
     * </p>
     */
    @Test
    void wrap_shouldWrapByteArrayToByteSource() throws IOException {
        var data = "Hello Guava".getBytes(Charsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 确认可以获取到 ByteSource 可读取数据长度
        then(source.sizeIfKnown().get()).isEqualTo(11);

        // 确认可以获取到 ByteSource 可读取数据长度
        then(source.size()).isEqualTo(11);
    }

    /**
     * 从 {@link ByteSource} 中读取数据
     *
     * <p>
     * 通过 {@link ByteSource#read()} 方法读取 {@link ByteSource} 中包含的全部内容, 返回一个 {@code byte} 数组
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#read(ByteProcessor)} 方法读取 {@link ByteSource} 中包含的全部内容, 并将读取的内容进行转化后,
     * 返回转化后的对象
     * </p>
     */
    @Test
    void read_shouldReadDataFromByteSources() throws IOException {
        var data = "Hello Guava".getBytes(Charsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 确认读取了 ByteSource 中的全部内容
        then(source.read()).isEqualTo(data);

        // 确认读取了 ByteSource 中的全部内容, 且转换为 String 对象返回
        then(source.read(new ByteProcessor<String>() {
            // 存储每次读取内容的 OutputStream 对象
            private final ByteArrayOutputStream os = new ByteArrayOutputStream();

            @Override
            public boolean processBytes(byte[] buf, int off, int len) throws IOException {
                // 将每次读取的内容存入 OutputStream 对象
                os.write(buf, off, len);
                return true;
            }

            @Override
            public String getResult() {
                try {
                    try (os) {
                        // 将 OutputStream 内容转为字符串返回
                        return new String(os.toByteArray(), Charsets.UTF_8);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        })).isEqualTo("Hello Guava");
    }

    /**
     * 将 {@link ByteSource} 对象的内容拷贝到 {@link java.io.OutputStream OutputStream} 对象中
     *
     * <p>
     * 通过 {@link ByteSource#copyTo(java.io.OutputStream) ByteSource.copyTo(OutputStream)} 方法可以将 {@link ByteSource}
     * 对象的数据全部复制到 {@link java.io.OutputStream OutputStream} 对象中
     * </p>
     */
    @Test
    void copyTo_shouldCopyByteSourceIntoOutputStream() throws IOException {
        var data = "Hello Guava".getBytes(Charsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 产生一个 OutputStream 对象
        try (var os = new ByteArrayOutputStream()) {
            // 对数据进行拷贝
            source.copyTo(os);

            // 确认拷贝完成后, OutputStream 中包含原 ByteSource 中的所有数据
            then(os.toByteArray()).isEqualTo(data);
        }
    }

    /**
     * 将 {@link ByteSource} 对象的内容拷贝到 {@link com.google.common.io.ByteSink ByteSink} 对象中
     *
     * <p>
     * 通过 {@link ByteSource#copyTo(com.google.common.io.ByteSink) ByteSource.copyTo(ByteSink)} 方法可以将
     * {@link ByteSource} 对象的数据全部复制到 {@link com.google.common.io.ByteSink ByteSink} 对象中
     * </p>
     */
    @Test
    void copyTo_shouldCopyFromByteSourceToByteSink() throws IOException {
        var data = "Hello Guava".getBytes(Charsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 创建一个临时文件用于写入
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var file = Files.createTempFile("guava", ".tmp", attrs);

        try {
            // 将 Path 对象包装为 Sink 对象
            var sink = MoreFiles.asByteSink(file, StandardOpenOption.WRITE);

            // 将 ByteSource 的内容拷贝到 ByteSink 对象
            source.copyTo(sink);

            // 将 Path 对象包装为 ByteSource 对象
            source = MoreFiles.asByteSource(file, StandardOpenOption.READ);
            // 确认文件存储的内容和预期一致
            then(source.read()).isEqualTo(data);
        } finally {
            Files.delete(file);
        }
    }

    /**
     * 将多个 {@link ByteSource} 对象连接为一个 {@link ByteSource} 对象
     *
     * <p>
     * {@link ByteSource#concat(ByteSource...)} 方法可以将多个 {@link ByteSource} 对象连接为一个, 读取数据的时候按照链接是的顺序,
     * 依次读取每个 {@link ByteSource} 中的内容, 让这些 {@link ByteSource} 对象看起来如同一个
     * </p>
     */
    @Test
    void concat_shouldJoinByteResourcesToOne() throws IOException {
        var data1 = "abc".getBytes(Charsets.UTF_8);
        var data2 = "def".getBytes(Charsets.UTF_8);
        var data3 = "ghi".getBytes(Charsets.UTF_8);

        // 创建 3 个 ByteSource 对象
        var source1 = ByteSource.wrap(data1);
        var source2 = ByteSource.wrap(data2);
        var source3 = ByteSource.wrap(data3);

        // 将这些 ByteSource 对象连接为一个
        var source = ByteSource.concat(source1, source2, source3);
        // 确认可以通过连接后的对象一次性读取被连接的 ByteSource 对象
        then(source.read()).isEqualTo(Bytes.concat(data1, data2, data3));
    }

    /**
     * 对 {@link ByteSource} 中的数据求散列值
     *
     * <p>
     * 通过 {@link ByteSource#hash(com.google.common.hash.HashFunction) ByteSource.hash(HashFunction)} 方法可以对
     * {@link ByteSource} 对象中的数据求散列, 其中 {@link com.google.common.hash.HashFunction HashFunction} 散列计算对象可以从
     * {@link Hashing} 类中获得 (例如 {@link Hashing#sha256()})
     * </p>
     */
    @Test
    void hash_shouldCalculateHashCodeForByteSource() throws IOException {
        var data = "Hello Guava".getBytes(Charsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 获得 SHA-256 散列计算函数
        var hashFn = Hashing.sha256();

        // 通过指定的散列函数计算 DataSource 中数据的散列值
        var hash = source.hash(hashFn);
        // 确认散列值计算正确
        then(hash).isEqualTo(hashFn.hashString("Hello Guava", Charsets.UTF_8));
    }
}
