package alvin.study.springboot.shiro.app.endpoint;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.shiro.app.domain.service.MenuService;
import alvin.study.springboot.shiro.app.endpoint.common.BaseController;
import alvin.study.springboot.shiro.app.endpoint.mapper.MenuEntityMapper;
import alvin.study.springboot.shiro.app.endpoint.model.HomeDto;
import alvin.study.springboot.shiro.app.endpoint.model.MenuDto;
import alvin.study.springboot.shiro.conf.ShiroConfig;
import alvin.study.springboot.shiro.core.shiro.CustomerRealm;

/**
 * Home 页控制器类型
 *
 * <p>
 * 只有 {@code /auth/**} URL 下的请求不会验证登录情况, 其余所有 URL 的请求都会验证登录情况, 具体配置请参考
 * {@link ShiroConfig#shiroFilterFactoryBean(org.apache.shiro.mgt.SecurityManager)
 * ShiroConfig.shiroFilterFactoryBean(SecurityManager)}, 其中设置了需要验证和无需验证的 URL
 * 以及过滤器的使用情况
 * 方法
 * </p>
 *
 * <p>
 * {@link RequiresRoles @RequiresRoles} 注解表示调用该方法的用户是否具备指定的角色
 * </p>
 *
 * <p>
 * {@link RequiresPermissions @RequiresPermissions} 注解表示调用该方法的用户是否具备指定的权限
 * </p>
 *
 * <p>
 * 角色和权限的获取参考
 * {@link CustomerRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
 * CustomerRealm.doGetAuthorizationInfo(PrincipalCollection)} 方法
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
    @RequiresRoles("ANY_ONE")
    HomeDto getHome() {
        var user = currentUser();
        return new HomeDto(String.format("Welcome %s", user.getAccount()));
    }

    /**
     * 获取菜单信息
     *
     * @return 菜单信息对象
     */
    @GetMapping("menu")
    @ResponseBody
    @RequiresPermissions("COMMON:MENU:READ")
    MenuDto getMenu() {
        var menus = menuService.loadUserMenu(currentUser().getId());
        return menuMapper.toDto(menus);
    }
}
