package alvin.study.misc.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试命令行
 */
class CliTest {
    // 保留系统原本的 System.out 和 System.err 对象
    private static final PrintStream ORIGINAL_OUT = System.out;
    private static final PrintStream ORIGINAL_ERR = System.err;

    // 用于替换 System.out 和 System.err 流的对象
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    // 实例化命令行对象, 以 Main 类型对象为根名令, 加入 DatetimeCli 作为子命令对象
    private final CommandLine cli = new CommandLine(new Main()).addSubcommand(new DatetimeCli());

    /**
     * 每次测试前执行
     *
     * <p>
     * 为了获取系统输出 (System.out, System.err), 需要将系统输出进行替换, 本例中替换为了一个内存流, 这样就可以再命令行执行完毕后,
     * 从中获取输出内容进行检测
     * </p>
     */
    @BeforeEach
    void beforeEach() {
        // 将两个内存流重置
        out.reset();
        err.reset();

        // 将 System.out 和 System.err 重定向
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    /**
     * 每次测试后执行
     *
     * <p>
     * 测试结束后, 将原本的系统输出对象 (System.out, System.err) 恢复为原对象
     * </p>
     */
    @AfterEach
    void afterEach() {
        /// 恢复 System.out 和 System.err 的指向
        System.setOut(ORIGINAL_OUT);
        System.setErr(ORIGINAL_ERR);

        // 恢复 Clock 对象
        DatetimeCli.setClock(Clock.systemDefaultZone());
    }

    /**
     * 测试根名令
     *
     * <p>
     * 该命令将由 {@link Main#call()} 方法执行
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar --plain}, {@code --plain},
     * 选项的作用是输出不带格式的文本, 便于断言比较
     * </p>
     */
    @Test
    void cli_shouldRootCommandExecuted() {
        cli.execute("--plain");
        then(out).hasToString("""

            This is PicoCli Demo

                Picocli aims to be the easiest way to create rich command line applications that
            can run on and off the JVM.

            """);
    }

    /**
     * 测试 {@code echo} 命令及其参数
     *
     * <p>
     * 该命令将由 {@link Main#echo(boolean, String, boolean, boolean, String[])} 方法执行
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar echo Hello World}
     * </p>
     */
    @Test
    void echo_shouldExecuteCommandWithArguments() {
        cli.execute("echo", "Hello World");
        then(out).hasToString("Hello World\n");
    }

    /**
     * 测试 {@code echo} 命令及其选项和参数
     *
     * <p>
     * 该命令将由 {@link Main#echo(boolean, String, boolean, boolean, String[])} 方法执行
     * </p>
     *
     * <p>
     * <ul>
     * <li>
     * {@code -cblue} 表示 {@code --color} (或 {@code -c}) 选项, 值为 {@code blue}, 输出蓝色字体
     * </li>
     * <li>
     * {@code -u} 表示 {@code --underline} (或 {@code -u}) 选项, 输出下划线字体
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar echo Hello World}
     * </p>
     */
    @Test
    void echo_shouldExecuteCommandWithFormatOptions() {
        cli.execute("echo", "-cblue", "-u", "Hello World");
        then(out).hasToString("\033[34m\033[4mHello World\033[24m\033[39m\033[0m\n");
    }

    /**
     * 测试 {@code echo} 命令及其参数
     *
     * <p>
     * 该命令将由 {@link Main#echo(boolean, String, boolean, boolean, String[])} 方法执行
     * </p>
     *
     * <p>
     * {@code --err} 表示 {@code --err} (或 {@code -e}) 选项, 输出文本到 {@code System.err} 流
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar echo --err Hello World}
     * </p>
     */
    @Test
    void echo_shouldExecuteCommandWithErrOption() {
        cli.execute("echo", "--err", "Hello World");
        then(err).hasToString("Hello World\n");
    }

    /**
     * 测试 {@code datetime} 命令及其 {@code -d} 参数
     *
     * <p>
     * 该命令将由 {@link DatetimeCli#call()} 方法执行
     * </p>
     *
     * <p>
     * {@code -d} 表示 {@code --date} (或 {@code -d}) 选项, 输出当前日期
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar datetime -d}
     * </p>
     */
    @Test
    void datetime_shouldExecuteCommandWithDateOption() {
        DatetimeCli.setClock(Clock.fixed(Instant.parse("2021-09-11T08:00:00Z"), ZoneId.of("UTC")));

        cli.execute("datetime", "-d");
        assertThat(out).hasToString("Date: 2021-09-11\n");
    }

    /**
     * 测试 {@code datetime} 命令及其 {@code -t} 参数
     *
     * <p>
     * 该命令将由 {@link DatetimeCli#call()} 方法执行
     * </p>
     *
     * <p>
     * {@code -t} 表示 {@code --time} (或 {@code -t}) 选项, 输出当前时间
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar datetime -t}
     * </p>
     */
    @Test
    void datetime_shouldExecuteCommandWithTimeOption() {
        DatetimeCli.setClock(Clock.fixed(Instant.parse("2021-09-11T08:00:00Z"), ZoneId.of("UTC")));

        cli.execute("datetime", "-t");
        assertThat(out).hasToString("Time: 08:00\n");
    }

    /**
     * 测试 {@code datetime} 命令及其 {@code -t} 参数
     *
     * <p>
     * 该命令将由 {@link DatetimeCli#call()} 方法执行
     * </p>
     *
     * <p>
     * <ul>
     * <li>
     * {@code -t} 表示 {@code --time} (或 {@code -t}) 选项, 输出当前时间
     * </li>
     * <li>
     * {@code -d} 表示 {@code --date} (或 {@code -d}) 选项, 输出当前时间
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar datetime -t -d}
     * </p>
     */
    @Test
    void datetime_shouldExecuteCommandWithDateAndTimeOptions() {
        DatetimeCli.setClock(Clock.fixed(Instant.parse("2021-09-11T08:00:00Z"), ZoneId.of("UTC")));

        cli.execute("datetime", "-t", "-d");
        assertThat(out).hasToString("Date: 2021-09-11\nTime: 08:00\n");
    }

    /**
     * 测试 {@code datetime} 命令及其 {@code -t} 参数
     *
     * <p>
     * 该命令将由 {@link DatetimeCli#call()} 方法执行
     * </p>
     *
     * <p>
     * 相当于执行命令 {@code java -jar cli.jar datetime}
     * </p>
     *
     * <p>
     * 由于未传递任何选项, 所以输出错误信息
     * </p>
     */
    @Test
    void datetime_shouldExecuteCommandWithoutAnyOptions() {
        cli.execute("datetime");
        then(out).hasToString("WARN: --date or --time option must have least one\n");
    }
}
