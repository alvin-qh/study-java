package alvin.study.springcloud.nacos.endpoint.mapper;

import alvin.study.springcloud.nacos.core.model.ApplicationConfig;
import alvin.study.springcloud.nacos.core.model.annotation.Mapper;
import alvin.study.springcloud.nacos.endpoint.model.ApplicationConfigDto;
import org.jetbrains.annotations.NotNull;

/**
 * 将 {@link ApplicationConfig} 类型对象进行转换的 Mapper 类型
 */
@Mapper
public class ApplicationConfigMapper {
    /**
     * 将 {@link ApplicationConfig} 类型对象转为 {@link ApplicationConfigDto} 类型对象
     *
     * @param entity {@link ApplicationConfig} 类型对象
     * @return {@link ApplicationConfigDto} 对象
     */
    public ApplicationConfigDto toDto(@NotNull ApplicationConfig entity) {
        return new ApplicationConfigDto(
            new ApplicationConfigDto.Common(entity.getCommon().getSearchUrl()));
    }
}
