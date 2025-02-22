package alvin.study.springboot.security.core.security.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import alvin.study.springboot.security.infra.entity.User;

/**
 * 对 JWT 字符串进行验证的 {@link Authentication} 接口类型
 */
@Deprecated(forRemoval = true, since = "3.0")
public class JwtAuthenticationToken implements Authentication {
    /**
     * 存储 JWT 字符串
     */
    private final String jwt;

    /**
     * 存储 Detail 信息
     */
    private transient Object details;

    /**
     * 构造器
     *
     * @param user        验证通过后得到的 {@link User} 对象
     * @param authorities 登录用户的权限列表
     */
    public JwtAuthenticationToken(String jwt) {
        this.jwt = jwt;
    }

    /**
     * 获取用户名称, 该方法在此类型中无效
     */
    @Override
    public String getName() { return null; }

    /**
     * 获取用户角色和权限集合, 该方法在此类型中无效
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    /**
     * 获取账号主体, 该方法在此类型中无效
     */
    @Override
    public Object getPrincipal() { return null; }

    /**
     * 获取登录凭证, 返回 JWT 字符串
     *
     * @return JWT 字符串
     */
    @Override
    public Object getCredentials() { return jwt; }

    /**
     * 获取登录明星, 此方法在该类型中无效
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
     * 该类型不能设置已认证过状态
     */
    @Override
    public boolean isAuthenticated() { return true; }

    /**
     * 设置已认证状态, 该方法在该类型中无效
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { /* Ignore */ }
}
