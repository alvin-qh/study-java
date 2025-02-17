package alvin.study.se.binary;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

import java.nio.charset.StandardCharsets;

/**
 * 测试 {@link CRC16} 类型, 验证 CRC16 验证码计算
 */
class CRC16Test {
    /**
     * 测试 {@link CRC16.AlgorithmMode#TABLE_LOOKUP} 方式计算 CRC16 值
     */
    @Test
    void tableLookup_shouldGenerateCRCValue() {
        // 原数据
        var data = "HelloWorld".getBytes(StandardCharsets.UTF_8); // 对应的 16 机制为 48 65 6C 6C 6F 57 6F 72 6C 64

        var crc = new CRC16();

        // 分批计算
        crc.update(data[0]);
        crc.update(data, 1, 3);

        // 计算结果
        var result = crc.doFinal(data, 4);
        then(Hex.toString((short) result)).isEqualTo("6053");
    }

    /**
     * 测试 {@link CRC16.AlgorithmMode#XOR} 方式计算 CRC16 值
     */
    @Test
    void xor_shouldGenerateCRCValue() {
        // 原数据
        var data = "HelloWorld".getBytes(StandardCharsets.UTF_8); // 对应的 16 机制为 48 65 6C 6C 6F 57 6F 72 6C 64

        var crc = new CRC16(CRC16.AlgorithmMode.XOR);

        // 分批计算
        crc.update(data[0]);
        crc.update(data, 1, 3);

        // 计算结果
        var result = crc.doFinal(data, 4);
        then(Hex.toString((short) result)).isEqualTo("6053");
    }
}
