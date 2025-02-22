package alvin.study.springboot.autoconf.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 读取 `application.yml` 中指定前缀下的配置信息
 * <p>
 * 对应每个配置的提示项配置参见 `spring-configuration-metadata.json` 配置文件
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "autoconfig")
public class ConfigProperties {
    private String timeZone;
    private ConfigCommonProperties common;
}
