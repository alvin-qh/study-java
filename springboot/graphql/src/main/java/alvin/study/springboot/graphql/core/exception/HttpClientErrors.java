package alvin.study.springboot.graphql.core.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public final class HttpClientErrors {
    private HttpClientErrors() {}

    public static HttpClientErrorException methodNotAllowed(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.METHOD_NOT_ALLOWED, statusText, HttpHeaders.EMPTY, null, null);
    }

    public static HttpClientErrorException forbidden(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.FORBIDDEN, statusText, HttpHeaders.EMPTY, null, null);
    }
}
