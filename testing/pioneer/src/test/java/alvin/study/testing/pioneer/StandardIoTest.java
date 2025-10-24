package alvin.study.testing.pioneer;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIn;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import org.junit.jupiter.api.Test;

/**
 * 替换 {@link System#in} 和 {@link System#out}, 用于测试标准输入输出
 *
 * <p>
 * 注解 {@link StdIo @StdIo} 后, {@link System#in} 和 {@link System#out}
 * 两个对象会被替换, 参考:
 * {@link System#setIn(java.io.InputStream) System.setIn(InputStream)} 和
 * {@link System#setOut(java.io.PrintStream) System.setOut(PrintStream)} 以及
 * {@link System#setErr(java.io.PrintStream) System.setErr(PrintStream)} 方法
 * </p>
 *
 * <p>
 * 可以为测试方法设置类型为 {@link StdOut}, {@link StdErr} 以及 {@link StdIn} 参数,
 * 其中:
 * <ul>
 * <li>
 * {@link StdOut} 参数对应 {@link System#out} 对象, 可以用来读取 {@link System#out}
 * 输出的内容
 * </li>
 * <li>
 * {@link StdErr} 参数对应 {@link System#err} 对象, 可以用来读取 {@link System#err}
 * 输出的内容
 * </li>
 * <li>
 * {@link StdIn} 参数对应 {@link System#in} 对象, 可以用来读取 {@link System#in}
 * 输入的内容
 * </li>
 * </ul>
 * </p>
 */
class StandardIoTest {
    /**
     * 测试通过 {@link StdIo @StdIo} 注解替换 {@link System#in} 对象
     *
     * @param out {@link StdOut} 对象, 用于读取 {@link System#out}
     *            输出的内容
     */
    @Test
    @StdIo
    void stdout_shouldReplaceStandardOutputStream(StdOut out) {
        // 输出内容
        System.out.println("Hello, Pioneer.\nThe System.out changed");

        // 通过 StdOut 对象读取输出的内容
        var lines = out.capturedLines();
        // 确认读取的输出内容符合预期
        then(lines)
                .hasSize(2)
                .containsExactly("Hello, Pioneer.", "The System.out changed");
    }

    /**
     * 测试通过 {@link StdIo @StdIo} 注解替换 {@link System#err} 对象
     *
     * @param err {@link StdErr} 对象, 用于读取 {@link System#err}
     *            输出的内容
     */
    @Test
    @StdIo
    void stdout_shouldReplaceStandardErrorStream(StdErr err) {
        // 输出内容
        System.err.println("Hello, Pioneer.\nThe System.out changed");

        // 通过 StdErr 对象读取输出的内容
        var lines = err.capturedLines();
        // 确认读取的输出内容符合预期
        then(lines).hasSize(2)
                .containsExactly("Hello, Pioneer.", "The System.out changed");
    }

    /**
     * 测试通过 {@link StdIo @StdIo} 注解替换 {@link System#err} 对象
     *
     * @param in {@link StdIn} 对象, 用于读取 {@link System#in} 输入的内容
     */
    @Test
    @StdIo({ "Hello", "World" })
    void stdin_shouldSetContentIntoOutputStream(StdIn in) {
        var lines = new ArrayList<String>();

        // 实例化 Scanner 对象用于读取 System.in 内容
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                // 按行读取输入内容
                lines.add(scanner.nextLine());
            }
        } catch (NoSuchElementException e) {}

        // 确认输入内容符合预期
        then(lines).containsExactly("Hello", "World");

        // 通过 StdIn 对象可以更简单的读取输入内容
        then(lines).isNotEqualTo(in.capturedLines());
    }
}
