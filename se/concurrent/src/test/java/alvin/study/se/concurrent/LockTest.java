package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.concurrent.util.FaireCounter;
import alvin.study.se.concurrent.util.SystemInfo;
import alvin.study.se.concurrent.util.Threads;

/**
 * 测试 {@link java.util.concurrent.locks.Lock Lock} 接口
 *
 * <p>
 * {@link java.util.concurrent.locks.Lock Lock} 接口是 Java
 * 并发编程中用于控制线程同步的接口, 该接口定义了一个互斥锁
 * </p>
 *
 * <p>
 * {@link ReentrantLock} 是 {@link java.util.concurrent.locks.Lock
 * Lock} 接口的一个实现类, 实现了一个可重入锁, 即同一线程可以多次获取锁
 * </p>
 *
 * <p>
 * 默认情况下, {@link ReentrantLock} 是非公平锁,
 * 即线程获取锁的顺序是随机的, 可以通过构造器参数产生公平的
 * {@link ReentrantLock} 锁对象
 * </p>
 *
 * <p>
 * 和 {@code synchronized} 关键字类似, 但 {@link ReentrantLock}
 * 类对象作为互斥锁时性能更好
 * </p>
 */
class LockTest {
    /**
     * 测试 {@link ReentrantLock} 锁为公平锁时是否真正公平
     *
     * <p>
     * 默认情况下 (即调用默认构造器), {@link ReentrantLock} 锁是非公平的,
     * 即线程获取锁的顺序是随机的, 因为公平锁需要消耗更多的 CPU 资源,
     * 所以除非有特殊需求, 大部分情况下使用非公平锁即可
     * </p>
     *
     * <p>
     * 通过 {@link ReentrantLock#ReentrantLock(boolean)} 构造器,
     * 根据参数决定锁是否为公平锁
     * </p>
     *
     * <p>
     * 测试方法为: 启动 `10` 个线程, 每个线程都尝试获取同一个 {@link ReentrantLock}
     * 锁, 并记录每个线程实际进入锁的次数
     * </p>
     *
     * <p>
     * 如果 `10` 个线程可以基本平均的概率获取到锁, 则认为锁为公平的, 否则则认为锁为非公平的,
     * 判断依据为各线程进入锁的次数的标准差小于 `4.0`
     * </p>
     */
    @Test
    @SneakyThrows
    void lock_shouldLockThreadByFairLock() {
        var counter = new FaireCounter(10000);

        // 定义一个公平的可重入锁
        var lock = new ReentrantLock(true);

        // 启动 `SystemInfo.cpuCount()` 个线程, 同时访问 `result` 对象资源
        var threads = new Thread[SystemInfo.cpuCount()];
        for (var i = 0; i < threads.length; i++) {
            var index = i + 1;

            // 在线程内启动一个任务, 该任务会获取锁, 每成功获取到一次锁后进行一次记录
            threads[i] = new Thread(() -> {
                var finish = false;

                while (!finish) {
                    // 获取锁, 如果获取成功则继续执行后续代码,
                    // 否则在次进入等待
                    lock.lock();
                    try {
                        // 记录当前线程获取到锁的次数
                        finish = !counter.add(index);
                    } finally {
                        // 释放锁
                        lock.unlock();
                    }
                }
            });

            threads[i].start();
        }

        // 等待所有线程结束
        for (var thread : threads) {
            thread.join();
        }

        // 确认线程进入锁的次数是否公平
        then(counter.isFair(4.0)).isTrue();
    }

    /**
     * 测试 {@link ReentrantLock} 锁为非公平锁时是否真正非公平
     *
     * <p>
     * 默认情况下 (即调用默认构造器), {@link ReentrantLock} 锁是非公平的,
     * 即线程获取锁的顺序是随机的, 因为公平锁需要消耗更多的 CPU 资源,
     * 所以除非有特殊需求, 大部分情况下使用非公平锁即可
     * </p>
     *
     * <p>
     * 通过 {@link ReentrantLock#ReentrantLock(boolean)} 构造器,
     * 根据参数决定锁是否为公平锁
     * </p>
     *
     * <p>
     * 测试方法为: 启动 `10` 个线程, 每个线程都尝试获取同一个 {@link ReentrantLock}
     * 锁, 并记录每个线程实际进入锁的次数
     * </p>
     *
     * <p>
     * 如果 `10` 个线程可以基本平均的概率获取到锁, 则认为锁为公平的, 否则则认为锁为非公平的,
     * 判断依据为各线程进入锁的次数的标准差大于等于 `4.0`
     * </p>
     */
    @Test
    @SneakyThrows
    void lock_shouldLockThreadByUnfairLock() {
        var counter = new FaireCounter(10000);

        // 定义一个非公平的可重入锁
        var lock = new ReentrantLock(false);

        // 启动 `SystemInfo.cpuCount()` 个线程, 同时访问 `result` 对象资源
        var threads = new Thread[SystemInfo.cpuCount()];
        for (var i = 0; i < threads.length; i++) {
            var index = i + 1;

            // 在线程内启动一个任务, 该任务会获取锁, 并将结果添加到 `results` 中
            threads[i] = new Thread(() -> {
                var finish = false;

                while (!finish) {
                    // 获取锁, 如果获取成功则继续执行后续代码,
                    // 否则在次进入等待
                    lock.lock();
                    try {
                        // 记录当前线程获取到锁的次数
                        finish = !counter.add(index);
                    } finally {
                        // 释放锁
                        lock.unlock();
                    }
                }
            });
            threads[i].start();
        }

        // 等待所有线程结束
        for (var thread : threads) {
            thread.join();
        }

        // 确认线程进入锁的次数是否公平
        then(counter.isFair(4.0)).isFalse();
    }

    /**
     * 测试可重入锁的重入特性
     *
     * <p>
     * {@link ReentrantLock} 类对象作为互斥锁时性能更好, 一方面是它具有可重入的特性,
     * 即同一个线程可以多次获取锁, 而无需进行任何等待
     * </p>
     *
     * <p>
     * {@link ReentrantLock#getHoldCount()} 方法返回当前线程获取锁的次数,
     * 根据锁的特性, 只有在同一个线程中重入了锁, 才有可能将该返回增加到大于 `1`
     * </p>
     *
     * <p>
     * {@link ReentrantLock#isHeldByCurrentThread()} 方法返回当前线程获取锁的次数,
     * 根据锁的特性, 只有在同一个线程中重入了锁, 才有可能将该返回增加到大于 `1`
     * </p>
     */
    @Test
    @SneakyThrows
    void lock_shouldLockReentrant() {
        // 定义可重入锁
        var lock = new ReentrantLock();
        then(lock.isLocked()).isFalse(); // 确认此时未锁定
        then(lock.getHoldCount()).isEqualTo(0); // 确认锁持有次数为 0
        then(lock.isHeldByCurrentThread()).isFalse(); // 确认当前线程未获取锁

        try {
            // 执行锁定函数, 令当前线程获取锁
            lock.lock();
            then(lock.isLocked()).isTrue(); // 确认此时已锁定
            then(lock.getHoldCount()).isEqualTo(1); // 确认锁持有次数为 1
            then(lock.isHeldByCurrentThread()).isTrue(); // 确认当前线程已获取锁

            try {
                // 在同一线程内, 再次尝试获取锁, 仍能获取成功
                lock.lock();
                then(lock.isLocked()).isTrue(); // 确认此时已锁定
                then(lock.getHoldCount()).isEqualTo(2); // 确认锁持有次数为 2
                then(lock.isHeldByCurrentThread()).isTrue(); // 确认当前线程已获取锁
            } finally {
                // 释放一次锁, 由于之前锁定了两次, 故还有一次锁需要释放
                lock.unlock();
                then(lock.isLocked()).isTrue(); // 确认此时已锁定
                then(lock.getHoldCount()).isEqualTo(1); // 确认锁持有次数为 1
                then(lock.isHeldByCurrentThread()).isTrue(); // 确认当前线程已获取锁
            }
        } finally {
            // 释放第二次锁, 至此完成全部解锁
            lock.unlock();
            then(lock.isLocked()).isFalse(); // 确认此时未锁定
            then(lock.getHoldCount()).isEqualTo(0); // 确认锁持有次数为 0
            then(lock.isHeldByCurrentThread()).isFalse(); // 确认当前线程未获取锁
        }
    }

    /**
     * 测试 {@link ReentrantLock} 锁的等待队列相关方法
     *
     * <p>
     * {@link ReentrantLock} 锁的等待队列相关方法, 包括:
     * <ul>
     * <li>
     * 通过 {@link ReentrantLock#hasQueuedThreads()} 判断是否有线程在等待获取锁
     * </li>
     * <li>
     * 通过 {@link ReentrantLock#getQueueLength()} 获取等待队列的长度
     * </li>
     * <li>
     * 通过 {@link ReentrantLock#hasQueuedThread(Thread)} 判断指定线程是否在等待获取锁
     * </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    void waiters_shouldGetWaitersInQueueOnLock() {
        var lock = new ReentrantLock();
        var threads = new Thread[10];

        // 令主线程获取锁
        lock.lock();
        then(lock.hasQueuedThreads()).isFalse(); // 确认等待队列为空
        then(lock.getQueueLength()).isEqualTo(0); // 确认等待队列长度为 0

        try {
            // 启动 10 个线程, 并在每个线程中获取锁
            for (var i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    // 获取锁, 由于锁已经被主线程获取, 故子线程会进入该锁的等待队列
                    lock.lock();
                    try {
                        // do nothing
                    } finally {
                        // 释放锁
                        lock.unlock();
                    }
                });

                // 逐个启动线程
                threads[i].start();
            }

            // 等待所有线程全部启动起来
            Thread.sleep(10);

            then(lock.hasQueuedThreads()).isTrue(); // 确认等待队列不为空
            then(lock.getQueueLength()).isEqualTo(threads.length); // 确认等待队列长度为 10

            // 确认数组中的每个线程都在锁的等待队列中
            for (var thread : threads) {
                then(lock.hasQueuedThread(thread)).isTrue();
            }
        } finally {
            // 主线程释放锁
            lock.unlock();
        }

        // 等待所有线程结束
        for (var thread : threads) {
            thread.join();
        }
    }

    /**
     * 测试 {@link ReentrantLock} 锁的条件相关方法
     *
     * <p>
     * {@link ReentrantLock} 锁的条件为一个 {@link java.util.concurrent.locks.Condition
     * Condition} 类型对象，用于线程间传递事件通知, 即有一个线程中进入等待,
     * 由另一个线程发出通知唤醒前者
     * </p>
     *
     * <p>
     * {@link java.util.concurrent.locks.Condition Condition}
     * 对象的作用是用来在一个线程中进入等待, 另一个线程发出通知唤醒前者, 其包括的方法为:
     * <ul>
     * <li>
     * {@link java.util.concurrent.locks.Condition#await() Condition.await()}
     * 方法, 该方法会释放锁, 并且当前线程进入等待, 直到被另一个线程调用
     * {@link java.util.concurrent.locks.Condition#signal() Condition.signal()}
     * 方法, 唤醒等待的线程
     * </li>
     * <li>
     * {@link java.util.concurrent.locks.Condition#awaitNanos(long)
     * Condition.awaitNanos(long)} 方法, 该方法可以指定线程等待的时长, 单位为纳秒,
     * 可通过该方法的返回值判断是否等待成功, 该方法返回实际等待时间, 如果返回值小于参数值,
     * 则表示等待成功, 如果返回 `0` 或负数, 则表示等待失败
     * </li>
     * <li>
     * {@link java.util.concurrent.locks.Condition#await(long, java.util.concurrent.TimeUnit)
     * Condition.await(long, TimeUnit)} 方法, 该方法可以通过一个数值和时间单位参数,
     * 指定线程等待时长
     * </li>
     * <li>
     * {@link java.util.concurrent.locks.Condition#awaitUntil(java.util.Date)
     * Condition.awaitUntil(java.util.Date)} 方法, 该方法可以指定一个
     * {@link java.util.Date Date} 对象, 表示等待的截止时间, 该方法会一直等待,
     * 直到收到通知或到达截止时间
     * </li>
     * <li>
     * {@link java.util.concurrent.locks.Condition#signal() Condition.signal()}
     * 方法用于发出一个通知, 该方法会唤醒所有等待线程中的一个
     * </li>
     * <li>
     * {@link java.util.concurrent.locks.Condition#signalAll() Condition.signalAll()}
     * 方法用于发出一个通知, 该方法会唤醒所有等待线程
     * </p>
     *
     * <p>
     * 要使用条件, 需要先获取锁, 在获取锁的基础上, 进入等待或发出通知, 例如对于如下锁对象
     *
     * <pre>
     * var lock = new ReentrantLock();
     * </pre>
     *
     * 可以为其创建一个条件对象
     *
     * <pre>
     * var cond = lock.newCondition();
     * </pre>
     *
     * 在一个线程中进入等待:
     *
     * <pre>
     * // 先获取到锁
     * lock.lock();
     * try {
     *     // 进入等待
     *     cond.await();
     * } finally {
     *     // 解锁
     *     lock.unlock();
     * }
     * </pre>
     *
     * 在另一个线程中发出通知:
     *
     * <pre>
     * lock.lock();
     * try {
     *     cond.signal();
     * } finally {
     *     lock.unlock();
     * }
     * </pre>
     * </p>
     */
    @Test
    @SneakyThrows
    void condition_shouldGetConditionOnLock() {
        // 创建锁对象
        var lock = new ReentrantLock();

        // 从锁对象中创建两个条件对象
        // `cond1` 用于主线程对子线程的控制
        // `cond2` 用于子线程对主线程的控制
        var cond1 = lock.newCondition();
        var cond2 = lock.newCondition();

        // 记录等待成功线程数
        var waitCount = new AtomicInteger(0);

        var threads = new Thread[10];

        // 启动 10 个线程, 并在每个线程中获取锁, 并等待条件对象
        for (var i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // 等待锁并获取锁
                lock.lock();
                try {
                    // 等待条件对象
                    // 当条件对象进入等待后, 当前线程会暂时释放锁,
                    // 等待条件对象发出通知后, 当前线程会重新获取锁并继续执行
                    cond1.await();

                    // 等待成功后, 增加等待成功线程数
                    // 如果全部子线程都等待成功, 则发出通知
                    if (waitCount.incrementAndGet() == threads.length) {
                        // 确认主线程在该条件对象上等待
                        then(lock.hasWaiters(cond2)).isTrue();

                        // 确认等待队列长度为 `1`
                        then(lock.getWaitQueueLength(cond2)).isEqualTo(1);

                        // 通过条件对象发出通知
                        cond2.signal();
                    }
                } catch (InterruptedException ignore) {
                    // do nothing
                } finally {
                    // 解除锁
                    lock.unlock();
                }
            });

            // 启动线程
            threads[i].start();
        }

        // 等待 10ms, 令所有子线程都进入等待
        Thread.sleep(10);

        // 按子线程的数量, 发出通知, 令每个子线程都等待成功
        for (var i = 0; i < threads.length; i++) {
            lock.lock();
            try {
                // 确认有子线程在指定条件对象上等待
                then(lock.hasWaiters(cond1)).isTrue();

                // 确认在指定条件对象上的等待队列长度
                // 当一个子线程等待成功后, 该队列长度依次递减
                then(lock.getWaitQueueLength(cond1)).isEqualTo(threads.length - i);

                // 发出通知, 令一个在 `cond1` 条件上等待的线程被唤醒
                cond1.signal();
            } finally {
                lock.unlock();
            }
        }

        // 等待子线程发送到主线程的通知
        lock.lock();
        try {
            cond2.await();
        } finally {
            lock.unlock();
        }

        // 等待所有子线程结束
        then(Threads.joinAll(threads, 100)).isTrue();

        // 确认所有子线程都等待成功
        then(waitCount.get()).isEqualTo(threads.length);
    }

    /**
     * 测试通过 {@link java.util.concurrent.locks.Condition Condition}
     * 对象一次性通知所有等待线程
     */
    @Test
    @SneakyThrows
    void condition_shouldNotifyAllByCondition() {
        // 创建锁对象
        var lock = new ReentrantLock();

        // 从锁对象中创建条件对象
        var cond = lock.newCondition();

        // 记录等待成功线程数
        var waitCount = new AtomicInteger(0);

        var threads = new Thread[10];

        // 启动 10 个线程, 并在每个线程中获取锁, 并等待条件对象
        for (var i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // 等待锁并获取锁
                lock.lock();
                try {
                    // 等待条件对象
                    // 当条件对象进入等待后, 当前线程会暂时释放锁,
                    // 等待条件对象发出通知后, 当前线程会重新获取锁并继续执行
                    cond.await();

                    // 等待成功后, 增加等待成功线程数
                    waitCount.incrementAndGet();
                } catch (InterruptedException ignore) {
                    // do nothing
                } finally {
                    // 解除锁
                    lock.unlock();
                }
            });

            // 启动线程
            threads[i].start();
        }

        // 等待 10ms, 令所有子线程都进入等待
        Thread.sleep(10);

        // 按子线程的数量, 发出通知, 令每个子线程都等待成功
        lock.lock();
        try {
            // 确认有子线程在指定条件对象上等待
            then(lock.hasWaiters(cond)).isTrue();

            // 确认在指定条件对象上的等待队列长度
            // 当一个子线程等待成功后, 该队列长度依次递减
            then(lock.getWaitQueueLength(cond)).isEqualTo(threads.length);

            // 发出通知, 令所有在 `cond` 条件上等待的线程都被唤醒
            cond.signalAll();
        } finally {
            lock.unlock();
        }

        // 等待所有子线程结束
        then(Threads.joinAll(threads, 100)).isTrue();

        // 确认所有子线程都等待成功
        then(waitCount.get()).isEqualTo(threads.length);
    }
}
