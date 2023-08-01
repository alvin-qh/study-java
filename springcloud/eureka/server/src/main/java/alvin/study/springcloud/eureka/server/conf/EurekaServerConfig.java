package alvin.study.springcloud.eureka.server.conf;

import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Eureka 服务端配置类
 *
 * <p>
 * {@link EnableEurekaServer @EnableEurekaServer} 注解表示当前应用程序启用 Eureka 服务端
 * </p>
 *
 * <p>
 * 由于本例中 Eureka Server 和 Eureka Client 在一个工程中, 所以需要通过不同的 Profile 来加以区分, 通过
 * {@link Profile @Profile} 注解指定当启动参数为
 * {@code --spring.profiles.active=server-01} 或
 * {@code --spring.profiles.active=server-02} 时加载当前配置
 * </p>
 */
@Profile({
    "server-01",
    "server-02"
})
@Configuration("config.eureka.server")
@EnableEurekaServer
public class EurekaServerConfig {
}
