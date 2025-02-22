package alvin.study.springboot.jpa.common;

import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.core.context.CustomRequestAttributes;

/**
 * {@link Context} 对象切换器类型
 *
 * <p>
 * 测试时有时候需要临时切换请求上下文对象, 该类型的作用是接收一个新的 {@link Context} 对象取代原有的对象,
 * 并在使用完毕后切换回原来的上下文对象
 * </p>
 *
 * <p>
 * 该类型继承了 {@link AutoCloseable} 接口, 意味着将 {@link Context} 对象作为一个资源看待,
 * 当操作完毕后可以自动的切换回原来的上下文, 即可以应用类似如下代码完成 {@link Context} 对象切换:
 *
 * <pre>
 * try (var switcher = switchContext(newContext)) {
 *     // 切换后的操作
 * }
 * </pre>
 * </p>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContextSwitcher implements AutoCloseable {
    // 保持原 Context 对象
    private final Context precedingContext;
    // 在操作结束后, 切换回原 Context 对象后的回调函数
    private final Consumer<Context> consumer;

    /**
     * 进行 {@link Context} 对象切换
     *
     * @param context  新的 {@link Context} 对象
     * @param consumer 恢复原 {@link Context} 对象后执行的回调函数
     * @return 当前类型对象, 用于恢复原 {@link Context} 对象
     */
    public static ContextSwitcher doSwitch(Context context, Consumer<Context> consumer) {
        // 获取现有的 Context 对象并保存
        var currentContext = Context.current();
        // 切换新的 Context 对象
        CustomRequestAttributes.register(context);
        return new ContextSwitcher(currentContext, consumer);
    }

    /**
     * 关闭资源对象, 意味着操作结束, 此时切换回原来的 {@link Context} 对象
     *
     * <pre>
     * try (var switcher = switchContext(newContext)) {
     *     // 切换后的操作
     * }
     * </pre>
     */
    @Override
    public void close() {
        if (precedingContext == null) {
            // 之前没有 Context 对象, 则清除现在的 Context 对象
            CustomRequestAttributes.unregister();
        } else {
            // 注册之前的 Context 对象以取代现有的 Context 对象
            CustomRequestAttributes.register(precedingContext);
        }
        consumer.accept(precedingContext);
    }
}
