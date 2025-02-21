package alvin.study.springboot.springdoc.core.security.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

import alvin.study.springboot.springdoc.infra.entity.User;

/**
 * 对承载用户名/密码, JWT 或者 {@link User}/{@link UserDetails} 对象的凭证对象进行认证操作
 */
@Getter
public class CustomAuthenticationToken implements Authentication, CredentialsContainer {
    private final Type type;
    // 用户信息主体, 本例中可以为用户名, User 对象或 UserDetails 对象
    private Object principal;

    /**
     * 构造器, 对所有的字段进行初始化
     *
     * @param principal 账号主体
     * @param type      凭证类型
     */
    public CustomAuthenticationToken(Object principal, Type type) {
        this.principal = principal;
        this.type = type;
    }

    /**
     * 获取用户名
     *
     * @return 用户名字符串
     */
    @Override
    public String getName() {
        return switch (type) {
            case BASIC -> ((UserDetails) principal).getUsername();
            case USER -> ((User) principal).getUsername();
        };
    }

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
     * 获取凭证, 根据 {@link #getType()} 方法的结果不同, 有可能会返回 {@code password} 或 JWT 字符串
     */
    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("getCredentials() is not supported");
    }

    /**
     * 获取登录详情对象
     */
    @Override
    public Object getDetails() {
        throw new UnsupportedOperationException("getDetails() is not supported");
    }

    /**
     * 获取当前凭证是否已被认证, 如果返回 {@code true} 表示无需再次认证
     */
    @Override
    public boolean isAuthenticated() {
        return principal != null;
    }

    /**
     * 禁止修改已认证状态
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("setAuthenticated() is not supported");
    }

    /**
     * 清除凭证信息
     */
    @Override
    public void eraseCredentials() {
        principal = null;
    }

    /**
     * 表示不同含义用户凭证的类型
     */
    public enum Type {
        /**
         * 承载用户名密码, 通过 Basic Auth 形成的凭证
         */
        BASIC,
        /**
         * 已经完成认证, 存储 User 凭证
         */
        USER
    }
}
