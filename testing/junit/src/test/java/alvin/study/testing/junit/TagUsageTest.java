package alvin.study.testing.junit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import alvin.study.testing.junit.tag.ImportantTagTest;

/**
 * 演示在测试中使用 Tag
 *
 * <p>
 * 在命令行中使用 Tag:
 *
 * <pre>
 * mvn test -Dgroups="tag_test"
 *
 * ./gradlew test -DincludeTags='tag_test' -DexcludeTags='common_test'
 * </pre>
 * </p>
 *
 * <p>
 * 在 Maven Surefire 插件中使用
 *
 * <pre>
 * &lt;build&gt;
 *   &lt;plugins&gt;
 *     &lt;plugin&gt;
 *       &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *       &lt;version&gt;x.x.x&lt;/version&gt;
 *       &lt;configuration&gt;
 *         &lt;!-- 要执行的 Tag --&gt;
 *         &lt;groups&gt;tag_test&lt;/groups&gt;
 *         &lt;!-- 不执行的 Tag --&gt;
 *         &lt;excludedGroups&gt;common_test&lt;/excludedGroups&gt;
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 *   &lt;/plugins&gt;
 * &lt;/build&gt;
 * </pre>
 * </p>
 *
 * <p>
 * 在 Gradle 中使用
 *
 * <pre>
 * test {
 *   useJUnitPlatform {
 *     includeTags "tag_test | important"
 *     excludeTags "common_test"
 *   }
 * }
 * </pre>
 * </p>
 *
 * <p>
 * 在 Test Suite 中使用
 *
 * <pre>
 * &#064;Suite
 * &#64;SelectPackages("alvin.study")
 * &#64;IncludeTags("production")
 * class TestSuite {}
 * </pre>
 * </p>
 *
 * <p>
 * 标签表达式
 *
 * <ul>
 * <li>
 * {@code &} 同时具备多个标签时执行测试, 例如: {@code includeTags "tag_test & important"}
 * </li>
 * <li>
 * {@code !} 不具备某个标签时执行测试, 例如: {@code includeTags "!tag_test"}
 * </li>
 * <li>
 * {@code |} 具备多个标签的任意一个时执行测试, 例如: {@code includeTags "tag_test | important"}
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 也可以通过注解简化测试标签, 参考 {@link ImportantTagTest @ImportantTagTest} 注解
 * </p>
 */
@Tags({
    @Tag("tag_test"),
    @Tag("common_test")
})
class TagUsageTest {
    /**
     * 通过标记指定要执行的测试
     */
    @Test
    @Tag("important")
    void useTag_shouldIncludeImportantTagForTest() {
        System.out.println("important test tag included");
    }

    /**
     * 通过标记指定要执行的测试
     */
    @Test
    @Tag("optional")
    void useTag_shouldIncludeOptionalTagForTest() {
        System.out.println("optional test tag included");
    }

    /**
     * 通过注解指定要执行的测试
     *
     * <p>
     * 参考 {@link ImportantTagTest @ImportantTagTest} 注解定义
     * </p>
     */
    @ImportantTagTest
    void useAnnotation_shouldIncludeImportantTestWithAnnotation() {
        System.out.println("important test annotation included");
    }
}
