package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.BlockingQueue;
import alvin.study.se.concurrent.util.Threads;
import alvin.study.se.concurrent.util.TimeIt;

/**
 * 测试 {@link Semaphore} 类
 *
 * <p>
 * {@link Semaphore} 类, 即信号量, 用于控制对共享资源的访问, 通过指定数量的
 * "许可证" 对资源访问进行控制, 当资源的许可证耗尽时, 访问资源的线程被挂起,
 * 直到某一个线程释放了资源许可证, 之前被挂起的线程方可继续运行
 * </p>
 *
 * <p>
 * 所以 {@link Semaphore} 对象中的许可证可以视为 "资源" 本身, 许可证可被占用,
 * 即意味着资源可被使用, 反之则表示资源不可使用
 * </p>
 */
class SemaphoreTest {
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

        // 创建 4 个线程对象
        var threads = new Thread[MAX_THREADS];

        // 记录线程执行结果
        var records = new ArrayList<TimeIt>();

        // 同时启动 4 个线程, 在每个线程中通过信号量获取许可证, 并在成功获取到许可证后,
        // 记录获取到许可证的时间
        // 在获取到许可证后, 线程休眠 100ms, 释放获取到的许可证
        // 由于 `Semaphore` 的许可证数量为 2, 所以每次只能有两个线程可以同时获取到许可证,
        // 结果就是: 在记录的结果中, 前两个线程结果的时间几乎一致, 后两个几乎一致,
        // 第三个和第四个记录结果相差 100ms 左右
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    // 获取信号量许可证, 如果信号量暂时无许可证, 则阻塞当前线程
                    semaphore.acquire();
                    try {
                        // 获取到许可证后, 记录获取到许可证的时间
                        synchronized (records) {
                            records.add(TimeIt.start());
                        }

                        // 休眠 100ms 后, 释放获取到的许可证, 故后续再要获取许可证,
                        // 至少要在 100ms 之后
                        Thread.sleep(100);
                    } finally {
                        semaphore.release();
                    }
                } catch (InterruptedException ignore) {}
            });

            threads[i].start();
        }

        // 等待所有线程结束
        then(Threads.joinAll(threads, 1000)).isTrue();

        // 确认结果的前两项和后两项的时间差值, 应该在 0ms 左右
        then(records.get(0).since(records.get(0))).isBetween(0L, 10L);
        then(records.get(2).since(records.get(3))).isBetween(0L, 10L);

        // 确认结果的第三项和第四项的时间差值, 应该在 100ms 左右
        then(records.get(1).since(records.get(3))).isBetween(100L, 110L);
    }

    /**
     * 测试释放许可证方法
     *
     * <p>
     * 当 {@link Semaphore} 对象中的一个许可证被占用后, 需要及时释放, 以保证
     * {@link Semaphore} 对象中的许可证可及时的被下一个资源访问方占用,
     * 避免资源空闲浪费
     * </p>
     *
     * <p>
     * 如果 {@link Semaphore} 对象的许可证被占用后未被释放, 则称为 "资源泄露",
     * 则意味着整体资源的可访问数量少 `1`, 可能会导致某个线程永远无法访问资源
     * </p>
     *
     * <p>
     * 通过 {@link Semaphore#release()} 方法可以释放 `1` 个许可证,
     * 可以让等待占用许可证的 `1` 个线程立即唤醒并获取到许可证, 执行后续代码
     * </p>
     *
     * <p>
     * 通过 {@link Semaphore#release(int)} 方法可以释放 `n` 个许可证,
     * 可以让等待占用许可证的 `n` 个线程立即唤醒并获取到许可证, 执行后续代码,
     * 而且 `n` 的值可以为任意正整数, 不拘泥于 {@link Semaphore}
     * 对象在初始化时的许可证数量
     * </p>
     */
    @Test
    @SneakyThrows
    void release_shouldReleaseSemaphore() {
        // 初始化信号量, 确认初始许可证数量为 1
        var semaphore = new Semaphore(1);
        then(semaphore.availablePermits()).isEqualTo(1);

        // 释放 100 个许可证, 确认许可证数量为 101
        semaphore.release(100);
        then(semaphore.availablePermits()).isEqualTo(101);

        // 占用 10 个许可证, 确认许可证数量为 91
        semaphore.acquire(10);
        then(semaphore.availablePermits()).isEqualTo(91);
    }

    /**
     * 测试 {@link Semaphore#drainPermits()} 方法
     *
     * <p>
     * {@link Semaphore#drainPermits()} 方法用于占用信号量中剩余的全部许可证,
     * 令所有在此信号量上获取许可证的线程全部进入阻塞状态, 直到有新的许可证被释放
     * </P>
     */
    @Test
    @SneakyThrows
    void drainPermits_shouldControlBetweenThreads() {
        final int MAX_PERMITS = 5;

        // 创建具备 5 个许可证的信号量对象, 确认所有许可证均可被使用
        var semaphore = new Semaphore(MAX_PERMITS);
        then(semaphore.availablePermits()).isEqualTo(MAX_PERMITS);

        // 占用所有许可证, 确认所有许可证均被占用, 所有子线程无法获取到许可证
        semaphore.acquire(MAX_PERMITS);
        then(semaphore.availablePermits()).isEqualTo(0);

        // 释放一半的许可证, 此时有 `MAX_PERMITS / 2` 个线程可以获取到许可证
        semaphore.release(MAX_PERMITS / 2);
        then(semaphore.availablePermits()).isEqualTo(MAX_PERMITS / 2);

        // 占用剩余的所有许可证, 此时所有线程均无法获取到许可证
        semaphore.drainPermits();
        then(semaphore.availablePermits()).isEqualTo(0);

        // 确认无法获取到许可证
        then(semaphore.tryAcquire(100, TimeUnit.MICROSECONDS)).isFalse();
    }

    /**
     * 测试获取在信号量上等待的线程数量
     *
     * <p>
     * 当若干个线程在一个信号量上等待获取许可证时, 可通过
     * {@link Semaphore#hasQueuedThreads()} 方法进行判断, 当该方法返回 `true`
     * 时, 表示确实有线程在等待获取许可证
     * </p>
     *
     * <p>
     * 可进一步通过 {@link Semaphore#getQueueLength()}
     * 方法获取在信号量上等待许可证的线程数量
     * </p>
     */
    @Test
    @SneakyThrows
    void queue_shouldGetQueueLengthOnSemaphore() {
        final int MAX_THREADS = 10;

        // 创建具备 0 个许可证的信号量对象
        var semaphore = new Semaphore(0);

        // 创建 10 个线程, 令每个线程都去占用一个许可证
        var threads = new Thread[MAX_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ignore) {}
            });

            // 启动线程
            threads[i].start();
        }

        // 等待 10ms, 确认所有线程都执行到 `semaphore.acquire()` 语句
        Thread.sleep(10);

        // 确认有线程在等待许可证
        then(semaphore.hasQueuedThreads()).isTrue();

        // 确认有 10 个线程在等待获取许可证
        then(semaphore.getQueueLength()).isEqualTo(MAX_THREADS);

        // 释放 10 个许可证, 确认所有线程都能获取到许可证并执行结束
        semaphore.release(semaphore.getQueueLength());
        then(Threads.joinAll(threads, 1000)).isTrue();
    }

    /**
     * 测试信号量控制队列
     *
     * <p>
     * 信号量的一个重要应用是控制多个线程完成 "生产者" 和 "消费者" 模式,
     * 即一部分线程产生数据, 另一部分线程消费产生的数据, 这就要求 "生产者"
     * 和 "消费者" 之间具备资源控制
     * </p>
     *
     * <p>
     * 当 "生产者" 产生一个数据项时, 相当于资源数量 `+1`,
     * 此时信号量的许可证数量同时 `+1`, 此时 "消费者" 线程可以获取到许可证,
     * 从而唤醒 "消费者" 线程进行消费
     * </p>
     *
     * @see BlockingQueue
     */
    @Test
    @SneakyThrows
    void blockingQueue_shouldGetQueueLengthOnSemaphore() {
        final int MAX_QUEUE_SIZE = 5;

        // 创建一个有 5 个元素的阻塞队列
        var queue = new BlockingQueue<Integer>(MAX_QUEUE_SIZE);

        // 往队列中添加 5 个元素
        for (int i = 0; i < MAX_QUEUE_SIZE; i++) {
            queue.offer(i);
        }

        // 确认队列中元素数量为 5
        then(queue.size()).isEqualTo(MAX_QUEUE_SIZE);

        // 往队列中添加第 6 个元素, 确认添加失败, 因为队列已满
        then(queue.offer(5, 100, TimeUnit.MILLISECONDS)).isFalse();

        // 从队列中获取 5 个元素, 确认获取成功
        then(queue.peek()).isEqualTo(0);
        then(queue.peek()).isEqualTo(1);
        then(queue.peek()).isEqualTo(2);
        then(queue.peek()).isEqualTo(3);
        then(queue.peek()).isEqualTo(4);

        // 从队列中获取第 6 个元素, 确认获取失败, 因为队列已空
        then(queue.peek(100, TimeUnit.MICROSECONDS)).isNull();

        var results = new ArrayList<Integer>();

        // 创建一个线程, 从队列中获取元素, 并将获取到的元素添加到结果列表中
        // 当线程从队列中获取到值为 `10` 的元素时, 线程结束
        var thread = new Thread(() -> {
            while (true) {
                try {
                    // 从队列中获取元素, 并将获取到的元素添加到结果列表中
                    var num = queue.peek();
                    // 将获取到的元素添加到结果列表中
                    results.add(num);

                    // 当线程从队列中获取到值为 `10` 的元素时, 线程结束
                    if (num == 10) {
                        break;
                    }
                } catch (InterruptedException ignore) {}
            }
        });

        // 启动线程
        thread.start();

        // 往队列中添加 10 个元素, 值为 `0~9`
        for (int i = 0; i < 10; i++) {
            queue.offer(i);
        }

        // 确认线程未结束, 因为线程尚未从队列中获取到值为 `10` 的元素
        then(thread.join(Duration.ofMillis(100))).isFalse();

        // 往队列中添加值为 `10` 的元素
        queue.offer(10);

        // 确认线程已结束, 因为线程从队列中获取到值为 `10` 的元素
        then(thread.join(Duration.ofMillis(100))).isTrue();

        // 确认结果列表中包含 `0~10` 的元素
        then(results).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
}
