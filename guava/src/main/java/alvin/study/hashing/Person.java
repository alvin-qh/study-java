package alvin.study.hashing;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.google.common.hash.Funnel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Person {
    private final long id;
    private final String name;
    private final LocalDate birthday;
    private final String location;

    public static Funnel<Person> makeFunnel() {
        return (Funnel<Person>) (person, into) -> into
                .putLong(person.getId())
                .putString(person.getName(), StandardCharsets.UTF_8)
                .putString(person.getBirthday().toString(), StandardCharsets.UTF_8)
                .putString(person.getLocation(), StandardCharsets.UTF_8);
    }
}
