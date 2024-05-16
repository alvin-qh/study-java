package alvin.study.testing.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

// @formatter:off

/**
 * 演示 junit 框架中注解的使用
 *
 * <p>
 * {@link TestMethodOrder @TestMethodOrder} 注解表示要启用按顺序执行测试, 此时测试方法上的 {@link Order @Order} 注解生效. 一般
 * 而言, 不应该给测试方法指定顺序, 而应该通过代码顺序组织测试结构
 * </p>
 */
@TestMethodOrder(OrderAnnotation.class)
class AnnotationTest {
    private static final List<String> TEST_LIST = new ArrayList<>();

    /**
     * 在所有测试方法执行前执行
     *
     * <p>
     * 相关的几个方法的执行顺序为:
     * <ol>
     * <li>
     * 在所有测试方法执行前, {@link BeforeAll @BeforeAll} 注解的方法会预先执行一次;
     * </li>
     * <li>
     * 在每次测试方法执行前, {@link BeforeEach @BeforeEach} 注解的方法会预先执行;
     * </li>
     * <li>
     * 在每个测试方法执行之后, {@link AfterEach @AfterEach} 注解的方法会被执行;
     * </li>
     * <li>
     * 在所有测试执行完毕后, {@link AfterAll @AfterAll} 注解的方法会被执行一次;
     * </li>
     * </ol>
     * </p>
     */
    @BeforeAll
    static void beforeAll() {
        TEST_LIST.add("beforeAll");
    }

    /**
     * 在所有测试方法执行完毕后执行一次
     *
     * <p>
     * 相关的几个方法的执行顺序为:
     * <ol>
     * <li>
     * 在所有测试方法执行前, {@link BeforeAll @BeforeAll} 注解的方法会预先执行一次;
     * </li>
     * <li>
     * 在每次测试方法执行前, {@link BeforeEach @BeforeEach} 注解的方法会预先执行;
     * </li>
     * <li>
     * 在每个测试方法执行之后, {@link AfterEach @AfterEach} 注解的方法会被执行;
     * </li>
     * <li>
     * 在所有测试执行完毕后, {@link AfterAll @AfterAll} 注解的方法会被执行一次;
     * </li>
     * </ol>
     * </p>
     */
    @AfterAll
    static void afterAll() {
        TEST_LIST.add("afterAll");

        assertEquals(
            TEST_LIST,
            List.of(
                "beforeAll",
                "beforeEach",
                "runTest1",
                "afterEach",
                "beforeEach",
                "runTest2",
                "afterEach",
                "afterAll"
            )
        );
    }

    /**
     * 在每个测试执行前执行
     *
     * <p>
     * 相关的几个方法的执行顺序为:
     * <ol>
     * <li>
     * 在所有测试方法执行前, {@link BeforeAll @BeforeAll} 注解的方法会预先执行一次;
     * </li>
     * <li>
     * 在每次测试方法执行前, {@link BeforeEach @BeforeEach} 注解的方法会预先执行;
     * </li>
     * <li>
     * 在每个测试方法执行之后, {@link AfterEach @AfterEach} 注解的方法会被执行;
     * </li>
     * <li>
     * 在所有测试执行完毕后, {@link AfterAll @AfterAll} 注解的方法会被执行一次;
     * </li>
     * </ol>
     * </p>
     */
    @BeforeEach
    void beforeEach() {
        TEST_LIST.add("beforeEach");
    }

    /**
     * 演示用于定义测试元数据的一组注解
     *
     * <p>
     * {@link org.junit.jupiter.api.Order @Order} 注解用于定义测试方法的执行顺序, 数值较小的会先被执行
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.DisplayName @DisplayName}
     * 注解用于定义测试报告中显示的当前测试方法的名字
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.RepeatedTest @RepeatedTest} 用于定义测试方法重复执行的次数.
     * 如果值为 {@code 1}, 表示重复 1 次, 加上原本要执行的 1 次, 该测试方法总共执行 2 次
     * </p>
     */
    @Order(1)
    @DisplayName("Running test")
    @RepeatedTest(1)
    void order1_shouldTestRunAtFirst() {
        TEST_LIST.add("runTest1");
    }

    /**
     * 演示一组用于定义测试方法执行前置条件的注解
     *
     * <p>
     * {@link org.junit.jupiter.api.condition.EnabledOnOs @EnabledOnOs},
     * {@link org.junit.jupiter.api.condition.DisabledOnOs @DisabledOnOs}
     * 注解用于定义允许或禁止执行当前测试方法执行的操作系统类型
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.condition.EnabledOnJre @EnabledOnJre},
     * {@link org.junit.jupiter.api.condition.DisabledOnJre @DisabledOnJre},
     * {@link org.junit.jupiter.api.condition.EnabledForJreRange @EnabledForJreRange},
     * {@link org.junit.jupiter.api.condition.DisabledForJreRange @DisabledForJreRange}
     * 注解用于定义允许或禁止执行当前测试方法的 JRE 环境信息
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.condition.EnabledIfSystemProperties @EnabledIfSystemProperties},
     * {@link org.junit.jupiter.api.condition.EnabledIfSystemProperty @EnabledIfSystemProperty}
     * {@link org.junit.jupiter.api.condition.DisabledIfSystemProperties @DisabledIfSystemProperties},
     * {@link org.junit.jupiter.api.condition.DisabledIfSystemProperty @DisabledIfSystemProperty}
     * 注解用于定义允许或禁止执行当前测试方法的虚拟机环境变量
     * </p>
     *
     * <p>
     * 设置虚拟机环境变量的途径一般包括
     * <ol>
     * <li>
     * 通过 {@link java.lang.System#setProperties(java.util.Properties)
     * System.setProperties(Properties)} 和
     * {@link java.lang.System#setProperty(String, String)
     * System.setProperty(String, String)} 方法
     * </li>
     * <li>
     * 通过 java 命令的 {@code -D} 参数, 即:
     * {@code java MainClass -D<property_name="value">}
     * </li>
     * <li>
     * 通过 gradle 命令的 {@code -D} 参数, 即:
     * {@code gradle <task_name> -D<property_name="value">}
     * </li>
     * <li>
     * 在 "gradle.properties" 文件中设置, 即: {@code systemProp.<property_name>=<"value">}
     * </li>
     * </ol>
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables @EnabledIfEnvironmentVariables},
     * {@link org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable @EnabledIfEnvironmentVariable},
     * {@link org.junit.jupiter.api.condition.DisabledIfEnvironmentVariables @DisabledIfEnvironmentVariables},
     * {@link org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable @DisabledIfEnvironmentVariable}
     * 注解用于定义允许或禁止执行当前测试方法的环境变量
     * </p>
     *
     * <p>
     * {@link org.junit.jupiter.api.condition.EnabledIf @EnabledIf},
     * {@link org.junit.jupiter.api.condition.DisabledIf @DisabledIf}
     * 注解用于通过设置自定义函数, 并根据自定义函数的返回值决定测试方法是否执行
     * </p>
     */
    @Test
    @Order(2)
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    @DisabledOnOs({ OS.WINDOWS })
    @EnabledOnJre({ JRE.JAVA_17 })
    @DisabledOnJre({ JRE.JAVA_8 })
    @EnabledForJreRange(min = JRE.JAVA_11, max = JRE.JAVA_17)
    @DisabledForJreRange(min = JRE.JAVA_8, max = JRE.JAVA_10)
    @EnabledIfSystemProperties({ @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*") })
    @DisabledIfSystemProperties({ @DisabledIfSystemProperty(named = "java.vm.name", matches = ".*HoSpot.*") })
    @EnabledIfEnvironmentVariables({ @EnabledIfEnvironmentVariable(named = "PATH", matches = ".*/bin.*") })
    // @DisabledIfEnvironmentVariables({ @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*") })
    @EnabledIf("checkTestEnabled")
    @DisabledIf("checkTestDisabled")
    void order2_shouldTestRunAtSecond() {
        TEST_LIST.add("runTest2");
    }

    /**
     * 该方法配合 {@link EnabledIf @EnabledIf} 注解使用
     *
     * <p>
     * 当前方法返回 {@code true} 时, {@link EnabledIf @EnabledIf} 注解允许测试执行
     * </p>
     *
     * @return {@code true} 表示允许测试执行
     */
    @SuppressWarnings("unused")
    private boolean checkTestEnabled() {
        return true;
    }

    /**
     * 该方法配合 {@link DisabledIf @DisabledIf} 注解使用
     *
     * <p>
     * 当前方法返回 {@code false} 时, {@link DisabledIf @DisabledIf} 注解允许测试执行
     * </p>
     *
     * @return {@code false} 表示允许测试执行
     */
    @SuppressWarnings("unused")
    private boolean checkTestDisabled() {
        return false;
    }

    /**
     * 不执行当前测试方法
     *
     * <p>
     * {@link Disabled @Disabled} 注解表示禁止当前测试方法执行
     * </p>
     */
    @Test
    @Disabled("for testing")
    void disable_shouldTestDisabled() {
        TEST_LIST.add("disabledTest");
    }

    /**
     * 在每个测试执行后执行
     *
     * <p>
     * 相关的几个方法的执行顺序为:
     * <ol>
     * <li>
     * 在所有测试方法执行前, {@link BeforeAll @BeforeAll} 注解的方法会预先执行一次;
     * </li>
     * <li>
     * 在每次测试方法执行前, {@link BeforeEach @BeforeEach} 注解的方法会预先执行;
     * </li>
     * <li>
     * 在每个测试方法执行之后, {@link AfterEach @AfterEach} 注解的方法会被执行;
     * </li>
     * <li>
     * 在所有测试执行完毕后, {@link AfterAll @AfterAll} 注解的方法会被执行一次;
     * </li>
     * </ol>
     * </p>
     */
    @AfterEach
    void afterEach() {
        TEST_LIST.add("afterEach");
    }
}
