package alvin.study.se.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

/**
 * 随机数据生成器类型
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataGenerator {
    // 安全随机数对象, 用于产生安全随机数
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 随机生成任意长度的字节数据
     *
     * @param size 数据长度
     * @return 随机字节数据
     */
    public static byte[] generate(int size) {
        var data = new byte[size];
        RANDOM.nextBytes(data);
        return data;
    }
}
