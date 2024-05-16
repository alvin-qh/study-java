package alvin.study.springcloud.nacos.conf;

import alvin.study.springcloud.nacos.util.NacosUtil;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Nacos 相关配置类型
 *
 * <p>
 * {@link EnableDiscoveryClient @EnableDiscoveryClient} 注解表示启用 Nacos 服务发现客户端功能,
 * 但在当前版本的 Nacos Discovery 依赖中, 忽略此注解并不影响功能
 * </p>
 */
@TestConfiguration
@EnableDiscoveryClient
public class TestingConfig {
    /**
     * 产生 {@link NacosUtil} 类型 Bean 对象
     *
     * @param username        Nacos 登录用户名
     * @param password        Nacos 登陆密码
     * @param serverAddr      Nacos 服务地址
     * @param namingNamespace Nacos 相关命名空间
     * @return {@link NacosUtil} 类型对象
     */
    @Bean
    NacosUtil nacosUtil(
        @Value("${spring.cloud.nacos.username}") String username,
        @Value("${spring.cloud.nacos.password}") String password,
        @Value("${spring.cloud.nacos.server-addr}") String serverAddr,
        @Value("${spring.cloud.nacos.config.namespace:}") String configNamespace,
        @Value("${spring.cloud.nacos.discovery.namespace:}") String namingNamespace) throws NacosException {
        // 实例化对象
        return new NacosUtil(username, password, serverAddr, configNamespace, namingNamespace);
    }

    /**
     * 产生一个 {@link RestTemplate} 类型对象, 用于进行 HTTP 请求
     *
     * <p>
     * 通过 {@link TestingConfig} 类型的
     * {@link EnableDiscoveryClient @EnableDiscoveryClient} 注解以及方法上的
     * {@link LoadBalanced @LoadBalanced} 注解, 会通过 Nacos Discovery 产生的 HTTP 客户端对象,
     * 该对象可以忽略服务地址, 直接使用服务名访问服务, 并自动为服务访问启用负载均衡
     * </p>
     *
     * <p>
     * 注意, 使用 {@link LoadBalanced @LoadBalanced} 注解时, 需要在 Maven 或 Gradle 中田家庵
     * {@code spring-cloud-starter-loadbalancer} 依赖
     * </p>
     *
     * <p>
     * 参考
     * {@code NacosNamingTest#discover_shouldVisitServiceByName()} 方法中对 {@link RestTemplate} 对象的使用方法
     * </p>
     *
     * @return {@link RestTemplate} 对象
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
