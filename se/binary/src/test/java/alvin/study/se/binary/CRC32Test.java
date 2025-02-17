package alvin.study.se.binary;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link CRC32} 类型, 验证 CRC32 验证码计算
 */
class CRC32Test {
    /**
     * 测试 {@code CRC32.update(...)} 方法和 {@link CRC32#getValue()} 计算 CRC32 校验码
     */
    @Test
    void crc32_shouldGenerateCRCValue() {
        // 原数据
        var data = "HelloWorld".getBytes(StandardCharsets.UTF_8); // 对应的 16 机制为 48 65 6C 6C 6F 57 6F 72 6C 64

        var crc = new CRC32();

        // 分批计算
        crc.update(data[0]);
        crc.update(data, 1, 3);
        crc.update(data, 4, data.length - 4);

        // 计算结果
        var result = crc.getValue();
        then(Hex.toString((int) result)).isEqualTo("77770C79");
    }
}
