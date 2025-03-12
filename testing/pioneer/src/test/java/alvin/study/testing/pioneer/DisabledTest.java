package alvin.study.testing.pioneer;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

import org.junitpioneer.jupiter.DisableIfTestFails;
import org.junitpioneer.jupiter.DisabledUntil;
import org.junitpioneer.jupiter.params.DisableIfAllArguments;
import org.junitpioneer.jupiter.params.DisableIfAnyArgument;
import org.junitpioneer.jupiter.params.DisableIfArgument;
import org.junitpioneer.jupiter.params.DisableIfArgument.DisableIfArguments;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 测试通过 Pioneer 库对测试方法进行临时禁用
 *
 * <p>
 * {@link DisabledUntil @DisabledUntil} 注解可以在指定日期前禁用测试
 * </p>
 *
 * <p>
 * {@link DisableIfTestFails @DisableIfTestFails} 可以在某个前置测试失败后,
 * 禁止执行后续的测试
 * <ul>
 * <li>
 * 一般而言, 如果一系列测试通过 {@link Order @Order} 注解指定了执行顺序,
 * 则前置的测试会作为后续测试正确的条件,如果前置测试失败, 则后续的测试也无必要执行,
 * 此时可通过 {@link DisableIfTestFails @DisableIfTestFails} 达成此目标
 * </li>
 * <li>
 * {@link DisableIfTestFails @DisableIfTestFails} 注解的 {@code with}
 * 属性可以指定一个异常类型, 表示当前置测试因为此异常失败, 则该注解生效
 * </li>
 * <li>
 * {@link DisableIfTestFails @DisableIfTestFails} 注解的
 * {@code onAssertion} 属性可以指定前置测试是否是因为断言导致的失败, 默认为
 * {@code true}, 如果设置为 {@code false}, 则断言失败并不会导致后续测试禁用
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link DisableIfDisplayName @DisableIfDisplayName}
 * 注解将根据测试的显示名称进行匹配, 如果满足条件, 则该测试被禁用
 * <ul>
 * <li>
 * 该注解通过 {@code contains} 和 {@code matches} 两个属性来匹配测试名称,
 * 前者匹配名称中包含的内容, 后者通过一个正则表达式匹配名称
 * </li>
 * <li>
 * 通常情况下, 该注解配合参数化注解 {@link ParameterizedTest @ParameterizedTest}
 * 一同使用, 即在动态生成测试显示名称时, 过滤掉其中的一部分测试执行
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link DisableIfArgument @DisableIfArgument},
 * {@link DisableIfAnyArgument}, {@link DisableIfAllArguments} 等注解用于
 * 参数化测试中, 可以在参数值为特定情况下, 禁用此次测试
 * <ul>
 * <li>
 * 某些时候, 特定的参数会导致异常, 这类参数应在专门的异常测试环节进行,
 * 而正常测试环境可以过滤掉这类参数. 如果测试集数据量较大,
 * 可以通过注解在测试执行环节进行过滤
 * </li>
 * <li>
 * 这一系列注解过滤主要通过注解的两个属性: {@code contains} 和 {@code matches}
 * 来设定, 前者匹配参数包含的内容, 后者根据一个正则表达式对参数进行匹配
 * </li>
 * <li>
 * {@link DisableIfArgument @DisableIfArgument} 注解用于对指定参数进行匹配,
 * 通过其 {@code name} 或 {@code index} 属性指定具体
 * 的参数
 * </li>
 * <li>
 * {@link DisableIfAnyArgument} 和 {@link DisableIfAllArguments}
 * 两个注解用于对所有参数进行匹配, 前者表示任意参数满足过滤条件, 后者
 * 表示所有参数满足过滤条件
 * </li>
 * </ul>
 * </p>
 */
@DisableIfTestFails
@TestMethodOrder(OrderAnnotation.class)
class DisabledTest {
    /**
     * 通过 {@link DisabledUntil} 可以在指定日期前禁用测试方法
     */
    @Test
    @DisabledUntil(date = "2090-12-31", reason = "Disable test before I die")
    void disable_shouldDisableTestBeforeTheSpecifiedDate() {
        fail();
    }

    /**
     * 本例中, 该测试作为 {@link #disable_shouldSubsequentTestDisabled()}
     * 测试的前置测试, 应当失败
     *
     * <p>
     * 演示时需去掉 {@link Disabled @Disabled} 注解
     * </p>
     */
    @Test
    @Order(1)
    @Disabled("Just a demo")
    void disable_thePrepositiveTestMustFailed() {
        fail();
    }

    /**
     * 由于 {@link #disable_thePrepositiveTestMustFailed()} 前置测试失败,
     * 所以该测试不会被执行
     *
     * <p>
     * 演示时需去掉 {@link Disabled @Disabled} 注解
     * </p>
     */
    @Test
    @Order(2)
    @Disabled("Just a demo")
    void disable_shouldSubsequentTestDisabled() {
        fail();
    }

    /**
     * 测试通过匹配测试显示名称禁用部分测试
     *
     * <p>
     * 本例中, 通过 {@link ValueSource @ValueSource} 注解来向
     * {@code x} 参数注入一系列值, 并通过 {@code x} 参数值生成测试显示名称,
     * 通过 {@link DisableIfDisplayName @DisableIfDisplayName}
     * 注解对测试名称匹配正则表达式的情况进行过滤
     * </p>
     *
     * @param x 通过 {@link ValueSource} 注解注入的一系列参数
     */
    @ValueSource(ints = { 1, 2, 3, 4, 5 })
    @ParameterizedTest(name = "Enabled test with x={0}")
    @DisableIfDisplayName(matches = ".*?x=(1|3|5).*")
    void disabled_shouldDisableTestByDisplayName(int x) {
        // 确认只有特定参数值的测试可以被执行
        then(x).isIn(2, 4);
    }

    /**
     * 测试对指定参数进行过滤, 禁用符合条件的测试
     *
     * <p>
     * 本例中, 通过 {@link CsvSource @CsvSource} 注解来向 {@code x}
     * 参数注入一系列值, 并通过 {@link DisableIfArgument @DisableIfArgument}
     * 注解对 {@code x} 参数值包含特定内容的情况进行过滤
     * </p>
     *
     * <p>
     * 本例中使用 {@link DisableIfArgument @DisableIfArgument}
     * 注解的 {@code name} 属性来指定要匹配的参数, 即按名称匹配参数,
     * 对于 JDK 14 以上的 Java 版本, 需要在编译时打开 {@code -parameters},
     * 参考: {@code build.gradle} 文件以及 {@code pom.xml}
     * 文件
     * </p>
     *
     * @param x 通过 {@link CsvSource} 注解注入的一系列参数
     */
    @CsvSource({ "a", "b", "c" })
    @ParameterizedTest
    @DisableIfArgument(name = "x", contains = { "a", "c" })
    void disabled_shouldDisableForSpecificArgument(String x) {
        // 确认只有特定参数的测试可以被执行
        then(x).isEqualTo("b");
    }

    /**
     * 测试对指定参数进行过滤, 禁用符合条件的测试
     *
     * <p>
     * 本例中, 通过 {@link CsvSource @CsvSource} 注解来向 {@code x}
     * 参数注入一系列值, 并通过 {@link DisableIfArgument @DisableIfArgument}
     * 注解对 {@code x} 参数值包含特定内容的情况进行过滤
     * </p>
     *
     * <p>
     * 本例中使用 {@link DisableIfArgument @DisableIfArgument} 注解的
     * {@code index} 属性来指定要匹配的参数, 即按索引匹配参数
     * </p>
     *
     * @param x 通过 {@link CsvSource} 注解注入的一系列参数
     */
    @CsvSource({ "a", "b", "c" })
    @ParameterizedTest
    @DisableIfArgument(index = 0, contains = { "a", "c" })
    void disabled_shouldDisableForSpecificArgumentByIndex(String x) {
        // 确认只有特定参数的测试可以被执行
        then(x).isEqualTo("b");
    }

    /**
     * {@link DisableIfArguments @DisableIfArguments} 注解用来组织一组
     * {@link DisableIfArgument @DisableIfArgument} 注解,
     * 对多个参数进行过滤
     *
     * @param x 通过 {@link CsvSource} 注解注入的一系列参数
     */
    @CsvSource({ "a", "b", "c" })
    @ParameterizedTest
    @DisableIfArguments({
        @DisableIfArgument(name = "x", contains = "a"),
        @DisableIfArgument(index = 0, contains = "c")
    })
    void disabled_shouldDisableForSpecificArguments(String x) {
        // 确认只有特定参数的测试可以被执行
        then(x).isEqualTo("b");
    }

    /**
     * 当任意参数符合过滤条件时, 禁用符合条件的测试
     *
     * <p>
     * 本例中测试方法有两个参数 {@code x} 和 {@code n},
     * 这两个参数中任意参数的值符合指定条件, 当前测试被禁用
     * </p>
     *
     * @param x 通过 {@link CsvSource} 注解注入的一系列参数
     * @param n 通过 {@link CsvSource} 注解注入的一系列参数
     */
    @CsvSource({ "a,1", "b,2", "c,3" })
    @ParameterizedTest
    @DisableIfAnyArgument(contains = { "a", "3" })
    void disabled_shouldDisableForAnyArgumentsByContains(String x, int n) {
        // 确认只有特定参数的测试可以被执行
        then(tuple(x, n)).isEqualTo(tuple("b", 2));
    }

    /**
     * 当所有参数均符合过滤条件时, 禁用符合条件的测试
     *
     * <p>
     * 本例中测试方法有两个参数 {@code x} 和 {@code n},
     * 这两个参数的值都符合指定条件时, 当前测试被禁用
     * </p>
     *
     * @param x 通过 {@link CsvSource} 注解注入的一系列参数
     * @param n 通过 {@link CsvSource} 注解注入的一系列参数
     */
    @CsvSource({ "a,1", "b,2", "c,3" })
    @ParameterizedTest
    @DisableIfAllArguments(matches = "^[a-z]|\\d$")
    void disabled_shouldDisableForAllArgumentsByContains(String x, int n) {
        // 确认只有特定参数的测试可以被执行
        then(tuple(x, n)).isEqualTo(tuple("b", 2));
    }
}
