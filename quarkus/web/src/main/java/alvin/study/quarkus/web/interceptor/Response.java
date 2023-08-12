package alvin.study.quarkus.web.interceptor;

import java.time.Instant;

import lombok.Builder;

/**
 * 用于包装响应对象的包装类
 */
@Builder
public record Response<T>(boolean ok, T payload, String path, Instant timestamp) {
}
