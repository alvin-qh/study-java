package alvin.study.springboot.graphql.app.interceptor;

import java.util.Map;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.context.ContextKey;
import alvin.study.springboot.graphql.core.exception.ForbiddenException;
import alvin.study.springboot.graphql.util.security.Jwt;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAuthInterceptor implements WebGraphQlInterceptor {
    private static final String HEADER_AUTH = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";

    private static final String INTROSPECTION_QUERY = "IntrospectionQuery";

    // 注入 Jwt 对象
    private final Jwt jwt;

    private final OrgService orgService;
    private final UserService userService;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        log.info("Execute operation \"{}\"", request.getOperationName());

        if (!INTROSPECTION_QUERY.equals(request.getOperationName())) {
            var auth = request.getHeaders().getFirst(HEADER_AUTH);
            if (!Strings.isNullOrEmpty(auth)) {
                if (!auth.startsWith(TOKEN_PREFIX)) {
                    throw new ForbiddenException("invalid_bearer_token");
                }
            }

            var token = auth.substring(TOKEN_PREFIX.length()).trim();

            try {
                // 解析 token, 获取 token 负载
                var payload = jwt.verify(token);

                var org = orgService.findById(Long.parseLong(payload.getAudience().get(0)))
                        .orElseThrow(() -> new ForbiddenException("invalid_org"));

                var user = userService.findById(Long.parseLong(payload.getIssuer()))
                        .orElseThrow(() -> new ForbiddenException("invalid_user"));

                // 将获取的负载信息存入请求上下文对象
                request.configureExecutionInput((input, builder) -> builder.graphQLContext(
                    Map.of(
                        ContextKey.ORG, org,
                        ContextKey.USER, user)).build());
            } catch (Exception e) {
                throw new ForbiddenException("invalid_bearer_token", e);
            }
        }
        return chain.next(request);
    }
}
