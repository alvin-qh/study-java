package alvin.study.testing.junit.parameterized;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * 为
 * {@code ParameterizeTest.shouldReturnTrueForNullOrBlankStringsArgProvider(String)}
 * 测试方法提供参数的类型
 */
public class BlankStringsArgumentsProvider implements ArgumentsProvider {
    /**
     * 提供参数的方法, 参考
     * {@code ParameterizeTest.shouldReturnTrueForNullOrBlankStringsByMethod()} 方法
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of(""));
    }
}
