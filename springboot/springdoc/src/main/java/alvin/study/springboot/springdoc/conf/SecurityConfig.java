package alvin.study.springboot.springdoc.conf;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.springdoc.core.security.filter.CustomErrorHandlerEntryPoint;
import alvin.study.springboot.springdoc.core.security.filter.CustomRequestFilter;
import alvin.study.springboot.springdoc.infra.repository.UserRepository;
import alvin.study.springboot.springdoc.util.security.Jwt;
import alvin.study.springboot.springdoc.util.security.PasswordEncoder;

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
     * @see PasswordEncoder#matches(CharSequence, String)
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
    CustomRequestFilter customRequestFilter(
        UserDetailsService userDetailsService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        Jwt jwt
    ) {
        return new CustomRequestFilter(
            // 设置需要 basic auth 登录认证拦截的路径
            new String[]{ "/**/api-docs/**" },
            // 设置需要 JWT 登录认证拦截的路径
            new String[]{ "/api/**" },
            // 设置不需要认证拦截的路径
            new String[]{ "/swagger-ui/**" },
            userDetailsService,
            passwordEncoder,
            jwt,
            userRepository
        );
    }

    /**
     * 设置过滤器链配置
     *
     * @param security {@link HttpSecurity} 对象
     * @return {@link SecurityFilterChain} 对象, 表示过滤器链配置
     */
    @Bean
    SecurityFilterChain filterChain(
        HttpSecurity security,
        CustomRequestFilter customRequestFilter,
        CustomErrorHandlerEntryPoint errorHandlerEntryPoint) throws Exception {
        return security
            // 禁用 CSRF 重复提交检验
            .csrf(AbstractHttpConfigurer::disable)
            // 设置认证相关的访问 URI
            .authorizeHttpRequests(registry ->
                registry
                    // 无需凭证的 URI
                    .requestMatchers("/auth/**", "/swagger-ui/**").permitAll()
                    // 其它请求均需要凭证认证
                    .anyRequest().authenticated()
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
            .addFilterBefore(customRequestFilter, HeaderWriterFilter.class)
            .build();
    }
}
