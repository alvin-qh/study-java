package alvin.study.se.concurrent;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.BlockedService;
import alvin.study.se.concurrent.service.BlockedService.Model;
import alvin.study.se.concurrent.util.ThreadPool;
import alvin.study.se.concurrent.util.TimeIt;

/**
 * 通过 {@link CompletableFuture} 简化异步代码编写
 *
 * <p>
 * 和通过
 * {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
 * ExecutorService.submit(Callable)} 方法相比, {@link CompletableFuture}
 * 类型具备更简单有效的异步调用方法, 包括:
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
 * {@link CompletableFuture} 类型同时实现了
 * {@link java.util.concurrent.Future Future} 接口和
 * {@link java.util.concurrent.CompletionStage CompletionStage} 接口,
 * 前者提供了异步任务的状态, 后者提供了异步任务链式调用的能力
 * </p>
 */
class CompletableFutureTest {
    /**
     * 执行异步任务
     *
     * <p>
     * 通过 {@link CompletableFuture#supplyAsync(java.util.function.Supplier)
     * CompletableFuture.supplyAsync(Supplier)}
     * 方法可以执行一个异步任务, 返回一个 {@link CompletableFuture} 对象表示异步执行情况
     * </p>
     *
     * <p>
     * {@code supplyAsync} 方法会将异步任务放入线程池执行, 由于本例中未指定线程池,
     * 所以任务会通过 Fork/Join 公共线程池执行
     * (即 {@link java.util.concurrent.ForkJoinPool#commonPool()
     * ForkJoinPool.commonPool()} 方法返回的线程池)
     * </p>
     *
     * <p>
     * 和 {@link java.util.concurrent.Future Future} 类型类似, 也可以通过
     * {@link CompletableFuture#get()} (或
     * {@link CompletableFuture#get(long, TimeUnit)}) 方法获取异步执行的结果
     * </p>
     *
     * <p>
     * 通过 {@link CompletableFuture#runAsync(Runnable)
     * CompletableFuture.runAsync(Runnable)} 方法也可以执行一个异步任务,
     * 返回一个 {@link CompletableFuture} 对象, 但该方法执行的异步任务不应该返回任何值,
     * 仅仅是执行一个动作后结束
     * </p>
     */
    @Test
    @SneakyThrows
    void supplyAsync_shouldExecuteTaskAndGetResult() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 获取异步执行方法的返回值, 并确认返回值正确
        // 异步任务使用约 1s 执行完毕返回结果
        var model = future.get(110, TimeUnit.MILLISECONDS);

        // 确认返回值正确
        then(model).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");
    }

    /**
     * 测试通过自定义线程池执行器执行异步任务
     *
     * <p>
     * 通过
     * {@link CompletableFuture#supplyAsync(java.util.function.Supplier,
     * java.util.concurrent.Executor) CompletableFuture.supplyAsync(Supplier,
     * Executor)} 方法可以在一个指定的线程池中执行一个异步任务, 返回一个
     * {@link CompletableFuture} 对象表示异步执行情况
     * </p>
     *
     * <p>
     * 指定的线程池是一个 {@link java.util.concurrent.Executor Executor} 类型的对象,
     * 可以是 {@link java.util.concurrent.ExecutorService ExecutorService}
     * 线程池对象, 也可以是 {@link java.util.concurrent.ForkJoinPool ForkJoinPool}
     * 线程池对象
     * </p>
     *
     * <p>
     * 通过 {@link CompletableFuture#runAsync(Runnable, java.util.concurrent.Executor)
     * CompletableFuture.runAsync(Runnable, Executor)}
     * 方法可以在指定的线程池中执行无需返回值的异步任务
     * </p>
     */
    @Test
    void supplyAsync_shouldExecuteTaskByCustomExecutorAndGetResult() throws Exception {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 创建线程池执行器对象
        try (var executor = ThreadPool.fixedPoolExecutor(20)) {
            // 启动异步任务
            var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L), executor);

            // 获取异步执行方法的返回值, 并确认返回值正确
            // 异步任务使用约 1s 执行完毕返回结果
            var model = future.get(1100, TimeUnit.MILLISECONDS);

            // 确认返回值正确
            then(model).isPresent()
                    .get()
                    .extracting("id", "name")
                    .containsExactly(1L, "Alvin");
        }
    }

    /**
     * 测试等待任务执行完毕并获取结果
     *
     * <p>
     * 通过 {@link CompletableFuture#get()} 方法可以获取异步任务的执行结果返回值
     * </p>
     *
     * <p>
     * {@link CompletableFuture#get()} 方法会阻塞当前线程,
     * 直到异步任务执行完毕返回执行结果
     * </p>
     */
    @Test
    @SneakyThrows
    void get_shouldExecuteTaskAndGetResultUntilTaskIsDone() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 记录任务开始时间
        var timeit = TimeIt.start();

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 等待任务直到任务执行结束, 获取结果, 此过程会阻塞
        var result = future.get();

        // 确认整个任务耗时 100ms 左右
        then(timeit.since()).isBetween(100L, 120L);

        // 确认结果为预期结果
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");

        // 确认结果被获取到
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");
    }

    /**
     * 测试执行异步任务, 并在指定时间周期内获取结果
     *
     * <p>
     * 通过 {@link CompletableFuture#get(long, TimeUnit)}
     * 方法可以在指定时间周期内获取异步任务的执行结果, 如果在此指定的时间周期内,
     * 异步任务执行完毕, 则返回其结果, 否则抛出 {@link TimeoutException} 异常
     * </p>
     */
    @Test
    void get_shouldExecuteTaskAndGetResultInTimePeriod() throws Exception {
        var service = new BlockedService(new Model(1L, "Alvin"));

        var timeit = TimeIt.start();

        // 启动异步任务
        var future1 = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 等待任务, 如果任务在 2s 内结束, 则获取结果, 否则抛出 TimeoutException 异常
        var result = future1.get(110, TimeUnit.MILLISECONDS);

        // 确认整个任务耗时 100ms 左右
        then(timeit.since()).isBetween(100L, 120L);

        // 确认任务已经完成
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");

        // 确认结果被获取到
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");

        // 再次执行一个异步任务
        var future2 = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 等待任务执行结果, 确认因等待时间过短, 导致超时异常
        thenThrownBy(() -> future2.get(50, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);
    }

    /**
     * 测试立即获取异步任务的结果
     *
     * <p>
     * 通过 {@link CompletableFuture#getNow(Object) CompletableFuture.getNow(T)}
     * 方法可以从异步任务对象中获取执行结果, 如果异步任务已经结束, 则结果立即被返回,
     * 如果异步任务尚未结束, 则返回由参数指定的缺省值
     * </p>
     */
    @Test
    void getNow_shouldExecuteTaskAndGetResultImmediately() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 记录开始时间
        var timeit = TimeIt.start();

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 立即获取结果, 并指定缺省值
        var result = future.getNow(Optional.empty());

        // 确认此时任务尚未结束, 结果为缺省值
        then(result).isEmpty();

        // 等待 100ms 后
        await().until(() -> timeit.since() > 100);

        // 再次立即获取结果, 并指定缺省值
        result = future.getNow(Optional.empty());

        // 确认此时任务已经结束, 并获取到正确结果
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");
    }

    /**
     * 测试结束任务
     *
     * <p>
     * 通过 {@link CompletableFuture#complete(Object)
     * CompletableFuture.complete(T)} 方法可以立即结束当前异步任务
     * </p>
     *
     * </p>
     * 如果执行 {@code complete} 方法前任务已结束, 则正常返回任务结果,
     * 反之以 {@link CompletableFuture#complete(Object)
     * CompletableFuture.complete(T)} 方法的参数作为该任务的结果
     * </p>
     */
    @Test
    @SneakyThrows
    void complete_shouldCompleteTaskByGivenResult() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        var timeit = TimeIt.start();

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 如果任务尚未结束, 则结束任务, 并设置任务结果
        var completed = future.complete(Optional.empty());

        // 确认任务执行结束
        then(completed).isTrue();

        // 确认任务已经执行结束
        then(future.isDone()).isTrue();

        // 确认任务到结束为消耗原本时间, 即因为 complete 方法的调用导致任务提前结束
        then(timeit.since() / 100).isEqualTo(0);

        // 确认任务已经结束
        then(completed).isTrue();
        then(future.isDone()).isTrue();

        // 获取任务执行结果
        var result = future.get();

        // 确认结果是设置的结果而非任务本身执行的返回值
        then(result).isEmpty();
    }

    /**
     * 测试结束任务
     *
     * <p>
     * 通过 {@link CompletableFuture#completeAsync(java.util.function.Supplier)
     * CompletableFuture.completeAsync(Supplier)}
     * 方法可以为当前任务增加一个异步回调, 如果该回调执行完成前任务已结束, 则正常返回任务结果,
     * 否则以该回调的结果为任务结果并结束任务.
     * </p>
     *
     * <p>
     * {@link CompletableFuture#completeAsync(java.util.function.Supplier, Executor)
     * CompletableFuture.completeAsync(Supplier, Executor)}
     * 方法也可以通过第二个参数指定一个自定义线程池
     * </p>
     */
    @Test
    @SneakyThrows
    void completeAsync_shouldCompleteTaskAsyncByGivenResult() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        var timeit = TimeIt.start();

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 为任务添加一个异步回调, 如果该回调执行完成任务仍未结束,
        // 则以该回调的返回值作为任务结果并结束任务
        // 这里整个任务执行完成需 100ms, 所以 complete 回调会率先完成,
        // 将 Optional.empty() 作为任务结果并提前结束任务
        future = future.completeAsync(Optional::empty);

        // 获取任务执行结果
        var result = future.get();

        // 确认任务执行结束
        then(future.isDone()).isTrue();

        // 确认结果是设置的结果而非任务本身执行的返回值
        then(result).isEmpty();

        // 确认任务到结束为消耗原本时间, 即因为 completeAsync 方法的调用导致任务提前结束
        then(timeit.since() / 100).isEqualTo(0);

        // 重新启动异步任务
        future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 为任务添加异步回调, 本次令回调时间超出任务执行时间
        future = future.completeAsync(() -> {
            try {
                Thread.sleep(110);
            } catch (InterruptedException ignore) {}

            return Optional.empty();
        });

        // 获取任务执行结果
        result = future.get();

        // 确认任务执行结束
        then(future.isDone()).isTrue();

        // 确认任务执行结果正确
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");
    }

    /**
     * 测试结束任务
     *
     * <p>
     * 通过 {@link CompletableFuture#completeOnTimeout(Object, long, TimeUnit)
     * CompletableFuture.completeOnTimeout(T, long, TimeUnit)}
     * 方法可以为当前任务增加一个延时异步回调, 在指定的超时时间内, 如果任务结束,
     * 则正常返回任务结果, 否则执行该回调, 并以回调结果作为任务结果, 结束异步任务
     * </p>
     */
    @Test
    @SneakyThrows
    void completeOnTimeout_shouldCompleteTask() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        var timeit = TimeIt.start();

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 为任务添加一个异步回调, 在指定的超时时间后, 如果任务仍未执行完毕,
        // 则结束任务, 并将指定的结果作为任务结果
        future = future.completeOnTimeout(Optional.empty(), 50, TimeUnit.MILLISECONDS);

        // 获取任务执行结果
        var result = future.get();

        // 确认结果是设置的结果而非任务本身执行的返回值
        then(result).isEmpty();

        // 确认任务到结束为消耗原本时间, 即因为 completeAsync 方法的调用导致任务提前结束
        then(timeit.since() / 100).isEqualTo(0);

        // 再次执行任务, 确认任务已经结束, 并且结果是 null
        future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 为任务添加一个异步回调, 在指定的超时时间后, 如果任务仍未执行完毕,
        // 则结束任务, 并将指定的结果作为任务结果
        future = future.completeOnTimeout(Optional.empty(), 110, TimeUnit.MILLISECONDS);

        // 获取任务执行结果
        result = future.get();

        // 确认此时任务已经结束, 并获取到正确结果
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");

        // 确认任务到结束为消耗原本时间, 即因为 completeAsync 方法的调用导致任务提前结束
        then(timeit.since() / 100).isEqualTo(1);
    }

    /**
     * 测试结束任务
     *
     * <p>
     * 通过 {@link CompletableFuture#completeExceptionally(Throwable)}
     * 方法可以设置一个异常对象, 当调用此方法时任务尚未结束,
     * 则结束当前异步任务, 之后在执行 {@link CompletableFuture#get()}
     * 方法时会抛出设定的异常
     * </p>
     */
    @Test
    void complete_shouldCompleteTaskByRaiseException() {
        var service = new BlockedService(new Model(1L, "Alvin"));

        // 启动异步任务
        var future = CompletableFuture.supplyAsync(() -> service.loadModel(1L));

        // 设置一个异常, 并结束当前任务
        var completed = future.completeExceptionally(new IllegalStateException("Timeout"));

        // 确认任务已完成
        then(completed).isTrue();

        // 确认任务已结束
        then(future).isDone();

        // 确认任务是以 "抛出异常" 方式结束的
        then(future.isCompletedExceptionally()).isTrue();

        // 确认获取任务结果时会抛出指定异常
        thenThrownBy(future::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Timeout");
    }

    /**
     * 在异步任务中增加链式调用
     *
     * <p>
     * 通过 {@link CompletableFuture#thenApply(java.util.function.Function)
     * CompletableFuture.thenApply(Function)} 方法可以对一个已经开始的异步任务进行
     * "链式方式" 调用, 它表示: 在已有 {@link CompletableFuture} 对象中增加链式调用,
     * 当前一个步骤执行完毕后, 接着执行
     * {@link CompletableFuture#thenApply(java.util.function.Function)
     * CompletableFuture.thenApply(Function)} 方法指定的下一个步骤,
     * 且前一部分的返回值会作为后一部分的参数传递
     * </p>
     *
     * <p>
     * 此异步任务的返回值仍是通过 {@link CompletableFuture} 对象来获取
     * </p>
     *
     * <p>
     * 通过链式调用, 可以避免对任务调用的"阻塞等待", 即不必阻塞线程去等待前一个任务的结果,
     * 即可提交下一个任务, 当前一个任务执行完毕后, 自然的去执行之后的任务
     * </p>
     *
     * <p>
     * 链式调用是在同一个 {@link CompletableFuture} 对象上进行的,
     * 相当于不断增加了异步任务的步骤
     * </p>
     *
     * <p>
     * 另外, {@code thenApply} 方法会在"和前一部分相同"的线程池中提交任务, 而
     * {@link CompletableFuture#thenApplyAsync(java.util.function.Function)
     * CompletableFuture.thenApplyAsync(Function)}
     * 方法则会在 Fork/Join 公共线程池中执行后续部分, 或者通过
     * {@link CompletableFuture#thenApplyAsync(java.util.function.Function,
     * java.util.concurrent.Executor)
     * CompletableFuture.thenApplyAsync(Function, Executor)}
     * 方法在指定线程池中执行后续部分
     * </p>
     *
     * <p>
     * 还可以通过如下方法执行链式异步调用, 包括:
     *
     * <ul>
     * <li>
     * {@link CompletableFuture#thenAccept(java.util.function.Consumer)
     * CompletableFuture.thenAccept(Consumer)} 方法,
     * 表示接收前一部分的返回值作为参数, 但其自身不返回值
     * </li>
     * <li>
     * {@link CompletableFuture#thenRun(Runnable)
     * CompletableFuture.thenRun(Runnable)} 方法, 表示前一部分无返回值,
     * 且当前部分自身也无返回值, 只是有一个逻辑上的时序,
     * 即后一部分必须在前一个部分执行完毕后执行
     * </li>
     * </ul>
     *
     * 上述介绍的链式调用方法, 均具备带有 {@code Async} 后缀的方法,
     * 表示指定的链式调用是在 Fork/Join 公共线程池或者自定义线程池中执行
     * </p>
     */
    @Test
    @SneakyThrows
    void thenApply_shouldExecuteAsyncMethodInChain() {
        var service = new BlockedService();

        // 链式调用执行异步任务, 返回的结果表示最后一个异步任务执行情况
        var future = CompletableFuture
                // 执行第一个任务
                .supplyAsync(() -> service.saveModel(new Model(1, "Alvin")))
                // 执行后继任务, 以前一个任务的返回值为参数, 并返回新的结果
                .thenApply(created -> {
                    // 确认前一个任务的返回值
                    then(created).isTrue();

                    // 返回当前任务执行结果
                    return service.loadModel(1L);
                });

        // 获取异步执行方法的返回值
        // 异步任务使用约 200ms 执行完毕返回结果 (两个链式任务)
        var model = future.get(250, TimeUnit.MILLISECONDS);

        // 确认任务执行结果正确
        then(model).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");
    }

    /**
     * 处理异步任务完成回调
     *
     * <p>
     * 通过 {@link CompletableFuture#whenComplete(java.util.function.BiConsumer)
     * CompletableFuture.whenComplete(BiConsumer)} 方法可以指定一个回调,
     * 用于接收整个异步方法链执行完毕的通知.
     * </p>
     *
     * <p>
     * 该回调具备两个参数:
     *
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
     * 可以通过具备 {@code Async} 后缀的同名方法在 Fork/Join 公共线程池以及自定义线程池中执行
     * </p>
     */
    @Test
    @SneakyThrows
    void whenComplete_shouldCallbackWhenAsyncTaskWasCompleted() {
        var service = new BlockedService();

        // 记录程序执行开始时间
        var timeit = TimeIt.start();

        // 链式调用执行异步任务, 返回的结果表示最后一个异步任务执行情况
        var future = CompletableFuture
                // 执行第一个任务
                .supplyAsync(() -> service.saveModel(new Model(1L, "Alvin")))
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
                    then(mayModel).isPresent()
                            .get()
                            .extracting("id", "name")
                            .containsExactly(1L, "Alvin");

                    // 确认整个任务链未抛出异常
                    then(ex).isNull();
                });

        // 获取整个任务执行结果
        var result = future.get();

        // 确认任务结果正确
        then(result).isPresent()
                .get()
                .extracting("id", "name")
                .containsExactly(1L, "Alvin");

        // 确认异步任务使用约 200ms 执行完毕返回结果 (两个链式任务)
        then(timeit.since() / 200).isEqualTo(1);
    }

    /**
     * 令多个异步任务并行执行, 并合并执行结果
     *
     * <p>
     * 通过
     * {@link CompletableFuture#thenCombine(
     * java.util.concurrent.CompletionStage, java.util.function.BiFunction)
     * CompletableFuture.thenCombine(CompletionStage, BiFunction)}
     * 方法可以对两个异步任务进行合并, 以便让这两个任务并行执行,
     * 并对任务结束返回的结果进行归并操作, 返回表示归并结果的 {@link CompletableFuture}
     * 对象
     * </p>
     *
     * <p>
     * 同样的,
     * {@link CompletableFuture#thenCombineAsync(
     * java.util.concurrent.CompletionStage, java.util.function.BiFunction)
     * CompletableFuture.thenCombineAsync(CompletionStage, BiFunction)} 和
     * {@link CompletableFuture#thenCombineAsync(
     * java.util.concurrent.CompletionStage, java.util.function.BiFunction,
     * java.util.concurrent.Executor)
     * CompletableFuture.thenCombineAsync(CompletionStage, BiFunction, Executor)}
     * 方法可以用来决定异步方法所执行线程池 (Fork/Join 公共线程池或自定义线程池)
     * </p>
     *
     * <p>
     * 本例中演示了一个 Map/Reduce 方式的流程, 即将要查询的参数转化为异步任务, 并行执行,
     * 在依次将每一次任务的结果进行合并, 得到最终结果
     * </p>
     *
     * <p>
     * 本例可以通过一个小技巧, 即: {@link CompletableFuture#completedFuture(Object)
     * CompletableFuture.completedFuture(T)} 方法产生一个"已完成" 且 "返回值已确定"
     * 的异步任务对象, 从而和其它正常的异步任务发生合并调用或链式调用
     * </p>
     */
    @Test
    void thenCombine_shouldCombineMultipleAsyncTasksWorkAtSameTime() throws Exception {
        var service = new BlockedService(
            new Model(1L, "Alvin"),
            new Model(2L, "Emma"),
            new Model(3L, "Lucy"));

        // 定义参数集合
        var args = List.of(1L, 2L, 3L);

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
         *             // 合并异步任务执行完毕后的结果,
         *             // 即前一个任务的结果 List 集合和后一个任务的结果 Optional<Model> 进行合并
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
                // 要被合并的异步任务
                CompletableFuture.supplyAsync(() -> service.loadModel(arg)),
                // 定义异步任务完成后结果如何合并, 即前一个任务的结果 List 集合和后一个任务的结果 Optional<Model> 进行合并
                (models, opt) -> {
                    opt.ifPresent(models::add);
                    return models;
                }),
            // 如何合并每次 reduce 产生的结果, 因为 reduce 是发生在一个集合对象上, 所以 r1, r2 表示同一个对象, 无需合并
            (r1, r2) -> r2);

        // 获取一组异步任务的执行结果
        // 异步任务使用约 300ms 执行完毕返回结果 (3 个并行任务, 每个任务需 100ms)
        var result = future.get(350, TimeUnit.MILLISECONDS);

        // 确认结果正确
        then(result).extracting("id", "name")
                .containsExactlyInAnyOrder(
                    tuple(1L, "Alvin"),
                    tuple(2L, "Emma"),
                    tuple(3L, "Lucy"));
    }

    /**
     * 令多个异步任务串行执行, 并合并执行结果
     *
     * <p>
     * 通过 {@link CompletableFuture#thenCompose(java.util.function.Function)
     * CompletableFuture.thenCompose(Function)}
     * 可以基于一个异步任务执行完毕的基础上, 执行指定的异步任务
     * </p>
     *
     * <p>
     * 与 {@link CompletableFuture#thenApply(java.util.function.Function)
     * CompletableFuture.thenApply(Function)} 方法类似,
     * 两者都是在前一个异步任务执行结果上执行下一个异步任务, 两者主要区别是,
     * 前者的 {@code Function} 参数返回的是一个 {@link CompletableFuture} 对象,
     * 而后者的 {@code Function} 参数则是整个异步链式调用 {@link CompletableFuture}
     * 的一部分
     * </p>
     *
     * <p>
     * 也就是说, {@code thenCompose} 方法是将多个 {@link CompletableFuture}
     * 对象组合在一起顺序调用, 而 {@code thenCompose}
     * 方法是将一系列回调对象以按顺序调用组合成一个 {@link CompletableFuture} 对象
     * </p>
     */
    @Test
    @SneakyThrows
    void thenCompose_shouldExecuteAsyncTaskOneByOneAndCombineTheResult() {
        var service = new BlockedService(
            new Model(1L, "Alvin"),
            new Model(2L, "Emma"),
            new Model(3L, "Lucy"));

        // 定义参数集合
        var args = List.of(1L, 2L, 3L);

        /**
         * 下面这段代码具有相同的逻辑
         *
         * <pre>
         * var future = CompletableFuture.completedFuture(new ArrayList<Model>());
         *
         * for (var arg : args) {
         *     future = future.thenCompose(l -> CompletableFuture.supplyAsync(() -> {
         *         service.loadModel(arg).ifPresent(l::add);
         *         return l;
         *     }));
         * }
         * </pre>
         */

        // 遍历参数集合, 并使用 reduce 进行任务合并
        var future = args.stream().reduce(
            // 定义第一个异步任务, 已完成且返回 List 集合
            CompletableFuture.completedFuture(new ArrayList<Model>()),
            // 在前一个异步任务完成的结果上, 产生下一个异步任务
            (fc, arg) -> fc.thenCompose(
                models -> CompletableFuture.supplyAsync(() -> {
                    // 将执行结果和上一个异步任务的结果合并
                    service.loadModel(arg).ifPresent(models::add);
                    return models;
                })),
            // 如何合并每次 reduce 产生的结果, 因为 reduce 是发生在一个集合对象上,
            // 所以 r1, r2 表示同一个对象, 无需合并
            (r1, r2) -> r2);

        // 获取一组异步任务的执行结果
        // 异步任务使用约 300ms 执行完毕返回结果 (3 个串行任务, 每个任务需 100ms)
        var result = future.get(350, TimeUnit.MILLISECONDS);

        // 确认结果正确
        then(result).extracting("id", "name")
                .containsExactlyInAnyOrder(
                    tuple(1L, "Alvin"),
                    tuple(2L, "Emma"),
                    tuple(3L, "Lucy"));
    }

    /**
     * 在异步任务调用链中增加特殊回调
     *
     * <p>
     * 通过
     * {@link CompletableFuture#thenAcceptBoth(
     * java.util.concurrent.CompletionStage, java.util.function.BiConsumer)
     * CompletableFuture.thenAcceptBoth(CompletionStage, BiConsumer)}
     * 方法可以为异步任务调用链增加一个无返回值的回调
     * </p>
     *
     * <p>
     * 类似 {@link CompletableFuture#thenAccept(java.util.function.Consumer)
     * CompletableFuture.thenAccept(Consumer)} 方法,
     * 但与之不同的是, {@code thenAcceptBoth} 方法允许执行一个附加的异步调用,
     * 并在之后的回调中同时传递两个异步任务的执行结果
     * </p>
     *
     * <p>
     * 同样的, 通过具有 {@code Async} 后缀的方法, 可以使用 Fork/Join
     * 公共线程池以及自定义线程池
     * </p>
     */
    @Test
    @SneakyThrows
    void thenAcceptBoth_shouldAcceptCurrentAndOtherAsyncMethods() {
        var service = new BlockedService(
            new Model(1L, "Alvin"),
            new Model(2L, "Emma"));

        // 保存结果的集合对象
        var results = new ArrayList<Model>();

        // 执行异步任务
        var future = CompletableFuture
                // 第一部分异步调用
                .supplyAsync(() -> service.loadModel(1L))
                // 在第一部分调用的基础上执行第二部分调用, 并合并两部分调用结果
                .thenAcceptBoth(
                    // 第二部分异步调用
                    CompletableFuture.supplyAsync(() -> service.loadModel(2L)),
                    // 当两部分调用均完成后, 合并调用结果进行回调
                    (opt1, opt2) -> {
                        if (opt1.isPresent() && opt2.isPresent()) {
                            results.add(opt1.get());
                            results.add(opt2.get());
                        }
                    });

        // 确认异步调用整体结束, 且持续 100ms 左右 (完成两部分并发调用)
        future.get(120, TimeUnit.MILLISECONDS);

        // 确认和并的结果符合预期
        then(results).extracting("id", "name")
                .containsExactlyInAnyOrder(
                    tuple(1L, "Alvin"),
                    tuple(2L, "Emma"));
    }

    /**
     * 从两个异步任务中获取执行速度较快的那一个的结果, 并放弃另一个
     *
     * <p>
     * 通过 {@link CompletableFuture#acceptEither(
     * java.util.concurrent.CompletionStage, java.util.function.Consumer)
     * CompletableFuture.acceptEither(CompletionStage, Consumer)}
     * 方法用于从当前异步任务和另一个异步任务中竞争结果,
     * 执行较快的那个异步任务的结果会传递给回调, 另一个异步任务则被放弃
     * </p>
     *
     * <p>
     * 注意, 因为是两个异步任务竞争, 所以两个异步任务的返回结果类型必须相同
     * </p>
     *
     * <p>
     * 同样的, 通过具有 {@code Async} 后缀的方法, 可以使用 Fork/Join
     * 公共线程池以及自定义线程池
     * </p>
     */
    @Test
    void acceptEither_shouldGetResultBetweenTwoTasksWhichReturnedFirst() throws Exception {
        var service = new BlockedService(
            new Model(1L, "Alvin"),
            new Model(2L, "Emma"));

        // 保存结果的引用对象
        var reference = new AtomicReference<Model>();

        // 执行异步任务
        var future = CompletableFuture
                // 第一部分异步调用, 耗时 2s 左右
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {}

                    return service.loadModel(1L);
                })
                // 在第一部分调用的基础上执行第二部分调用, 并合并两部分调用结果
                .acceptEither(
                    // 第二部分异步调用, 耗时 1s 左右
                    CompletableFuture.supplyAsync(() -> service.loadModel(2L)),
                    // 回调, 参数为两个异步任务的竞争结果, 为执行较快的任务的结果
                    result -> result.ifPresent(reference::set));

        // 确认异步调用整体结束, 且持续 200ms 左右 (完成两部分并发调用, 以耗时最长的任务为准)
        future.get(220, TimeUnit.MILLISECONDS);

        // 确认和并的结果符合预期, 为耗时较少的那个任务的结果
        then(reference.get())
                .extracting("id", "name")
                .contains(2L, "Emma");
    }

    /**
     * 产生 {@code count} 个 {@link Model} 对象组成的 {@link List} 集合
     *
     * @param count {@link List} 集合中 {@link Model} 元素的个数
     * @return 包含指定数量 {@link Model} 对象的 {@link List} 集合
     */
    private List<Model> generateModels(int count) {
        var rand = new Random();

        return LongStream
                // 产生 [1..count] 区间内的整数值
                .range(1, count + 1)
                // 将产生的整数值装箱
                .boxed()
                // 打乱顺序
                .sorted((l, r) -> rand.nextInt(-1, 1))
                // 将每个整数值转为 Model 类型对象
                .map(id -> new Model(id, String.format("Name-%d", id)))
                .toList();
    }

    /**
     * 将 {@link Model} 对象进行保存
     *
     * @param service 用于保存 {@link Model} 对象的服务类对象
     * @param models  {@link Model} 对象集合
     * @return 异步任务对象, 执行结果为已保存对象的 id 集合
     */
    private CompletableFuture<List<Long>> saveModels(
            BlockedService service,
            List<Model> models,
            Executor executor) {
        // 将模型对象集合转为存储模型对象的任务
        return models.stream().reduce(
            // 用于合并之后任务的异步任务
            CompletableFuture.completedFuture(new ArrayList<>()),
            // 将任务和前一个任务进行合并
            (fc, model) -> fc.thenCombineAsync(
                // 存储一个模型对象的异步任务
                CompletableFuture.supplyAsync(() -> service.saveModel(model), executor),
                // 执行完毕后, 和前一个任务结果进行合并
                (results, success) -> {
                    if (success) {
                        results.add(model.id());
                    }
                    return results;
                },
                executor),
            (a, b) -> b);
    }

    /**
     * 根据 id 集合读取对应的 {@link Model} 对象集合
     *
     * @param service 用于读取 {@link Model} 对象的服务类对象
     * @param ids     id 值集合
     * @return 异步任务对象, 执行结果读取到的所有 {@link Model} 对象集合
     */
    private CompletableFuture<List<Model>> loadModels(BlockedService service, List<Long> ids, Executor executor) {
        // 将 id 集合转为读取模型对象的任务
        return ids.stream().reduce(
            // 用于合并之后任务的异步任务
            CompletableFuture.completedFuture(new ArrayList<>()),
            // 将任务和前一个任务进行合并
            (fc, id) -> fc.thenCombineAsync(
                // 根据 id 读取一个模型对象的任务
                CompletableFuture.supplyAsync(() -> service.loadModel(id), executor),
                // 执行完毕后, 和前一个任务结果进行合并
                (models, result) -> {
                    result.ifPresent(models::add);
                    return models;
                },
                executor),
            (a, b) -> b);
    }

    /**
     * 获取异步任务的执行状态
     *
     * <p>
     * 本例以一个 DAG 任务图来构建任务关系, 并展示各个异步任务的状态, 整个任务图如下:
     * </p>
     *
     * <p>
     * <img src="assets/completable_future_dag.png"/>, 其中:
     *
     * <ul>
     * <li>
     * F0 任务作为所有任务的起点, 其作用是产生一系列随机数据 (模拟从网络抓取数据包)
     * </li>
     * <li>
     * F0 执行完毕后, 接着执行 F1-1 和 F2-1 两个任务 (并发执行), F1-1 会将 F0
     * 产生数据中 id 为偶数的部分分离出来, 而 F2-1 会将 F0 产生数据中 id
     * 为奇数的部分分离出来
     * </li>
     * <li>
     * F1-2 会在 F1-1 结束后运行, 将 F1-1 分离出的那部分数据进程存储,
     * 每存储一条数据需要 10ms 延时, 所以 F1-2 相当于是若干并发任务组合在一起,
     * 每个任务存储一条数据; 类似的, F2-1 也是完成同样的工作
     * </li>
     * <li>
     * F3-1 任务是当 F1-2 和 F2-2 两个任务执行完毕后, 对执行结果的归并
     * </li>
     * <li>
     * F3-2 任务是将 F3-1 归并出来的结果, 将数据从数据源中再读取出来
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 本例中会调用 {@link BlockedService#saveModel(Model)} 和
     * {@link BlockedService#loadModel(long)} 方法, 该方法会阻塞约
     * 1s 时间, 如果 1000 个对象逐一进行一次读写操作, 则总体会耗时
     * 1 * 1000 * 2 约 2000s;
     * </p>
     *
     * <p>
     * 本例中将所有的写操作和读操作并发执行,
     * 所以整体耗时 2s 就可以完成 (其中 1s 完成所有的写操作, 之后在通过 1s
     * 完成所有的读操作), 加上其它任务的损耗, 约 2.2s 内即可以完成所有操作
     * </p>
     *
     * <p>
     * 本例中计算任务不多, 线程主要损耗来自于阻塞等待, 可以看作是 IO 密集型任务,
     * 所以使用 {@link ThreadPool#synchronousTaskExecutor()}
     * 方法来创建线程池, 即用大量线程执行任务以应对线程阻塞情况, 提高并发性能
     * </p>
     */
    @Test
    @SneakyThrows
    void status_shouldGetStatusOfAsyncTask() {
        var service = new BlockedService();

        // 创建线程池对象, 使用大量线程保证并发性, 即每个任务均有一个线程来执行 (IO 密集型)
        try (var executor = ThreadPool.synchronousTaskExecutor()) {
            // 根任务: 产生一系列随机数据, 模拟从网络抓取数据的情形
            var f0 = CompletableFuture.supplyAsync(() -> {
                try {
                    // 等待通知, 启动任务
                    synchronized (service) {
                        service.wait();
                    }

                    // 生成 1000 个数据
                    return generateModels(1000);
                } catch (InterruptedException ignore) {
                    return List.<Model>of();
                }
            }, executor);

            // 任务 1.1: 筛选 id 为偶数的任务
            var f1_1 = f0.thenComposeAsync(
                models -> CompletableFuture.supplyAsync(
                    () -> models.stream().filter(m -> m.id() % 2 == 0).toList()),
                executor);

            // 任务 1.2: 存储 id 为偶数的任务
            var f1_2 = f1_1.thenComposeAsync(models -> saveModels(service, models, executor), executor);

            // 任务 2.1: 筛选 id 为奇数的任务
            var f2_1 = f0.thenComposeAsync(
                models -> CompletableFuture.supplyAsync(() -> models.stream().filter(m -> m.id() % 2 != 0).toList()),
                executor);

            // 任务 2.2: 存储 id 为奇数的任务
            var f2_2 = f2_1.thenComposeAsync(models -> saveModels(service, models, executor), executor);

            // 任务 3: 合并两部分 id
            var f3_1 = f1_2.thenCombineAsync(
                f2_2,
                (ids1, ids2) -> Stream.concat(ids1.stream(), ids2.stream()).sorted().toList(),
                executor);

            // 任务 3.2: 根据 id 读取数据
            var f3_2 = f3_1.thenComposeAsync(ids -> loadModels(service, ids, executor), executor);

            // 确认各个任务所被依赖的任务个数
            then(f0.getNumberOfDependents()).isEqualTo(2);
            then(f1_1.getNumberOfDependents()).isEqualTo(1);
            then(f1_2.getNumberOfDependents()).isEqualTo(1);
            then(f2_1.getNumberOfDependents()).isEqualTo(1);
            then(f2_2.getNumberOfDependents()).isEqualTo(1);
            then(f3_1.getNumberOfDependents()).isEqualTo(1);
            then(f3_2.getNumberOfDependents()).isEqualTo(0);

            // 发出通知, 令 T0 任务开始执行
            synchronized (service) {
                service.notify();
            }

            // 获取最后一个任务的结果, 当该任务完成后, 之前的所有任务均已完成
            // 整个任务耗时约 300ms 左右, 即所有的对象存储和读取均为并发执行 (共 3 部分)
            var results = f3_2.get(350, TimeUnit.MILLISECONDS);

            // 确认各个任务的状态, 都已经结束
            then(f0.isDone()).isTrue();
            then(f1_1.isDone()).isTrue();
            then(f1_2.isDone()).isTrue();
            then(f2_1.isDone()).isTrue();
            then(f2_2.isDone()).isTrue();
            then(f3_1.isDone()).isTrue();
            then(f3_2.isDone()).isTrue();

            // 确认任务执行结果
            then(results).hasSize(1000)
                    .containsAnyElementsOf(f0.get());
        }
    }
}
