package alvin.study.quarkus.web.persist.entity;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record User(String id, String name, LocalDate birthday, Gender gender) {
}
