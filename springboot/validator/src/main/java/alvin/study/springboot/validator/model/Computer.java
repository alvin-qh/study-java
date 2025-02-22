package alvin.study.springboot.validator.model;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import alvin.study.springboot.validator.validator.IpAddress;

/**
 * 通过自定义校验器验证类字段
 *
 * <p>
 * 自定义校验注解 {@link IpAddress @IpAddress} 用于校验字符串字段是否表示一个 IP 地址, 同时结合其它校验标签组合使用
 * </p>
 */
@Data
@RequiredArgsConstructor
public class Computer {
    @NotBlank
    private final String name;

    @NotBlank
    @IpAddress(version = 4)
    private final String ipAddress;
}
