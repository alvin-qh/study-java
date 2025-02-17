package alvin.study.se.ratelimit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滑动窗口限流
 *
 * <p>
 * 滑动窗口算法是固定窗口算法的改良版本, 将后者的固定窗口进一步分割为多个"块", 然后通过窗口滑动来保证每次请求都在一个合理的"动态窗口"内,
 * 由此避免了固定窗口临界问题, 可以较为合理的进行限流, 其原理如下:
 * <ol>
 * <li>
 * 设定时间窗口 ({@code windowUnit}) 大小, 窗口块的数量 ({@code windowBlocks}) 以及时间窗口内可调用的次数 ({@code threshold});
 * </li>
 * <li>
 * 计算每个窗口块的大小 ({@code windowUnit / windowBlocks}), 通过一个双端队列 ({@code windowQueue}) 管理窗口块;
 * </li>
 * <li>
 * 要计算限流时, 通过窗口块队列 ({@code windowQueue}) 来进行窗口滑动, 得到一个相对窗口, 即以当前时间为止, 大小为 {@code windowUnit}
 * 的窗口, 并计算这个相对窗口中已经发起的处理次数; 相对窗口之前的块即可丢弃;
 * </li>
 * <li>
 * 将上一步计算的次数和 {@code threshold} 进行比较, 判断是否已经到达限流上限
 * </li>
 * <li>
 * 如果未达到限流上限, 则记录本次处理, 即本次处理的时间如果落在一个块中, 则为该块增加调用次数记录, 否则添加一个新的块;
 * </li>
 * </ol>
 * <p>
 * 整个过程如下图所示:
 * </p>
 *
 * <p>
 * <img src="../../../../../../assets/slidingwindow-rate-limiter.png">
 * </p>
 *
 * <p>
 * 滑动窗口算法的权限是, 如果短时间大量请求, 会把一个窗口内的调用快速消耗掉, 导致后续的调用被拒绝, 可以通过设置粒度更小的窗口和块部分解决此问题
 * </p>
 */
public class SlidingWindowRateLimiter implements RateLimiter {
    // 总窗口大小, 单位毫秒
    private final int windowUnit;
    // 窗口中每个块的大小
    private final int windowBlockUnit;
    // 调用数量上限
    private final int threshold;
    // 记录窗口块的双端队列
    private final Deque<Window> windowQueue = new ArrayDeque<>();

    /**
     * 构造器, 构造限流器对象
     *
     * @param windowUnit   总窗口大小, 单位毫秒
     * @param windowBlocks 窗口中块的数量
     * @param threshold    调用数量上限
     */
    public SlidingWindowRateLimiter(int windowUnit, int windowBlocks, int threshold) {
        this.windowUnit = windowUnit;
        this.windowBlockUnit = windowUnit / windowBlocks;
        this.threshold = threshold;
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        // 计算当前时间毫秒数
        var nowMills = System.currentTimeMillis();

        // 滑动窗口, 获取当前窗口内的调用次数
        var count = countCurrentWindow(nowMills);
        // 判断调用次数是否达到了上限
        if (count + permits - 1 >= threshold) {
            return false;
        }

        var lastWin = windowQueue.peekLast();
        // 查看当前时间是否在窗口中最后一个块内
        if (lastWin != null && nowMills - lastWin.getStartTime() <= windowBlockUnit) {
            // 为最后一个快增加调用次数
            lastWin.add(permits);
        } else {
            // 当前时间超出窗口最后一个块的时间范围, 增加新的块
            windowQueue.addLast(new Window(nowMills, permits));
        }
        return true;
    }

    /**
     * 计算当前窗口中已经发生的调用次数
     *
     * @param nowMills 当前时间的毫秒数
     * @return 窗口中已调用的次数
     */
    private int countCurrentWindow(long nowMills) {
        // 对窗口进行滑动, 即从当前时间向前, 将超过窗口大小前的块丢弃
        while (!windowQueue.isEmpty()) {
            var win = windowQueue.peek();
            if (nowMills - win.getStartTime() <= windowUnit) {
                break;
            }
            windowQueue.removeFirst();
        }

        // 统计当前窗口内总的调用次数
        int count = 0;
        for (var win : windowQueue) {
            count += win.getCount();
        }
        return count;
    }

    /**
     * 记录"块"的类型
     */
    private static class Window {
        // 块的起始时间
        private final long startTime;

        // 块内的调用次数
        private final AtomicInteger count;

        /**
         * 构造器, 构造一个块
         *
         * @param startTime 起始时间
         * @param count     调用次数
         */
        public Window(long startTime, int count) {
            this.startTime = startTime;
            this.count = new AtomicInteger(count);
        }

        /**
         * 为当前块增加调用次数
         *
         * @param n 调用次数
         * @return 增加后的结果
         */
        public int add(int n) {
            return count.addAndGet(n);
        }

        /**
         * 获取块起始时间, 单位毫秒
         *
         * @return 块起始时间
         */
        public long getStartTime() { return startTime; }

        /**
         * 获取块中记录的调用次数
         *
         * @return 块中记录的调用次数
         */
        public int getCount() { return count.get(); }
    }
}
