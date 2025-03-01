package alvin.study.springboot.graphql.app.interceptor;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.Test;

import graphql.ExecutionInput;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.context.ContextKey;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.util.security.Jwt;

public class ApiAuthInterceptorTest extends IntegrationTest {
    private static final String PASSWORD = "test~123";

    @Autowired
    private Jwt jwt;

    @Autowired
    private OrgService orgService;

    @Autowired
    private UserService userService;

    @Mock
    private WebGraphQlRequest mockedRequest;

    @Mock
    private WebGraphQlInterceptor.Chain mockedChain;

    @Mock
    private ExecutionInput mockedExecutionInput;

    @Mock
    private ExecutionInput.Builder mockedBuilder;

    private HttpHeaders buildHttpHeaders() {
        var token = userService.login(currentOrg().getId(), currentUser().getAccount(), PASSWORD);

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    @Test
    void intercept_shouldInterceptGraphqlRequest() {
        var contextMap = new HashMap<Object, Object>();

        doReturn("").when(mockedRequest).getOperationName();

        doReturn(buildHttpHeaders()).when(mockedRequest).getHeaders();

        doAnswer(invoke -> {
            contextMap.putAll(invoke.<Map<Object, Object>>getArgument(0));
            return mockedBuilder;
        }).when(mockedBuilder).graphQLContext(anyMap());

        doAnswer(invoke -> {
            var configurer = invoke.<BiFunction<ExecutionInput, ExecutionInput.Builder, ExecutionInput>>getArgument(0);
            configurer.apply(mockedExecutionInput, mockedBuilder);
            return null;
        }).when(mockedRequest).configureExecutionInput(any());

        var interceptor = new ApiAuthInterceptor(jwt, orgService, userService);
        interceptor.intercept(mockedRequest, mockedChain);

        then(((Org) contextMap.get(ContextKey.ORG)).getId()).isEqualTo(currentOrg().getId());
        then(((User) contextMap.get(ContextKey.USER)).getId()).isEqualTo(currentUser().getId());
    }
}
