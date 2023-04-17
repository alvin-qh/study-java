package alvin.study.concurrent;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import alvin.study.concurrent.service.BlockedService;
import alvin.study.concurrent.service.BlockedService.Model;
import alvin.study.concurrent.util.ExecutorCreator;

/**
 * 通过 {@link CompletableFuture} 简化异步代码编写
 *
 * <p>
 * 和通过 {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
 * ExecutorService.submit(Callable)} 方法相比, {@link CompletableFuture} 类型具备更简单有效的异步调用方法, 包括:
 * <ul>
 * <li>
 * 异步的链式调用
 * </li>
 * <li>
 * 异步执行完毕后的回调通知
 * </li>
 * </ul>
 * 通过 {@link CompletableFuture} 方法可以最大限度的避免"线程阻塞", 即在连续
 * </p>
 *
 * <p>
 * {@link CompletableFuture} 类型同时实现了 {@link java.util.concurrent.Future Future} 接口和
 * {@link java.util.concurrent.CompletionStage CompletionStage} 接口, 前者提供了异步任务的状态, 后者提供了异步任务链式调用的能力
 * </p>
 */
class CompletableFutureTest {
    private ExecutorCreator executorCreator = new ExecutorCreator();

    /**
     * 在每个测试之后执行, 关闭线程池
     */
    @AfterEach
    void afterEach() {
        executorCreator.close();
    }

    /**
     * 执行异步任务
     *
     * <p>
     * 通过 {@link CompletableFuture#supplyAsync(java.util.function.Supplier) CompletableFuture.supplyAsync(Supplier)}
     * 方法可以执行一个异步任务, 返回一个 {@link CompletableFuture} 对象表示异步执行情况
     * </p>
     *
     * <p>
     * {@code supplyAsync} 方法会将异步任务放入线程池执行, 由于本例中未指定线程池, 所以任务会通过 Fork/Join 公共线程池执行 (即
     * {@link java.util.concurrent.ForkJoinPool#commonPool() ForkJoinPool.commonPool()} 方法返回的线程池)
     * </p>
     *
     * <p>
     * 和 {@link java.util.concurrent.Future Future} 类型类似, 也可以通过 {@link CompletableFuture#get()} (或
     * {@link CompletableFuture#get(long, TimeUnit)}) 方法获取异步执行的结果
     * </p>
     *
     * <p>
     * 通过 {@link CompletableFuture#runAsync(Runnable) CompletableFuture.runAsync(Runnable)} 方法也可以执行一个异步任务,
     * 返回一个 {@link CompletableFuture} 对象, 但该方法执行的异步任务不应该返回任何值, 仅仅是执行一个动作后结束
     * </p>
     */
    @Test
    void supplyAsync_shouldExecuteAsyncMethodAndGetResultByCommonPool() throws Exception {
        var service = new BlockedService(new Model(1L, "Alvin"));

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

    /**
     * 通过自定义线程池执行异步任务
     *
     * <p>
     * 通过 {@link CompletableFuture#supplyAsync(java.util.function.Supplier, java.util.concurrent.Executor)
     * CompletableFuture.supplyAsync(Supplier, Executor)} 方法可以在一个指定的线程池中执行一个异步任务, 返回一个
     * {@link CompletableFuture} 对象表示异步执行情况
     * </p>
     *
     * <p>
     * 指定的线程池是一个 {@link java.util.concurrent.Executor Executor} 类型的对象, 可以是
     * {@link java.util.concurrent.ExecutorService ExecutorService} 线程池对象, 也可以是
     * {@link java.util.concurrent.ForkJoinPool ForkJoinPool} 线程池对象
     * </p>
     *
     * <p>
     * 通过 {@link CompletableFuture#runAsync(Runnable, java.util.concurrent.Executor)
     * CompletableFuture.runAsync(Runnable, Executor)} 方法可以在指定的线程池中执行无需返回值的异步任务
     * </p>
     */
    @Test
    void supplyAsync_shouldExecuteAsyncMethodAndGetResultByCustomPool() throws Exception {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 创建线程池执行器对象
        var executor = executorCreator.arrayBlockingQueueExecutor(20);

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

    /**
     * 以链式方式调用异步方法
     *
     * <p>
     * 通过 {@link CompletableFuture#thenApply(java.util.function.Function) CompletableFuture.thenApply(Function)}
     * 方法可以对一个已经开始的异步任务进行"链式方式"调用, 它表示: 当前 {@link CompletableFuture} 对象代表的异步任务执行完毕后, 执行
     * {@code thenApply} 方法参数表示的下一个异步任务, 此异步任务的参数为前一个异步任务的返回值, 此异步任务的返回值通过 {@code thenApply}
     * 方法返回的 {@link CompletableFuture} 对象来获取
     * </p>
     *
     * <p>
     * 通过链式调用, 可以避免对任务调用的"阻塞等待", 即不必阻塞线程去等待前一个任务的结果, 即可提交下一个任务, 当前一个任务执行完毕后,
     * 自然的去执行之后的任务
     * </p>
     *
     * <p>
     * 另外, {@code thenApply} 方法会在"和前一个任务相同"的线程池中提交任务, 而
     * {@link CompletableFuture#thenApplyAsync(java.util.function.Function) CompletableFuture.thenApplyAsync(Function)}
     * 方法则会在 Fork/Join 公共线程池中执行后续的任务, 或者通过
     * {@link CompletableFuture#thenApplyAsync(java.util.function.Function, java.util.concurrent.Executor)
     * CompletableFuture.thenApplyAsync(Function, Executor)} 方法在指定线程池中执行后续任务
     * </p>
     *
     * <p>
     * 还可以通过如下方法执行链式异步调用, 包括:
     * <ul>
     * <li>
     * {@link CompletableFuture#thenAccept(java.util.function.Consumer) CompletableFuture.thenAccept(Consumer)} 方法,
     * 表示接收前一个任务的返回值作为参数, 但其自身不返回值
     * </li>
     * <li>
     * {@link CompletableFuture#thenRun(Runnable) CompletableFuture.thenRun(Runnable)} 方法, 表示前一个任务无返回值,
     * 且当前任务自身也无返回值, 只是有一个逻辑上的时序, 后一个任务必须在前一个任务结束后执行
     * </li>
     * </ul>
     * 上述介绍的链式调用方法, 均具备带有 {@code Async} 后缀的方法, 表示后继任务不需要和调用链的第一个任务在相同的线程池运行, 而是在
     * Fork/Join 公共线程池或者自定义线程池中执行
     * </p>
     */
    @Test
    void thenApply_shouldExecuteAsyncMethodInChain() throws Exception {
        var service = new BlockedService();

        // 记录程序执行开始时间
        var start = System.currentTimeMillis();

        // 链式调用执行异步任务, 返回的结果表示最后一个异步任务执行情况
        var future = CompletableFuture
                // 执行第一个任务
                .supplyAsync(() -> service.createModel(new Model(1, "Alvin")))
                // 执行后继任务, 以前一个任务的返回值为参数, 并返回新的结果
                .thenApply(created -> {
                    // 确认前一个任务的返回值
                    then(created).isTrue();

                    // 返回当前任务执行结果
                    return service.loadModel(1L);
                });

        // 获取异步执行方法的返回值, 并确认返回值正确
        var model = future.get(3, TimeUnit.SECONDS);
        then(model).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

        // 确认整个异步方法执行时间
        then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(2L);
    }

    /**
     * 处理异步任务完成回调
     *
     * <p>
     * 通过 {@link CompletableFuture#whenComplete(java.util.function.BiConsumer)
     * CompletableFuture.whenComplete(BiConsumer)} 方法可以指定一个回调, 用于接收整个异步方法链执行完毕的通知. 该回调具备两个参数,
     * <ul>
     * <li>
     * 第一个参数表示调用链最后一个任务返回值, 即整个链式调用的结果
     * </li>
     * <li>
     * 第二个参数表示链式调用中抛出的异常
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@code whenComplete} 会在与第一个任务相同的线程池中执行回调, 也可以通过
     * {@link CompletableFuture#completeAsync(java.util.function.Supplier) CompletableFuture.completeAsync(Supplier)} 或
     * {@link CompletableFuture#completeAsync(java.util.function.Supplier, java.util.concurrent.Executor)
     * CompletableFuture.completeAsync(Supplier, Executor)} 方法在 Fork/Join 公共线程池以及自定义线程池中执行
     * </p>
     */
    @Test
    void whenComplete_shouldCallbackWhenAsyncTaskWasCompleted() throws Exception {
        var service = new BlockedService();

        // 记录程序执行开始时间
        var start = System.currentTimeMillis();

        // 定义一个信号量用于等待异步任务结束
        var sem = new Semaphore(1);
        sem.acquire();

        // 链式调用执行异步任务, 返回的结果表示最后一个异步任务执行情况
        var future = CompletableFuture
                // 执行第一个任务
                .supplyAsync(() -> service.createModel(new Model(1L, "Alvin")))
                // 执行后继任务, 以前一个任务的返回值为参数, 并返回新的结果
                .thenApplyAsync(created -> {
                    // 确认前一个任务的返回值
                    then(created).isTrue();

                    // 返回当前任务执行结果
                    return service.loadModel(1L);
                })
                // 执行完成回调, 监听调用链最后一个任务执行完毕
                .whenComplete((mayModel, ex) -> {
                    // 确认最后一个任务返回结果
                    then(mayModel).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

                    // 确认整个任务链未抛出异常
                    then(ex).isNull();

                    // 释放信号量, 表示任务已结束
                    sem.release();
                });

        // 等待任务结束信号量
        sem.acquire();

        // 获取整个任务执行结果 (此时立即返回结果, 不进行等待)
        var mayModel = future.getNow(Optional.empty());
        // 确认任务结果正确
        then(mayModel).isPresent().get().extracting("id", "name").containsExactly(1L, "Alvin");

        // 确认任务执行时间
        then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(2L);
    }

    /**
     * 合并多个异步任务以求并行执行
     *
     * <p>
     * 通过 {@link CompletableFuture#thenCombine(java.util.concurrent.CompletionStage, java.util.function.BiFunction)
     * CompletableFuture.thenCombine(CompletionStage, BiFunction)} 方法可以对两个异步任务进行合并, 以便让这两个任务并行执行,
     * 并对任务结束返回的结果进行归并操作, 返回表示归并结果的 {@link CompletableFuture} 对象
     * </p>
     *
     * <p>
     * 同样的,
     * {@link CompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage, java.util.function.BiFunction)
     * CompletableFuture.thenCombineAsync(CompletionStage, BiFunction)} 和
     * {@link CompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage, java.util.function.BiFunction, java.util.concurrent.Executor)
     * CompletableFuture.thenCombineAsync(CompletionStage, BiFunction, Executor)} 方法可以用来决定异步方法所执行线程池
     * (Fork/Join 公共线程池或自定义线程池)
     * </p>
     *
     * <p>
     * 本例中演示了一个 Map/Reduce 方式的流程, 即将要查询的参数转化为异步任务, 并行执行, 在依次将每一次任务的结果进行合并, 得到最终结果
     * </p>
     */
    @Test
    void thenCombine_shouldCombineMultipleAsyncTasksWorkAtSameTime() throws Exception {
        var service = new BlockedService(
            new Model(1L, "Alvin"),
            new Model(2L, "Emma"),
            new Model(3L, "Lucy"));

        var args = List.of(1L, 2L, 3L);

        // 记录程序执行开始时间
        var start = System.currentTimeMillis();

        /**
         * 下面这段代码具有相同的逻辑
         *
         * <pre>
         * // 定义第一个异步任务, 表示一个已完成且返回 List 集合的任务
         * var future = CompletableFuture.completedFuture(new ArrayList<Model>());
         *
         * // 遍历参数集合元素
         * for (var arg : args) {
         *     // 通过下一个参数产生异步任务, 并和前一个任务合并
         *     future = future.thenCombine(
         *         CompletableFuture.supplyAsync(() -> service.loadModel(arg)),
         *         (l, opt) -> {
         *             // 合并异步任务执行完毕后的结果, 即前一个任务的结果 List 集合和后一个任务的结果 Optional<Model> 进行合并
         *             if (opt.isPresent()) {
         *                 l.add(opt.get());
         *             }
         *         });
         * }
         * </pre>
         */
        // 遍历参数集合, 并使用 reduce 进行任务合并
        var future = args.stream().reduce(
            // 定义第一个异步任务, 已完成且返回 List 集合
            CompletableFuture.completedFuture(new ArrayList<Model>()),
            // 将前一个任务与通过后一个集合元素产生的异步任务进行合并
            (fc, arg) -> fc.thenCombine(
                // 根据后一个参数产生一个异步任务
                CompletableFuture.supplyAsync(() -> service.loadModel(arg)),
                // 定义异步任务完成后结果如何合并, 即前一个任务的结果 List 集合和后一个任务的结果 Optional<Model> 进行合并
                (l, opt) -> {
                    if (opt.isPresent()) {
                        l.add(opt.get());
                    }
                    return l;
                }),
            (a, b) -> b);

        // 获取一组异步任务的执行结果, 并确认结果正确
        var result = future.get(3, TimeUnit.SECONDS);
        then(result).extracting("id", "name").containsExactlyInAnyOrder(
            tuple(1L, "Alvin"),
            tuple(2L, "Emma"),
            tuple(3L, "Lucy"));

        // 确认任务执行时间, 所有任务都是同时执行
        then(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)).isEqualTo(1L);
    }
}
