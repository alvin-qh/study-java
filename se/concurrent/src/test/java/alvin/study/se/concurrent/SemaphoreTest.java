package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.Threads;

/**
 * 测试 {@link Semaphore} 类
 *
 * <p>
 * {@link Semaphore} 类, 即信号量, 用于控制对共享资源的访问, 通过指定数量的
 * "许可证" 对资源访问进行控制, 当资源的许可证耗尽时, 访问资源的线程被挂起,
 * 直到某一个线程释放了资源许可证, 之前被挂起的线程方可继续运行
 * </p>
 */
public class SemaphoreTest {
    /**
     * 测试 {@link Semaphore#acquire()} 和 {@link Semaphore#release()} 方法
     *
     * <p>
     * 本例中创建了具备 `2` 个许可的 {@link Semaphore} 对象, 并创建 `4` 个线程,
     * 当程序运行时, 每次只有 `2` 个线程可以同时运行, 其余的线程会被挂起,
     * 直到前 `2` 个线程释放了许可证, 后 `2` 个线程才可以继续运行
     * </p>
     */
    @Test
    @SneakyThrows
    void acquire_shouldAcquireAndReleaseSemaphore() {
        final int MAX_THREADS = 4;

        // 创建具备 2 个许可证的信号量对象
        var semaphore = new Semaphore(MAX_THREADS / 2);

        // 创建 4 个线程
        var threads = new Thread[MAX_THREADS];

        
        var results = new ArrayList<Long>();

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    semaphore.acquire();
                    try {
                        synchronized (results) {
                            results.add(System.currentTimeMillis());
                        }

                        Thread.sleep(100);
                    } finally {
                        semaphore.release();
                    }
                } catch (InterruptedException ignore) {}
            });

            threads[i].start();
        }

        Threads.joinAll(threads, 1000);

        then(results.get(1) - results.get(0)).isBetween(0L, 10L);
        then(results.get(3) - results.get(2)).isBetween(0L, 10L);

        then(results.get(2) - results.get(1)).isBetween(100L, 110L);
    }
}
