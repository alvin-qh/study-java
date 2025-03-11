package alvin.study.testing.hamcrest;

import static alvin.study.testing.hamcrest.matcher.IsPresent.notPresent;
import static alvin.study.testing.hamcrest.matcher.IsPresent.present;
import static alvin.study.testing.hamcrest.matcher.IsPresent.presentThen;
import static alvin.study.testing.hamcrest.matcher.IsTruly.falsely;
import static alvin.study.testing.hamcrest.matcher.IsTruly.truly;
import static alvin.study.testing.hamcrest.matcher.ObjectOf.objectOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.array;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import alvin.study.testing.testcase.model.User;
import alvin.study.testing.testcase.service.UserService;

/**
 * 测试 hamcrest 框架提供的断言方法
 */
@ExtendWith(MockitoExtension.class)
class AssertionTest {
    // mock 一个服务对象
    @Mock
    private UserService userService;

    /**
     * 基本应用
     *
     * <p>
     * hamcrest 框架的断言原语起始于一个
     * {@link org.hamcrest.MatcherAssert#assertThat(
     * Object, org.hamcrest.Matcher)
     * MatcherAssert.assertThat(Object, Matcher)} 方法,
     * 第一个参数为要断言的值, 第二个参数是执行断言的匹配器对象
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#is Matchers.is} 方法和
     * {@link org.hamcrest.Matchers#not Matchers.not}
     * 是两个最基本的断言方法, 返回对应的 {@link org.hamcrest.Matcher
     * Matcher} 对象, 表示"是"或"不是"这两个基本概念, 其断言可以针对值,
     * 也可以针对下一级的 {@link org.hamcrest.Matcher Matcher} 对象
     * </p>
     *
     * <p>
     * {@link org.hamcrest.MatcherAssert#assertThat
     * MatcherAssert.assertThat} 方法也可以在第一个参数中传递断言信息,
     * 在测试报告中可以看到
     * </p>
     */
    @Test
    void isOrNot_shouldAssertByIsOrNotOperator() {
        assertThat(true, is(equalTo(Boolean.TRUE)));
        assertThat("The failure reason", true, not(Boolean.FALSE));
    }

    /**
     * 等值断言
     *
     * <p>
     * 对于两个值来说, {@link org.hamcrest.Matchers#is(Object)
     * Matchers.is(Object)} 和 {@link org.hamcrest.Matchers#equalTo(Object)
     * Matchers.equalTo(Object)} 的结果是相同的, 均是通过
     * {@link Object#equals(Object)} 方法的结果, 对是否等值进行断言
     * </p>
     *
     * <p>
     * 但 hamcrest 希望用 {@link org.hamcrest.Matchers#is(org.hamcrest.Matcher)
     * Matchers.is(Matcher)} 作为断言原语的谓语, 下一级
     * {@link org.hamcrest.Matcher Matcher} 对象作为宾语,
     * 这样以被断言值为主语的主谓宾部分就完善了, 可以在测试失败后显式完整的断言结果
     * </p>
     *
     * <p>
     * 如果只希望比较对象引用, 而无需比较对象本身, 则可通过
     * {@link org.hamcrest.Matchers#sameInstance(Object)
     * Matchers.sameInstance(Object)} 进行断言
     * </p>
     */
    @Test
    void equalToAndSameInstance_shouldAssertValuesIfEquals() {
        // 直接使用 equalTo 匹配器进行断言
        assertThat(100, equalTo(Integer.valueOf(100)));
        // 直接使用 is 匹配器进行断言
        assertThat("Hello", is(new String("Hello")));
        // 按照 hamcrest 规范, 通过 is equalTo 描述断言完整的主谓宾结构
        assertThat("Hello", is(equalTo(new String("Hello"))));

        // 直接使用 not 匹配器进行断言
        assertThat("Hello", not("World"));
        // 按照 hamcrest 规范, 通过 not equalTo 描述断言完整的主谓宾结构
        assertThat("Hello", not(equalTo("World")));
        // 按照 hamcrest 规范, 通过 is not equalTo 描述断言完整的主谓宾结构
        assertThat("Hello", is(not(equalTo("World"))));

        // 判断两个引用是否一致 (或不一致)
        assertThat("Hello", is(sameInstance("Hello")));
        assertThat("Hello", not(sameInstance(new String("Hello"))));
    }

    /**
     * {@code null} 和 非{@code null} 断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#nullValue() Matchers.nullValue()}
     * 用于对主语是否为 {@code null} 进行断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#notNullValue() Matchers.notNullValue()}
     * 用于对主语是否不为 {@code null} 进行断言, 相当于
     * {@link org.hamcrest.Matchers#not(org.hamcrest.Matcher)
     * Matchers.not(Matcher)} 和 {@link org.hamcrest.Matchers#nullValue()
     * Matchers.nullValue()} 的组合
     * </p>
     *
     * <p>
     * 进一步的, {@link org.hamcrest.Matchers#notNullValue(Class)
     * Matchers.notNullValue(Class)} 用于对主语不为 {@code null}
     * 时是否匹配具体类型进行断言
     * </p>
     */
    @Test
    void nullOrNotNullValue_shouldAssertValueIsNullOrNotNull() {
        // 判断一个引用是否为 null 值
        assertThat(null, is(nullValue()));
        // 判断一个引用是否不为 null 值
        assertThat("Hello", is(not(nullValue())));

        // 判断一个引用是否不为 null 值且类型匹配
        assertThat(userService, is(notNullValue(UserService.class)));
        // 判断一个引用是否不为 null 值
        assertThat("Hello", is(notNullValue()));
    }

    /**
     * 类型匹配断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#instanceOf(Class)
     * Matchers.instanceOf(Class)} 用于对主语的类型是否匹配进行断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#isA(Class) Matchers.isA(Class)}
     * 相当于是 {@link org.hamcrest.Matchers#is(org.hamcrest.Matcher)
     * Matchers.is(Matcher)} 和 {@link org.hamcrest.Matchers#instanceOf(Class)
     * Matchers.instanceOf(Class)} 的组合
     * </p>
     */
    @Test
    void instanceOfAndIsA_shouldAssertObjectMatchedType() {
        // 判断一个引用是否为指定类型
        assertThat("Hello", is(instanceOf(String.class)));

        // 判断一个引用是否不为指定类型
        assertThat("Hello", not(instanceOf(Integer.class)));

        // 判断一个引用是否为指定类型
        assertThat("Hello", isA(String.class));
    }

    /**
     * 字符串匹配断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#emptyString() Matchers.emptyString()}
     * 用于断言主语是否为一个空字符串
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#emptyOrNullString()
     * Matchers.emptyOrNullString()} 用于断言主语是否为一个空或 {@code null}
     * 值的字符串
     * </p>
     *
     * <p>
     * 对于字符串等值断言, 则直接使用 {@link org.hamcrest.Matchers#equalTo(Object)
     * Matchers.equalTo(Object)} 断言即可
     * </p>
     *
     * <p>
     * 如果需要对字符串进行不区分大小写的比较, 则可以通过
     * {@link org.hamcrest.Matchers#equalToIgnoringCase(String)
     * Matchers.equalToIgnoringCase(String)} 断言进行
     * </p>
     *
     * <p>
     * 如果对比较的字符串忽略前后的空白字符, 则可通过
     * {@link org.hamcrest.Matchers#equalToCompressingWhiteSpace(String)
     * Matchers.equalToCompressingWhiteSpace(String)} 断言进行
     * </p>
     *
     * <p>
     * 对于断言主语字符串是否已指定字符串起始 (或结尾), 则可通过
     * {@link org.hamcrest.Matchers#startsWith(String) Matchers.startsWith(String)}
     * 和 {@link org.hamcrest.Matchers#endsWith(String) Matchers.endsWith(String)}
     * 这两个断言进行
     * </p>
     *
     * <p>
     * 对于断言主语字符串是否包含指定子字符串, 则可通过
     * {@link org.hamcrest.Matchers#containsString(String)
     * Matchers.containsString(String)} 断言进行
     * </p>
     */
    @Test
    void strings_shouldAssertIfStringMatchedMatchers() {
        // 判断一个字符串是否为空
        assertThat("", is(emptyString()));
        // 判断一个字符串是否为空或空字符串
        assertThat(null, is(emptyOrNullString()));
        // 判断一个字符串是否不为 null 或空字符串
        assertThat("Hello", not(emptyOrNullString()));

        // 判断一个字符串是否和指定字符串相等
        assertThat("Hello", is(equalTo("Hello")));
        // 判断一个字符串是否和指定字符串相等 (忽略大小写比较)
        assertThat("Hello", is(equalToIgnoringCase("hello")));
        // 判断一个字符串是否和指定字符串相等 (忽略大小写并去掉前后空格)
        assertThat("Hello", is(equalToCompressingWhiteSpace(" Hello ")));

        // 判断一个字符串是否以指定字符串开始
        assertThat("Hello", is(startsWith("H")));
        // 判断一个字符串是否以指定字符串结尾
        assertThat("Hello", is(endsWith("o")));

        // 判断一个字符串是否包含指定字符串
        assertThat("Hello", is(containsString("ello")));
    }

    /**
     * 数组匹配断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#array(org.hamcrest.Matcher...)
     * Matchers.array(Matcher)} 方法用于通过一组 {@link org.hamcrest.Matcher
     * Matcher} 对象, 来对主语数组的元素进行逐一匹配断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#arrayContaining(Object...)
     * Matchers.arrayContaining(Object...)} 方法用于对主语数组的元素按顺序进行匹配;
     * 而 {@link org.hamcrest.Matchers#arrayContaining(List)
     * Matchers.arrayContaining(List&lt; Matcher&gt;)} 和
     * {@link org.hamcrest.Matchers#arrayContaining(org.hamcrest.Matcher...)
     * Matchers.arrayContaining(Matcher...)} 则用于通过一组 {@link org.hamcrest.Matcher
     * Matcher} 对象对主语数组的元素按顺序进行匹配, 功能和
     * {@link org.hamcrest.Matchers#array(org.hamcrest.Matcher...)
     * Matchers.array(Matcher)} 类似
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#arrayContainingInAnyOrder(Object...)
     * Matchers.arrayContainingInAnyOrder(Object...)},
     * {@link org.hamcrest.Matchers#arrayContainingInAnyOrder(java.util.Collection)
     * Matchers.arrayContainingInAnyOrder(Collection&lt; Matcher&gt;)} 以及
     * {@link org.hamcrest.Matchers#arrayContainingInAnyOrder(org.hamcrest.Matcher...)
     * Matchers.arrayContainingInAnyOrder(Matcher...)} 和上述三个方法功能类似,
     * 但不匹配主语数组的元素顺序
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#arrayWithSize(int) Matchers.arrayWithSize(int)}
     * 以及 {@link org.hamcrest.Matchers#arrayWithSize(org.hamcrest.Matcher)
     * Matchers.arrayWithSize(Matcher)} 方法用于对主语数组的长度进行断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#emptyArray() Matchers.emptyArray()}
     * 方法用于对主语数组是否为空数组进行断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#hasItemInArray(Object)
     * Matchers.hasItemInArray(Object)} 和
     * {@link org.hamcrest.Matchers#hasItemInArray(org.hamcrest.Matcher)
     * Matchers.hasItemInArray(Matcher)}
     * 方法用于对主语数组是否包含指定元素或指定元素是否匹配某个
     * {@link org.hamcrest.Matcher Matcher} 对象进行断言
     */
    @Test
    void arrays_shouldAssertArrayIfMatchedMatchers() {
        // 判断一个数组是否和指定的元素值匹配
        assertThat(new Integer[] { 1, 2, 3 }, is(arrayContaining(1, 2, 3)));
        // 判断一个数组是否和指定的匹配器匹配
        assertThat(new Integer[] { 1, 2, 3 }, is(array(equalTo(1), equalTo(2), equalTo(3))));
        // 判断一个数组是否和指定的匹配器匹配
        assertThat(new Integer[] { 1, 2, 3 }, is(arrayContaining(equalTo(1), equalTo(2), equalTo(3))));

        // 判断一个数组是否和指定的元素值匹配 (忽略元素顺序)
        assertThat(new Integer[] { 1, 2, 3 }, is(arrayContainingInAnyOrder(3, 2, 1)));
        // 判断一个数组是否和指定的匹配器匹配 (忽略匹配器顺序)
        assertThat(new Integer[] { 1, 2, 3 }, is(arrayContainingInAnyOrder(equalTo(2), equalTo(1), equalTo(3))));

        // 判断一个数组元素数量是否匹配
        assertThat(new Integer[] { 1, 2, 3 }, is(arrayWithSize(equalTo(3))));

        // 判断一个数组是否为空
        assertThat(new Integer[] {}, is(emptyArray()));
        // 判断一个数组是否不为空
        assertThat(new Integer[] { 1, 2, 3 }, not(emptyArray()));

        // 判断一个数组是否包含指定元素
        assertThat(new Integer[] { 1, 2, 3 }, is(hasItemInArray(2)));
        // 判断一个数组的元素是否匹配指定的匹配器
        assertThat(new Integer[] { 1, 2, 3 }, is(hasItemInArray(greaterThan(2))));
    }

    /**
     * 集合匹配断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#contains(Object...)
     * Matchers.contains(Object...)} 方法用于对主语集合的元素按顺序进行匹配;
     * 而 {@link org.hamcrest.Matchers#contains(List)
     * Matchers.contains(List&lt;Matcher&gt;)} 和
     * {@link org.hamcrest.Matchers#contains(org.hamcrest.Matcher...)
     * Matchers.contains(Matcher...)} 则用于通过一组
     * {@link org.hamcrest.Matcher Matcher} 对象对主语数组的元素按顺序进行匹配
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#containsInAnyOrder(Object...)
     * Matchers.containsInAnyOrder(Object...)},
     * {@link org.hamcrest.Matchers#containsInAnyOrder(java.util.Collection)
     * Matchers.containsInAnyOrder(Collection&lt; Matcher&gt;)} 以及
     * {@link org.hamcrest.Matchers#containsInAnyOrder(org.hamcrest.Matcher...)
     * Matchers.containsInAnyOrder(Matcher...)} 和上述三个方法功能类似,
     * 但不匹配主语集合的元素顺序
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#iterableWithSize(int)
     * Matchers.iterableWithSize(int)} 以及
     * {@link org.hamcrest.Matchers#iterableWithSize(org.hamcrest.Matcher)
     * Matchers.iterableWithSize(Matcher)} 方法通过遍历迭代器的方式对主语集合的长度进行断言;
     * {@link org.hamcrest.Matchers#hasSize(int) Matchers.hasSize(int)} 以及
     * {@link org.hamcrest.Matchers#hasSize(org.hamcrest.Matcher)
     * Matchers.hasSize(Matcher)} 方法则是通过主语集合对象的 {@code size()}
     * 方法对其长度进行断言, 若主语集合无 {@code size()} 方法, 则抛出异常
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#empty() Matchers.empty()}
     * 方法用于对主语集合是否为空数组进行断言
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#hasItem(Object)
     * Matchers.hasItem(Object)} 和
     * {@link org.hamcrest.Matchers#hasItem(org.hamcrest.Matcher)
     * Matchers.hasItem(Matcher)} 方法用于对主语集合是否包含指定元素或指定元素是否匹配某个
     * {@link org.hamcrest.Matcher Matcher} 对象进行断言
     */
    @Test
    void collection_shouldAssertCollectionIfMatchedMatchers() {
        // 判断一个集合是否和指定的元素值匹配
        assertThat(List.of(1, 2, 3), is(contains(1, 2, 3)));
        // 判断一个集合是否和指定的元素匹配器匹配
        assertThat(List.of(1, 2, 3), is(contains(equalTo(1), equalTo(2), equalTo(3))));
        // 判断一个集合是否和指定的元素匹配器匹配
        assertThat(List.of(1, 2, 3), is(contains(List.of(equalTo(1), equalTo(2), equalTo(3)))));

        // 判断一个集合是否和指定的元素值匹配 (忽略元素顺序)
        assertThat(List.of(1, 2, 3), is(containsInAnyOrder(2, 1, 3)));
        // 判断一个集合是否和指定的元素匹配器匹配 (忽略匹配器顺序)
        assertThat(List.of(1, 2, 3), is(containsInAnyOrder(equalTo(2), equalTo(1), equalTo(3))));
        // 判断一个集合是否和指定的元素匹配器匹配 (忽略匹配器顺序)
        assertThat(List.of(1, 2, 3), is(containsInAnyOrder(List.of(equalTo(2), equalTo(1), equalTo(3)))));

        // 判断一个集合是否可迭代且元素数量符合预期
        assertThat(List.of(1, 2, 3), is(iterableWithSize(3)));
        // 判断一个集合是否可迭代且元素数量符合预期
        assertThat(Set.copyOf(List.of(1, 2, 2)), is(iterableWithSize(2)));
        // 判断一个集合元素数量是否符合预期
        assertThat(List.of(1, 2), is(hasSize(equalTo(2))));

        // 判断一个集合为空
        assertThat(List.of(), is(empty()));
        // 判断一个集合是否不为空
        assertThat(List.of(1, 2, 3), not(empty()));

        // 判断一个集合是否包含指定元素
        assertThat(List.of(1, 2, 3), is(hasItem(2)));
        // 判断一个集合元素是否能匹配指定的匹配器
        assertThat(List.of(1, 2, 3), is(hasItem(greaterThan(2)))); // 是否包含大于 2 的元素
    }

    /**
     * Map 集合匹配断言
     *
     * <p>
     * {@link org.hamcrest.Matchers#hasKey(Object) Matchers.hasKey(Object)}
     * 和 {@link org.hamcrest.Matchers#hasKey(org.hamcrest.Matcher)
     * Matchers.hasKey(Matcher)} 方法用于匹配主语 {@link Map} 中是否包含指定的
     * {@code Key} 值
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#hasValue(Object) Matchers.hasValue(Object)}
     * 和 {@link org.hamcrest.Matchers#hasValue(org.hamcrest.Matcher)
     * Matchers.hasValue(Matcher)} 方法用于匹配主语 {@link Map} 中是否包含指定的
     * {@code Value} 值
     * </p>
     *
     * <p>
     * {@link org.hamcrest.Matchers#hasEntry(Object, Object)
     * Matchers.hasValue(Object, Object)} 和
     * {@link org.hamcrest.Matchers#hasEntry(org.hamcrest.Matcher, org.hamcrest.Matcher)
     * Matchers.hasValue(Matcher, Matcher)} 方法用于匹配主语 {@link Map}
     * 中是否包含指定的 {@link Map.Entry} 键值对
     * </p>
     *
     * <p>
     * 可以通过 {@link org.hamcrest.Matchers#allOf(org.hamcrest.Matcher...)
     * Matchers.allOf(Matcher...)} 匹配器匹配一组
     * {@link org.hamcrest.Matcher Matcher} 对象, 以对多个键值对进行匹配
     * </p>
     */
    @Test
    void map_shouldAssertMapIfMatchedMatchers() {
        // 判断一个 Map 集合是否包含指定的 Key 值
        assertThat(Map.of("A", 1, "B", 2), is(hasKey("A")));
        // 判断一个 Map 集合是否包含指定的 Value 值
        assertThat(Map.of("A", 1, "B", 2), is(hasValue(2)));
        // 判断一个 Map 集合是否包含指定的键值对
        assertThat(Map.of("A", 1, "B", 2), is(hasEntry("A", 1)));

        // 通过 allOf 匹配器对多个键值对进行匹配
        assertThat(
            Map.of("A", 1, "B", 2, "C", 3),
            is(allOf(
                hasEntry("A", 1),
                hasEntry("B", 2))));
    }

    /**
     * 自定义匹配规则进行断言
     *
     * <p>
     * 通过 {@link alvin.study.testing.hamcrest.matcher.IsTruly#truly()
     * IsTruly.truly()} 以及
     * {@link alvin.study.testing.hamcrest.matcher.IsTruly#falsely()
     * IsTruly.falsely()} 对主语对象是否表示 {@code true} 或 {@code false}
     * 进行断言
     * </p>
     *
     * <p>
     * 通过
     * {@link alvin.study.testing.hamcrest.matcher.ObjectOf#objectOf(
     * java.util.function.Function, org.hamcrest.Matcher)
     * ObjectOf.objectOf(Function, Matcher)} 方法以主语对象为参数进行回调,
     * 并对返回结果进行匹配
     * </p>
     *
     * <p>
     * 通过 {@link alvin.study.testing.hamcrest.matcher.IsPresent#present()
     * IsPresent.present()},
     * {@link alvin.study.testing.hamcrest.matcher.IsPresent#notPresent()
     * IsPresent.notPresent()} 方法对主语 {@link Optional} 对象的
     * {@link Optional#isPresent()} 是否为 {@code true} 进行断言;
     * {@link alvin.study.testing.hamcrest.matcher.IsPresent#presentThen(
     * org.hamcrest.Matcher...) IsPresent.presentThen(Matcher...)}
     * 方法对主语 {@link Optional} 对象包含的对象进行一组匹配, 进行断言
     * </p>
     */
    @Test
    void customMatchers_shouldAssertByCustomMatchers() {
        // 匹配一个值是否代表"真"或"假"
        assertThat(true, is(truly()));
        assertThat("Hello", is(truly()));
        assertThat(100, is(truly()));
        assertThat(new Object(), is(truly()));

        assertThat(false, is(falsely()));
        assertThat("", is(falsely()));
        assertThat(0, is(falsely()));
        assertThat(0.0, is(falsely()));
        assertThat(null, is(falsely()));

        // 匹配一个对象是否具备指定的属性, 且属性值符合预期的匹配器
        assertThat("Hello", is(objectOf(String::length, equalTo(5))));
        assertThat(new User(1, "Alvin"), is(objectOf(User::getName, equalTo("Alvin"))));

        // 匹配一个 Optional 对象是否包含内容
        assertThat(Optional.of(new Object()), is(present()));
        assertThat(Optional.empty(), is(notPresent()));
        assertThat(
            Optional.of(new User(1, "Alvin")),
            is(presentThen(
                objectOf(User::getId, is(equalTo(1))),
                objectOf(User::getName, is(equalTo("Alvin"))))));
    }
}
