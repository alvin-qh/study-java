package alvin.study.springboot.security.app.endpoint;

import alvin.study.springboot.security.app.domain.service.AuthService;
import alvin.study.springboot.security.app.endpoint.common.BaseController;
import alvin.study.springboot.security.app.endpoint.model.LoginForm;
import alvin.study.springboot.security.app.endpoint.model.TokenDto;
import alvin.study.springboot.security.app.endpoint.model.UserDto;
import alvin.study.springboot.security.core.cache.Cache;
import alvin.study.springboot.security.util.security.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * {@code SecurityConfig.filterChain(HttpSecurity)}, 其中设置了需要验证和无需验证的 URL 以及过滤器的使用情况方法
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
    private final AuthService authService;

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
     * @param form 登录表单对象
     * @return 生成的 JWT token 对象
     */
    @PostMapping("/login")
    @ResponseBody
    TokenDto postLogin(@RequestBody @Valid LoginForm form) {
        // 通过用户名和密码产生 Authentication 对象, 进行验证
        var user = authService.login(form.getAccount(), form.getPassword());

        // 生成 JWT token 对象
        var token = jwt.encode(String.valueOf(user.getId()));

        // 将用户存储到 redis 中
        cache.saveUser(token.getToken(), user, jwt.getPeriod());

        // 返回结果
        return new TokenDto(token.getToken(), token.getExpiresAt());
    }
}
