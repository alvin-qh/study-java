package alvin.study.quarkus.web.endpoint.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private final String name;
    private final LocalDate birthday;
    private final Gender gender;
}
