package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.Counter;
import alvin.study.se.concurrent.util.TimeIt;

/**
 * 测试线程互斥
 *
 * 通过 {@code synchronized} 关键字, 可以对任意 Java 对象进行互斥操作,
 * 所谓的互斥操作, 即:
 *
 * <ul>
 * <li>
 * 可以通过 {@code synchronized} 关键字以及一个对象定义一个代码块,
 * (该对象可以为任意对象), 多个线程会依次进入该以该对象为互斥对象的代码块,
 * 且每次只有一个线程可进入代码块, 其它线程进入等待
 * </li>
 * <li>
 * 也可以通过 {@code synchronized} 关键字修饰一个方法,
 * 则表示整个方法的代码为互斥, 即每次只有一个线程可以执行该方法,
 * 其它线程进入等待
 * </li>
 * <li>
 * {@code synchronized} 关键字定义的互斥锁为不可重入的非公平锁,
 * 即以获得锁的线程不能重复获取同一个锁, 且在锁上等待的线程在锁解除后,
 * 会随机获取到锁
 * </li>
 * </ul>
 */
class SynchronizedTest {
    /**
     * 启动两个线程, 并且同时运行指定的任务
     *
     * @param runner 任务对象
     */
    @SneakyThrows
    static void startTwoThreadsWithRunner(Runnable runner) {
        var t1 = new Thread(runner);
        var t2 = new Thread(runner);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    /**
     * 测试线程互斥锁
     *
     * <p>
     * 通过 {@code synchronized} 关键字, 可以对任意 Java 对象进行互斥操作,
     * 即多个线程并行运行时, 只有一个线程可以执行同步代码块, 其它线程则进入等待,
     * 直到该线程退出同步代码块
     * </p>
     *
     * <p>
     * {@code synchronized} 关键字可以修饰代码块, 也可以修饰方法:
     * <ul>
     * <li>
     * 当 {@code synchronized} 关键字修饰代码块时, 需要指定一个对象作为互斥对象,
     * 该对象可以为任意对象, 多个线程会依次进入该以该对象为互斥对象的代码块
     * </li>
     * <li>
     * 当 {@code synchronized} 关键字修饰方法时, 会自动将该方法的当前对象作为互斥对象,
     * 即对于同一对象, 每次只能有一个线程可以执行该方法, 其它线程进入等待
     * </li>
     * <li>
     * 当 {@code synchronized} 关键字修饰静态方法时, 会自动将该方法所属的类对象作为互斥对象,
     * 即在全局范围内, 每次只能有一个线程可以执行该方法, 其它线程进入等待
     * </li>
     * </ul>
     * </p>
     */
    @Test
    @SneakyThrows
    void synchronized_shouldMakeThreadSynchronized() {
        var counter = new Counter();

        // 定义线程运行对象
        var runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 没有任何互斥处理
            for (var i = 0; i < 10000; i++) {
                counter.increment();
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 验证数字是否被正确自加, 两个线程针对 `num` 对象各进行 `10000` 次自加操作,
        // 但最终结果却小于 `20000`, 这是因为 `num` 对象的自加不是原子操作
        then(counter.getValue()).isLessThan(20000);

        counter.reset();

        // 定义线程运行对象
        runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 这次通过互斥锁对 `num` 对象进行原子化自加操作
            for (var i = 0; i < 10000; i++) {
                synchronized (counter) {
                    counter.increment();
                }
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 这次结果为 `20000`, 表示`num` 对象的自加为原子操作
        then(counter.getValue()).isEqualTo(20000);

        counter.reset();

        // 定义线程运行对象
        runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 这次调用原子化方法对 `num` 对象进行自加操作
            for (var i = 0; i < 10000; i++) {
                counter.incrementSynchronized();
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 这次结果为 `20000`, 表示`num` 对象的自加为原子操作
        then(counter.getValue()).isEqualTo(20000);
    }

    /**
     * 测试线程等待和通知机制
     *
     * <p>
     * 通过 {@code synchronized} 关键字, 和一个用于互斥的任意对象, 可以完成 "等待"
     * 和 "通知" 的机制, 即进入同步代码块的线程可以进入等待, 直到另一个线程在同一对象上发出通知,
     * 然后被等待的线程可以继续运行
     * </p>
     *
     * <p>
     * 在一个线程的 {@code synchronized} 代码块中, 可以通过互斥对象的 {@link Object#wait()}
     * 方法进入等待
     * </p>
     *
     * <p>
     * 进入等待的线程会暂停运行并暂时退出同步代码块, 直到被通知后立即回到同步代码块,
     * 继续运行后续代码
     * </p>
     *
     * <p>
     * 在另一个线程的 {@code synchronized} 代码块中, 可以通过同一个互斥对象的
     * {@link Object#notify()} 方法发出通知, 此时如果存在等待中的线程,
     * 则等待中线程的其中一个会被唤醒
     * </p>
     *
     * <p>
     * 发出通知的线程会进入同步代码块, 并发出通知, 唤醒等待的线程, 然后继续运行后续代码
     * </p>
     */
    @Test
    @SneakyThrows
    void wait_shouldWaitForNotify() {
        // 用于保存生产数据的集合
        var origin = new ArrayList<Integer>();

        // 用于保存消费数据的集合
        var target = new ArrayList<Integer>();

        // 定义线程对象, 用于将数据从生产集合按顺序转移到消费集合
        var thread = new Thread(() -> {
            var stop = false;
            try {
                while (!stop) {
                    // 进入以 `origin` 对象进行互斥的同步代码块
                    synchronized (origin) {
                        // 等待 `origin` 上发出通知, 接收到通知前,
                        // 代码会暂停在这里
                        origin.wait();

                        // 将 `origin` 中的数据全部转移到 `target` 中
                        while (!origin.isEmpty()) {
                            target.add(origin.removeFirst());
                        }

                        // 当 `target` 中的数据达到指定数量时, 退出循环
                        if (target.size() == 1000) {
                            stop = true;
                        }
                    }
                }
            } catch (InterruptedException ignore) {}
        });

        // 启动线程
        thread.start();

        // 暂停主线程, 让其它线程得到运行机会
        Thread.sleep(0);

        // 循环 `1000` 次, 每次添加一个数到 `origin` 集合中
        for (var i = 1; i <= 1000; i++) {
            synchronized (origin) {
                origin.add(i);

                // 每次添加一个数, 都发出通知, 唤醒等待的线程去进行消费
                origin.notify();
            }
        }

        // 等待线程结束
        thread.join();

        // 此时 `origin` 集合应该为空, 其中所有数据均被消费
        then(origin).isEmpty();

        // 此时 `target` 集合应该有 `1000` 个元素, 且按顺序排列,
        // 为从 `origin` 集合中依次消费的数据
        then(target).hasSize(1000);
        then(target).first().isEqualTo(1);
        then(target).last().isEqualTo(1000);

        for (var i = 0; i < target.size() - 1; i++) {
            then(target.get(i + 1) - target.get(i)).isEqualTo(1);
        }
    }

    /**
     * 测试一次性通知所有等待中的线程
     *
     * <p>
     * 在 {@code synchronized} 代码块中, 通过互斥对象的 {@link Object#notifyAll()}
     * 方法可以通知此时此刻所有正在等待的线程, 所有等待线程会被唤醒
     * </p>
     */
    @Test
    @SneakyThrows
    void notifyAll_shouldWaitForNotifyAll() {
        // 定义线程数组
        var threads = new Thread[10];

        // 用于记录各个线程运行结果的集合
        var results = new HashSet<Long>();

        // 批量创建 10 个线程
        for (var i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    synchronized (results) {
                        // 进入等待
                        results.wait();

                        // 保存线程执行结果
                        results.add(Thread.currentThread().threadId());
                    }
                } catch (InterruptedException ignore) {}
            });

            // 启动线程
            threads[i].start();
        }

        Thread.sleep(100);

        // 此时子线程都在等待, 结果集合为空
        then(results).isEmpty();

        // 发出通知, 由于 `notifyAll()` 方法会唤醒所有正在等待的线程,
        // 故所有线程会在同一时刻开始执行
        synchronized (results) {
            results.notifyAll();
        }

        // 等待所有线程结束
        for (var thread : threads) {
            thread.join();
        }

        // 此时结果集合应该有 10 个元素, 且为所有子线程执行的结果
        then(results).hasSize(10);
    }

    /**
     * 测试等待超时
     *
     * <p>
     * 在 {@code synchronized} 代码块中, 通过互斥对象的 {@link Object#wait(long)}
     * 方法可以指定等待时间, 如果在指定时间内没有收到通知, 则会继续运行后续代码
     * </p>
     *
     * <p>
     * 可以通过 {@link System#currentTimeMillis()} (或 {@link System#nanoTime()})
     * 方法对等待进行计时, 并通过计时结果判断等待是否超时, 例如
     *
     * <pre>
     * var ts = System.currentTimeMillis();
     * obj.wait(100);
     *
     * var success = System.currentTimeMillis() - ts &lt; 100;
     * </pre>
     *
     * 参见上述代码, 当 `success` 为 `true`, 则说明在 100ms 内收到通知, 等待成功,
     * 否则表示等待失败
     * </p>
     */
    @Test
    @SneakyThrows
    void wait_shouldWaitTimeout() {
        var mutex = new Object();

        // 测试等待超时的情况
        synchronized (mutex) {
            // 记录等待前的时间
            var timeit = TimeIt.start();

            // 执行等待, 等待 `100ms`
            mutex.wait(100);

            // 等待时间应该大于等于 `100ms`, 表示等待失败
            then(timeit.since()).isGreaterThanOrEqualTo(100);
        }

        // 测试等待成功的情况
        synchronized (mutex) {
            // 启动一个线程, 模拟发出通知
            var thread = new Thread(() -> {
                try {
                    // 休眠 90ms 后发出通知
                    Thread.sleep(90);

                    synchronized (mutex) {
                        mutex.notify();
                    }
                } catch (InterruptedException ignore) {}
            });

            // 启动线程
            thread.start();

            // 记录等待前的时间
            var timeit = TimeIt.start();

            // 执行等待, 等待 `100ms`
            mutex.wait(100, 100);

            // 等待时间应该小于 `100ms`, 表示等待成功
            then(timeit.since()).isLessThan(100);
        }
    }
}
