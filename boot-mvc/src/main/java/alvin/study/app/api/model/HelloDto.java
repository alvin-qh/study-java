package alvin.study.app.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * 向客户端返回数据的 DTO 对象
 */
@Getter
public class HelloDto implements Serializable {
    private final String name;
    private final String greeting;

    /**
     * 构造器, 通过 JSON 构建当前对象
     *
     * @param name     姓名参数
     * @param greeting 问候语参数
     */
    @JsonCreator
    public HelloDto(
            @JsonProperty("name") String name,
            @JsonProperty("greeting") String greeting) {
        this.name = name;
        this.greeting = greeting;
    }
}
