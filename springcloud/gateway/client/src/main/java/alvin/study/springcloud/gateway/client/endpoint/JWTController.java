package alvin.study.springcloud.gateway.client.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springcloud.gateway.client.endpoint.model.AuthDto;
import alvin.study.springcloud.gateway.client.util.http.Headers;

/**
 * 身份验证后端服务控制器
 *
 * <p>
 * 本控制器类用于测试名为 {@code JWT} 的断言, 参考 {@code classpath:application.yml} 中关于断言的定义
 *
 * <pre>
 * spring:
 *   cloud:
 *     gateway:
 *       routes:
 *         - id: path_router
 *           uri: lb://study-springcloud-gateway-backend
 *           predicates:
 *             - Path=/auth/**
 *             - JWT=Authorization,HMAC256,TS#1}'v(xo{5QhU]*~>3CHEBk)|MLqRe,third-part
 * </pre>
 * <p>
 * 上述配置表示:
 *
 * <ul>
 * <li>
 * 使用名为 {@code Path} 的断言, 参数为 {@code /auth/**}, 表示所有符合该路径的请求全部进行转发
 * </li>
 * <li>
 * 使用名为 {@code JWT} 的断言 (具体断言参见
 * {@link alvin.study.core.gateway.predicate.JWTRoutePredicateFactory
 * JWTRoutePredicateFactory} 类型), 将具备 JWT 凭证的请求转发到目标地址同为 {@code /auth/} 的路径上,
 * </li>
 * <li>
 * 目标地址为 {@link lb://study-springcloud-gateway-backend}, 表示通过服务发现查找名为
 * {@code study-springcloud-gateway-backend} 的服务并进行转发, 且转发时使用负载均衡方式
 * </li>
 * <li>
 * 转发前后的请求路径一致 (由 {@code Path} 断言确定), 所以无需对转发路径进行额外处理
 * </li>
 * </ul>
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class JWTController {
    /**
     * 获取 JWT 凭证携带的信息
     *
     * <p>
     * 对 {@code @GetMapping("**")} 注解表示该方法可以对 {@code /auth/**} 路径下的所有子路径请求进行处理
     * </p>
     *
     * <p>
     * {@link RequestHeader @RequestHeader} 注解表示从请求中获取指定名称的请求头信息, 作为参数
     * </p>
     *
     * @param authorization 请求头中名为 {@code Authorization} 的属性值
     * @return {@link AuthDto} 对象, 表示 JWT 凭证中携带的信息
     */
    @GetMapping("**")
    @ResponseBody
    AuthDto get(@RequestHeader(Headers.AUTHORIZATION) String authorization) {
        if (Strings.isNullOrEmpty(authorization)) {
            // 验证 authorization 参数是否有效: 非空字符串
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_jwt");
        }

        if (!authorization.startsWith(Headers.BEARER)) {
            // 验证 authorization 参数是否有效: 以 Bearer 开头
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_jwt");
        }

        // 提取变量中 JWT 部分
        var token = authorization.substring(Headers.BEARER.length()).trim();

        try {
            // 对 JWT 进行解码, 获取载荷信息
            var payload = JWT.decode(token);
            // 返回 JWT 信息
            return new AuthDto(
                payload.getIssuer(),
                payload.getAudience().get(0),
                payload.getSubject(),
                payload.getClaim("sub_org_code").asString(),
                payload.getClaim("sub_user_type").asString(),
                payload.getIssuedAtAsInstant(),
                payload.getExpiresAtAsInstant());
        } catch (JWTDecodeException e) {
            log.error("Invalid JWT {}", token, e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_jwt", e);
        }
    }
}
