package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;
import alvin.study.se.concurrent.util.Threads;

/**
 * 测试 Java 线程
 */
public class ThreadTest {
    /**
     * 测试线程启动和停止
     *
     * <p>
     * 可通过实例化 {@link Thread} 类对象创建一个线程对象, 可通过该对象的构造函数参数可传入一个
     * {@link Runnable} 接口对象, 即线程执行入口
     * </p>
     *
     * <p>
     * 可通过 {@link Thread#start()} 方法启动线程, 线程启动成功后,
     * 会调用之前构造线程对象时传入的回调函数
     * </p>
     */
    @Test
    @SneakyThrows
    void start_shouldStartThread() {
        // 用于存放上下文数据的 `Map` 对象
        var context = new HashMap<String, Object>();

        // 实例化线程对象, 为线程对象设置回调函数表示线程执行入口
        var thread = new Thread(() -> {
            context.put("threadId", Thread.currentThread().threadId());
        });

        // 启动线程, 此时线程对象中关联的回调函数会被执行
        thread.start();

        // 等待线程结束
        thread.join();

        // 确认线程入口函数中的确实被执行
        then(context)
                .containsKeys("threadId")
                .extracting("threadId")
                .matches(id -> id instanceof Long && ((long) id) > 0);
    }

    /**
     * 测试线程中断
     *
     * <p>
     * 通过 {@link Thread#interrupt()} 方法可以打断对应线程, 线程被打断后, 会抛出
     * {@link InterruptedException} 异常, 通过在线程内捕获该异常即可结束线程
     * </p>
     *
     * <p>
     * 被打断的线程必须具备一个等待调用, 例如调用了 {@link Thread#sleep(long)} 方法,
     * 这类方法均具备可抛出 {@link InterruptedException} 异常的声明,
     * 如果线程中未调用此类函数, 则该线程不会被打断
     * </p>
     */
    @Test
    @SneakyThrows
    void interceptor_shouldInterceptThread() {
        // 记录线程循环次数的集合
        var results = new ArrayList<Integer>();

        // 启动线程
        var thread = new Thread(() -> {
            try {
                for (var i = 0; i < 10; i++) {
                    results.add(i);
                    // 令线程休眠 100ms, 线程可以在休眠 (或等待) 的语句上被打断
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignore) {
                // 线程被打断时, 会抛出 `InterruptedException` 异常
                results.add(-1);
            }
        });

        // 启动线程
        thread.start();

        // 等待 500ms
        Thread.sleep(500);

        // 打断线程执行, 此时线程代码中任意等待语句都有可能抛出 `InterruptedException` 异常
        thread.interrupt();

        // 等待线程结束
        thread.join();

        // 确认线程在正确位置被打断
        then(results).containsExactly(0, 1, 2, 3, 4, -1);
    }

    /**
     * 通过 {@link FutureTask} 类型执行一个线程, 并返回执行结果
     *
     * <p>
     * 普通线程执行的 {@link Runnable} 接口返回值为 `void`,
     * 即意味着无法从线程中返回值, 而 {@link FutureTask}
     * 类型则可以获取线程的返回值
     * </p>
     *
     * <p>
     * {@link FutureTask} 类实现了 {@link Runnable} 接口,
     * 所以其对象可以作为参数传递给线程对象, 在线程执行完该接口方法后, 会令其
     * {@link FutureTask#isDone()} 方法返回 {@code true}, 且可以通过
     * {@link FutureTask#get()} 方法返回执行结果
     * </p>
     */
    @Test
    @SneakyThrows
    void futureTask_shouldGetResultOfThreadByFutureTask() {
        // 定义 FutureTask 对象, 设置线程回调函数
        var task = new FutureTask<>(() -> Fibonacci.calculate(20));

        // 实例化线程对象并启动
        var thread = new Thread(task);
        thread.start();

        // 等待异步任务结束
        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);

        // 确认异步任务结束后, 线程也已经结束
        then(thread.isAlive()).isFalse();

        // 确认计算结果
        then(task.get()).isEqualTo(6765);
    }

    /**
     * 测试线程异常处理
     *
     * <p>
     * 可以为线程设置一个处理器, 用于对处理线程中未捕获的异常对象进行统一处理
     * </p>
     *
     * <p>
     * 当线程中抛出异常但未捕获时, 会调用
     * {@link Thread.UncaughtExceptionHandler#uncaughtException(Thread, Throwable)}
     * 方法, 调用线程的异常处理器对该异常进行处理
     * </p>
     */
    @Test
    @SneakyThrows
    void exceptionHandler_shouldHandleThreadUncaughtException() {
        // 定义类, 用于记录线程中抛出的异常对象
        record ExceptionResult(Thread thread, Throwable throwable) {}

        // 定义引用对象, 用于存储线程执行结果
        var resultRef = new AtomicReference<ExceptionResult>();

        // 创建线程对象, 在线程中会抛出异常
        var thread = new Thread(() -> {
            throw new RuntimeException("thread test exception");
        });

        // 设置线程异常处理器, 捕获线程中抛出的未处理异常,
        // 由于线程对象本身会作为参数传入处理器, 故可以在多个线程中共享同一个异常处理器,
        // 并通过传入的线程对象来明确抛出的异常属于哪个线程
        thread.setUncaughtExceptionHandler((t, e) -> {
            resultRef.set(new ExceptionResult(t, e));
        });

        // 启动线程
        thread.start();

        // 等待线程执行完毕
        thread.join();

        // 确认线程抛出的异常结果不为空
        then(resultRef.get()).isNotNull();

        // 确认线程抛出的异常结果
        var result = resultRef.get();
        then(result.thread()).isSameAs(thread);
        then(result.throwable().getMessage()).isEqualTo("thread test exception");
    }

    /**
     * 测试 {@link Thread#sleep(long)} 方法, 使当前线程休眠指定时间
     *
     * <p>
     * 休眠线程, 使当前线程休眠指定时间, 休眠结束后继续执行后续代码
     * </p>
     *
     * <p>
     * 线程休眠, 表示当前线程放弃自己的执行时间窗口, 等待指定时间 (`ms`)
     * 后被唤醒
     * </p>
     *
     * <p>
     * 所谓 "休眠时间", 是一个大概的数字, 线程会在指定的休眠时间之后唤醒,
     * 但不会精确到和指定的休眠时间一致
     * </p>
     *
     * <p>
     * 故可以出现的休眠时间为 `0` 的情况, 这并不代表线程不休眠,
     * 线程依然会放弃自身的执行时间窗口, 等待下次被唤醒, 只是唤醒过程非常短暂
     * </p>
     */
    @Test
    @SneakyThrows
    void sleep_shouldSleepThread() {
        // 创建一个线程对象
        var thread = new Thread(() -> {
            try {
                // 休眠 200ms 秒
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}
        });

        // 确认此时线程未启动
        then(thread.isAlive()).isFalse();

        // 记录线程启动时间
        var startTime = System.currentTimeMillis();

        // 启动线程
        thread.start();

        // 确认线程此时已启动
        then(thread.isAlive()).isTrue();

        // 等待线程执行完成
        thread.join();

        // 确认线程执行完成时间在 200ms 左右
        then(System.currentTimeMillis() - startTime).isBetween(200L, 220L);
    }

    /**
     * 测试 {@link Thread#onSpinWait()} 方法, 优化空循环 (自旋等待，忙等待)
     *
     * <p>
     * 所谓空循环, 指的是在循环中对某个条件进行持续检测, 在条件不满足前,
     * 循环不会执行任何代码
     * </p>
     *
     * <p>
     * 空循环会导致大量的资源消耗, 影响性能, 并且会消耗 CPU 资源,
     * 使得使用同一 CPU 资源的其它线程得不到执行的时间窗口,
     * 故一般会在空循环中加入 {@code Thread.sleep(0)} 强迫线程进行切换,
     * 故 {@code Thread.sleep(0)} 适合长时间空循环等待,
     * 或提升空循环等待过程中同一 CPU 上所有线程的公平性
     * </p>
     *
     * <p>
     * {@link Thread#onSpinWait()} 方法, 可以看作是
     * {@code Thread.sleep(0)} 的等效方法, 但后者会根据平台进行优化,
     * 避免发生线程切换, 适合于短时间空循环等待
     * </p>
     */
    @Test
    @SneakyThrows
    void onSpinWait_shouldSleepThread() {
        // 定义原子变量, 用于标记线程是否继续执行
        var stop = new AtomicBoolean(false);

        // 创建一个线程对象
        var thread = new Thread(() -> {
            // 等待 `stop` 变量的值变为 `true`
            while (!stop.get()) {
                //
                Thread.onSpinWait();
            }
        });

        // 启动线程
        thread.start();

        // 启动定时器, 在 200ms 后, 将 `stop` 变量设置为 `true`
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                stop.set(true);
            }
        }, 500);

        // 等待线程执行完成
        thread.join();
        then(thread.isAlive()).isFalse();
    }

    /**
     * 测试 {@link Thread#yield()} 方法
     *
     * <p>
     * {@link Thread#yield()} 方法用于提示线程调度器当前线程愿意让出 CPU 资源，
     * 但实际行为取决于 JVM 实现，该方法主要用于调试/测试场景
     * </p>
     *
     * <p>
     * 注意：此测试中的非确定性现象（完成顺序）仅用于演示 API 用法，实际生产代码中应避免依赖
     * {@link Thread#yield()} 的特定行为
     * </p>
     */
    @Test
    @SneakyThrows
    void yield_shouldHintThreadScheduler() {
        // 记录两个线程执行次数的原子变量
        var counterA = new AtomicInteger(0);
        var counterB = new AtomicInteger(0);

        var tsA = new AtomicLong(0);
        var tsB = new AtomicLong(0);

        // 创建两个竞争线程
        // 两个线程各自进行 `1000000` 次循环, 但后一个线程通过
        // `Thread.yield()` 方法, 提示调度器让出当前线程执行权

        // 创建第一个线程
        var threadA = new Thread(() -> {
            var start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                counterA.incrementAndGet();
            }

            // 记录执行时间
            tsA.set(System.currentTimeMillis() - start);
        }, "thread-A");

        // 创建第二个线程
        var threadB = new Thread(() -> {
            var start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                // 提示调度器让出当前线程执行权
                Thread.yield();
                counterB.incrementAndGet();
            }

            // 记录执行时间
            tsB.set(System.currentTimeMillis() - start);
        }, "thread-B");

        // 启动线程
        threadA.start();
        threadB.start();

        // 等待线程结束
        threadA.join();
        threadB.join();

        // 验证两个线程都完成了任务
        then(counterA.get()).isEqualTo(1000000);
        then(counterB.get()).isEqualTo(1000000);

        // 由于 `yield()` 的提示作用，带 `yield` 的线程可能完成得更慢
        // (实际结果取决于 JVM 实现)
        then(tsA.get()).isLessThan(tsB.get());
    }

    /**
     * 测试获取所有线程的堆栈跟踪信息
     *
     * <p>
     * 在调试过程中, 往往需要获取当前正在执行线程的堆栈跟踪信息, 以便分析线程状态,
     * 方便进行调试 (例如分析线程死锁), 通过 {@link Thread#getAllStackTraces()}
     * 方法即可获取所有线程的堆栈跟踪信息
     * </p>
     *
     * <p>
     * {@link Thread#getAllStackTraces()} 方法返回的线程不仅包括代码中启动的线程,
     * 也包括 JDK 自身启动的线程
     * </p>
     */
    @Test
    @SneakyThrows
    void join_shouldWaitForThread() {
        // 定义一个锁对象和条件对象
        var lock = new ReentrantLock();
        var cond = lock.newCondition();

        // 定义 10 个线程
        var threads = new Thread[10];

        // 启动 10 个线程, 在每个线程中等待条件变量, 直到被唤醒
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // 进入锁
                lock.lock();
                try {
                    // 等待条件
                    cond.await();
                } catch (InterruptedException ignore) {
                    // do nothing
                } finally {
                    // 解除锁
                    lock.unlock();
                }
            });

            // 启动每个线程
            threads[i].start();
        }

        // 获取所有线程的堆栈跟踪信息
        var traces = Thread.getAllStackTraces();
        then(traces).isNotEmpty();

        // 确认获取的线程追踪信息中包含之前启动的 10 个线程
        then(Arrays.stream(threads).allMatch(t -> traces.keySet().contains(t))).isTrue();

        // 向所有线程发出信号, 唤醒所有线程
        lock.lock();
        try {
            cond.signalAll();
        } finally {
            lock.unlock();
        }

        // 等待所有线程执行完成
        Threads.joinAll(threads, 1000);
        // 再次获取线程的堆栈跟踪信息, 不再包括已结束的线程
        then(Thread.getAllStackTraces().size()).isEqualTo(traces.size() - threads.length);
    }

    /**
     * 测试启动虚拟线程
     *
     * <p>
     * JDK 21 之后的版本支持创建 "虚拟线程", "虚拟线程" 是一个 "轻量级线程",
     * 它不占用系统资源, 所以可以大量创建
     * </p>
     * <p>
     * "虚拟线程" 相当于其它语言中的 "协程", 通过编译器进行调度,
     * 所以无需进行传统线程的上下文切换, 减少上下文切换的开销
     * </p>
     * <p>
     * "虚拟线程" 并不是真正的 "线程", 它不会真正的占用 CPU 单元,
     * 而是通过代码执行切换来达到类似 "并发" 的能力, 所以 "虚拟线程"
     * 并不适合计算密集型任务的 "并发" 执行, 而更适合 "IO 密集型"
     * 任务, 即当一个 "虚拟线程" 执行 IO 操作并进入等待时,
     * 可以让出 CPU 资源, 让其它 "虚拟线程" 执行其它 IO 操作
     * </p>
     * <p>
     * "虚拟线程" 仍是通过 {@link Thread} 类的对象实例来表示, 通过
     * {@link Thread#isVirtual()} 方法来判断对象是否为一个 "虚拟线程",
     * Java 将 "虚拟线程" 的接口以及对应的同步工具类设计的与 "平台线程"
     * (即 Java 传统线程) 基本类似, 无需掌握一套新的线程 API
     * </p>
     * <p>
     * 另外, 无法通过创建 "虚拟线程" 来降低系统的延迟, 因为 "虚拟线程"
     * 并不是真正的 "并行" 执行, 如果之前系统的延时较高, 通过 "虚拟线程"
     * 执行后, 延时并不会降低, 仍需要使用传统的 "平台线程" 解决延时问题
     * </p>
     * <p>
     * 无法直接通过 {@code new} 运算符来实例化 "虚拟线程" 实例,
     * JDK 中提供了两种方法创建 "虚拟线程":
     *
     * <ul>
     * <li>
     * 通过 {@link Thread#startVirtualThread(Runnable)} 方法直接启动
     * "虚拟线程", 该方法返回一个表示 "虚拟线程" 的 {@link Thread} 对象
     * </li>
     * <li>
     * 通过 {@link Thread#ofVirtual()} 方法创建 "虚拟线程" 的构建器对象,
     * 即 {@link Thread.Builder} 接口对象, 通过该对象可以设置 "虚拟线程" 的属性,
     * 并创建或启动 "虚拟线程"
     * </li>
     * </ul>
     * </p>
     */
    @Test
    @SneakyThrows
    void virtual_shouldStartVirtualThread() {
        // 创建一个 HTTP 客户端对象
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        // 用于记录 HTTP 响应对象的原子引用对象
        var responseRef = new AtomicReference<HttpResponse<String>>();

        // 启动虚拟线程, 在其中发起 HTTP 请求, 并记录响应对象
        var thread = Thread.startVirtualThread(() -> {
            // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
            var request = HttpRequest.newBuilder().GET()
                    .uri(URI.create("https://www.baidu.com"))
                    .timeout(Duration.ofMillis(3000))
                    .build();

            try {
                // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                responseRef.set(
                    client.send(request, HttpResponse.BodyHandlers.ofString()));
            } catch (IOException | InterruptedException ignore) {
                responseRef.set(null);
            }
        });

        // 确认所创建的线程为虚拟线程对象
        then(thread.isVirtual()).isTrue();

        // 等待线程执行完毕
        thread.join();

        // 确认虚拟线程执行完毕 HTTP 请求, 并存储了返回的响应对象
        then(responseRef.get()).isNotNull();

        // 获取 HTTP 响应对象
        var response = responseRef.get();

        // 确认 HTTP 响应内容
        then(response.statusCode()).isEqualTo(200);
        then(response.headers().firstValue("Content-Type")).isPresent().get().isEqualTo("text/html");
        then(response.body()).contains("<!DOCTYPE html>");
    }

    /**
     * 测试通过 {@link FutureTask} 创建 "虚拟线程"
     *
     * <p>
     * 由于 "虚拟线程" 和 "平台线程" 具备基本一致的接口 API, 所以仍可利用
     * {@link FutureTask} 接口取代 {@link Runnable} 接口作为 "虚拟线程"
     * 的执行单元
     * </p>
     * <p>
     * {@link FutureTask} 接口对象自身可以获取线程的执行状态, 并获取执行结果
     * </p>
     */
    @Test
    @SneakyThrows
    void virtual_shouldStartVirtualThreadByFutureTask() {
        // 创建一个 HTTP 客户端对象
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        // 创建一个 FutureTask 对象, 作为异步任务执行
        // 在该任务中会发起一个 HTTP 请求, 并将返回的响应对象作为任务的返回值
        var task = new FutureTask<Optional<HttpResponse<String>>>(() -> {
            // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
            var request = HttpRequest.newBuilder().GET()
                    .uri(URI.create("https://www.baidu.com"))
                    .timeout(Duration.ofMillis(3000))
                    .build();

            try {
                // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                return Optional.of(
                    client.send(request, HttpResponse.BodyHandlers.ofString()));
            } catch (IOException | InterruptedException ignore) {
                return Optional.empty();
            }
        });

        // 通过 `FutureTask` 对象启动 "虚拟线程"
        var thread = Thread.startVirtualThread(task);

        // 等待 "虚拟线程" 执行完毕
        thread.join();

        // 确认 "虚拟线程" 执行完毕后, `FutureTask` 对象的状态为 `DONE`,
        // 即异步任务也执行完毕
        then(task.isDone()).isTrue();

        // 获取异步任务的执行结果
        var mayResponse = task.get(1, TimeUnit.SECONDS);
        then(mayResponse).isPresent();

        // 确认返回的响应对象内容正确
        var response = mayResponse.get();
        then(response.statusCode()).isEqualTo(200);
        then(response.headers().firstValue("Content-Type")).isPresent().get().isEqualTo("text/html");
        then(response.body()).contains("<!DOCTYPE html>");
    }

    /**
     * 测试 {@link Thread#ofVirtual()} 方法, 创建虚拟线程并启动
     *
     * <p>
     * 通过 {@link Thread#ofVirtual()} 方法可以产生一个 {@link Thread.Builder.OfVirtual
     * OfVirtual} 对象, 即一个虚拟线程构建器对象, 通过该对象可以设置虚拟线程的属性, 包括:
     * <ul>
     * <li>
     * 虚拟线程名称: {@link Thread.Builder.OfVirtual#name(String)
     * OfVirtual.name(String)}
     * </li>
     * <li>
     * 虚拟线程异常处理:
     * {@link Thread.Builder.OfVirtual#uncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
     * OfVirtual.uncaughtExceptionHandler(UncaughtExceptionHandler)}
     * </li>
     * <li>
     * 是否继承父线程的线程上下文内容: {@link Thread.Builder.OfVirtual#inheritInheritableThreadLocals(boolean)
     * OfVirtual.inheritInheritableThreadLocals(boolean)}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link Thread.Builder} 对象创建后, 可以通过 {@link Thread.Builder.OfVirtual#start() OfVirtual.start()}
     * 方法, 通过 {@link Runnable} 接口对象, 基于构建的线程属性创建线程并启动
     * </p>
     */
    @Test
    @SneakyThrows
    void ofVirtual_shouldBuildVirtualThreadByThreadBuilder() {
        // 创建一个 HTTP 客户端对象
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        // 用于记录 HTTP 响应对象的原子引用对象
        var responseRef = new AtomicReference<HttpResponse<String>>();

        var thread = Thread.ofVirtual()
                .name("virtual-thread")
                .uncaughtExceptionHandler((t, err) -> {
                    // log.exception(t);
                })
                .inheritInheritableThreadLocals(false)
                .start(() -> {
                    // 创建 HTTP 请求对象, 通过 `GET` 方法发起请求
                    var request = HttpRequest.newBuilder().GET()
                            .uri(URI.create("https://www.baidu.com"))
                            .timeout(Duration.ofMillis(3000))
                            .build();

                    try {
                        // 发起 HTTP 请求, 并获取响应对象, 保存到 `responseRef` 对象中
                        responseRef.set(
                            client.send(request, HttpResponse.BodyHandlers.ofString()));
                    } catch (IOException | InterruptedException ignore) {}
                });

        // 等待线程结束
        thread.join();

        // 确认虚拟线程执行完毕 HTTP 请求, 并存储了返回的响应对象
        then(responseRef.get()).isNotNull();

        // 获取 HTTP 响应对象
        var response = responseRef.get();

        // 确认 HTTP 响应内容
        then(response.statusCode()).isEqualTo(200);
        then(response.headers().firstValue("Content-Type")).isPresent().get().isEqualTo("text/html");
        then(response.body()).contains("<!DOCTYPE html>");
    }

    /**
     * 测试通过 {@link Thread.Builder Builder} 接口创建 "平台线程" 并启动
     *
     * <p>
     * JDK 也为传统的平台线程提供了 {@link Thread#ofPlatform()} 方法,
     * 通过该方法可以产生一个 {@link Thread.Builder.OfPlatform}
     * 对象, 即一个平台线程构建器对象, 通过该对象可以设置平台线程的属性, 包括:
     * <ul>
     * <li>
     * 线程名称: {@link Thread.Builder.OfPlatform#name(String)
     * OfPlatform.name(String)}
     * </li>
     * <li>
     * 线程优先级: {@link Thread.Builder.OfPlatform#priority(int)
     * OfPlatform.priority(int)}
     * </li>
     * <li>
     * 线程栈大小: {@link Thread.Builder.OfPlatform#stackSize(long)
     * OfPlatform.stackSize(long)}
     * </li>
     * <li>
     * 线程组: {@link Thread.Builder.OfPlatform#group(ThreadGroup)
     * OfPlatform.group(ThreadGroup)}
     * </li>
     * <li>
     * 是否继承父线程的线程上下文内容: {@link Thread.Builder.OfPlatform#inheritInheritableThreadLocals(boolean)
     * OfVirtual.inheritInheritableThreadLocals(boolean)}
     * </li>
     * <li>
     * 线程异常处理器: {@link Thread.Builder.OfPlatform#uncaughtExceptionHandler(Thread.UncaughtExceptionHandler)
     * OfPlatform.uncaughtExceptionHandler(UncaughtExceptionHandler)}
     * </li>
     * <li>
     * 是否精灵线程: {@link Thread.Builder.OfPlatform#daemon(boolean)
     * OfPlatform.daemon(boolean)}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link Thread.Builder} 对象创建后, 可以通过
     * {@link Thread.Builder.OfPlatform#start() OfPlatform.start()} 方法,
     * 通过 {@link Runnable} 接口对象, 基于构建的线程属性创建线程并启动
     * </p>
     */
    @Test
    @SneakyThrows
    void ofPlatform_shouldBuildPlatformThreadByThreadBuilder() {
        // 定义类, 用于记录线程执行结果
        record ThreadException(Thread thread, Throwable throwable) {}

        // 定义引用对象, 用于记录线程执行结果
        var ref = new AtomicReference<ThreadException>();

        // 通过构建器对象创建线程对象
        var thread = Thread.ofPlatform()
                .name("test-thread") // 设置线程名称
                .uncaughtExceptionHandler((t, e) -> { // 设置线程异常处理程序
                    // 将线程中抛出的线程进行捕获, 并作为线程执行结果进程存储
                    ref.set(new ThreadException(t, e));
                })
                .daemon(false) // 设置线程是否为守护线程
                .inheritInheritableThreadLocals(false) // 设置线程是否继承父线程的 ThreadLocal 内容
                .priority(Thread.NORM_PRIORITY) // 设置线程优先级
                .start(() -> { // 启动线程
                    try {
                        // 等待信号通知
                        synchronized (ref) {
                            ref.wait();
                        }

                        // 抛出异常
                        throw new RuntimeException("thread cause exception");
                    } catch (InterruptedException ignore) {}
                });

        // 确认线程属性
        then(thread.getName()).isEqualTo("test-thread");
        then(thread.isDaemon()).isFalse();
        then(thread.getPriority()).isEqualTo(Thread.NORM_PRIORITY);
        then(thread.isVirtual()).isFalse();

        // 确认线程状态为运行中
        then(thread.isAlive()).isTrue();

        // 发送通知, 令线程继续执行
        synchronized (ref) {
            ref.notify();
        }

        // 等待线程执行完成, 并确认线程已经执行完成
        thread.join();
        then(thread.isAlive()).isFalse();

        // 获取线程执行结果, 并确认线程对象以及其抛出的异常
        var threadException = ref.get();
        then(threadException.thread()).isSameAs(thread);
        then(threadException.throwable().getMessage()).isEqualTo("thread cause exception");
    }

    /**
     * 测试通过 {@link Thread.Builder Builder} 接口创建一个未启动的线程
     *
     * <p>
     * 当调用 {@link Thread.Builder} 对象创建并设置各类属性后, 可以调用
     * {@link Thread.Builder#unstarted(Runnable)} 方法创建一个线程对象,
     * 但并不启动线程, 之后在合适时机通过 {@link Thread#start()} 方法启动线程
     * </p>
     */
    @Test
    @SneakyThrows
    void ofPlatform_shouldBuildThreadButNotStart() {
        // 存储线程执行结果
        var ref = new AtomicReference<String>();

        // 构建线程对象, 但不启动线程
        var thread = Thread.ofPlatform()
                .unstarted(() -> {
                    ref.set("thread executed");
                });

        // 确认线程未运行
        then(thread.isAlive()).isFalse();

        // 启动线程
        thread.start();

        // 等待线程结束
        thread.join();
        then(thread.isAlive()).isFalse();

        // 确认线程执行结果
        then(ref.get()).isEqualTo("thread executed");
    }

    /**
     * 测试 {@link Thread.Builder#factory()} 方法, 创建一个线程工厂对象
     *
     * <p>
     * 可以通过 {@link Thread.Builder} 对象设置线程的属性, 包括线程名, 线程优先级, 线程组等,
     * 并通过 {@link Thread.Builder#factory()} 方法创建一个
     * {@link java.util.concurrent.ThreadFactory ThreadFactory} 类型的线程工厂对象,
     * 之后可以通过该线程工厂对象创建线程对象
     * </p>
     */
    @Test
    @SneakyThrows
    void factory_shouldCreateThreadByThreadFactory() {
        // 存储线程执行结果
        var ref = new AtomicReference<String>();

        // 创建一个构造线程的工厂对象
        var factory = Thread.ofPlatform()
                .name("test-thread") // 设置线程名称
                .daemon(false) // 设置线程是否为守护线程
                .inheritInheritableThreadLocals(false) // 设置线程是否继承父线程的 ThreadLocal 内容
                .priority(Thread.NORM_PRIORITY) //
                .factory();

        // 通过线程工厂对象创建新线程
        var thread = factory.newThread(() -> {
            ref.set("thread executed");
        });

        // 确认线程未运行
        then(thread.isAlive()).isFalse();

        // 启动线程
        thread.start();

        // 等待线程结束
        thread.join();
        then(thread.isAlive()).isFalse();

        // 确认线程执行结果
        then(ref.get()).isEqualTo("thread executed");
    }
}
