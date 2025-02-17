package alvin.study.springboot.ds.core.data;

import java.util.Optional;

/**
 * 数据源标识线程上下文类型
 *
 * <p>
 * 在本例中, 数据源的切换是依赖于 {@link DataSourceTarget} 的值, 该类会将 {@link DataSourceTarget}
 * 的值存储在线程上下文中, 作为数据源切换的依据
 * </p>
 *
 * <p>
 * 参考 {@link DynamicDataSource#determineCurrentLookupKey()} 方法对数据源标识的使用方式
 * </p>
 */
public final class DataSourceContext {
    // 本地线程存储对象
    private static final ThreadLocal<DataSourceTarget> LOCAL = new ThreadLocal<>();

    private DataSourceContext() {}

    /**
     * 切换数据源标识
     *
     * @param target 数据源标识枚举
     */
    public static void change(DataSourceTarget target) {
        LOCAL.set(target);
    }

    /**
     * 获取当前数据源标识
     *
     * @return 数据源标识枚举
     */
    public static DataSourceTarget current() {
        return Optional.ofNullable(LOCAL.get()).orElse(DataSourceTarget.db1);
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
     * 获取一个 {@link Switcher} 自动切换对象
     *
     * @param newTarget 要切换的数据源标识
     * @return 自动切换对象
     */
    public static Switcher switchTo(DataSourceTarget newTarget) {
        return new Switcher(newTarget);
    }

    /**
     * 数据源标识自动切换类型
     *
     * <p>
     * 该类型用于临时切换数据源, 在作用范围内使用新数据源标识, 离开作用范围后恢复原先使用数据源标识
     * </p>
     */
    public static class Switcher implements AutoCloseable {
        // 记录原先的数据源标识
        private final DataSourceTarget previously;

        /**
         * 构造器
         *
         * @param newly 新数据源标识
         */
        public Switcher(DataSourceTarget newly) {
            // 获取当前数据源标识
            previously = DataSourceContext.current();
            // 切换到新数据源标识
            DataSourceContext.change(newly);
        }

        /**
         * 离开作用域范围, 恢复原先的数据源标识
         */
        @Override
        public void close() {
            DataSourceContext.change(previously);
        }
    }
}
