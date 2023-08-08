package alvin.study.quarkus.web.endpoint.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDto {
    private final int status;
    private final String message;
    private final String detail;
}
