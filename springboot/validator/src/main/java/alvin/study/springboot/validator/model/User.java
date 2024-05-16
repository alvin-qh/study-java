package alvin.study.springboot.validator.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 定义一个具备字段校验规则的模型类
 *
 * @see NotNull @NotNull
 * @see NotBlank @NotBlank
 * @see Min @Min
 * @see Max @Max
 */
@Data
@RequiredArgsConstructor
public class User {
    // 非 null 校验
    @NotNull
    private final Long id;

    // 非空校验 (not null and not empty)
    @NotBlank
    private final String name;

    // 非 null 校验
    // 数值范围校验
    @Min(10)
    @Max(100)
    @NotNull
    private final Integer age;
}
