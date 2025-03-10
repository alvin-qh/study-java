package alvin.study.guava.concurrent;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvin.study.guava.concurrent.model.User;

/**
 * 测试 Guava 中的多任务组件库
 */
class FuturesTest {
    // 线程执行器对象
    private ExecutorService executor;

    /**
     * 在每个测试前执行
     */
    @BeforeEach
    void beforeEach() {
        // 创建线程池执行器对象
        executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 在每个测试后执行
     */
    @AfterEach
    void afterEach() {
        // 清理线程池
        executor.shutdown();
    }

    /**
     * 通过并行计算方式计算斐波那契数列
     *
     * <p>
     * 通过 {@link Futures#addCallback(ListenableFuture, FutureCallback,
     * java.util.concurrent.Executor) Futures.addCallback(ListenableFuture,
     * FutureCallback, Executor)} 方法可以向线程池提交一个任务,
     * 并在任务结束后进行回调, 其中:
     * <ul>
     * <li>
     * 参数 1 是通过
     * {@link com.google.common.util.concurrent.ListeningExecutorService#submit(
     * java.util.concurrent.Callable) ListeningExecutorService.submit(Callable)}
     * 方法 (有返回值) 或
     * {@link com.google.common.util.concurrent.ListeningExecutorService#submit(Runnable)
     * ListeningExecutorService.submit(Runnable)} 方法 (无返回值) 获得, 为一个
     * {@link ListenableFuture} 类型对象
     * </li>
     * <li>
     * 参数 2 是一个 {@link FutureCallback} 接口对象, 用于对执行结果进行处理, 其中
     * {@link FutureCallback#onSuccess(Object)}
     * 表示对执行成功的结果进行处理, {@link FutureCallback#onFailure(Throwable)}
     * 表示对执行失败的异常进行处理
     * </li>
     * <li>
     * 参数 3 是一个 {@link java.util.concurrent.Executor Executor} 类型对象,
     * 表示用于执行 {@link FutureCallback} 接口的线程执行器
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void submit_shouldSubmitTaskOneByOne() {
        // 用于保存计算结果的数组
        var results = new int[10];

        // 通过线程池对象创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 为每个异步计算任务添加回调
        IntStream.range(1, 11).forEach(n -> Futures.addCallback(
            // 为计算创建异步任务, 并为该任务设置回调
            listeningDecorator.submit(() -> Fibonacci.calculate(n)),
            // 回调函数
            new FutureCallback<>() {
                /**
                 * 计算任务处理成功后的回调, 将计算结果作为参数传递
                 */
                @Override
                public void onSuccess(@Nonnull Integer result) {
                    // 保存计算结果
                    results[n - 1] = result;
                }

                /**
                 * 计算任务处理失败后的回调, 将计算过程中产生的异常作为参数传递
                 */
                @Override
                public void onFailure(@Nonnull Throwable t) {
                    throw new IllegalStateException("Cannot calculate fibonacci value", t);
                }
            },
            // 设置回调方法运行的线程执行器
            MoreExecutors.directExecutor()));

        // 等待所有任务执行完毕
        await().atMost(2, TimeUnit.SECONDS).until(() -> Arrays.stream(results).noneMatch(n -> n == 0));

        // 确认结果正确
        then(results).containsExactly(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
    }

    /**
     * 批量提交并行任务
     *
     * <p>
     * 通过 {@link Futures#allAsList(Iterable)} 方法可以将一系列
     * {@link ListenableFuture} 集合转为一个 {@link ListenableFuture}
     * 对象, 该 {@link ListenableFuture} 对象的执行结果为前面所有
     * {@link ListenableFuture} 对象执行结果的集合
     * </p>
     */
    @Test
    void submit_shouldSubmitTaskBatched() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 将要计算的参数转为 ListenableFuture 类型对象集合
        var futures = IntStream.range(1, 11)
                .mapToObj(n -> listeningDecorator.submit(() -> Fibonacci.calculate(n)))
                .toList();

        // 保存计算结果的集合对象
        var numbers = Lists.<Integer>newArrayList();

        // 为异步任务添加回调函数, 在其任务执行完毕后进行回调
        Futures.addCallback(
            // 将 ListenableFuture 集合转为单个 ListenableFuture 对象, 并为该任务添加回调
            Futures.allAsList(futures),
            // 回调函数
            new FutureCallback<>() {
                /**
                 * 计算任务处理成功后的回调, 将批量任务计算结果的集合作为参数传递
                 */
                @Override
                public void onSuccess(@Nonnull List<Integer> result) {
                    // 将计算结果复制到集合对象中
                    numbers.addAll(result);
                }

                /**
                 * 计算任务处理失败后的回调, 将计算过程中产生的异常作为参数传递
                 */
                @Override
                public void onFailure(@Nonnull Throwable t) {
                    throw new UnsupportedOperationException("Unimplemented method 'onFailure'");
                }
            },
            // 设置回调方法运行的线程执行器
            MoreExecutors.directExecutor());

        // 等待所有任务执行完毕
        await().atMost(2, TimeUnit.SECONDS).until(() -> !numbers.isEmpty());

        // 确认结果正确
        then(numbers).containsExactly(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
    }

    /**
     * 通过监听器获取任务结束通知
     *
     * <p>
     * 通过
     * {@link ListenableFuture#addListener(Runnable, java.util.concurrent.Executor)
     * ListenableFuture.addListener(Runnable, Executor)} 方法可以为 {@code Future}
     * 对象增加一个监听器, 当异步任务执行完毕后会回调监听器
     * </p>
     */
    @Test
    void listener_shouldAddListenerCallbackToListeningFuture() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 创建服务对象
        var service = new UserFutureService(listeningDecorator);

        // 产生一个创建用户的异步任务
        var createTask = service.createUser(new User(1L, "Alvin"));

        // 保存创建结果的引用对象
        var userRef = new AtomicReference<User>();

        // 为异步任务添加监听器
        createTask.addListener(
            // 设定异步任务完成后执行的 Callable 对象
            () -> {
                // 确认异步任务已经完成
                then(createTask.isDone()).isTrue();
                try {
                    // 保存异步任务执行结果
                    userRef.set(createTask.get());
                } catch (InterruptedException | ExecutionException ignored) {}
            },
            // 设定异步任务的线程执行器
            MoreExecutors.directExecutor());

        // 等待异步任务存入结果
        await().atMost(210, TimeUnit.MILLISECONDS).until(() -> userRef.get() != null);

        // 确认异步任务返回正确结果
        then(userRef.get()).extracting("id", "name").contains(1L, "Alvin");
    }

    /**
     * 创建链式任务
     *
     * <p>
     * 通过 {@link Futures#whenAllSucceed(ListenableFuture...)}
     * 方法可以为一系列异步任务的后续创建链式任务, 该方法返回一个
     * {@link com.google.common.util.concurrent.Futures.FutureCombiner
     * FutureCombiner} 对象, 当前面那一系列异步任务全部成功后, 会通过
     * {@link com.google.common.util.concurrent.Futures.FutureCombiner#call(
     * java.util.concurrent.Callable, java.util.concurrent.Executor)
     * FutureCombiner.call(Callable, Executor)} 方法或者
     * {@link com.google.common.util.concurrent.Futures.FutureCombiner#callAsync(
     * com.google.common.util.concurrent.AsyncCallable, java.util.concurrent.Executor)
     * FutureCombiner.callAsync(AsyncCallable, Executor)} 方法进行回调
     * </p>
     *
     * <p>
     * {@code call} 方法无法产生链式调用, 只是为之前的一系列异步任务执行成功进行后续处理,
     * 而 {@code callAsync} 方法可以产生链式调用, 其参数 {@code AsyncCallable}
     * 回调函数返回 {@link ListenableFuture} 类型对象,
     * 可以和之前的一系列异步任务合并为一条调用链
     * </p>
     *
     * <p>
     * 后面的例子 {@link #transform_shouldTransformFutureResultToAnother()} 中的
     * {@link Futures#transform(ListenableFuture, com.google.common.base.Function,
     * java.util.concurrent.Executor)
     * Futures.transform(ListenableFuture, Function, Executor)} 以及
     * {@link Futures#transformAsync(ListenableFuture,
     * com.google.common.util.concurrent.AsyncFunction, java.util.concurrent.Executor)
     * Futures.transformAsync(ListenableFuture, AsyncFunction, Executor)}
     * 也能产生类似的链式调用效果
     * </p>
     */
    @Test
    void whenAllSucceed_shouldExecuteNextTaskAfterTasksSuccessful() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 创建服务对象
        var service = new UserFutureService(listeningDecorator);

        // 测试任务的非链式调用: 产生一组创建用户的异步任务, 并在任务成功后, 获取任务结果
        var createdResults = Lists.<User>newArrayList();
        {
            // 产生一个创建用户的异步任务
            var createTask1 = service.createUser(new User(1L, "Alvin"));
            var createTask2 = service.createUser(new User(2L, "Emma"));

            // 当所有创建用户异步任务执行成功后, 进行回调, 返回该回调的 ListenableFuture 任务对象
            var createAllTask = Futures.whenAllSucceed(createTask1, createTask2).call(
                () -> {
                    // 确认前置任务已经完成
                    then(createTask1.isDone()).isTrue();
                    then(createTask2.isDone()).isTrue();

                    // 返回前置任务的执行结果
                    return ImmutableList.of(createTask1.get(), createTask2.get());
                },
                MoreExecutors.directExecutor());

            // 对异步任务添加回调
            Futures.addCallback(
                // 要回调的异步任务
                createAllTask,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull ImmutableList<User> result) {
                        // 将任务执行结果加入集合
                        createdResults.addAll(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {}
                },
                MoreExecutors.directExecutor());
        }

        // 测试任务的异步链式调用: 产生一组创建用户的异步任务, 并在任务成功后, 链接一组查询用户信息的异步任务
        var findResults = Lists.<User>newArrayList();
        {
            // 产生一个创建用户的异步任务
            var createTask1 = service.createUser(new User(1L, "Alvin"));
            var createTask2 = service.createUser(new User(2L, "Emma"));

            // 当所有创建用户异步任务执行成功后, 进行回调, 返回另一组异步任务的 ListenableFuture 任务对象
            var findTasks = Futures.whenAllSucceed(createTask1, createTask2).callAsync(
                () -> {
                    // 确认前置任务已经完成
                    then(createTask1.isDone()).isTrue();
                    then(createTask2.isDone()).isTrue();

                    // 返回由两个查询任务组成的异步任务
                    return Futures.allAsList(
                        service.findUserById(createTask1.get().id()),
                        service.findUserById(createTask2.get().id()));
                },
                MoreExecutors.directExecutor());

            // 对异步任务添加回调
            Futures.addCallback(
                // 要回调的异步任务
                findTasks,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull List<Optional<User>> result) {
                        // 将任务执行结果加入集合
                        result.stream()
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .forEach(findResults::add);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {}
                },
                MoreExecutors.directExecutor());
        }

        // 等待异步任务执行完毕
        await().atMost(3, TimeUnit.SECONDS).until(() -> !createdResults.isEmpty() && !findResults.isEmpty());

        // 确认任务执行正确
        then(createdResults).containsAll(findResults)
                .extracting("id", "name")
                .contains(tuple(1L, "Alvin"), tuple(2L, "Emma"));
    }

    /**
     * 对 {@code Future} 得到的结果进行转化
     *
     * <p>
     * 通过
     * {@link Futures#transform(ListenableFuture, com.google.common.base.Function,
     * java.util.concurrent.Executor)
     * Futures.transform(ListenableFuture, Function, Executor)} 方可对一个
     * {@link ListenableFuture} 的结果类型进行转化, 其中参数 2 指定了转化函数,
     * 参数 3 指定了执行转化函数的线程运行器
     * </p>
     *
     * <p>
     * {@link Futures#transformAsync(ListenableFuture,
     * com.google.common.util.concurrent.AsyncFunction, java.util.concurrent.Executor)
     * Futures.transformAsync(ListenableFuture, AsyncFunction, Executor)} 方法的作用和
     * {@code transform} 的作用基本类似, 只是后者对 {@link ListenableFuture}
     * 对象的执行和转化是同步进行的, 必须一次性完成; 而前者则是将转换也变成是一个异步任务的
     * {@link ListenableFuture} 对象
     * </p>
     *
     * <p>
     * 和传统的转换方法不同, {@code transform} 和 {@code transformAsync} 可以看作是链式调用,
     * 即这两个方法提供了一个异步任务, 其参数是前一个异步任务的结果
     * </p>
     */
    @Test
    void transform_shouldTransformFutureResultToAnother() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 创建服务对象
        var service = new UserFutureService(listeningDecorator);

        // 保存用户创建结果的集合
        var createdUsers = Lists.<User>newArrayList();

        Futures.addCallback(
            // 创建用户, 返回异步任务
            Futures.allAsList(
                service.createUser(new User(1L, "Alvin")),
                service.createUser(new User(2L, "Emma"))),
            // 异步执行完毕后的回调, 传递执行结果
            new FutureCallback<>() {
                @Override
                public void onSuccess(@Nonnull List<User> result) {
                    // 保存用户创建任务结果
                    createdUsers.addAll(result);
                }

                @Override
                public void onFailure(@Nonnull Throwable t) {}
            },
            // 结果回调所在的线程执行器
            MoreExecutors.directExecutor());

        // 等待直到创建用户任务完成
        await().atMost(2, TimeUnit.SECONDS).until(() -> createdUsers.size() == 2);

        // 确认用户创建结果正确
        then(createdUsers)
                .extracting("id", "name")
                .contains(tuple(1L, "Alvin"), tuple(2L, "Emma"));

        // 测试 transform 方法对异步任务结果进行转化
        // 保存异步任务结果的集合
        var foundUsers = Lists.<User>newArrayList();
        {
            // 创建用户查询任务, 并通过 transform 将结果进行转换, 返回整个链式任务的异步任务对象
            var findUsersTask = Futures.transform(
                // 合并两个查询任务为一个异步任务
                Futures.allAsList(service.findUserById(1L), service.findUserById(2L)),
                // 通过一个 Function 对象对合并查询任务结果进行转换, 该函数返回转换结果
                opts -> opts.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList(),
                // 执行转换任务的线程执行器
                MoreExecutors.directExecutor());

            // 添加回调函数, 对链式任务的执行结果进行处理
            Futures.addCallback(
                findUsersTask,
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull List<User> result) {
                        // 保存执行结果
                        foundUsers.addAll(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {}
                },
                // 执行结果回调任务的线程执行器
                MoreExecutors.directExecutor());
        }

        // 测试 transformAsync 方法对异步任务结果进行转化
        // 保存异步任务结果的集合
        var asyncFoundUsers = Lists.<User>newArrayList();
        {
            // 创建用户查询任务, 并通过 transform 将结果进行转换, 返回整个链式任务的异步任务对象
            var findUsersTask = Futures.transformAsync(
                Futures.allAsList(service.findUserById(1L), service.findUserById(2L)),
                // 通过一个 AsyncFunction 对象对合并查询任务结果进行转换,
                // 该函数返回转换的 ListenableFuture 异步任务对象
                input -> listeningDecorator.submit(
                    () -> input.stream()
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList()),
                // 执行转换任务的线程执行器
                MoreExecutors.directExecutor());

            // 添加回调函数, 对链式任务的执行结果进行处理
            Futures.addCallback(
                findUsersTask,
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull List<User> result) {
                        // 保存执行结果
                        asyncFoundUsers.addAll(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {}
                },
                MoreExecutors.directExecutor());
        }

        // 等待直到异步任务完成
        await().atMost(2, TimeUnit.SECONDS).until(
            () -> !foundUsers.isEmpty() && !asyncFoundUsers.isEmpty());

        // 确认两种方法返回的结果一致
        then(foundUsers).containsExactlyElementsOf(asyncFoundUsers)
                .extracting("id", "name")
                .contains(tuple(1L, "Alvin"), tuple(2L, "Emma"));
    }

    /**
     * 对异常处理的链式调用
     *
     * <p>
     * 通过
     * {@link Futures#catching(ListenableFuture, Class, com.google.common.base.Function,
     * java.util.concurrent.Executor)
     * Futures.catching(ListenableFuture, Class, Function, Executor)}
     * 方法可以对一个任务抛出的指定类型异常进行处理
     * </p>
     *
     * <p>
     * 假设有任务 {@code A}, 则针对 {@code A} 的 {@code catching} 方法会产生链式任务
     * {@code B}, 且当任务 {@code A} 确实抛出指定类型异常时, 任务 {@code B} 会执行
     * {@code catching} 方法指定的回调方法处理异常, 并返回任务 {@code A} 的一个备选结果,
     * 否则会在任务 {@code A} 执行完毕后结束. 简言之, 任务 {@code B} 相当于是任务
     * {@code A} + 异常处理两个部分的链式调用
     * </p>
     *
     * <p>
     * {@link Futures#catchingAsync(ListenableFuture, Class,
     * com.google.common.util.concurrent.AsyncFunction, java.util.concurrent.Executor)
     * Futures.catchingAsync(ListenableFuture, Class, AsyncFunction, Executor)} 方法则是
     * {@code catching} 方法的异步版本
     * </p>
     */
    @Test
    void caching_shouldCacheTaskExceptionAndReturnFallbackValue() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 创建服务对象
        var service = new UserFutureService(listeningDecorator);
        // 创建测试实体
        service.createUserSync(new User(1L, "Alvin"));

        // 任务未抛出异常的情况
        // 删除 id 为 1 的实体对象, 该对象存在, 所以正确删除, 无异常抛出
        {
            // 捕获任务的异常, 对异常进行处理
            var cachingTask = Futures.catching(
                // 期待抛出异常的任务 (本例中无异常抛出)
                service.deleteUser(1L),
                // 期望处理的异常类型
                Exception.class,
                // 异常处理回调函数, 对异常进行处理, 并返回原任务结果的备选值
                ex -> {
                    throw new AssertionError("Cannot run here", ex);
                },
                // 执行异常处理器的线程执行器对象
                executor);

            // 保存执行结果的引用对象
            var userRef = new AtomicReference<User>();

            // 为异常捕获任务添加回调函数
            // 由于本次 deleteUser 方法返回的任务不会抛出异常, 所以 cachingTask 任务相当于 deleteUser 任务
            Futures.addCallback(
                // 要添加回调的任务对象
                cachingTask,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {
                        // 保存任务结果
                        userRef.set(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {
                        fail();
                    }
                },
                // 执行回调函数的线程执行器
                MoreExecutors.directExecutor());

            // 等待任务结果返回
            await().atMost(300, TimeUnit.MILLISECONDS).until(() -> userRef.get() != null);

            // 确认任务结果正确
            then(userRef.get()).extracting("id", "name").contains(1L, "Alvin");
        }

        // 任务抛出异常的情况
        // 对异常进行处理, 返回原任务的备选结果
        {
            // 保存异常的引用对象
            var exceptionRef = new AtomicReference<Throwable>();

            // 捕获任务的异常, 对异常进行处理
            var cachingTask = Futures.catching(
                // 期待抛出异常的任务 (本例中抛出 NoSuchElementException 异常)
                service.deleteUser(2L),
                // 期望处理的异常类型
                Exception.class,
                // 异常处理回调函数, 对异常进行处理, 并返回原任务结果的备选值
                ex -> {
                    // 保存异常对象
                    exceptionRef.set(ex);
                    // 返回钱一个任务结果的备选值
                    return new User(0L, "Nobody");
                },
                // 执行异常处理器的线程执行器对象
                MoreExecutors.directExecutor());

            // 保存执行结果的引用对象
            var userRef = new AtomicReference<User>();

            // 为异常捕获任务添加回调函数
            // 由于本次 deleteUser 方法会抛出异常,
            // 所以 cachingTask 任务相当于执行 deleteUser 任务后执行异常处理回调
            Futures.addCallback(
                // 要添加回调的任务对象
                cachingTask,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {
                        userRef.set(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {
                        fail();
                    }
                },
                // 执行回调函数的线程执行器
                MoreExecutors.directExecutor());

            // 等待任务结果返回
            await().atMost(300, TimeUnit.MILLISECONDS).until(() -> userRef.get() != null);

            // 确认抛出的异常正确
            then(exceptionRef.get()).isInstanceOf(NoSuchElementException.class);

            // 确认任务结果正确, 为异常处理回调返回的备选值
            then(userRef.get()).extracting("id", "name").contains(0L, "Nobody");
        }

        // 任务抛出异常的情况
        // 对异常进行处理, 抛出新的异常
        {
            // 捕获任务的异常, 对异常进行处理
            var cachingTask = Futures.catching(
                // 要添加回调的任务对象
                service.deleteUser(2L),
                // 期望处理的异常类型
                Exception.class,
                // 异常处理回调函数, 对异常进行处理, 并返回原任务结果的备选值
                ex -> {
                    throw new IllegalArgumentException(ex);
                },
                // 执行异常处理器的线程执行器对象
                MoreExecutors.directExecutor());

            // 保存抛出异常的引用对象
            var exceptionRef = new AtomicReference<Throwable>();

            // 为异常捕获任务添加回调函数
            // 由于本次 cachingTask 任务抛出了新的异常, 所以 onFailure 方法会被回调
            Futures.addCallback(
                // 要添加回调的任务对象
                cachingTask,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {
                        fail();
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {
                        exceptionRef.set(t);
                    }
                },
                // 执行回调函数的线程执行器
                MoreExecutors.directExecutor());

            // 等待任务结果返回
            await().atMost(300, TimeUnit.MILLISECONDS).until(() -> exceptionRef.get() != null);

            // 确认抛出的异常正确
            then(exceptionRef.get()).isInstanceOf(IllegalArgumentException.class);
        }

        // 任务抛出异常的情况
        // 本例中对异常进行异步处理
        {
            // 保存异常的引用对象
            var exceptionRef = new AtomicReference<Throwable>();

            // 捕获任务的异常, 对异常进行处理
            var cachingTask = Futures.catchingAsync(
                // 要添加回调的任务对象
                service.deleteUser(2L),
                // 期望处理的异常类型
                Exception.class,
                // 异常处理回调函数, 对异常进行处理, 并返回产生原任务结果备选值的异步任务
                ex -> {
                    exceptionRef.set(ex);
                    return Futures.immediateFuture(new User(0L, "Nobody"));
                },
                // 执行异常处理器的线程执行器对象
                MoreExecutors.directExecutor());

            // 保存执行结果的引用对象
            var userRef = new AtomicReference<User>();

            // 为异常捕获任务添加回调函数
            // 由于本次 deleteUser 方法会抛出异常,
            // 所以 cachingTask 任务相当于执行 deleteUser 任务后执行异常处理回调
            Futures.addCallback(
                // 要添加回调的任务对象
                cachingTask,
                // 回调函数
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {
                        userRef.set(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {
                        fail();
                    }
                },
                // 执行回调函数的线程执行器
                MoreExecutors.directExecutor());

            // 等待任务结果返回
            await().atMost(300, TimeUnit.MILLISECONDS).until(() -> userRef.get() != null);

            // 确认抛出的异常正确
            then(exceptionRef.get()).isInstanceOf(NoSuchElementException.class);

            // 确认任务结果正确, 为异常处理回调返回的备选值
            then(userRef.get()).extracting("id", "name").contains(0L, "Nobody");
        }
    }

    /**
     * 在任务超时后, 终止任务执行
     *
     * <p>
     * 通过
     * {@link Futures#withTimeout(ListenableFuture, long, TimeUnit,
     * java.util.concurrent.ScheduledExecutorService)
     * Futures.withTimeout(ListenableFuture, long, TimeUnit, ScheduledExecutorService)}
     * 方法可以为异步任务设置超时时间, 在超时时间内未完成的任务将被终止
     * </p>
     *
     * <p>
     * {@code withTimeout} 方法需要通过 {@link ScheduledThreadPoolExecutor}
     * 执行器进行, 并通过其设置一个定时任务来监控异步任务的执行情况
     * </p>
     */
    @Test
    void withTimeout_shouldTerminateTaskWhenTimeout() {
        // 创建 ScheduledExecutorService 对象
        var scheduledExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(scheduledExecutor);

        // 创建服务对象
        var service = new UserFutureService(listeningDecorator);

        // 测试任务超时终止的情况
        {
            // 为 createUser 任务设置超时时间
            // createUser 任务执行需要 100ms 以上, 这里的设置会导致该任务执行超时
            var timeoutTask = Futures.withTimeout(
                // 要监控超时的任务对象
                service.createUser(new User(1L, "Alvin")),
                // 任务超时时间设置
                50, TimeUnit.MILLISECONDS,
                // 执行超时监控的执行器
                scheduledExecutor);

            // 保存任务执行中异常的引用对象
            var exceptionRef = new AtomicReference<Throwable>();

            // 为任务添加回调
            // 由于该任务一定会超时, 所以不会回调 onSuccess,
            // 会在 onFailure 回调中传递 TimeoutException 异常
            Futures.addCallback(
                timeoutTask,
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {}

                    @Override
                    public void onFailure(@Nonnull Throwable t) {
                        exceptionRef.set(t);
                    }
                },
                MoreExecutors.directExecutor());

            // 等待任务执行完毕
            await().atMost(200, TimeUnit.MILLISECONDS)
                    .until(() -> exceptionRef.get() != null);

            // 确认任务执行中抛出 TimeoutException 异常
            then(exceptionRef.get()).isInstanceOf(TimeoutException.class);
        }

        // 测试任务未超时执行完毕的情况
        {
            // 为 createUser 任务设置超时时间
            // createUser 任务执行需要 100ms 以上, 这里的设置不会导致超时
            var timeoutTask = Futures.withTimeout(
                service.createUser(new User(1L, "Alvin")),
                150, TimeUnit.MILLISECONDS,
                scheduledExecutor);

            // 保存任务执行结果的引用对象
            var userRef = new AtomicReference<User>();

            // 为任务添加回调
            // 由于该任务不会超时, 所以会回调 onSuccess, 传递任务执行结果
            Futures.addCallback(
                timeoutTask,
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nonnull User result) {
                        // 保存任务执行结果
                        userRef.set(result);
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable t) {}
                },
                MoreExecutors.directExecutor());

            // 等待任务执行完毕
            await().atMost(500, TimeUnit.MILLISECONDS)
                    .until(() -> userRef.get() != null);

            // 确认获得了正确的任务结果
            then(userRef.get()).extracting("id", "name")
                    .contains(1L, "Alvin");
        }

        // 关闭线程执行器
        scheduledExecutor.shutdown();
    }

    /**
     * 处理抛出 Checked 异常的异步任务
     *
     * <p>
     * 通过
     * {@link Futures#getChecked(java.util.concurrent.Future, Class, long, TimeUnit)
     * Futures.getChecked(Future, Class, long, TimeUnit)} 方法可以对一个异步任务进行检查,
     * 如果其执行完毕, 则返回任务结果, 如果其产生了异常, 则抛出该异常
     * </p>
     *
     * <p>
     * 不带时间参数的 {@link Futures#getChecked(java.util.concurrent.Future, Class)
     * Futures.getChecked(Future, Class)} 方法会阻塞线程, 直到异步任务返回结果或抛出异常
     * </p>
     *
     * <p>
     * 如果异步任务不会产生异常, 或者产生的异常未 {@link RuntimeException}, 则可以通过
     * {@link Futures#getUnchecked(java.util.concurrent.Future)
     * Futures.getUnchecked(Future)} 方法, 该方法也是检查一个异步任务, 若其执行完毕,
     * 则返回任务结果, 如果抛出 Unchecked 异常, 则抛出该异常
     * </p>
     *
     * <p>
     * {@code getChecked} 和 {@code getUnchecked} 方法是幂等的,
     * 即对于同一个异步任务对象多次执行该方法, 其行为一致
     * </p>
     */
    @Test
    void getChecked_shouldThrowCheckedException() {
        // 创建 ListeningExecutorService 对象
        var listeningDecorator = MoreExecutors.listeningDecorator(executor);

        // 测试任务未抛出异常时, 返回任务结果
        {
            // 定义一个结果为字符串值的任务
            var future = listeningDecorator.submit(() -> "Success");
            try {
                // 检查任务, 如果任务已经执行完毕, 则获取任务结果
                var result = Futures.getChecked(future, IOException.class, 2, TimeUnit.SECONDS);
                // 确认任务结果值正确
                then(result).isEqualTo("Success");

                // 重复检查任务, 获取任务结果
                result = Futures.getChecked(future, IOException.class);
                // 确认任务结果值正确
                then(result).isEqualTo("Success");
            } catch (IOException e) {
                fail();
            }
        }

        // 测试任务出现异常时, 将该异常进行抛出
        {
            // 定义一个抛出异常的任务
            var future = listeningDecorator.submit(() -> {
                // 抛出 Checked 异常
                throw new IOException();
            });
            // 检查任务, 如果任务执行完毕且产生了异常, 则抛出该异常
            thenThrownBy(() -> Futures.getChecked(future, IOException.class, 2, TimeUnit.SECONDS))
                    .isInstanceOf(IOException.class);

            // 重复检查任务, 重复抛出异常
            thenThrownBy(() -> Futures.getChecked(future, IOException.class))
                    .isInstanceOf(IOException.class);
        }
    }
}
