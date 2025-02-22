package alvin.study.springboot.security.app.domain.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTVerificationException;

import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.security.core.cache.Cache;
import alvin.study.springboot.security.core.security.exception.UserIdentityNotFoundException;
import alvin.study.springboot.security.infra.entity.Group;
import alvin.study.springboot.security.infra.entity.User;
import alvin.study.springboot.security.infra.mapper.GroupMapper;
import alvin.study.springboot.security.infra.mapper.PermissionMapper;
import alvin.study.springboot.security.infra.mapper.RoleMapper;
import alvin.study.springboot.security.infra.mapper.UserMapper;
import alvin.study.springboot.security.util.security.Jwt;
import alvin.study.springboot.security.util.security.PasswordEncoder;

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

    // 注入缓存对象
    private final Cache cache;

    // 注入 JWT 编解码对象
    private final Jwt jwt;

    // 注入密码处理工具类对象
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户 ID 查询用户实体对象
     *
     * @param id 用户 ID
     * @return 用户实体 {@link User} 对象
     * @throws UsernameNotFoundException 查无此用户 ID
     */
    @Transactional(readOnly = true)
    public User findUserById(long id) {
        var user = userMapper.selectById(id);
        if (user == null) {
            throw new UserIdentityNotFoundException(id);
        }
        return user;
    }

    /**
     * 根据 JWT token 获取用户对象
     *
     * @param token JWT token 对象
     * @return 用于对象
     */
    @Transactional(readOnly = true)
    public User decodeJwtToken(String token) {
        if (Strings.isNullOrEmpty(token)) {
            throw new BadCredentialsException("Invalid jwt token");
        }

        return cache.loadUser(token).orElseGet(() -> {
            try {
                // 验证 token 并解码
                var payload = jwt.verify(token);
                var userId = Long.parseLong(payload.getIssuer());

                // 根据 JWT token 中携带的用户 id 进行查询
                return cache.saveUser(token, findUserById(userId), jwt.getPeriod());
            } catch (JWTVerificationException e) {
                throw new BadCredentialsException("Invalid jwt token");
            }
        });
    }

    /**
     * 根据用户账号查询用户实体对象
     *
     * @param account 用户账号
     * @return 用户实体 {@link User} 对象
     * @throws UsernameNotFoundException 查无此用户账号
     */
    @Transactional(readOnly = true)
    public User findUserByAccount(String account) {
        return userMapper.selectByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException(account));
    }

    /**
     * 用户登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户实体对象
     */
    @Transactional(readOnly = true)
    public User login(String username, String password) {
        var user = findUserByAccount(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return user;
    }

    /**
     * 根据所给的用户 id 查询该用户的所有角色和权限
     *
     * <p>
     * 角色或权限均是通过 {@link GrantedAuthority} 接口的对象表示的, 该接口的实现类为
     * {@link SimpleGrantedAuthority} 类型, 通过一个字符串表示角色和权限
     * </p>
     *
     * <p>
     * 角色和权限的含义不同, 但都是通过 {@link SimpleGrantedAuthority} 对象中保存的字符串来实现的, 区别在于角色的字符串以
     * {@code "ROLE_"} 开头
     * </p>
     *
     * @param userId 用户 id
     * @return 该用户的全部权限集合
     */
    @Transactional(readOnly = true)
    public Collection<GrantedAuthority> findPermissionsByUserId(Long userId) {
        return cache.loadAuthorities(userId).orElseGet(() -> {
            // 保存所有角色的集合

            // 查询和用户 ID 相关的所有角色
            var roles = new ArrayList<>(roleMapper.selectByUserId(userId));

            // 获取用户所在的组 id
            var groupIds = groupMapper.selectByUserId(userId)
                    .stream()
                    .map(Group::getId)
                    .toList();
            if (!groupIds.isEmpty()) {
                roles.addAll(roleMapper.selectByGroupIds(groupIds));
            }

            var roleIds = new ArrayList<Long>(roles.size());
            var authorities = new ArrayList<GrantedAuthority>(roles.size());
            for (var role : roles) {
                // 记录 role id
                roleIds.add(role.getId());
                // 记录权限
                authorities.add(buildRoleAuthority(role.getName()));
            }

            if (!roleIds.isEmpty()) {
                // 根据角色 id 列表获取全部权限
                permissionMapper.selectByRoleIds(roleIds)
                        .stream()
                        .distinct()
                        .forEach(p -> authorities.add(buildPermissionAuthority(p.getPermission())));
            }

            return cache.saveAuthorities(userId, authorities);
        });
    }

    /**
     * 根据角色名称构建角色权限对象
     *
     * @param roleName 角色名称
     * @return 表示角色的权限对象
     */
    private GrantedAuthority buildRoleAuthority(String roleName) {
        return new SimpleGrantedAuthority("ROLE_" + roleName);
    }

    /**
     * 利用权限名称构建权限对象
     *
     * @param permission 权限名称
     * @return 表示权限的对象
     */
    private GrantedAuthority buildPermissionAuthority(String permission) {
        return new SimpleGrantedAuthority(permission);
    }
}
