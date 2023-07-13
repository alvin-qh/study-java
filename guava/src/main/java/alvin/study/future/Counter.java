package alvin.study.future;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于测试并发计算的计数器类型
 */
@Getter
public class Counter {
    // 计算值
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * 将计数值增加 {@code 1}
     */
    public void inc() {
        count.incrementAndGet();
    }
}
