package alvin.study.springboot.shiro.conf;

import alvin.study.springboot.shiro.util.security.Jwt;
import alvin.study.springboot.shiro.util.security.PasswordEncoder;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
        return new Jwt(Algorithm.HMAC256(key), aud, jti, Duration.parse(period));
    }

    /**
     * 获取一个固定线程数的线程池执行器
     *
     * @param corePoolSize 最小线程数
     * @param maxPoolSize  最大线程数
     * @param keepAlive    线程空闲后存活的时间
     * @param queueSize    任务队列的最大长度
     * @return 线程执行器对象
     */
    @Bean
    Executor fixedThreadPoolExecutor(
        @Value("${application.thread-pool.core-pool-size}") int corePoolSize,
        @Value("${application.thread-pool.max-pool-size}") int maxPoolSize,
        @Value("${application.thread-pool.keep-alive}") String keepAlive,
        @Value("${application.thread-pool.queue-size}") int queueSize) {
        // 通过正则表达式匹配线程存活时间参数
        var m = Pattern.compile("(\\d+)(h|m|s|ms|ns)").matcher(keepAlive);
        if (!m.matches()) {
            throw new IllegalArgumentException("keepAlive");
        }

        // 获取线程存活时间数值
        var keepAliveTime = Integer.parseInt(m.group(1));

        // 获取线程存活时间单位
        var unit = switch (m.group(2)) {
            case "h" -> TimeUnit.HOURS;
            case "m" -> TimeUnit.MINUTES;
            case "s" -> TimeUnit.SECONDS;
            case "ms" -> TimeUnit.MICROSECONDS;
            case "ns" -> TimeUnit.NANOSECONDS;
            default -> TimeUnit.SECONDS;
        };

        // 创建线程池执行器
        return new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            unit,
            new ArrayBlockingQueue<>(queueSize));
    }
}
