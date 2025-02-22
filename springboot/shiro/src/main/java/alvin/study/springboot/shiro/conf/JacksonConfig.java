package alvin.study.springboot.shiro.conf;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Jackson 框架的配置类型
 */
@Configuration("conf/jackson")
public class JacksonConfig {
    /**
     * 设置 Jackson 序列化和反序列的配置
     *
     * <p>
     * 如果未在枚举指定字段上增加 {@link com.fasterxml.jackson.annotation.JsonValue @JsonValue}
     * 注解, 则需要配置
     * {@link org.springframework.http.converter.json.Jackson2ObjectMapperBuilder#featuresToEnable(SerializationFeature)
     * Jackson2ObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)}
     * 配置项
     * </p>
     *
     * @return 包含自定义配置的 Builder 对象
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializationInclusion(Include.NON_NULL)
                .featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                // 遇到未知属性不抛出错误
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 日期时间以字符串而不是 timestamp 输出
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 空类型不抛出错误
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
