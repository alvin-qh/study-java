package alvin.study.springboot.graphql.conf;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.algorithms.Algorithm;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.util.security.Jwt;
import alvin.study.springboot.graphql.util.security.PasswordUtil;

/**
 * 配置需被容器管理的 Bean 对象
 *
 * <p>
 * 一些情况下, 无法直接通过 {@link org.springframework.stereotype.Component @Component}
 * 注解类型, 例如该类型构造器的某些参数无法直接从容器中获得, 此时可以通过 Bean 构造方法创建 Bean 对象
 * </p>
 *
 * <p>
 * 首先, 整个类需要通过 {@link Configuration} 注解, 表示是一个配置类, 在 Spring 应用启动时执行; 另外, 这类
 * {@code Bean} 构造方法需要通过 {@link Bean @Bean} 注解标记, 表示方法的返回值会作为产生的 Bean 被容器管理
 * </p>
 */
@Slf4j
@Configuration("conf/bean")
public class BeanConfig {
    /**
     * 生成 {@link PasswordUtil} 对象, 用于对明文密码进行散列操作
     *
     * <p>
     * {@link Bean @Bean} 注解方法的参数也是从 {@code Bean} 容器中注入的, 本例中是通过
     * {@link Value @Value} 注解从 {@code application.yml} 配置文件中获取
     * </p>
     *
     * @param algorithm 加密算法名称, 从 {@code application.yml} 文件中获得
     * @param key       散列信息认证码, 从 {@code application.yml} 文件中获得
     * @return {@link PasswordUtil} 对象
     * @see PasswordUtil#PasswordUtil(String, String)
     */
    @Bean
    PasswordUtil passwordUtil(
            @Value("${application.security.hash.algorithm}") String algorithm,
            @Value("${application.security.hash.key}") String key) {
        var password = new PasswordUtil(algorithm, key);
        log.info("[CONF] Password object created, algorithm=\"{}\", hmacKey=\"{}\"", algorithm, key);
        return password;
    }

    /**
     * 获取 {@link Jwt} 对象, 用于产生用户 Token
     *
     * @param key    加密密钥
     * @param jti    JWT ID
     * @param period 过期时间
     * @return {@link Jwt} 对象
     */
    @Bean
    Jwt jwt(@Value("${application.security.jwt.key}") String key,
            @Value("${application.security.jwt.jti}") String jti,
            @Value("${application.security.session.period}") String period) {
        var alg = Algorithm.HMAC256(key);
        var prd = Duration.parse(period);
        return new Jwt(alg, jti, prd);
    }
}
