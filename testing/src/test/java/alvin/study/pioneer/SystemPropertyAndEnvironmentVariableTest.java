package alvin.study.pioneer;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty.SetSystemProperties;

/**
 *
 * <p>
 * 自 JDK 17 开始, 无法通过反射修改 JDK 内部状态, 所以和<b>环境变量修改</b>相关的测试会失败, 抛出如下异常
 *
 * <div>
 * org.junit.jupiter.api.extension.ExtensionConfigurationException:
 * Cannot access Java runtime internals to modify environment variables.
 * Have a look at the documentation for possible solutions:
 * https://junit-pioneer.org/docs/environment-variables/#warnings-for-reflective-access
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
 * </li>
 * <li>
 * 对于 MAVEN 工具, 需修改 {@code pom.xml} 文件, 为 {@code maven-surefire-plugin} 插件增加 {@code <argLine>} 节点
 *
 * <pre>
 * {@code
 * // 修改 pom.xml 文件, 修改如下内容
 * <build>
 *   <plugins>
 *     <plugin>
 *       <groupId>org.apache.maven.plugins</groupId>
 *       <artifactId>maven-surefire-plugin</artifactId>
 *       <configuration>
 *         <argLine>
 *           --add-opens="java.base/java.util=ALL-UNNAMED"
 *           --add-opens="java.base/java.lang=ALL-UNNAMED"
 *         </argLine>
 *       </configuration>
 *     </plugin>
 *   </plugins>
 * </build>
 * }
 * </pre>
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
 * </li>
 * </ul>
 *
 * 至此, 可以确保通过测试
 * </p>
 */
class SystemPropertyAndEnvironmentVariableTest {
    @Test
    void originalSystemProperty_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getProperty("os.name")).isNotEmpty();
    }

    @Test
    @ClearSystemProperty(key = "os.name")
    void clearSystemProperty_shouldValueIsNull() {
        // 确认该系统变量不为空
        then(System.getProperty("os.name")).isNull();
    }

    @Test
    @SetSystemProperty(key = "os.name", value = "macOS")
    void setSystemProperty_shouldSetFakeValue() {
        // 确认该系统变量不为空
        then(System.getProperty("os.name")).isEqualTo("macOS");
    }

    @Test
    @SetSystemProperties(value = {
        @SetSystemProperty(key = "os.name", value = "macOS"),
        @SetSystemProperty(key = "user.name", value = "Alvin")
    })
    void setSystemProperties_shouldSetFakeValues() {
        // 确认该系统变量不为空
        then(System.getProperty("os.name")).isEqualTo("macOS");
        then(System.getProperty("user.name")).isEqualTo("Alvin");
    }

    @Test
    void originalEnvironmentVariable_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getenv("PATH")).isNotEmpty();
    }

    @Test
    @ClearEnvironmentVariable(key = "PATH")
    void clearEnvironmentVariable_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getenv("PATH")).isNull();
    }

    @Test
    @SetEnvironmentVariable(key = "PATH", value = "/opt/jvm/jdk-17")
    void setEnvironmentVariable_shouldGetValue() {
        // 确认该系统变量不为空
        then(System.getenv("PATH")).isEqualTo("/opt/jvm/jdk-17");
    }
}
