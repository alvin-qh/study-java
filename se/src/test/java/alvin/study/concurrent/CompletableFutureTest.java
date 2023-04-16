package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import alvin.study.concurrent.BlockedService.Model;

class CompletableFutureTest {
    // 保存线程执行器对象的集合, 用于在测试结束后进行关闭
    private WeakReference<ExecutorService> executorHolder;

    /**
     * 在每个测试之后执行, 关闭线程池
     */
    @AfterEach
    void afterEach() {
        // 获取线程池对象
        var executor = executorHolder == null ? null : executorHolder.get();
        if (executor != null) {
            // 关闭线程池
            executor.shutdown();
        }
    }

    /**
     * 通过阻塞队列创建线程池
     *
     * <p>
     * 通过
     * {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, java.util.concurrent.BlockingQueue,
     * java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
     * ThreadPoolExecutor.ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory,
     * RejectedExecutionHandler)} 方法用于创建一个线程池执行器对象, 其参数依次如下:
     * <ul>
     * <li>
     * {@code corePoolSize} 核心线程数量, 即线程池中最少的线程数量, 这些线程和线程池具备相同的生命周期, 不会自动销毁
     * </li>
     * <li>
     * {@code maximumPoolSize} 最大线程数量, 当线程池中无空闲线程, 且任务队列已满时, 线程池会增加线程数来提高消费任务队列的效率,
     * 直到增加到该参数指定的数量. 当任务减少后, 则会逐步减少线程数量, 直到线程数为 {@code corePoolSize} 指定的数量
     * </li>
     * <li>
     * {@code keepAliveTime} 和 {@code unit} 参数共同指定了一个时间量, 表示当一个线程从空闲状态到被销毁间隔的时间
     * </li>
     * <li>
     * {@code workQueue} 指定一个 {@code BlockingQueue<Runtime>} 类型的队列, 所有提交的任务均在队列中等待,
     * 直到线程池中的一个线程将其取走并执行, 该队列如果已满, 则必须通过淘汰机制舍弃某个任务
     * </li>
     * <li>
     * {@code threadFactory} 指定一个用于创建线程对象的工厂对象, 参见 {@link java.util.concurrent.ThreadFactory ThreadFactory}
     * </li>
     * <li>
     * {@code handler} 指定一个回调, 当任务队列已满, 则通过该回调决定淘汰那一个任务, 参见
     * {@link java.util.concurrent.RejectedExecutionHandler RejectedExecutionHandler} 类型
     * </li>
     * </ul>
     * </p>
     *
     * @param queueSize 任务队列的长度
     * @return 线程池执行器对象
     */
    private ExecutorService arrayBlockingQueueExecutor(int queueSize) {
        // 获取当前系统的 CPU 逻辑核心数 (Logical Kernel)
        var maxThread = Runtime.getRuntime().availableProcessors();

        // 实例化线程池对象
        // 设置核心线程数和最大线程数一致, 所以线程池不会销毁线程也不会增加线程
        // 使用 ArrayBlockingQueue 保证不会无休止的增加任务
        // 淘汰策略为: 从任务队列中淘汰一个最早的任务, 以容纳新任务 (默认规则是丢弃新任务)
        var executor = new ThreadPoolExecutor(
            maxThread,
            maxThread,
            0,
            TimeUnit.NANOSECONDS,
            new ArrayBlockingQueue<>(queueSize),
            (runnable, exec) -> {
                var queue = exec.getQueue();
                queue.poll();
                queue.offer(runnable);
            });

        // 存储线程池对象以便适时关闭
        executorHolder = new WeakReference<>(executor);
        return executor;
    }

    @Test
    void supplyAsync_shouldExecuteAsyncMethodAndGetResult() throws Exception {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 通过公用的 Fork/Join 线程池执行异步方法
        {
            // 记录程序执行开始时间
            var start = System.currentTimeMillis();

            // 异步执行方法
            var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

            // 获取异步执行方法的返回值, 并确认返回值正确
            var model = future.get(2, TimeUnit.SECONDS);
            then(model).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

            // 确认整个异步方法执行时间
            then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(1L);
        }

        // 通过自定义线程池执行异步方法
        {
            // 创建线程池执行器对象
            var executor = arrayBlockingQueueExecutor(20);

            // 记录程序执行开始时间
            var start = System.currentTimeMillis();

            // 异步执行方法
            var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L), executor);

            // 获取异步执行方法的返回值, 并确认返回值正确
            var model = future.get(2, TimeUnit.SECONDS);
            then(model).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

            // 确认整个异步方法执行时间
            then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(1L);
        }
    }

    @Test
    void thenApply_shouldRunAsyncMethodAfter() throws Exception {
        {
            var service = new BlockedService();

            var start = System.currentTimeMillis();

            var future = CompletableFuture
                    .supplyAsync(() -> service.createModel(new Model(1, "Alvin")))
                    .thenApply(created -> {
                        then(created).isTrue();
                        return service.loadModel(1L);
                    });

            var model = future.get(3, TimeUnit.SECONDS);
            then(model).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

            then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(2L);
        }

        {
            var service = new BlockedService();

            // 创建线程池执行器对象
            var executor = arrayBlockingQueueExecutor(20);

            var start = System.currentTimeMillis();

            // 异步执行方法
            var future = CompletableFuture
                    .supplyAsync(() -> service.createModel(new Model(1L, "Alvin")), executor)
                    .thenApplyAsync(created -> {
                        then(created).isTrue();
                        return service.loadModel(1L);
                    }, executor);

            // 获取异步执行方法的返回值, 并确认返回值正确
            var model = future.get(3, TimeUnit.SECONDS);
            then(model).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

            then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(2L);
        }
    }

    @Test
    void whenComplete_should() {
        {
            var service = new BlockedService();

            var start = System.currentTimeMillis();

            // 在末尾加上 whenComplete 调用
            var future = CompletableFuture
                    .runAsync(() -> service.createModel(new Model(1L, "Alvin")))
                    .thenApplyAsync(ignore -> service.loadModel(1L), null)
                    .whenComplete((mayModel, throwable) -> {

                    });
        }
    }

}
