package alvin.study.springboot.shiro.builder;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.shiro.infra.entity.Role;
import alvin.study.springboot.shiro.infra.mapper.RoleMapper;

/**
 * 角色实体构建器类
 */
public class RoleBuilder implements Builder<Role> {
    @Autowired
    private RoleMapper roleMapper;

    private String name = "ADMIN";

    /**
     * 设置角色名称
     */
    public RoleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Role build() {
        var role = new Role();
        role.setName(name);
        return role;
    }

    @Override
    public Role create() {
        var role = build();
        roleMapper.insert(role);
        return role;
    }
}
