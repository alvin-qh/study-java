package alvin.study.core.graphql.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import alvin.study.app.service.OrgService;
import alvin.study.app.service.UserService;
import alvin.study.core.context.Context;
import alvin.study.core.exception.HttpClientErrors;
import alvin.study.util.http.Headers;
import alvin.study.util.security.Jwt;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 对 {@link GraphQLContext} 进行解析处理
 *
 * <p>
 * 该类型对象会自动注入 {@link alvin.study.conf.GraphqlConfig#contextResolvers
 * GraphqlConfig.contextResolvers} 集合字段中, 会传入 {@link GraphQLContext} 进行处理
 * </p>
 *
 * <p>
 * 在当前类型的 {@link #resolve(GraphQLContext)} 方法中, 从 HTTP Header 中获取 Bearer token,
 * 解析得到到 userId 和 orgCode 后填充 {@link Context} 对象
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServletGraphQLContextResolver implements GraphQLContextResolver {
    /**
     * 从配置文件中读取 JWT ID
     */
    @Value("${application.security.jwt.jti}")
    private String jti;

    /**
     * Spring Context 上下文对象
     *
     * <p>
     * 参考 {@link Context} 对象以及 {@link alvin.study.conf.ContextConfig#context()
     * ContextConfig.context()} 方法
     * </p>
     */
    private final Context context;

    /**
     * Jwt token 编解码对象
     *
     * <p>
     * 参考 {@link Jwt} 对象以及 {@link alvin.study.conf.BeanConfig#jwt()
     * BeanConfig.jwt()} 方法
     * </p>
     */
    private final Jwt jwt;

    /**
     * 用户服务类
     */
    private final UserService userService;

    /**
     * 组织服务类
     */
    private final OrgService orgService;

    /**
     * 从 {@link GraphQLContext} 对象中获取 Bearer token, 并进行解析得到 userId 和 orgCode 值,
     * 并将对应的 {@link alvin.study.infra.entity.User User} 对象和
     * {@link alvin.study.infra.entity.Org Org} 对象存入 {@link Context} 对象中
     *
     * <p>
     * {@link GraphQLContext} 在
     * {@link alvin.study.conf.GraphqlConfig#build(HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * GraphqlConfig.build(HttpServletRequest, HttpServletResponse)} 方法中通过
     * {@link graphql.kickstart.execution.context.GraphQLKickstartContext
     * GraphQLKickstartContext} 对象产生
     * </p>
     *
     * @param qlContext {@link GraphQLContext} 对象
     */
    @Override
    public void resolve(GraphQLContext qlContext) {
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = qlContext.get(HttpServletRequest.class);
        if (request == null) {
            throw HttpClientErrors.methodNotAllowed("Need http request");
        }

        // 获取 HTTP 请求头
        var token = request.getHeader(Headers.AUTHORIZATION);
        if (!token.startsWith(Headers.BEARER)) {
            throw HttpClientErrors.forbidden("Invalid bearer token");
        }
        // 获取 Bearer Token
        token = token.substring(Headers.BEARER.length()).trim();

        // 验证 token, 获取 token 中的负载信息
        var payload = jwt.verify(token);

        try {
            checkUser(Long.parseLong(payload.getIssuer()), payload.getAudience().get(0));
        } catch (NumberFormatException e) {
            log.warn("Invalid user id", e);
            throw HttpClientErrors.forbidden("Invalid user");
        }
    }

    /**
     * 检查 token 中携带的 User 信息
     *
     * @param userId  用户 id
     * @param orgCode 组织名称
     */
    private void checkUser(Long userId, String orgCode) {
        var user = userService.findById(userId)
                .orElseThrow(() -> HttpClientErrors.forbidden("Invalid user"));

        var org = orgService.findById(user.getOrgId())
                .filter(o -> o.getName().equals(orgCode))
                .orElseThrow(() -> HttpClientErrors.forbidden("Invalid org"));

        log.info("Set context with userId={} and orgCode={}", user.getId(), org.getName());

        context.set(Context.ORG, org);
        context.set(Context.USER, user);
    }
}
