package alvin.study.springboot.security.conf;

import alvin.study.springboot.security.core.security.auth.NameAndPasswordAuthenticationToken;
import alvin.study.springboot.security.core.security.filter.AuthenticationErrorHandlerEntryPoint;
import alvin.study.springboot.security.core.security.filter.JwtRequestFilter;
import alvin.study.springboot.security.core.security.handler.AclPermissionEvaluator;
import jakarta.annotation.Nonnull;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

/**
 * 配置 Spring Security
 *
 * <p>
 * {@link EnableWebSecurity @EnableWebSecurity} 注解用于开启 Spring Security
 * </P>
 *
 * <p>
 * 已弃用
 * <s>
 * {@code @EnableGlobalMethodSecurity} 注解表示使用方法级别的权限控制, 添加此注解后, 即可以使用
 * {@link org.springframework.security.access.prepost.PreAuthorize @PreAuthorize} 等注解对方法进行权限控制
 * </s>
 * </p>
 *
 * <p>
 * {@link EnableMethodSecurity @EnableMethodSecurity} 注解表示使用方法级别的权限控制, 添加此注解后, 即可以使用
 * {@link org.springframework.security.access.prepost.PreAuthorize @PreAuthorize} 等注解对方法进行权限控制
 * </p>
 *
 * <p>
 * 已弃用
 * <s>
 * 继承 {@code WebSecurityConfigurerAdapter} 类型, 并覆盖其 {@code configure} 方法, 通过该方法传入的 {@link HttpSecurity}
 * 类型对象完成配置
 * </s>
 * </p>
 *
 * <p>
 * 已弃用
 * <s>
 * {@link AuthenticationManager} 对象用于对 {@link org.springframework.security.core.Authentication Authentication}
 * 对象进行验证, {@link org.springframework.security.core.Authentication Authentication} 类型对象用于存储用户登录信息, 参考
 * {@code JwtAuthenticationToken} 和
 * {@link NameAndPasswordAuthenticationToken NameAndPasswordAuthenticationToken} 类型
 * </s>
 * </p>
 *
 * <p>
 * 6.0 以上版本无需关注 {@link AuthenticationManager} 类型对象, 只需要在过滤器中为
 * {@link org.springframework.security.core.context.SecurityContext SecurityContext} 正确设置
 * {@link org.springframework.security.core.Authentication Authentication} 对象并保证
 * {@link org.springframework.security.core.Authentication#isAuthenticated() Authentication.isAuthenticated()}
 * 方法返回 {@code true} 即可
 * </p>
 *
 * <p>
 * 已弃用
 * <s>
 * {@code JwtAuthenticationProvider} 用于对 {@code JwtAuthenticationToken} 对象进行验证;
 * {@code NameAndPasswordAuthenticationProvider} 用于对
 * {@link NameAndPasswordAuthenticationToken NameAndPasswordAuthenticationToken}
 * 对象进行验证
 * </s>
 * </p>
 *
 * <p>
 * {@link SecurityFilterChain} 对象配置请求的过滤器集合, 本例中通过 {@link JwtRequestFilter} 拦截请求中的 JWT token;
 * 另外 {@link AuthenticationErrorHandlerEntryPoint} 对象用于对
 * {@link org.springframework.security.core.AuthenticationException AuthenticationException} 异常进行拦截
 * </p>
 */
@Configuration("conf/security")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * 配置 {@link org.springframework.security.authentication.ProviderManager ProviderManager} 对象
     *
     * <p>
     * 需要设置 {@link org.springframework.security.core.Authentication Authentication} 验证信息对象的验证器 Provider
     * </p>
     *
     * <p>
     * 另外需要将 {@link AuthenticationManagerBuilder#parentAuthenticationManager(AuthenticationManager)}
     * 参数设置为 {@code null}, 令 {@code ProviderManager.parent} 字段未 {@code null}, 否则当抛出
     * {@link org.springframework.security.core.AuthenticationException AuthenticationException} 异常后, 会导致无限递归
     * </p>
     *
     * @param security             {@link HttpSecurity} 类型对象, 用于配置
     *                             Spring Security,
     *                             该对象必须通过参数注入, 否则会导致循环依赖
     * @param jwtProvider          对 {@code JwtAuthenticationToken} 进行验证的 {@code JwtAuthenticationProvider} 类型对象
     * @param namePasswordProvider 对 {@link NameAndPasswordAuthenticationToken
     *                             NameAndPasswordAuthenticationToken} 进行验证的
     *                             {@code NameAndPasswordAuthenticationProvider} 类型对象
     * @return {@link org.springframework.security.authentication.ProviderManager ProviderManager} 类型对象
     * @deprecated 已弃用, Spring Security 6+ 后版本无需定义此方法
     */
    // @Bean
    @Deprecated(forRemoval = true, since = "3.0")
    AuthenticationManager authManager(
            HttpSecurity security
    /* , JwtAuthenticationProvider jwtProvider */
    /* , NameAndPasswordAuthenticationProvider namePasswordProvider */) throws Exception {
        // 获取 AuthenticationManagerBuilder 对象, 用于构建 ProviderManager 对象
        return security.getSharedObject(AuthenticationManagerBuilder.class)
                // 设置 Provider, 用于处理 JwtAuthenticationToken 对象
                // .authenticationProvider(jwtProvider)
                // 设置 Provider 用于处理 NameAndPasswordAuthenticationToken 对象
                // .authenticationProvider(namePasswordProvider)
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
     * @param security         {@link HttpSecurity} 类型对象, 用于配置 Spring Security,
     *                         该对象必须通过参数注入, 否则会导致循环依赖
     * @param jwtRequestFilter 过滤器对象
     * @return {@link SecurityFilterChain} 对象
     */
    @Bean
    @Order(1)
    SecurityFilterChain filterChain(
            @Nonnull HttpSecurity security,
            @Nonnull JwtRequestFilter jwtRequestFilter) throws Exception {
        return security
                // 禁用 CSRF 重复提交检验
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        // 设置其它请求都需要验证
                        .anyRequest().authenticated())
                // 设置 AuthenticationException 异常处理器
                .exceptionHandling(
                    configurer -> configurer.authenticationEntryPoint(new AuthenticationErrorHandlerEntryPoint()))
                // 设置 Session 创建的策略为无状态 Session
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 设置 JwtRequestFilter 过滤器, 在 UsernamePasswordAuthenticationFilter 过滤器之前进行拦截
                .addFilterAfter(jwtRequestFilter, HeaderWriterFilter.class)
                // 设置 Basic Auth 方式, 即 http://username:password@localhost:8080 格式的访问方式
                // .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * 注册方法级权限控制表达式, 在 Spring Security 6+ 之后版本, 需要通过此方法注册
     * {@link org.springframework.security.access.PermissionEvaluator PermissionEvaluator} 对象
     *
     * @return 包含 {@link org.springframework.security.access.PermissionEvaluator PermissionEvaluator} 对象的
     *         {@link MethodSecurityExpressionHandler MethodSecurityExpressionHandler} 对象
     */
    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator());

        return expressionHandler;
    }
}
