package alvin.study.springboot.ds.core.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 用于切换数据源的线程上下文类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSourceContext {
    // 本地线程上下文对象
    private static final ThreadLocal<Deque<String>> LOCAL = new ThreadLocal<>();

    /**
     * 获取当前数据源标识名
     *
     * @return 当前数据源标识名
     */
    public static String current() {
        var stack = LOCAL.get();
        return stack == null || stack.isEmpty() ? null : stack.peek();
    }

    /**
     * 进入指定的数据源标识
     *
     * @param target 数据源标示名
     */
    public static void enter(String target) {
        var stack = LOCAL.get();
        if (stack == null) {
            stack = new LinkedList<>();
            LOCAL.set(stack);
        }
        stack.push(target);
    }

    /**
     * 重置数据源标识
     *
     * @param target 数据源标示名
     */
    public static void reset(String target) {
        var stack = new LinkedList<String>();
        stack.push(target);
        LOCAL.set(stack);
    }

    /**
     * 退出当前的数据源标识
     *
     * @return 退出的数据源标识, {@code null} 标识无数据源标识可以退出
     */
    public static String leave() {
        var stack = LOCAL.get();
        if (stack.size() > 1) {
            return stack.pop();
        }
        return null;
    }

    /**
     * 清除线程上下文存储
     *
     * <p>
     * 在使用线程池的时候, 由于线程都是复用的, 所以每次完成任务后清理一次线程上下文存储是一个好习惯
     * </p>
     */
    public static void clear() {
        LOCAL.remove();
    }

    /**
     * 返回初始的数据源标识
     *
     * @return 初始的数据源标识
     */
    public static String backToBeginning() {
        var stack = LOCAL.get();
        while (stack.size() > 1) {
            stack.pop();
        }
        return current();
    }

    /**
     * 获取一个 {@link Switcher} 自动切换对象
     *
     * @param newTarget 要切换的数据源标识
     * @return 自动切换对象
     */
    public static Switcher switchTo(String newTarget) {
        return new Switcher(newTarget);
    }

    /**
     * 获取一个 {@link Switcher} 自动切换对象
     *
     * @param newTarget 要切换的数据源标识
     * @return 自动切换对象
     */
    public static Switcher switchToDefault() {
        return new Switcher(null);
    }

    /**
     * 数据源标识自动切换类型
     *
     * <p>
     * 该类型用于临时切换数据源, 在作用范围内使用新数据源标识, 离开作用范围后恢复原先使用数据源标识
     * </p>
     */
    public static class Switcher implements AutoCloseable {
        /**
         * 构造器
         *
         * @param newly 新数据源标识
         */
        public Switcher(String newly) {
            // 切换到新数据源标识
            DataSourceContext.enter(newly);
        }

        /**
         * 离开作用域范围, 恢复原先的数据源标识
         */
        @Override
        public void close() {
            DataSourceContext.leave();
        }
    }
}
