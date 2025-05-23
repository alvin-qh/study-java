package alvin.study.quarkus.web.interceptor;

import java.time.Instant;

/**
 * 用于包装响应对象的包装类
 */
public record Response<T>(boolean ok, T payload, String path, Instant timestamp) {}
