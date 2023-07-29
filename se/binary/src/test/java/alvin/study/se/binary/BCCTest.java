package alvin.study.se.binary;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link BCC} 类, 计算异或校验码
 */
class BCCTest {
    /**
     * 测试通过 {@link BCC#doFinal(byte[])} 方法计算累加和校验码
     */
    @Test
    void bcc_shouldCalculateChecksumByDoFinal() {
        var data = new byte[]{ 0x01, (byte) 0xA0, 0x7C, (byte) 0xFF, 0x02 };

        var bcc = new BCC();
        then(bcc.doFinal(data)).isEqualTo(0x20);
    }

    /**
     * 测试通过 {@link BCC#update(byte[])} 和 {@link Checksum#doFinal()} 方法计算累加和校验码
     */
    @Test
    void bcc_shouldCalculateChecksumByUpdateAndDoFinal() {
        var bcc = new BCC();

        var data1 = new byte[]{ 0x01, (byte) 0xA0, 0x7C };
        var data2 = new byte[]{ (byte) 0xFF, 0x02 };

        bcc.update(data1);
        then(bcc.doFinal(data2)).isEqualTo(0x20);
    }

    /**
     * 测试通过 {@link BCC#update(byte[], int, int)} 和 {@link BCC#doFinal(byte[], int)}
     * 方法计算累加和校验码
     */
    @Test
    void bcc_shouldCalculateChecksumByUpdateAndDoFinalWithPos() {
        var data = new byte[]{ 0x01, (byte) 0xA0, 0x7C, (byte) 0xFF, 0x02 };

        var bcc = new BCC();

        bcc.update(data, 0, 1);
        bcc.update(data, 1, 2);
        bcc.update(data, 3, 2);
        then(bcc.doFinal()).isEqualTo(0x20);
    }
}
