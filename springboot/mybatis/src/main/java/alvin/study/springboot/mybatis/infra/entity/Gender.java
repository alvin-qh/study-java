package alvin.study.springboot.mybatis.infra.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 表示性别的枚举
 */
public enum Gender {
    MALE("M"), FEMALE("F");

    /**
     * {@link JsonValue @JsonValue} 注解表示当前枚举转为 JSON 字段时, 以该字段的值来表示
     */
    @JsonValue
    private final String name;

    Gender(String name) {
        this.name = name;
    }

    /**
     * 获取性别名称
     *
     * @return 性别名称
     */
    public String getName() { return name; }
}
