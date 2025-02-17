package alvin.study.se.reflect.scan.match;

import static org.assertj.core.api.BDDAssertions.then;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link Matchers.AnnotatedWith AnnotatedWith} 和
 * {@link Matchers.AnnotatedWithType AnnotatedWithType} 两个匹配器的注解类型
 *
 * <p>
 * 按照匹配器要求, 注解必须具备 {@code @Retention(RetentionPolicy.RUNTIME)} 标识
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@interface TestAnnotation {}

/**
 * 测试类型
 *
 * <p>
 * 用于测试和类型, 注解相关的匹配器
 * </p>
 */
@TestAnnotation
class TestClass {
    /**
     * 该方法上的注解用于测试和注解相关的匹配器
     */
    @Override
    @TestAnnotation
    public boolean equals(Object obj) {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

/**
 * 定义 {@link TestClass} 类型的子类型
 *
 * <p>
 * 用于测试 {@link Matchers.SubclassesOf} 匹配器
 * </p>
 */
class TestSubClass extends TestClass {}

/**
 * 测试 {@link Matchers} 类型, 检验各个 {@link Matcher} 实现类
 */
class MatchersTest {
    /**
     * 通过 {@link Matchers#any()} 方法测试 {@link Matchers.Any Any} 匹配器
     */
    @Test
    void any_shouldMatcherWorked() {
        // 实例化匹配器对象
        var matcher = Matchers.any();

        // 确认对任意参数匹配结果都为 true
        then(matcher.matches(new Object())).isTrue();
    }

    /**
     * 通过 {@link Matchers#not(Matcher)} 方法测试 {@link Matchers.Not Not} 匹配器
     */
    @Test
    void not_shouldMatcherWorked() {
        // 实例化匹配器对象, 对一个 Any 类型匹配器求非
        var matcher = Matchers.not(Matchers.any());

        // 确认对任意参数匹配结果都为 false
        then(matcher.matches(new Object())).isFalse();
    }

    /**
     * 通过 {@link Matchers#annotatedWith(Class)} 方法测试
     * {@link Matchers.AnnotatedWithType AnnotatedWithType} 匹配器
     */
    @Test
    void annotatedWith_shouldMatcherWorkedWithAnnotationType() throws Exception {
        // 实例化匹配器对象, 设置期待的注解类型
        var matcher = Matchers.annotatedWith(TestAnnotation.class);

        // 确认通过匹配器匹配具备指定注解的类型
        then(matcher.matches(TestClass.class)).isTrue();

        // 确认通过匹配器匹配具备指定注解的方法
        var method = TestClass.class.getMethod("equals", Object.class);
        then(matcher.matches(method)).isTrue();
    }

    /**
     * 通过 {@link Matchers#annotatedWith(java.lang.annotation.Annotation)
     * Matchers.annotatedWith(Annotation)} 方法测试 {@link Matchers.AnnotatedWith
     * AnnotatedWith} 匹配器
     */
    @Test
    void annotatedWith_shouldMatcherWorkedWithAnnotationInstance() throws Exception {
        // 实例化匹配器对象, 设置期待的注解对象
        var anno = TestClass.class.getAnnotation(TestAnnotation.class);
        var matcher = Matchers.annotatedWith(anno);

        // 确认通过匹配器匹配具备指定注解的类
        then(matcher.matches(TestClass.class)).isTrue();

        // 确认通过匹配器匹配具备指定注解的方法
        var method = TestClass.class.getMethod("equals", Object.class);
        then(matcher.matches(method)).isTrue();
    }

    /**
     * 通过 {@link Matchers#subclassesOf(Class)} 方法测试 {@link Matchers.SubclassesOf
     * SubclassesOf} 匹配器
     */
    @Test
    void subclassesOf_shouldMatcherWorked() {
        // 实例化匹配器, 设置期待的超类类型
        var matcher = Matchers.subclassesOf(TestClass.class);

        // 确认通过匹配器可以匹配到对应的子类类型
        then(matcher.matches(TestSubClass.class)).isTrue();
    }

    /**
     * 通过 {@link Matchers#only(Object)} 方法测试 {@link Matchers.Only Only} 匹配器
     */
    @Test
    void only_shouldMatcherWorked() {
        var obj1 = new TestClass();

        // 实例化匹配器, 设置要匹配的对象值
        var matcher = Matchers.only(obj1);

        // 确认通过匹配器可以匹配对象等值
        var obj2 = obj1;
        then(matcher.matches(obj2)).isTrue();

        // 确认两个等值对象可以完成匹配
        obj2 = new TestClass();
        then(matcher.matches(obj2)).isTrue();
    }

    /**
     * 通过 {@link Matchers#identicalTo(Object)} 方法测试 {@link Matchers.IdenticalTo
     * IdenticalTo} 匹配器
     */
    @Test
    void identicalTo_shouldMatcherWorked() {
        var obj1 = new TestClass();

        // 实例化匹配器, 设置要匹配的对象引用
        var matcher = Matchers.identicalTo(obj1);

        // 确认通过匹配器可以匹配相同的对象引用
        var obj2 = obj1;
        then(matcher.matches(obj2)).isTrue();

        // 确认同一个类型的不同引用无法通过匹配
        obj2 = new TestClass();
        then(matcher.matches(obj2)).isFalse();
    }

    /**
     * 通过 {@link Matchers#inSubpackage(String)} 方法测试 {@link Matchers.InSubpackage
     * InSubpackage} 匹配器
     */
    @Test
    void inSubpackage_shouldMatcherWorked() {
        // 定义一组包名称, 分别表示要检测类型的"所在包", "上一级包" 和 "上两级包"
        String[] packages = {
            "alvin.study.se.reflect.scan.match",
            "alvin.study.se.reflect.scan",
            "alvin.study.se.reflect"
        };

        for (var packageName : packages) {
            // 实例化匹配器, 设置要匹配的包名
            var matcher = Matchers.inSubpackage(packageName);

            // 确认通过匹配器可以匹配指定类型在期待的包 (或子包) 中
            then(matcher.matches(TestClass.class)).isTrue();
        }
    }

    /**
     * 通过 {@link Matchers#inPackage(Package)} 方法测试 {@link Matchers.InPackage
     * InPackage} 匹配器
     */
    @Test
    void inPackage_shouldMatcherWorked() {
        // 获取指定的包对象
        var pack = getClass().getClassLoader().getDefinedPackage("alvin.study.se.reflect.scan.match");

        // 实例化匹配器, 设置要匹配的包对象
        var matcher = Matchers.inPackage(pack);

        // 确认通过匹配器可以匹配指定类型在期待的包中
        then(matcher.matches(TestClass.class)).isTrue();
    }

    /**
     * 通过 {@link Matchers#is(Class)} 方法测试 {@link Matchers.Is Is} 匹配器
     */
    @Test
    void is_shouldMatcherWorked() {
        // 实例化匹配器, 设置要匹配的类型
        var matcher = Matchers.is(TestClass.class);

        // 确认通过匹配器可以匹配相同的类型
        then(matcher.matches(TestClass.class)).isTrue();
    }

    /**
     * 通过 {@link Matchers#returns(Matcher)} 方法测试 {@link Matchers.Returns Returns}
     * 匹配器
     */
    @Test
    void returns_shouldMatcherWorked() throws Exception {
        // 获取指定方法
        var method = TestClass.class.getMethod("equals", Object.class);

        // 实例化匹配对象, 设置期望的方法返回类型
        var matcher = Matchers.returns(Matchers.is(boolean.class));

        // 确认通过匹配器可以匹配到具备期望返回值类型的方法
        then(matcher.matches(method)).isTrue();
    }

    /**
     * 通过 {@link Matcher#and(Matcher)} 方法测试 {@link Matcher.AndMatcher AndMatcher}
     * 匹配器
     */
    @Test
    void and_shouldMatcherWorked() {
        // 定义两个预期会成立的 Matcher 对象
        var matcherA = Matchers.inSubpackage("alvin.study.se.reflect.scan.match");
        var matcherB = Matchers.annotatedWith(TestAnnotation.class);

        // 通过 And 连接两个 Matcher 对象, 确认需要同时满足 matcherA 和 matcherB 方能通过匹配
        var matcherAnd = matcherA.and(matcherB);
        then(matcherAnd.matches(TestClass.class)).isTrue();

        // 再定义一个预期不成立的 Matcher 对象, 通过 And 连接, 此时无法通过匹配
        var matcherC = Matchers.inSubpackage("java.util");
        matcherAnd = matcherAnd.and(matcherC);
        then(matcherAnd.matches(TestClass.class)).isFalse();
    }

    /**
     * 通过 {@link Matcher#or(Matcher)} 方法测试 {@link Matcher.OrMatcher OrMatcher} 匹配器
     */
    @Test
    void or_shouldMatcherWorked() {
        // 定义两个预期会成立的 Matcher 对象
        var matcherA = Matchers.inSubpackage("alvin.study.se.reflect.scan.match");
        var matcherB = Matchers.annotatedWith(TestAnnotation.class);

        // 通过 And 连接两个 Matcher 对象, 确认需要同时满足 matcherA 和 matcherB 方能通过匹配
        var matcherAnd = matcherA.and(matcherB);
        then(matcherAnd.matches(TestClass.class)).isTrue();

        // 再定义一个预期不成立的 Matcher 对象, 通过 And 连接, 此时无法通过匹配
        var matcherC = Matchers.inSubpackage("java.util");
        matcherAnd = matcherAnd.and(matcherC);
        then(matcherAnd.matches(TestClass.class)).isFalse();
    }
}
