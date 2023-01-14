package alvin.study.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public final class HttpClientErrors {
    private HttpClientErrors() {
    }

    public static HttpClientErrorException methodNotAllowed(String statusText) {
        return HttpClientErrorException.create(
                HttpStatus.METHOD_NOT_ALLOWED, statusText, null, null, null);
    }

    public static HttpClientErrorException forbidden(String statusText) {
        return HttpClientErrorException.create(
                HttpStatus.FORBIDDEN, statusText, null, null, null);
    }
}
