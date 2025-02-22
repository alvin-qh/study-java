package alvin.study.springboot.jooq.conf;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;

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
        // 实例化对象
        var modelMapper = new ModelMapper();
        // 配置对象
        modelMapper.getConfiguration()
                // 增加 JOOQ 相关配置
                .addValueReader(new RecordValueReader())
                // 增加名称转换规则
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        return modelMapper;
    }
}
