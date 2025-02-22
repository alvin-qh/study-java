package alvin.study.quarkus.web.endpoint.model;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

import alvin.study.quarkus.web.persist.entity.Gender;

/**
 * DTO 类型
 *
 * <p>
 * 本类中通过 {@link NotBlank @NotBlank} 等注解指定了验证规则
 * </p>
 *
 * <p>
 * 如果验证失败, 可以通过验证注解的 {@code message} 属性指定错误信息, 也可以通过验证国际化文件来指定错误信息,
 * 本例中通过 {@code resources:ValidationMessages_*.properties} 文件来指定错误信息
 * </p>
 */
@Builder
public record UserDto(
        String id,
        @NotBlank String name,
        @NotNull LocalDate birthday,
        @NotNull Gender gender) {
    public boolean isOlderThan(int age) {
        if (birthday == null) {
            return false;
        }
        return Period.between(birthday, LocalDate.now()).getYears() > age;
    }
}
