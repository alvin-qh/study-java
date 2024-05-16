package alvin.study.springcloud.nacos.endpoint;

import alvin.study.springcloud.nacos.core.model.ApplicationConfig;
import alvin.study.springcloud.nacos.endpoint.mapper.ApplicationConfigMapper;
import alvin.study.springcloud.nacos.endpoint.model.ApplicationConfigDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取应用程序配置的 Controller 类型
 *
 * @see ApplicationConfig
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ApplicationConfigController {
    // 注入应用程序配置类型对象
    private final ApplicationConfig config;

    // 注入对象类型转换对象
    private final ApplicationConfigMapper configMapper;

    /**
     * 获取应用程序配置对象
     *
     * @return {@link ApplicationConfigDto} 类型对象
     */
    @GetMapping
    @ResponseBody
    ApplicationConfigDto getConfig() {
        return configMapper.toDto(config);
    }
}
