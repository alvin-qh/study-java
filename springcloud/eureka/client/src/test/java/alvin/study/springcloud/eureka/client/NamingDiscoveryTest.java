package alvin.study.springcloud.eureka.client;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import alvin.study.springcloud.eureka.client.conf.TestingConfig;
import alvin.study.springcloud.eureka.client.core.model.ResponseWrapper;
import alvin.study.springcloud.eureka.client.endpoint.model.HelloDto;
import alvin.study.springcloud.eureka.client.service.HelloService;

/**
 * 集成测试类的超类
 *
 * <p>
 * 集成测试指的是将数据库操作和业务操作集成在一起进行测试, 可以比较真实的复现业务执行的流程
 * </p>
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(classes = TestingConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class NamingDiscoveryTest {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HelloService helloService;

    @Test
    void get_shouldVisitServiceByServiceName() {
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            try {
                var resp = restTemplate.getForObject("http://eureka-client/api/hello", ResponseWrapper.class);
                then(resp)
                        .isNotNull()
                        .extracting(ResponseWrapper::getRetCode)
                        .isEqualTo(0);

                var dto = objectMapper.convertValue(resp.getPayload(), HelloDto.class);
                then(dto.getApplicationName()).isEqualTo("eureka-client");
                then(dto.getContent()).isEqualTo("Hello Eureka");
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    void get_shouldVisitByHelloService() {
        var resp = helloService.get();

        then(resp.getRetCode()).isZero();
        then(resp.getPayload().getApplicationName()).isEqualTo("eureka-client");
        then(resp.getPayload().getContent()).isEqualTo("Hello Eureka");
    }
}
