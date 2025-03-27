package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link Lock} 接口
 *
 * <p>
 * {@link Lock} 接口是 Java 并发编程中用于控制线程同步的接口,
 * 该接口定义了一个互斥锁
 * </p>
 *
 * <p>
 * {@link ReentrantLock} 是 {@link Lock} 接口的一个实现类,
 * 实现了一个可重入锁, 即同一线程可以多次获取锁
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
public class LockTest {
    @Test
    @SneakyThrows
    public void sss1() {
        var results = new ArrayList<Integer>();

        // 定义一个公平的可重入锁
        Lock lock = new ReentrantLock(true);

        var threads = new Thread[10];
        for (var i = 0; i < threads.length; i++) {
            var index = i + 1;

            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    results.add(index);
                } finally {
                    lock.unlock();
                }
            });
            threads[i].start();
        }

        for (var thread : threads) {
            thread.join();
        }

        then(results).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    @SneakyThrows
    public void sss2() {
        // 定义一个公平的可重入锁
        Lock lock = new ReentrantLock(true);

        lock.lock();

        var locked = lock.tryLock(1, TimeUnit.SECONDS);
        then(locked).isTrue();
    }
}
