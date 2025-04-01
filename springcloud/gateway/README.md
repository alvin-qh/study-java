# Spring Gateway 网关

- [Spring Gateway 网关](#spring-gateway-网关)
  - [1. 服务端](#1-服务端)
    - [1.1. 单节点配置](#11-单节点配置)
    - [1.2. 集群配置](#12-集群配置)
  - [2. 客户端](#2-客户端)
  - [3. 多 Profiles 配置](#3-多-profiles-配置)
    - [3.1. application.yml 文件](#31-applicationyml-文件)
    - [3.2. Java 注解](#32-java-注解)
    - [3.3. 启动时传递参数](#33-启动时传递参数)
  - [4. 启动本工程](#4-启动本工程)
  - [5. 测试用例](#5-测试用例)

## 1. 服务端

服务端用于接受客户端发起的服务注册, 并向客户端发送服务注册信息

### 1.1. 单节点配置

单节点配置需要在 `application.yml` 中设置:

- `server.port=<port>` 服务端口号
- `eureka.instance.hostname=<hostname>` 当前节点的域名
- `eureka.client.fetch-registry=false` 不获取服务注册信息
- `eureka.client.register-with-eureka=false` 不注册当前服务
- `eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/`
  当前服务域为 `http://本机域名:本机端口号/eureka`

### 1.2. 集群配置

集群配置需要在 `application.yml` 中设置:

- `server.port=<port>` 服务端口号
- `eureka.instance.hostname=<hostname>` 当前节点的域名
- `eureka.instance.prefer-ip-address=true` `true` 表示以 IP 注册节点, `false` 表示用域名注册节点 (
  此时 `eureka.instance.hostname` 必填). 如果在一台机器上启动多个节点, 则此配置必须为 `false`
- `eureka.client.fetch-registry=true` 或者省略此配置, 表示当前节点从其它节点获取注册信息
- `eureka.client.register-with-eureka=true` 或者省略此配置, 标识当前节点注册到集群
- `eureka.client.service-url.defaultZone=<节点1地址>,<节点2地址>,...,<节点n地址>` 配置为集群除本机外其它所有节点的地址

如果需要在本机启动多个服务节点, 则需要注意如下要点:

- 需要配置 `hosts` 文件 (如 `/etc/hosts`), 为本地 `127.0.0.1` 地址映射多个域名, 例如

    ```plaintext
    127.0.0.1  local-server-01
    127.0.0.1  local-server-02
    ...
    ```

- 需要将 `eureka.instance.prefer-ip-address` 配置设置为 `true`, 强行以域名方式注册服务而非 IP 地址

## 2. 客户端

客户端向服务端进行服务注册, 并接收服务端发送的服务信息

客户端配置需要在 `application.yml` 中设置:

- `eureka.instance.instance-id` 所注册的服务实例 id, 对于一个服务名注册多个服务实例的情形 (高可用 + 负载均衡), 需要设置该
  id
- `eureka.client.service-url.defaultZone=<所有节点地址>` 所有 Eureka 服务节点的地址, 以 `,` 分隔

注意, 一般情况下, 服务端需要引入 `org.springframework.cloud:spring-cloud-starter-netflix-eureka-server` 依赖,
而客户端需要引入 `org.springframework.cloud:spring-cloud-starter-netflix-eureka-client` 依赖. 但本例中服务端和客户端在同一个工程中,
所以不能引入 `org.springframework.cloud:spring-cloud-starter-netflix-eureka-client` 依赖, 否则会导致依赖冲突问题

## 3. 多 Profiles 配置

在本例中, 服务端和客户端的代码在同一个工程中, 所以需要用到 Spring 多配置文件来在不同情况下加载不同的配置, 执行不同部分的代码

### 3.1. application.yml 文件

通过 `spring.profiles.active` 配置来指定默认激活的配置项

可以通过两种方式处理多配置文件

1. 通过 `---` 分隔符

   通过在 `application.yml` 文件中加入 `---` 分隔符, 即可以将配置文件分割为多个部分,
   每个部分通过 `spring.config.activate.on-profile` 来指定当前部分属于哪个 profile

2. 通过多个配置文件

   通过 `application-<profile>.yml` 定义多个配置文件, 文件名的 `<profile>` 部分表明该配置文件属于哪个 profile

### 3.2. Java 注解

通过 `@Profile` 注解根据 profile 选择不同的配置类

```java
@Profile("<profile>")
@Configuration
public class ConfigOnGivenProfile {
    //...
}
```

### 3.3. 启动时传递参数

通过 Maven 启动项目时, 可以通过 `-Dspring-boot.run.arguments` 参数指定 Java 运行时参数, 例如:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

通过 Gradle 启动项目时, 可以通过 `--args` 参数指定 Java 运行时参数, 例如:

```bash
./gradlew bootRun --args='--server.port=8081'
```

## 4. 启动本工程

首先需要启动 Nacos 容器

```bash
docker compose -f springcloud/gateway/docker/docker-compose.yml up
```

其次启动其它服务

以 Maven 为例:

1. 启动应用服务集群

    ```bash
    mvn spring-boot:run \
        -Dspring-boot.run.arguments="--server.port=8081" \
        -pl springcloud/gateway/client

    mvn spring-boot:run \
        -Dspring-boot.run.arguments="--server.port=8082" \
        -pl springcloud/gateway/client
    ```

   此时在 `8081` 和 `8082` 端口各自启动了一个 Web 服务

2. 启动 Gateway 服务

    ```bash
    mvn spring-boot:run -pl springcloud/gateway/server
    ```

   此时在 `8080` 端口上启动了网关服务

3. 执行测试

    ```bash
    mvn test -pl springcloud/gateway/client
    ```

以 Gradle 为例

1. 启动应用服务集群

    ```bash
    gradle :springcloud:gateway:client:bootRun --args="--server.port=8081"

    gradle :springcloud:gateway:client:bootRun --args="--server.port=8082"
    ```

   此时在 `8081` 和 `8082` 端口各自启动了一个 Web 服务

2. 启动 Gateway 服务

    ```bash
    gradle :springcloud:gateway:server:bootRun
    ```

   此时在 `8080` 端口上启动了网关服务

3. 执行测试

    ```bash
    gradle :springcloud:gateway:client:test
    ```

所有服务启动后, 可以正确执行测试

## 5. 测试用例

启动所有服务后, 可以用下列请求进行测试

- 测试 Path 断言

  ```bash
  curl -L -X GET 'localhost:8080/backend/api/info' \
       -H 'Accept: application/json' | json_pp
  ```

- 测试自定义 JWT 断言

  <!-- cspell: disable -->
  ```bash
  curl -L -X GET 'localhost:8080/auth' \
    -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ0aGlyZC1wYXJ0Iiwic3ViIjoidV9iOTg5YzllYS1lMDdiLTQyNjMtYWU5OC0xMWQ2YjFiMGUzMjciLCJpc3MiOiJBbHZpbiIsInN1Yl91c2VyX3R5cGUiOiJlbXBsb3llZSIsImV4cCI6OTk5OTk5OTk5OSwic3ViX29yZ19jb2RlIjoib19hNGVmMzAiLCJpYXQiOjE1MDI5MzkxNzB9.JxVRbYIyAJisOwncRaisEvL8ge51HDhqfd45SfxLW2I' \
    -H 'Accept: application/json' | json_pp
  ```
  <!-- cspell: enable -->
