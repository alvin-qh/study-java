package alvin.study.springboot.shiro.builder;

import alvin.study.springboot.shiro.infra.entity.RolePermission;
import alvin.study.springboot.shiro.infra.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 权限实体构建器类
 */
public class RolePermissionBuilder implements Builder<RolePermission> {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    private Long roleId;
    private Long permissionId;

    /**
     * 设置角色 id
     */
    public RolePermissionBuilder withRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    /**
     * 设置权限 id
     */
    public RolePermissionBuilder withPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    @Override
    public RolePermission build() {
        var rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        return rolePermission;
    }

    @Override
    public RolePermission create() {
        var rolePermission = build();
        rolePermissionMapper.insert(rolePermission);
        return rolePermission;
    }
}
