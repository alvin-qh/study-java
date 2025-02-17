package alvin.study.testing.testcase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.List;

/**
 * 用于测试的组实体类型
 */
@Getter
public class Group implements Serializable {
    // 组 id
    private final int id;

    // 组名称
    private final String name;

    // 组中包含的用户集合
    private final List<User> users;

    public Group(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("users") List<User> users) {
        this.id = id;
        this.name = name;
        this.users = users;
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
