package alvin.study.springboot.shiro.util.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * HTTP 头字段常量群
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Headers {
    public static final String BEARER = "Bearer";
    public static final String AUTHORIZATION = "Authorization";
}
