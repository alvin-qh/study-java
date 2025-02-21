package alvin.study.springboot.springdoc.app.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import alvin.study.springboot.springdoc.app.endpoint.model.AccessLogDto;
import alvin.study.springboot.springdoc.conf.SpringDocConfig;
import alvin.study.springboot.springdoc.infra.repository.AccessLogRepository;

/**
 * 获取访问日志的控制器类
 *
 * <p>
 * {@link Tag @Tag} 注解用于对整个控制器进行文档注释, 主要提供 {@code name} (API 名称) 属性和
 * {@code description} (API 描述)
 * </p>
 *
 * <p>
 * {@link SecurityRequirement @SecurityRequirement} 注解用于指定调用此 API 时需要的认证方式,
 * 这里指定的 {@code BearerAuth}, 认证配置参考
 * {@link SpringDocConfig#openAPI() SpringDocConfig.openAPI()}
 * 方法
 * </p>
 *
 * <p>
 * {@link Operation @Operation} 注解用于对控制器方法进行文档注释, 通过 {@code summary} 属性指定 API
 * 的概要, {@code description} 属性指定 API 的具体描述
 * </p>
 */
@Tag(name = "访问日志", description = "获取指定用户或所有用户的访问日志")
@RestController
@RequestMapping("/api/access-log")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AccessLogController {
    // 注入访问日志持久化操作对象
    private final AccessLogRepository accessLogRepository;

    /**
     * 获取指定用户的访问日志
     *
     * @param username 要获取访问日志的用户名
     * @return 指定用户的访问日志
     */
    @Operation(summary = "获取当前登录用户的访问日志", description = "通过传递一个 {username} 参数, 获取该用户的全部访问日志, 如果用户不存在或无访问日志, 返回空集合")
    @GetMapping("/{username}")
    List<AccessLogDto> getAccessLog(@NotBlank @PathVariable("username") String username) {
        // 获取当前登录的用户对象
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return List.of();
        }

        // 根据用户名查询该用户的访问日志集合
        return accessLogRepository.selectByUsername(username).stream()
            .map(log -> new AccessLogDto(log.getUsername(), log.getLastAccessAt(), log.isActionLogin()))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有用户的访问日志
     *
     * @return 所有用户的访问日志
     */
    @Operation(summary = "获取所有历史登录用户的访问日志")
    @GetMapping
    List<AccessLogDto> getAllAccessLog() {
        // 查询所有的访问日志集合
        return accessLogRepository.selectAll().stream()
            .map(log -> new AccessLogDto(log.getUsername(), log.getLastAccessAt(), log.isActionLogin()))
            .collect(Collectors.toList());
    }
}
