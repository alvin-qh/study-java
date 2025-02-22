package alvin.study.springboot.shiro.builder;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.shiro.infra.entity.RoleGrant;
import alvin.study.springboot.shiro.infra.entity.RoleGrantType;
import alvin.study.springboot.shiro.infra.mapper.RoleGrantMapper;

/**
 * 角色实体构建器类
 */
public class RoleGrantBuilder implements Builder<RoleGrant> {
    @Autowired
    private RoleGrantMapper roleGrantMapper;

    private Long userOrGroupId;
    private RoleGrantType type = RoleGrantType.USER;
    private Long roleId;

    /**
     * 设置用户或组 id
     */
    public RoleGrantBuilder withUserOrGroupId(Long userOrGroupId) {
        this.userOrGroupId = userOrGroupId;
        return this;
    }

    /**
     * 设置角色名称
     */
    public RoleGrantBuilder withType(RoleGrantType type) {
        this.type = type;
        return this;
    }

    /**
     * 设置角色名称
     */
    public RoleGrantBuilder withRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    @Override
    public RoleGrant build() {
        var roeGrant = new RoleGrant();
        roeGrant.setUserOrGroupId(userOrGroupId);
        roeGrant.setType(type);
        roeGrant.setRoleId(roleId);
        return roeGrant;
    }

    @Override
    public RoleGrant create() {
        var roeGrant = build();
        roleGrantMapper.insert(roeGrant);
        return roeGrant;
    }
}
