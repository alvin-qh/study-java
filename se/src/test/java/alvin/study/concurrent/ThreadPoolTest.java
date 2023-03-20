package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

class ThreadPoolTest {
    // 保存线程执行器对象的集合, 用于在测试结束后进行关闭
    private AtomicReference<ExecutorService> executorsHolder = new AtomicReference<>();

    /**
     * 通过阻塞队列创建线程池
     *
     * <p>
     * 通过 {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, java.util.concurrent.BlockingQueue)}
     * </p>
     *
     * @param queueSize
     * @return
     */
    private ExecutorService arrayBlockingQueueExecutor(int queueSize) {
        var maxThread = Runtime.getRuntime().availableProcessors();

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

        executorsHolder.set(executor);
        return executor;
    }

    /**
     * 在每个测试之后执行, 关闭线程池
     */
    @AfterEach
    void afterEach() {
        var executor = executorsHolder.getAndUpdate(t -> null);
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    void futureTask_shouldUseFutureTaskInThread() throws Exception {
        var task = new FutureTask<>(() -> Fibonacci.calculate(20));

        var thread = new Thread(task);
        thread.start();

        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);
        then(thread.isAlive()).isFalse();
        then(task.get()).isEqualTo(6765);
    }

    @Test
    void futureTask_shouldCreateFutureTaskBySubmitThreadPool() throws Exception {
        var executor = arrayBlockingQueueExecutor(20);

        var task = executor.submit(() -> Fibonacci.calculate(20));

        await().atMost(5, TimeUnit.SECONDS).until(task::isDone);
        then(task.get()).isEqualTo(6765);
    }

    @Test
    void multiFutureTasks_() {
        var results = Lists.<Future<Integer>>newArrayList();
        var resultCount = new AtomicInteger();

        var executor = arrayBlockingQueueExecutor(20);

        for (var i = 0; i < 20; i++) {
            var n = i;

            results.add(executor.submit(() -> {
                try {
                    return Fibonacci.calculate(n + 1);
                } finally {
                    resultCount.incrementAndGet();
                }
            }));
        }

        await().atMost(5, TimeUnit.SECONDS).until(() -> resultCount.get() == 20);

        then(results).allMatch(Future::isDone);
        then(results).map(Future<Integer>::get).containsExactly(1, 1, 2, 3, 5, 8, 13, 21,
            34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765);
    }
}
