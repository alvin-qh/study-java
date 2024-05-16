package alvin.study.testing.hamcrest.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.function.Function;

/**
 * 对一个对象执行回调方法, 并对回调结果进行匹配
 *
 * <p>
 * 这个类主要用来和其它匹配器配合使用, 达到链式调用的目的, 例如:
 *
 * <pre>
 * assertThat(obj, allOf(
 *    objectOf(o -> o.getValue(), is(equalTo("test"))),
 *    ...
 * ));
 * </pre>
 * </p>
 * <p>
 * 当前类型的超类 {@link TypeSafeDiagnosingMatcher} 表示一个"类型安全"并且"包含下一级
 * {@link Matcher}"的类型, 相当于结合了 {@link org.hamcrest.TypeSafeMatcher
 * TypeSafeMatcher} (类型安全 Matcher) 以及 {@link org.hamcrest.DiagnosingMatcher
 * Matcher} (具备下一级 Matcher) 这两个超类
 *
 * @param <T> 原始对象类型, 通过 {@code mapper} 参数转化为 {@code <R>} 类型对象
 * @param <R> 要匹配的对象类型
 */
public class ObjectOf<T, R> extends TypeSafeDiagnosingMatcher<T> {
    private final Function<? super T, ? extends R> mapper;
    private final Matcher<? extends R> matcher;

    /**
     * 构造器, 传入 {@link Function} 用于对象转换, 传入 {@link Matcher} 对象对转换后的对象进行匹配
     *
     * @param mapper  {@link Function} 对象, 用于对象转换
     * @param matcher {@link Matcher} 对象, 用于对转换后的对象进行匹配
     */
    protected ObjectOf(Function<? super T, ? extends R> mapper, Matcher<? extends R> matcher) {
        this.mapper = mapper;
        this.matcher = matcher;
    }

    /**
     * 构造一个 {@link Matcher} 对象, 将 {@code mapper} 参数的返回值通过 {@code matcher}
     * 参数进行匹配后返回匹配结果
     *
     * @param <T>     原始对象类型, 通过 {@code mapper} 参数转化为 {@code <R>} 类型对象
     * @param <R>     要匹配的对象类型
     * @param mapper  用于对象转换的 {@link Function} 对象
     * @param matcher 用于对转换后结果进行匹配的 {@link Matcher} 对象
     * @return {@link ObjectOf} 类型对象
     */
    public static <T, R> Matcher<T> objectOf(Function<? super T, ? extends R> mapper, Matcher<? extends R> matcher) {
        return new ObjectOf<>(mapper, matcher);
    }

    /**
     * 将当前匹配失败的信息拼入整体匹配失败信息流中
     *
     * <p>
     * 匹配失败时, 需要输出当前 {@link Matcher} 的期望信息, 即 {@code matcher} 参数的 {@code describeTo}
     * 方法返回值
     * </p>
     */
    @Override
    public void describeTo(Description description) {
        // 直接设置子匹配器的期望结果为当前匹配器期望结果
        description.appendDescriptionOf(matcher);
    }

    /**
     * 进行匹配操作
     *
     * <p>
     * 由于是类型安全的匹配, 所以参数传入的是实际要求的数据类型
     * </p>
     */
    @Override
    protected boolean matchesSafely(T item, Description mismatch) {
        // 获取 map 后的值
        var obj = mapper.apply(item);

        // 进行匹配
        var result = matcher.matches(obj);

        // 设置匹配结果值
        matcher.describeMismatch(obj, mismatch);
        return result;
    }
}
