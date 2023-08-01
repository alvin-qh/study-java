package alvin.study.springcloud.eureka.client.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * Eureka 客户端配置类
 *
 * <p>
 * {@link EnableEurekaClient @EnableEurekaClient} 注解表示当前应用程序启用 Eureka 客户端
 * </p>
 *
 * <p>
 * 由于本例中 Eureka Server 和 Eureka Client 在一个工程中, 所以需要通过不同的 Profile 来加以区分, 通过
 * {@link Profile @Profile} 注解指定当启动参数为 {@code --spring.profiles.active=client}
 * 时加载当前配置
 * </p>
 */
@TestConfiguration
public class TestingConfig {
    /**
     * 获取支持 Eureka 服务发现的 Restful 请求对象
     *
     * <p>
     * {@link LoadBalanced @LoadBalanced} 注解表示该 {@link RestTemplate} 对象支持服务发现和负载均衡
     * </p>
     *
     * @return {@link RestTemplate} 对象, 支持服务发现和负载均衡
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
