# Nacos

- [Nacos](#nacos)
  - [1. Nacos 集群配置](#1-nacos-集群配置)
  - [2. Spring Cloud Starter Alibaba Nacos Config 版本差异](#2-spring-cloud-starter-alibaba-nacos-config-版本差异)
  - [3. 服务发现](#3-服务发现)
    - [3.1. 服务注册](#31-服务注册)
    - [3.2. 服务发现](#32-服务发现)

## 1. Nacos 集群配置

本例中采用 Docker 集群启动 Nacos, 有如下几个注意要点:

1. Nacos 集群最少需要 `3` 个节点

2. 初始化数据库
   初始化数据库需要创建 Schema (本例中为 `nacos_config`); 初始化数据表; 创建 `nacos` 用户

   整个初始化脚本在 [docker/sql/mysql-schema.sql](docker/sql/mysql-schema.sql) 文件中定义, 并映射到 Percona
   镜像的 `/docker-entrypoint-initdb.d` 目录中, 参见 [docker/docker-compose.yml](docker/docker-compose.yml) 中 `percona`
   配置

3. Nginx 配置
   Nacos 集群可以通过客户端进行负载均衡, 在客户端的 `application.yml` 中设置 `server-addr` 集群地址, 并用逗号分隔即可:

    ```yml
    spring:
      cloud:
        nacos:
          server-addr: http://host1:8848,http://host2:8848,http://host3:8848
    ```

   本例中为了简化设置, 减少单机上对外暴露的端口数量, 采用 Nginx 作为负载均衡

   Nacos 对外暴露 3 个端口, 默认为:

  - `8848`: Web 管理端口
  - `9848,9849`: RPC 访问端口, 必须为 Web 管理端口 (如`8848`) 分别加上 `1000` 和 `1001`, 否则 Nacos 无法正确访问

   所以 Nginx 需要全部代理这三个端口, 其中:

  - `8848` 端口需要通过 `http` 协议进行代理
  - `8849,9849` 端口需要通过 `stream` 协议进行代理 (TCP)

   具体配置参见 [docker/conf/nginx.conf](docker/conf/nginx.conf) 配置

## 2. Spring Cloud Starter Alibaba Nacos Config 版本差异

在 Spring Cloud `2021.0.0` 之前版本 (对应 `spring-cloud-starter-alibaba-nacos-config: 2021.1` 及之前版本),
需要通过 `spring-cloud-starter-bootstrap` 进行启动应用程序, Nacos 客户端的配置需要写在项目的 `classpath:bootstrap.yml`
配置文件中

在 Spring Cloud `2021.0.0` 之后版本 (对应 `spring-cloud-starter-alibaba-nacos-config: 2021.0.4.0` 及之后版本), Spring
Cloud 放弃了通过 `spring-cloud-starter-bootstrap` 启动应用程序, 需要通过如下方式进行处理:

> 注意, Nacos 在 `2021.x` 版本系列改过一次版本号, 即 `2021.1` 是第一个版本, 之后依次为 `2021.0.1.0` 和 `2021.0.4.0`.
> 参见 [版本说明](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

- 方法 1: 在应用程序中加入 `org.springframework.cloud：spring-cloud-starter-bootstrap` 依赖,
  此时原配置 `classpath:bootstrap.yml` 文件可以继续生效

- 方法 2: 将配置写入 `classpath:application.yml` 配置文件中

`spring-cloud-starter-alibaba-nacos-config: 2021.1` 之前版本, 需要在 `classpath:bootstrap.yml` 配置文件中配置如下内容:

```yml
spring:
  cloud:
    nacos:
      server-addr: http://localhost:8848
      username: nacos
      password: nacos
      config:
        namespace: 25935fce-b849-4882-861a-922232c1e39e
        prefix: alvin-study-config
        file-extension: yml
        group: STUDY_CONFIG
```

- `spring.cloud.nacos.config.namespace` 用于指定当前客户端要获取 Nacos 配置的命名空间, 缺省为 `public` 公共命名空间
- `spring.cloud.nacos.config.group` 表示要访问的 Nacos 配置分组, 缺省为 `DEFAULT_GROUP` 分组

需要在 `classpath:application.yml` 配置文件中配置如下内容:

```yml
spring:
  application:
    name: application-name
  config:
    activate:
      on-profile: dev
```

- `spring.cloud.nacos.config.prefix` 是要获取配置 `dataId` 的前缀, 如果为空, 则使用 `spring.application.name` 来替代
- `spring.cloud.nacos.config.file-extension` 是要获取配置 `dataId` 的后缀, 可以为 `properties`, `yaml`, `yml`, `json`
  和 `xml`
- `spring.config.activate.on-profile` 是非必要配置, 如果设置了, 会加入到 `dataId` 中

因此整个 `dataId`
的组成为 `${spring.cloud.nacos.config.prefix}-${spring.config.activate.on-profile}.${spring.cloud.nacos.config.file-extension}`

除了默认的 `dataId` 以外, 还可以通过 `spring.cloud.nacos.config.extension-configs` 配置来指定额外加载的 `dataId`, 即:

```yml
spring:
  cloud:
    nacos:
      server-addr: http://localhost:8848
      username: nacos
      password: nacos
      config:
        namespace: 25935fce-b849-4882-861a-922232c1e39e
        prefix: alvin-study-config
        file-extension: yml
        group: STUDY_CONFIG
      extension-configs:
        - data-id: ext-1-config.yaml
          group: EXT_STUDY_CONFIG
          refresh: false
        - data-id: ext-2-config.yaml
          group: EXT_STUDY_CONFIG
```

> 这种配置方式仍属于旧方式, 需要写入 `classpath:bootstrap.yml` 配置文件中

在 `2021.0.4.0` 及其之后版本中, Nacos 支持了 SpringCloud 的 `spring.config.import` 方式指定配置,
具体格式为 `[optional]:nacos:<dataId>.<file_extension>`, 如果要覆盖 `spring.cloud.nacos.config.group` 配置,
则需要加入 `[optional]:nacos:<dataId>.<file_extension>?group=<GROUP_NAME>` 参数, 另外还有 `refreshEnabled=<true|false>`
参数, 具体配置参见 [src/main/resources/application.yml](src/main/resources/application.yml) 配置

## 3. 服务发现

### 3.1. 服务注册

只要工程中加入了 `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery` 依赖并具备如下配置, 即启用了服务注册,
当应用程序启动时, 会自动以当前应用程序的 `spring.application.name` 配置作为**服务名** 注册到 Nacos

`application.yml` (或 `bootstrap.yml`) 配置如下:

```yml
spring:
  application:
    name: alvin-study-spring-cloud-nacos
  cloud:
    nacos:
      server-addr: http://localhost:8848
      username: nacos
      password: nacos
      discovery:
        namespace: 25935fce-b849-4882-861a-922232c1e39e
        group: STUDY_DISCOVERY
```

### 3.2. 服务发现

在工程中引入 `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery` 依赖并具备如下配置, 即启用了**服务发现**,
只需在应用程序中增加如下代码即可

`application.yml` (或 `bootstrap.yml`) 配置如下:

```yml
spring:
  application:
    name: alvin-study-spring-cloud-nacos
  cloud:
    nacos:
      server-addr: http://localhost:8848
      username: nacos
      password: nacos
      discovery:
        namespace: 25935fce-b849-4882-861a-922232c1e39e
        group: STUDY_DISCOVERY
```

代码如下:

```java
@Configuration("conf/nacos")
@EnableDiscoveryClient
public class NacosConfig {
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

这样即可通过产生的 `RestTemplate` 对象使用服务发现, 本例中, 可以不关心服务所在的 URL 等信息, 直接使用服务名访问服务, 例如

```java
public class DemoService {
    @Autowired
    private RestTemplate restTemplate;

    public Config callServer() throws NacosException {
        return restTemplate.getForObject(
                "http://alvin-study-spring-cloud-nacos/api/config",
                Config.class);
    }
}
```

如果无法正确访问服务发现, 检查是否依赖的库不全, 增加 `org.springframework.cloud:spring-cloud-starter-loadbalancer`
依赖后在此尝试
