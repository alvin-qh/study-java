package alvin.study.future;

import lombok.Getter;

/**
 * 用于测试并发计算的计数器类型
 */
@Getter
public class Counter {
    // 计算值
    private volatile long count = 0;

    /**
     * 将计数值增加 {@code 1}
     */
    @SuppressWarnings("java:S3078")
    public void inc() {
        this.count++;
    }
}
