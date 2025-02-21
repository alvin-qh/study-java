package alvin.study.springboot.springdoc.app.endpoint;


import jakarta.validation.Valid;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import alvin.study.springboot.springdoc.app.endpoint.model.LoginForm;
import alvin.study.springboot.springdoc.app.endpoint.model.TokenDto;
import alvin.study.springboot.springdoc.infra.entity.AccessLog;
import alvin.study.springboot.springdoc.infra.repository.AccessLogRepository;
import alvin.study.springboot.springdoc.infra.repository.UserRepository;
import alvin.study.springboot.springdoc.util.security.Jwt;
import alvin.study.springboot.springdoc.util.security.PasswordEncoder;

/**
 * 获取访问日志的控制器类
 *
 * <p>
 * {@link Tag @Tag} 注解用于对整个控制器进行文档注释, 主要提供 {@code name} (API 名称) 属性和
 * {@code description} (API 描述)
 * </p>
 *
 * <p>
 * {@link Operation @Operation} 注解用于对控制器方法进行文档注释, 通过 {@code summary} 属性指定 API
 * 的概要, {@code description} 属性指定 API 的具体描述
 * </p>
 */
@Tag(name = "身份验证", description = "通过用户名和密码对登录用户的身份进行认证, 获取代表登录凭证的 JWT 字符串")
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    // 注入 JWT 操作对象
    private final Jwt jwt;

    // 注入用户持久化对象
    private final UserRepository userRepository;

    // 密码解码器对象
    private final PasswordEncoder passwordEncoder;

    // 注入访问日志持久化操作对象
    private final AccessLogRepository accessLogRepository;

    /**
     * 通过用户名密码进行登录
     *
     * @param form {@link LoginForm} 对象, 包含用户名和密码
     * @return {@link TokenDto} 对象, 包含 JWT 字符串和超时时间
     */
    @Operation(summary = "登录系统, 获取登录 Token", description = "访问其它 API 需要先登录", operationId = "login")
    @PostMapping("/login")
    TokenDto login(@Valid @RequestBody LoginForm form) {
        // 对登录信息进行验证
        var user = userRepository
            .selectUserByName(form.getUsername())
            .orElseThrow(() -> new BadCredentialsException("bad_credentials"));

        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("bad_credentials");
        }

        // 记录访问日志, 并标识为登录日志
        accessLogRepository.insert(AccessLog.forLogin(form.getUsername()));

        // 产生 JWT 结果并返回
        var token = jwt.encode(user.getUsername());
        return new TokenDto(token.getToken(), token.getExpiresAt());
    }
}
