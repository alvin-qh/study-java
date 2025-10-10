package alvin.study.springboot.springdoc.conf;

import java.time.Instant;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import alvin.study.springboot.springdoc.core.http.ResponseWrapper;

/**
 * 配置 Spring Doc
 */
@Configuration("core/springdoc")
public class SpringDocConfig {
    /**
     * 创建 {@link OpenAPI} 对象
     *
     * <p>
     * {@link OpenAPI} 对象是 Spring Doc 的主对象, 用于对 API 页面的公共信息进行获取
     * </p>
     *
     * @return {@link OpenAPI} 类型对象
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                // 设置服务器信息
                .addServersItem(new Server()
                        // 添加服务器地址
                        .url("http://localhost:8080"))
                // 设置组件信息
                .components(new Components()
                        // 添加安全组件信息
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                // 认证方式
                                .type(SecurityScheme.Type.HTTP)
                                // 认证结构
                                .scheme("bearer")
                                // 认证格式
                                .bearerFormat("JWT")))
                // 设置 API 信息
                .info(new Info()
                        // 标题
                        .title("Spring Doc 测试文档")
                        // 描述
                        .description("测试 Spring Doc 文档生成")
                        // 版本号
                        .version("1.0.0")
                        // 版权信息
                        .license(new License()
                                // 版权声明
                                .name("Apache 2.0")
                                // 版权相关页面
                                .url("https://github.com/alvin-qh/study-java")))
                // 扩展信息
                .externalDocs(new ExternalDocumentation()
                        // 描述
                        .description("其它 Java 演示项目")
                        // 地址
                        .url("https://github.com/alvin-qh/study-java"));
    }

    /**
     * 文档分组设置, 设置 Auth 分组
     *
     * @param customizer 注入 {@link #openApiCustomiser()} 方法创建的对象
     * @return {@link GroupedOpenApi} 类型对象
     */
    @Bean
    GroupedOpenApi groupedOpenApiForAuth(@Qualifier("openApiCustomizer") OpenApiCustomizer customiser) {
        return GroupedOpenApi.builder()
                // 设置分组名称
                .group("Auth")
                // 设置分组对应的 URL 路径
                .pathsToMatch("/auth/**")
                // 设置分组内容的个性化处理对象
                .addOpenApiCustomizer(customiser)
                .build();
    }

    /**
     * 文档分组设置, 设置 API 分组
     *
     * @param customizer 注入 {@link #openApiCustomiser()} 方法创建的对象
     * @return {@link GroupedOpenApi} 类型对象
     */
    @Bean
    GroupedOpenApi groupedOpenApiForApi(@Qualifier("openApiCustomizer") OpenApiCustomizer customiser) {
        return GroupedOpenApi.builder()
                // 设置分组名称
                .group("API")
                // 设置分组对应的 URL 路径
                .pathsToMatch("/api/**")
                // 设置分组内容的个性化处理对象
                .addOpenApiCustomizer(customiser)
                .build();
    }

    /**
     * 创建文档分组内容个性化对象
     *
     * <p>
     * {@link OpenApiCustomiser} 对象用于对分组文档内容进行个性化, 参考
     * {@link GroupedOpenApi.Builder#addOpenApiCustomiser(OpenApiCustomiser)} 方法
     * </p>
     *
     * <p>
     * 由于 Spring Doc 框架本身也创建了该类型对象, 所以需要重新设置一个 Bean 的名称
     * </p>
     *
     * @return {@link OpenApiCustomiser} 对象
     */
    @Bean("openApiCustomizer")
    OpenApiCustomizer openApiCustomiser() {
        // 返回 OpenApiCustomiser 接口对象 (lambda 形式)
        return openApi -> openApi.getPaths().forEach((path, item) -> item
                // 读取所有操作
                .readOperations()
                // 遍历每一个操作, 进行个性化处理
                .forEach(operation -> customizeOperation(operation, path)));
    }

    /**
     * 创建文档内容个性化对象
     *
     * <p>
     * {@link OperationCustomizer} 对象用于对非分组文档内容进行个性化
     * </p>
     *
     * @return {@link OperationCustomizer} 对象
     */
    @Bean
    OperationCustomizer operationCustomizer() {
        // 返回 OperationCustomizer 接口对象 (lambda 形式), 对指定的操作进行个性化处理
        return (operation, _) -> customizeOperation(operation, "path");
    }

    /**
     * 对 {@link Operation} 对象进行个性化处理
     *
     * @param operation {@link Operation} 对象, 即一个 API 文档描述
     * @param path      API 的访问 URI
     * @return {@link Operation} 对象
     */
    private Operation customizeOperation(Operation operation, String path) {
        // 读取操作的所有响应, 并处理其中 200 响应的内容
        var content = operation.getResponses().get("200").getContent();
        // 遍历每个相应内容, 进行个性化处理
        content.forEach((_, mediaType) -> mediaType.schema(customizeSchema(mediaType.getSchema(), path)));
        return operation;
    }

    /**
     * 对 {@link Schema} 对象进行
     *
     * @param schema 响应结构对象
     * @param path   请求 URI
     * @return 个性化后的相应结构对象
     */
    private Schema<?> customizeSchema(Schema<?> schema, String path) {
        // 产生一个新结构对象
        var wrapperSchema = new Schema<>();
        // 增加包装属性
        wrapperSchema.addProperty("retCode", new IntegerSchema()._default(ResponseWrapper.SUCCESS_CODE));
        wrapperSchema.addProperty("retMsg", new StringSchema()._default(ResponseWrapper.SUCCESS_MESSAGE));
        // 将原本属性写入包装属性
        wrapperSchema.addProperty("payload", schema);
        // 增加包装属性
        wrapperSchema.addProperty("path", new StringSchema()._default(path));
        wrapperSchema.addProperty("timestamp", new ObjectSchema()._default(Instant.now()));
        return wrapperSchema;
    }
}
