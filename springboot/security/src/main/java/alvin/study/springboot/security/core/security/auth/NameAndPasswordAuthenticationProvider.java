package alvin.study.springboot.security.core.security.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.security.app.domain.service.AuthService;
import alvin.study.springboot.security.app.endpoint.AuthController;
import alvin.study.springboot.security.app.endpoint.model.LoginForm;
import alvin.study.springboot.security.conf.SecurityConfig;
import alvin.study.springboot.security.core.security.filter.JwtRequestFilter;
import alvin.study.springboot.security.util.security.PasswordEncoder;

/**
 * 对 {@link NameAndPasswordAuthenticationToken} 类型对象进行身份验证的类型
 *
 * <p>
 * 该类型对象在
 * {@link SecurityConfig#authManager(org.springframework.security.config.annotation.web.builders.HttpSecurity)
 * SecurityConfig.authManager(HttpSecurity)} 方法中进行注册
 * </p>
 *
 * <p>
 * 在
 * {@link AuthController#postLogin(LoginForm)
 * AuthController.postLogin(LoginForm)} 方法中, 通过用户名密码产生了
 * {@link NameAndPasswordAuthenticationToken} 对象, 该对象由当前类型的
 * {@link #authenticate(Authentication)} 方法进行校验
 * </p>
 *
 * @deprecated 在 Spring Security 6+ 版本之后, 无需使用 Provider, 只需要在 Filter 中为
 *             {@link org.springframework.security.core.context.SecurityContext SecurityContext}传递
 *             {@link Authentication} 对象即可, 参见 {@link JwtRequestFilter JwtRequestFilter} 类型
 */
@Component
@Deprecated(forRemoval = true, since = "3.0")
@RequiredArgsConstructor
public class NameAndPasswordAuthenticationProvider implements AuthenticationProvider {
    // 注入登录验证服务类
    private final AuthService authService;

    // 注入密码处理工具类对象
    private final PasswordEncoder passwordEncoder;

    /**
     * 对传入的 {@link NameAndPasswordAuthenticationToken} 类型对象进行身份验证
     *
     * <p>
     * 传入的 {@link NameAndPasswordAuthenticationToken} 对象携带用户名和密码信息, 返回的
     * {@link NameAndPasswordAuthenticationToken} 对象携带用户对象信息
     * </p>
     *
     * @param auth {@link NameAndPasswordAuthenticationToken} 类型对象, 携带用户名和密码信息
     * @return {@link NameAndPasswordAuthenticationToken} 类型对象, 携带用户实体对象信息
     */
    @Override
    public Authentication authenticate(Authentication auth) {
        // 如果 Authentication 已经认证过, 则无需重复认证
        if (auth.isAuthenticated()) {
            return auth;
        }

        // 通过用户名查找用户对象
        var user = authService.findUserByAccount(auth.getName());

        // 判断密码是否匹配
        if (!passwordEncoder.matches((String) auth.getCredentials(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 产生新的 Authentication 对象, 存储 User 对象和角色权限信息 (触发一次缓存动作)
        return new NameAndPasswordAuthenticationToken(user, "", authService.findPermissionsByUserId(user.getId()));
    }

    /**
     * 设置当前类型对象只处理 {@link NameAndPasswordAuthenticationToken} 类型对象
     *
     * @param authType 待匹配的 {@link Authentication} 对象类型
     * @return 如果 {@code authType} 参数类型为 {@link NameAndPasswordAuthenticationToken}
     *         类型则返回 {@code true}
     */
    @Override
    public boolean supports(Class<?> authType) {
        return authType.isAssignableFrom(NameAndPasswordAuthenticationToken.class);
    }
}
