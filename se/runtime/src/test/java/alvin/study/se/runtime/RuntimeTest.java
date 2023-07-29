package alvin.study.se.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

/**
 * 测试 {@link Runtime} 类型对象
 *
 * <p>
 * {@link Runtime} 类型是 Java 运行时库, 可以执行部分和系统相关的操作, 例如 {@link Runtime#exit(int)} 和 {@link Runtime#halt(int)},
 * 前者可以有序的结束当前 JVM 进程 (按顺序调用所有的关闭钩子) 后关闭虚拟机进程, 后者则是强制关闭虚拟机进程
 * </p>
 */
class RuntimeTest {
    // 获取当前虚拟机进程的 Runtime 对象
    private final Runtime runtime = Runtime.getRuntime();

    /**
     * 测试当前系统的逻辑核心数 (Logical cores)
     *
     * <p>
     * 通过 {@link Runtime#availableProcessors()} 方法可以获取有效的逻辑核心数, 该数值可以作为启动线程的依据
     * </p>
     *
     * <p>
     * 对于现代 CPU, 使用了"超线程"技术, 可以在一个核心上执行 1~2 个线程, {@code availableProcessors} 方法可以返回有效的逻辑核心数
     * </p>
     */
    @Test
    void availableProcessors_shouldGetAvailableThreadCount() {
        var threadCount = runtime.availableProcessors();
        then(threadCount).isGreaterThan(1);
    }

    /**
     * 执行命令行, 启动新进程
     *
     * <p>
     * 通过 {@link Runtime#exec(String[])} 等方法可以通过命令行启动一个进程, 返回该进程的 {@link Process} 类型对象
     * </p>
     *
     * <p>
     * 通过 {@link Process#inputReader(java.nio.charset.Charset) Process.inputReader(Charset)} 等方法可以读取进场的标准输出流,
     * 通过 {@link Process#outputWriter(java.nio.charset.Charset) Process.outputWriter(Charset)}
     * 等方法可以向进程的标准输入流写入内容, 从而达到和进程交互的目的
     * </p>
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void exec_shouldStartNewProcessByCommandLine() throws Exception {
        // 通过命令行 echo Hello Java 启动 echo 进程
        var process = runtime.exec(new String[]{ "echo", "Hello Java" });

        // 通过 Reader 读取进程的标准输出
        try (var reader = process.inputReader(StandardCharsets.UTF_8)) {
            // 确认标准输出已经就绪
            await().atMost(1, TimeUnit.SECONDS).until(reader::ready);

            // 读取标准输出
            var buf = CharBuffer.allocate(128);
            var len = reader.read(buf);
            then(len).isEqualTo(11);

            // 确认标准输出内容符合预期
            buf.flip();
            then(buf.toString()).isEqualTo("Hello Java\n");
        } finally {
            // 等待进程结束
            process.waitFor();
        }
    }

    /**
     * 获取当前虚拟机的总内存数和可用内存数, 单位为 Byte
     *
     * <p>
     * 通过 {@link Runtime#totalMemory()} 方法和 {@link Runtime#freeMemory()} 方法可以获取到为当前 JVM
     * 分配的总内存数和剩余的可用内存数
     * </p>
     *
     * <p>
     * 注意: 获取的总内存数并不是一成不变的, 因为启动 JVM 时可以设置分配的内存和最大分配内存, JVM 会根据内存使用情况进行自动调整
     * </p>
     */
    @Test
    void memory_shouldGetTotalAndFreeMemoryOfCurrentVM() {
        var totalMem = runtime.totalMemory();
        then(totalMem / 1024.0 / 1024.0).isGreaterThanOrEqualTo(16);

        var freeMem = runtime.freeMemory();
        then(freeMem).isLessThan(totalMem);
    }

    /**
     * 测试加载 C++ 动态库
     *
     * <p>
     * 参考 {@link JNIDemo} 类型和 {@link LoadLibrary LoadLibrary} 类型
     * </p>
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void loadLibrary_shouldLoadDynamicLibrary() {
        var demo = new JNIDemo();
        then(demo.itoa(123)).isEqualTo("123");
    }
}
