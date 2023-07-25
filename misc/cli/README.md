# PicoCli

- [PicoCli](#picocli)
  - [1. 打包后执行](#1-打包后执行)
    - [1.1. 打包](#11-打包)
    - [1.2. 执行](#12-执行)
  - [2. 通过 Maven 执行](#2-通过-maven-执行)
  - [3. 通过 Gradle 执行](#3-通过-gradle-执行)

文档参考: [https://picocli.info/](https://picocli.info/)

## 1. 打包后执行

### 1.1. 打包

- 通过 Maven 打包

  > 参考 [pom.xml](pom.xml) 打包文件

  ```bash
  mvn package -pl cli
  ```

  Maven 打包后的 jar 文件位于 `cli/target/cli.jar`

- 通过 Gradle 打包

  > 参考 [build.gradle](build.gradle) 打包文件

  ```bash
  ./gradlew :cli:fatJar
  ```

  Gradle 打包后的 jar 文件位于 `cli/build/libs/cli.jar`

### 1.2. 执行

对于已经打包好的 jar 文件, 通过以下命令执行即可

```bash
java -jar <path-to-package>/cli.jar
java -jar <path-to-package>/cli.jar echo -cblue -u Hello World
java -jar <path-to-package>/cli.jar datetime -d -t
```

## 2. 通过 Maven 执行

通过 Maven 的 `exec-maven-plugin` 插件, 可以直接执行程序

```bash
mvn exec:java -pl cli
mvn exec:java -Dexec.args="echo -cblue -u Hello World" -pl cli
mvn exec:java -Dexec.args="datetime -d -t" -pl cli
```

## 3. 通过 Gradle 执行

通过 Gradle 的 `exec` 任务, 可以直接执行程序

```bash
./gradlew :cli:exec
./gradlew :cli:exec --args="echo -cblue -u Hello World"
./gradlew :cli:exec --args="datetime -d -t"
```
