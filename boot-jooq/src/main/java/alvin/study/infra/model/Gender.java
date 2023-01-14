package alvin.study.infra.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举
 */
public enum Gender {
    MALE("m"), FEMALE("f");

    @JsonValue
    private String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
}
