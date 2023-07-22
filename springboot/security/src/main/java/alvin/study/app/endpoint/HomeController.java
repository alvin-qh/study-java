package alvin.study.app.endpoint;

import alvin.study.app.domain.service.MenuService;
import alvin.study.app.endpoint.common.BaseController;
import alvin.study.app.endpoint.mapper.MenuEntityMapper;
import alvin.study.app.endpoint.model.HomeDto;
import alvin.study.app.endpoint.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Home 页控制器类型
 *
 * <p>
 * 只有 {@code /auth/**} URL 下的请求不会验证登录情况, 其余所有 URL 的请求都会验证登录情况, 具体配置请参考
 * {@link alvin.study.conf.SecurityConfig#filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity)
 * SecurityConfig.filterChain(HttpSecurity)}, 其中设置了需要验证和无需验证的 URL 以及过滤器的使用情况
 * 方法
 * </p>
 *
 * <p>
 * {@link PreAuthorize @PreAuthorize} 注解中的 {@code hasRole('ANY_ONE')}
 * 表达式表示调用该方法的用户是否具备指定的角色
 * </p>
 *
 * <p>
 * {@link PreAuthorize @PreAuthorize} 注解中的
 * {@code hasPermission('COMMON:MENU', 'READ')} 表达式表示调用该方法的用户是否具备指定的权限,
 * 具体权限如何匹配参考 {@link alvin.study.core.security.handler.AclPermissionEvaluator
 * AclPermissionEvaluator} 类型
 * </p>
 *
 * <p>
 * 角色和权限的获取参考
 * {@link alvin.study.core.security.auth.JwtAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
 * JwtAuthenticationProvider.authenticate(Authentication)} 方法
 * </p>
 *
 * <p>
 * 最终对一个用户对应的角色和权限是通过
 * {@link alvin.study.app.domain.service.AuthService#findPermissionsByUserId(Long)
 * AuthService.findPermissionsByUserId(Long)} 方法进行获取
 * </p>
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController extends BaseController {
    // 注入菜单服务类
    private final MenuService menuService;

    // 注入菜单对象转换器
    private final MenuEntityMapper menuMapper;

    /**
     * GET 请求, 获取欢迎信息
     *
     * <p>
     * {@code /} URL 下的情况会验证登录情况, 所以请求中必须包含 {@code Authorization} 请求头, 且其中包含登录用户信息的
     * JWT token
     * </p>
     *
     * @return {@link HomeDto} 对象, 表示欢迎信息
     */
    @GetMapping
    @ResponseBody
    @PreAuthorize("hasRole('ANY_ONE')")
    HomeDto getHome() {
        var user = currentUser();
        return new HomeDto(String.format("Welcome %s", Objects.requireNonNull(user).getAccount()));
    }

    /**
     * 获取菜单信息
     *
     * @return 菜单信息对象
     */
    @GetMapping("menu")
    @ResponseBody
    @PreAuthorize("hasPermission('COMMON:MENU', 'READ')")
    MenuDto getMenu() {
        var menus = menuService.loadUserMenu(Objects.requireNonNull(currentUser()).getId());
        return menuMapper.toDto(menus);
    }
}
