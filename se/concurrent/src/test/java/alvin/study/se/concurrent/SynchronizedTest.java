package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试线程互斥锁
 */
public class SynchronizedTest {
    /**
     * 定义一个数字对象, 用于对一个数字从 `1` 开始自加
     */
    class Number {
        private int value = 1;

        /**
         * 对 `value` 字段进行非原子的自加操作
         */
        public void increment() {
            this.value++;
        }

        /**
         * 对 `value` 字段进行原子化的自加操作, 此时该方法将以当前对象作为互斥对象
         */
        public synchronized void incrementSynchronized() {
            this.value++;
        }

        public int getValue() { return value; }

        public void reset() {
            this.value = 0;
        }
    };

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
        var num = new Number();

        // 定义线程运行对象
        var runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 没有任何互斥处理
            for (var i = 0; i < 10000; i++) {
                num.increment();
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 验证数字是否被正确自加, 两个线程针对 `num` 对象各进行 `10000` 次自加操作,
        // 但最终结果却小于 `20000`, 这是因为 `num` 对象的自加不是原子操作
        then(num.getValue()).isLessThan(20000);

        num.reset();

        // 定义线程运行对象
        runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 这次通过互斥锁对 `num` 对象进行原子化自加操作
            for (var i = 0; i < 10000; i++) {
                synchronized (num) {
                    num.increment();
                }
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 这次结果为 `20000`, 表示`num` 对象的自加为原子操作
        then(num.getValue()).isEqualTo(20000);

        num.reset();

        // 定义线程运行对象
        runner = (Runnable) () -> {
            // 运行 `10000` 次, 每次自加 `1`, 这次调用原子化方法对 `num` 对象进行自加操作
            for (var i = 0; i < 10000; i++) {
                num.incrementSynchronized();
            }
        };

        // 启动两个线程同时执行上述任务
        startTwoThreadsWithRunner(runner);

        // 这次结果为 `20000`, 表示`num` 对象的自加为原子操作
        then(num.getValue()).isEqualTo(20000);
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
     * 进入等待的线程会暂停运行并暂时退出同步代码块, 直到被通知后立即回到同步代码块,
     * 继续运行后续代码
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
            } catch (InterruptedException e) {}
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
}
