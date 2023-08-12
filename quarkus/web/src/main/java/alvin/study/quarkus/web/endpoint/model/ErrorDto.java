package alvin.study.quarkus.web.endpoint.model;

import java.util.List;

import alvin.study.quarkus.util.ObjectUtil;
import io.quarkus.hibernate.validator.runtime.jaxrs.ViolationReport.Violation;
import lombok.Builder;

@Builder
public record ErrorDto(int status, String message, String detail, List<Violation> violations) {
    public ErrorDto {
        violations = List.copyOf(ObjectUtil.nullElse(violations, List.of()));
    }

    public ErrorDto(int status, String message, String detail) {
        this(status, message, detail, List.of());
    }

    public ErrorDto(int status, String message, List<Violation> violations) {
        this(status, message, null, violations);
    }
}
