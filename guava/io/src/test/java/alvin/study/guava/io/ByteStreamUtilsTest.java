package alvin.study.guava.io;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

/**
 * 测试 {@link ByteStreams} 工具类
 *
 * <p>
 * {@link ByteStreams} 工具类集合了对 IO 流操作的一系列方法, 以简化 IO 流操作的复杂性
 * </p>
 */
class ByteStreamUtilsTest {
    /**
     * 将字符串转为 {@link InputStream} 对象
     *
     * <p>
     * 该方法返回一个 {@link InputStream} 对象, 且可以从该对象中读取到所给的字符串内容
     * </p>
     *
     * <p>
     * 该方法用于后续的测试中, 用于方便的获得一个可以读取到已知内容的 {@link InputStream} 对象
     * </p>
     *
     * @param s 字符串对象
     * @return 包含字符串内容的 {@link InputStream} 对象
     */
    private static InputStream asStream(String s) {
        return new ByteArrayInputStream(s.getBytes(Charsets.UTF_8));
    }

    /**
     * 将对象转为 {@link InputStream} 对象
     *
     * <p>
     * 该方法返回一个 {@link InputStream} 对象, 且可以从该对象中反序列化得到所给的对象
     * </p>
     *
     * <p>
     * 该方法用于后续的测试中, 用于方便的获得一个可以读取到已知内容的 {@link InputStream} 对象
     * </p>
     */
    @SneakyThrows
    private static InputStream asStream(Object o) {
        Preconditions.checkArgument(o instanceof Serializable, "Input object cannot be serialized");

        try (var bo = new ByteArrayOutputStream()) {
            try (var oo = new ObjectOutputStream(bo)) {
                oo.writeObject(o);
                bo.flush();

                return new ByteArrayInputStream(bo.toByteArray());
            }
        }
    }

    /**
     * 测试从输入流中读取数据
     *
     * <p>
     * {@link ByteStreams#read(InputStream, byte[], int, int)} 方法可以从一个 {@link InputStream} 对象中读取字节数据,
     * 并存入到所给的 byte 数组中, 该方法返回实际读取的字节数
     * </p>
     */
    @Test
    void read_shouldReadBytesFromInputStreamIntoByteArray() throws IOException {
        try (var in = asStream("Hello Guava")) {
            var data = new byte[100];

            // 从 InputStream 中读取数据, 并存入 data 数组中
            var len = ByteStreams.read(in, data, 0, data.length);
            then(len).isEqualTo(11);

            // 确认实际读取的内容符合预期
            then(Arrays.copyOf(data, len)).isEqualTo("Hello Guava".getBytes(Charsets.UTF_8));
        }
    }

    /**
     * 测试从输入流中读取一个对象
     *
     * <p>
     * {@link ByteStreams#readBytes(InputStream, ByteProcessor)} 方法从给定的输入流中读取数据, 并将读取的结果送入
     * {@link ByteProcessor} 接口对象中进行处理, 最终返回一个对象结果
     * </p>
     *
     * <p>
     * {@link ByteProcessor} 应该具备两个能力: 1. 分批接收数据; 2. 当所有数据接收完毕后, 能返回一个和接收数据相关的对象结果
     * </p>
     *
     * <p>
     * 本例中的 {@link ByteProcessor} 接口对象在接受完所有数据后, 将数据反序列化, 得到对象结果返回
     * </p>
     */
    @Test
    void readBytes_shouldReadObjectFromInputStream() throws IOException {
        var srcObj = new Staff(1L, "Alvin");

        // 通过已有对象产生一个输入流
        try (var in = asStream(srcObj)) {
            var dstObj = ByteStreams.readBytes(in, new ByteProcessor<Staff>() {
                // 保存输入数据的字节流对象
                private final ByteArrayOutputStream bio = new ByteArrayOutputStream();

                @Override
                public boolean processBytes(byte @NotNull [] buf, int off, int len) {
                    // 将输入的数据存储输出字节流对象中
                    bio.write(buf, off, len);
                    return true;
                }

                @Override
                @SneakyThrows
                public @Nullable Staff getResult() {
                    try (bio) {
                        bio.flush();
                        // 将字节流存储的数据反序列化为对象
                        return Staff.deserialize(bio.toByteArray());
                    }
                }
            });

            // 确认从输入流中读取到了正确的对象
            then(dstObj).isEqualTo(srcObj);
        }
    }

    /**
     * 按要求的字节数读取输入流
     *
     * <p>
     * {@link ByteStreams#readFully(InputStream, byte[], int, int)} 方法用于从流中读取指定长度的数据
     * </p>
     *
     * <p>
     * {@link ByteStreams#readFully(InputStream, byte[], int, int)} 方法和
     * {@link java.io.DataInputStream#readFully(byte[], int, int) DataInputStream.readFully(byte[], int, int)}
     * 方法作用一致
     * </p>
     *
     * <p>
     * 和 {@link ByteStreams#read(InputStream, byte[], int, int)} 方法不同, 后者结束读取只需要满足: 1. 读取数据达到指定长度; 2.
     * 流已经被读完. 而前者则必须读到指定的数据长度, 否则会阻塞等待, 直到流中写入了足够的数据, 或者在流提前结束或关闭时抛出异常
     * </p>
     */
    @Test
    void readFully_shouldReadEnoughBytesFromInputStream() throws Exception {
        // 定义一个管道, 获取读取管道数据的输入流
        try (var pi = new PipedInputStream()) {
            // 将一个输出流连接到管道, 用于向管道写入数据
            try (var po = new PipedOutputStream(pi)) {
                // 定义一个线程对象, 异步对管道的另一端进行操作
                var inputThread = new Thread(() -> {
                    // 分三次向管道写入数据, 每次间隔 500 毫秒
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(500);
                            po.write("Hello Guava".getBytes(Charsets.UTF_8));
                        } catch (IOException | InterruptedException ignored) {
                        }
                    }
                });

                // 启动线程, 开始管道操作
                inputThread.start();

                var timestamp = System.currentTimeMillis();

                // 定义总读取长度
                var data = new byte[33];
                // 从输入流读取指定长度的数据
                ByteStreams.readFully(pi, data, 0, data.length);

                // 确认整个数据读取持续了 1500 毫秒, 即三次写入管道的整体花费时间
                // 所以 readFully 方法在未读取到指定长度数据前, 会一直阻塞, 直到规定长度的数据都被读取
                then(System.currentTimeMillis() - timestamp).isGreaterThan(1500L);
                // 确认读取数据的正确性
                then(data).isEqualTo("Hello GuavaHello GuavaHello Guava".getBytes(Charsets.UTF_8));
            }

            // 由于流已经读完, 所以即使再多读一个字节都会导致 EOFException 异常
            var data = new byte[1];
            thenThrownBy(() -> ByteStreams.readFully(pi, data, 0, data.length)).isInstanceOf(EOFException.class);
        }
    }

    /**
     * 跳过输入流中指定长度的字节数
     *
     * <p>
     * {@link ByteStreams#skipFully(InputStream, long)} 方法用于跳过输入流指定长度的数据, 相当于从流中读取了指定长度的数据且丢弃
     * </p>
     *
     * <p>
     * {@link ByteStreams#readFully(InputStream, byte[], int, int)} 方法类似, {@code skipFully} 方法会跳过指定长度的数据,
     * 如果流中的数据暂时不足, 则该方法会被阻塞, 直到流中有足够的数据. 如果流提前结束或关闭, 则抛出异常
     * </p>
     *
     * <p>
     * {@link ByteStreams#skipFully(InputStream, long)} 方法和
     * {@link java.io.DataInputStream#skipNBytes(long) DataInputStream.skipNBytes(long)} 方法作用一致
     * </p>
     */
    @Test
    void skipFully_shouldReadBytesFromInputStream() throws Exception {
        // 定义一个管道, 获取读取管道数据的输入流
        try (var pi = new PipedInputStream()) {
            // 将一个输出流连接到管道, 用于向管道写入数据
            try (var po = new PipedOutputStream(pi)) {
                // 定义一个线程对象, 异步对管道的另一端进行操作
                var inputThread = new Thread(() -> {
                    try {
                        // 分三次向管道写入数据, 每次间隔 500 毫秒
                        for (int i = 0; i < 3; i++) {
                            Thread.sleep(500);
                            po.write("Hello Guava".getBytes(Charsets.UTF_8));
                        }
                    } catch (IOException | InterruptedException ignored) {
                    }
                });

                // 启动线程, 开始管道操作
                inputThread.start();

                var timestamp = System.currentTimeMillis();

                // 跳过指定的字节数, 如果输入流中的数据暂时不足, 则进入阻塞直到有足够的数据被跳过
                ByteStreams.skipFully(pi, 22);

                // 在跳过数据的基础上进行读取, 确认读取到指定的内容
                var data = new byte[11];
                ByteStreams.readFully(pi, data);

                // 确认整个数据读取持续了 1500 毫秒, 即全部三次写入管道的整体花费时间
                then(System.currentTimeMillis() - timestamp).isGreaterThan(1500L);
                then(data).isEqualTo("Hello Guava".getBytes(Charsets.UTF_8));
            }

            // 由于流已经关闭, 所以即使再多读一个字节都会导致 EOFException 异常
            thenThrownBy(() -> ByteStreams.skipFully(pi, 1)).isInstanceOf(EOFException.class);
        }
    }

    /**
     * 将输入流的全部内容读取到一个字节数组中
     *
     * <p>
     * {@link ByteStreams#toByteArray(InputStream)} 方法将给定的输入流内容全部读取, 并将读取的结果存入 {@code byte} 数组返回
     * </p>
     */
    @Test
    void toByteArray_shouldReadBytesStreamIntoByteArray() throws IOException {
        try (var in = asStream("Hello Guava")) {
            // 读取输入流, 得到保存输入流全部内容的数组
            var bytes = ByteStreams.toByteArray(in);

            // 确认得到的数组内容正确
            then(bytes).isEqualTo("Hello Guava".getBytes(Charsets.UTF_8));
        }
    }

    /**
     * 将输入流的全部内容写入输出流中
     *
     * <p>
     * {@link ByteStreams#copy(InputStream, java.io.OutputStream)} 方法相当于进行流直接的拷贝, 将一个输入流的全部内容进行读取,
     * 并写入另一个输出流中. 该方法返回拷贝的字节数
     * </p>
     */
    @Test
    void copy_shouldCopyBytesFromInputStreamIntoOutputStream() throws IOException {
        try (var in = asStream("Hello Guava")) {
            try (var out = new ByteArrayOutputStream()) {
                // 读取输入流, 将其全部内容拷贝到一个输出流中
                var len = ByteStreams.copy(in, out);
                // 确认整个拷贝的数据长度
                then(len).isEqualTo(11);

                out.flush();
                // 确认拷贝的数据正确性
                then(out.toByteArray()).isEqualTo("Hello Guava".getBytes(Charsets.UTF_8));
            }
        }
    }

    /**
     * 将可读通道的全部内容写入到可写通道中
     *
     * <p>
     * {@link ByteStreams#copy(java.nio.channels.ReadableByteChannel, java.nio.channels.WritableByteChannel)
     * ByteStreams.copy(ReadableByteChannel, WritableByteChannel)} 方法相当于对进行通道间拷贝操作, 将一个
     * {@link java.nio.channels.ReadableByteChannel ReadableByteChannel} 对象的可读取的全部内容复制到一个
     * {@link java.nio.channels.WritableByteChannel WritableByteChannel} 对象中
     * </p>
     *
     * <p>
     * 为了简单起见, 本例通过 {@link Files#newByteChannel(java.nio.file.Path, java.nio.file.OpenOption...)
     * Files.newByteChannel(Path, OpenOption...)} 方法从文件中创建 {@link java.nio.channels.SeekableByteChannel
     * SeekableByteChannel} 对象, 该对象可以表示 {@code ReadableByteChannel} 或 {@code WritableByteChannel}, 主要看
     * {@code newByteChannel} 方法的 {@code OpenOption} 参数是 {@link StandardOpenOption#READ} 还是
     * {@link StandardOpenOption#WRITE}
     * </p>
     */
    @Test
    void copy_shouldCopyBytesFromByteBufferIntoOutputStream() throws IOException {
        // 定义文件权限对象
        // 权限为当前用户可读写, 其它用户和组无权限
        var fileAttrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));

        // 创建两个临时文件, 一个用于读, 一个用于写
        var fileIn = Files.createTempFile("guava-io", ".tmp", fileAttrs);
        var fileOut = Files.createTempFile("guava-io", ".tmp", fileAttrs);

        // 向用于读的文件内写入内容
        Files.writeString(fileIn, "Hello Guava", Charsets.UTF_8);

        // 通过用于读的文件创建可读通道, StandardOpenOption.READ 表示创建的通道为一个 ReadableByteChannel
        try (var channelIn = Files.newByteChannel(fileIn, StandardOpenOption.READ)) {
            // 通过用于写的文件创建可写通道, StandardOpenOption.WRITE 表示创建的通道为一个 WritableByteChannel
            try (var channelOut = Files.newByteChannel(fileOut, StandardOpenOption.WRITE)) {
                // 将可读通道的全部内容写入到可写通道中
                var len = ByteStreams.copy(channelIn, channelOut);
                // 确认拷贝的字节数
                then(len).isEqualTo(11);
            }

            // 确认用于写入的文件中写入了期望的内容
            var content = Files.readString(fileOut, Charsets.UTF_8);
            then(content).isEqualTo("Hello Guava");
        } finally {
            Files.delete(fileIn);
            Files.delete(fileOut);
        }
    }

    /**
     * 限制输入流的读取长度
     *
     * <p>
     * 通过 {@link ByteStreams#limit(InputStream, long)} 方法可以限制从一个输入流中读取的字节数, 该方法返回一个代理
     * {@link InputStream} 对象, 通过该对象最多只能读取规定长度的字节
     * </p>
     */
    @Test
    void limit_shouldLimitSizeOfBytesForInputStream() throws IOException {
        var srcIn = asStream("Hello Guava");

        // 包装 srcIn 对象, 限制只能从输入流中读取 5 字节
        try (var limitedIn = ByteStreams.limit(srcIn, 5)) {
            // 从输入流读取数据
            var data = ByteStreams.toByteArray(limitedIn);
            // 确认只读取到了 5 字节数据
            then(data).isEqualTo("Hello".getBytes(Charsets.UTF_8));
        }
    }

    /**
     * 在连续内存空间中存储数据并读取
     *
     * <p>
     * {@link ByteStreams#newDataOutput()} 方法产生一个 {@link java.io.DataOutput DataOutput} 接口对象,
     * 可以将各种类型数据写入一块连续内存中 (即一个 {@code byte} 类型数组), 其中:
     * <ul>
     * <li>
     * {@code newDataOutput} 方法返回的 {@code DataOutput} 对象底层是通过一个 {@link ByteArrayOutputStream}
     * 对象提供数据存储支持的
     * </li>
     * <li>
     * 可以通过 {@link ByteStreams#newDataOutput(int)} 方法设置连续内存的初始大小, 以降低内存重分配的几率, 内部是通过
     * {@link ByteArrayOutputStream#ByteArrayOutputStream(int)} 构造器构建 {@link ByteArrayOutputStream} 对象
     * </li>
     * <li>
     * 也可以通过 {@link ByteStreams#newDataOutput(ByteArrayOutputStream)} 方法, 用一个已有的 {@link ByteArrayOutputStream}
     * 对象提供数据存储支持
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link ByteStreams#newDataInput(byte[])} 方法产生一个 {@link java.io.DataInput DataInput} 接口对象,
     * 可以从一段连续内存空间 ({@code byte} 数组) 中按顺序读取所需的各种类型数据; 而方法
     * {@link ByteStreams#newDataInput(ByteArrayInputStream)} 则可以从一个现有的 {@link ByteArrayInputStream}
     * 对象中进行数据读取
     * </p>
     */
    @Test
    void newDataOutputAndInput_shouldCreateDataOutput() throws IOException {
        // 测试通过连续内存空间写入和读取各种类型数据
        {
            var output = ByteStreams.newDataOutput();
            output.writeChar('A');
            output.writeByte(0x20);
            output.writeInt(123);
            output.writeBoolean(true);

            var input = ByteStreams.newDataInput(output.toByteArray());
            then(input.readChar()).isEqualTo('A');
            then(input.readByte()).isEqualTo((byte) 0x20);
            then(input.readInt()).isEqualTo(123);
            then(input.readBoolean()).isTrue();
        }

        // 测试通过 ByteArrayOutputStream 和 ByteArrayInputStream 对象写入和读取各类型数据
        try (var os = new ByteArrayOutputStream()) {
            var output = ByteStreams.newDataOutput(os);
            output.writeChar('A');
            output.writeByte(0x20);
            output.writeInt(123);
            output.writeBoolean(true);

            try (var is = new ByteArrayInputStream(os.toByteArray())) {
                var input = ByteStreams.newDataInput(is);
                then(input.readChar()).isEqualTo('A');
                then(input.readByte()).isEqualTo((byte) 0x20);
                then(input.readInt()).isEqualTo(123);
                then(input.readBoolean()).isTrue();
            }
        }
    }

    /**
     * 读取并丢弃输入流中的所有数据, 返回读取数据的字节数
     *
     * <p>
     * {@link ByteStreams#exhaust(InputStream)} 方法用于从所给的输入流中读取所有的数据, 但并不对数据进行存储操作, 而是全部丢弃,
     * 最终返回数据的长度
     * </p>
     */
    @Test
    void exhaust_shouldReadAndDiscardAllDataFromInputStream() throws IOException {
        try (var is = asStream("Hello Guava")) {
            var len = ByteStreams.exhaust(is);
            then(len).isEqualTo(11);
        }
    }

    /**
     * 创建一个空输出流
     *
     * <p>
     * 通过 {@link ByteStreams#nullOutputStream()} 方法可以产生一个"空"的输出流, 即具备输出流接口的行为, 但并不做实际写入动作
     * </p>
     */
    @Test
    void nullOutputStream_shouldReadAndDiscardAllDataFromInputStream() throws IOException {
        try (var os = ByteStreams.nullOutputStream()) {
            os.write("Hello Guava".getBytes(Charsets.UTF_8));
        }
    }
}
