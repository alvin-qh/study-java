package alvin.study.se.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 进程工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessUtil {
    /**
     * 通过命令行启动进程
     *
     * <p>
     * 命令行是一个数组, 通常表示用空格分隔的命令, 例如:
     *
     * <pre>
     * echo -e Hello World
     * </pre>
     * <p>
     * 可以写作
     *
     * <pre>
     * ProcessUtil.exec("echo", "-e", "Hello World");
     * </pre>
     * </p>
     *
     * @param command 命令行数组
     * @return {@link Process} 进程对象
     */
    public static Process exec(String... command) throws IOException {
        return new ProcessBuilder(command).start();
    }

    /**
     * 获取进程的标准输出内容
     *
     * <p>
     * 在本例中, 通过 {@link Process#getInputStream()} 方法连接到进程的标准输出上, 进程标准输出会输入到这个流
     * </p>
     *
     * <p>
     * 为了防止读取标准输出时因无输出内容而无限等待, 本例中在一个新线程中进行读取, 如果达到超时时间, 会将线程 interrupt 掉,
     * 以确保不会锁死线程
     * </p>
     *
     * @param process {@link Process} 进程对象
     * @param timeout 读取标准输出的超时时间, 在此时间内未完成读取, 则中断
     * @return 标准输出内容字符串
     */
    public static String fetchOutput(Process process, long timeout) {
        var sb = new StringBuilder();

        // 定义线程对象, 在该线程中进行指定进程的标准输出读取
        var thread = new Thread(() -> {
            // 将一个输入流连接到进程的标准输出上, 并在其基础上构建字符串 Reader 进行读取
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                // 定义读取字符缓冲
                var buf = new char[1024];
                while (true) {
                    // 读取内容
                    var n = reader.read(buf);
                    // 判断是否已读取到 EOF
                    if (n <= 0) {
                        break;
                    }
                    sb.append(new String(buf, 0, n));
                }
            } catch (IOException ignore) {
            }
        });

        // 启动线程
        thread.start();
        try {
            // 等待线程结束
            thread.join(timeout);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }

        // 如果在等待时间结束后, 线程依然未结束, 则中断线程
        if (thread.isAlive()) {
            thread.interrupt();
        }

        // 返回进程标准输出内容
        return sb.toString();
    }

    /**
     * 根据进程 {@code id} 查询进程信息
     *
     * @param pid 进程 {@code id}
     * @return 进程信息 {@link ProcessInfo} 对象的 {@link Optional} 包装
     */
    public static Optional<@NotNull ProcessInfo> process(long pid) {
        // 根据进程 id 获取进程对象实例
        return ProcessHandle.of(pid).map(ProcessInfo::of);
    }

    /**
     * 根据进程 {@code id} 终止一个进程
     *
     * @param pid      进程 {@code id}
     * @param forcibly {@code true} 表示强行终止
     * @return 指定的进程是否已被终止
     */
    public static boolean kill(long pid, boolean forcibly) {
        // 根据 id 查询进程对象
        return ProcessHandle.of(pid).map(p -> {
            if (forcibly) {
                // 强行终止进程
                p.destroyForcibly();
            } else {
                // 终止进程
                p.destroy();
            }
            return true;
        }).orElse(false);
    }

    /**
     * 列举所有进程
     *
     * @param condition 过滤条件
     * @return 符合条件的进程对象集合
     */
    public static List<@NotNull ProcessInfo> allProcesses(Predicate<ProcessInfo> condition) {
        var stream = ProcessHandle.allProcesses().map(ProcessInfo::of);
        if (condition != null) {
            stream = stream.filter(condition);
        }
        return stream.toList();
    }

    /**
     * 获取当前进程
     *
     * @return 当前进程信息对象
     */
    @Contract(" -> new")
    public static @NotNull ProcessInfo current() {
        return ProcessInfo.of(ProcessHandle.current());
    }

    /**
     * 根据进程 {@code id} 查询子进程列表
     *
     * @param pid       进程 {@code id}
     * @param condition 匹配进程的条件
     * @return 子进程列表
     */
    public static List<@NotNull ProcessInfo> children(long pid, Predicate<ProcessInfo> condition) {
        // 根据进程 id 查询进程信息
        var stream = ProcessHandle.of(pid)
            // 如果进程存在, 则进一步查询子进程
            .map(ProcessHandle::children).orElse(Stream.empty())
            // 子进程对象转换为目标对象
            .map(ProcessInfo::of);

        if (condition != null) {
            // 如果具备过滤条件, 则对子进程进行过滤
            stream = stream.filter(condition);
        }
        return stream.toList();
    }

    /**
     * 保存进程信息的 Pojo 类型
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProcessInfo implements Serializable {
        /**
         * 进程 {@code id}
         */
        private final long pid;

        /**
         * 是否为活动进程
         */
        private final boolean alive;

        /**
         * 启动进程的命令行
         */
        private final String commandLine;

        /**
         * 启动进程的命令
         */
        private final String command;

        /**
         * 启动进程的命令参数
         */
        private final String[] arguments;

        /**
         * 进程启动时间
         */
        private final Instant startAt;

        /**
         * 进程使用的 CPU 时间
         */
        private final Duration cpuTime;

        /**
         * 通过 {@link ProcessHandle} 对象创建当前类型对象
         *
         * @param handle {@link ProcessHandle} 对象
         * @return 转换后的当前类型对象
         */
        @Contract("_ -> new")
        private static @NotNull ProcessInfo of(@NotNull ProcessHandle handle) {
            return new ProcessInfo(
                handle.pid(),
                handle.isAlive(),
                handle.info().commandLine().orElse(""),
                handle.info().command().orElse(""),
                handle.info().arguments().orElse(null),
                handle.info().startInstant().orElse(null),
                handle.info().totalCpuDuration().orElse(null)
            );
        }

        /**
         * 重写 {@link Object#toString()} 方法
         *
         * <p>
         * 结果中包含进程 {@code id} 和进程的 {@code commandLine} 两项内容
         * </p>
         *
         * @return 表示对象的字符串
         */
        @Override
        public String toString() {
            return String.format("pid=%d, command-line=\"%s\"", pid, commandLine);
        }
    }
}
