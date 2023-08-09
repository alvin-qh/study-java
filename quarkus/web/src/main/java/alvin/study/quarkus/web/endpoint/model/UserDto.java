package alvin.study.quarkus.web.endpoint.model;

import java.time.LocalDate;
import java.time.Period;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private final String name;
    private final LocalDate birthday;
    private final Gender gender;

    public boolean isOlderThan(int age) {
        if (birthday == null) {
            return false;
        }

        return Period.between(birthday, LocalDate.now()).getYears() > age;
    }
}
