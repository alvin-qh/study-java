package alvin.study.quarkus.web.endpoint.model;

import lombok.Builder;

@Builder
public record ErrorDto(int status, String message, String detail) {
}
