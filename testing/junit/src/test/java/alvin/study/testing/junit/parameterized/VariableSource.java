package alvin.study.testing.junit.parameterized;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个注解, 其注解在测试方法上, 相当于在测试方法上添加了 {@link ArgumentsSource @ArgumentsSource}
 * 注解和指定的参数提供器类型 {@link VariableArgumentsProvider}
 *
 * <p>
 * 在 {@link VariableArgumentsProvider} 中, 再通过 {@link #value()}
 * 属性的值获取保存假设参数列表的字段值
 * </p>
 *
 * <p>
 * 注意, {@link #value()} 参数指定的字段必须为 {@code static}, 这一点可以参考
 * {@code VariableArgumentsProvider.getValue(Field) 方法中对字段取值的方式
 * </p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(VariableArgumentsProvider.class)
public @interface VariableSource {
    /**
     * 测试类中保存假设值的静态字段名
     */
    String value();
}
