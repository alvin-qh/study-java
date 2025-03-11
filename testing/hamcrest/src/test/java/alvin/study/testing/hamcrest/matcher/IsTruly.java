package alvin.study.testing.hamcrest.matcher;

import static org.hamcrest.Matchers.not;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.google.common.base.Strings;

/**
 * 匹配一个值是否表示"真"的概念
 *
 * <p>
 * 所谓"真", 即: {@code true}, 包括非 {@code 0} 数字, 非空字符串,
 * 非 {@code null} 引用
 * </p>
 *
 * <p>
 * 匹配器类型应该继承自 {@link BaseMatcher} (而非实现 {@link Matcher}
 * 接口), 其中:
 *
 * <ul>
 * <li>
 * {@link BaseMatcher#matches(Object)} 方法执行匹配动作, 返回是否匹配成功
 * </li>
 * <li>
 * {@link BaseMatcher#describeTo(Description)} 方法用于在匹配失败后,
 * 输出当前匹配器的期望结果
 * </li>
 * <li>
 * {@link BaseMatcher#describeMismatch(Object, Description)}
 * 方法输出匹配完毕后的实际结果
 * </li>
 * </ul>
 * </p>
 *
 * @param <T> 指定该匹配器可接受的参数类型, 对于 {@link BaseMatcher}
 *            超类, 该参数只用于语法层面的类型约束, 实际对类型进行约束可参见
 *            {@link org.hamcrest.TypeSafeMatcher TypeSafeMatcher}
 */
public class IsTruly<T> extends BaseMatcher<T> {
    /**
     * 构造器, 禁用 {@code new} 操作符产生对象
     */
    protected IsTruly() {}

    /**
     * 构造一个 {@link Matcher} 对象, 用于匹配一个对象值是否表示
     * {@code true}
     *
     * @return {@link IsTruly} 类型对象
     */
    public static <T> Matcher<T> truly() {
        return new IsTruly<>();
    }

    /**
     * 构造一个 {@link Matcher} 对象, 用于匹配一个对象值是否表示
     * {@code false}
     *
     * @return {@link org.hamcrest.core.IsNot IsNot(IsTruly)}
     *         类型对象
     */
    public static <T> Matcher<T> falsely() {
        return not(truly());
    }

    /**
     * 进行匹配操作
     *
     * @param item 要匹配的目标参数
     * @return {@code true} 表示匹配成功, {@code false}
     *         表示匹配失败
     */
    @Override
    public boolean matches(Object item) {
        if (item == null) {
            // null 值表示 false, 匹配失败
            return false;
        }
        if (item instanceof Boolean it) {
            // Boolean.FALSE 表示 false, 匹配失败
            return Boolean.TRUE.equals(it);
        }
        if (item instanceof String it) {
            // 空字符串表示 false, 匹配失败
            return !Strings.isNullOrEmpty(it);
        }
        if (item instanceof Number it) {
            // 0 表示 false, 匹配失败
            return it.doubleValue() != 0.0;
        }
        // 其余情况表示 true, 匹配成功
        return true;
    }

    /**
     * 将当前匹配失败的信息拼入整体匹配失败信息流中
     *
     * <p>
     * 匹配失败时, 需要输出当前 {@link Matcher} 的期望信息,
     * 期望值为 {@code "<true>"} 常量值
     * </p>
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("<true>");
    }
}
