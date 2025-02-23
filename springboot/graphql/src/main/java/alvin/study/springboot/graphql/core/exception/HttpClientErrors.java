package alvin.study.springboot.graphql.core.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 响应客户端错误码的异常类
 */
public final class HttpClientErrors {
    private HttpClientErrors() {}

    /**
     * 响应 {@code 405 Method Not Allowed} 错误码
     *
     * @param statusText 错误信息
     * @return {@code 405 Method Not Allowed} 错误码的响应异常
     */
    public static HttpClientErrorException methodNotAllowed(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.METHOD_NOT_ALLOWED, statusText, HttpHeaders.EMPTY, null, null);
    }

    /**
     * 响应 {@code 403 Forbidden} 错误码
     *
     * @param statusText 错误信息
     * @return {@code 403 Forbidden} 错误码的响应异常
     */
    public static HttpClientErrorException forbidden(String statusText) {
        return HttpClientErrorException.create(
            HttpStatus.FORBIDDEN, statusText, HttpHeaders.EMPTY, null, null);
    }
}
