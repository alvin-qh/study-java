package alvin.study.testing.junit.parameterized;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

/**
 * 为
 * {@code ParameterizeTest.shouldReturnTrueForNullOrBlankStringsByClassMethod(String)}
 * 测试方法提供测试参数的类型
 */
public class StringParams {
    /**
     * 为
     * {@code ParameterizeTest.shouldReturnTrueForNullOrBlankStringsByClassMethod(String)}
     * 方法提供测试参数的方法
     *
     * <p>
     * 当测试方法只有一个参数时, 返回类型可以是具体的参数类型的流对象,
     * 可以不是 {@link Arguments} 对象, 例如本例中,
     * {@code ParameterizeTest.shouldReturnTrueForNullOrBlankStringsByClassMethod(String)}
     * 方法只有一个 {@link String} 类型参数
     * </p>
     *
     * @return 包含一组 {@link String} 对象的流对象, 每个 {@link String} 对象作为传递给测试方法的参数
     */
    public static Stream<String> blankStrings() {
        return Stream.of(null, "");
    }
}
