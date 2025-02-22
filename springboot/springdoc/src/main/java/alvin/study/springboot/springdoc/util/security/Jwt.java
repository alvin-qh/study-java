package alvin.study.springboot.springdoc.util.security;

import java.time.Duration;
import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Jwt 令牌处理类型
 */
@RequiredArgsConstructor
public class Jwt {
    // token 签名算法
    private final Algorithm algorithm;

    // JWT 接收方
    private final String aud;

    // JWT 内定 ID
    private final String jwtId;

    // JWT 过期时间
    private final Duration period;

    /**
     * 产生一个 jwt token 字符串
     *
     * @param org      token 所属的组织代码
     * @param username token 对应的用户名
     * @return jwt token
     */
    public JwtToken encode(String userId) {
        // 如果设置了 clock 字段, 则已 clock 字段值作为当前时间产生 token
        var now = Instant.now();
        // 设置过期时间
        var expiresAt = now.plusSeconds(period.toSeconds());

        // 创建 token
        var token = JWT.create()
                .withAudience(aud) // 设置 token 的接收方
                .withIssuer(userId) // 设置 token 的发行方
                .withIssuedAt(now) // 设置 token 的发行时间
                .withNotBefore(now) // 设置 token 的生效时间
                .withExpiresAt(expiresAt) // 设置 token 的失效时间
                .withJWTId(jwtId) // 设置 token 的标识 id
                .sign(algorithm); // 对 token 进行签名操作, 产生完整的 token 字符串

        // 返回 token 对象
        return new JwtToken(token, expiresAt);
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
                .withAudience(aud)
                .withJWTId(jwtId)
                .build()
                .verify(token);
    }

    /**
     * 获取过期时间
     *
     * @return token 过期时间
     */
    public Duration getPeriod() { return period; }

    /**
     * 保持 token 结构的类型
     */
    @Getter
    @RequiredArgsConstructor
    public static class JwtToken {
        /**
         * token 字符串
         */
        private final String token;

        /**
         * token 过期时间
         */
        private final Instant expiresAt;
    }
}
