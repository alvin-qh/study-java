package alvin.study.springcloud.gateway.server.predicate;

import alvin.study.springcloud.gateway.server.jwt.JWTAlgorithm;
import alvin.study.springcloud.gateway.server.util.http.Headers;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.common.base.Strings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 自定义断言类
 *
 * <p>
 * 断言对象是通过"断言工厂"创建的, 断言工厂类需继承 {@link AbstractRoutePredicateFactory} 类型, 且类名字必须以
 * {@code RoutePredicateFactory} 为后缀, 例如当前 {@link JWTRoutePredicateFactory}
 * 类型创建的断言名称为 {@code JWT}
 * </p>
 *
 * <p>
 * 如果创建断言对象需要额外的参数, 需要设置一个 Pojo 类型接收参数, 并在 {@link AbstractRoutePredicateFactory}
 * 的泛型参数中指定这个 Pojo 类型
 * </p>
 *
 * <p>
 * 自定义的断言类和内置断言类用法一致, 在 {@code classpath:application.yml} 中, 设置如下配置:
 *
 * <pre>
 * spring:
 *   gateway:
 *     routes:
 *       - id: jwt_router
 *         uri: https://www.baidu.com
 *         predicates:
 *           - JWT=Authorization,HMAC256,TS#1}'v(xo{5QhU]*~>3CHEBk)|MLqRe,third-part
 *                 ^^^^^^^^^^^^^ ^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^
 *                  headerName  algorithm            securityKey           audience
 * </pre>
 * <p>
 * {@code -JWT} 表示使用 {@link JWTRoutePredicateFactory} 创建断言, {@code =} 号后面是根据
 * {@link #shortcutFieldOrder()} 定义的顺序传递的参数
 * </p>
 */
@Slf4j
@Component
public class JWTRoutePredicateFactory extends AbstractRoutePredicateFactory<JWTRoutePredicateFactory.Config> {
    // JWT 验证对象
    private JWTVerifier jwtVerifier = null;

    /**
     * 构造器, 指定配置类
     */
    public JWTRoutePredicateFactory() {
        super(Config.class);
    }

    /**
     * 指定配置参数的顺序
     *
     * @return 配置参数名称集合, 将按集合顺序获取参数
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("headerName", "algorithm", "securityKey", "audience");
    }

    /**
     * 进行断言
     *
     * @param config {@link Config} 类型对象, 表示当前断言的参数
     * @return {@link Predicate} 断言对象
     */
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        if (this.jwtVerifier == null) {
            // 打印配置信息
            log.info("""
                Get predicate arguments:
                  Header Name: {}
                  Algorithm: {}
                  Security Key: {}
                  Audience: {}
                """,
                config.headerName,
                config.algorithm.getName(),
                config.securityKey,
                config.audience);

            // 创建 JWT 验证对象
            this.jwtVerifier = JWT.require(config.algorithm.build(config.securityKey))
                    .withAudience(config.audience)
                    .build();
        }

        // 返回断言对象
        return exchange -> {
            // 获取请求头集合
            var headers = exchange.getRequest().getHeaders();

            // 获取指定名称的请求头
            var token = Objects.requireNonNull(headers.getFirst(config.getHeaderName()));
            if (Strings.isNullOrEmpty(token) || !token.startsWith(Headers.BEARER)) {
                // 如果不是 Bearer Token, 则断言失败
                return false;
            }
            // 获取 Bearer Token 内容
            token = token.substring(Headers.BEARER.length()).trim();

            try {
                // 验证 JWT token
                this.jwtVerifier.verify(token);
                return true;
            } catch (JWTVerificationException ignore) {
                return false;
            }
        };
    }

    /**
     * 断言配置类
     */
    @Data
    @Validated
    public static class Config {
        /**
         * 要获取的请求头名称
         */
        @NotBlank
        private String headerName;

        /**
         * 加密类型枚举
         */
        @NotNull
        private JWTAlgorithm algorithm;

        /**
         * 密钥字符串
         */
        @NotBlank
        private String securityKey;

        /**
         * JWT 允许的接收者标识符
         */
        @NotBlank
        private String audience;
    }
}
