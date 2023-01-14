package alvin.study.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用于测试的组实体类型
 */
@Data
@RequiredArgsConstructor
public class Group implements Serializable {
    // 组 id
    private final int id;

    // 组名称
    private final String name;

    // 组中包含的用户集合
    private final List<User> users;

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
