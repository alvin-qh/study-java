package alvin.study.springboot.mvc.util.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

/**
 * Jwt 令牌处理类型
 */
@RequiredArgsConstructor
public class Jwt {
    // token 签名算法
    private final Algorithm algorithm;

    // JWT 内定 ID
    private final String jwtId;

    // JWT 过期时间
    private final Duration period;

    /**
     * 测试用的时钟对象, 表示当前时间
     */
    @VisibleForTesting
    private Clock clock = null;

    /**
     * 产生一个 jwt token 字符串
     *
     * @param org  token 所属的组织代码
     * @param user token 对应的用户名
     * @return jwt token
     */
    public String encode(String org, String user) {
        // 如果设置了 clock 字段, 则已 clock 字段值作为当前时间产生 token
        var now = clock == null ? Instant.now() : Instant.now(clock);
        // 设置过期时间
        var expiresAt = now.plusSeconds(period.toSeconds());

        // 创建 token
        return JWT.create()
                .withAudience(org) // 设置 token 的接收方
                .withIssuer(user)  // 设置 token 的发行方
                .withIssuedAt(now) // 设置 token 的发行时间
                .withNotBefore(now) // 设置 token 的生效时间
                .withExpiresAt(expiresAt) // 设置 token 的失效时间
                .withJWTId(jwtId) // 设置 token 的标识 id
                .sign(algorithm); // 对 token 进行签名操作, 产生完整的 token 字符串
    }

    /**
     * 对 token 进行解析, 获得其负载信息
     *
     * @param token token 字符串
     * @return 信息负载对象
     */
    public DecodedJWT decode(String token) {
        return JWT.decode(token);
    }

    /**
     * 对 token 进行验证
     *
     * @param token token 字符串
     * @return 信息负载对象
     */
    public DecodedJWT verify(String token) {
        return JWT.require(algorithm)
                .withJWTId(jwtId)
                .build()
                .verify(token);
    }

    /**
     * 基于当前对象, 获取一个新的 {@link Jwt} 对象
     *
     * @param clock 表示当前时间的 {@link Clock} 对象, token 的发行时间和生效时间将给予此对象产生
     * @return 新的 {@link Jwt} 对象
     */
    @VisibleForTesting
    Jwt cloneForTesting(Clock clock) {
        var newJwt = new Jwt(algorithm, jwtId, period);
        newJwt.clock = clock;
        return newJwt;
    }
}
