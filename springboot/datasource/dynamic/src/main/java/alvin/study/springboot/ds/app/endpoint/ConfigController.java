package alvin.study.springboot.ds.app.endpoint;

import alvin.study.springboot.ds.app.domain.model.ConfigDto;
import alvin.study.springboot.ds.app.domain.service.ConfigService;
import alvin.study.springboot.ds.core.http.common.ResponseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置 Controller 类
 *
 * <p>
 * 配置信息存储在 {@code default} 数据源对应的数据库中, 需要切换到 {@code default} 数据源进行操作
 * </p>
 */
@Validated
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    // 注入配置服务类对象
    private final ConfigService configService;

    /**
     * 创建配置信息
     *
     * @param org 组织代码
     * @return 配置信息 Dto 对象
     */
    @PostMapping("/{org}")
    @ResponseBody
    ResponseDto<ConfigDto> createConfig(@NotBlank @PathVariable("org") String org) {
        // 创建配置实体对象
        var dto = configService.createConfig(org);

        // 将 Entity 转为 Dto 后包装为成功结果
        return ResponseDto.success(dto);
    }

    /**
     * 删除配置信息
     *
     * @param org 组织代码
     * @return 空
     */
    @ResponseBody
    @DeleteMapping("/{org}")
    ResponseDto<Void> deleteConfig(@NotBlank @PathVariable("org") String org) {
        // 删除配置实体
        configService.deleteConfig(org);

        // 返回成功结果
        return ResponseDto.success();
    }
}
