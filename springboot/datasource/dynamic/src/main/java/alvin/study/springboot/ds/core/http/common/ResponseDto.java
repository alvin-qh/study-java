package alvin.study.springboot.ds.core.http.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 响应包装类
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto<T> {
    /**
     * 状态码
     */
    private int status;

    /**
     * 响应负载
     */
    private T payload;

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 产生表示成功的响应包装对象
     *
     * @return 响应包装对象
     */
    public static ResponseDto<Void> success() {
        return new ResponseDto<>(0, null, Instant.now());
    }

    /**
     * 产生表示成功的响应包装对象
     *
     * @param <T>     负载类型
     * @param payload 响应负载
     * @return 响应包装对象
     */
    public static <T> ResponseDto<T> success(T payload) {
        return new ResponseDto<>(0, payload, Instant.now());
    }

    /**
     * 产生表示错误的响应包装对象
     *
     * @param status 响应代码
     * @param error  {@link ClientError} 错误对象
     * @return 响应包装对象
     */
    public static ResponseDto<ClientError> error(int status, ClientError error) {
        return new ResponseDto<>(status, error, Instant.now());
    }
}
