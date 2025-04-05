package alvin.study.se.concurrent.util;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nullable;

/**
 * 定义阻塞的消息队列, 用于完成 "生产者" 线程和 "消费者" 线程之间的通讯
 */
public class BlockingQueue<T> {
    // 用于存储队列消息的链表对象
    private final LinkedList<T> values = new LinkedList<>();

    // 用于管控 "生产者" 线程的信号量, 确保 "生产者" 线程不能生产太多消息
    private final Semaphore semaOffer;

    // 用于管控 "消费者" 线程的信号量, 确保 "消费者" 线程只能消费生产者产生的消息
    private final Semaphore semaPeek;

    /**
     * 构造器, 初始化信号量对象
     *
     * @param capacity 消息队列的最大长度, 即 "生产者" 线程可产生的最多消息
     */
    public BlockingQueue(int capacity) {
        // 初始化信号量对象, 表示 "生产者" 可同时产生的最大消息数量
        // 在无消费的前提下, "生产者" 最多可产生的消息数量为 `capacity`
        this.semaOffer = new Semaphore(capacity);

        // 初始化信号量对象, 表示 "消费者" 线程可同时消费的最大消息数量
        // 参数值 `0` 表示目前 "消费者" 无消息可消费
        this.semaPeek = new Semaphore(0);
    }

    /**
     * 添加消息到消息队列中
     *
     * @param object 消息对象
     * @throws InterruptedException 线程中断异常
     */
    public void offer(T object) throws InterruptedException {
        // 获取 "生产者" 信号量许可证, 确认 "生产者" 线程可继续生产消息
        this.semaOffer.acquire();

        synchronized (this.values) {
            // 将消息对象添加到消息队列中
            this.values.add(object);
        }

        // 释放 "消费者" 信号量许可证, 表示 "消费者" 线程可消费消息
        this.semaPeek.release();
    }

    /**
     * 添加消息到消息队列中
     *
     * @param object  消息对象
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return 如果消息添加成功, 返回 {@code true}, 否则返回 {@code false}
     * @throws InterruptedException 线程中断异常
     */
    public boolean offer(T object, long timeout, TimeUnit unit) throws InterruptedException {
        // 尝试获取 "生产者" 信号量许可证, 超时返回 {@code false}, 表示无法生产消息
        if (!this.semaOffer.tryAcquire(timeout, unit)) {
            return false;
        }

        synchronized (this.values) {
            // 将消息对象添加到消息队列中
            this.values.add(object);
        }

        // 释放 "消费者" 信号量许可证, 表示 "消费者" 线程可消费消息
        this.semaPeek.release();
        return true;
    }

    /**
     * 从消息队列中获取消息
     *
     * @return 消息对象
     * @throws InterruptedException 线程中断异常
     */
    public T peek() throws InterruptedException {
        // 获取 "消费者" 信号量许可证, 确认 "消费者" 线程可继续消费消息
        this.semaPeek.acquire();

        var val = (T) null;
        synchronized (this.values) {
            // 从消息队列中获取消息对象
            val = this.values.poll();
        }

        // 释放 "生产者" 信号量许可证, 表示 "生产者" 线程可继续生产消息
        this.semaOffer.release();
        return val;
    }

    /**
     * 从消息队列中获取消息
     *
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return 消息对象
     * @throws InterruptedException 线程中断异常
     */
    public @Nullable T peek(long timeout, TimeUnit unit) throws InterruptedException {
        // 尝试获取 "消费者" 信号量许可证, 超时返回 {@code null}, 表示无法消费消息
        if (!this.semaPeek.tryAcquire(timeout, unit)) {
            return null;
        }

        var val = (T) null;
        synchronized (this.values) {
            // 从消息队列中获取消息对象
            val = this.values.poll();
        }

        // 释放 "生产者" 信号量许可证, 表示 "生产者" 线程可继续生产消息
        this.semaOffer.release();
        return val;
    }

    /**
     * 获取消息队列中消息的数量
     *
     * @return 消息数量
     */
    public synchronized int size() {
        return this.values.size();
    }

    /**
     * 判断消息队列是否为空
     *
     * @return 如果消息队列为空, 返回 {@code true}, 否则返回 {@code false}
     */
    public synchronized boolean isEmpty() { return this.values.isEmpty(); }
}
