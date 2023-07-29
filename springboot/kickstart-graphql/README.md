# Kickstart Graphql For Spring Boot

- [Kickstart Graphql For Spring Boot](#kickstart-graphql-for-spring-boot)
  - [1. 预览](#1-预览)
    - [1.1. 通过 Gradle 构建](#11-通过-gradle-构建)
    - [1.2. 通过 Maven 构建](#12-通过-maven-构建)
  - [2. 开启 Graphql Servlet](#2-开启-graphql-servlet)
  - [3. 开启 GraphQL 客户端](#3-开启-graphql-客户端)
    - [3.1. 开启 Graph*i*ql](#31-开启-graphiql)
    - [3.2. 开启 Altair](#32-开启-altair)
    - [3.3. 开启 GraphQL Playground](#33-开启-graphql-playground)
    - [3.3.3. 开启 GraphQL Voyager](#333-开启-graphql-voyager)
  - [4. 对 GraphQL-Java 库的支持](#4-对-graphql-java-库的支持)
    - [4.1. 通过 GraphQL Java Tools](#41-通过-graphql-java-tools)
    - [4.2. 通过 GraphQL 注解](#42-通过-graphql-注解)
      - [4.2.1. 根 resolvers, directives 和 type 扩展](#421-根-resolvers-directives-和-type-扩展)
      - [4.2.2. Interfaces](#422-interfaces)
      - [4.2.3. 自定义 scalars 和 type](#423-自定义-scalars-和-type)
      - [4.2.4. 自定义 Relay 和 GraphQL Annotation Processor](#424-自定义-relay-和-graphql-annotation-processor)
    - [4.3. 扩展 scalars](#43-扩展-scalars)
    - [4.4. scalars 别名](#44-scalars-别名)
  - [5. 指标分析和追踪](#5-指标分析和追踪)

Kickstart 的一些简单的范例可以参考 [Simples]([Metrics](https://github.com/graphql-java-kickstart/samples))

## 1. 预览

### 1.1. 通过 Gradle 构建

在 `gradle.properties` 文件中设置 Kotlin 版本

```properties
kotlin.version=1.3.70
```

在 `build.gradle` 文件中设置

```groovy
repositories {
  // 可以不设置
  maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}

repositories {
  mavenCentral()

  dependencies {
    implementation 'com.graphql-java-kickstart:graphql-spring-boot-starter:14.0.0'

    testImplementation 'com.graphql-java-kickstart:graphql-spring-boot-starter-test:14.0.0'
  }
}
```

### 1.2. 通过 Maven 构建

在 `pom.xml` 文件中设置 `<properties>` 值

```xml
<properties>
  <kotlin.version>1.3.70</kotlin.version>
</properties>
```

在 `pom.xml` 文件中添加依赖

```xml
<repositories>
  <!-- 可以不设置 -->
  <repository>
    <id>osshr-snapshots</id>
    <name>osshr-sonatype-snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.graphql-java-kickstart</groupId>
  <artifactId>graphql-spring-boot-starter</artifactId>
  <version>14.0.0</version>
</dependency>

<dependency>
  <groupId>com.graphql-java-kickstart</groupId>
  <artifactId>graphql-spring-boot-starter-test</artifactId>
  <version>14.0.0</version>
  <scope>test</scope>
</dependency>
```

## 2. 开启 Graphql Servlet

默认情况下, GraphQL Servlet 对外暴露的 URL 路径为 `/graphql`, 可以通过 Spring Boot 的 `application.yml` 配置该配置

```yml
graphql:
  servlet:
    # 开启 GraphQL Servlet (默认为 true)
    enabled: true
    # 设置 GraphQL Servlet 暴露的 URL 路径, 默认为 "/graphql"
    mapping: /graphql
    # 允许跨域访问
    cors-enabled: true
    cors:
      allowed-origins: http://some.domain.com
      allowed-methods: GET, HEAD, POST
    # 允许全局异常处理, 即通过 @ExceptionHandler 注解来处理自定义异常
    exception-handlers-enabled: true
    # 定义上下文对象的范围, 为每个请求创建上下文
    context-setting: PER_REQUEST_WITH_INSTRUMENTATION
    # 开启异步模式
    async-mode-enabled: true
```

默认情况下, 跨域访问是被允许的, 可以通过 `cors-enabled: false` 配置关闭

## 3. 开启 GraphQL 客户端

### 3.1. 开启 Graph*i*ql

如果在 `application.yml` 文件中配置了 `graphql.graphiql.enabled=true`, 则可以开启 Graph*i*QL 客户端, 默认的访问 URL
路径为 `/graphiql`

注意: 默认情况下, Graph*i*QL 访问 GraphQL servlet 暴露的路径为 `/graphql/*`, 可以通过配置修改

常用的 Spring Boot 配置 (`application.yml`) 如下:

```yml
graphql:
  graphiql:
    mapping: /graphiql
    endpoint:
      graphql: /graphql
      subscriptions: /subscriptions
    subscriptions:
      timeout: 30
      reconnect: false
    basePath: /
    enabled: true
    pageTitle: GraphiQL
    cdn:
      enabled: false
      version: latest
    props:
      resources:
        query: query.graphql
        defaultQuery: defaultQuery.graphql
        variables: variables.json
      variables:
        # 客户端主题样式
        editorTheme: solarized light
    headers:
      # 定义请求的 HTTP 头属性
      Authorization: Bearer <your-token>
```

### 3.2. 开启 Altair

如果在 `application.yml` 文件中配置了 `graphql.altair.enabled=true`, 则可以开启 Altair 客户端, 默认的访问 URL
路径为 `/altair`

注意: 默认情况下, Altair 访问 GraphQL Servlet 暴露的路径为 `/graphql/*`

常用的 Spring Boot 配置 (`application.yml`) 如下:

```yml
graphql:
  altair:
    enabled: true
    mapping: /altair
    subscriptions:
      timeout: 30
      reconnect: false
    static:
      base-path: /
    page-title: Altair
    cdn:
      enabled: false
      version: 4.0.2
    options:
      endpoint-url: /graphql
      subscriptions-endpoint: /subscriptions
      initial-settings:
        theme: dracula
      initial-headers:
        Authorization: Bearer <your-token>
    resources:
      initial-query: defaultQuery.graphql
      initial-variables: variables.graphql
      initial-pre-request-script: pre-request.graphql
      initial-post-request-script: post-request.graphql
```

默认情况下, Altair 从 `jar` 包的资源中读取静态文件, 可以改为通过 CDN 读取, 配置如下: `graphql.altair.cdn.enabled=true`

### 3.3. 开启 GraphQL Playground

如果在 `application.yml` 文件中配置了 `graphql.altair.enabled=true`, 则可以开启 Altair 客户端, 默认的访问 URL
路径为 `/playground`

常用的 Spring Boot 配置 (`application.yml`) 如下:

```yml
graphql:
  playground:
    mapping: /playground
    endpoint: /graphql
    subscriptionEndpoint: /subscriptions
    staticPath.base: my-playground-resources-folder
    enabled: true
    pageTitle: Playground
    cdn:
      enabled: false
      version: latest
    settings:
      editor.cursorShape: line
      editor.fontFamily: "'Source Code Pro', 'Consolas', 'Inconsolata', 'Droid Sans Mono', 'Monaco', monospace"
      editor.fontSize: 14
      editor.reuseHeaders: true
      editor.theme: dark
      general.betaUpdates: false
      prettier.printWidth: 80
      prettier.tabWidth: 2
      prettier.useTabs: false
      request.credentials: omit
      schema.polling.enable: true
      schema.polling.endpointFilter: "*localhost*"
      schema.polling.interval: 2000
      schema.disableComments: true
      tracing.hideTracingResponse: true
    headers:
      headerFor: AllTabs
    tabs:
      - name: Example Tab
        query: classpath:exampleQuery.graphql
        headers:
          SomeHeader: Some value
        variables: classpath:variables.json
        responses:
          - classpath:exampleResponse1.json
          - classpath:exampleResponse2.json
```

其常用配置如下:

`mapping`, `endpoint` 和 `subscriptionEndpoint` 的配置项分别默认为 `/playground`, `/graphql` 和 `/subscriptions`,
这些配置项不应该为空

`enabled` 配置项的默认值为 `true`, 也就是说 Playground 会在工程引入依赖库后默认生效

`pageTitle` 配置项的默认值为 `Playground`

`headers` 配置项允许设置指定的 HTTP 头属性 (例如 Token). 默认这些配置会在所有的页签上生效 (`AllTabs`)

CDN 配置项默认是关闭的

通过 `staticPath.base` 配置项可以设置静态资源文件的路径, 在该路径下可以包含如下静态资源文件

- `static/css/index.css`
- `static/js/middleware.js`
- `favicon.png`
- `logo.png`

参考 [GraphQL Playground Readme](https://github.com/graphql/graphql-playground#settings) 文档了解如何进行二次开发

### 3.3.3. 开启 GraphQL Voyager

如果在 `application.yml` 文件中配置了 `graphql.voyager.enabled=true`, 则可以开启 Voyager 客户端, 默认的访问 URL
路径为 `/voyager`

常用的 Spring Boot 配置 (`application.yml`) 如下:

```yml
graphql:
  voyager:
    enabled: true
    basePath: /
    mapping: /voyager
    endpoint: /graphql
    cdn:
      enabled: false
      version: latest
    pageTitle: Voyager
    displayOptions:
      skipRelay: true
      skipDeprecated: true
      rootType: Query
      sortByAlphabet: false
      showLeafFields: true
      hideRoot: false
    hideDocs: false
    hideSettings: false
```

`mapping` 和 `endpoint` 配置项分别默认为 `/voyager` 和 `/graphql`, 这些配置项不能为空

`enabled` 配置项默认为 `true`, 这意味着工程一旦引入依赖, Voyager 就默认开启

`pageTitle` 配置项默认为 `Voyager`

其它配置内容请参考 [GraphQL Voyager Readme](https://github.com/APIs-guru/graphql-voyager#properties)

可以通过 `displayOptions`, `hideDocs` 和 `hideSettings` 配置项对 Voyager 进行自定义配置,
具体方法可参考 [GraphQL Voyager Readme](https://github.com/APIs-guru/graphql-voyager#properties)

## 4. 对 GraphQL-Java 库的支持

### 4.1. 通过 GraphQL Java Tools

[https://github.com/graphql-java-kickstart/graphql-java-tools](https://github.com/graphql-java-kickstart/graphql-java-tools)

通过定义 `*.graphqls` 文件来描述 GraphQL 的 Schema, 包括 `Type`, `Scalar`, `Enum` 等并自动映射到对应的 Java 类型上,
具体参考 [Readme](https://github.com/graphql-java-kickstart/graphql-java-tools#usage) 文档

相关 `application.yml` 中的配置如下:

```yml
graphql:
  tools:
    file-extensions: graphqls
    schema-location-pattern: classpath*:graphql/**/*.graphqls
    # Enable or disable the introspection query. Disabling it puts your server in contravention of the GraphQL
    # specification and expectations of most clients, so use this option with caution
    introspection-enabled: true
```

### 4.2. 通过 GraphQL 注解

[https://github.com/Enigmatis/graphql-java-annotations](https://github.com/Enigmatis/graphql-java-annotations)

可以通过注解方式取代 GraphQL Java Tools 的方式, 需要设置 `graphql.schema-strategy=annotation`

相关 `application.yml` 中的配置如下:

```yml
graphql:
  annotations:
      base-package: alvin.study.schema.annotation # required
      always-prettify: true # true is the default value, no need to specify it
```

主要的配置项为 `base-package`, 它指定了需要解析注解的类型所在的包名

`always-prettify` 为 `true` 表示对生成的 GraphQL Schema 进行美化

#### 4.2.1. 根 resolvers, directives 和 type 扩展

根 Resolvers 必须标记 `@GraphQLQueryResolver`, `@GraphQLMutationResolver` 和 `GraphQLSubscription` 注解

每个类型只能标记一个注解, 不能组合使用

`@GraphQLDirectiveDefinition` 和 `@GraphQLTypeExtension` 注解也有类似限制

#### 4.2.2. Interfaces

至少有一个方法标记为 `@GraphQLField` 注解的接口被认为是 GraphQL interface, 并且这些接口的实现类会自动的加入 GraphQL
schema 中, 另外, 需要给 GraphQL interface 增加注解 `@GraphQLTypeResolver(GraphQLInterfaceTypeResolver.class)`

#### 4.2.3. 自定义 scalars 和 type

自定义 scalars 和使用 GraphQL Java Tools 一致, 只需要定义一个 `GraphQLScalarType` bean 即可

#### 4.2.4. 自定义 Relay 和 GraphQL Annotation Processor

可以定义一个实现了 `Relay` 和/或 `GraphQLAnnotations` 的 bean 类型, 并且 Spring 注入器可以在这些类型中工作

### 4.3. 扩展 scalars

[扩展 scalars](https://github.com/graphql-java/graphql-java-extended-scalars) 提供了对标准 GraphQL 类型的扩展,
可以通过 `application.yml` 文件的 `graphql.extended-scalars` 配置进行声明

```yml
graphql:
  extended-scalars: BigDecimal, Date
```

该扩展库目前提供了如下扩展 Scalars 类型:

`BigDecimal`, `BigInteger`, `Byte`, `Char`, `Date`, `DateTime`, `JSON`, `LocalTime` (since
13.0.0), `Locale`, `Long`, `NegativeFloat`, `NegativeInt`, `NonNegativeFloat`, `NonNegativeInt`, `NonPositiveFloat`, `NonPositiveInt`, `Object`, `PositiveFloat`, `PositiveInt`, `Short`, `Time`, `UUID` (
since 13.0.0), `Url`

该配置对 GraphQL Java Tools 和 GraphQL 注解两种方式均有效

对于使用 GraphQL Java Tools, 需要在 `graphqls` 文件中定义

```graphql
scalar BigDecimal
scalar Date
```

### 4.4. scalars 别名

可以通过 `application.yml` 文件配置 Scalars 的别名, 以表达更确切的含义

```yml
graphql:
  aliased-scalars:
    BigDecimal: Number, Decimal
    String: Text
```

## 5. 指标分析和追踪

[Apollo style tracing](https://github.com/graphql-java-kickstart/graphql-spring-boot#aliased-scalars:~:text=Apollo%20style%20tracing)

通过在 `application.yml` 中开启指标分析和追踪的选项, 将允许在查询每个字段时进行所需指标的追踪和分析

```yml
graphql:
  servlet:
    tracing-enabled: true
    actuator-metrics: true
```

指标包括如下配置项:

- `graphql.timer.query`
- `graphql.websocket.sessions`: Websocket 订阅产生的 Session 数量
- `graphql.websocket.subscriptions`: Websocket 的订阅数量

Actuators 指标追踪框架参考 [Baeldung Spring Boot Actuators](https://www.baeldung.com/spring-boot-actuators)
