package alvin.study.springboot.mvc.app.api.model;

import jakarta.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 包装请求的 Form 类
 */
public record HelloForm(
        @JsonProperty("name") @NotEmpty @Length(min = 3, max = 10) String name) {
    @JsonCreator
    public HelloForm {}
}
