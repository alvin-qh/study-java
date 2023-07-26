package alvin.study.springboot.mvc.app.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * 向客户端返回数据的 DTO 对象
 */
@Getter
public class I18nDto implements Serializable {
    private final String key;
    private final String message;

    @JsonCreator
    public I18nDto(
            @JsonProperty("key") String key,
            @JsonProperty("message") String message) {
        this.key = key;
        this.message = message;
    }
}
