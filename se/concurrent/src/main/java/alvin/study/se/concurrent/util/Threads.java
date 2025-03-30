package alvin.study.se.concurrent.util;

import java.time.Duration;
import java.util.Collection;

/**
 * 线程工具类
 */
public final class Threads {
    /**
     * 等待所有线程执行完毕
     *
     * @param threads 线程对象数组
     * @throws InterruptedException 线程中断异常
     */
    public static boolean joinAll(Thread[] threads, long millis) throws InterruptedException {
        for (var thread : threads) {
            if (!thread.join(Duration.ofMillis(millis))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 等待所有线程执行完毕
     *
     * @param threads 线程对象集合
     * @throws InterruptedException 线程中断异常
     */
    public static boolean joinAll(Collection<? extends Thread> threads, long millis) throws InterruptedException {
        return joinAll(threads.stream().toArray(Thread[]::new), millis);
    }
}
