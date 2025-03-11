# AssertJ 代码生成插件

为每个类型书写 AssertJ 断言类较为繁琐, 所以 AssertJ 提供了自动生成断言类代码的功能

## 1. Maven 插件

完整的插件配置类型

```xml
<plugin>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-assertions-generator-maven-plugin</artifactId>
  <version>2.2.0</version>

  <!-- 定义插件运行, 在每次构建时生成断言类 -->
  <executions>
    <execution>
      <goals>
        <goal>generate-assertions</goal>
      </goals>
    </execution>
  </executions>

  <configuration>
    <!-- 为下列包中的类型生成断言类 -->
    <packages>
      <param>org.assertj.examples.rpg</param>
      <param>org.assertj.examples.data</param>
      <param>com.google.common.net</param>
    </packages>

    <!-- 为下列类生成断言类 -->
    <classes>
      <param>java.nio.file.Path</param>
      <param>com.fake.UnknownClass</param>
    </classes>

    <!-- 生成的断言类是否支持继承 -->
    <hierarchical>true</hierarchical>

    <!-- 是否为所有字段生成断言方法, 默认为 false, 只为 public 字段生成断言方法 -->
    <generateAssertionsForAllFields>true</generateAssertionsForAllFields>

    <!-- 指定断言入口类所在的包名, 默认和断言类在同一个包中 -->
    <entryPointClassPackage>org.assertj</entryPointClassPackage>

    <!-- 指定要生成断言类型的正则表达式, 符合的类型都会生成断言类 -->
    <includes>
      <param>org\.assertj\.examples\.rpg.*</param>
    </includes>

    <!-- 指定不生成断言类型的正则表达式, 符合的类型会被排除, 不生成断言类 -->
    <excludes>
      <param>.*google.*HostSpecifier</param>
      <param>.*google.*Headers</param>
      <param>.*google.*MediaType</param>
      <param>.*google.*Escaper.*</param>
      <param>.*Examples.*</param>
    </excludes>

    <!-- 指定生成的断言类代码的目录 -->
    <targetDir>src/test/generated-assertions</targetDir>

    <!-- 如果为 true, 则每次生成代码前先情况目标目录, 默认为 false -->
    <cleanTargetDir>true</cleanTargetDir>

    <!-- 要生成的断言类所在的包名, 默认和被断言类的报名一致 -->
    <generateAssertionsInPackage>my.assertions</generateAssertionsInPackage>

    <!-- 指定要生成哪些形式的断言类 -->
    <generateAssertions>true</generateAssertions>
    <generateBddAssertions>true</generateBddAssertions>
    <generateSoftAssertions>true</generateSoftAssertions>
    <generateJUnitSoftAssertions>true</generateJUnitSoftAssertions>

    <!-- 设置生成代码的范围, 可以为 'compile' 和 'test', 默认为 'test' -->
    <generatedSourcesScope>test</generatedSourcesScope>

    <!-- 如果为 true, 则不产生任何 log 输出, 默认为 false -->
    <quiet>true</quiet>

    <!-- 将生成代码的报告写入文件, 默认不写入 -->
    <writeReportInFile>assertions-generation-report.txt</writeReportInFile>

    <!-- 如果为 true, 则当前插件失效 -->
    <skip>false</skip>
  </configuration>
</plugin>
```

参考本例的 [pom.xml](./pom.xml) 文件

通过如下命令执行插件, 自动生成断言代码

```bash
mvn assertj:generate-assertions
```

也可以通过将 `generate-assertions` 这个 goal 绑定到某个构建阶段, 在构建时自动生成, 本例绑定了 `generate-test-sources`
构建阶段, 所以可以通过如下代码进行生成

```bash
mvn generate-test-sources
```

如果代码没有生成在 `src/java` 路径下, 则要通过 `build-helper-maven-plugin` 插件设置源码路径:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>build-helper-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>add-test-source</id>
      <!-- 指定当前执行要附加到的 phase -->
      <phase>generate-test-sources</phase>
      <!-- 指定要执行的 goal, 即在 generate-test-sources 阶段执行 add-test-source 任务 -->
      <goals>
        <goal>add-test-source</goal>
      </goals>
      <configuration>
        <sources>
          <!-- 设置额外的源码路径 -->
          <source>${project.basedir}/src/test/assertj/</source>
        </sources>
      </configuration>
    </execution>
  </executions>
</plugin>
```

这样就可以通过 `mvn test` 命令, 在测试执行前生成代码

## 2. Gradle 插件

通过如下 `id` 引入 Gradle 插件

```groovy
plugins {
  id "com.waftex.assertj-generator" version "1.1.4"
}
```

在 `assertjGenerator` 任务中进行配置

```groovy
assertjGenerator {
  ...
}
```

可配置的属性包括:

| 属性                  | 类型                         | 默认值                                    | 说明                         |
| --------------------- | ---------------------------- | ----------------------------------------- | ---------------------------- |
| `classOrPackageNames` | `String[]`                   | `[]`                                      | 被生成断言类的类名或包名     |
| `entryPointPackage`   | `String`                     | `null`                                    | 断言入口类的包名             |
| `outputDir`           | `Object`                     | `src/[testSourceSet.name]/generated-java` | 要生成代码的路径             |
| `sourceSet`           | `SourceSet`                  | `sourceSets.main`                         | 被断言类所在的 SourceSet     |
| `testSourceSet`       | `SourceSet`                  | `sourceSets.test`                         | 生成断言类所在的 SourceSet   |
| `entryPointTypes`     | `AssertionsEntryPointType[]` | `["STANDARD"]`                            | 要生成的断言类的类型         |
| `entryPointInherits`  | `boolean`                    | `true`                                    | 生成断言类是否支持继承       |
| `cleanOutputDir`      | `boolean`                    | `true`                                    | 在生成代码前是否情况目标目录 |

参考本例的 [build.gradle](./build~.gradle) 文件

通过 `mvn assertj:generate-assertions` 命令执行插件, 生成代码

如果代码没有生成在 `src/java` 路径下, 则要将生成代码的路径合并到 SourceSet 中:

```groovy
sourceSets {
  test {
    java {
      srcDirs += ["src/test/assertj"]
    }
  }
}
```

通过 `./gradlew generateAssertions` 命令执行插件, 生成代码
通过 `./gradlew test` 执行测试前, 会自动生成代码
