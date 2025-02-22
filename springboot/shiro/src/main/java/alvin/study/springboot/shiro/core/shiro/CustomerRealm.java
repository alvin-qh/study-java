package alvin.study.springboot.shiro.core.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import alvin.study.springboot.shiro.app.domain.service.AuthService;
import alvin.study.springboot.shiro.app.endpoint.AuthController;
import alvin.study.springboot.shiro.app.endpoint.model.LoginForm;
import alvin.study.springboot.shiro.conf.ShiroConfig;
import alvin.study.springboot.shiro.infra.entity.User;

/**
 * 本类型是 Shiro 的核心类型, 用于获取登录用户信息以及用户的角色权限信息
 *
 * <p>
 * {@link #doGetAuthenticationInfo(AuthenticationToken)} 方法根据用户的凭证对象, 获取用户信息
 * {@link AuthorizationInfo} 对象. 本例中支持的用户凭证有两种:
 * <ul>
 * <li>
 * {@link UsernamePasswordToken} 凭证, 通过
 * {@link AuthController#postLogin(LoginForm)
 * AuthController.postLogin(LoginForm)} 方法产生, 携带用户名和密码
 * </li>
 * <li>
 * {@link JwtAuthenticationToken} 凭证, 通过后续的请求 Header 中携带, 由
 * {@link TokenAuthenticationFilter#isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object)
 * TokenAuthenticationFilter.isAccessAllowed(ServletRequest, ServletResponse,
 * Object)} 方法产生, 携带登录认证的 JWT
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link #doGetAuthorizationInfo(PrincipalCollection)}
 * 方法则是对已经通过认知的用户进一步获取其权限和角色
 * </p>
 *
 * <p>
 * 在处理过程中可以通过 Cache 来提升处理速度, 参考 {@link RedisCacheManager} 类型, 另外,
 * 是否要对登录用户信息进行缓存, 需要设置 {@link #setAuthenticationCachingEnabled(boolean)} 为
 * {@code true}
 * </p>
 *
 * <p>
 * 参考 {@link ShiroConfig#realm() ShiroConfig.realm()} 方法
 * </p>
 */
public class CustomerRealm extends AuthorizingRealm {
    // 注入认证服务类对象
    private final AuthService authService;

    /**
     * 构造器
     *
     * @param cacheManager 缓存管理器对象, 参考 {@link RedisCacheManager} 类型
     * @param matcher      凭证校验器对象, 参考 {@link CustomerCredentialsMatcher} 类型
     * @param authService  认证服务对象, 参考 {@link AuthService} 类型
     */
    public CustomerRealm(CacheManager cacheManager, CredentialsMatcher matcher, AuthService authService) {
        super(cacheManager, matcher);
        this.authService = authService;
    }

    /**
     * 指定当前 Realm 类型可处理的凭证类型
     *
     * <p>
     * 如果缺少该方法, 则指定的凭证无法被当前类型处理
     * </p>
     *
     * @param token 凭证对象
     * @return {@code true} 表示所给凭证可以被当前类型处理
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken || token instanceof JwtAuthenticationToken;
    }

    /**
     * 获取当前登录用户角色权限信息
     *
     * <p>
     * 对于一般应用, 用户只有主要登录身份, 通过 {@link PrincipalCollection#getPrimaryPrincipal()} 获得,
     * 如果需要支持多身份, 则其它身份可以通过遍历 {@link PrincipalCollection} 集合来获取
     * </p>
     *
     * @param principalCollection 登录用户身份信息
     * @return 角色权限信息集合
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取用户主要登录身份对象, 也是本例中用户唯一登录身份
        var user = (User) principalCollection.getPrimaryPrincipal();

        // 根据用户的 id 获取其角色权限信息
        var rolePermission = authService.findRoleAndPermissionsByUserId(user.getId());

        // 构建用户角色权限信息集合对象
        var authInfo = new SimpleAuthorizationInfo();
        authInfo.setRoles(rolePermission.toRoleStrings());
        authInfo.setStringPermissions(rolePermission.toPermissionStrings());

        return authInfo;
    }

    /**
     * 根据凭证对象获取登录用户信息
     *
     * <p>
     * 本方法只需根据用户凭证获取注册用户的信息即可, 无需对用户的凭证进行校验, 教研工作会交由
     * {@link CustomerCredentialsMatcher} 类型来完成, 当然也可以在本方法中进行校验, 此时无需再当前类构造器中定义
     * {@link CredentialsMatcher} 参数
     * </p>
     *
     * @param authToken 登录凭证
     * @return 登录用户信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        User user = null;
        if (authToken instanceof UsernamePasswordToken upToken) {
            // 如果用户凭证为用户名和密码, 则根据用户名获取用户实体对象
            user = authService.findUserByAccount(upToken.getUsername());
        } else if (authToken instanceof JwtAuthenticationToken jwtToken) {
            // 如果用户凭证为 JWT, 则对 JWT 进行解码操作
            user = authService.decodeJwtToken((String) jwtToken.getPrincipal());
        } else {
            throw new IllegalArgumentException("authToken");
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 获取角色权限缓存对象的 key
     *
     * <p>
     * 参考 {@link RedisCacheManager#getCache(String)} 方法
     * </p>
     */
    @Override
    public String getAuthorizationCacheName() { return "authorization"; }

    /**
     * 获取缓存角色权限信息的 key 值
     */
    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        var user = (User) principals.getPrimaryPrincipal();
        return user.getId();
    }

    /**
     * 获取登录信息缓存的 key
     *
     * <p>
     * 参考 {@link RedisCacheManager#getCache(String)} 方法
     * </p>
     */
    @Override
    public String getAuthenticationCacheName() { return "authentication"; }

    /**
     * 获取缓存登录信息的 key 值
     */
    @Override
    protected Object getAuthenticationCacheKey(AuthenticationToken token) {
        return token.getPrincipal();
    }
}
