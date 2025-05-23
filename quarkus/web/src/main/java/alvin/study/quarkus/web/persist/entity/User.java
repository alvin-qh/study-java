package alvin.study.quarkus.web.persist.entity;

import java.time.LocalDate;

public record User(String id, String name, LocalDate birthday, Gender gender) {}
