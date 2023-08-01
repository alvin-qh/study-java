package alvin.study.misc.jwt;

import alvin.study.misc.jwt.util.RSAKeyLoader;
import com.google.common.io.ByteStreams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link JWTs} 工具类
 */
class JWTsTest {
    // 用于 HS265 算法加密的密钥串
    private static final String SECRET = "TS#1}'v(xo{5QhU]*~>3CHEBk)|MLqRe";

    // 表示 JWT 创建时间
    private static final Instant EXPECTED_ISSUED_AT = Instant.parse("2023-01-01T12:00:00Z");

    /**
     * 读取私钥
     *
     * @return RSA 私钥 {@link RSAPrivateKey} 对象
     */
    private static RSAPrivateKey readPrivateKey() throws IOException, InvalidKeySpecException {
        try (var is = Objects.requireNonNull(JWTsTest.class.getResourceAsStream("/keyfile/key"))) {
            var key = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
            return (RSAPrivateKey) new RSAKeyLoader().loadPrivateKey(key);
        }
    }

    /**
     * 读取公钥
     *
     * @return RSA 公钥 {@link RSAPublicKey} 对象
     */
    private static RSAPublicKey readPublicKey() throws IOException, InvalidKeySpecException {
        try (var is = Objects.requireNonNull(JWTsTest.class.getResourceAsStream("/keyfile/key.pub"))) {
            var key = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
            return (RSAPublicKey) new RSAKeyLoader().loadPublicKey(key);
        }
    }

    /**
     * 每次测试前执行
     */
    @BeforeEach
    void beforeEach() {
        // 将表示当前时间的 Clock 对象进行固定
        JWTs.changeClock(Clock.fixed(EXPECTED_ISSUED_AT, ZoneOffset.UTC));
    }

    /**
     * 每次测试结束后执行
     */
    @AfterEach
    void afterEach() {
        // 恢复 Clock 的初始值
        JWTs.changeClock(Clock.systemUTC());
    }

    /**
     * 测试通过 {@link JWTs#createByHS(String)} 创建 {@link JWTs} 对象, 再通过
     * {@link JWTs#encode(String, String[], String, String, String, Instant)} 方法编码 JWT 字符串, 最后通过
     * {@link JWTs#verify(String, String[], String, String)} 验证 JWT 字符串
     */
    @Test
    void verify_shouldEncodeJWTByHS256Signature() {
        // 通过一个密钥字符串创建对象, 采用 HMAC-SHA256 算法
        var jwt = JWTs.createByHS(SECRET);

        // 编码一个 JWT 字符串
        var token = jwt.encode(
            "Alvin",
            new String[]{ "third-part" },
            "u_b989c9ea-e07b-4263-ae98-11d6b1b0e327",
            "o_a4ef30",
            "EMPLOYEE",
            Instant.parse("2286-11-20T17:46:39Z"));
        then(token).isNotEmpty();

        // 验证 JWT 字符串, 确认验证结果正确
        var payload = jwt.verify(
            token,
            new String[]{ "third-part" },
            "o_a4ef30",
            "EMPLOYEE");
        then(payload.getId()).matches("^[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}$");
        then(payload.getIssuer()).isEqualTo("Alvin");
        then(payload.getAudience()).containsExactly("third-part");
        then(payload.getSubject()).isEqualTo("u_b989c9ea-e07b-4263-ae98-11d6b1b0e327");
        then(payload.getClaim(JWTs.CLAIM_ORG_CODE).asString()).isEqualTo("o_a4ef30");
        then(payload.getClaim(JWTs.CLAIM_USER_TYPE).asString()).isEqualTo("EMPLOYEE");
        then(payload.getIssuedAtAsInstant()).isEqualTo(EXPECTED_ISSUED_AT);
        then(payload.getExpiresAtAsInstant()).isEqualTo(Instant.parse("2286-11-20T17:46:39Z"));
    }

    /**
     * 测试通过 {@link JWTs#createByRS(java.security.interfaces.RSAKey) JWTs.createByRS(RSAKey)} 创建 {@link JWTs} 对象,
     * 再通过 {@link JWTs#encode(String, String[], String, String, String, Instant)} 方法编码 JWT 字符串, 最后通过
     * {@link JWTs#verify(String, String[], String, String)} 验证 JWT 字符串
     */
    @Test
    void verify_shouldEncodeJWTByRS256Signature() throws Exception {
        // 通过一个私钥对象创建 JWT 工具类, 并编码 JWT 字符串
        var jwt = JWTs.createByRS(readPrivateKey());
        var token = jwt.encode(
            "Alvin",
            new String[]{ "third-part" },
            "u_b989c9ea-e07b-4263-ae98-11d6b1b0e327",
            "o_a4ef30",
            "EMPLOYEE",
            Instant.parse("2286-11-20T17:46:39Z"));
        then(token).isNotEmpty();

        // 通过一个公钥对象创建 JWT 工具类, 并验证 JWT 字符串
        jwt = JWTs.createByRS(readPublicKey());
        var payload = jwt.verify(
            token,
            new String[]{ "third-part" },
            "o_a4ef30",
            "EMPLOYEE");
        then(payload.getId()).matches("^[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}$");
        then(payload.getIssuer()).isEqualTo("Alvin");
        then(payload.getAudience()).containsExactly("third-part");
        then(payload.getSubject()).isEqualTo("u_b989c9ea-e07b-4263-ae98-11d6b1b0e327");
        then(payload.getClaim(JWTs.CLAIM_ORG_CODE).asString()).isEqualTo("o_a4ef30");
        then(payload.getClaim(JWTs.CLAIM_USER_TYPE).asString()).isEqualTo("EMPLOYEE");
        then(payload.getIssuedAtAsInstant()).isEqualTo(EXPECTED_ISSUED_AT);
        then(payload.getExpiresAtAsInstant()).isEqualTo(Instant.parse("2286-11-20T17:46:39Z"));
    }
}
