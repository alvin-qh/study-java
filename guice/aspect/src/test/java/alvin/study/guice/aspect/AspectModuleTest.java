package alvin.study.guice.aspect;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Instant;

import jakarta.inject.Inject;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvin.study.guice.aspect.AspectModule.EventDemo;
import alvin.study.guice.aspect.AspectModule.HandlerDemo;

/**
 * 测试 {@link AspectModule} 模块, 拦截器的使用
 */
class AspectModuleTest {
    // 注入事件触发对象
    @Inject
    private EventDemo eventDemo;

    // 注入事件处理对象
    @Inject
    private HandlerDemo handlerDemo;

    @BeforeEach
    void beforeEach() {
        // 获取注入器对象
        var injector = Guice.createInjector(new AspectModule());
        // 向当前对象注入成员字段
        injector.injectMembers(this);
    }

    /**
     * 测试方法拦截器是否工作
     */
    @Test
    void aspect_shouldInterceptorWorked() {
        // 执行方法, 触发时间
        var result = eventDemo.doSomething("Demo", Instant.parse("2022-10-01T12:00:00Z"));
        // 确认方法执行完毕, 返回值正确
        then(result).isEqualTo("Thing: Demo started at: 2022-10-01T12:00:00Z");

        // 从事件处理对象中获取 log 字符串
        var log = handlerDemo.getLog();
        // 确认事件处理完成
        then(log).isEqualTo("Method: doSomething, arguments: [Demo, 2022-10-01T12:00:00Z]");
    }
}
