# Study Java Project

## 1. 工具链

- JDK 17

  ```bash
  sudo apt install openjdk-17-jdk-headless
  ```

- Gradle 7.6

  - 从 [官网](https://gradle.org/next-steps/?version=7.6&format=bin) 下载安装包
  - 解压到 `/opt/gradle/gradle-7.6` 目录下
  - 建立一个表示当前版本的软链接:

    ```bash
    ln -s -d /opt/gradle/current /opt/gradle/gradle-7.6
    ```

  - 在 Shell 启动脚本中增加

    ```bash
    export GRADLE_HOME="/opt/gradle/current"
    export PATH="$GRADLE_HOME/bin:$PATH"
    ```

- Maven 3.8.7

  - 从 [官网](https://dlcdn.apache.org/maven/maven-3/3.8.7/binaries/apache-maven-3.8.7-bin.zip) 下载安装包
  - 解压到 `/opt/maven/apache-maven-3.8.7` 目录下
  - 建立一个表示当前版本的软链接

    ```bash
    sudo ln -s -d /opt/maven/current /opt/maven/apache-maven-3.8.7
    ```

  - 在 Shell 启动脚本中增加

    ```bash
    export MAVEN_HOME="/opt/maven/current"
    export PATH="$MAVEN_HOME/bin:$PATH"
    ```

## 2. VSCode 配置

### 2.1. Plugins

Java 开发插件

- Project Manager for Java
- Language Support for Java(TM) by Red Hat
- Debugger for Java
- Test Runner for Java
- Maven for Java
- Dependency Analytics
- Project Manager for Java
- IntelliCode

Gradle 工具插件

- Gradle for Java
- Gradle Language Support

Spring 插件

- Spring Initializr Java Support
- Spring Boot Tools
- Spring Boot Dashboard

数据库插件

- MybatisX

Graphql 插件

- GraphQL: Syntax Highlighting

Git 插件

- Git Graph
- Git History

远程开发插件

- WSL
- Dev Containers
- Remote - SSH
- Remote - SSH: Editing Configuration Files
- Remote Explorer

代码规范插件

- Code Spell Checker
- EditorConfig for VS Code
- SonarLint

Markdown 插件

- Markdown All in One
- Markdown Preview Enhanced

其他工具插件

- XML
- Java Properties
- Javadoc Tools
- IntelliJ IDEA Keybindings
- Local History

### 2.2. 配置文件

```json
{
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.rulers": [
      120
    ]
  },
  "java.codeGeneration.hashCodeEquals.useInstanceof": true,
  "java.codeGeneration.hashCodeEquals.useJava7Objects": true,
  "java.completion.favoriteStaticMembers": [
    "org.junit.Assert.*",
    "org.junit.Assume.*",
    "org.junit.jupiter.api.Assertions.*",
    "org.junit.jupiter.api.Assumptions.*",
    "org.junit.jupiter.api.DynamicContainer.*",
    "org.junit.jupiter.api.DynamicTest.*",
    "org.mockito.Mockito.*",
    "org.mockito.ArgumentMatchers.*",
    "org.mockito.Answers.*",
    "org.assertj.core.api.Assertions.*",
    "org.assertj.core.api.BDDAssertions.*"
  ],
  "java.eclipse.downloadSources": true,
  "java.format.settings.profile": "Eclipse",
  "java.format.settings.url": "eclipse-formatter.xml",
  "java.implementationsCodeLens.enabled": true,
  "java.maven.downloadSources": true,
  "java.referencesCodeLens.enabled": true,
  "redhat.telemetry.enabled": true,
  "sonarlint.rules": {
    "xml:S125": {
        "level": "off"
    },
    "java:S5853": {
        "level": "off"
    },
    "java:S3415": {
        "level": "off"
    },
    "java:S108": {
        "level": "off"
    },
    "java:S125": {
        "level": "off"
    },
    "java:S115": {
        "level": "off"
    },
    "java:S1700": {
        "level": "off"
    },
    "java:S2176": {
        "level": "off"
    },
    "java:S5663": {
        "level": "off"
    },
    "java:S1845": {
        "level": "off"
    }
  },
  "boot-java.rewrite.reconcile": true
}
```

## 2. 工程说明

### 2.1. 工程结构

Maven 工程结构

```plaintext
pom.xml                       -- 根工程 Maven 配置, 所有子工程都继承该配置, 所有依赖的版本号在此配置中定义
 ├─ pom-spring-boot2.xml      -- 支持 Spring Boot 2.x 的配置, 所有使用 Spring Boot 2.x 的工程继承此配置
 │   ├─ pom-spring-cloud.xml  -- 支持 Spring Cloud 2021.x 的配置, 所有使用 Spiring Cloud 的工程继承此配置
 │   │   ├─ cloud-nacos/pom.xml
 │   │   ├─ cloud-eureka/pom.xml
 │   │   └─ cloud-gateway/pom.xml
 │   ├─ boot-mybatis/pom.xml
 │   ├─ boot-security/pom.xml
 │   ├─ boot-shiro/pom.xml
 │   └─ boot-springdoc/pom.xml
 ├─ pom-spring-boot3.xml      -- 支持 Spring Boot 3.x 的配置, 所有使用 Spring Boot 3.x 的工程继承此配置
 │   ├─ boot-spel/pom.xml
 │   ├─ boot-validator/pom.xml
 │   ├─ boot-testing/pom.xml
 │   ├─ boot-aop/pom.xml
 │   ├─ boot-mvc/pom.xml
 │   ├─ boot-jpa/pom.xml
 │   ├─ boot-jooq/pom.xml
 │   ├─ boot-multi-ds/pom.xml
 │   └─ boot-dynamic-ds/pom.xml
 ├─ se/pom.xml
 ├─ cli/pom.xml
 ├─ guice/pom.xml
 ├─ jackson/pom.xml
 ├─ testing/pom.xml
 └─ security/pom.xml
```

Gradle 工程结构

```plaintext
gradle/depends-common.gradle
 ├─ gradle/depends-spring-boot.gradle
 │   ├─ boot-spel/build.gradle
 │   ├─ boot-validator/build.gradle
 │   ├─ boot-testing/build.gradle
 │   ├─ boot-aop/build.gradle
 │   ├─ boot-mvc/build.gradle
 │   ├─ boot-jpa/build.gradle
 │   ├─ boot-jooq/build.gradle
 │   ├─ boot-multi-ds/build.gradle
 │   ├─ boot-dynamic-ds/build.gradle
 │   ├─ boot-mybatis/build.gradle
 │   ├─ boot-security/build.gradle
 │   ├─ boot-shiro/build.gradle
 │   └─ boot-springdoc/build.gradle
 ├─ gradle/depends-spring-cloud.gradle
 │   ├─ cloud-nacos/build.gradle
 │   ├─ cloud-eureka/build.gradle
 │   └─ cloud-gateway/build.gradle
 ├─ se/build.gradle
 ├─ cli/build.gradle
 ├─ guice/build.gradle
 ├─ jackson/build.gradle
 ├─ testing/build.gradle
 └─ security/build.gradle
```

- `gradle/depends-common.gradle`, `gradle/depends-spring-boot.gradle` 和 `gradle/depends-spring-cloud.gradle` 定义了所需要的依赖
  - 在 `build.gradle` 中定义了根工程配置, 所有子工程均会遵守该定义
  - Spring 相关的依赖均定义在 `gradle/depends-spring-boot.gradle` 文件中, 通过 `mavenBom` 来定义要使用的 Spring Boot 版本号, 在子工程的 `build.gradle` 文件头部定义

    ```groovy
    // 引入 Spring Boot 依赖
    apply from: "${rootDir}/gradle/depends-spring-boot.gradle"

    // 引入 Spring Boot 2.x / 3.x Bom
    dependencyManagement {
      imports {
        mavenBom springboot2Bom  // 或 springboot3Bom
      }
    }

    // 其它配置
    ```

- 依赖的版本号在 `gradle.properties` 文件中定义
- 子工程在 `settings.gradle` 文件中定义

### 2.2. 工程说明

- `se`: Java SE 的一些代码范例
- `testing`: `JUnit` 和 `TestNG` 测试工具的使用范例
- `cli`: 通过 `picocli` 框架实现的命令行工具范例
- `jackson`: 通过 `Jackson` 框架实现的 JSON 序列号和反序列化范例
- `guice`: 通过 `Google Guice` 框架实现的 IoC 和 AoP 范例
- `security`: 和安全相关的代码范例, 包括: 加解密 (对称/非对称), 摘要签名, JWT 认证等
- `boot-spel`: Spring Expression Language (SpEL) 使用范例
- `boot-validator`: Spring (Hibernate) 验证框架使用范例
- `boot-testing`: Spring Boot 测试范例
- `boot-aop`: Spring AoP 框架使用范例
- `boot-mvc`: Spring MVC 框架使用范例
- `boot-jpa`: Spring JPA 持久化框架使用范例
- `boot-mybatis`: MyBatis-Plus 持久化框架使用范例
- `boot-jooq`: JOOQ 持久化框架使用范例
- `boot-multi-ds`: Spring 多数据源使用范例
- `boot-dynamic-ds`: Spring 动态数据源使用范例
- `boot-security`: Spring 安全框架使用范例
- `boot-shiro`: Shiro 安全框架使用范例
- `boot-springdoc`: Spring API 文档工具使用范例
- `cloud-nacos`: Spring Cloud Nacos 服务注册, 服务发现和配置中心使用范例
- `cloud-eureka`: Spring Cloud Eureka 服务注册, 服务发现使用范例
- `cloud-gateway`: Spring Cloud Gateway 网关服务使用范例


## 附录. 使用 jEnv 管理 JDK

### 1. 安装

#### 1.1. Linux / OS X

```bash
git clone https://github.com/jenv/jenv.git ~/.jenv
```

对于 macOS 也可以通过 Homebrew 进行安装

```bash
brew install jenv
```

#### 1.2. 配置

Bash

```bash
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
echo 'eval "$(jenv init -)"' >> ~/.bash_profile
```

Zsh

```bash
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
```

### 2. 使用

添加已安装的 JDK

``` bash
jenv add /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
  oracle64-1.6.0.39 added

jenv add /Library/Java/JavaVirtualMachines/jdk17011.jdk/Contents/Home
  oracle64-1.7.0.11 added
```

列出已添加的 JDK

```bash
jenv versions
  system
  oracle64-1.6.0.39
* oracle64-1.7.0.11 (set by /Users/xxx/.jenv/version)
```

配置全局 JDK 版本

```bash
jenv global oracle64-1.6.0.39
```

配置当前目录 JDK  版本

```bash
jenv local oracle64-1.6.0.39
```

配置当前 Shell JDK 版本

```bash
jenv shell oracle64-1.6.0.39
```

### 3. Windows 上使用

在 [此处](https://github.com/FelixSelter/JEnv-for-Windows) 下载 jEnv 的 Windows 版本, 执行其中的 `bat` 文件即可
