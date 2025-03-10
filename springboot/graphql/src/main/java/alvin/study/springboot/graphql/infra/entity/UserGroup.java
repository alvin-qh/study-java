package alvin.study.springboot.graphql.infra.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 表示用户类型的枚举类型
 *
 * <p>
 * {@link EnumValue @EnumValue} 注解表示持久化该枚举对象时在数据表中保持的实际值; 如果不使用该注解, 则枚举类型必须实现
 * {@link com.baomidou.mybatisplus.annotation.IEnum IEnum} 接口
 * </p>
 */
public enum UserGroup {
    ADMIN("admin"),
    OPERATOR("opt"),
    NORMAL("norm");

    // 类型代码
    @EnumValue
    private final String code;

    UserGroup(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
}
