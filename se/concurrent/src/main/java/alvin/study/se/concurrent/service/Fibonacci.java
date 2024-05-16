package alvin.study.se.concurrent.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 测试异步任务的斐波那契计算类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Fibonacci {
    /**
     * 计算斐波那契数列
     *
     * @param n 数列的第 {@code n} 项
     * @return 斐波那契数列值
     */
    public static int calculate(int n) {
        if (n < 2) {
            return n;
        }
        return calculate(n - 1) + calculate(n - 2);
    }
}
