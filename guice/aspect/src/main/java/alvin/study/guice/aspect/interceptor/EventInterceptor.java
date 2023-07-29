package alvin.study.guice.aspect.interceptor;

import alvin.study.guice.aspect.anno.Event;
import alvin.study.guice.aspect.anno.EventHandler;
import alvin.study.guice.aspect.anno.Handler;
import com.google.inject.name.Named;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实现一个拦截器类
 *
 * <p>
 * 拦截器 {@link MethodInterceptor} 是作为 Guice 框架进行 AOP 编程的主要接口, 其
 * {@link MethodInterceptor#invoke(MethodInvocation)} 方法的作用就是拦截目标方法,
 * 在目标方法执行前后进行切面化处理
 * </p>
 */
public class EventInterceptor implements MethodInterceptor {
    /**
     * 保存所有 {@link EventHandler} 对象的 Map
     *
     * <p>
     * Key 为 {@link EventHandler#handler(Object, Method, Object[])} 方法上
     * {@link Handler @Handler} 注解的 {@code name} 属性. 参考:
     * {@code HandlerDemo.handler(Object, Method, Object[])} 方法
     * </p>
     */
    private Map<String, EventHandler> handlers;

    /**
     * 从 {@link EventHandler} 接口对象的
     * {@link EventHandler#handler(Object, Method, Object[])
     * EventHandler.handler(...)} 方法上获取
     * {@link Handler @Handler} 注解的 {@code name} 属性值
     *
     * <p>
     * {@link Handler @Handler} 注解的 {@code name} 属性值表示该事件处理方法的一个名字, 用来和
     * {@link Event @Event} 注解的 {@code handler} 属性值对应
     * </p>
     *
     * @param handler {@link EventHandler} 接口对象
     * @return {@link EventHandler#handler(Object, Method, Object[])
     * EventHandler.handler(...)} 方法上 {@link Handler @Handler} 注解的
     * {@code name} 属性值
     */
    @SneakyThrows
    private static String getHandlerName(EventHandler handler) {
        var method = handler.getClass().getMethod("handler", Object.class, Method.class, Object[].class);
        var annotation = method.getAnnotation(Handler.class);
        return annotation.name();
    }

    /**
     * 执行方法拦截
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            // 获取被拦截的方法
            var method = invocation.getMethod();

            // 获取被拦截方法上的 @Event 注解
            var annotations = method.getAnnotationsByType(Event.class);
            for (var annotation : annotations) {
                // 通过注解的 handler 属性获取对应的 @Handler 注解名称, 并在 Map 中找到对应的 EventHandler 对象
                var handler = handlers.get(annotation.handler());
                if (handler != null) {
                    // 调用方法处理事件
                    handler.handler(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
                }
            }
        } catch (Throwable ignore) {
        }

        // 执行被拦截的方法, 返回执行结果
        return invocation.proceed();
    }

    /**
     * 注入所有 {@link EventHandler} 接口类型的对象
     *
     * @param handlers 所有通过 {@code @Named("Handlers")} 进行标识的 {@link EventHandler}
     *                 接口类型的对象
     */
    @Inject
    public void setHandlers(@Named("Handlers") Set<EventHandler> handlers) {
        this.handlers = handlers.stream().collect(
            Collectors.toMap(
                EventInterceptor::getHandlerName,
                Function.identity()
            )
        );
    }
}
