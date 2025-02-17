package alvin.study.testing.hamcrest.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.not;

/**
 * 针对 {@link Optional} 对象进行匹配的 {@link Matcher} 接口对象
 *
 * <p>
 * 该匹配器接收一个 {@link Optional} 类型对象, 并匹配器是否包含内容, 即 {@link Optional#isPresent()} 为
 * {@code true}
 * </p>
 *
 * <p>
 * 该匹配器还可以设置一组下级匹配器, 当传入的 {@link Optional} 对象不为空时, 进一步调用下一级的匹配器对
 * {@link Optional} 对象包含的内容进行匹配
 * </p>
 *
 * <p>
 * 当前类型的超类 {@link TypeSafeDiagnosingMatcher} 表示一个"类型安全"并且"包含下一级 {@link Matcher}"
 * 对象的类型, 相当于结合了 {@link org.hamcrest.TypeSafeMatcher TypeSafeMatcher} (类型安全
 * Matcher) 以及 {@link org.hamcrest.DiagnosingMatcher DiagnosingMatcher} (具备下一级
 * Matcher) 这两个超类
 * </p>
 *
 * @param <T> 要匹配的对象类型, 即 {@link Optional Optional&lt; T&gt;}
 */
public class IsPresent<T> extends TypeSafeDiagnosingMatcher<Optional<? extends T>> {
    // 表示下级 Matcher 对象的集合迭代器
    private Iterable<Matcher<? extends T>> matchers;

    /**
     * 构造器, 传入下级 {@link Matcher} 对象集合迭代器
     *
     * <p>
     * 下级 {@link Matcher} 对象集合迭代器将在 {@link #presentThen(Matcher...)} 方法中进行传递
     * </p>
     *
     * @param matchers 下级 {@link Matcher} 对象集合迭代器, {@code null} 表示无需继续下一级匹配操作
     */
    protected IsPresent(Iterable<Matcher<? extends T>> matchers) {
        this.matchers = matchers;
    }

    /**
     * 构造一个 {@link Matcher} 对象, 用于匹配 {@link Optional#isPresent()} 是否为 {@code true}
     *
     * @param <T> 任意类型
     * @return {@link IsPresent} 类型对象
     */
    public static <T> Matcher<Optional<? extends T>> present() {
        return new IsPresent<>(null);
    }

    /**
     * 构造一个 {@link Matcher} 对象, 用于匹配 {@link Optional#isPresent()} 是否为 {@code false}
     *
     * @param <T> 任意类型
     * @return {@link org.hamcrest.core.IsNot IsNot(IsPresent)} 类型对象
     */
    public static <T> Matcher<Optional<? extends T>> notPresent() {
        return not(present());
    }

    /**
     * 构造一个 {@link Matcher} 对象, 用于匹配 {@link Optional#isPresent()} 是否为 {@code true},
     * 且在匹配成功后执行下级 {@link Matcher} 集合进一步进行匹配
     *
     * @param <T> 任意类型
     * @return {@link IsPresent} 类型对象
     */
    @SafeVarargs
    public static <T> Matcher<Optional<? extends T>> presentThen(Matcher<? extends T>... matcher) {
        return new IsPresent<>(List.of(matcher));
    }

    /**
     * 将当前匹配失败的信息拼入整体匹配失败信息流中
     *
     * <p>
     * 匹配失败时, 需要输出当前 {@link Matcher} 的期望信息, 对于 {@link #matchers} 字段为空的情况, 期望值为
     * {@code "<present>"} 常量值
     * </p>
     *
     * <p>
     * 如果 {@link #matchers} 字段不为空, 则通过
     * {@link Description#appendList(String, String, String, Iterable)} 方法对下级
     * {@link Matcher} 对象的匹配结果进行拼装
     * </p>
     */
    @Override
    public void describeTo(Description description) {
        if (matchers == null) {
            description.appendText("<present>");
        } else {
            description.appendList("<", ", ", ">", matchers);
        }
    }

    /**
     * 进行匹配操作
     *
     * <p>
     * 由于是类型安全的匹配, 所以参数传入的是实际要求的数据类型
     * </p>
     */
    @Override
    protected boolean matchesSafely(Optional<? extends T> item, Description mismatch) {
        // 如果 Optional 对象为空, 则匹配失败
        if (item.isEmpty()) {
            matchers = null;
            return false;
        }

        // 如果没有下一级匹配, 则匹配成功
        if (matchers == null) {
            return true;
        }

        var result = true;
        var first = true;

        // 设置匹配结果起始文本
        mismatch.appendText("<");

        // 遍历所有下一级匹配器, 匹配指定的值
        for (var matcher : matchers) {
            var obj = item.get();
            if (!first) {
                // 分隔各个子匹配器匹配结果
                mismatch.appendText(", ");
            }
            first = false;

            // 执行子匹配器
            if (!matcher.matches(obj)) {
                result = false;
            }

            // 匹配完毕后, 添加匹配实际结果
            matcher.describeMismatch(obj, mismatch);
        }

        // 设置匹配结果结束文本
        mismatch.appendText(">");
        return result;
    }
}
