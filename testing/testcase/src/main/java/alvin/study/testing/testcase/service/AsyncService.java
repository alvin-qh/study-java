package alvin.study.testing.testcase.service;

import alvin.study.testing.testcase.model.User;
import lombok.SneakyThrows;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 为测试 Awaitility 库定义测试类型
 *
 * <p>
 * 为了模拟异步操作, 本类型的方法设定了"执行时间"的概念, 即方法内部会通过线程执行异步操作, 并等待指定的时间 (模拟现实中执行方法所需时间)
 * </p>
 */
public class AsyncService {
    // 执行普通方法需等待的时长
    private static final int DELAY = 1000;
    // 初始化方法需等待的时长
    private static final int INIT_DELAY = 2000;
    // 异步线程执行器
    private final Executor executor = queuedExecutor(1, 10);
    // Value 值
    private final AtomicLong value = new AtomicLong(0);
    // User 值
    private final AtomicReference<User> userRef = new AtomicReference<>();
    // 是否初始化完毕的标记量
    private volatile boolean initialized = false;

    /**
     * 实例化一个执行器 ({@link Executor} 对象
     *
     * <p>
     * 执行器内部是一个线程池 (Thread Pool), 通过从线程池中获取一个空闲线程并执行代码来进行异步操作
     * </p>
     *
     * @param coreSize 线程池的最小线程数
     * @param maxSize  线程池的最大线程数
     * @return 执行器对象
     */
    private static Executor queuedExecutor(int coreSize, int maxSize) {
        return new ThreadPoolExecutor(
            coreSize, // 核心线程数, 即线程池维护的最小线程数
            maxSize,  // 最大线程数, 即线程池最大可达到的线程数, 达到该数量后, 即便任务再多, 也不会增加线程数
            5,        // 放弃一个任务前等待的时间
            TimeUnit.SECONDS, // keepAliveTime 参数的单位
            new ArrayBlockingQueue<>(maxSize * 10),  // 组织任务的队列对象
            new ThreadPoolExecutor.AbortPolicy()     // 当队列满后, 新加入任务的淘汰机制
        );
    }

    /**
     * 延迟指定的时间, 模拟方法执行过程中的 IO 等待
     *
     * @param mills 延迟时间的毫秒值
     */
    @SneakyThrows
    private static void delay(int mills) {
        Thread.sleep(mills);
    }

    /**
     * 初始化方法
     *
     * <p>
     * 初始化方法耗时 {@link #INIT_DELAY} 毫秒, 通过 {@link #isInitialized()} 方法判断是否初始化完毕
     * </p>
     */
    public void initialize() {
        executor.execute(() -> {
            delay(INIT_DELAY);
            initialized = true;
        });
    }

    /**
     * 获取是否初始化完毕
     *
     * @return {@code true} 表示初始化完毕
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 获取已设置的值
     *
     * @return 之前设置的整数值
     */
    public long getValue() {
        return value.get();
    }

    /**
     * 设置一个值
     *
     * @param value 要设置的整数值
     */
    public void setValue(long value) {
        executor.execute(() -> {
            delay(DELAY);
            this.value.set(value);
        });
    }

    /**
     * 获取已设置的 {@link User} 对象
     *
     * @return 已设置的 {@link User} 对象
     * @throws IllegalStateException @{@link User} 对象值尚未设置或未设置完毕
     */
    public User getUser() {
        var user = userRef.get();
        if (user == null) {
            throw new IllegalStateException("Object not ready");
        }
        return user;
    }

    /**
     * 设置一个 {@link User} 对象
     *
     * @param user {@link User} 对象
     */
    public void setUser(User user) {
        executor.execute(() -> {
            delay(DELAY);
            userRef.set(user);
        });
    }
}
