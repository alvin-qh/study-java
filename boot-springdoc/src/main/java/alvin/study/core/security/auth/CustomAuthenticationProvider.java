package alvin.study.core.security.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import alvin.study.infra.repository.UserRepository;
import alvin.study.util.security.Jwt;
import alvin.study.util.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;

/**
 * 定义指定 {@link Authentication} 凭证对象的验证类型
 *
 * <p>
 * 当前类型会对对 {@link CustomAuthenticationToken} 凭证对象进行认证
 * </p>
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    // 注入用户持久化对象 (API 用户处理)
    private final UserRepository userRepository;

    // 注入用户服务对象 (swagger 用户处理)
    private final UserDetailsService detailsService;

    // 密码处理对象
    private final PasswordEncoder passwordEncoder;

    // jwt 编解码对象
    private final Jwt jwt;

    /**
     * 对 {@link CustomAuthenticationToken} 认证对象进行认证处理
     *
     * @param authentication {@link CustomAuthenticationToken} 对象, 存储用户名密码或 JWT 字符串
     * @return {@link CustomAuthenticationToken} 对象, 存储
     *         {@link alvin.study.infra.entity.User User} 对象或
     *         {@link org.springframework.security.core.userdetails.UserDetails
     *         UserDetails} 对象
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 如果凭证已被认证, 则直接返回该凭证
        if (authentication.isAuthenticated()) {
            return authentication;
        }

        // 转换凭证类型
        var auth = (CustomAuthenticationToken) authentication;

        // 根据凭证类型以不同方式处理凭证
        var newAuth = switch (auth.getType()) {
        // 处理 basic auth 凭证
        case BASIC -> authAsBasic(auth);
        // 处理用户名密码凭证
        case NAME_PASS -> authAsNameAndPassword(auth);
        // 处理 JWT 凭证
        case JWT -> authAsJwt(auth);
        default -> null;
        };

        if (newAuth != null) {
            // 拷贝登录详细信息
            newAuth.setDetails(auth.getDetails());

            // 擦除用户凭证
            auth.eraseCredentials();
        }

        return newAuth;
    }

    /**
     * 处理 Basic Auth 格式的凭证
     *
     * @param auth {@link CustomAuthenticationToken} 凭证对象, 存储用户名和密码, 通过
     *             {@link UserDetailsService} 查询用户信息
     * @return {@link CustomAuthenticationToken} 对象, 存储
     *         {@link org.springframework.security.core.userdetails.UserDetails
     *         UserDetails} 对象
     */
    private CustomAuthenticationToken authAsBasic(CustomAuthenticationToken auth) {
        // 获取用户信息对象
        var details = detailsService.loadUserByUsername(auth.getName());

        // 匹配密码是否正确
        if (!passwordEncoder.matches((String) auth.getCredentials(), details.getPassword())) {
            throw new BadCredentialsException("bad_credentials");
        }

        // 返回存储 UserDetails 对象的新对象
        return new CustomAuthenticationToken(details);
    }

    /**
     * 处理用户名密码凭证
     *
     * @param auth {@link CustomAuthenticationToken} 凭证对象, 存储用户名和密码, 通过
     *             {@link UserRepository} 查询用户信息
     * @return {@link CustomAuthenticationToken} 对象, 存储
     *         {@link alvin.study.infra.entity.User User} 对象
     */
    private CustomAuthenticationToken authAsNameAndPassword(CustomAuthenticationToken auth) {
        // 获取用户信息对象
        var user = userRepository.selectUserByName(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("bad_username"));

        // 匹配密码是否正确
        if (!passwordEncoder.matches((String) auth.getCredentials(), user.getPassword())) {
            throw new BadCredentialsException("bad_credentials");
        }

        // 返回存储 User 对象的新对象
        return new CustomAuthenticationToken(user);
    }

    /**
     * 处理 jwt 凭证
     *
     * @param auth {@link CustomAuthenticationToken} 凭证对象, 存储 jwt 字符串
     * @return {@link CustomAuthenticationToken} 对象, 存储
     *         {@link alvin.study.infra.entity.User User} 对象
     */
    private CustomAuthenticationToken authAsJwt(CustomAuthenticationToken auth) {
        DecodedJWT payload;
        try {
            // 解码 jwt 字符串
            payload = jwt.verify((String) auth.getCredentials());
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("bad_jwt", e);
        }

        // 查询相关用户信息
        var user = userRepository.selectUserByName(payload.getIssuer())
                .orElseThrow(() -> new BadCredentialsException("bad_jwt"));

        // 返回存储 User 对象的新对象
        return new CustomAuthenticationToken(user);
    }

    /**
     * 定义当前类型支持的 {@link Authentication} 类型
     *
     * <p>
     * 本例中仅对 {@link CustomAuthenticationToken} 类型对象进行认证
     * </p>
     *
     * @param authentication 要进行认证的 {@link Authentication} 对象类型
     * @return 是否支持对给定类型进行认证
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
