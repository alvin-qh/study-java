package alvin.study.guava.base;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import org.junit.jupiter.api.Test;

/**
 * 演示字符串操作工具类
 */
class StringUtilsTest {
    /**
     * 测试将对象数组 (或集合) 连接为字符串
     *
     * <p>
     * {@link Joiner#on(String)} 方法用于创建一个 {@link Joiner} 对象, 并设置"连接分隔符"
     * </p>
     *
     * <p>
     * {@link Joiner#join(Iterable)} 方法将一个集合内的元素通过之前设置的连接符连接为一个字符串,
     * 该方法有若干重载, 分别针对: 数组 (不定参数), 迭代器和字典类型参数
     * </p>
     *
     * <p>
     * {@link Joiner#skipNulls()} 方法表示, 在连接字符串的时候, 忽略值为 {@code null} 的元素值
     * </p>
     *
     * <p>
     * {@link Joiner#useForNull(String)} 方法表示, 在连接字符串的时候, 如果遇到值为 {@code null}
     * 的元素, 将其替换为指定的字符串
     * </p>
     *
     * <p>
     * {@link Joiner#withKeyValueSeparator(String)} 方法用于连接一个 {@link java.util.Map Map}
     * 对象中的所有键值对时, 指定 Key 与 Value 之间的分隔符
     * </p>
     *
     * <p>
     * {@link Joiner#appendTo(StringBuilder, Iterable)} 方法和 {@link Joiner#join(Iterable)}
     * 方法类似, 只是不直接返回字符串, 而是将产生的字符串放到指定的 {@link StringBuilder} 对象中
     * </p>
     */
    @Test
    void joiner_shouldJoinObjectsToString() {
        // 通过 >> 连接符连接多个不定参数
        var s = Joiner.on(">>")
                .join("A", "B", "C");
        then(s).isEqualTo("A>>B>>C");

        // 通过 >> 连接符连接数组中的所有元素
        s = Joiner.on(">>")
                .join(new String[] { "A", "B", "C" });
        then(s).isEqualTo("A>>B>>C");

        // 在连接过程中, 忽略所有为 null 的元素
        s = Joiner.on(">>")
                .skipNulls()
                .join(new String[] { "A", "B", null, "C" });
        then(s).isEqualTo("A>>B>>C");

        // 在连接过程中, 将值为 null 的元素替换为给定的字符串
        s = Joiner.on(">>")
                .useForNull("null")
                .join(new String[] { "A", "B", null, "C" });
        then(s).isEqualTo("A>>B>>null>>C");

        // 在连接过程中, 将值为 null 的元素替换为给定的字符串
        s = Joiner.on(">>")
                .join(ImmutableList.of("A", "B", "C"));
        then(s).isEqualTo("A>>B>>C");

        // 在连接过程中, 将 Map 中的所有键值对进行连接, 键值对之间通过指定的分隔符字符串分割
        s = Joiner.on(">>")
                .withKeyValueSeparator(":")
                .join(ImmutableSortedMap.of(
                    "A", 100,
                    "B", 200,
                    "C", 300));
        then(s).isEqualTo("A:100>>B:200>>C:300");

        var builder = new StringBuilder();
        // 在连接过程中, 将连接结果写入 StringBuilder 对象
        Joiner.on(">>")
                .appendTo(builder, ImmutableList.of("A", "B", "C"));
        then(builder).hasToString("A>>B>>C");
    }

    /**
     * 将字符串根据分隔符切分为数组或集合
     *
     * <p>
     * {@link Splitter#on(String)} 方法返回一个 {@link Splitter} 对象,
     * 该对象可以根据指定的分隔符将字符串进行分割
     * </p>
     *
     * <p>
     * {@link Splitter#onPattern(String)} 和 {@link Splitter#on(String)}
     * 方法类似, 只是分隔符是一个正则表达式
     * </p>
     *
     * <p>
     * {@link Splitter#split(CharSequence)} 方法将所给的字符串分割为一个
     * {@link Iterable} 对象
     * </p>
     *
     * <p>
     * {@link Splitter#omitEmptyStrings()} 方法可以忽略结果中的空字符串
     * </p>
     *
     * <p>
     * {@link Splitter#trimResults()} 方法可以对分割的每个部分去除两端的的空白
     * (或指定) 字符
     * </p>
     *
     * <p>
     * {@link Splitter#limit(int)} 方法用于限制分割结果的数量
     * </p>
     *
     * <p>
     * {@link Splitter#fixedLength(int)} 返回一个 {@link Splitter} 对象,
     * 可以将字符串按固定长度进行分割
     * </p>
     *
     * <p>
     * {@link Splitter#withKeyValueSeparator(String)} 的分割结果为一个
     * {@link java.util.Map Map} 对象, 该方法指定 Key 和 Value 之间的分隔符
     * </p>
     *
     * <p>
     * {@link Splitter#splitToList(CharSequence)} 方法返回一个保存分割结果的
     * {@link java.util.List List} 对象
     * </p>
     *
     * <p>
     * {@link Splitter#splitToStream(CharSequence)} 方法返回一个保存分割结果的
     * {@link java.util.stream.Stream Stream} 对象
     * </p>
     */
    @Test
    void splitter_shouldSplitStringToCollection() {
        // 通过 >> 分隔符分割字符串
        var iterable = Splitter.on(">>")
                .split("A>>B>>C");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B", "C");

        // 通过正则表达式分割字符串
        iterable = Splitter.onPattern("\\s+")
                .split("A  B    C");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B", "C");

        // 确认在分割结果中包含"空"字符串的情况
        iterable = Splitter.on(">>")
                .split("A>>B>>>>C");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B", "", "C");

        // 忽略分割结果中的空字符串
        iterable = Splitter.on(">>")
                .omitEmptyStrings()
                .split("A>>B>>>>C");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B", "C");

        // 对分割结果的每个部分进行 trim 操作, 去除前后空白字符串
        iterable = Splitter.on(">>")
                .trimResults(CharMatcher.whitespace())
                .split(" A >> B >> C ");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B", "C");

        // 限制分割结果的数量, 将字符串分割为 2 部分, 之后的内容不在分割
        iterable = Splitter.on(">>")
                .limit(2)
                .split("A>>B>>C");
        then(ImmutableList.copyOf(iterable))
                .containsExactly("A", "B>>C");

        // 将字符串按固定长度进行分割
        iterable = Splitter.fixedLength(2)
                .split("AABBCC"); // cspell: disable-line
        then(ImmutableList.copyOf(iterable))
                .containsExactly("AA", "BB", "CC");

        // 将字符串通过指定分隔符分割为键值对, 组成 Map 对象,
        // 这里需要指定键值对之间的分隔符
        var map = Splitter.on(">>")
                .withKeyValueSeparator(":")
                .split("A:100>>B:200>>C:300");
        then(map).containsExactly(
            entry("A", "100"),
            entry("B", "200"),
            entry("C", "300"));

        // 将字符串分割为 List 对象
        var list = Splitter.on(">>")
                .splitToList("A>>B>>C");
        then(list).isInstanceOf(List.class)
                .containsExactly("A", "B", "C");

        // 将字符串分割为 Stream 对象
        var stream = Splitter.on(">>")
                .splitToStream("A>>B>>C");
        then(stream.toList()).isInstanceOf(List.class)
                .containsExactly("A", "B", "C");
    }

    /**
     * 测试 {@link CharMatcher} 类
     *
     * <p>
     * {@link CharMatcher} 类对象表示一个"字符匹配器"对象, 可以对指定字符进行匹配,
     * 返回是否匹配的结果
     * </p>
     */
    @Test
    void charMatcher_shouldMatchCharacter() {
        // 创建一个对单个字符进行匹配的匹配器对象
        var matcher = CharMatcher.is('A');

        // 确认匹配器可以对指定目标进行匹配
        // 对单个字符进行匹配
        then(matcher.matches('A')).isTrue();
        // 对字符串中的所有字符进行匹配, 要求所有字符匹配
        then(matcher.matchesAllOf("AAAAA")).isTrue();
        // 对字符串中的所有字符进行匹配, 要求任一字符匹配
        then(matcher.matchesAnyOf("ABC")).isTrue();
        // 对字符串中的所有字符进行匹配, 要求所有字符不匹配
        then(matcher.matchesNoneOf("BCD")).isTrue();

        // 创建一个对给定字符中的任意字符匹配的匹配器
        matcher = CharMatcher.anyOf("AEIOU"); // cspell: disable-line
        then(matcher.matchesAllOf("AAOIUUEE")).isTrue(); // cspell: disable-line

        // 创建一个对任何字符都不匹配的匹配器
        matcher = CharMatcher.none();
        then(matcher.matches('A')).isFalse();

        // 创建一个对任意字符都匹配的匹配器
        matcher = CharMatcher.any();
        then(matcher.matches('A')).isTrue();

        // 创建一个对空格字符匹配的匹配器 (包括 ' ', '\t', '\n', '\r') 空白字符
        matcher = CharMatcher.whitespace();
        then(matcher.matchesAllOf(" \n\t\r")).isTrue();

        // 创建一个不是指定字符的匹配器
        matcher = CharMatcher.isNot('A');
        then(matcher.matchesAllOf("BCDEF")).isTrue(); // cspell: disable-line

        // 创建一个对空格字符匹配的匹配器 (包括 ' ', '\t', '\n', '\r') 空白字符
        matcher = CharMatcher.breakingWhitespace();
        then(matcher.matchesAllOf(" \n\t\r")).isTrue(); // cspell: disable-line

        // 创建一个对所有 ASCII 字符匹配的匹配器
        matcher = CharMatcher.ascii();
        then(matcher.matchesAllOf("ABCabc+-;.\\*")).isTrue();

        // 创建一个对指定范围内字符匹配的匹配器
        matcher = CharMatcher.inRange('A', 'C');
        then(matcher.matchesAllOf("ABC")).isTrue();

        // 创建一个多个条件组合而成的匹配器
        // `or` 匹配器表示和前一个匹配器任意一个匹配
        // `and` 匹配器表示和前一个匹配器同时匹配
        // `negate` 表示和前一个匹配器不匹配
        // `precomputed` 表示优化组合关系
        matcher = CharMatcher.inRange('A', 'C')
                .or(CharMatcher.inRange('a', 'c'))
                .and(CharMatcher.whitespace().negate())
                .precomputed();
        then(matcher.matchesAllOf("ABCabc")).isTrue();
        then(matcher.matchesAllOf(" A")).isFalse();
    }

    /**
     * 使用标准字符集
     *
     * <p>
     * {@link Charsets} 类型包含了各个国家使用的标准字符集对象
     * ({@link java.nio.charset.Charset Charset} 类型对象)
     * </p>
     *
     * <p>
     * 如果使用 JDK 7 以上版本, 请使用
     * {@link java.nio.charset.StandardCharsets StandardCharsets} 类型
     * </p>
     */
    @Test
    void charsets_shouldUseStandardCharset() {
        var bytes = "Hello World".getBytes(StandardCharsets.UTF_8);
        then(bytes).isEqualTo(new byte[] {
            72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100 });
    }

    /**
     * 对单词进行大小写格式处理
     *
     * <p>
     * {@link CaseFormat} 类包含各种常用单词大小写格式化类, 包括:
     * <ul>
     * <li>
     * {@link CaseFormat#LOWER_CAMEL}, 将单词格式化为形如 {@code lowerCamel}
     * 的格式
     * </li>
     * <li>
     * {@link CaseFormat#LOWER_HYPHEN}, 将单词格式化为形如 {@code lower-camel}
     * 的格式
     * </li>
     * <li>
     * {@link CaseFormat#LOWER_UNDERSCORE}, 将单词格式化为形如 {@code lower_underscore}
     * 的格式
     * </li>
     * <li>
     * {@link CaseFormat#UPPER_CAMEL}, 将单词格式化为形如 {@code UpperCamel} 的格式
     * </li>
     * <li>
     * {@link CaseFormat#UPPER_UNDERSCORE}, 将单词格式化为形如 {@code UPPER_UNDERSCORE}
     * 的格式
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 处理的语法为: {@code 原格式.to(新格式, "字符串")}
     * </p>
     */
    @Test
    void caseFormat_shouldFormatCaseOfWorld() {
        var word = "helloWorld";

        // LOWER_CAMEL => LOWER_HYPHEN
        then(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, word))
                .isEqualTo("hello-world");

        // LOWER_CAMEL => LOWER_UNDERSCORE
        then(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, word))
                .isEqualTo("hello_world");

        // LOWER_CAMEL => UPPER_CAMEL
        then(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, word))
                .isEqualTo("HelloWorld");

        // LOWER_CAMEL => UPPER_UNDERSCORE
        then(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, word))
                .isEqualTo("HELLO_WORLD");

        // LOWER_CAMEL => LOWER_CAMEL
        then(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_CAMEL, word))
                .isEqualTo("helloWorld");
    }

    /**
     * 获取两个字符串的共同前缀
     *
     * <p>
     * {@link Strings#commonPrefix(CharSequence, CharSequence)}
     * 方法用于查找两个参数共同的前缀, 该方法的返回值要同时满足两个参数的
     * {@link String#startsWith(String)} 方法
     * </p>
     */
    @Test
    void commonPrefix_shouldFindCommonPrefixOfTwoStrings() {
        var s = Strings.commonPrefix("xx-yy-zz", "xx-zz");
        then(s).isEqualTo("xx-");
    }

    /**
     * 获取两个字符串的共同后缀
     *
     * <p>
     * {@link Strings#commonSuffix(CharSequence, CharSequence)}
     * 方法用于查找两个参数共同的后缀, 该方法的返回值要同时满足两个参数的
     * {@link String#endsWith(String)} 方法
     * </p>
     */
    @Test
    void commonSuffix_shouldFindCommonSuffixOfTwoStrings() {
        var s = Strings.commonSuffix("xx-yy-zz", "xx-zz");
        then(s).isEqualTo("-zz");
    }

    /**
     * 检查字符串是否为 {@code null} 或"空字符串"
     *
     * <p>
     * {@link Strings#isNullOrEmpty(String)} 用于判断一个字符串类型变量是否为
     * {@code null} 或引用了一个"空字符串"
     * </p>
     */
    @Test
    @SuppressWarnings("null")
    void emptyToNull_checkStringIfNullOrEmpty() {
        then(Strings.isNullOrEmpty("")).isTrue();
        then(Strings.isNullOrEmpty(null)).isTrue();
        then(Strings.isNullOrEmpty(" ")).isFalse();
    }

    /**
     * 为字符串添加前缀字符
     *
     * <p>
     * {@link Strings#padStart(String, int, char)} 方法用于为指定字符串添加前缀字符,
     * 其中:
     * <ul>
     * <li>
     * 第一个参数表示要添加前缀的字符串
     * </li>
     * <li>
     * 第二个参数表示结果字符串的最小长度, 该方法会用前缀字符将所给字符串长度补齐到该最小长度;
     * 若所给字符串长度已经不小于最小长度, 则不进行补齐
     * </li>
     * <li>
     * 第三个参数表示用于补齐长度的字符
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void padStart_addPrefixForString() {
        // 用 '0' 字符将所给字符串通过前缀补齐到长度为 3
        var s = Strings.padStart("7", 3, '0');
        then(s).isEqualTo("007");

        // 用 '0' 字符将所给字符串补齐到长度为 3, 但所给字符串长度已经为 3, 所以不进行补齐
        s = Strings.padStart("777", 3, '0');
        then(s).isEqualTo("777");
    }

    /**
     * 为字符串添加前缀字符
     *
     * <p>
     * {@link Strings#padStart(String, int, char)} 方法用于为指定字符串添加前缀字符,
     * 其中:
     * <ul>
     * <li>
     * 第一个参数表示要添加后缀的字符串
     * </li>
     * <li>
     * 第二个参数表示结果字符串的最小长度, 该方法会用后缀字符将所给字符串长度补齐到该最小长度;
     * 若所给字符串长度已经不小于最小长度, 则不进行补齐
     * </li>
     * <li>
     * 第三个参数表示用于补齐长度的字符
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void padEnd_addSuffixForString() {
        // 用 '0' 字符将所给字符串通过后缀补齐到长度为 3
        var s = Strings.padEnd("7", 3, '0');
        then(s).isEqualTo("700");

        // 用 '0' 字符将所给字符串补齐到长度为 3, 但所给字符串长度已经为 3, 所以不进行补齐
        s = Strings.padEnd("777", 3, '0');
        then(s).isEqualTo("777");
    }

    /**
     * 将字符串重复指定次数
     *
     * <p>
     * {@link Strings#repeat(String, int)} 用于将字符串重复指定次数
     * </p>
     */
    @Test
    void repeat_shouldRepeatStringByGivenCount() {
        var s = Strings.repeat("ABC", 5);
        then(s).isEqualTo("ABCABCABCABCABC"); // cspell: disable-line
    }
}
