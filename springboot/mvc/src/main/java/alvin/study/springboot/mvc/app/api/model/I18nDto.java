package alvin.study.springboot.mvc.app.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 向客户端返回数据的 DTO 对象
 */
public record I18nDto(
        @JsonProperty("key") String key,
        @JsonProperty("message") String message) {
    @JsonCreator
    public I18nDto {}
}
