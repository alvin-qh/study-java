package alvin.study.springboot.kickstart.core.exception;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public final class HttpClientErrors {
    private HttpClientErrors() {}

    @Contract("_ -> new")
    public static @NotNull HttpClientErrorException methodNotAllowed(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.METHOD_NOT_ALLOWED, statusText, HttpHeaders.EMPTY, null, null);
    }

    @Contract("_ -> new")
    public static @NotNull HttpClientErrorException forbidden(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.FORBIDDEN, statusText, HttpHeaders.EMPTY, null, null);
    }
}
