package alvin.study.springcloud.nacos.conf;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Jackson JSON 序列化配置
 *
 * <p>
 * Spring 的 {@code application.yml} 配置文件中提供了对 Jackson 框架的配置支持, 本类中会覆盖配置文件中的配置,
 * 达到和配置文件相同的效果. 参考: {@code application.yml} 中的 {@code spring.jackson} 配置部分
 * </p>
 */
@Configuration("conf/jackson")
public class JacksonConfig {
    /**
     * 设置 Jackson 功能开关
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer addCustomBigDecimalDeserialization() {
        return builder -> {
            // JSON 中不包含 null 值
            builder.serializationInclusion(Include.NON_NULL)
                    .featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                    // 遇到未知属性不抛出错误
                    .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    // 日期时间以字符串而不是 timestamp 输出
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    // 空类型不抛出错误
                    .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        };
    }
}
