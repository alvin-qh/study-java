package alvin.study.springboot.testing.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * 测试 Controller 返回结果的模型类
 *
 * @see JsonCreator
 * @see JsonProperty
 */
@Getter
public class TestModel {
    private final Long id;
    private final String name;
    private final Instant timestamp;

    @JsonCreator
    public TestModel(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("timestamp") Instant timestamp) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
    }
}
