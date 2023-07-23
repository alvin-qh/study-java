package alvin.study.conf;

import alvin.study.core.security.auth.CustomAuthenticationProvider;
import alvin.study.core.security.filter.CustomErrorHandlerEntryPoint;
import alvin.study.core.security.filter.CustomRequestFilter;
import alvin.study.util.security.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * 设置 Spring Security 配置
 */
@Slf4j
@Configuration("core/security")
@EnableWebSecurity
public class SecurityConfig {
    /**
     * 生成 {@link PasswordEncoder} 对象, 用于对明文密码进行散列操作
     *
     * <p>
     * {@link Bean @Bean} 注解方法的参数也是从 {@code Bean} 容器中注入的, 本例中是通过
     * {@link Value @Value} 注解从 {@code application.yml} 配置文件中获取
     * </p>
     *
     * @param algorithm 加密算法名称, 从 {@code application.yml} 文件中获得
     * @param key       散列信息认证码, 从 {@code application.yml} 文件中获得
     * @return {@link PasswordEncoder} 对象
     * @see PasswordEncoder#PasswordUtil(String, String)
     */
    @Bean
    PasswordEncoder passwordEncoder(
        @Value("${application.security.hash.algorithm}") String algorithm,
        @Value("${application.security.hash.key}") String key) {
        var encoder = new PasswordEncoder(algorithm, key);
        log.info("[CONF] PasswordEncoder object created, algorithm=\"{}\", hmacKey=\"{}\"", algorithm, key);
        return encoder;
    }

    /**
     * 设置用户管理方式
     *
     * @param encoder  {@link PasswordEncoder} 类型对象, 密码处理工具类对象
     * @param username 用户名, 从 {@code classpath:application.yml} 文件中载入
     * @param password 密码, 从 {@code classpath:application.yml} 文件中载入
     * @return {@link InMemoryUserDetailsManager} 对象, 在内存中管理用户
     */
    @Bean
    InMemoryUserDetailsManager userDetailsManager(
        PasswordEncoder encoder,
        @Value("${springdoc.authorization.username}") String username,
        @Value("${springdoc.authorization.password}") String password) {
        return new InMemoryUserDetailsManager(User.withUsername(username)
            .password(encoder.encode(password))
            .authorities(List.of())
            .build());
    }

    /**
     * 产生 {@link CustomRequestFilter} 对象
     *
     * <p>
     * 设置 {@code /api-docs} 路径需要 basic auth 认证拦截; {@code /api/**} 需要 JWT 登录认证拦截;
     * {@link /swagger-ui/**} 不需要拦截
     * </p>
     *
     * @return {@link CustomRequestFilter} 对象
     */
    @Bean
    CustomRequestFilter customRequestFilter() {
        return new CustomRequestFilter(
            // 设置需要 basic auth 登录认证拦截的路径
            new String[]{ "/**/api-docs/**" },
            // 设置需要 JWT 登录认证拦截的路径
            new String[]{ "/api/**" },
            // 设置不需要认证拦截的路径
            new String[]{ "/swagger-ui/**" });
    }

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
     * @param security     {@link HttpSecurity} 类型对象, 用于配置
     *                     Spring Security, 该对象必须通过参数注入, 否则会导致循环依赖
     * @param authProvider 对
     *                     {@link alvin.study.core.security.auth.CustomAuthenticationToken
     *                     CustomAuthenticationToken} 进行验证的
     *                     {@link CustomAuthenticationProvider} 类型对象
     * @return {@link org.springframework.security.authentication.ProviderManager
     * ProviderManager} 类型对象
     */
    @Bean
    AuthenticationManager authManager(
        HttpSecurity security, CustomAuthenticationProvider authProvider) throws Exception {
        // 获取 AuthenticationManagerBuilder 对象, 用于构建 ProviderManager 对象
        return security.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authProvider)
            // 设置 ProviderManager.parent 字段为 null, 防止抛出 AuthenticationException 异常后导致无线递归
            .parentAuthenticationManager(null)
            .build();
    }

    /**
     * 设置过滤器链配置
     *
     * @param security    {@link HttpSecurity} 对象
     * @param authManager {@link #authManager(HttpSecurity, CustomAuthenticationProvider)}
     *                    方法产生的对象
     * @return {@link SecurityFilterChain} 对象, 表示过滤器链配置
     */
    @Bean
    SecurityFilterChain filterChain(
        HttpSecurity security,
        CustomRequestFilter customRequestFilter,
        AuthenticationManager authManager,
        CustomErrorHandlerEntryPoint errorHandlerEntryPoint) throws Exception {
        return security.authenticationManager(authManager)
            // 禁用 CSRF 重复提交检验
            .csrf(AbstractHttpConfigurer::disable)
            // 设置认证相关的访问 URI
            .authorizeHttpRequests(registry ->
                registry
                    // 无需凭证的 URI
                    // .requestMatchers("/auth/**", "/swagger-ui/**").permitAll()
                    // 其它请求均需要凭证认证
                    .anyRequest().permitAll()
            )
            .sessionManagement(configurer ->
                // 设置 session 管理方式, 以 Cookie 管理无状态 session
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(configurer ->
                // 设置异常处理器
                configurer.authenticationEntryPoint(errorHandlerEntryPoint)
            )
            // 设置拦截器的位置
            .addFilterBefore(customRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
