package alvin.study.springboot.security.app.domain.service;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.security.core.cache.Cache;
import alvin.study.springboot.security.core.security.handler.AclPermissionEvaluator;
import alvin.study.springboot.security.infra.entity.Menu;
import alvin.study.springboot.security.infra.entity.Permission;
import alvin.study.springboot.security.infra.entity.Role;
import alvin.study.springboot.security.infra.mapper.MenuMapper;

/**
 * 菜单服务类
 */
@Service
@RequiredArgsConstructor
public class MenuService {
    // 注入菜单数据库访问对象
    private final MenuMapper menuMapper;

    // 注入缓存对象
    private final Cache cache;

    /**
     * 获取当前用户的角色权限列表
     *
     * @return 当前用户的角色权限列表集合
     */
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 读取菜单
     *
     * @return 菜单实体集合
     */
    @Transactional(readOnly = true)
    public Collection<Menu> loadUserMenu(Long userId) {
        // 先从 cache 中获取菜单缓存, 如不存在, 则进一步从数据库中获取
        return cache.loadMenus(userId).orElseGet(() -> {
            var menus = menuMapper.selectWithRoleAndPermission().stream()
                    .filter(m -> checkRoleMatched(m.getRole()))
                    .filter(m -> checkPermissionMatched(m.getPermission()))
                    .toList();

            // 写入缓存
            return cache.saveMenus(userId, menus);
        });
    }

    /**
     * 检查菜单项的角色是否匹配
     *
     * @param role 菜单项要求角色
     * @return 是否匹配
     */
    private boolean checkRoleMatched(Role role) {
        if (role == null) {
            return true;
        }
        return new AclPermissionEvaluator().hasRole(getAuthentication(), role.getName());
    }

    /**
     * 检查菜单项的权限是否匹配
     *
     * @param permission 菜单项要求权限
     * @return 是否匹配
     */
    private boolean checkPermissionMatched(Permission permission) {
        if (permission == null) {
            return true;
        }
        return new AclPermissionEvaluator().hasPermission(
            getAuthentication(), permission.getTarget(), permission.getAction());
    }
}
