package alvin.study.testing.assertj;

import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.not;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import alvin.study.testing.testcase.model.Group;
import alvin.study.testing.testcase.model.User;

/**
 * 测试 AssertJ 断言库
 */
class AssertionTest {
    /**
     * 断言值是否为 {@code true} 或 {@code false}
     */
    @Test
    void true_shouldAssertValueIsTrue() {
        var val = true;

        // 断言一个值是否为 true
        assertThat(val).isTrue();

        // 断言一个值是否为 false
        assertThat(!val).isFalse();
    }

    /**
     * 通过 {@link Condition} 对象或 {@code matches} 方法进行匹配
     *
     * <p>
     * 对于一部分复杂的断言逻辑, 无法通过简单的判断完成, 此时可以通过
     * {@link Condition} 对象进行匹配, 其内部调用一个
     * {@link java.util.function.Predicate Predicate}
     * 函数对象执行判断逻辑
     * </p>
     *
     * <p>
     * 如果需要简化代码, 也可以通过 {@code matches} 方法直接通过一个
     * {@link java.util.function.Predicate Predicate}
     * 函数对象执行判断逻辑
     * </p>
     */
    @Test
    void condition_shouldAssertToMatchedCondition() {
        // 定义要断言的对象
        var obj = new User(1, "Alvin");

        // 定义一个 Condition 对象, 断言是否满足该 Condition 对象
        var cond = new Condition<User>(
            user -> user.getName().equals("Alvin"),
            "Check name");
        assertThat(obj).is(cond);
        assertThat(obj).has(cond);

        // 定义另一个 Condition 对象, 断言是否无需满足该 Condition 对象
        cond = new Condition<>(
            user -> user.getName().equals("Emma"),
            "Check name");
        assertThat(obj).isNot(cond);
        assertThat(obj).doesNotHave(cond);

        // 直接利用 Predicate 对象进行条件匹配
        assertThat(obj).matches(
            user -> user.getName().equals("Alvin"));

        // 定义多个 Condition 对象
        var condId = new Condition<User>() {
            @Override
            public boolean matches(User value) {
                return value.getId() == 1;
            }
        };

        var condName = new Condition<User>() {
            @Override
            public boolean matches(User value) {
                return value.getName().equals("Alvin");
            }
        };

        var condBad = new Condition<User>() {
            @Override
            public boolean matches(User value) {
                return value == null;
            }
        };

        // 断言一个对象是否满足所有指定的 Condition 对象
        assertThat(obj).is(allOf(condId, condName));

        // 断言一个对象是否满足任意一个 Condition 对象
        assertThat(obj).is(anyOf(condId, condName, condBad));

        // 断言一个对象是否不满足指定的 Condition 对象
        assertThat(obj).is(not(condBad));
    }

    /**
     * 自定义比较器进行断言
     */
    @Test
    void comparator_shouldAssertValueByCustomComparator() {
        // 自定义比较器进行断言
        var num = 100;
        assertThat(num)
                .usingComparator((l, r) -> l / 10 - r)
                .isEqualTo(10);
    }

    /**
     * 与 {@link org.assertj.core.api.Assertions#assertThat
     * assertThat} 静态方法类似, 也可以通过
     * {@link org.assertj.core.api.BDDAssertions#then then}
     * 方法进行断言, 属于另一种断言格式
     */
    @Test
    void bdd_shouldMakeAssertionWithBDDFormat() {
        // 定义要断言的对象
        var obj = new User(1, "Alvin");

        // 定义一个 Condition 对象, 断言是否满足该 Condition 对象
        var cond = new Condition<User>(
            user -> user.getName()
                    .equals("Alvin"),
            "Check name");
        then(obj).is(cond);
        then(obj).has(cond);

        // 定义另一个 Condition 对象, 断言是否无需满足该 Condition 对象
        cond = new Condition<>(
            user -> user.getName()
                    .equals("Emma"),
            "Check name");
        then(obj).isNot(cond);
        then(obj).doesNotHave(cond);

        // 直接利用 Predicate 对象进行条件匹配
        then(obj).matches(
            user -> user.getName()
                    .equals("Alvin"));
    }

    /**
     * 断言值是否为和指定值相等
     */
    @Test
    void equalTo_shouldAssertValueEqualToAnother() {
        var val = 100;

        // 断言一个值是否等于指定值
        then(val).isEqualTo(100);

        // 断言一个值是否不等于指定值
        then(val).isNotEqualTo(200);
    }

    /**
     * 断言一个引用是否和指定的引用相等
     */
    @Test
    void sameAs_shouldAssertObjectSameToAnother() {
        var obj = "Hello";

        // 断言一个引用是否和指定引用相等
        then(obj).isSameAs("Hello");

        // 断言一个引用是否和指定引用不相等
        then(obj).isNotSameAs(new String("Hello"));
    }

    /**
     * 断言一个引用是否为指定类型
     */
    @Test
    void instanceOf_shouldAssertReferenceIsInstanceOfType() {
        var obj = "Hello";

        // 断言一个引用是否为指定类型
        then(obj).isInstanceOf(String.class);

        // 断言一个引用是否不为指定类型
        then(obj).isNotInstanceOf(Integer.class);
    }

    /**
     * 断言一个引用是否为 {@code null} 或不为 {@code null}
     */
    @Test
    void null_shouldAssertReferenceIsNullOrNot() {
        Object obj = null;
        // 断言一个引用是否为 null 值
        then(obj).isNull();

        obj = "Hello";
        // 断言一个引用是否不为 null 值
        then(obj).isNotNull();
    }

    /**
     * 断言字符串是否匹配期待的匹配器
     */
    @Test
    void strings_shouldAssertStringMatchedMatchers() {
        // 断言字符串是否为空或不为空
        then("").isEmpty();
        then((String) null).isNullOrEmpty();
        then("Hello").isNotEmpty();

        // 断言字符串和期待字符串的比较
        then("Hello")
                .isEqualToIgnoringCase("hello");
        then("Hello")
                .isEqualToIgnoringWhitespace(" Hello ");
        then("Hello")
                .isEqualToIgnoringNewLines("Hel\nlo");

        // 断言字符串是否以期待字符串开头或结尾
        then("Hello").startsWith("H");
        then("Hello").endsWith("o");

        // 断言字符串是否包含了期待的子字符串
        then("Hello").contains("ello");
    }

    /**
     * 断言数组是否匹配期待的匹配器
     */
    @Test
    void arrays_shouldAssertArrayMatchedMatches() {
        var array = new int[] { 1, 2, 3, 3 };

        // 断言一个数组的长度
        then(array).hasSize(4);

        // 断言一个数组是否包含 (或不包含) 指定的元素
        then(array).contains(1, 2);
        then(array).doesNotContain(4);

        // 断言一个数组是否包含全部的指定元素
        then(array).containsOnly(1, 2, 3);
        // 断言一个数组是否只包含一次指定的元素
        then(array).containsOnlyOnce(1, 2);

        // 断言一个数组是否精确的包含全部的指定元素
        then(array).containsExactly(1, 2, 3, 3);
        // 断言一个数组是否精确的包含全部的指定元素且不关心元素顺序
        then(array).containsExactlyInAnyOrder(3, 1, 3, 2);
        // 断言一个数组是否包含任意指定元素
        then(array).containsAnyOf(1, 3);
        // 断言一个数组是否按顺序包含指定元素 (重复元素算一个元素)
        then(array).containsSequence(1, 2, 3);

        // 断言一个数组的元素是否有序排列
        then(array).isSorted();
        // 断言一个数组的元素是否安装指定的比较器有序排列
        then(array).isSortedAccordingTo(Integer::compareTo);
    }

    /**
     * 断言 {@link java.util.Collection Collection} 对象是否匹配期待的匹配器
     */
    @Test
    void collection_shouldAssertCollectionMatchedMatches() {
        var list = List.of(1, 2, 3, 3);

        // 断言一个集合的长度
        then(list).hasSize(4);
        then(list).size().isGreaterThan(3);

        // 断言一个集合是否包含 (或不包含) 指定的元素
        then(list).contains(1, 2);
        then(list).doesNotContain(4);

        // 断言一个集合是否包含全部的指定元素
        then(list).containsOnly(1, 2, 3);
        // 断言一个集合是否只包含一次指定的元素
        then(list).containsOnlyOnce(1, 2);

        // 断言一个集合是否精确的包含全部的指定元素
        then(list).containsExactly(1, 2, 3, 3);
        // 断言一个集合是否精确的包含全部的指定元素且不关心元素顺序
        then(list).containsExactlyInAnyOrder(3, 1, 3, 2);
        // 断言一个集合是否包含任意指定元素
        then(list).containsAnyOf(1, 3);
        // 断言一个集合是否按顺序包含指定元素 (重复元素算一个元素)
        then(list).containsSequence(1, 2, 3);
        // 断言指定索引位置是否具有期待元素
        then(list).contains(3, 2);

        // 断言一个集合的元素是否有序排列
        then(list).isSorted();
        // 断言一个集合的元素是否安装指定的比较器有序排列
        then(list).isSortedAccordingTo(Integer::compareTo);

        // 断言集合的所有元素是否满足指定条件
        then(list).allMatch(n -> n > 0);
        // 断言集合的任意元素是否满足指定条件
        then(list).anyMatch(n -> n % 2 == 1);

        // 对集合元素通过条件进行过滤, 断言过滤结果
        then(list)
                .filteredOn(n -> n > 0)
                .containsExactly(1, 2, 3, 3);
        // 对集合元素进行转换后, 断言转换后的结果
        then(list).map(String::valueOf)
                .filteredOn(s -> !s.isEmpty())
                .containsExactly("1", "2", "3", "3");

        // 断言集合的所有元素都满足指定的 Condition 对象
        var cond = new Condition<Integer>(n -> n > 0, "great than 1");
        then(list).are(cond);

        // 断言集合的指定数量元素满足指定的 Condition 对象
        cond = new Condition<>(n -> n > 1, "");
        then(list).areAtMost(3, cond);

        var users = List.of(
            new User(1, "Alvin"),
            new User(2, "Emma"),
            new User(3, "Lucy"));

        // 获取集合元素的特定属性, 进行断言
        then(users).extracting("name")
                .contains("Alvin", "Emma", "Lucy")
                .doesNotContain("Lily");

        // 获取集合元素的若干属性, 进行断言
        then(users).extracting("id", "name")
                .contains(
                    tuple(1, "Alvin"),
                    tuple(2, "Emma"),
                    tuple(3, "Lucy"));

        var groups = List.of(
            new Group(1,
                "Group-1",
                List.of(
                    new User(1, "Alvin"),
                    new User(2, "Emma"))),
            new Group(2,
                "Group-2",
                List.of(
                    new User(3, "Lucy"),
                    new User(4, "Lily"))));

        // 将集合中对象中的集合属性展开, 进行断言
        then(groups).flatExtracting("users")
                .extracting("name")
                .contains("Alvin", "Emma", "Lucy", "Lily")
                .doesNotContain("Arthur");
    }

    /**
     * 断言 {@link Map} 对象是否匹配期待的匹配器
     */
    @Test
    void map_shouldAssertMapIfMatchedMatchers() {
        var map = Map.of(
            "A", 1,
            "B", 2);

        // 断言 Map 对象的键值对个数
        then(map).hasSize(2);
        then(map).size()
                .isGreaterThan(1)
                .isLessThan(3);

        // 断言 Map 对象中是否包含指定的 Key
        then(map).containsKey("A");
        then(map).doesNotContainKey("C");

        // 断言 Map 对象包含的全部 Key 值
        then(map).containsOnlyKeys("A", "B");

        // 断言 Map 对象中是否包含指定的 Value
        then(map).containsValue(1);
        then(map).doesNotContainValue(3);

        // 断言 Map 对象是否包含指定的键值对
        then(map).containsEntry("B", 2);
        then(map).contains(
            entry("A", 1),
            entry("B", 2));

        // 断言指定的 Key 对应的 Value 是否符合期待值
        then(map).extracting("A")
                .isEqualTo(1);
        then(map).extracting("A", "B")
                .contains(1, 2);

        var keyCond = new Condition<String>(
            key -> "A".equals(key) || "B".equals(key),
            "has key");

        // 断言 Map 的 Key 是否符合指定的 Condition 对象
        then(map).hasKeySatisfying(keyCond);

        var valueCond = new Condition<Integer>(
            n -> n > 0 && n < 3,
            "has value");

        // 断言 Map 对象的 Value 是否符合指定的 Condition 对象
        then(map).hasValueSatisfying(valueCond);

        // 断言 Map 对象的 Key 和 Value 是否匹配指定的 Condition 对象
        then(map).hasEntrySatisfying(keyCond, valueCond);
        then(map).hasEntrySatisfying("B", valueCond);
        then(map).hasEntrySatisfying(
            new Condition<>(e -> keyCond.matches(e.getKey()) &&
                                 valueCond.matches(e.getValue()),
                ""));
    }

    /**
     * 断言代码是否抛出异常
     */
    @Test
    void thrown_shouldCacheException() {
        thenThrownBy(() -> {
            throw new IllegalAccessException("Test exception");
        })
                .isInstanceOf(IllegalAccessException.class)
                .hasMessage("Test exception");
    }

    /**
     * 给测试增加具体的描述
     *
     * <p>
     * 描述信息会在测试失败后, 在测试报告中显示, 以说明该测试的目标
     * </p>
     *
     * <p>
     * {@code as} 和 {@code describedAs} 方法的含义是相同的,
     * 注意: 设置描述必须在断言之前调用
     * </p>
     */
    @Test
    @Disabled("Just a demo")
    void as_shouldGivenTestName() {
        then(100 > 99)
                .as("Number compare")
                .isFalse();
        then(100 > 99)
                .describedAs("Number compare")
                .isFalse();
    }
}
