package alvin.study.core.security.auth;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import alvin.study.infra.entity.User;

/**
 * 对用户名密码进行验证的 {@link Authentication} 接口对象
 */
public class NameAndPasswordAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 用户实体对象, 对用户名密码验证完毕后产生的新对象具备此属性
     */
    private final User user;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 通过用户名密码实例化对象
     *
     * @param username 用户名
     * @param password 密码
     */
    public NameAndPasswordAuthenticationToken(String username, String password) {
        super(List.of());
        this.username = username;
        this.password = password;
        this.user = null;
        super.setAuthenticated(false);
    }

    /**
     * 通过 {@link User} 对象实例化
     *
     * @param user        用户实体对象
     * @param authorities 角色和权限集合
     */
    public NameAndPasswordAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.username = null;
        this.password = null;
        super.setAuthenticated(true);
    }

    /**
     * 获取用户主体信息
     *
     * <p>
     * 如果 {@link #isAuthenticated()} 方法返回 {@code false}, 表示尚未进行认证, 此时该方法返回用户用户名;
     * 否则表示已认证, 此时返回 {@link User} 对象
     * </p>
     */
    @Override
    public Object getPrincipal() { return user; }

    /**
     * 获取用户凭证, 这里是密码
     *
     * <p>
     * 如果 {@link #isAuthenticated()} 方法返回 {@code false}, 表示尚未进行认证, 此时该方法返回用户的密码信息;
     * 否则表示已认证, 此时返回 {@code null}
     * </p>
     */
    @Override
    public Object getCredentials() { return password; }

    /**
     * 获取用户名信息
     *
     * @return 登录的用户名
     */
    @Override
    public String getName() {
        if (isAuthenticated()) {
            return user.getAccount();
        }

        return username;
    }

    /**
     * 设置是否已通过认证, 该方法在当前类型无效
     */
    @Override
    public void setAuthenticated(boolean authenticated) {
        // 不允许改变是否信任的设置
    }

    /**
     * 取消认证凭证
     */
    @Override
    public void eraseCredentials() {
        if (user == null) {
            this.password = null;
        }
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(user, username, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof NameAndPasswordAuthenticationToken other)) {
            return false;
        }

        return Objects.equals(user, other.user)
               && Objects.equals(username, other.username)
               && Objects.equals(password, other.password);
    }

}
