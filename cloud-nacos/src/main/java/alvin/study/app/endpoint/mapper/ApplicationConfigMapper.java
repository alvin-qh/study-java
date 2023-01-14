package alvin.study.app.endpoint.mapper;

import alvin.study.app.endpoint.model.ApplicationConfigDto;
import alvin.study.app.endpoint.model.ApplicationConfigDto.Common;
import alvin.study.core.model.ApplicationConfig;
import alvin.study.core.model.annotation.Mapper;

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
    public ApplicationConfigDto toDto(ApplicationConfig entity) {
        return new ApplicationConfigDto(
                new Common(entity.getCommon().getSearchUrl()));
    }
}
