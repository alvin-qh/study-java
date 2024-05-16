package alvin.study.springboot.mybatis.infra.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 表示用户类型的枚举类型
 *
 * <p>
 * {@link EnumValue @EnumValue} 注解表示持久化该枚举对象时在数据表中保持的实际值; 如果不使用该注解, 则枚举类型必须实现
 * {@link com.baomidou.mybatisplus.annotation.IEnum IEnum} 接口
 * </p>
 *
 * <p>
 * {@link JsonValue @JsonValue} 注解表示序列化时该枚举对象在 JSON 中体现的值; 如果不实用该注解, 则必须设置枚举到
 * JSON 的 toString 配置, 参考: {@code JacksonConfig.customizer()} 方法
 * </p>
 */
public enum UserType {
    ADMIN("Administrator", "admin"),
    OPERATOR("Operator", "opt"),
    NORMAL("Normal", "norm");

    // 类型名称
    private final String name;

    // 类型代码
    @EnumValue
    @JsonValue
    private final String code;

    UserType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() { return code; }

    public String getName() { return name; }
}
