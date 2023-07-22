package alvin.study.app.endpoint;


import alvin.study.app.endpoint.common.BaseController;
import alvin.study.app.endpoint.model.LoginForm;
import alvin.study.app.endpoint.model.TokenDto;
import alvin.study.app.endpoint.model.UserDto;
import alvin.study.core.cache.Cache;
import alvin.study.core.security.auth.NameAndPasswordAuthenticationToken;
import alvin.study.infra.entity.User;
import alvin.study.util.security.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理用户认证的 Controller 类型
 *
 * <p>
 * 只有 {@code /auth/**} URL 下的请求不会验证登录情况, 其余所有 URL 的请求都会验证登录情况, 具体配置请参考
 * {@link alvin.study.conf.SecurityConfig#filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity)
 * SecurityConfig.filterChain(HttpSecurity)}, 其中设置了需要验证和无需验证的 URL 以及过滤器的使用情况
 * 方法
 * </p>
 */
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    // 注入 JWT 工具对象
    private final Jwt jwt;

    // 注入验证管理器对象
    private final AuthenticationManager authManager;

    // 注入缓存对象
    private final Cache cache;

    /**
     * 获取当前登录用户信息
     *
     * <p>
     * 通过 {@link BaseController#currentUser()} 方法获取当前登录用户
     * </p>
     *
     * @return 如果用户已登录, 则返回 {@link UserDto} 对象, 否则返回 {@code null}
     */
    @GetMapping("/me")
    @ResponseBody
    UserDto getMe() {
        // 返回结果
        var user = currentUser();
        if (user == null) {
            return null;
        }
        return mapper(user, UserDto.class);
    }

    /**
     * 用户登录
     *
     * <p>
     * 通过登录的用户名和密码产生一个 {@link NameAndPasswordAuthenticationToken} 对象, 再通过
     * {@link AuthenticationManager#authenticate(org.springframework.security.core.Authentication)
     * AuthenticationManager.authenticate(Authentication)} 方法进行校验, 通过
     * {@link alvin.study.conf.SecurityConfig#authManager(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     * SecurityConfig.authManager(HttpSecurity)} 方法获取
     * </p>
     *
     * <p>
     * {@link AuthenticationManager#authenticate(org.springframework.security.core.Authentication)
     * AuthenticationManager.authenticate(Authentication)} 方法在内部会调用
     * {@link alvin.study.core.security.auth.NameAndPasswordAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     * NameAndPasswordAuthenticationProvider.authenticate(Authentication)} 方法并返回一个
     * {@link NameAndPasswordAuthenticationToken} 对象, 其
     * {@link NameAndPasswordAuthenticationToken#getPrincipal()} 方法返回一个 {@link User}
     * 对象表示登录的用户实体
     * </p>
     *
     * @param form 登录表单对象
     * @return 生成的 JWT token 对象
     */
    @PostMapping("/login")
    @ResponseBody
    TokenDto postLogin(@RequestBody @Valid LoginForm form) {
        // 通过用户名和密码产生 Authentication 对象, 进行验证
        var auth = authManager.authenticate(
            new NameAndPasswordAuthenticationToken(form.getAccount(), form.getPassword()));

        // 获取登录的用户对象
        var user = (User) auth.getPrincipal();

        // 生成 JWT token 对象
        var token = jwt.encode(String.valueOf(user.getId()));

        // 将用户存储到 redis 中
        cache.saveUser(token.getToken(), user, jwt.getPeriod());

        // 返回结果
        return new TokenDto(token.getToken(), token.getExpiresAt());
    }
}
