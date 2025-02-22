package alvin.study.springboot.mybatis.conf;

import java.sql.Timestamp;
import java.time.Instant;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTokenizers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置对象之间相互 mapping 的配置类
 */
@Configuration("core/modelmapper")
public class ModelMapperConfig {
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
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true)
                // 增加名称转换规则
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        // 添加特殊的转换器
        modelMapper.addConverter(new AbstractConverter<Timestamp, Instant>() {
            /**
             * 将 {@link Timestamp} 类型对象转为 {@link Instant} 类型对象
             *
             * @param source 源对象的 {@link Timestamp} 类型字段值
             * @return 目标对象的 {@link Instant} 类型字段值
             */
            @Override
            protected Instant convert(Timestamp source) {
                return source.toInstant();
            }
        });

        return modelMapper;
    }
}
