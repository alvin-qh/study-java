package alvin.study.testing.junit.parameterized;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * 测试假设参数提供器
 *
 * <p>
 * 该提供器由 {@link VariableSource @VariableSource} 注解指定, 参考
 * {@link org.junit.jupiter.params.provider.ArgumentsSource @ArgumentsSource} 注解
 * </p>
 *
 * <p>
 * {@link ArgumentsProvider} 接口说明该类型提供了假设参数
 * ({@link ArgumentsProvider#provideArguments(ExtensionContext)} 方法)
 * </p>
 *
 * <p>
 * {@link AnnotationConsumer} 接口说明了该类型需要从指定注解中获取信息, 来提供假设参数
 * </p>
 */
public class VariableArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<VariableSource> {
    // 保存假设参数的字段名
    private String variableName;

    @Override
    public void accept(VariableSource variableSource) {
        // 从所给的相关注解对象中获取 value 属性, 即保存假设参数的字段名
        variableName = variableSource.value();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        // 获取正在执行测试的实例对象, 本例中因获取的是静态字段, 所以无需此对象
        // var testInstance = context.getTestInstance().get();

        // 从上下文中获取当前执行的测试类型
        return context
            .getTestClass()
            // 转换为指定的字段对象
            .map(this::getField)
            // 从字段中获取字段值
            .map(this::getValue)
            // 为获取到结果, 抛出异常
            .orElseThrow(() -> new IllegalArgumentException("Failed to load test arguments"));
    }

    /**
     * 获取所给测试类型的指定字段
     *
     * @param clazz 所给测试类型
     * @return 根据注解的 {@code value} 属性值表示的字段名获取字段对象
     */
    private Field getField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField(variableName);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 获取所给字段的字段值
     *
     * <p>
     * 字段参考
     * {@code ParameterizedTest.variableSourceArguments}, 其类型为
     * {@code Stream<Arguments>} 类型
     * </p>
     *
     * @param field 所給測試字段
     * @return 測試參數序列
     */
    @SuppressWarnings("unchecked")
    private Stream<Arguments> getValue(Field field) {
        // 获取字段是否能直接访问, 对于 private 字段返回 false
        var accessible = field.canAccess(null);

        try {
            if (!accessible) {
                // 对无法直接访问的字段开放访问权限
                field.setAccessible(true);
            }
            // 从字段获取字段值, 因为是静态字段, 所以无需类实例对象
            return (Stream<Arguments>) field.get(null);
        } catch (Exception ignored) {
        } finally {
            if (!accessible) {
                // 关闭无法直接访问字段的访问权限
                field.setAccessible(false);
            }
        }

        return null;
    }
}
