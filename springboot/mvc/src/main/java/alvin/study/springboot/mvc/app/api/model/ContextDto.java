package alvin.study.springboot.mvc.app.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 向客户端返回数据的 DTO 对象
 *
 * @param orgCode 组织代码
 * @param userId  用户 ID
 */
public record ContextDto(
        @JsonProperty("orgCode") String orgCode,
        @JsonProperty("userId") Long userId) {

    @JsonCreator
    public ContextDto {}
}
