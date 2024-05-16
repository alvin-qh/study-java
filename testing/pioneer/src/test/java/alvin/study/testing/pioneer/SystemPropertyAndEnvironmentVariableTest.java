package alvin.study.testing.pioneer;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.ClearEnvironmentVariable.ClearEnvironmentVariables;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.ClearSystemProperty.ClearSystemProperties;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable.SetEnvironmentVariables;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty.SetSystemProperties;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 在测试过程中设置系统属性和环境变量
 *
 * <p>
 * JVM 在启动时, 会读取指定的系统属性, 以 {@link java.util.Properties Properties} 对象存储, 可通过
 * <ul>
 * <li>{@link System#setProperty(String, String)}</li>
 * <li>{@link System#getProperty(String)}</li>
 * <li>{@link System#clearProperty(String)}</li>
 * <li>{@link System#getProperties()}</li>
 * <li>{@link System#setProperties(java.util.Properties) System.setProperties(Properties)}</li>
 * </ul>
 * 等一系列方法进行读取和设置
 * </p>
 *
 * <p>
 * 另外, JVM 也会读取当前设置的环境变量, 通过 {@link System#getenv()} 和 {@link System#getenv(String)} 方法进行读取 (JDK 不允许
 * 直接修改环境变量的值)
 * </p>
 *
 * <p>
 * 在测试过程中, 有时候需要临时 Mock 一些系统属性和环境变量的值, 但通过 Mockito 之类的库比较难以实现, 此时可以通过本例中提供的方法进行
 * </p>
 *
 * <p>
 * 下列注解用于在测试方法中临时修改系统属性的值, 包括:
 *
 * <ul>
 * <li>{@link SetSystemProperty @SetSystemProperty}, 临时设置一个系统属性</li>
 * <li>{@link ClearSystemProperty @ClearSystemProperty}, 临时删除一个系统属性</li>
 * <li>{@link SetSystemProperties @SetSystemProperties}, 临时批量设置一系列系统属性</li>
 * <li>{@link ClearSystemProperties @SetSystemProperties}, 临时批量设置一系列系统属性</li>
 * </ul
 * <p>
 *
 * <p>
 * Pioneer 通过反射修改 JVM 中存储的环境变量值. 自 JDK 17 开始, 无法通过反射修改 JDK 内部状态, 所以和<b>环境变量修改</b>相关的测试会失败,
 * 抛出如下异常:
 *
 * <div>
 * org.junit.jupiter.api.extension.ExtensionConfigurationException:
 * Cannot access Java runtime internals to modify environment variables.
 * Have a look at the documentation for possible solutions:
 * <a href="https://junit-pioneer.org/docs/environment-variables/#warnings-for-reflective-access">
 * https://junit-pioneer.org/docs/environment-variables/#warnings-for-reflective-access
 * </a>
 * </div>
 *
 * <div>
 * Java 认为环境变量不应当改变, 所以一般情况下不要使用 {@link ClearEnvironmentVariable @ClearEnvironmentVariable} 和
 * {@link SetEnvironmentVariable @SetEnvironmentVariable} 这一类注解, 应当在 IDE 或者测试启动参数中设置正确的环境变量,
 * 否则可能会引发未知的测试错误
 * </div>
 *
 * <div>
 * 为了让本例可以正确运行, 需要在 {@code jvmArgs} 参数中允许修改指定 Java 模块的 JDK 内部状态, 分以下几个环节修改:
 * </div>
 *
 * <ul>
 * <li>
 * 对于 IDE (以 vscode 为例), 修改测试 {@code jvmArgs}, 例如:
 *
 * <pre>
 * // 修改当前工程的 setting.json 文件, 修改如下配置
 * {
 *   "java.test.config": {
 *     "name": "JavaTest",
 *     "vmArgs": [
 *       "--add-opens='java.base/java.util=ALL-UNNAMED'",
 *       "--add-opens='java.base/java.lang=ALL-UNNAMED'"
 *     ]
 *   }
 * }
 * </pre>
 *
 * </li>
 * <li>
 * 对于 MAVEN 工具, 需修改 {@code pom.xml} 文件, 为 {@code maven-surefire-plugin} 插件增加 {@code <argLine>} 节点
 *
 * <pre>
 * // 修改 pom.xml 文件, 修改如下内容
 * &lt;build&gt;
 *   &lt;plugins&gt;
 *     &lt;plugin&gt;
 *       &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *       &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *       &lt;configuration&gt;
 *         &lt;argLine&gt;
 *           --add-opens="java.base/java.util=ALL-UNNAMED"
 *           --add-opens="java.base/java.lang=ALL-UNNAMED"
 *         &lt;/argLine&gt;
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 *   &lt;/plugins&gt;
 * &lt;/build&gt;
 * </pre>
 *
 * </li>
 * <li>
 * 对于 GRADLE 工具, 需修改 {@code build.gradle} 文件, 为 {@code test} 任务增加 {@code jvmArgs} 配置
 *
 * <pre>
 * // 修改 build.gradle 文件, 修改如下内容
 * test {
 *   jvmArgs "--add-opens", "java.base/java.util=ALL-UNNAMED",
 *           "--add-opens", "java.base/java.lang=ALL-UNNAMED"
 * }
 * </pre>
 *
 * </li>
 * </ul>
 * <p>
 * 至此, 可以确保通过测试
 * </p>
 */
class SystemPropertyAndEnvironmentVariableTest {
    /**
     * 测试获取原始系统属性值
     */
    @Test
    void originalSystemProperty_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getProperty("os.name")).isNotEmpty();
    }

    /**
     * 测试通过 {@link ClearSystemProperty @ClearSystemProperty} 注解删除系统属性值
     */
    @Test
    @ClearSystemProperty(key = "os.name")
    void clearSystemProperty_shouldValueIsNull() {
        // 确认该系统属性不存在
        then(System.getProperty("os.name")).isNull();
    }

    /**
     * 测试通过 {@link SetSystemProperty @SetSystemProperty} 注解设置新的系统属性值
     */
    @Test
    @SetSystemProperty(key = "os.name", value = "macOS")
    void setSystemProperty_shouldSetNewValue() {
        // 确认该系统属性值为新设置的值
        then(System.getProperty("os.name")).isEqualTo("macOS");
    }

    /**
     * 测试通过 {@link ClearSystemProperties @ClearSystemProperties} 注解批量删除指定的系统属性
     */
    @Test
    @ClearSystemProperties(value = { @ClearSystemProperty(key = "os.name"), @ClearSystemProperty(key = "user.name") })
    void clearSystemProperties_shouldValuesAreNull() {
        // 确认多个系统属性不存在
        then(System.getProperty("os.name")).isNull();
        then(System.getProperty("user.name")).isNull();
    }

    /**
     * 测试通过 {@link SetSystemProperties @SetSystemProperties} 注解批量设置新的系统属性值
     */
    @Test
    @SetSystemProperties(value = { @SetSystemProperty(key = "os.name", value = "macOS"), @SetSystemProperty(key = "user.name", value = "Alvin") })
    void setSystemProperties_shouldSetNewValues() {
        // 确认多个系统属性值为新设置的值
        then(System.getProperty("os.name")).isEqualTo("macOS");
        then(System.getProperty("user.name")).isEqualTo("Alvin");
    }

    /**
     * 测试获取原始环境变量值
     */
    @Test
    void originalEnvironmentVariable_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getenv("PATH")).isNotEmpty();
    }

    /**
     * 测试通过 {@link ClearEnvironmentVariable @ClearEnvironmentVariable} 注解删除指定环境变量值
     */
    @Test
    @ClearEnvironmentVariable(key = "PATH")
    void clearEnvironmentVariable_shouldValueIsNull() {
        // 确认指定的环境变量不存在
        then(System.getenv("PATH")).isNull();
    }

    /**
     * 测试通过 {@link SetEnvironmentVariable @SetEnvironmentVariable} 注解设置新的环境变量值
     */
    @Test
    @SetEnvironmentVariable(key = "PATH", value = "/opt/jvm/jdk-17")
    void setEnvironmentVariable_shouldSetNewValue() {
        // 确认环境变量为新设置的值
        then(System.getenv("PATH")).isEqualTo("/opt/jvm/jdk-17");
    }

    /**
     * 测试通过 {@link ClearEnvironmentVariables @ClearEnvironmentVariables} 注解批量删除指定环境变量值
     */
    @Test
    @ClearEnvironmentVariables({ @ClearEnvironmentVariable(key = "PATH"), @ClearEnvironmentVariable(key = "HOME") })
    void clearEnvironmentVariables_shouldValuesAreNull() {
        // 确认指定的环境变量不存在
        then(System.getenv("PATH")).isNull();
        then(System.getenv("HOME")).isNull();
    }

    /**
     * 测试通过 {@link SetEnvironmentVariables @SetEnvironmentVariables} 注解批量设置新的环境变量值
     */
    @Test
    @SetEnvironmentVariables({ @SetEnvironmentVariable(key = "PATH", value = "/opt/jvm/jdk-17"), @SetEnvironmentVariable(key = "HOME", value = "/home/alvin") })
    void setEnvironmentVariables_shouldSetNewValues() {
        // 确认环境变量为新设置的值
        then(System.getenv("PATH")).isEqualTo("/opt/jvm/jdk-17");
        then(System.getenv("HOME")).isEqualTo("/home/alvin");
    }
}
