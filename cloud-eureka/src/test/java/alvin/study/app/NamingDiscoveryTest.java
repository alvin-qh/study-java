package alvin.study.app;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import alvin.study.IntegrationTest;
import alvin.study.app.endpoint.model.HelloDto;
import alvin.study.client.HelloService;
import alvin.study.core.model.ResponseWrapper;

public class NamingDiscoveryTest extends IntegrationTest {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HelloService helloService;

    @Test
    void get_shouldVisitServiceByServiceName() {
        var resp = restTemplate.getForObject("http://study-springcloud-eureka-client/api/hello", ResponseWrapper.class);
        then(resp.getRetCode()).isZero();

        var dto = objectMapper.convertValue(resp.getPayload(), HelloDto.class);
        then(dto.getApplicationName()).isEqualTo("study-springcloud-eureka-client");
        then(dto.getContent()).isEqualTo("Hello Eureka");
    }

    @Test
    void get_shouldVisitByHelloService() {
        var resp = helloService.get();

        then(resp.getRetCode()).isZero();
        then(resp.getPayload().getApplicationName()).isEqualTo("study-springcloud-eureka-client");
        then(resp.getPayload().getContent()).isEqualTo("Hello Eureka");
    }
}
