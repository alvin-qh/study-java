package alvin.study.springboot.security.builder;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.security.infra.entity.Menu;
import alvin.study.springboot.security.infra.mapper.MenuMapper;

/**
 * 菜单实体构建类
 */
public class MenuBuilder implements Builder<Menu> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private MenuMapper mapper;

    private int order = SEQUENCE.incrementAndGet();
    private String text = "Menu";
    private String icon = "fa_icon";
    private Long parentId;
    private Long roleId;
    private Long permissionId;

    public MenuBuilder withOrder(int order) {
        this.order = order;
        return this;
    }

    public MenuBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public MenuBuilder withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public MenuBuilder withParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public MenuBuilder withRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public MenuBuilder withPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    @Override
    public Menu build() {
        var menu = new Menu();
        menu.setOrder(order);
        menu.setText(text);
        menu.setIcon(icon);
        menu.setParentId(parentId);
        menu.setRoleId(roleId);
        menu.setPermissionId(permissionId);
        return menu;
    }

    @Override
    public Menu create() {
        var menu = build();
        mapper.insert(menu);
        return menu;
    }
}
