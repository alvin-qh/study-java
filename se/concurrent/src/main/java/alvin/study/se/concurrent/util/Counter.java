package alvin.study.se.concurrent.util;

/**
 * 定义一个计数器类
 */
public class Counter {
    private int value = 0;

    /**
     * 增加计数器 (非线程安全)
     */
    public void increment() {
        this.value++;
    }

    /**
     * 增加计数器 (线程安全)
     */
    public synchronized void incrementSynchronized() {
        this.value++;
    }

    /**
     * 获取计数器值
     *
     * @return 计数器值
     */
    public int getValue() { return value; }

    /**
     * 重置计数器
     */
    public void reset() {
        this.value = 0;
    }
}
