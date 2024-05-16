package alvin.study.misc.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.annotations.VisibleForTesting;

import java.security.interfaces.RSAKey;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * JWT 工具类
 */

public class JWTs {
    // 附加字段名, 表示组织代码
    static final String CLAIM_ORG_CODE = "org";

    // 附件字段名, 表示用户类型
    static final String CLAIM_USER_TYPE = "typ";

    // 用于获取当前时间的 Clock 对象, 可以为测试进行修改
    private static Clock clock = Clock.systemUTC();

    // 签名算法对象
    private final Algorithm algorithm;

    /**
     * 构造器, 设置签名算法对象
     *
     * @param algorithm 签名算法对象
     */
    private JWTs(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 根据 HMAC-SHA256 摘要算法创建 JWT 工具对象
     *
     * @param secret 密钥字节串
     * @return {@link JWTs} 类型对象
     */
    public static JWTs createByHS(byte[] secret) {
        return new JWTs(Algorithm.HMAC256(secret));
    }

    /**
     * 根据 HMAC-SHA256 摘要算法创建 JWT 工具对象
     *
     * @param secret 密钥字符串
     * @return {@link JWTs} 类型对象
     */
    public static JWTs createByHS(String secret) {
        return new JWTs(Algorithm.HMAC256(secret));
    }

    /**
     * 根据 SHA256withRSA 签名算法创建 JWT 工具对象
     *
     * @param secret 密钥对象, 在创建 JWT 字符串时应为 {@link java.security.interfaces.RSAPrivateKey RSAPrivateKey}, 在校验
     *               JWT 字符串时应为 {@link java.security.interfaces.RSAPublicKey RSAPublicKey}
     * @return {@link JWTs} 类型对象
     */
    public static JWTs createByRS(RSAKey secret) {
        return new JWTs(Algorithm.RSA256(secret));
    }

    /**
     * 测试用方法, 更换产生当前时间的 {@code Clock} 对象
     *
     * @param clock 产生当前时间的 {@code Clock} 对象
     */
    @VisibleForTesting
    static void changeClock(Clock clock) {
        JWTs.clock = clock;
    }

    /**
     * 编码生成一个 JWT 字符串
     *
     * <p>
     * 一个完整的 JWT 字符串由三部分组成, 通过 {@code .} 进行分隔
     * </p>
     *
     * <p>
     * 第一部分是 JWT 的 Header, 由如下 JSON 结构表示
     *
     * <pre>
     * {
     *   "typ": "JWT",
     *   "alg": "HS256"
     * }
     * </pre>
     * <p>
     * 表示 JWT 的类型 (固定值为 {@code JWT}) 和签名算法名称
     * </p>
     *
     * <p>
     * 第二部分表示 JWT 的 Payload, 是一个 JSON 结构, 每个属性被称为一个 {@code Claim}, 例如:
     *
     * <pre>
     * {
     *   "iss": "Alvin",
     *   "aud": ["third-part"],
     *   "sub": "u_b989c9ea-e07b-4263-ae98-11d6b1b0e327",
     *   "org": "o_a4ef30",
     *   "exp": "2286-11-20T17:46:39Z"
     * }
     * </pre>
     * <p>
     * Payload 的内容并没有统一标准和固定格式, 可以为任意 JSON 字段, 但字段数量过多会导致 JWT 字符串长度变长
     * (原始 JSON 变大以及签名信息变大)
     * </p>
     *
     * <p>
     * 第三部分为第二部分内容的一个签名, 用来验证 JWT 的有效性
     * </p>
     *
     * <p>
     * 三部分内容经过 Base64 编码之后, 用 {@code .} 分隔之后, 即组成完整的 JWT 字符串
     * </p>
     *
     * @param issuer    {@code iss} 字段, 表示 token 的发行方
     * @param audiences {@code aud} 字段, 表示 token 的接收方
     * @param subject   {@code sub} 字段, 表示 token 的主题
     * @param org       {@code org} 字段, 该字段是自定义字段, 表示 token 所在组织的代码
     * @param type      {@code typ} 字段, 该字段是自定义字段, 表示 token 面向的用户类型
     * @param expiredAt {@code exp} 字段, 表示 token 的有效时间
     * @return JWT 字符串
     */
    public String encode(
        String issuer,
        String[] audiences,
        String subject,
        String org,
        String type,
        Instant expiredAt) {
        return JWT.create()
            .withJWTId(UUID.randomUUID().toString())
            .withIssuer(issuer)
            .withAudience(audiences)
            .withSubject(subject)
            .withClaim(CLAIM_ORG_CODE, org)
            .withClaim(CLAIM_USER_TYPE, type)
            .withIssuedAt(Instant.now(clock))
            .withExpiresAt(expiredAt)
            // 根据所给的签名算法进行签名, 生成 JWT 字符串
            .sign(algorithm);

    }

    /**
     * 对 JWT 字符串进行解码
     *
     * <p>
     * 解码并不会验证 JWT 的正确性, 只是对 Payload 部分的 Base64 进行解码, 得到原始 JSON 字符串, 并获取其中每个 {@code Claim} 属性
     * </p>
     *
     * @param token JWT 字符串
     * @return JWT Payload 解码后的结果
     */
    public DecodedJWT decode(String token) {
        return JWT.decode(token);
    }

    /**
     * 对 JWT 字符串进行验证
     *
     * <p>
     * 对 JWT 的验证包括:
     * <ul>
     * <li>
     * 对签名部分进行验证, 以确保 JWT 的合法性
     * </li>
     * <li>
     * 对 Payload 部分的指定 {@code Claim} 属性进行校验, 包括校验值是否符合预期, 设定的有效期时间是否超过等
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 签名的验证主要是两类算法: {@code HS} 和 {@code RS}, 前者是通过 {@code HMAC-SHA} (包括: {@code HMAC-SHA256},
     * {@code HMAC-SHA384}, {@code HMAC-SHA512} 等) 摘要算法进行签名和验签; 后者是通过 {@code SHAwithRSA} (包括:
     * {@code SHA256withRSA}, {@code SHA384withRSA}, {@code SHA512withRSA} 等) 算法通过公私钥进行签名和验签
     * </p>
     *
     * <p>
     * JWT 标准并未对签名算法做出强制规定, 上述算法也只是比较常用且足够安全, 通过其它签名算法 (例如 MD5, SHA-1 等) 也是可以的. 注意, 过于
     * 复杂的签名算法会导致 JWT 长度增加
     * </p>
     *
     * @param token     要验证的 JWT 字符串
     * @param audiences 允许的 {@code aud} 属性值
     * @param org       允许的 {@code org} 属性值
     * @param type      允许的 {@code typ} 属性值
     * @return 解码后的 Payload 内容
     * @throws com.auth0.jwt.exceptions.AlgorithmMismatchException     无效的签名算法, 即在 JWT Header
     *                                                                 中定义的签名算法和实际使用的不符
     * @throws com.auth0.jwt.exceptions.SignatureVerificationException 签名验证失败
     * @throws com.auth0.jwt.exceptions.TokenExpiredException          JWT 已过期, 即当前时间超出了 {@code exp} 字段定义的时间范围
     * @throws com.auth0.jwt.exceptions.MissingClaimException          要求的 {@code Claim} 属性不存
     * @throws com.auth0.jwt.exceptions.IncorrectClaimException        要求的 {@code Claim} 属性值不符合预设值
     * @throws JWTVerificationException                                以上任意异常
     */
    public DecodedJWT verify(String token, String[] audiences, String org, String type)
        throws JWTVerificationException {
        return JWT.require(algorithm)
            .withAudience(audiences)
            .withClaim(CLAIM_ORG_CODE, org)
            .withClaim(CLAIM_USER_TYPE, type)
            .build()
            .verify(token);
    }
}
