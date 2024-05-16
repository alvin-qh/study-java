package alvin.study.springboot.jooq.conf;

import alvin.study.springboot.jooq.core.context.Context;
import alvin.study.springboot.jooq.core.context.WebContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 请求上下文对象配置类
 *
 * <p>
 * 主要用于配置 {@link Context} 接口的对象
 * </p>
 *
 * @see WebContext
 */
@Slf4j
@Configuration("conf/context")
public class ContextConfig {
    /**
     * 实例化 {@link Context} 接口的实现类 {@link WebContext}, 将产生的对象交由 Bean 容器管理
     *
     * <p>
     * {@link Context} 对象用于管理请求上下文, 所以该类型对象不能为单例,
     * 而是为每个请求生成不同的对象. 这个目的需要借助
     * {@link RequestScope @RequestScope} 注解实现, 表示当前方法返回的对象作用域范围是一次请求, 请求结束后对象即被销毁
     * </p>
     *
     * @return {@link WebContext} 对象
     */
    @Bean
    @RequestScope
    Context context() {
        var ctx = new WebContext();
        log.info("[CONF] Context \"{}\" was created", ctx.name());
        return ctx;
    }
}
