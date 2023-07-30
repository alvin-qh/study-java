package alvin.study.springcloud.nacos.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 映射 Nacos 配置的类型
 *
 * <p>
 * 该类型会根据 Nacos 配置中心的内容, 通过字段上的 {@link Value @Value} 注解获取指定的配置项目, 例如:
 * {@link Common#searchUrl} 字段, 配置信息在 {@code classpath:application.yml} 中设置
 * </p>
 *
 * <p>
 * {@link RefreshScope @RefreshScope} 注解表示监听 Nacos 服务端, 如果配置发生变化, 则刷新配置内容
 * </p>
 *
 * <p>
 * 配置是通过 {@link Value @Value} 注解根据 Nacos 服务方配置的配置项 {@code Key} 值来注入的
 * </p>
 */
@Getter
@Component
@RefreshScope
@RequiredArgsConstructor
public class ApplicationConfig {
    /**
     * 对应 Nacos 配置 {@code common} 部分的对象
     */
    private final Common common;

    /**
     * 对应 Nacos 配置 {@code common} 字段内容的类型
     */
    @Getter
    @Component
    @RefreshScope
    public static class Common {
        /**
         * 对应 Nacos 配置 {@code common.search_url} 部分字符串, 缺省为 {@code ""} 空字符串
         */
        @Value("${common.search_url:}")
        private String searchUrl;
    }
}
