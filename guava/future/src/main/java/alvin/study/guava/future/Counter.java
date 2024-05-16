package alvin.study.guava.future;

import lombok.Getter;

/**
 * 用于测试并发计算的计数器类型
 */
@Getter
public class Counter {
    // 计算值
    private int count;

    /**
     * 将计数值增加 {@code 1}
     */
    public void inc() {
        count++;
    }
}
