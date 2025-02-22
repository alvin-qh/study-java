package alvin.study.springboot.shiro.app.endpoint;

import java.util.Optional;

import jakarta.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.shiro.app.endpoint.common.BaseController;
import alvin.study.springboot.shiro.app.endpoint.model.LoginForm;
import alvin.study.springboot.shiro.app.endpoint.model.TokenDto;
import alvin.study.springboot.shiro.app.endpoint.model.UserDto;
import alvin.study.springboot.shiro.conf.ShiroConfig;
import alvin.study.springboot.shiro.core.shiro.CustomerRealm;
import alvin.study.springboot.shiro.infra.entity.User;
import alvin.study.springboot.shiro.util.security.Jwt;

/**
 * 处理用户认证的 Controller 类型
 *
 * <p>
 * 只有 {@code /auth/**} URL 下的请求不会验证登录情况, 其余所有 URL 的请求都会验证登录情况, 具体配置请参考
 * {@link ShiroConfig#shiroFilterFactoryBean(org.apache.shiro.mgt.SecurityManager)
 * ShiroConfig.shiroFilterFactoryBean(SecurityManager)} 方法, 其中设置了需要验证和无需验证的 URL
 * 以及过滤器的使用情况方法
 * </p>
 */
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    // 注入 JWT 工具对象
    private final Jwt jwt;

    /**
     * 获取当前登录用户信息
     *
     * <p>
     * 通过 {@link BaseController#currentUser()} 方法获取当前登录用户对象
     * </p>
     *
     * @return 如果用户已登录, 则返回 {@link UserDto} 对象, 否则返回 {@code null}
     */
    @GetMapping("/me")
    @ResponseBody
    UserDto getMe() { return Optional.ofNullable(currentUser())
            .map(u -> mapper(u, UserDto.class))
            .orElse(null); }

    /**
     * 用户登录
     *
     * <p>
     * 通过登录的用户名和密码产生一个 {@link UsernamePasswordToken} 对象, 再通过
     * {@link CustomerRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     * CustomerRealm.doGetAuthenticationInfo(AuthenticationToken)} 方法对用户的合法性进行校验
     * </p>
     *
     * @param form 登录表单对象
     * @return 生成的 JWT token 对象
     */
    @PostMapping("/login")
    @ResponseBody
    TokenDto postLogin(@RequestBody @Valid LoginForm form) {
        // 获取或创建一个 subject 对象
        var subject = SecurityUtils.getSubject();

        // 通过用户名密码产生 UsernamePasswordToken 对象并进行登录验证
        subject.login(new UsernamePasswordToken(form.getAccount(), form.getPassword()));

        // 获取登录成功的用户对象
        var user = (User) subject.getPrincipals().getPrimaryPrincipal();

        // 将用户 id 编码为 jwt 返回
        var token = jwt.encode(String.valueOf(user.getId()));
        return new TokenDto(token.getToken(), token.getExpiresAt());
    }
}
