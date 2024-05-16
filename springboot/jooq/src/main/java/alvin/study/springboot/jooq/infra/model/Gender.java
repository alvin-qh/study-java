package alvin.study.springboot.jooq.infra.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举
 */
public enum Gender {
    MALE("m"), FEMALE("f");

    @JsonValue
    private final String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
}
