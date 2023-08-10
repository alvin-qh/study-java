package alvin.study.quarkus.web.endpoint.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {
    private final int code;
    private final T payload;
    private final String path;
    private final Instant timestamp;

    @JsonCreator
    public Response(
            @JsonProperty("code") int code,
            @JsonProperty("payload") T payload,
            @JsonProperty("path") String path,
            @JsonProperty("timestamp") Instant timestamp) {
        this.code = code;
        this.payload = payload;
        this.path = path;
        this.timestamp = timestamp;
    }
}
