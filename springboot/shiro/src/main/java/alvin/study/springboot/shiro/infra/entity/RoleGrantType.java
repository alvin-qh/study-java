package alvin.study.springboot.shiro.infra.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.Getter;

/**
 * 授权类型
 */
@Getter
public enum RoleGrantType {
    // 为用户授权
    USER("user"),
    // 为用户组授权
    GROUP("group");

    // 授权类型代码
    @EnumValue
    private final String code;

    RoleGrantType(String code) {
        this.code = code;
    }
}
