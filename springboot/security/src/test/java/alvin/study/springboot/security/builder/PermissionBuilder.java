package alvin.study.springboot.security.builder;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.security.infra.entity.Permission;
import alvin.study.springboot.security.infra.mapper.PermissionMapper;

/**
 * 权限实体构建器类
 */
public class PermissionBuilder implements Builder<Permission> {
    @Autowired
    private PermissionMapper permissionMapper;

    private String name = "COMMON";
    private String resource = "USER";
    private String action = "READ";

    /**
     * 设置权限名称
     */
    public PermissionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置资源名称
     */
    public PermissionBuilder withResource(String resource) {
        this.resource = resource;
        return this;
    }

    /**
     * 设置行为名称
     */
    public PermissionBuilder withAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public Permission build() {
        var permission = new Permission();
        permission.setName(name);
        permission.setResource(resource);
        permission.setAction(action);
        return permission;
    }

    @Override
    public Permission create() {
        var permission = build();
        permissionMapper.insert(permission);
        return permission;
    }
}
