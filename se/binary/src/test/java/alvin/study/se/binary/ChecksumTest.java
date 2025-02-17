package alvin.study.se.binary;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Checksum} 类, 计算累加和校验码
 */
class ChecksumTest {
    /**
     * 测试通过 {@link Checksum#doFinal(byte[])} 方法计算累加和校验码
     */
    @Test
    void checksum_shouldCalculateChecksumByDoFinal() {
        // 计算累加结果不超过 0xFF 的情况
        var data = new byte[] { (byte) 0xB5, 0x42, (byte) 0xE2, (byte) 0xC7 };
        var checksum = new Checksum();
        then(checksum.doFinal(data)).isEqualTo(0x60);

        // 计算累加结果超过 0xFF 的情况
        data = new byte[] { (byte) 0xB5, 0x42, (byte) 0xE2, (byte) 0xC7, (byte) 0xF8, (byte) 0x90 };
        checksum = new Checksum();
        then(checksum.doFinal(data)).isEqualTo(0xD8);
    }

    /**
     * 测试通过 {@link Checksum#update(byte[])} 和 {@link Checksum#doFinal()} 方法计算累加和校验码
     */
    @Test
    void checksum_shouldCalculateChecksumByUpdateAndDoFinal() {
        var checksum = new Checksum();

        var data1 = new byte[] { (byte) 0xB5, 0x42 };
        var data2 = new byte[] { (byte) 0xE2, (byte) 0xC7 };

        // 计算累加结果不超过 0xFF 的情况
        checksum.update(data1);
        checksum.update(data2);
        then(checksum.doFinal()).isEqualTo(0x60);

        // 计算累加结果超过 0xFF 的情况
        var data3 = new byte[] { (byte) 0xF8, (byte) 0x90 };
        then(checksum.doFinal(data3)).isEqualTo(0xD8);
    }

    /**
     * 测试通过 {@link Checksum#update(byte[], int, int)} 和
     * {@link Checksum#doFinal(byte[], int)} 方法计算累加和校验码
     */
    @Test
    void checksum_shouldCalculateChecksumByUpdateAndDoFinalWithPos() {
        var data1 = new byte[] { (byte) 0xB5, 0x42, (byte) 0xE2, (byte) 0xC7 };
        var data2 = new byte[] { (byte) 0xF8, (byte) 0x90 };

        var checksum = new Checksum();

        // 计算累加结果不超过 0xFF 的情况
        checksum.update(data1, 0, 1);
        checksum.update(data1, 1, 2);
        checksum.update(data1, 3);
        then(checksum.doFinal()).isEqualTo(0x60);

        // 计算累加结果超过 0xFF 的情况
        checksum.update(data2, 0, 1);
        then(checksum.doFinal(data2, 1)).isEqualTo(0xD8);
    }
}
