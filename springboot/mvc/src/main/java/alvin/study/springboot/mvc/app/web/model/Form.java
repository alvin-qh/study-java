package alvin.study.springboot.mvc.app.web.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收页面表单提交的表单类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    public static final Form EMPTY = new Form();

    @NotBlank
    private String name;

    @NotNull
    @Min(20)
    @Max(100)
    private Integer age;

    @NotBlank
    private String gender;
}
