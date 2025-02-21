package alvin.study.springboot.springdoc.conf;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Functions;

import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import alvin.study.springboot.springdoc.core.data.DataSource;
import alvin.study.springboot.springdoc.core.data.Storage;
import alvin.study.springboot.springdoc.infra.entity.User;
import alvin.study.springboot.springdoc.util.security.Jwt;
import alvin.study.springboot.springdoc.util.security.PasswordEncoder;

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
@Configuration("conf/bean")
public class BeanConfig {
    /**
     * 获取 {@link Jwt} 对象
     *
     * @param key    加密密钥
     * @param aud    接收方 ID
     * @param jti    JWT ID
     * @param period 过期时间
     * @return {@link Jwt} 对象
     */
    @Bean
    Jwt jwt(@Value("${application.security.jwt.key}") String key,
            @Value("${application.security.jwt.aud}") String aud,
            @Value("${application.security.jwt.jti}") String jti,
            @Value("${application.security.session.period}") String period) {
        var alg = Algorithm.HMAC256(key);
        var prd = Duration.parse(period);
        return new Jwt(alg, aud, jti, prd);
    }

    /**
     * 获取 {@code classpath:application.yml} 中配置的可登录用户列表
     *
     * <p>
     * {@link ConfigurationProperties @ConfigurationProperties} 注解用于给当前方法的返回值注入属性,
     * 本例中为返回的 {@link List} 集合中注入 {@link Map} 项
     * </p>
     *
     * @return 可登录用户列表
     */
    @Lazy
    @Bean("userInfo")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @ConfigurationProperties(prefix = "application.access-users")
    List<Map<String, String>> userInfo() {
        return new ArrayList<>();
    }

    /**
     * 构建数据源对象
     *
     * @param userInfo        {@link #userInfo()} 方法返回的结果, 表示可以访问 API 的用户
     * @param passwordEncoder 密码处理工具对象
     * @return 数据源对象
     */
    @Bean
    DataSource dataSource(
        @Qualifier("userInfo") List<Map<String, String>> userInfo,
        PasswordEncoder passwordEncoder) {
        var ds = new DataSource();

        // 创建存储访问日志实体对象的存储对象
        ds.addStorage(new Storage<>("access-logs"));

        // 创建存储 API 访问用户实体的存储对象
        ds.addStorage(new Storage<>("access-users",
            userInfo.stream()
                .map(u -> new User(
                    u.get("username").toLowerCase(),
                    passwordEncoder.encode(u.get("password"))))
                .collect(Collectors.toMap(User::getUsername, Functions.identity()))));

        return ds;
    }
}
