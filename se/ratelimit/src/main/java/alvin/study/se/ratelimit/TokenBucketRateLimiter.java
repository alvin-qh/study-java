package alvin.study.se.ratelimit;

/**
 * 令牌桶限流
 *
 * <p>
 * 令牌桶限流算法的原理如下:
 * <ol>
 * <li>
 * 设定一个可以存储固定数量"令牌"的桶, 令牌数量代表当前系统可处理的请求数上限
 * </li>
 * <li>
 * 当请求到来时, 尝试从桶中获取一个令牌, 如果能获取到, 则允许请求, 否则请求被拒绝
 * </li>
 * <li>
 * 按照一个固定的频率向桶中不断放入令牌, 直到放满为止
 * </li>
 * </ol>
 * <p>
 * 整个过程如下图所示:
 * </p>
 *
 * <p>
 * <img src="../../../../../../assets/token-bucket-rate-limiter.png">
 * </p>
 */
public class TokenBucketRateLimiter implements RateLimiter {
    // 令牌桶的总容量
    private final int capacity;
    // 令牌生成的速率 (每秒)
    private final int rate;

    // 当前生成的令牌数量
    private int tokens;
    // 上次生成令牌的时间戳
    private long lastRefillTimestamp;

    /**
     * 构造器, 实例化限流器对象
     *
     * @param capacity 令牌桶的总容量
     * @param rate     令牌生成的速率 (每秒)
     */
    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    @Override
    public boolean tryAcquire(int permits) {
        // 生成令牌
        refill();

        // 计算消耗的令牌
        if (tokens > permits - 1) {
            tokens -= permits;
            return true;
        }

        return false;
    }

    /**
     * 生成令牌
     */
    private void refill() {
        // 计算当前时间的时间戳
        long nowMills = System.currentTimeMillis();
        if (nowMills > lastRefillTimestamp) {
            // 计算这段时间生成的令牌
            var generatedTokens = (int) ((nowMills - lastRefillTimestamp) / 1000 * rate);
            if (generatedTokens > 0) {
                // 增加生成的 token 数量
                tokens = Math.min(tokens + generatedTokens, capacity);
                lastRefillTimestamp = nowMills;
            }
        }
    }
}
