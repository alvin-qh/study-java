package alvin.study.springboot.aop.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 框架的配置类型
 */
@Configuration("conf/jackson")
public class JacksonConfig {
    /**
     * 设置 Jackson 序列化和反序列的配置
     *
     * @return 包含自定义配置的 Builder 对象
     */
    @Bean
    ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModules(
                // 启用 Java 时间日期模块
                // new JavaTimeModule(),
                // 启用 JDK 8 模块
                // new Jdk8Module()
                )
                // 日期时间以字符串而不是 timestamp 输出
                // .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 空类型不抛出错误
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 遇到未知属性不抛出错误
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 重写枚举的 toString 方法, 针对于枚举中不包含标记为 @JsonValue 注解字段的情况
                // .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                // 是否允许通过 @JsonView 注解进行不同的序列化
                .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
    }
}
