package alvin.study.springboot.security.app.endpoint.mapper;

import alvin.study.springboot.security.IntegrationTest;
import alvin.study.springboot.security.app.domain.service.MenuService;
import alvin.study.springboot.security.app.endpoint.model.MenuDto;
import alvin.study.springboot.security.builder.MenuBuilder;
import alvin.study.springboot.security.builder.PermissionBuilder;
import alvin.study.springboot.security.builder.RoleBuilder;
import alvin.study.springboot.security.core.security.auth.NameAndPasswordAuthenticationToken;
import alvin.study.springboot.security.infra.entity.Menu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link MenuEntityMapper} 类, 将 {@link Menu Menu}
 * 实体对象转为 {@link MenuDto MenuDto} 对象
 */
class MenuEntityMapperTest extends IntegrationTest {
    // 注入 Menu 数据表访问对象
    @Autowired
    private MenuService menuService;

    // 注入 Menu 到 MenuDto 转换对象
    @Autowired
    private MenuEntityMapper menuMapper;

    /**
     * 测试 Menu 实体对象转换到 MenuDto 对象
     */
    @Test
    @Transactional
    void toDto_shouldMenuEntityToDto() {
        var role = newBuilder(RoleBuilder.class)
                .withName("R@II")
                .create();

        var permission = newBuilder(PermissionBuilder.class)
                .withName("MENU")
                .withResource("P@II-II")
                .withAction("READ")
                .create();

        // 为当前用户设置角色, 不设置必要权限, 所以 II-II-I 和 II-II—II 两个菜单不予返回
        SecurityContextHolder.setContext(new SecurityContextImpl(
            new NameAndPasswordAuthenticationToken(
                currentUser(),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + role.getName())))));

        // 构建数据库记录
        var menu1 = newBuilder(MenuBuilder.class).withText("I").create();
        newBuilder(MenuBuilder.class).withText("I-I").withParentId(menu1.getId()).create();
        newBuilder(MenuBuilder.class).withText("I-II").withParentId(menu1.getId()).create();
        newBuilder(MenuBuilder.class).withText("I-III").withParentId(menu1.getId()).create();

        var menu2 = newBuilder(MenuBuilder.class).withText("II").withRoleId(role.getId()).create();
        newBuilder(MenuBuilder.class).withText("II-I").withParentId(menu2.getId()).create();

        var menu2_2 = newBuilder(MenuBuilder.class).withText("II-II").withParentId(menu2.getId()).create();
        newBuilder(MenuBuilder.class)
                .withText("II-II-I").withParentId(menu2_2.getId()).withPermissionId(permission.getId()).create();
        newBuilder(MenuBuilder.class)
                .withText("II-II-II").withParentId(menu2_2.getId()).withPermissionId(permission.getId()).create();
        newBuilder(MenuBuilder.class).withText("II-II-III").withParentId(menu2_2.getId()).create();

        newBuilder(MenuBuilder.class).withText("II-III").withParentId(menu2.getId()).create();

        clearSessionCache();

        // 读取菜单项集合
        var menus = menuService.loadUserMenu(currentUser().getId());

        // 将菜单项集合转为 MenuDto 对象, 确认转换结果
        var menu = menuMapper.toDto(menus);
        then(menu.getItems()).extracting("text").containsExactly("I", "II");

        var menuItem = menu.getItems().get(0);
        then(menuItem.getItems()).extracting("text").containsExactly("I-I", "I-II", "I-III");

        menuItem = menu.getItems().get(1);
        then(menuItem.getRole()).isEqualTo("R@II");
        then(menuItem.getItems()).extracting("text").containsExactly("II-I", "II-II", "II-III");

        // II-II-I, II-II-II 两个菜单项因为权限不匹配不予返回
        menuItem = menu.getItems().get(1).getItems().get(1);
        then(menuItem.getItems()).extracting("text").containsExactly("II-II-III");
    }
}
