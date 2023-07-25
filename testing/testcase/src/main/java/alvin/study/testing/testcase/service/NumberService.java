package alvin.study.testing.testcase.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用于测试的服务类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberService {
    /**
     * 判断一个数字是否为奇数
     *
     * @param number 整数数字
     * @return 是否为奇数
     */
    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }
}
