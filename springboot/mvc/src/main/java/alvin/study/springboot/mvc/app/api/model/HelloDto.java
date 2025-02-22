package alvin.study.springboot.mvc.app.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 向客户端返回数据的 DTO 对象
 */
public record HelloDto(
        @JsonProperty("name") String name,
        @JsonProperty("greeting") String greeting) {
    @JsonCreator
    public HelloDto {}
}
