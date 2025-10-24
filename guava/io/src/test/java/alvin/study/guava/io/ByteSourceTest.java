package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;
import com.google.common.primitives.Bytes;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试通过 {@link ByteSource} 类型读取数据
 *
 * <p>
 * {@link ByteSource} 类型对象是 Guava 针对一系列字节输入设备的抽象 (例如文件或网络),
 * 理论上, 所有可以读取字节数据的源都可以抽象为 {@link ByteSource} 类型对象
 * </p>
 *
 * <p>
 * Guava 默认提供了四种产生 {@link ByteSource} 对象的方法, 分别为:
 * <ul>
 * <li>
 * {@link com.google.common.io.Files#asByteSource(java.io.File)
 * Files.asByteSource(File)} 方法, 用于通过一个
 * {@link java.io.File File} 对象创建 {@link ByteSource} 对象
 * </li>
 * <li>
 * {@link MoreFiles#asByteSource(java.nio.file.Path, java.nio.file.OpenOption...)
 * MoreFiles.asByteSource(Path, OpenOption...)} 方法, 用于通过一个
 * {@link java.nio.file.Path Path} 对象创建 {@link ByteSource} 对象
 * </li>
 * <li>
 * {@link Resources#asByteSource(java.net.URL) Resources.asByteSource(URL)}
 * 方法, 用于通过一个 {@link java.net.URL URL} 对象创建 {@link ByteSource} 对象
 * </li>
 * <li>
 * {@link com.google.common.io.CharSource#asByteSource(
 * java.nio.charset.Charset) CharSource.asByteSource(Charset)} 方法,
 * 用于通过一个 {@link com.google.common.io.CharSource CharSource} 对象创建
 * {@link ByteSource} 对象
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 可以参考 {@link CachedUrlLoader} 范例, 演示了从网络资源读取数据时, 如何通过
 * {@link ByteSource} 类型对象来抽象网络资源和缓存文件资源这两种不同的数据源
 * </p>
 */
class ByteSourceTest {
    /**
     * 将一个 {@code byte} 数组包装为 {@link ByteSource} 类型对象
     *
     * <p>
     * 通过 {@link ByteSource#isEmpty()} 方法可以确定对象中是否包含数据,
     * 返回值为 {@code true} 表示一个空的 {@link ByteSource}
     * 对象
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#wrap(byte[])} 方法可以将一个 {@code byte}
     * 数组包装为 {@link ByteSource} 类型对象, 通过包装后的对象可以读取到被包装的
     * {@link byte} 数组内容
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#sizeIfKnown()} 方法可以获取 {@link ByteSource}
     * 包含数据的长度. 但如果当前 {@link ByteSource} 实现类无法获取到自身包含数据的长度,
     * 则返回空的 {@link com.google.common.base.Optional Optional} 对象
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#size()} 方法先通过 {@link ByteSource#sizeIfKnown()}
     * 方法获取长度, 如果当前 {@link ByteSource} 类型不支持, 则通过完全读取
     * {@link ByteSource} 中的内容来求长度
     * </p>
     */
    @Test
    @SneakyThrows
    void wrap_shouldWrapByteArrayToByteSource() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 确认 ByteSource 中有数据
        then(source.isEmpty()).isFalse();

        // 确认可以获取到 ByteSource 可读取数据长度
        then(source.sizeIfKnown().orNull()).isEqualTo(11);

        // 确认可以获取到 ByteSource 可读取数据长度
        then(source.size()).isEqualTo(11);
    }

    /**
     * 从 {@link ByteSource} 中读取数据
     *
     * <p>
     * 通过 {@link ByteSource#read()} 方法读取 {@link ByteSource} 中包含的全部内容,
     * 返回一个 {@code byte} 数组
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#read(ByteProcessor)} 方法读取 {@link ByteSource}
     * 中包含的全部内容, 并将读取的内容进行转化后, 返回转化后的对象
     * </p>
     */
    @Test
    @SneakyThrows
    void read_shouldReadDataFromByteSources() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 确认读取了 ByteSource 中的全部内容
        then(source.read()).isEqualTo(data);

        // 确认读取了 ByteSource 中的全部内容, 且转换为 String 对象返回
        then(source.read(new ByteProcessor<String>() {
            // 存储每次读取内容的 OutputStream 对象
            private final ByteArrayOutputStream os = new ByteArrayOutputStream();

            @Override
            public boolean processBytes(byte[] buf, int off, int len) {
                // 将每次读取的内容存入 OutputStream 对象
                os.write(buf, off, len);
                return true;
            }

            @Override
            public String getResult() {
                try {
                    try (os) {
                        // 将 OutputStream 内容转为字符串返回
                        return os.toString(StandardCharsets.UTF_8);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        })).isEqualTo("Hello Guava");
    }

    /**
     * 将 {@link ByteSource} 对象的内容拷贝到 {@link java.io.OutputStream
     * OutputStream} 对象中
     *
     * <p>
     * 通过 {@link ByteSource#copyTo(java.io.OutputStream)
     * ByteSource.copyTo(OutputStream)} 方法可以将 {@link ByteSource}
     * 对象的数据全部复制到 {@link java.io.OutputStream OutputStream} 对象中
     * </p>
     */
    @Test
    @SneakyThrows
    void copyTo_shouldCopyByteSourceIntoOutputStream() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
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
     * 将 {@link ByteSource} 对象的内容拷贝到 {@link com.google.common.io.ByteSink
     * ByteSink} 对象中
     *
     * <p>
     * 通过 {@link ByteSource#copyTo(com.google.common.io.ByteSink)
     * ByteSource.copyTo(ByteSink)} 方法可以将 {@link ByteSource} 对象的数据全部复制到
     * {@link com.google.common.io.ByteSink ByteSink} 对象中
     * </p>
     */
    @Test
    @SneakyThrows
    void copyTo_shouldCopyFromByteSourceToByteSink() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 创建一个临时文件用于写入
        var attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        var file = Files.createTempFile("guava", ".tmp", attrs);

        try {
            // 将 Path 对象包装为 ByteSink 对象
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
     * {@link ByteSource#concat(ByteSource...)} 方法可以将多个
     * {@link ByteSource} 对象连接为一个, 读取数据的时候按照链接是的顺序,
     * 依次读取每个 {@link ByteSource} 中的内容, 让这些 {@link ByteSource}
     * 对象看起来如同一个
     * </p>
     */
    @Test
    @SneakyThrows
    void concat_shouldJoinByteResourcesToOne() {
        var data1 = "abc".getBytes(StandardCharsets.UTF_8);
        var data2 = "def".getBytes(StandardCharsets.UTF_8);
        var data3 = "ghi".getBytes(StandardCharsets.UTF_8);

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
     * 通过 {@link ByteSource#hash(com.google.common.hash.HashFunction)
     * ByteSource.hash(HashFunction)} 方法可以对 {@link ByteSource} 对象中的数据求散列,
     * 其中 {@link com.google.common.hash.HashFunction HashFunction} 散列计算对象可以从
     * {@link Hashing} 类中获得 (例如 {@link Hashing#sha256()})
     * </p>
     */
    @Test
    @SneakyThrows
    void hash_shouldCalculateHashCodeForByteSource() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 获得 SHA-256 散列计算函数
        var hashFn = Hashing.sha256();

        // 通过指定的散列函数计算 DataSource 中数据的散列值
        var hash = source.hash(hashFn);
        // 确认散列值计算正确
        then(hash).isEqualTo(hashFn.hashString("Hello Guava", StandardCharsets.UTF_8));
    }

    /**
     * 测试将一个 {@link ByteSource} 对象进行分割
     *
     * <p>
     * 通过 {@link ByteSource#slice(long, long)} 方法可以将指定 {@link ByteSource}
     * 的一部分内容分割成为新的 {@link ByteSource} 对象
     * </p>
     */
    @Test
    @SneakyThrows
    void slice_shouldSplitByteSourceIntoMultiple() {
        var data = "1234567890".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 将 5 个字节之后的内容分割为新的 ByteSource 对象
        var source1 = source.slice(0, 5);
        then(source1.sizeIfKnown().get()).isEqualTo(5L);

        // 将后 5 个字节分割为新的 ByteSource 对象
        var source2 = source.slice(5, Long.MAX_VALUE);
        then(source2.sizeIfKnown().get()).isEqualTo(5L);

        // 确认分割的 DataSource 内容正确
        then(source1.read()).isEqualTo("12345".getBytes(StandardCharsets.UTF_8));
        then(source2.read()).isEqualTo("67890".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 测试创建"空" {@link ByteSource} 对象
     *
     * <p>
     * 通过 {@link ByteSource#empty()} 方法可以创建一个空的 {@link ByteSource} 对象
     * </p>
     */
    @Test
    @SneakyThrows
    void empty_shouldCreateAnEmptyByteSource() {
        var source = ByteSource.empty();

        // 确认 ByteSource 为空
        then(source.isEmpty()).isTrue();
        then(source.sizeIfKnown().get()).isZero();

        // 确认读取的数据为空
        then(source.read()).isEqualTo(new byte[] {});
    }

    /**
     * 测试通过 {@link ByteSource} 对象创建 {@link java.io.InputStream InputStream} 对象
     *
     * <p>
     * 通过 {@link ByteSource#openStream()} 方法可以建立一个用于从 {@link ByteSource}
     * 读取数据的 {@link java.io.InputStream InputStream} 对象
     * </p>
     *
     * <p>
     * 通过 {@link ByteSource#openBufferedStream()} 方法可以建立一个用于从
     * {@link ByteSource} 读取数据的 {@link java.io.BufferedInputStream
     * BufferedInputStream} 对象
     * </p>
     */
    @Test
    @SneakyThrows
    void openStream_shouldOpenByteSourceAsInputStream() {
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);
        // 将 byte 数组包装为 ByteSource 对象
        var source = ByteSource.wrap(data);

        // 从 ByteBuffer 对象打开一个 InputStream 对象
        try (var is = source.openStream()) {
            then(is.readAllBytes()).isEqualTo(data);
        }

        // 从 ByteBuffer 对象打开一个 BufferedInputStream 对象
        try (var is = source.openBufferedStream()) {
            then(is.readAllBytes()).isEqualTo(data);
        }
    }

    /**
     * 测试 {@link CachedUrlLoader} 类, 从网络资源或缓存中读取数据
     *
     * <p>
     * 本例是 {@link DataSource} 类型的典型应用, 即将数据源进行抽象, 以隐藏数据源本身,
     * 以便以一种统一的方式进行数据读取
     * </p>
     */
    @Test
    @SneakyThrows
    void loadHTML_shouldLoadHTMLByCache() {
        // 创建 CacheLoader 对象
        try (var loader = new CachedUrlLoader()) {
            // 确认网络资源数据尚未被缓存
            var mayCache = loader.cacheInfo("https://www.baidu.com");
            then(mayCache).isEmpty();

            // 读取网络资源数据
            var dataFromUrl = loader.loadHTML("https://www.baidu.com");

            // 等待缓存完毕
            await().atMost(2, TimeUnit.SECONDS)
                    .untilAsserted(
                        () -> then(loader.cacheInfo("https://www.baidu.com"))
                                .isPresent());

            // 获取缓存对象
            var cache = loader.cacheInfo("https://www.baidu.com").get();
            // 确认缓存文件存在
            then(cache.getPath()).exists();
            // 确认缓存有效期正确
            then(cache.getCreatedAt()).isBefore(Instant.now());

            // 再次读取网络数据, 由于前一次读取已经形成了缓存, 所以本次操作不会访问网络资源,
            // 而是从缓存中直接读取数据
            var dataFromCache = loader.loadHTML("https://www.baidu.com");
            then(dataFromUrl).isEqualTo(dataFromCache);

            // 确认缓存的内容和网络读取数据内容一致
            var fileData = Files.readAllBytes(cache.getPath());
            then(fileData).isEqualTo(dataFromCache);
        }
    }

    /**
     * 将 {@link ByteSource} 类型对象转为 {@link com.google.common.io.CharSource
     * CharSource} 类型对象
     *
     * <p>
     * 通过 {@link ByteSource#asCharSource(java.nio.charset.Charset)
     * ByteSource.asCharSource(Charset)} 方法可以将一个字节数据源
     * ({@link ByteSource}) 转为字符数据源
     * ({@link com.google.common.io.CharSource CharSource}),
     * 以便直接对数据源存储的字节数据以字符编码进行读取
     * </p>
     */
    @Test
    @SneakyThrows
    void asCharSource_shouldConvertByteSourceToCharSource() {
        var bSource = ByteSource.wrap("Hello Guava".getBytes(StandardCharsets.UTF_8));

        // 将字节数据源转为字符数据源对象
        var cSource = bSource.asCharSource(StandardCharsets.UTF_8);
        // 确认从数据源读取到预期的字符串
        then(cSource.read()).isEqualTo("Hello Guava");
    }
}
