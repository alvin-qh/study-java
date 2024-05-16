package alvin.study.springcloud.gateway.client.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * 测试环境配置
 *
 * <p>
 * {@link Profile @Profile} 注解表示该配置类型仅在测试中生效
 * </p>
 *
 * <p>
 * {@link TestConfiguration @TestConfiguration} 注解和
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * 功能类似, 但专用于测试
 * </p>
 */
@TestConfiguration
public class TestingConfig {
    /**
     * 创建 Rest 客户端对象
     *
     * @return {@link RestTemplate} 对象
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
