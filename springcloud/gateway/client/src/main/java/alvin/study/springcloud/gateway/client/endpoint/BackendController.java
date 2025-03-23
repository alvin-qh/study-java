package alvin.study.springcloud.gateway.client.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import alvin.study.springcloud.gateway.client.endpoint.model.AppInfoDto;

/**
 * 应用程序后端服务控制器
 *
 * <p>
 * 本控制器类用于测试名为 {@code Path} 的断言, 参考 {@code classpath:application.yml} 中关于断言的定义
 *
 * <pre>
 * spring:
 *   cloud:
 *     gateway:
 *       routes:
 *         - id: path_router
 *           uri: lb://study-springcloud-gateway-backend
 *           predicates:
 *             - Path=/backend/**
 *           filters:
 *             - StripPrefix=1
 * </pre>
 * <p>
 * 上述配置表示:
 *
 * <ul>
 * <li>
 * 使用名为 {@code Path} 的断言, 参数为 {@code /backend/**}, 表示所有符合该路径的请求全部进行转发
 * </li>
 * <li>
 * 目标地址为 {@link lb://study-springcloud-gateway-backend}, 表示通过服务发现查找名为
 * {@code study-springcloud-gateway-backend} 的服务并进行转发, 且转发时使用负载均衡方式
 * </li>
 * <li>
 * 转发的路径和原请求路径一致, 即路径为 {@code /backend/api/info} 的请求转发到目标地址仍为该路径, 但目标地址并不存在
 * {@code backend} 这个路径, 所以需要通过 {@code StripPrefix} 过滤器将第一层路径去掉, 这样转发后的路径就变为
 * {@code /api/info}, 去掉了 {@code /backend/} 这部分
 * </li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/info")
public class BackendController {
    /**
     * 注入服务端监听端口号
     */
    @Value("${server.port}")
    private int localPort;

    /**
     * 注入后端服务应用程序名称
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 获取应用程序信息
     *
     * @return 应用程序信息 DTO 对象
     */
    @GetMapping
    @ResponseBody
    AppInfoDto get() {
        return new AppInfoDto(localPort, applicationName);
    }
}
