package alvin.study.springboot.aop.conf;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                new JavaTimeModule(),
                // 启用 JDK 8 模块
                new Jdk8Module())
            // 日期时间以字符串而不是 timestamp 输出
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 空类型不抛出错误
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // 遇到未知属性不抛出错误
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 重写枚举的 toString 方法, 针对于枚举中不包含标记为 @JsonValue 注解字段的情况
            .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
            // 是否允许通过 @JsonView 注解进行不同的序列化
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build()
            // JSON 中不包含 null 值
            .setSerializationInclusion(Include.NON_NULL);
    }
}
