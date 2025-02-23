package alvin.study.springboot.graphql.core.graphql.interceptor;

import java.util.Map;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * GraphQL 请求拦截器, 用于在请求中添加上下文信息
 */
@Component
public class ContextInterceptor implements WebGraphQlInterceptor {
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        request.configureExecutionInput((input, builder) -> builder.graphQLContext(Map.of()).build());
        return chain.next(request);
    }
}
