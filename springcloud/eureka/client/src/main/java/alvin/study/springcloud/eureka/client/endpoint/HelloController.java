package alvin.study.springcloud.eureka.client.endpoint;

import alvin.study.springcloud.eureka.client.endpoint.model.HelloDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用 Controller 类型
 */
@Profile("client")
@RestController
@RequestMapping("/api/hello")
public class HelloController {
    // 注入应用程序名称
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 测试用 GET 方法
     *
     * @return {@link HelloDto} 对象
     */
    @GetMapping
    @ResponseBody
    HelloDto get() {
        return new HelloDto(applicationName, "Hello Eureka");
    }
}
