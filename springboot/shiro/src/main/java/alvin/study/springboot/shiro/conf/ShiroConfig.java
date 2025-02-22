package alvin.study.springboot.shiro.conf;

import java.time.Duration;
import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.ShiroRequestMappingConfig;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import alvin.study.springboot.shiro.app.domain.service.AuthService;
import alvin.study.springboot.shiro.app.domain.service.SessionService;
import alvin.study.springboot.shiro.core.shiro.CustomerCredentialsMatcher;
import alvin.study.springboot.shiro.core.shiro.CustomerRealm;
import alvin.study.springboot.shiro.core.shiro.RedisCacheManager;
import alvin.study.springboot.shiro.core.shiro.RedisSessionDAO;
import alvin.study.springboot.shiro.core.shiro.RedisSessionManager;
import alvin.study.springboot.shiro.core.shiro.SessionUtil;
import alvin.study.springboot.shiro.core.shiro.TokenAuthenticationFilter;
import alvin.study.springboot.shiro.util.security.PasswordEncoder;

/**
 * 对 Shiro 框架进行配置
 */
@Import({
    ShiroBeanConfiguration.class,
    // ShiroAnnotationProcessorConfiguration.class,
    ShiroWebConfiguration.class,
    ShiroWebFilterConfiguration.class,
    ShiroRequestMappingConfig.class
})
@Configuration("conf/shiro")
public class ShiroConfig {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SessionUtil sessionUtil;

    /**
     * 缓存管理器对象
     */
    @Bean
    CacheManager cacheManager() {
        return new RedisCacheManager(redisTemplate);
    }

    /**
     * 创建 {@link Realm} 对象, 用于对登录信息进行验证和处理
     *
     * @return {@link CustomerRealm} 类型对象
     */
    @Bean
    Realm realm(
            CacheManager cacheManager,
            PasswordEncoder passwordEncoder,
            AuthService authService) {
        var realm = new CustomerRealm(
            // 注入缓存管理器对象
            cacheManager,
            // 注入密码比较对象
            new CustomerCredentialsMatcher(passwordEncoder),
            // 注入认证服务对象
            authService);

        // 允许缓存用户登录信息
        realm.setAuthenticationCachingEnabled(true);
        return realm;
    }

    /**
     * 产生 Session 管理器对象
     *
     * @param period Session 过期时间
     * @return {@link RedisSessionManager} 对象, 通过 Redis 管理 Session
     */
    @Bean
    SessionManager sessionManager(
            @Value("${application.security.session.period}") String period,
            SessionService sessionService) {
        // 实例化 Session 存储对象
        var sessionDAO = new RedisSessionDAO(redisTemplate, Duration.parse(period), sessionService);
        // 实例化 Session 管理器对象
        return new RedisSessionManager(sessionDAO, sessionUtil);
    }

    /**
     * 设置过滤器
     *
     * <table>
     * <tr>
     * <th>简写</th>
     * <th>名称</th>
     * <th>优先级</th>
     * <th>说明</th>
     * <th>默认类型</th>
     * </tr>
     * <tr>
     * <td>{@code anon}</td>
     * <td>匿名拦截器</td>
     * <td>1</td>
     * <td>无需登录状态即可访问 (例如静态资源)</td>
     * <td>{@link org.apache.shiro.web.filter.authc.AnonymousFilter
     * AnonymousFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code authc}</td>
     * <td>登录拦截器</td>
     * <td>2</td>
     * <td>必须登录状态方可访问</td>
     * <td>{@link org.apache.shiro.web.filter.authc.FormAuthenticationFilter
     * FormAuthenticationFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code authcBasic}</td>
     * <td>基本认证拦截器</td>
     * <td>3</td>
     * <td>在 http 头中通过 {@code basic <name>:<password>} 的 base64 编码作为认证字符串</td>
     * <td>{@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
     * BasicHttpAuthenticationFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code logout}</td>
     * <td>登出拦截器</td>
     * <td>4</td>
     * <td>用户登出的拦截器, 登出后重定向到指定地址</td>
     * <td>{@link org.apache.shiro.web.filter.authc.LogoutFilter LogoutFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code noSessionCreation}</td>
     * <td>不保存会话状态拦截器</td>
     * <td>5</td>
     * <td>通过该拦截器的地址不会创建 Session 存储</td>
     * <td>{@link org.apache.shiro.web.filter.session.NoSessionCreationFilter
     * NoSessionCreationFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code prems}</td>
     * <td>权限拦截器</td>
     * <td>6</td>
     * <td>验证用户是否具备指定权限拦截器</td>
     * <td>{@link org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
     * PermissionsAuthorizationFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code port}</td>
     * <td>端口拦截器</td>
     * <td>7</td>
     * <td>对非指定端口的访问进行重定向</td>
     * <td>{@link org.apache.shiro.web.filter.authz.PortFilter PortFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code rest}</td>
     * <td>rest 风格拦截器</td>
     * <td>8</td>
     * <td>自动根据请求方法构建权限字符串构建权限字符串</td>
     * <td>{@link org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
     * HttpMethodPermissionFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code roles}</td>
     * <td>角色拦截器</td>
     * <td>9</td>
     * <td>验证用户是否拥有指定角色</td>
     * <td>{@link org.apache.shiro.web.filter.authz.RolesAuthorizationFilter
     * RolesAuthorizationFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code ssl}</td>
     * <td>SSL 拦截器</td>
     * <td>10</td>
     * <td>对于 {@code 80} 端口的请求重定向到 {@code 443} 端口</td>
     * <td>{@link org.apache.shiro.web.filter.authz.SslFilter SslFilter}</td>
     * </tr>
     * <tr>
     * <td>{@code user}</td>
     * <td>用户拦截器</td>
     * <td>11</td>
     * <td>用户拦截器, 用户已经身份验证/记住我登录的都可</td>
     * <td>{@link org.apache.shiro.web.filter.authc.UserFilter UserFilter}</td>
     * </tr>
     * </table>
     *
     * @param securityManager 安全管理器对象
     * @return 过滤器工厂对象
     */
    @Bean
    ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ObjectMapper objectMapper) {
        var filterFactoryBean = new ShiroFilterFactoryBean();

        // 设置安全管理器对象
        filterFactoryBean.setSecurityManager(securityManager);

        // 设置过滤器, 将 authc 拦截器进行替换
        filterFactoryBean.getFilters().putAll(
            Map.of(
                "authc", new TokenAuthenticationFilter(sessionUtil, objectMapper)));

        // 设置登录 URI, 该地址可以直接访问
        filterFactoryBean.setLoginUrl("/auth/login");

        // 设置各类 URI 的拦截方式
        filterFactoryBean.getFilterChainDefinitionMap().putAll(Map.of(
            // 设置可以匿名访问的地址
            "/static/**", "anon",
            // 设置必须登录的地址
            "/**", "authc"));

        return filterFactoryBean;
    }

    /**
     * 支持角色权限注解
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    @ConditionalOnMissingBean
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        var creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 支持角色权限注解
     */
    @Bean
    @ConditionalOnMissingBean
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        var advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
