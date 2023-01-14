package alvin.study.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置 Spring Cloud Gateway
 *
 * <p>
 * 通过 {@link Profile @Profile} 注解表明, 当前配置类只有当
 * {@code spring.profiles.active=gateway} 时生效, 即当前工程以 Gateway 模式运行时生效
 * </p>
 *
 * <p>
 * 另外, 本工程中同时包含了 Spring Cloud Gateway 依赖和 Spring Web MVC 依赖, 这两个框架是相互冲突的, 前者使用了
 * reactive 模式, 后者使用了多线程模式, 此时需要在 {@code classpath:application.yml} 设置
 * {@code spring.main.web-application-type=reactive}
 * </p>
 */
@Profile("gateway")
@Configuration("conf/gateway")
public class GatewayConfig {}
