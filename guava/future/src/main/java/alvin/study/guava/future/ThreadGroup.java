package alvin.study.guava.future;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Lists;

/**
 * 管理多个线程的线程组类型
 */
public class ThreadGroup {
    // 存储多个线程对象的集合
    private final List<Thread> threads = Lists.newArrayList();

    /**
     * 添加一个线程对象
     *
     * @param thread 线程对象
     */
    public void add(Thread thread) {
        threads.add(thread);
    }

    /**
     * 启动所有线程
     */
    public void startAll() {
        for (var thread : threads) {
            thread.start();
        }
    }

    /**
     * 等待所有线程结束
     *
     * @param millis 每个线程的最大等待毫秒数
     * @throws InterruptedException 线程中断异常
     * @throws TimeoutException     某个线程等待超时异常
     */
    public void joinAll(long millis) throws InterruptedException, TimeoutException {
        for (var thread : threads) {
            var ts = System.nanoTime();
            thread.join(millis);

            if (System.nanoTime() - ts >= millis * 1000000) {
                throw new TimeoutException();
            }
        }
    }
}
