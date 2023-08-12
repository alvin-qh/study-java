package alvin.study.quarkus.web.endpoint.model;

import java.time.LocalDate;
import java.time.Period;

import alvin.study.quarkus.web.persist.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

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
