package alvin.study.springboot.security.core.security.auth;

import alvin.study.springboot.security.app.domain.service.AuthService;
import alvin.study.springboot.security.conf.SecurityConfig;
import alvin.study.springboot.security.core.security.filter.JwtRequestFilter;
import alvin.study.springboot.security.infra.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 对 {@code JwtAuthenticationToken} 类型对象进行身份验证的类型
 *
 * <p>
 * 该类型对象在 {@code SecurityConfig.authManage} 方法中进行注册 (参见 {@link SecurityConfig
 * SecurityConfig} 类型)
 * </p>
 *
 * @deprecated 在 Spring Security 6+ 版本之后, 无需使用 Provider, 只需要在 Filter 中为
 * {@link org.springframework.security.core.context.SecurityContext SecurityContext}传递
 * {@link Authentication} 对象即可, 参见 {@link JwtRequestFilter JwtRequestFilter} 类型
 */
@Component
@Deprecated(forRemoval = true, since = "3.0")
@SuppressWarnings("all")
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthService authService;

    /**
     * 对传入的 {@code JwtAuthenticationToken} 类型对象进行身份验证
     *
     * @param auth {@code JwtAuthenticationToken} 类型对象
     * @return {@link NameAndPasswordAuthenticationToken} 存储
     * {@link User User} 实体对象
     */
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        // 获取 JWT 字符串
        var jwt = (String) auth.getCredentials();

        // 对 JWT 进行解密, 并获取 User 对象
        var user = authService.decodeJwtToken(jwt);

        // 产生新的 Authentication 对象, 存储用户和其角色权限
        return new NameAndPasswordAuthenticationToken(user, jwt, authService.findPermissionsByUserId(user.getId()));
    }

    /**
     * 设置当前类型对象只处理 {@code JwtAuthenticationToken} 类型对象
     *
     * @param authType 待匹配的 {@link Authentication} 对象类型
     * @return 如果 {@code authType} 参数类型为 {@code JwtAuthenticationToken} 类型则返回 {@code true}
     */
    @Override
    public boolean supports(Class<?> authType) {
        return JwtAuthenticationToken.class.isAssignableFrom(authType);
    }
}
