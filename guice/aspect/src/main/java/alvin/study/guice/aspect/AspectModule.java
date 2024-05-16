package alvin.study.guice.aspect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.Instant;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import alvin.study.guice.aspect.anno.Event;
import alvin.study.guice.aspect.anno.EventHandler;
import alvin.study.guice.aspect.anno.Handler;
import alvin.study.guice.aspect.interceptor.EventInterceptor;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

/**
 * 演示对 Guice 方法拦截器的使用
 *
 * <p>
 * 方法拦截器是一个实现了 {@link org.aopalliance.intercept.MethodInterceptor
 * MethodInterceptor} 接口的类型, 一旦拦截器生效, 则执行被拦截方法时, 会自行跳转到
 * {@link org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
 * MethodInterceptor.invoke(MethodInvocation)} 方法执行, 从而实现了切面化编程的目的. 参考:
 * {@link EventInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
 * EventInterceptor#invoke(MethodInvocation)} 方法
 * </p>
 */
public class AspectModule extends AbstractModule {
    /**
     * 判断一个 Class 表示的类是否具备指定接口类型
     *
     * @param clazz         要判断类型的 Class 对象
     * @param interfaceType 指定接口的 Class 对象
     * @return 所给类型是否实现了指定接口
     */
    private static boolean withInterface(Class<?> clazz, Class<?> interfaceType) {
        for (var interfaceClass : clazz.getInterfaces()) {
            if (interfaceClass == interfaceType) {
                return true;
            }
        }
        return false;
    }

    /**
     * 配置模块
     *
     * <p>
     * {@link Multibinder} 对象中可以添加多个绑定关系, 并在容器中产生一个 {@link java.util.Set} 集合, 注入该集合,
     * 集合中的各个元素即为通过设置的绑定关系产生的对象实例
     * </p>
     *
     * <p>
     * {@link Multibinder#newSetBinder(com.google.inject.Binder, Class, java.lang.annotation.Annotation)
     * Multibinder.newSetBinder(Binder, Class, Annotation)} 用于产生一个
     * {@link Multibinder} 实例对象
     * </p>
     *
     * <p>
     * {@link Multibinder#addBinding()} 用于添加一个绑定关系, 返回一个
     * {@link com.google.inject.binder.LinkedBindingBuilder LinkedBindingBuilder} 对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.LinkedBindingBuilder#toInstance(Object)
     * LinkedBindingBuilder.toInstance(Object)} 创建一个绑定关系, 类似的方法还包括:
     * {@link com.google.inject.binder.LinkedBindingBuilder#to(Class)
     * LinkedBindingBuilder.to(Class)},
     * {@link com.google.inject.binder.LinkedBindingBuilder#toConstructor(java.lang.reflect.Constructor)
     * LinkedBindingBuilder.toConstructor(Constructor)},
     * {@link com.google.inject.binder.LinkedBindingBuilder#toProvider(com.google.inject.Provider)}
     * LinkedBindingBuilder.toProvider(Provider)} 等方法
     * </p>
     *
     * <p>
     * {@link AbstractModule#requestInjection(Object)} 表示指定的对象在使用前需要对其进行一次注入操作, 参考:
     * {@link EventInterceptor#setHandlers(java.util.Set)
     * EventInterceptor.setHandlers(Set)} 方法, 通过该方法注入了 {@link EventHandler} 对象集合
     * </p>
     *
     * <p>
     * {@link AbstractModule#bindInterceptor(com.google.inject.matcher.Matcher, com.google.inject.matcher.Matcher, org.aopalliance.intercept.MethodInterceptor...)
     * AbstractModule.bindInterceptor(Matcher&lt;? super Class&lt;? &gt;&gt;,
     * Matcher&lt;? super Method&gt;, MethodInterceptor...)} 方法用于设置一组拦截器,
     * 第一个参数为要拦截的类, 第二个参数为要拦截的方法类型, 后续参数为 1 或多个拦截器实例
     * </p>
     */
    @Override
    @SneakyThrows
    protected void configure() {
        // 产生一个多实例绑定集合, 对 EventHandler 类型的多个实例进行绑定, 并设置标识名称
        var multibinder = Multibinder.newSetBinder(binder(), EventHandler.class, Names.named("Handlers"));

        // 遍历 alvin.study 下的所有 EventHandler 对象, 并将其加入到 Multibinder 集合中
        ClassPath.from(AspectModule.class.getClassLoader())
            // 获取所有 ClassInfo 对象
            .getAllClasses()
            .stream()
            // 过滤包名以 alvin.study 起始的 ClassInfo 对象
            .filter(ci -> ci.getPackageName().startsWith("alvin.study"))
            // 将 ClassInfo 对象转为 Class 对象
            .map(ClassInfo::load)
            // 过滤实现了 EventHandler 接口的 Class 对象
            .filter(c -> withInterface(c, EventHandler.class))
            // 将得到的 Class 对象加入到 Multibinder 绑定集合中
            .forEach(c -> multibinder.addBinding().to((Class<EventHandler>) c));

        // 实例化拦截器对象并对其进行注入操作
        var interceptor = new EventInterceptor();
        requestInjection(interceptor);

        // 将拦截器进行绑定, 任意类型的 具备 @Event 注解的方法调用时均触发拦截器
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Event.class), interceptor);
    }

    /**
     * 测试拦截器的类型
     */
    @Singleton
    static class EventDemo {
        /**
         * 该方法的执行会被 {@link EventInterceptor} 拦截器拦截, 并将事件传递到 {@link Handler @Handler} 注解的
         * {@code name} 属性为 {@code "LogHandler"} 的事件处理方法中
         */
        @Event(handler = "LogHandler")
        public String doSomething(String thing, Instant startAt) {
            return String.format("Thing: %s started at: %s", thing, startAt);
        }
    }

    /**
     * 测试事件处理的类型
     */
    @Singleton
    static class HandlerDemo implements EventHandler {
        // 记录日志的 Writer 对象
        private final StringWriter writer = new StringWriter();

        /**
         * 处理来自 {@link Event @Event} 注解的 {@code handler} 属性值为 {@code "LogHandler"} 的事件
         */
        @Handler(name = "LogHandler")
        @Override
        public void handler(Object obj, Method method, Object[] arguments) {
            try (var out = new PrintWriter(writer)) {
                out.printf("Method: %s, arguments: %s", method.getName(), formatArgument(arguments));
            }
        }

        /**
         * 将参数列表转为字符串
         *
         * @param arguments 参数列表
         * @return 字符串
         */
        private String formatArgument(Object[] arguments) {
            var builder = new StringBuilder("[");
            for (var arg : arguments) {
                if (arg != arguments[0]) {
                    builder.append(", ");
                }
                builder.append(arg.toString());
            }
            builder.append("]");
            return builder.toString();
        }

        /**
         * 获取日志内容
         *
         * @return 日志内容
         */
        public String getLog() {
            return writer.getBuffer().toString();
        }
    }
}
