package alvin.study.se.reflect.generic;

import alvin.study.se.reflect.scan.ClassScanner;
import alvin.study.se.reflect.scan.match.Matcher;
import alvin.study.se.reflect.scan.match.Matchers;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.BDDAssertions.then;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@interface TestAnnotation { }

@TestAnnotation
abstract class TestClass { }

@TestAnnotation
class TestSubClass extends TestClass { }

/**
 * 测试 {@link ClassScanner} 类型, 查找符合条件的类
 */
class ClassScannerTest {
    /**
     * 测试 {@link ClassScanner#matching(Matcher)
     * ClassScanner#matching(Matcher)} 和 {@link ClassScanner#in(Package...)} 方法,
     * 通过指定的匹配器在指定包范围内查询类
     *
     * <p>
     * 测试查找文件系统中 {@code .class} 文件中的 {@link Class} 对象
     * </p>
     */
    @Test
    void file_shouldClassMatched() {
        // 定义匹配器, 由如下几个条件组合而成
        // 1. 是 TestClass 类的子类
        // 2. 具备 TestAnnotation 注解
        // 3. 自身不能是 TestClass 类
        var matcher = Matchers.subclassesOf(TestClass.class)
            .and(Matchers.annotatedWith(TestAnnotation.class))
            .and(Matchers.not(Matchers.is(TestClass.class)));

        // 在 ClassScannerTest 类型所在的包下进行查找
        var classes = ClassScanner.matching(matcher)
            .in(ClassScannerTest.class.getPackage());

        // 确认查找结果为 TestSubClass 类
        then(classes).containsExactly(TestSubClass.class);
    }

    /**
     * 测试 {@link ClassScanner#matching(Matcher)
     * ClassScanner#matching(Matcher)} 和 {@link ClassScanner#in(Package...)} 方法,
     * 通过指定的匹配器在指定包范围内查询类
     *
     * <p>
     * 测试查找文件系统中 {@code .jar} 文件中的 {@link Class} 对象
     * </p>
     */
    @Test
    void jar_shouldClassMatched() {
        // 定义匹配器, 查找 Matchers 类型
        var matcher = Matchers.is(Test.class);

        // 设置查询的包范围
        var classes = ClassScanner.matching(matcher)
            .in("org.hamcrest", "org.junit");

        // 确认查找结果
        then(classes).containsExactly(Test.class);
    }
}
