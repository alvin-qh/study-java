package alvin.study.testing.pioneer;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junitpioneer.jupiter.cartesian.ArgumentSets;
import org.junitpioneer.jupiter.cartesian.CartesianArgumentsSource;
import org.junitpioneer.jupiter.cartesian.CartesianMethodArgumentsProvider;
import org.junitpioneer.jupiter.cartesian.CartesianParameterArgumentsProvider;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.junitpioneer.jupiter.cartesian.CartesianTest.MethodFactory;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.JsonFileSource;
import org.junitpioneer.jupiter.json.JsonSource;
import org.junitpioneer.jupiter.json.Property;
import org.junitpioneer.jupiter.params.IntRangeSource;

import alvin.study.testing.testcase.model.Group;
import alvin.study.testing.testcase.model.User;

/**
 * 该注解用于测试参数, 指定该参数值的生成规则
 *
 * <p>
 * {@link CartesianArgumentsSource @CartesianArgumentsSource} 注解用于指定实际生成参数值的类, 本例中通过
 * {@link NumberSourceProvider} 类来实际产生参数值
 * </p>
 *
 * <p>
 * 参考 {@link ParameterizeTest#cartesian_shouldGenerateArgumentsByAnnotation(int)} 测试方法
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CartesianArgumentsSource(NumberSourceProvider.class)
@interface NumberSource {
    /**
     * 参数值集合, 每次从该集合中产生一个参数值
     *
     * @return 参数值集合
     */
    int[] value();
}

/**
 * 该注解用于测试方法, 指定该方法所有参数值的生成规则
 *
 * <p>
 * {@link CartesianArgumentsSource @CartesianArgumentsSource} 注解用于指定实际生成参数值的类, 本例中通过
 * {@link NumberArgumentSourceProvider} 类来实际产生参数值
 * </p>
 *
 * <p>
 * 为了简单起见, 本例只使用一种参数产生规则 {@link NumberArgumentSource#value()}, 实际使用时, 可以定义任意属性来产生所需的参数值
 * </p>
 *
 * <p>
 * 参考 {@link ParameterizeTest#cartesian_shouldGenerateAllArgumentsByAnnotation(int, int)} 测试方法
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CartesianArgumentsSource(NumberArgumentSourceProvider.class)
@interface NumberArgumentSource {
    /**
     * 参数值集合, 每次从该集合中产生一个参数值
     *
     * @return 参数值集合
     */
    int[] value();
}

/**
 * 为 {@link NumberSource @NumberSource} 注解实际产生参数值的类
 *
 * <p>
 * 该类型需要实现 {@link CartesianParameterArgumentsProvider} 接口, 其泛型参数指定了要生成参数的类型, 通过实现
 * {@link CartesianParameterArgumentsProvider#provideArguments(ExtensionContext, Parameter)} 方法来产生参数集合
 * </p>
 *
 * <p>
 * 参考 {@link ParameterizeTest#cartesian_shouldGenerateArgumentsByAnnotation(int)} 测试方法
 * </p>
 */
class NumberSourceProvider implements CartesianParameterArgumentsProvider<Integer> {
    /**
     * 为 {@link NumberSource @NumberSource} 注解实际产生参数值集合
     *
     * @param context   测试方法的上下文对象, 包括测试方法的各类信息
     * @param parameter 被 {@link NumberSource @NumberSource} 注解的参数对象
     * @return 生成的参数值集合的 {@link Stream} 对象
     */
    @Override
    public Stream<Integer> provideArguments(ExtensionContext context, Parameter parameter) {
        // 从参数对象上获取注解对象
        var anno = Objects.requireNonNull(
            parameter.getAnnotation(NumberSource.class));

        // 从注解中获取生成参数的规则, 并生成参数值集合
        // 注意, 集合元素不能为值类型, 这里使用了 boxed 方法将值类型转为引用类型
        return Arrays.stream(Objects.requireNonNull(anno.value())).boxed();
    }
}

/**
 * 为 {@link NumberArgumentSource @NumberArgumentSource} 注解的方法所有参数实际产生参数值的类
 *
 * <p>
 * 该类型需要实现 {@link CartesianMethodArgumentsProvider} 接口, 通过实现
 * {@link CartesianMethodArgumentsProvider#provideArguments(ExtensionContext)} 方法来为测试方法的所有参数产生参数值
 * </p>
 *
 * <p>
 * 为了简单起见, 本例为所有的参数都按同一种规则产生参数值 ({@link NumberArgumentSource#value()}), 实际使用时可以产生任何类型参数的组合
 * </p>
 *
 * <p>
 * 为了能获取到注解对象, 本例实现了 {@link AnnotationConsumer} 接口, 其 {@link AnnotationConsumer#accept(Object)} 方法将测试
 * 方法上的 {@link NumberArgumentSource @NumberArgumentSource} 注解对象作为参数传递
 * </p>
 *
 * <p>
 * 参考 {@link ParameterizeTest#cartesian_shouldGenerateArgumentsByAnnotation(int)} 测试方法
 * </p>
 */
class NumberArgumentSourceProvider implements CartesianMethodArgumentsProvider,
                                   AnnotationConsumer<NumberArgumentSource> {
    // NumberArgumentSource 注解的 value 属性值, 作为产生参数值的规则
    private int[] numbers;

    /**
     * 传递注解对象
     *
     * @param source 定义在测试方法上的 {@link NumberArgumentSource @NumberArgumentSource} 注解对象
     */
    @Override
    public void accept(NumberArgumentSource source) {
        // 保持注解 value 属性值
        this.numbers = Objects.requireNonNull(source.value());
    }

    /**
     * 为 {@link NumberArgumentSource @NumberArgumentSource} 注解的测试方法实际产生参数值集合
     *
     * @param context 测试方法的上下文对象, 包括测试方法的各类信息
     * @return 生成的参数值集合的 {@link ArgumentSets} 对象, 包括测试方法的所有参数值
     */
    @Override
    public ArgumentSets provideArguments(ExtensionContext context) {
        // 创建保持参数集合
        var argSets = ArgumentSets.create();

        // 根据目标测试方法的参数数量, 生成对应的测试参数值
        // 根据 numbers 的设置, 为每个参数生成对应的一组参数值
        for (var i = 0; i < context.getRequiredTestMethod().getParameterCount(); i++) {
            argSets.argumentsForNextParameter(Arrays.stream(numbers).boxed());
        }
        return argSets;
    }

}

/**
 * 测试 pioneer 库的参数化功能
 */
class ParameterizeTest {
    /**
     * 为 {@link #cartesian_shouldGenerateArgumentsByFactoryMethod(int, String, TestEnum)} 测试方法提供测试参数
     *
     * <p>
     * 注意, 通过 {@link IntStream#range(int, int)} 得到的结果无法作为参数传递, 需要通过 {@link IntStream#boxed()} 方法转为
     * {@code Stream<Integer>} 类型
     * </p>
     *
     * <p>
     * 本方法需要声明为 {@code static} 静态方法, 如果要设置为非静态, 则测试类上必须标记
     * {@link org.junit.jupiter.api.TestInstance @TestInstance} 注解, 且
     * {@code value} 属性必须为 {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS Lifecycle.PER_CLASS}, 即:
     * {@code @TestInstance(Lifecycle#PER_CLASS)}
     * </p>
     *
     * <p>
     * 关于 {@code PER_CLASS} 生命周期的说明, 请参见 {@code LifecycleTest} 范例
     * </p>
     *
     * @return {@link ArgumentSets} 参数集合对象
     */
    static ArgumentSets argumentsGenerator() {
        return ArgumentSets
                .argumentsForFirstParameter(IntStream.range(1, 3).boxed())
                .argumentsForNextParameter("A", "B")
                .argumentsForNextParameter(TestEnum.values());
    }

    /**
     * 在所有测试执行前, 创建一个文件并写入 JSON 内容
     *
     * <p>
     * 本方法为 {@link #json_shouldDeserializedObjectFromJsonInFile(User)} 方法提供前置文件
     * </p>
     */
    @BeforeAll
    static void createJsonFile() throws Exception {
        var file = new File("user.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        try (var out = new FileOutputStream(file, false)) {
            out.write("{\"id\": 1001, \"name\": \"Alvin\"}".getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 在所有测试执行完毕后, 删除之前创建的 JSON 文件
     *
     * <p>
     * 本方法为 {@link #json_shouldDeserializedObjectFromJsonInFile(User)} 清理创建的文件
     * </p>
     */
    @AfterAll
    static void deleteJsonFile() {
        var file = new File("user.json");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 以笛卡尔积将设定的参数进行组合传递
     *
     * <p>
     * 通过 {@link Values @Values} 注解设定的两个参数 {@code x} 和 {@code y} 会将以 {@code (x=1, y=3)}, {@code (x=1, y=4)},
     * {@code (x=2, y=3)} 以及 {@code (x=2, y=4)} 的组合进行参数传递, 共执行 4 此测试
     * </p>
     *
     * <p>
     * {@link CartesianTest @CartesianTest} 注解的 {@code name} 属性可以指定本测试每次执行的名称, 便于在日志中进行观察, 可以使用的
     * 占位符包括 <code>{index}</code>, 表示测试的索引编号; <code>{0}, {1}, ..., {n}</code>, 表示传入的第几个参数
     * </p>
     *
     * @param x 第一组参数
     * @param y 第二组参数
     */
    @CartesianTest(name = "{index} => first arg: {0}, second arg: {1}")
    void cartesian_shouldInjectCartesianArguments(
            @Values(ints = { 1, 2 }) int x,
            @Values(ints = { 3, 4 }) int y) {
        var expected = new Object[] {
            new int[] { 1, 3 },
            new int[] { 1, 4 },
            new int[] { 2, 3 },
            new int[] { 2, 4 }
        };

        then(new int[] { x, y }).isIn(expected);
    }

    /**
     * 设置参数的取值范围
     *
     * <p>
     * {@link IntRangeSource @IntRangeSource} 注解用于设置一系列在指定范围内的参数, 类似功能的注解还包括
     * {@link org.junitpioneer.jupiter.params.ByteRangeSource @ByteRangeSource},
     * {@link org.junitpioneer.jupiter.params.ShortRangeSource @ShortRangeSource},
     * {@link org.junitpioneer.jupiter.params.LongRangeSource @LongRangeSource},
     * {@link org.junitpioneer.jupiter.params.FloatRangeSource @FloatRangeSource} 以及
     * {@link org.junitpioneer.jupiter.params.DoubleRangeSource @DoubleRangeSource} 注解
     * </p>
     *
     * <p>
     * 另外, 可以通过
     * {@link org.junitpioneer.jupiter.params.IntRangeSource.IntRangeSources @IntRangeSources},
     * {@link org.junitpioneer.jupiter.params.ByteRangeSource.ByteRangeSources @ByteRangeSources},
     * {@link org.junitpioneer.jupiter.params.ShortRangeSource.ShortRangeSources @ShortRangeSources},
     * {@link org.junitpioneer.jupiter.params.LongRangeSource.LongRangeSources @LongRangeSources},
     * {@link org.junitpioneer.jupiter.params.FloatRangeSource.FloatRangeSources @FloatRangeSources} 以及
     * {@link org.junitpioneer.jupiter.params.DoubleRangeSource.DoubleRangeSources @DoubleRangeSources} 注解组合
     * 多个范围设置, 进行批量设置
     * </p>
     *
     * <p>
     * {@link Enum @Enum} 注解的作用是遍历一个枚举的所有值, 其 {@code value} 属性表示要遍历的枚举的类型, 当枚举类型和参数类型一致时
     * 可以省略 (例如本例可以省略), 否则必须显式声明, 例如: {@code @Enum(ChronoUnit.class) TemporalUnit unit} 参数
     * </p>
     *
     * <p>
     * {@link Enum @Enum} 注解具备 {@code names} 属性, 默认情况表示要包含的枚举名称, 例如:
     * <code>@Enum(names = { "DAYS", "HOURS" }) ChronoUnit unit</code> 参数, 表示只包含 {@code DAYS} 和 {@code HOURS} 两项
     * </p>
     *
     * <p>
     * {@link Enum @Enum} 注解具备 {@code mode} 属性, 取值为 {@link Enum.Mode} 枚举, 表示 {@code names} 属性的含义, 默认为
     * {@link Enum.Mode#INCLUDE}, 表达如下:
     * <ul>
     * <li>
     * {@link Enum.Mode#INCLUDE}: 表示 {@code names} 属性指定的为要要包含的项
     * </li>
     * <li>
     * {@link Enum.Mode#EXCLUDE}: 表示 {@code names} 属性指定的为要要排除的项
     * </li>
     * <li>
     * {@link Enum.Mode#MATCH_ALL}: 表示 {@code names} 为一组正则表达式, 取匹配所有正则表达式的枚举项
     * </li>
     * <li>
     * {@link Enum.Mode#MATCH_ANY}: 表示 {@code names} 为一组正则表达式, 取匹配任一正则表达式的枚举项
     * </li>
     * </ul>
     * </p>
     *
     * @param x 第一组参数, 为 {@code [1..3)} 范围内的所有整数
     * @param s 第二组参数, 为 <code>{"A", "B"}</code> 范围内的所有字符串
     * @param e 第二组参数, 为 {@link TestEnum} 枚举包含的所有值
     */
    @CartesianTest
    void cartesian_shouldPassArgumentsInRange(
            @IntRangeSource(from = 1, to = 3, step = 1) int x,
            @Values(strings = { "A", "B" }) String s,
            @Enum(TestEnum.class) TestEnum e) {
        var expected = new Object[] {
            new Object[] { 1, "A", TestEnum.FIRST },
            new Object[] { 1, "A", TestEnum.SECOND },
            new Object[] { 1, "B", TestEnum.FIRST },
            new Object[] { 1, "B", TestEnum.SECOND },
            new Object[] { 2, "A", TestEnum.FIRST },
            new Object[] { 2, "A", TestEnum.SECOND },
            new Object[] { 2, "B", TestEnum.FIRST },
            new Object[] { 2, "B", TestEnum.SECOND }
        };

        then(new Object[] { x, s, e }).isIn(expected);
    }

    /**
     * 通过 {@link #argumentsGenerator()} 方法的返回值作为本测试的参数输入
     *
     * @param x 第一组参数, 为 {@code [1..3)} 范围内的所有整数
     * @param s 第二组参数, 为 <code>{"A", "B"}</code> 范围内的所有字符串
     * @param e 第二组参数, 为 {@link TestEnum} 枚举包含的所有值
     */
    @CartesianTest
    @MethodFactory("argumentsGenerator")
    void cartesian_shouldGenerateArgumentsByFactoryMethod(int x, String s, TestEnum e) {
        var expected = new Object[] {
            new Object[] { 1, "A", TestEnum.FIRST },
            new Object[] { 1, "A", TestEnum.SECOND },
            new Object[] { 1, "B", TestEnum.FIRST },
            new Object[] { 1, "B", TestEnum.SECOND },
            new Object[] { 2, "A", TestEnum.FIRST },
            new Object[] { 2, "A", TestEnum.SECOND },
            new Object[] { 2, "B", TestEnum.FIRST },
            new Object[] { 2, "B", TestEnum.SECOND }
        };

        then(new Object[] { x, s, e }).isIn(expected);
    }

    /**
     * 通过 {@link NumberSource @NumberSource} 注解参数以产生测试参数值
     *
     * <p>
     * 测试参数的产生参考 {@link NumberSourceProvider#provideArguments(ExtensionContext, Parameter)} 方法
     * </P>
     *
     * @param x 测试参数值
     */
    @CartesianTest
    void cartesian_shouldGenerateArgumentsByAnnotation(
            @NumberSource({ 1, 2, 3 }) int x) {
        then(x).isIn(1, 2, 3);
    }

    /**
     * 通过 {@link NumberArgumentSource @NumberArgumentSource} 注解方法, 为测试方法的每个参数产生一组测试值
     *
     * <p>
     * 测试参数的产生参考 {@link NumberArgumentSourceProvider#provideArguments(ExtensionContext)} 方法
     * </P>
     *
     * <p>
     * 本例中为简单起见, 为每个参数生成值的规则是相同的, 所以两个参数都会产生 <code>{1, 2, 3}</code> 这三个值, 根据笛卡尔积的组合原则,
     * 会产生 {@code (1, 1)}, {@code (1, 2)}, {@code (1, 3)}, {@code (2, 1)}, {@code (2, 2)}, {@code (2, 3)},
     * {@code (3, 1)}, {@code (3, 2)} 和 {@code (3, 3)} 共 9 组参数值, 分别传递给 {@code x} 和 {@code y} 参数
     * </p>
     *
     * @param x 测试参数值
     * @param y 测试参数值
     */
    @CartesianTest
    @NumberArgumentSource({ 1, 2, 3 })
    void cartesian_shouldGenerateAllArgumentsByAnnotation(int x, int y) {
        var expected = new Object[] {
            new int[] { 1, 1 },
            new int[] { 1, 2 },
            new int[] { 1, 3 },
            new int[] { 2, 1 },
            new int[] { 2, 2 },
            new int[] { 2, 3 },
            new int[] { 3, 1 },
            new int[] { 3, 2 },
            new int[] { 3, 3 },
        };

        then(new int[] { x, y }).isIn(expected);
    }

    /**
     * 定义一个在指定范围内执行的测试
     *
     * <p>
     * 使用 {@link ParameterizedTest @ParameterizedTest} 注解配合方法上的 {@link IntRangeSource} 注解, 可以产生一个在指定范围
     * 内执行的测试
     * </p>
     *
     * <p>
     * 对应的属性为:
     * <ul>
     * <li>{@code from} 范围起始值</li>
     * <li>{@code to} 范围结束值</li>
     * <li>
     * {@code closed} 范围是否为闭区间, 默认为 {@code false}, 如果是闭区间, 则参数取值为 {@code from <= value <= to}, 否则为
     * {@code from <= value < to}
     * </li>
     * <li>{@code step} 步长值, 即参数在区间内每次增加的值, 默认为 {@code 1}
     * </ul>
     * </p>
     *
     * <p>
     * 类似的注解还包括:
     * <ul>
     * <li>{@link org.junitpioneer.jupiter.params.ByteRangeSource ByteRangeSource}</li>
     * <li>{@link org.junitpioneer.jupiter.params.ShortRangeSource ShortRangeSource}</li>
     * <li>{@link org.junitpioneer.jupiter.params.LongRangeSource LongRangeSource}</li>
     * <li>{@link org.junitpioneer.jupiter.params.FloatRangeSource FloatRangeSource}</li>
     * <li>{@link org.junitpioneer.jupiter.params.DoubleRangeSource DoubleRangeSource}</li>
     * </ul>
     * </p>
     *
     * <p>
     * 规定为: {@link IntRangeSource @IntRangeSource} 等范围定义注解只能包含一次, 且只能包含一个对应参数
     * </p>
     *
     * @param range 每次测试输入的测试纸
     */
    @IntRangeSource(from = 1, to = 3, closed = true, step = 1)
    @ParameterizedTest(name = "{index}: range = {0}")
    void range_shouldTestByRange(int range) {
        then(range).isIn(1, 2, 3);
    }

    /**
     * 测试从 Java 资源中读取 JSON 文件, 并反序列化为指定类型对象
     *
     * @param group 从 JSON 反序列化后得到的对象
     */
    @ParameterizedTest
    @JsonClasspathSource("json/group.json")
    void json_shouldDeserializedObjectFromJsonInResource(Group group) {
        // 确认 JSON 反序列化结果正确
        then(group).extracting("id", "name").isNotEqualTo(tuple(1001, "Students"));
        then(group.getUsers()).extracting("id", "name")
                .containsExactly(tuple(1001001, "Alvin"), tuple(1001002, "Emma"));
    }

    /**
     * 测试通过指定 JSON 字符串反序列化为指定类型对象
     *
     * @param user 从 JSON 反序列化后得到的对象
     */
    @ParameterizedTest
    @JsonSource("{\"id\": 1001, \"name\": \"Alvin\"}")
    void json_shouldDeserializedObjectFromJsonInString(User user) {
        // 确认 JSON 反序列化结果正确
        then(user).extracting("id", "name").isNotEqualTo(tuple(1001, "Alvin"));
    }

    /**
     * 测试通过指定 JSON 文件内容反序列化为指定类型对象
     *
     * <p>
     * 本例中通过 {@link #createJsonFile()} 方法临时创建了文件并写入 JSON 内容, 在所有测试结束后, 通过 {@link #deleteJsonFile()}
     * 方法删除该文件
     * </p>
     *
     * @param user 从 JSON 反序列化后得到的对象
     */
    @JsonFileSource("user.json")
    @ParameterizedTest
    void json_shouldDeserializedObjectFromJsonInFile(User user) {
        // 确认 JSON 反序列化结果正确
        then(user).extracting("id", "name").isNotEqualTo(tuple(1001, "Alvin"));
    }

    /**
     * 测试通过指定 JSON 字符串反序列化为指定类型对象
     *
     * <p>
     * 如果无法 (或不必) 将 JSON 内容反序列化为对象, 可以通过 {@link Property @Property} 注解指定获取 JSON 指定字段的参数
     * </p>
     *
     * @param id   JSON 的 {@code id} 字段值
     * @param name JSON 的 {@code name} 字段值
     */
    @JsonSource("{\"id\": 1001, \"name\": \"Alvin\"}")
    @ParameterizedTest
    void json_shouldDeserializedObjectFromJsonInStringAndGetCertainProperties(
            @Property("id") Integer id,
            @Property("name") String name) {
        // 确认 JSON 反序列化结果正确
        then(id).isEqualTo(1001);
        then(name).isEqualTo("Alvin");
    }

    /**
     * 测试处理 JSON 的指定属性内容
     *
     * <p>
     * {@link JsonSource @JsonSource}, {@link JsonClasspathSource @JsonClasspathSource} 以及
     * {@link JsonFileSource @JsonFileSource} 三个注解的 {@code data} 属性可以指定一个 JSON 字段名, 这样就只会将该字段的内容
     * 作为参数进行传递
     * </p>
     *
     * <p>
     * 注意, 如果只获取 JSON 的部分属性, 则无法将其反序列化为某个类型的对象, 必须通过 {@link Property @Property} 对应指定属性名进行取值,
     * 如果 {@code data} 属性指定的是一个数组, 则会通过多批次的测试来测试所有的属性值
     * </p>
     *
     * @param id   JSON 的 {@code id} 字段值
     * @param name JSON 的 {@code name} 字段值
     */
    @ParameterizedTest
    @JsonClasspathSource(value = "json/group.json", data = "users")
    void json_shouldDeserializedObjectFromJsonAndGetAPartOfIt(
            @Property("id") int id,
            @Property("name") String name) {
        // 确认 JSON 反序列化结果正确
        then(id).isIn(1001001, 1001002);
        then(name).isIn("Alvin", "Emma");
    }

    enum TestEnum {
        FIRST,
        SECOND
    }
}
