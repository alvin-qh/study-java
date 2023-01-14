package alvin.study.core.security.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import alvin.study.infra.entity.User;

/**
 * 对承载用户名/密码, JWT 或者 {@link User}/{@link UserDetails} 对象的凭证对象进行认证操作
 */
@SuppressWarnings("java:S1948")
public class CustomAuthenticationToken implements Authentication, CredentialsContainer {
    // 用户信息主体, 本例中可以为用户名, User 对象或 UserDetails 对象
    private final Object principal;

    // 用户凭证, 本例中可以为密码, jwt 字符串
    private Object credentials;

    // 登录信息对象
    private Object details;

    // 凭证类型枚举值
    private Type type;

    /**
     * 构造器, 对所有的字段进行初始化
     *
     * @param principal   账号主体
     * @param credentials 账号凭证
     * @param type        凭证类型
     */
    protected CustomAuthenticationToken(Object principal, Object credentials, Type type) {
        this.principal = principal;
        this.credentials = credentials;
        this.type = type;
    }

    /**
     * 通过用户名, 密码构造凭证对象
     *
     * @param username 用户名
     * @param password 密码
     * @param isBasic  是否为 basic auth 凭证
     */
    public CustomAuthenticationToken(String username, String password, boolean isBasic) {
        this(username, password, isBasic ? Type.BASIC : Type.NAME_PASS);
    }

    /**
     * 通过 jwt 字符串构造凭证对象
     *
     * @param jwt jwt 字符串
     */
    public CustomAuthenticationToken(String jwt) {
        this(null, jwt, Type.JWT);
    }

    /**
     * 通过 {@link User} 对象构造凭证对象
     *
     * @param user 用户实体对象
     */
    public CustomAuthenticationToken(User user) {
        this(user, null, Type.USER);
    }

    /**
     * 通过 {@link UserDetails} 对象构造凭证对象
     *
     * @param user 用户信息对象
     */
    public CustomAuthenticationToken(UserDetails user) {
        this(user, null, Type.USER);
    }

    /**
     * 获取用户名
     *
     * @return 用户名字符串
     */
    @Override
    public String getName() {
        return switch (type) {
        case BASIC, NAME_PASS -> (String) principal;
        case USER -> {
            if (principal instanceof User user) {
                yield user.getUsername();
            } else if (principal instanceof UserDetails de) {
                yield de.getUsername();
            } else {
                yield "";
            }
        }
        default -> "";
        };
    }

    /**
     * 获取凭证类型
     *
     * @return {@link Type} 枚举, 表示凭证类型
     */
    public Type getType() { return type; }

    /**
     * 获取当前凭证相关的角色和权限
     *
     * <p>
     * 本例中不返回有效的角色权限
     * </p>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    /**
     * 获取账号主体
     *
     * <p>
     * 根据 {@link #getType()} 方法的结果不同, 有可能返回 {@code username}, {@link User} 或
     * {@link UserDetails} 对象
     * </p>
     */
    @Override
    public Object getPrincipal() { return principal; }

    /**
     * 获取凭证, 根据 {@link #getType()} 方法的结果不同, 有可能会返回 {@code password} 或 JWT 字符串
     */
    @Override
    public Object getCredentials() { return credentials; }

    /**
     * 获取登录详情对象
     */
    @Override
    public Object getDetails() { return details; }

    /**
     * 设置登录详情对象
     *
     * @param details 登录详情对象, 参考
     *                {@link org.springframework.security.web.authentication.WebAuthenticationDetailsSource#buildDetails(javax.servlet.http.HttpServletRequest)
     *                WebAuthenticationDetailsSource.buildDetails(HttpServletRequest)}
     *                方法
     */
    public void setDetails(Object details) { this.details = details; }

    /**
     * 获取当前凭证是否已被认证, 如果返回 {@code true} 表示无需再次认证
     */
    @Override
    public boolean isAuthenticated() { return type == Type.USER; }

    /**
     * 禁止修改已认证状态
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { /* Ignore */ }

    /**
     * 清除凭证信息
     */
    @Override
    public void eraseCredentials() {
        if (type == Type.BASIC || type == Type.NAME_PASS) {
            this.credentials = null;
        }
    }

    /**
     * 表示不同含义用户凭证的类型
     */
    public enum Type {
        /**
         * 承载 JWT 字符串的凭证
         */
        JWT,

        /**
         * 承载用户名密码, 通过 Basic Auth 形成的凭证
         */
        BASIC,

        /**
         * 承载用户名密码, 通过 /auth/login 登录形成的凭证
         */
        NAME_PASS,

        /**
         * 已经完成认证, 存储 User 或 UserDetails 对象的已认证凭证
         */
        USER
    }
}
