package alvin.study.springboot.mvc.conf;

import org.apache.coyote.http11.Http11NioProtocol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Tomcat Servlet 容器配置
 *
 * <p>
 * Spring 的 {@code application.yml} 配置文件中提供了对 Tomcat 容器的配置支持, 本类中会覆盖配置文件中的配置,
 * 达到和配置文件相同的效果. 参考: {@code application.yml} 中的 {@code spring.tomcat} 配置部分
 * </p>
 */
@Slf4j
@Configuration("conf/tomcat")
public class TomcatConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    // 保持连接的超时时间, 默认值 30000
    @Value("${server.tomcat.keep-alive-timeout:30000}")
    private Integer keepAliveTimeout;

    // 保持连接的最大请求数, 默认值 10000
    @Value("${server.tomcat.max-keep-alive-requests:10000}")
    private Integer maxKeepAliveRequests;

    /**
     * 设置 Tomcat 容器的启动参数
     */
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        if (factory instanceof TomcatServletWebServerFactory tomcatFactory) {
            tomcatFactory.addConnectorCustomizers(connector -> {
                var protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setKeepAliveTimeout(keepAliveTimeout);
                protocol.setMaxKeepAliveRequests(maxKeepAliveRequests);
            });

            log.info("[CONF] Setup tomcat server, keep-alive-timeout={}, max-keep-alive-requests={}",
                keepAliveTimeout, maxKeepAliveRequests);
        } else {
            log.warn("[CONF] Invalid factory type \"{}\"", factory.getClass().getName());
        }
    }
}
