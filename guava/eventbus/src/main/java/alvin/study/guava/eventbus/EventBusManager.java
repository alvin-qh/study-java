package alvin.study.guava.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理 {@link EventBus} 对象的管理器类型
 */
public final class EventBusManager implements Closeable {
    // 存储默认 EventBus 对象的 Key 名称
    private static final String BUS_NAME_COMMON = UUID.randomUUID().toString();

    // 当前类型的单例对象
    private static final EventBusManager INSTANCE = new EventBusManager();
    // 存储 EventBus 对象的 Map 对象
    private final Map<String, EventBus> eventBusMap = new ConcurrentHashMap<>();
    // 异步线程执行器
    private ExecutorService executorService = null;

    /**
     * 构造器, 创建管理器对象
     *
     * <p>
     * 该方法会在管理中创建默认的 {@link EventBus} 对象
     * </p>
     */
    private EventBusManager() {
        eventBusMap.put(BUS_NAME_COMMON, new EventBus());
    }

    /**
     * 获取单例的 {@link EventBusManager} 类型对象
     *
     * @return 单例 {@link EventBusManager} 类型对象
     */
    public static EventBusManager getInstance() { return INSTANCE; }

    /**
     * 通过一个名称标识注册一个 {@link EventBus} 对象
     *
     * @param name 标识名称字符串
     * @return 被注册的 {@link EventBus} 对象
     */
    public EventBus registerEventBus(String name) {
        return registerEventBus(name, null);
    }

    /**
     * 通过一个名称标识注册一个异步 {@link EventBus} 对象
     *
     * @param name 标识名称字符串
     * @return 被注册的 {@link EventBus} 对象
     */
    public EventBus registerAsyncEventBus(String name) {
        return registerAsyncEventBus(name, null);
    }

    /**
     * 通过一个名称标识注册一个 {@link EventBus} 对象, 并为事件处理设定异常处理对象
     *
     * <p>
     * {@link SubscriberExceptionHandler#handleException(Throwable, com.google.common.eventbus.SubscriberExceptionContext)
     * SubscriberExceptionHandler.handleException(Throwable, SubscriberExceptionContext)}
     * 方法会对对事件处理过程中产生的异常进行集中处理
     * </p>
     *
     * @param name             标识名称字符串
     * @param exceptionHandler 事件处理过程产生异常的处理对象
     * @return 被注册的 {@link EventBus} 对象
     */
    public EventBus registerEventBus(String name, SubscriberExceptionHandler exceptionHandler) {
        return eventBusMap.compute(name, (n, oldEventBus) -> {
            if (oldEventBus != null) {
                throw new IllegalArgumentException(String.format("Event bus \"%s\" was exist", n));
            }
            if (exceptionHandler == null) {
                return new EventBus();
            }

            return new EventBus(exceptionHandler);
        });
    }

    /**
     * 通过一个名称标识注册一个异步 {@link EventBus} 对象, 并为事件处理设定异常处理对象
     *
     * <p>
     * {@link SubscriberExceptionHandler#handleException(Throwable, com.google.common.eventbus.SubscriberExceptionContext)
     * SubscriberExceptionHandler.handleException(Throwable, SubscriberExceptionContext)}
     * 方法会对对事件处理过程中产生的异常进行集中处理
     * </p>
     *
     * @param name             标识名称字符串
     * @param exceptionHandler 事件处理过程产生异常的处理对象
     * @return 被注册的异步 {@link EventBus} 对象
     */
    public EventBus registerAsyncEventBus(String name, SubscriberExceptionHandler exceptionHandler) {
        return eventBusMap.compute(name, (n, oldEventBus) -> {
            if (oldEventBus != null) {
                throw new IllegalArgumentException(String.format("Event bus \"%s\" was exist", n));
            }
            if (exceptionHandler == null) {
                return new AsyncEventBus(getExecutorService());
            }

            return new AsyncEventBus(getExecutorService(), exceptionHandler);
        });
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                0L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.AbortPolicy());
        }
        return executorService;
    }

    public EventBus getBus(String name) {
        return eventBusMap.computeIfAbsent(name, n -> {
            throw new IllegalArgumentException(String.format("Event bus \"%s\" was not exist", n));
        });
    }

    /**
     * 释放被注册的 {@link EventBus} 对象
     *
     * <p>
     * 该方法只提供测试使用, 在生产代码中, 不应该释放已经注册过的 {@link EventBus} 对象, 而应该使用
     * {@link EventBus#unregister(Object)} 方法来释放一个订阅
     * </p>
     *
     * <p>
     * 本方法只是从 {@code Map} 中删除了 {@link EventBus} 对象, 但如果该 {@link EventBus} 发生的订阅未被解除, 则这个
     * {@link EventBus} 对象无法被垃圾回收, 会导致内存泄漏
     * </p>
     *
     * @param name 要释放的 {@link EventBus} 对象的注册名
     * @return 被释放的 {@link EventBus} 对象
     */
    @VisibleForTesting
    EventBus releaseEventBus(String name) {
        return eventBusMap.remove(name);
    }

    /**
     * 关闭 EventBus 管理器
     */
    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }
}
