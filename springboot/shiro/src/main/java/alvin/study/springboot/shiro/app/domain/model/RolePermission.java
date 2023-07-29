package alvin.study.springboot.shiro.app.domain.model;

import alvin.study.springboot.shiro.infra.entity.Permission;
import alvin.study.springboot.shiro.infra.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 记录一个用户相关角色权限集合的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission implements Serializable {
    /**
     * 相关用户
     */
    private Long userId;

    /**
     * 用户具备的角色集合
     */
    private Collection<Role> roles;

    /**
     * 用户具备的权限集合
     */
    private Collection<Permission> permissions;

    /**
     * 以字符串获取所有角色
     *
     * @return 角色名称集合
     */
    public Set<String> toRoleStrings() {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    /**
     * 以字符串获取所有权限
     *
     * @return 权限名称集合
     */
    public Set<String> toPermissionStrings() {
        return permissions.stream().map(Permission::getPermission).collect(Collectors.toSet());
    }
}
