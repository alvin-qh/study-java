package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.service.Fibonacci;

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
    void futureTask_shouldGetResultOfThreadByFutureTask() throws Exception {
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
}
