package alvin.study.springboot.shiro.app.domain.service;

import alvin.study.springboot.shiro.infra.entity.Menu;
import alvin.study.springboot.shiro.infra.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 菜单服务类
 */
@Service
@RequiredArgsConstructor
public class MenuService {
    private static final String MENU_CACHE_NAME = "menu";

    // 注入菜单数据库访问对象
    private final MenuMapper menuMapper;

    // 注入缓存管理器
    private final CacheManager cacheManager;

    /**
     * 读取菜单
     *
     * @return 菜单实体集合
     */
    @Transactional(readOnly = true)
    public Collection<Menu> loadUserMenu(Long userId) {
        var cache = cacheManager.<Long, Collection<Menu>>getCache(MENU_CACHE_NAME);

        var menus = cache.get(userId);
        if (menus == null) {
            var subject = SecurityUtils.getSubject();

            menus = menuMapper.selectWithRoleAndPermission().stream()
                .filter(m -> {
                    var role = m.getRole();
                    if (role == null) {
                        return true;
                    }
                    return subject.hasRole(role.getName());
                })
                .filter(m -> {
                    var permission = m.getPermission();
                    if (permission == null) {
                        return true;
                    }
                    return subject.isPermitted(permission.getPermission());
                })
                .toList();

            cache.put(userId, menus);
        }

        return menus;
    }
}
