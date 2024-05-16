package alvin.study.misc.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Callable;

/**
 * 通过类型定义一个命令
 *
 * <p>
 * 除了通过方法定义命令外, 也可以通过类定义一个命令, 如果命令的参数比较复杂, 同类型定义的方法较为有优势
 * </p>
 *
 * <p>
 * {@link Command @Command} 注解用于定义一个"命令", 属性包括:
 * <ul>
 * <li>
 * {@code name} 属性指定命令的名称
 * </li>
 * <li>
 * {@code mixinStandardHelpOptions} 属性指定是否混入标准帮助选项
 * </li>
 * <li>
 * {@code version} 属性表示当前命令的版本号
 * </li>
 * <li>
 * {@code description} 属性指定当前命令的详细描述
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 注意: 这个类必须实现 {@link Callable} 或 {@link Runnable} 接口, 并重写其
 * {@link Callable#call()} 或 {@link Runnable#run()} 方法, 作为命令执行的入口
 * </p>
 *
 * <p>
 * 一般推荐使用 {@link Callable} 类型, 并通过其泛型参数指定 {@link Callable#call()} 方法的返回值,
 * 最为整个进程的返回值
 * </p>
 *
 * <p>
 * 当前命令的格式为
 *
 * <pre>
 * java -jar cli/target/cli.jar datetime -d -t
 * </pre>
 * </p>
 */
@Command(name = "datetime", mixinStandardHelpOptions = true, version = "datetime 1.0", description = "Show datetime")
public final class DatetimeCli implements Callable<Integer> {
    // 定义获取当前时间的 Clock 对象
    private static Clock clock = Clock.systemDefaultZone();

    /**
     * 定义选项表示要输出当前日期
     *
     * <p>
     * {@link Option @Option} 注解表示一个命令选项, 属性包括:
     *
     * <ul>
     * <li>
     * {@code names} 属性表示选项的名称, 一般定义 {@code -<short name>} 和 {@code --<full name>}
     * 两个名称
     * </li>
     * <li>
     * {@code description} 属性表示选项的描述
     * </li>
     * </ul>
     * <p>
     * 其余属性参考 {@link Option @Option} 注解
     * </p>
     */
    @Option(names = { "-d", "--date" }, description = "Show the date of today")
    private boolean date;

    /**
     * 定义选项表示要输出当前时间
     *
     * <p>
     * {@link Option @Option} 注解表示一个命令选项, 属性包括:
     *
     * <ul>
     * <li>
     * {@code names} 属性表示选项的名称, 一般定义 {@code -<short name>} 和 {@code --<full name>}
     * 两个名称
     * </li>
     * <li>
     * {@code description} 属性表示选项的描述
     * </li>
     * </ul>
     * <p>
     * 其余属性参考 {@link Option @Option} 注解
     * </p>
     */
    @Option(names = { "-t", "--time" }, description = "Show the time of now")
    private boolean time;

    /**
     * 修改获取当前时间的 {@link Clock} 对象
     *
     * <p>
     * 在编写单元测试时, 通过默认的 {@link Clock} 对象获取的
     * </p>
     *
     * @param clock {@link Clock} 类型对象
     */
    public static void setClock(Clock clock) {
        DatetimeCli.clock = clock;
    }

    /**
     * 执行命令的方法
     *
     * <p>
     * 重写 {@link Callable#call()} 方法, 当命令执行时回调此方法
     * </p>
     *
     * @return 进程返回值
     */
    @Override
    public Integer call() {
        // 判断 --date 和 --time 选项是否设置
        if (this.date || this.time) {
            if (this.date) {
                // 设置 --date 选项, 输出当前日期
                System.out.println("Date: " + LocalDate.now(clock));
            }

            if (this.time) {
                // 设置 --time 选项, 输出当前时间
                System.out.println("Time: " + LocalTime.now(clock));
            }
        } else {
            // 未设置任何选项, 输出错误信息
            System.out.println("WARN: --date or --time option must have least one");
        }

        // 返回值作为进程返回值
        return 0;
    }
}
