package alvin.study.se.ratelimit;

/**
 * 漏桶限流
 *
 * <p>
 * 漏桶限流算法的原理如下:
 * <ol>
 * <li>
 * 设定一个具有固定容量的水桶, 该容量即为系统可以处理请求的能力上限, 水桶可以按固定速率进行"漏水", 即实际的请求的处理速度;
 * </li>
 * <li>
 * 每次发起请求时, 先查看桶是否"满了" (当前桶的剩余容量为: 已确定的请求数 - 已处理 (漏掉的) 的请求数), 如果桶未满, 则允许请求,
 * 否则丢弃该请求
 * </li>
 * </ol>
 * <p>
 * 整个过程如下图所示:
 * </p>
 *
 * <p>
 * <img src="../../../../../../assets/leak-bucket-rate-limiter.png">
 * </p>
 *
 * <p>
 * 漏桶限流的缺点是在处理大量突发并发时会被限流
 * </p>
 */
public class LeakBucketRateLimiter implements RateLimiter {
    // 桶整体容量
    private final int capacity;
    // 漏水的速率
    private final int rate;

    // 当前水位
    private long waterLevel;
    // 前一次计算漏水的实际
    private long lastLeakTimestamp;

    /**
     * 构造器, 实例化限流器对象
     *
     * @param capacity 桶整体容量
     * @param rate     漏水的速率
     */
    public LeakBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.waterLevel = 0;
        this.lastLeakTimestamp = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        // 计算漏水
        leak();

        // 看所需的请求数是否超出桶的总容量
        if (waterLevel + permits <= capacity) {
            // 将新增的请求添加到桶中
            waterLevel += permits;
            return true;
        }

        return false;
    }

    /**
     * 计算桶漏水
     */
    private void leak() {
        // 计算当前时间戳
        var nowMills = System.currentTimeMillis();

        // 计算该段时间内漏出的水
        var leakedWater = (nowMills - lastLeakTimestamp) * rate / 1000L;
        if (leakedWater > 0) {
            // 计算漏完水后, 桶的容量
            waterLevel = Math.max(0, waterLevel - leakedWater);
            lastLeakTimestamp = nowMills;
        }
    }
}
