package alvin.study.springcloud.gateway.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * 记录 Log 的全局过滤器
 *
 * <p>
 * 所谓"全局过滤器"指的是"无需特殊声明, 默认为所有请求都生效"的过滤器
 * </p>
 *
 * <p>
 * 全局过滤器需要实现 {@link GlobalFilter} 接口, 并重写其中的
 * {@link GlobalFilter#filter(ServerWebExchange, GatewayFilterChain)} 方法
 * </p>
 *
 * <p>
 * 通过 {@link org.springframework.core.annotation.Order @Order} 注释或者实现
 * {@link Ordered} 接口可以指定过滤器在过滤器链 (Filter Chain) 中的顺序, 数字越小位置越靠前, 越会优先被执行
 * </p>
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    /**
     * 获取过滤器在过滤器链 (Filter Chain) 中的顺序
     *
     * <p>
     * 返回的值越小, 当前过滤器的优先级越高, {@link Ordered#HIGHEST_PRECEDENCE} 表示最高优先级
     * </p>
     *
     * @return 优先级数字 (越小优先级越高)
     */
    @Override
    public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }

    /**
     * 执行过滤操作
     *
     * <p>
     * 本例中拦截了所有的请求并打印日志
     * </p>
     *
     * <p>
     * Spring Cloud Gateway 基于 WebFlux 框架, 通过 Reactive 方式异步执行,
     * {@link ServerWebExchange} 参数表示一个异步的请求交互对象, 提供对请求和相应的操作
     * </p>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录日志
        recordLog(exchange);

        // 调用下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 记录请求日志
     *
     * @param exchange 请求交互对象
     */
    private void recordLog(@NotNull ServerWebExchange exchange) {
        // 获取请求对象
        var request = exchange.getRequest();

        // 获取客户端 IP 地址
        var resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        var clientIp = resolver.resolve(exchange).getAddress().getHostAddress();
        var port = Optional.ofNullable(request.getRemoteAddress()).map(InetSocketAddress::getPort).orElse(0);

        // 输出日志
        log.info("""
                New request coming:
                Request Path:
                  {}

                Headers:
                  {}

                Remote Host:
                  {}:{}
                """,
            // 输出请求的目标路径
            request.getPath().value(),
            // 输出请求的 HTTP 头信息
            resolveHeaders(request.getHeaders()),
            // 输出客户端地址和端口号
            clientIp, port);
    }

    /**
     * 解析 HTTP 头信息
     *
     * @param headers HTTP 头信息集合
     * @return 解析结果组成的字符串
     */
    private @NotNull String resolveHeaders(@NotNull HttpHeaders headers) {
        var sb = new StringBuilder();
        headers.forEach((name, value) -> {
            if (!sb.isEmpty()) {
                sb.append("\n  ");
            }
            sb.append("  ").append(name).append(": ").append(value);
        });

        return sb.toString();
    }
}
