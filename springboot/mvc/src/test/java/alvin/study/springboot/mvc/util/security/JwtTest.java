package alvin.study.springboot.mvc.util.security;

import alvin.study.springboot.mvc.WebTest;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 测试 {@link Jwt} 类型
 */
class JwtTest extends WebTest {
    // 注入 Jwt 类型对象
    @Autowired
    private Jwt jwt;

    /**
     * 测试 {@link Jwt#encode(String, String)} 方法产生一个 token, 并通过
     * {@link Jwt#decode(String)} 将 token 进行解析
     */
    @Test
    void encode_decode_shouldCreateAndParseToken() {
        // 产生一个供测试使用的 Jwt 对象并指定一个固定时间作为当前时间
        var jwt = this.jwt.cloneForTesting(Clock.fixed(Instant.parse("2022-10-01T12:00:00.0Z"), ZoneOffset.UTC));

        // 产生一个 token
        var token = jwt.encode("test-org", "Alvin");

        // 将之前产生的 token 还原为信息负载对象
        var payload = jwt.decode(token);

        // 确认还原的信息符合预期
        then(payload.getAlgorithm()).isEqualTo("HS256");
        then(payload.getAudience()).containsExactly("test-org");
        then(payload.getIssuer()).isEqualTo("Alvin");
        then(payload.getIssuedAtAsInstant()).hasToString("2022-10-01T12:00:00Z");
        then(payload.getNotBeforeAsInstant()).hasToString("2022-10-01T12:00:00Z");
        then(payload.getExpiresAtAsInstant()).hasToString("2022-10-01T14:00:00Z");
    }

    /**
     * 测试 {@link Jwt#verify(String)} 方法, 对产生的 token 进行验证
     */
    @Test
    void verify_shouldVerifyTokenSuccessful() {
        // 产生一个 token
        var token = jwt.encode("test-org", "Alvin");

        // 对 token 进行校验, 校验通过后返回信息负载
        var payload = jwt.verify(token);

        // 确认得到的负载信息符合预期
        then(payload.getAlgorithm()).isEqualTo("HS256");
        then(payload.getAudience()).containsExactly("test-org");
        then(payload.getIssuer()).isEqualTo("Alvin");
    }

    /**
     * 测试 {@link Jwt#verify(String)} 方法, 对产生的 token 进行验证
     *
     * <p>
     * 本次测试假设系统时间还未到达 token 允许生效的时间, 从而引发一个预期的异常
     * </p>
     */
    @Test
    void verify_shouldFailedWhenVerifyTokenBeforeValidTime() {
        // 产生一个当前时间 60 秒后的时间对象
        var createdAt = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS);

        // 产生测试用 Jwt 对象, 设置产生 token 的时间和 token 生效时间均为 60 秒后
        var jwt = this.jwt.cloneForTesting(Clock.fixed(createdAt, ZoneOffset.UTC));

        // 产生一个 token, 实际生效时间在 60 秒后
        var token = jwt.encode("test-org", "Alvin");

        // 验证 token, 此时抛出 IncorrectClaimException 异常表示 token 尚未生效
        var e = assertThrows(IncorrectClaimException.class, () -> jwt.verify(token));
        then(e).hasMessage("The Token can't be used before %s.", createdAt);
    }

    /**
     * 测试 {@link Jwt#verify(String)} 方法, 对产生的 token 进行验证
     *
     * <p>
     * 本次测试假设系统时间已经超出 token 的有效期, 从而引发一个预期的异常
     * </p>
     */
    @Test
    void verify_shouldFailedWhenVerifyTokenAfterExpiredTime() {
        // 产生一个当前时间 2 小时 60 秒前的时间对象
        var expiredAt = Instant.now().minusSeconds(2 * 3600 + 60).truncatedTo(ChronoUnit.SECONDS);

        // 产生测试用 Jwt 对象, 设置产生 token 的时间和 token 生效时间均为 2 小时 60 秒前
        var jwt = this.jwt.cloneForTesting(Clock.fixed(expiredAt, ZoneOffset.UTC));

        // 产生一个 token, 已超过过期时间
        var token = jwt.encode("test-org", "Alvin");

        // 验证 token, 此时抛出 TokenExpiredException 异常表示 token 已经过期
        var e = assertThrows(TokenExpiredException.class, () -> jwt.verify(token));
        then(e).hasMessage("The Token has expired on %s.", expiredAt.plusSeconds(2 * 3600));
    }
}
