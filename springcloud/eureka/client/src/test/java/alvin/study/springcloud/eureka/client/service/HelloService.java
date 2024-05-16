package alvin.study.springcloud.eureka.client.service;

import alvin.study.springcloud.eureka.client.core.model.ResponseWrapper;
import alvin.study.springcloud.eureka.client.endpoint.model.HelloDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 通过 OpenFeign 框架通过 Eureka 访问服务
 *
 * <p>
 * {@link FeignClient @FeignClient} 注解表示当前接口为 OpenFeign 的客户端, 其中 {@code name} (或
 * {@code value}) 属性表示要访问的服务名, 这里要和在 Eureka 中注册的服务名一致
 * </p>
 *
 * <p>
 * 服务名通过从 {@code classpath:application.yml} 配置中获取
 * </p>
 */
@FeignClient("${application.server.name}")
public interface HelloService {
    /**
     * 访问 {@code /<server_name>/api/hello} 获取响应结果
     *
     * <p>
     * OpenFeign 采用和 Spring Controller 相同的 {@link RequestMapping @RequestMapping}
     * 注解来标记请求
     * </p>
     *
     * @return {@link ResponseWrapper} 包装的响应结果
     */
    @GetMapping("/api/hello")
    ResponseWrapper<HelloDto> get();
}
