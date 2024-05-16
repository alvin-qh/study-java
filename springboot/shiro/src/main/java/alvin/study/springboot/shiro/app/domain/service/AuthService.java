package alvin.study.springboot.shiro.app.domain.service;

import alvin.study.springboot.shiro.app.domain.model.RolePermission;
import alvin.study.springboot.shiro.infra.entity.Group;
import alvin.study.springboot.shiro.infra.entity.Permission;
import alvin.study.springboot.shiro.infra.entity.Role;
import alvin.study.springboot.shiro.infra.entity.User;
import alvin.study.springboot.shiro.infra.mapper.GroupMapper;
import alvin.study.springboot.shiro.infra.mapper.PermissionMapper;
import alvin.study.springboot.shiro.infra.mapper.RoleMapper;
import alvin.study.springboot.shiro.infra.mapper.UserMapper;
import alvin.study.springboot.shiro.util.security.Jwt;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 登录验证的服务类
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    // 注入用户 Mapper 对象
    private final UserMapper userMapper;

    // 注入角色 Mapper 对象
    private final RoleMapper roleMapper;

    // 注入权限 Mapper 对象
    private final PermissionMapper permissionMapper;

    // 注入用户组 Mapper 对象
    private final GroupMapper groupMapper;

    // 注入 JWT 编解码对象
    private final Jwt jwt;

    /**
     * 根据用户 ID 查询用户实体对象
     *
     * @param id 用户 ID
     * @return 用户实体 {@link User} 对象
     * @throws UnknownAccountException 查无此用户 ID
     */
    @Transactional(readOnly = true)
    public User findUserById(long id) {
        return Optional.ofNullable(userMapper.selectById(id))
            .orElseThrow(UnknownAccountException::new);
    }

    /**
     * 根据 JWT token 获取用户对象
     *
     * @param token JWT token 对象
     * @return 用于对象
     * @throws AuthenticationException token 认证失败
     */
    @Transactional(readOnly = true)
    public User decodeJwtToken(String token) {
        try {
            // 验证 token 并解码
            var payload = jwt.verify(token);
            var userId = Long.parseLong(payload.getIssuer());

            // 根据 JWT token 中携带的用户 id 进行查询
            return findUserById(userId);
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("Invalid token");
        }
    }

    /**
     * 根据用户账号查询用户实体对象
     *
     * @param account 用户账号
     * @return 用户实体 {@link User} 对象
     * @throws UnknownAccountException 查无此用户账号
     */
    @Transactional(readOnly = true)
    public User findUserByAccount(String account) {
        return userMapper.selectByAccount(account)
            .orElseThrow(UnknownAccountException::new);
    }

    /**
     * 根据所给的用户 id 查询该用户的所有角色和权限
     *
     * @param userId 用户 id
     * @return 该用户的全部角色权限集合
     */
    @Transactional(readOnly = true)
    public RolePermission findRoleAndPermissionsByUserId(Long userId) {
        // 保存所有角色的集合
        var roles = new ArrayList<Role>();

        // 查询和用户 ID 相关的所有角色
        roles.addAll(roleMapper.selectByUserId(userId));

        // 获取用户所在的组 id
        var groupIds = groupMapper.selectByUserId(userId)
            .stream()
            .map(Group::getId)
            .toList();
        if (!groupIds.isEmpty()) {
            roles.addAll(roleMapper.selectByGroupIds(groupIds));
        }

        var permissions = List.<Permission>of();
        if (!roles.isEmpty()) {
            // 根据角色 id 列表获取全部权限
            permissions = permissionMapper.selectByRoleIds(
                roles.stream().map(Role::getId).toList());
        }
        return new RolePermission(userId, roles, permissions);
    }
}
