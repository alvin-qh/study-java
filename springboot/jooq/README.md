# JOOQ For Java

- [JOOQ For Java](#jooq-for-java)
  - [1. 生成代码](#1-生成代码)
    - [1.1. Gradle 配置](#11-gradle-配置)
      - [1.1.1. 配置必要插件](#111-配置必要插件)
      - [1.1.2. 配置所需依赖](#112-配置所需依赖)
      - [1.1.3. 和 Spring Boot 集成](#113-和-spring-boot-集成)
      - [1.1.4. 配置 Flyway](#114-配置-flyway)
      - [1.1.5. 配置 JOOQ 代码生成](#115-配置-jooq-代码生成)
    - [1.2. Maven 配置](#12-maven-配置)
      - [1.2.1. 配置 JOOQ 依赖](#121-配置-jooq-依赖)
      - [1.2.2. 和 Spring Boot 集成](#122-和-spring-boot-集成)
      - [1.2.3. 配置 Flyway 插件](#123-配置-flyway-插件)
      - [1.2.4. 配置 JOOQ 插件](#124-配置-jooq-插件)
  - [2. 基本概念](#2-基本概念)
    - [2.1. POJO 和 Record 类型](#21-pojo-和-record-类型)
  - [其它](#其它)

## 1. 生成代码

JOOQ 相关代码 (Pojo, Record 和 Table 类型) 是通过 JOOQ Generator 生成的, 所以要使用 JOOQ, 需要先配置其代码生成相关的配置

### 1.1. Gradle 配置

在 `build.gradle` 文件中添加如下内容:

#### 1.1.1. 配置必要插件

```groovy
plugins {
  // Flyway DB Migration 插件
  id "org.flywaydb.flyway" version "${VER_GRADLE_FLYWAY}"
  // Jooq 代码生成器插件
  id "nu.studer.jooq" version "${VER_GRADLE_JOOQ}"
}
```

注意, 当前版本的 JOOQ 可能会和 `io.spring.dependency-management` 插件冲突, 所以不应引入此插件或将其屏蔽

```groovy
plugins {
  id "io.spring.dependency-management" version "${VER_GRADLE_SPRING_DEPENDENCY_MANAGEMENT}" apply false
}
```

#### 1.1.2. 配置所需依赖

```groovy
dependencies {
  implementation "org.jooq:jooq:${VER_JOOQ}",              // JOOQ 依赖
                 "com.h2database:h2:${VER_H2_DB}"          // 数据库驱动
  runtimeOnly    "org.flywaydb:flyway-core:${VER_GRADLE_FLYWAY}"  // Flyway 依赖
  jooqGenerator  "com.h2database:h2:${VER_H2_DB}"          // JOOQ 代码生成器的数据库驱动
}
```

#### 1.1.3. 和 Spring Boot 集成

当 JOOQ 需要和 Spring Boot 集成时, 首先需要将 `org.jooq:jooq`
依赖替换为 `org.springframework.boot:spring-boot-starter-jooq` 依赖

当 Spring Boot JOOQ 依赖和 Gradle 插件中 JOOQ 的版本不匹配时, 生成的代码和运行时可能会发生不兼容, 可以将 Spring Boot
JOOQ 依赖中的 JOOQ 排除, 单独引入高版本的 JOOQ 依赖

```groovy
dependencies {
  implementation dependencies.create("org.springframework.boot:spring-boot-starter-jooq:${VER_SPRING_BOOT}") {
    exclude group: "org.jooq"  // 排除 JOOQ 依赖
  },
  "org.jooq:jooq:${VER_JOOQ}"
}
```

或者在 `buildscript` 方法中将依赖中所有的 JOOQ 进行全局替换

```groovy
buildscript {
  configurations["classpath"].resolutionStrategy.eachDependency {
    if (requested.group == "org.jooq") {
      useVersion "${VER_JOOQ}"
    }
  }
}
```

#### 1.1.4. 配置 Flyway

```groovy
flyway {
  url       = "${DB_URL};MODE=MYSQL"
  user      = DB_USER
  password  = DB_PASSWORD
  // 数据库 migration 脚本位置
  locations = ["filesystem:${projectDir}/src/main/resources/migration"]
  table     = "schema_version"
}
```

此时通过如下命令即可进行数据库 Migration 操作

```bash
./gradlew :boot-jooq:flywayMigrate
```

#### 1.1.5. 配置 JOOQ 代码生成

配置 JOOQ 插件

```groovy
jooq {
  version = "${VER_JOOQ}"
  edition = nu.studer.gradle.jooq.JooqEdition.OSS

  // 配置 jooq 代码生成
  configurations {
    main {
      // 代码生成工具配置
      generationTool {
        logging = org.jooq.meta.jaxb.Logging.INFO   // 生成过程中日志级别

        // JDBC 连接属性
        jdbc {
          driver = "org.h2.Driver"
          url = DB_URL
          user = DB_USER
          password = DB_PASSWORD
        }

        // 配置生成器
        generator {
          name = "org.jooq.codegen.DefaultGenerator"  // 生成器实现类
          strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy" // 代码生成策略类

          // 配置要生成代码的数据库信息
          database {
            name = "org.jooq.meta.h2.H2Database"
            includes = "PUBLIC.*"
            excludes = "PUBLIC.schema_version | PUBLIC.flyway_schema_history"
          }

          // 配置生成规则
          generate {
            deprecated = false
            // 生成 Record 类型
            records = true
            fluentSetters = true
            // 生成 Pojo 类型
            pojos = true
            // 令 Pojo 类型必须通过构造器创建, 无 set 方法
            // immutablePojos = true
            pojosEqualsAndHashCode = true
            // 不约束 JOOQ 的版本号
            // jooqVersionReference = false
          }

          // 生成代码配置
          target {
            // 配置生成的代码所在的 java 包名称
            packageName = "alvin.study.infra.model"
            // 配置生成的的代码所在的路径
            directory = "src/jooq/java"
          }
        }
      }
    }
  }
}
```

配置 `generateJooq` 任务

```groovy
tasks.named("generateJooq").configure {
  // 自定义代码生成工具, 仅当出现错误时产生输出信息
  def outRef = new java.util.concurrent.atomic.AtomicReference<OutputStream>()
  javaExecSpec = { JavaExecSpec s ->
    outRef.set(new ByteArrayOutputStream())
    s.standardOutput = outRef.get()
    s.errorOutput = outRef.get()
    s.ignoreExitValue = true
  }
  execResultHandler = { ExecResult r ->
    def out = outRef.getAndSet(null)
    if (r.exitValue != 0) {
      throw new RuntimeException("jOOQ source code generation failed:\n\n" + out.toString())
    }
  }
}
```

此时通过如下代码可以生成 JOOQ 代码

```bash
./gradlew :boot-jooq:generateJooq
```

在本例中, 生成的代码位置为 `src/jooq/java`, 所以要将这部分路径加入 `sourceSet` 中

```groovy
sourceSets {
  main {
    java {
      srcDirs += ["src/jooq/java"]
    }
  }
}
```

### 1.2. Maven 配置

[`jooq-codegen-maven` 插件](https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration/)

#### 1.2.1. 配置 JOOQ 依赖

```xml
<dependency>
  <groupId>org.jooq</groupId>
  <artifactId>jooq</artifactId>
  <version>${version.jooq}</version>
</dependency>
```

#### 1.2.2. 和 Spring Boot 集成

当 JOOQ 需要和 Spring Boot 集成时, 首先需要将 `org.jooq:jooq`
依赖替换为 `org.springframework.boot:spring-boot-starter-jooq` 依赖

当 Spring Boot JOOQ 依赖和 Gradle 插件中 JOOQ 的版本不匹配时, 生成的代码和运行时可能会发生不兼容, 可以将 Spring Boot
JOOQ 依赖中的 JOOQ 排除, **单独引入高版本的 JOOQ 依赖**

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jooq</artifactId>
  <exclusions>
    <exclusion>
      <!-- 排除低版本 jooq 框架 -->
      <groupId>org.jooq</groupId>
      <artifactId>*</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

#### 1.2.3. 配置 Flyway 插件

引入依赖

```xml
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
  <version>${version.flyway}</version>
  <scope>runtime</scope>
</dependency>
```

配置插件

```xml
<plugin>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-maven-plugin</artifactId>
  <configuration>
    <url>${jdbc.url.h2}</url>
    <user>${jdbc.user.h2}</user>
    <password>${jdbc.password.h2}</password>
    <!-- 版本存储表名称 -->
    <table>schema_version</table>
    <!-- 数据库 migration 脚本文件位置 -->
    <locations>
      <location>filesystem:${project.basedir}/src/main/resources/migration</location>
    </locations>
  </configuration>
</plugin>
```

此时, 在 `boot-jooq` 目录下, 执行如下命令进行 migration 操作

```bash
mvn flyway:migrate
```

#### 1.2.4. 配置 JOOQ 插件

JOOQ Maven 插件的配置分为两种形式, 可以直接集成在 Maven 的 `pom.xml` 文件中, 也可以独立存在于一个 XML 文件中,
两种方式的内容大同小异

本例中以 Maven 集成的方式进行配置

```xml
<plugin>
  <groupId>org.jooq</groupId>
  <artifactId>jooq-codegen-maven</artifactId>
  <version>${version.maven-jooq}</version>
  <configuration>
    <jdbc>
      <url>${jdbc.url.h2}</url>
      <user>${jdbc.user.h2}</user>
      <password>${jdbc.password.h2}</password>
    </jdbc>
    <generator>
      <!-- 代码生成器类 -->
      <name>org.jooq.codegen.JavaGenerator</name>
      <!-- 代码生成策略类 -->
      <strategy>
        <name>org.jooq.codegen.DefaultGeneratorStrategy</name>
      </strategy>
      <!-- 数据库配置 -->
      <database>
        <name>org.jooq.meta.h2.H2Database</name>
        <!-- 要生成代码的 schema 集合 -->
        <includes>PUBLIC.*</includes>
        <!-- 不生成代码的 schema 集合 -->
        <excludes>PUBLIC.schema_version | PUBLIC.flyway_schema_history</excludes>
      </database>
      <!-- 生成规则配置 -->
      <generate>
        <deprecated>false</deprecated>
        <records>true</records>
        <fluentSetters>true</fluentSetters>
        <!-- 生成 pojo 类型 -->
        <pojos>true</pojos>
        <!-- 令 pojo 类型必须通过构造器创建, 无 set 方法 -->
        <!-- <immutablePojos>true</immutablePojos> -->
        <pojosEqualsAndHashCode>true</pojosEqualsAndHashCode>
        <!-- 生成校验注解 -->
        <!-- <validationAnnotations>true</validationAnnotations> -->
        <!-- 不约束 Jooq 的版本号 -->
        <!-- <jooqVersionReference>false</jooqVersionReference> -->
      </generate>
      <!-- 代码生成配置 -->
      <target>
        <!-- 配置生成代码所在的 java 包名称 -->
        <packageName>alvin.study.infra.model</packageName>
        <!-- 配置生成代码所在的路径 -->
        <directory>${project.basedir}/src/jooq/java</directory>
      </target>
    </generator>
  </configuration>
</plugin>
```

整个插件的配置 (`<configuration>` 标签) 分为两部分:

1. `<jdbc>` 标签, 配置 jdbc 驱动, 包括 URL, 用户名和密码等
2. `<generator>` 标签, 配置 jooq 代码生成器, 包括:

- `<name>` 代码生成器的实现类, 可省略
- `<strategy>` 代码生成策略的实现类, 可省略
- `<database>` 配置要生成代码的数据库, 包括:
  - `<name>` 数据库方言处理类
  - `<includes>` 要生成代码的表名称列表 (支持通配符)
  - `<excludes>` 不生成代码的表名称列表 (支持通配符)
- `<generate>` 配置生成器规则
- `<target>` 配置生成代码的规则, 包括代码的包名以及代码生成的位置

此时, 通过 JOOQ 插件的 `generate` goal 即可自动生成代码

```bash
mvn jooq-codegen:generate
```

插件中 JOOQ 代码生成的位置为 `/src/jooq/java`, 所以要将该路径设置为源码路径, 通过 mojo 插件配置如下

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>build-helper-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>add-source</id>
      <phase>generate-sources</phase>
      <goals>
        <goal>add-source</goal>
      </goals>
      <configuration>
        <sources>
          <!-- 设置额外的源码路径 -->
          <source>${project.basedir}/src/jooq/java</source>
        </sources>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## 2. 基本概念

完成上述配置后, 在 `src/main/resources/migration` 路径下添加 migration 文件 (
本例中为 `V20211201_1336__create_init_db.sql` 文件), 之后进行 migration 操作, 生成 JOOQ 代码

对于 Gradle, 命令如下

```bash
./gradlew :boot-jooq:flywayMigrate
./gradlew :boot-jooq:generateJooq
```

对于 Maven, 命令如下

```bash
mvn flyway:migrate
mvn jooq-codegen:generate
```

上述命令会在目标数据库中产生所需的表, 并在 `src/jooq/java` 路径下生成所需的代码, 代码结构如下

```plain
─ src/jooq/java
   └─ alvin.study.infra.model
       ├─ DefaultCatalog.java   # 默认的 JOOQ 配置信息类
       └─ public_               # 表示数据库的 schema
           ├─ Keys.java         # 键定义类, 包括所有的主键, 外键, 索引键 (唯一, 非唯一等)
           ├─ Public.java       # 名为 PUBLIC 的数据库 Schema 定义类
           ├─ Tables.java       # 所有数据表定义类
           └─ tables
               ├─ Department.java           # department 表定义
               ├─ DepartmentEmployee.java   # department_employee 表定义
               ├─ Employee.java             # employee 表定义
               ├─ pojos
               │   ├─ Department.java           # department 表对应的 pojo 类
               │   ├─ DepartmentEmployee.java   # department_employee 表对应的 pojo 类
               │   └─ Employee.java             # employee 表对应的 pojo 类
               └─ records
                   ├─ DepartmentRecord.java           # department 表对应的 record 类
                   ├─ DepartmentEmployeeRecord.java   # department_employee 表对应的 record 类
                   └─ EmployeeRecord.java             # employee 表对应的 record 类
```

### 2.1. POJO 和 Record 类型

当开启 JOOQ generate 的 `pojos` 选项后, 会产生和数据表对应的 POJO 类型, 该类型本质上是普通的 Java 类型,
具备无参构造器, `get/set` 方法, 可以用于作为数据传输的载体

当开启 JOOQ generate 的 `record` 选项后, 会产生和数据表对应的 Record 类型, 该类型相当于数据表的实体映射, 可以对实体进行*
*增删改查**操作

## 其它

对于 Gradle 工具来说, `generateJooq` 任务会在 `build` 任务之前自动执行, 但对于未修改表结构的情况下,
无需每次均执行 `generateJooq` 任务, 所以可以通过如下方式跳过该任务

```bash
./gradlew build -x generateJooq
./gradlew test -x generateJooq
```
