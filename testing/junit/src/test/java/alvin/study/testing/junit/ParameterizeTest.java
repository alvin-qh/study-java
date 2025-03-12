package alvin.study.testing.junit;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import alvin.study.testing.junit.parameterized.BlankStringsArgumentsProvider;
import alvin.study.testing.junit.parameterized.SlashyDateConverter;
import alvin.study.testing.junit.parameterized.UserAggregator;
import alvin.study.testing.junit.parameterized.VariableSource;
import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.NumberService;

/**
 * 参数化测试
 *
 * <p>
 * 为了对一个方法进行全面的测试, 往往需要对其各种参数情况进行测试, 为了减少工作量,
 * 可以用"参数化测试"对一组参数进行重复测试
 * </p>
 *
 * <p>
 * 参数化测试通过注解 {@link ParameterizedTest @ParameterizedTest} 来表示,
 * 此时需要通过 {@link ValueSource @ValueSource} 等注解为测试指定一组参数,
 * 这些参数将通过测试方法的对应参数传递; {@link ValueSource @ValueSource}
 * 注解具备一系列属性, 用于指定传递给测试的参数值
 *
 * <ul>
 * <li>{@code short} 属性, 指定一组 {@code short} 类型值</li>
 * <li>{@code byte} 属性, 指定一组 {@code byte} 类型值</li>
 * <li>{@code int} 属性, 指定一组 {@code int} 类型值</li>
 * <li>{@code long} 属性, 指定一组 {@code long} 类型值</li>
 * <li>{@code float} 属性, 指定一组 {@code float} 类型值</li>
 * <li>{@code double} 属性, 指定一组 {@code double} 类型值</li>
 * <li>{@code char} 属性, 指定一组 {@code char} 类型值</li>
 * <li>{@link String} 属性, 指定一组 {@link String} 类型值</li>
 * <li>{@link Class} 属性, 指定一组 {@link Class} 类型值</li>
 * </ul>
 * </p>
 */
class ParameterizeTest {
    /**
     * 定义一个成员字段, 作为假设值, 通过 {@link VariableSource @VariableSource}
     * 注解进行使用
     */
    @SuppressWarnings("unused")
    private static final Stream<Arguments> variableSourceArguments = Stream.of(
        Arguments.of(null, true),
        Arguments.of("", true),
        Arguments.of("not blank", false));

    /**
     * 为 {@link #methodSource_shouldReturnTrueForNullOrBlankStringsByMethod(String, boolean)}
     * 测试方法提供测试参数的方法
     *
     * @return 包含一组 {@link Arguments} 对象的流对象, 每个 {@link Arguments}
     *         对象作为一组传递给测试方法的参数
     */
    private static Stream<? extends Arguments> methodSource_shouldReturnTrueForNullOrBlankStringsByMethod() {
        return Stream.of(
            // 每个 Arguments 对象均包含两个参数值
            Arguments.of(null, true),
            Arguments.of("", true),
            Arguments.of("not blank", false));
    }

    /**
     * 该方法必须和 {@link #methodSource_shouldDisplayName(char, int)} 具备相同名称
     *
     * @return 传递给 {@link #methodSource_shouldDisplayName(char, int)} 方法的参数值
     */
    private static Stream<Arguments> methodSource_shouldDisplayName() {
        return Stream.of(
            Arguments.of('A', 65),
            Arguments.of('B', 66),
            Arguments.of('C', 67),
            Arguments.of('D', 68));
    }

    /**
     * 对 {@link NumberService#isOdd(int)} 方法进行测试
     *
     * <p>
     * 通过 {@link ValueSource @ValueSource} 注解的 {@code ints} 属性指定一系列假设值,
     * 这些值会通过 {@code input} 参数依次传递给测试方法, 以此验证测试方法是否针对所有假设的参数均可成立
     * </p>
     *
     * <p>
     * 测试报告中也会根据每个假设值产生一个报告结果
     * </p>
     *
     * @param input 假设的一组整数测试值
     */
    @ValueSource(ints = { 1, 3, 5, -3, 15, Integer.MAX_VALUE })
    @ParameterizedTest
    void valueSource_shouldReturnTrueForOddNumbers(int input) {
        then(NumberService.isOdd(input)).isTrue();
    }

    /**
     * 对 {@link NumberService#isOdd(int)} 方法进行测试
     *
     * <p>
     * 除过 {@link ValueSource @ValueSource} 注解的 {@code strings}
     * 属性指定一系列假设字符串值外, {@link NullAndEmptySource @NullAndEmptySource}
     * 注解提供了 {@code null} 和 {@code ""} 空字符串值 (相当于
     * {@link org.junit.jupiter.params.provider.NullSource @NullSource} 和
     * {@link org.junit.jupiter.params.provider.EmptySource @EmptySource}
     * 两个注解的组合)
     * </p>
     *
     * @param input 假设的一组字符串测试值
     */
    @ValueSource(strings = { "" })
    @NullAndEmptySource
    @ParameterizedTest
    void valueSource_shouldReturnTrueForNullOrBlankStrings(String input) {
        then(input).isNullOrEmpty();
    }

    /**
     * 对 {@link Month#length(boolean)} 方法进行测试
     *
     * <p>
     * 除过 {@link EnumSource @EnumSource} 注解的 {@code value} 属性指定一个枚举类型,
     * 通过 {@code names} 属性指定要包含或排除的枚举名称, 通过 {@code mode} 属性指定对于
     * {@code names} 属性进行操作的模式; 每个枚举值会通过 {@code input} 参数传递给测试方法
     * </p>
     *
     * @param month 假设的一组枚举测试值
     */
    @EnumSource(value = Month.class,
                names = {
                    "APRIL",
                    "JUNE",
                    "SEPTEMBER",
                    "NOVEMBER"
                },
                mode = Mode.INCLUDE)
    @ParameterizedTest
    void enumSource_shouldMonthsWith30DaysLong(Month month) {
        // 确认假设的月份天数均为 30 天 (非闰年情况)
        then(month.length(false)).isEqualTo(30);
    }

    /**
     * 对 {@link Mode#MATCH_ANY} 模式产生的枚举假设值进行测试
     *
     * <p>
     * 当 {@link EnumSource @EnumSource} 注解的 {@code names} 属性包含正则表达式通配符时,
     * 可以指定 {@code mode} 属性为 {@link Mode#MATCH_ANY} 或 {@link Mode#MATCH_NONE},
     * 以产生匹配正则表达式的假设值
     * </p>
     *
     * <p>
     * 本例中产生的假设枚举值的名称需要以 {@code "BER"} 结尾
     * </p>
     *
     * @param month 假设的一组枚举测试值
     */
    @EnumSource(value = Month.class, names = ".+?BER", mode = Mode.MATCH_ANY)
    @ParameterizedTest
    void enumSource_shouldMonthNameEndingWithBer(Month month) {
        var months = EnumSet.of(
            Month.SEPTEMBER,
            Month.OCTOBER,
            Month.NOVEMBER,
            Month.DECEMBER);
        then(months).contains(month);
    }

    /**
     * 验证一组 csv 格式数据作为假设参数
     *
     * <p>
     * {@link CsvSource @CsvSource} 注解的 {@code value} 属性可以指定一组 csv 格式的数据集,
     * 每一项相当于 csv 中的一行数据, 通过 {@code delimiter} 属性指定的字符进行分隔
     * (可选, 默认为 {@code ','} 分割)
     * </p>
     *
     * <p>
     * csv 格式数据按行依次传递给测试方法, 其中第一列数据传递给第一个参数,
     * 第二列数据传递给第二个参数, 以此类推
     * </p>
     *
     * @param input    csv 中第一列数据
     * @param expected csv 中第二列数据
     */
    @CsvSource(value = {
        "test,TEST",
        "tEst,TEST",
        "Java,JAVA"
    }, delimiter = ',')
    @ParameterizedTest
    void csvSource_shouldGenerateTheExpectedUppercaseValue(String input, String expected) {
        var actual = input.toUpperCase();
        then(actual).isEqualTo(expected);
    }

    /**
     * 通过 csv 文件存储的内容作为假设参数进行测试
     *
     * <p>
     * {@link CsvFileSource @CsvFileSource} 注解的 {@code resources}
     * 属性指定 csv 文件的路径 (相对于当前项目), 通过 {@code delimiter}
     * 属性指定的字符进行分隔 (可选, 默认为 {@code ','} 分割), 通过
     * {@code numLinesToSkip} 属性的值指定要跳过的行数 (标题行)
     * </p>
     *
     * <p>
     * csv 文件数据按行依次传递给测试方法, 其中第一列数据传递给第一个参数,
     * 第二列数据传递给第二个参数, 以此类推
     * </p>
     *
     * @param input    csv 中第一列数据
     * @param expected csv 中第二列数据
     */
    @CsvFileSource(resources = "/data/test.csv",
                   delimiter = ',',
                   numLinesToSkip = 1)
    @ParameterizedTest
    void csvFileSource_shouldGenerateTheExpectedUppercaseValueCSVFile(String input, String expected) {
        var actual = input.toUpperCase();
        then(actual).isEqualTo(expected);
    }

    /**
     * 通过一个特定方法的返回值作为当前测试方法的假设参数
     *
     * <p>
     * {@link MethodSource @MethodSource} 注解默认情况下会在当前类下找和被注解的测试方法同名的方法,
     * 作为参数生成方法, 也可以通过 {@code value} 属性指定特定的方法名称
     * </p>
     *
     * <p>
     * 参数生成方法必须为 {@code static} 方法, 且返回值必须是
     * {@code Stream<? extends Arguments>} 类型, {@link Arguments} 对象标识一组参数
     * </p>
     *
     * @param input    {@link Arguments} 对象中存储的第一个参数
     * @param expected {@link Arguments} 对象中存储的第二个参数
     */
    @MethodSource
    @ParameterizedTest
    void methodSource_shouldReturnTrueForNullOrBlankStringsByMethod(String input, boolean expected) {
        var actual = Strings.isNullOrEmpty(input);
        then(actual).isEqualTo(expected);
    }

    /**
     * 如果参数生成器方法不在当前类中, 则可以通过 {@link MethodSource @MethodSource} 注解的 {@code value}
     * 属性指定方法的具体位置
     *
     * <p>
     * 参数生成方法必须为 {@code static} 方法, 且返回值必须是
     * {@code Stream<? extends Arguments>} 类型, {@link Arguments} 对象标识一组参数
     * </p>
     *
     * @param input 参数生成方法返回的参数值
     */
    @MethodSource("alvin.study.testing.junit.parameterized.StringParams#blankStrings")
    @ParameterizedTest
    void methodSource_shouldReturnTrueForNullOrBlankStringsByClassMethod(String input) {
        var actual = Strings.isNullOrEmpty(input);
        then(actual).isTrue();
    }

    /**
     * 自定义参数提供器
     *
     * <p>
     * {@link ArgumentsSource} 和 {@link MethodSource} 类似,
     * 只是前者是一个专用的参数生成接口类型, 且注解的 {@code value}
     * 属性是指定参数生成类型的 {@link Class} 对象, 灵活度更高一些
     * </p>
     *
     * @param input {@link BlankStringsArgumentsProvider#provideArguments(ExtensionContext)}
     *              方法返回的参数值
     */
    @ArgumentsSource(BlankStringsArgumentsProvider.class)
    @ParameterizedTest
    void argumentsSource_shouldReturnTrueForNullOrBlankStringsArgProvider(String input) {
        var actual = Strings.isNullOrEmpty(input);
        then(actual).isTrue();
    }

    /**
     * 通过一个注解指定一个保存假设参数列表的字段, 作为测试参数
     *
     * <p>
     * 该方法本质上仍是通过 {@link ArgumentsSource @ArgumentsSource}
     * 注解结合 {@link org.junit.jupiter.params.provider.ArgumentsProvider
     * ArgumentsProvider} 完成参数的定义和传递, 参考:
     * {@link #argumentsSource_shouldReturnTrueForNullOrBlankStringsArgProvider(String)} 方法
     * </p>
     *
     * <p>
     * 不同在于, 通过 {@link VariableSource @VariableSource}
     * 注解将测试方法和参数提供方联系在一起
     * </p>
     *
     * @param input    第一个假设参数
     * @param expected 第二个假设参数
     */
    @VariableSource("variableSourceArguments")
    @ParameterizedTest
    void variableSource_shouldReturnTrueForNullOrBlankStringsVariableSource(String input, boolean expected) {
        var actual = Strings.isNullOrEmpty(input);
        then(actual).isEqualTo(expected);
    }

    /**
     * 测试字符串假设值自动转化为指定类型的测试参数值
     *
     * <p>
     * JUnit 5 可以自动将字符串值转换为如下类型的值:
     *
     * <ul>
     * <li>{@code byte}, {@code short}, {@code int}, {@code long}, {@code float},
     * {@code double}, {@code char}, {@code boolean}</li>
     * <li>{@link java.util.UUID UUID}</li>
     * <li>{@link java.util.Locale Locale}</li>
     * <li>{@link java.time.LocalDate LocalDate}, {@link java.time.LocalTime
     * LocalTime} and {@link java.time.LocalDateTime LocalDateTime}</li>
     * <li>{@link java.time.Year}, {@link Month} and etc.</li>
     * <li>{@link java.io.File File} and {@link java.nio.file.Path Path}</li>
     * <li>{@link java.net.URL URL} and {@link java.net.URI URI}</li>
     * <li>{@link Enum} and subclasses</li>
     * </ul>
     * </p>
     *
     * @param input    csv 第一列数据, 表示月份
     * @param expected csv 第二列数据, 表示期待的月份天数
     */
    @CsvSource({
        "JANUARY,31",
        "OCTOBER,31",
        "APRIL,30",
        "JUNE,30",
        "SEPTEMBER,30",
        "NOVEMBER,30"
    })
    @ParameterizedTest
    void csvSource_shouldConvertStringToEnumAndNumber(Month input, int expected) {
        then(input.length(false)).isEqualTo(expected);
    }

    /**
     * 演示自定义的参数类型转换
     *
     * <p>
     * 对于特殊的字符串或者类型, 可以定义类型转换进行处理
     * </p>
     *
     * <p>
     * 本例中的斜杠分割日期格式通过 {@link SlashyDateConverter} 类型完成到
     * {@link LocalDate} 类型的转换, 通过 {@link ConvertWith @ConvertWith}
     * 注解来指定转换类型
     * </p>
     *
     * @param input    转换后的日期类型
     * @param expected 期待的年份值
     */
    @CsvSource({
        "2018/12/25,2018",
        "2019/02/11,2019"
    })
    @ParameterizedTest
    void csvSource_shouldConvertSlashyDateToLocalDate(
            @ConvertWith(SlashyDateConverter.class) LocalDate input,
            int expected) {
        then(input.getYear()).isEqualTo(expected);
    }

    /**
     * 通过 {@link ArgumentsAccessor} 参数获取所有假设参数
     *
     * <p>
     * 对于一组 csv 假设数据, 通过 {@link ArgumentsAccessor}
     * 参数类型可以完整的获取每一行测试参数, 这样对于列数比较多的 csv 数据,
     * 就不必定义过多的测试参数来获取, 具体获取方法如下:
     *
     * <ul>
     * <li>
     * 通过 {@link ArgumentsAccessor#get(int)} 可以获取第 n 列的字符串数据
     * </li>
     * <li>
     * 通过 {@code ArgumentsAccessor.getXXXX(int)} 可以获取第 n
     * 列的指定类型数据, {@code XXXX} 可以为 {@link String},
     * {@link Byte}, {@link Short}, {@link Integer}, {@link Long},
     * {@link Boolean} 等
     * </li>
     * <li>通过 {@link ArgumentsAccessor#get(int, Class)} 可以获取第 n
     * 列的指定类型数据</li>
     * </ul>
     * </p>
     *
     * @param accessor {@link ArgumentsAccessor} 类型参数
     */
    @CsvSource({
        "1,Alvin,1-Alvin",
        "2,Emma,2-Emma",
        "3,Arthur,3-Arthur",
        "4,Lily,4-Lily",
        "5,Jimmy,5-Jimmy"
    })
    @ParameterizedTest
    void csvSource_shouldGetParameterByArgumentsAccessor(ArgumentsAccessor accessor) {
        var id = accessor.getInteger(0);
        var name = accessor.getString(1);
        var expected = accessor.get(2, String.class);

        then(new User(id, name)).hasToString(expected);
    }

    /**
     * 通过 {@link ArgumentsAccessor} 参数获取所有假设参数
     *
     * <p>
     * 对于一组 csv 假设数据, 通过 {@link AggregateWith @AggregateWith}
     * 注解可以指定一个聚合器类型 {@link UserAggregator}, 通过该类型聚合 csv
     * 数据形成指定类型的参数
     * </p>
     *
     * <p>
     * 由于 {@link User} 参数使用了 csv 的前两列数据, 所以无法直接获取第三列数据,
     * 此时可通过 {@link ArgumentsAccessor} 类型参数来获取指定列的数据
     * </p>
     *
     * @param user     通过 {@link UserAggregator} 类型转换得到的参数,
     *                 使用了 csv 的前两列数据
     * @param accessor 通过 {@link ArgumentsAccessor} 类型获取第三列数据作为参数
     */
    @CsvSource({
        "1,Alvin,1-Alvin",
        "2,Emma,2-Emma",
        "3,Arthur,3-Arthur",
        "4,Lily,4-Lily",
        "5,Jimmy,5-Jimmy"
    })
    @ParameterizedTest
    void csvSource_shouldGetParameterByAggregator(
            @AggregateWith(UserAggregator.class) User user,
            ArgumentsAccessor accessor) {
        // 获取 csv 的第三列数据
        var expected = accessor.getString(2);
        then(user).hasToString(expected);
    }

    /**
     * 可以自定义测试的名称
     *
     * <p>
     * 通过 {@link ParameterizedTest @ParameterizedTest} 注解的
     * {@code name} 属性可以指定测试在测试报告中显式的名称, 其中的占位符可以为:
     *
     * <ul>
     * <li>
     * <code>{index}</code> 占位符, 将替换为调用索引, 简单地说,
     * 第一次执行的调用索引为 {@code 1}, 第二次执行的调用索引为 {@code 2},
     * 依此类推
     * </li>
     * <li>
     * <code>{arguments}</code> 被替换为完整的, 以逗号分割的参数列表
     * </li>
     * <li>
     * <code>{0}, {1}, ...</code> 是表示单个参数的占位符
     * </li>
     * </ul>
     * </p>
     *
     * @param input    {@code methodSource_shouldDisplayName}
     *                 方法返回值的第一个参数序列
     * @param expected {@code methodSource_shouldDisplayName}
     *                 方法返回值的第二个参数序列
     */
    @MethodSource
    @ParameterizedTest(name = "{index}: {0} test completed")
    void methodSource_shouldDisplayName(char input, int expected) {
        then((int) input).isEqualTo(expected);
    }
}
