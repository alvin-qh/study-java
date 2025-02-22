package alvin.study.springboot.security.conf;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTokenizers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import alvin.study.springboot.security.infra.entity.Permission;
import alvin.study.springboot.security.infra.entity.Role;

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

        // 添加特殊类型转换器
        modelMapper.addConverter(new AbstractConverter<Role, String>() {
            @Override
            protected String convert(Role source) {
                return source == null ? "" : source.getName();
            }
        });

        // 添加特殊类型转换器
        modelMapper.addConverter(new AbstractConverter<Permission, String>() {
            @Override
            protected String convert(Permission source) {
                return source == null ? "" : source.getPermission();
            }
        });

        return modelMapper;
    }
}
