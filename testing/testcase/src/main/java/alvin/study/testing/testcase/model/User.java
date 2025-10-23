package alvin.study.testing.testcase.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * 用于测试的用户实体类型
 */
@Getter
public class User implements Serializable {
    // 用户 id
    private final int id;

    // 用户名称
    private final String name;

    public User(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%d-%s", id, name);
    }

    /**
     * 将当前对象转为 JSON 字符串
     *
     * @param objectMapper JSON 操作类对象
     * @return JSON 字符串
     */
    @SneakyThrows
    public String toJson(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}
