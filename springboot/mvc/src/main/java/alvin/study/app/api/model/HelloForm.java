package alvin.study.app.api.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/**
 * 包装请求的 Form 类
 */
@Getter
public class HelloForm implements Serializable {
    @NotEmpty
    @Length(min = 3, max = 10)
    private final String name;

    @JsonCreator
    public HelloForm(@JsonProperty("name") String name) {
        this.name = name;
    }
}
