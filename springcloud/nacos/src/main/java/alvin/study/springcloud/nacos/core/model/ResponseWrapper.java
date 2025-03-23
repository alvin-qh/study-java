package alvin.study.springcloud.nacos.core.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import alvin.study.springcloud.nacos.util.http.Servlets;

/**
 * Controller 返回结果包装类型
 */
@Getter
public final class ResponseWrapper<T> {
    /**
     * 返回的代码, {@code 0} 表示成功, 非 {@code 0} 表示失败
     */
    private final int retCode;

    /**
     * 返回的错误信息, {@code null} 表示无错误
     */
    private final String errMsg;

    /**
     * 返回的负载信息, 可以为任意类型对象
     */
    private final T payload;

    /**
     * 返回请求地址
     */
    private final String path;

    /**
     * 返回信息的时间戳
     */
    private final Instant timestamp;

    /**
     * 构造器
     *
     * @param retCode   返回代码
     * @param errMsg    返回错误信息
     * @param payload   返回的负载对象
     * @param path      请求地址
     * @param timestamp 返回时间戳
     */
    @JsonCreator
    ResponseWrapper(
            @JsonProperty("retCode") int retCode,
            @JsonProperty("errMsg") String errMsg,
            @JsonProperty("payload") T payload,
            @JsonProperty("path") String path,
            @JsonProperty("timestamp") Instant timestamp) {
        this.retCode = retCode;
        this.errMsg = errMsg;
        this.payload = payload;
        this.path = path;
        this.timestamp = timestamp;
    }

    /**
     * 构建表示正确返回的 {@link ResponseWrapper} 对象
     *
     * @param <T>     负载对象类型
     * @param payload 负载对象
     * @return {@link ResponseWrapper} 对象
     */
    public static <T> ResponseWrapper<T> success(T payload) {
        var request = Servlets.getHttpServletRequest();
        return new ResponseWrapper<>(0, null, payload, request.getRequestURI(), Instant.now());
    }

    /**
     * 构建表示错误返回的 {@link ResponseWrapper} 对象
     *
     * @param retCode 返回代码
     * @param errMsg  返回错误信息
     * @param detail  错误详细信息对象
     * @return {@link ResponseWrapper} 对象
     */
    public static ResponseWrapper<ErrorDetail> error(int retCode, String errMsg, ErrorDetail detail) {
        var request = Servlets.getHttpServletRequest();
        return new ResponseWrapper<>(retCode, errMsg, detail, request.getRequestURI(), Instant.now());
    }

    /**
     * 构建表示错误返回的 {@link ResponseWrapper} 对象
     *
     * @param retCode 返回代码
     * @param errMsg  返回错误信息
     * @return {@link ResponseWrapper} 对象
     */
    public static ResponseWrapper<Void> error(int retCode, String errMsg) {
        var request = Servlets.getHttpServletRequest();
        return new ResponseWrapper<>(retCode, errMsg, null, request.getRequestURI(), Instant.now());
    }
}
