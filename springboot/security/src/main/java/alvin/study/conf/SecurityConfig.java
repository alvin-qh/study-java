package alvin.study.conf;

import alvin.study.app.domain.service.AuthService;
import alvin.study.core.security.auth.JwtAuthenticationProvider;
import alvin.study.core.security.auth.NameAndPasswordAuthenticationProvider;
import alvin.study.core.security.filter.AuthenticationErrorHandlerEntryPoint;
import alvin.study.core.security.filter.JwtRequestFilter;
import alvin.study.core.security.handler.AclPermissionEvaluator;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Advisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 配置 Spring Security
 *
 * <p>
 * {@link EnableWebSecurity @EnableWebSecurity} 注解用于开启 Spring Security
 * </P>
 *
 * <p>
 * {@link EnableGlobalMethodSecurity @EnableGlobalMethodSecurity}
 * 注解表示使用方法级别的权限控制, 添加此注解后, 即可以使用
 * {@link org.springframework.security.access.prepost.PreAuthorize @PreAuthorize}
 * 等注解对方法进行权限控制
 * </p>
 *
 * <p>
 * 前期 Spring Boot 版本的配置类需继承
 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
 * WebSecurityConfigurerAdapter} 类型, 当前版本无需继承该类, 定义所需的 bean 方法即可
 * </p>
 *
 * <p>
 * {@link AuthenticationManager} 对象用于对
 * {@link org.springframework.security.core.Authentication Authentication}
 * 对象进行验证, {@link org.springframework.security.core.Authentication
 * Authentication} 类型对象用于存储用户登录信息, 参考
 * {@link alvin.study.core.security.auth.JwtAuthenticationToken
 * JwtAuthenticationToken} 和
 * {@link alvin.study.core.security.auth.NameAndPasswordAuthenticationToken
 * NameAndPasswordAuthenticationToken} 类型
 * </p>
 *
 * <p>
 * {@link JwtAuthenticationProvider} 用于对
 * {@link alvin.study.core.security.auth.JwtAuthenticationToken
 * JwtAuthenticationToken} 对象进行验证; {@link NameAndPasswordAuthenticationProvider}
 * 用于对
 * {@link alvin.study.core.security.auth.NameAndPasswordAuthenticationToken
 * NameAndPasswordAuthenticationToken} 对象进行验证
 * </p>
 *
 * <p>
 * {@link SecurityFilterChain} 对象配置请求的过滤器集合, 本例中通过 {@link JwtRequestFilter}
 * 拦截请求中的 JWT token; 另外 {@link AuthenticationErrorHandlerEntryPoint} 对象用于对
 * {@link org.springframework.security.core.AuthenticationException
 * AuthenticationException} 异常进行拦截
 * </p>
 */
@Configuration("conf/security")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class SecurityConfig {
    /**
     * 配置 {@link org.springframework.security.authentication.ProviderManager
     * ProviderManager} 对象
     *
     * <p>
     * 需要设置 {@link org.springframework.security.core.Authentication Authentication}
     * 验证信息对象的验证器 Provider
     * </p>
     *
     * <p>
     * 另外需要将
     * {@link AuthenticationManagerBuilder#parentAuthenticationManager(AuthenticationManager)}
     * 参数设置为 {@code null}, 令
     * {@link org.springframework.security.authentication.ProviderManager#parent
     * ProviderManager.parent} 字段未 {@code null}, 否则当抛出
     * {@link org.springframework.security.core.AuthenticationException
     * AuthenticationException} 异常后, 会导致无限递归
     * </p>
     *
     * @param security             {@link HttpSecurity} 类型对象, 用于配置
     *                             Spring Security,
     *                             该对象必须通过参数注入, 否则会导致循环依赖
     * @param authService          认证服务类 ({@link AuthService}) 对象
     * @param jwtProvider          对
     *                             {@link alvin.study.core.security.auth.JwtAuthenticationToken
     *                             JwtAuthenticationToken} 进行验证的
     *                             {@link JwtAuthenticationProvider} 类型对象
     * @param namePasswordProvider 对
     *                             {@link alvin.study.core.security.auth.NameAndPasswordAuthenticationToken
     *                             NameAndPasswordAuthenticationToken} 进行验证的
     *                             {@link NameAndPasswordAuthenticationProvider}
     *                             类型对象
     * @return {@link org.springframework.security.authentication.ProviderManager
     * ProviderManager} 类型对象
     */
    @Bean
    AuthenticationManager authManager(
        HttpSecurity security,
        JwtAuthenticationProvider jwtProvider,
        NameAndPasswordAuthenticationProvider namePasswordProvider) throws Exception {
        // 获取 AuthenticationManagerBuilder 对象, 用于构建 ProviderManager 对象
        return security.getSharedObject(AuthenticationManagerBuilder.class)
            // 设置 Provider, 用于处理 JwtAuthenticationToken 对象
            .authenticationProvider(jwtProvider)
            // 设置 Provider 用于处理 NameAndPasswordAuthenticationToken 对象
            .authenticationProvider(namePasswordProvider)
            // 设置 ProviderManager.parent 字段为 null, 防止抛出 AuthenticationException 异常后导致无线递归
            .parentAuthenticationManager(null)
            .build();
    }

    /**
     * 构建 {@link SecurityFilterChain} 对象, 用于配置过滤器拦截请求
     *
     * <p>
     * 可以定义多个产生 {@link SecurityFilterChain} 对象的 Bean (只要方法名不同即可). 可以为不同的 URI
     * 设置不同的认证方式, 即多种认证统一使用. 若多个认证规则相互冲突, 则 {@link Order @Order} 大的会覆盖小的
     * </p>
     *
     * @param security    {@link HttpSecurity} 类型对象, 用于配置 Spring Security,
     *                    该对象必须通过参数注入, 否则会导致循环依赖
     * @param authService 认证服务类 ({@link AuthService}) 对象
     * @return {@link SecurityFilterChain} 对象
     */
    @Bean
    @Order(1)
    SecurityFilterChain filterChain(
        @NotNull HttpSecurity security,
        JwtRequestFilter jwtRequestFilter,
        AuthenticationManager authManager) throws Exception {
        return security
            // 设置相关的 AuthenticationManager 对象
            .authenticationManager(authManager)
            // 禁用 CSRF 重复提交检验
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize ->
                authorize
                    // 设置无需身份验证 URL
                    .requestMatchers("/auth/**").permitAll()
                    // 设置其它请求都需要验证
                    .anyRequest().authenticated()
            )
            // 设置 AuthenticationException 异常处理器
            .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(new AuthenticationErrorHandlerEntryPoint())
            )
            // 设置 Session 创建的策略为无状态 Session
            .sessionManagement(configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 设置 JwtRequestFilter 过滤器, 在 UsernamePasswordAuthenticationFilter 过滤器之前进行拦截
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            // 设置 Basic Auth 方式, 即 http://username:password@localhost:8080 格式的访问方式
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    Advisor preAuthorizeAuthorizationMethodInterceptor(AclPermissionEvaluator aclPermissionEvaluator) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(aclPermissionEvaluator);

        var authorizationManager = new PreAuthorizeAuthorizationManager();
        authorizationManager.setExpressionHandler(expressionHandler);

        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(authorizationManager);
    }
}
