package alvin.study.springboot.kickstart.conf;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 配置对象之间相互 mapping 的配置类
 */
@Configuration("conf/modelmapper")
public class MapperConfig {
    /**
     * 产生 {@link ModelMapper} 实例对象
     *
     * @return {@link ModelMapper} 实例对象
     */
    @Bean
    ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        // 获取配置对象并进行设置
        modelMapper.getConfiguration()
                // 通过字段进行映射, 这种方式无需类型具备 setter 方法
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                // .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setAmbiguityIgnored(true)
                // 增加名称转换规则
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        modelMapper.addConverter(new AbstractConverter<Instant, OffsetDateTime>() {
            /**
             * 将 {@link Instant} 类型对象转为 {@link OffsetDateTime} 类型对象
             *
             * @param source 源对象的 {@link Instant} 类型字段值
             * @return 目标对象的 {@link OffsetDateTime} 类型字段值
             */
            @Override
            protected OffsetDateTime convert(Instant source) {
                return source.atOffset(ZoneOffset.UTC);
            }
        });

        return modelMapper;
    }
}
