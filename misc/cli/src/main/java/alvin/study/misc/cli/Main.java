package alvin.study.misc.cli;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * 命令行的入口类
 */
@Command(name = "cli", description = "Demo of java cli command")
public class Main implements Callable<Integer> {
    @Option(names = { "-p", "--plain" }, description = "If show text as plain text")
    private boolean plain = false;

    /**
     * Java 入口方法, 所有的命令均从此入口开始执行
     *
     * <p>
     * 本例中一共创建了三条命令
     *
     * <pre>
     * java -jar cli.jar                # 由 Main.call() 方法执行的命令
     * </pre>
     *
     * <pre>
     * java -jar cli.jar echo ...       # 由 Main.echo(...) 方法执行的命令
     * </pre>
     *
     * <pre>
     * java -jar cli.jar datetime ...   # 由 DatetimeCli.call() 方法执行的命令
     * </pre>
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建色彩配置
        var colorSchema = new ColorScheme.Builder().ansi(Ansi.ON)
                .commands(Style.bold, Style.underline)
                .options(Style.fg_yellow)
                .parameters(Style.fg_yellow)
                .optionParams(Style.italic)
                .errors(Style.fg_red, Style.bold)
                .stackTraces(Style.italic)
                .build();

        // 添加 Main 类为根命令 (会自动添加 echo 为子命令) 以及 DatetimeCli 为子命令
        var exitCode = new CommandLine(new Main())
                .setColorScheme(colorSchema)
                // 添加子命令对象
                .addSubcommand(new DatetimeCli())
                // 执行命令行命令
                .execute(args);

        // 以命令返回值作为进程返回值
        System.exit(exitCode);
    }

    /**
     * 根名令, 通过 {@code java -jar cli.jar} 直接执行, 无需额外命令名称
     *
     * @return 进程返回值
     */
    @Override
    public Integer call() {
        if (plain) {
            // 输出一段带格式的说明文本
            System.out.println("""

                This is PicoCli Demo

                    Picocli aims to be the easiest way to create rich command line applications that
                can run on and off the JVM.
                """);
        } else {
            // 输出一段说明文本
            System.out.println(Ansi.ON.string("""

                @|blue,underline This is PicoCli Demo|@

                @|green    Picocli aims to be the easiest way to create rich command line applications that
                can run on and off the JVM.|@
                """));
        }
        return 0;
    }

    /**
     * 定义 {@code echo} 子命令
     *
     * <p>
     * 该命令使用函数方式进行定义, 当实例化 {@link CommandLine} 对象时, 会自动将该方法表达的命令作为子命令加入, 参考:
     * {@link #main(String[])} 方法中对 {@link CommandLine} 对象的实例化
     * </p>
     *
     * <p>
     * 注册命令时, 可以加入此命令所在的类, 例如: {@code new CommandLine(new Main())}; 也可以加入特定的函数对象:
     * {@code new CommandLine(CommandLine.getCommandMethods(Main.class, "echo").get(0))}
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
     * {@link Option @Option} 注解表示一个命令选项, 属性包括:
     * <ul>
     * <li>
     * {@code names} 属性表示选项的名称, 一般定义 {@code -<short name>} 和 {@code --<full name>}
     * 两个名称
     * </li>
     * <li>
     * {@code description} 属性表示选项的描述
     * </li>
     * </ul>
     * 其余属性参考 {@link Option @Option} 注解
     * </p>
     *
     * <p>
     * {@link Parameters @Parameters} 注解用来表示命令参数, 对于本例来说, 命令参数类似:
     *
     * <pre>
     * java -jar cli.jar echo -cRed Hello World
     * </pre>
     * <p>
     * 其中, {@code Hello World} 即 {@code echo} 命令的参数, 由于中间有空格, 所以转递到程序中是一个
     * {@code String[]} 类型, 即 {@code ["Hello", "World"]}
     * </p>
     *
     * <p>
     * 当前类实现了 {@link Callable} 接口并重写了 {@link Callable#call()} 方法, 是为了当前类本身作为根名令,
     * 具体命令执行参考 {@link #call()} 方法
     * </p>
     *
     * @param err       {@code true} 表示通过 {@code System.err} 流输出文本, {@code false}
     *                  表示通过 {@code System.out} 流输出文本
     * @param color     要输出的文本颜色
     * @param bold      是否对输出文本进行加粗
     * @param underline 是否对输出文本加下划线
     * @param text      要输出的文本字符串, 如果该参数
     */
    @Command(name = "echo", mixinStandardHelpOptions = true, version = "echo 1.0", description = "Output string")
    void echo(@Option(names = { "-e", "--err" }, description = "If output text into System.err stream") boolean err,
            @Option(names = { "-c", "--color" }, description = "Set the font color") String color,
            @Option(names = { "-b", "--bold" }, description = "Set the font bolder") boolean bold,
            @Option(names = { "-u", "--underline" }, description = "Set font with underline") boolean underline,
            @Parameters String[] text) {
        // 按照 color, bold, underline 的顺序组成格式参数
        var formatter = new ArrayList<String>();
        if (bold) {
            formatter.add("bold");
        }
        if (color != null) {
            formatter.add(color);
        }
        if (underline) {
            formatter.add("underline");
        }

        // 将多个参数字符串合并为一个字符串
        var outputText = String.join(" ", text);

        // 为输出添加格式说明
        if (!formatter.isEmpty()) {
            // 产生类似 @|<bold>,<red>,<underline> <text>|@
            outputText = Ansi.ON.string(String.format("@|%s %s|@", String.join(",", formatter), outputText));
        }

        // 判断通过那个输出流输出, 并输出字符串
        if (err) {
            System.err.println(outputText);
        } else {
            System.out.println(outputText);
        }
    }
}
