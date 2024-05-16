package alvin.study.springboot.security.infra.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 授权类型
 */
public enum RoleGrantType {
    // 为用户授权
    USER("user"),
    // 为用户组授权
    GROUP("group");

    // 授权类型代码
    @EnumValue
    private String code;

    RoleGrantType(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
}
