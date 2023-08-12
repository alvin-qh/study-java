package alvin.study.quarkus.web.endpoint.model;

import java.time.LocalDate;
import java.time.Period;

import lombok.Builder;

@Builder
public record UserDto(String name, LocalDate birthday, Gender gender) {
    public boolean isOlderThan(int age) {
        if (birthday == null) {
            return false;
        }

        return Period.between(birthday, LocalDate.now()).getYears() > age;
    }
}
