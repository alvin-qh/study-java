package alvin.study.se.binary;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Hex} 类型, 将 16 进制字符串和 {@code byte} 数组相互转换
 */
class HexTest {
    /**
     * 测试 {@code Hex.toBytes(...)} 方法, 将 16 进制字符串转为字节数组
     */
    @Test
    void toBytes_shouldConvertHexStringToByteArray() {
        var hex = "0A1B2C";

        // 将字符串全部转为字节数组
        var bytes = Hex.toBytes(hex);
        then(bytes).containsExactly(0x0A, 0x1B, 0x2C);

        // 将字符串部分转为字节数组
        bytes = Hex.toBytes(hex, 0, 2);
        then(bytes).containsExactly(0x0A);

        bytes = Hex.toBytes(hex, 0, 4);
        then(bytes).containsExactly(0x0A, 0x1B);

        bytes = Hex.toBytes(hex, 2, 4);
        then(bytes).containsExactly(0x1B, 0x2C);
    }

    /**
     * 测试 {@code Hex.toString(...)} 方法, 将字节数组转为 16 进制字符串
     */
    @Test
    void toBytes_shouldConvertByteArrayToHexString() {
        var data = new byte[]{ 0x12, 0x34, 0x56, 0x78, (byte) 0x90 };

        // 转换单一字节为 16 进制字符串
        then(Hex.toString(data[0])).isEqualTo("12");

        // 将字节数组全部转为 16 进制字符串
        then(Hex.toString(data)).isEqualTo("1234567890");

        // 将字节数组部分转为 16 进制字符串
        then(Hex.toString(data, 1, 1)).isEqualTo("34");
        then(Hex.toString(data, 2, 2)).isEqualTo("5678");
        then(Hex.toString(data, 3)).isEqualTo("7890");

        // 将 short 数据转为 16 进制字符串
        then(Hex.toString((short) 0x1234)).isEqualTo("1234");

        // 将 int 数据转为 16 进制字符串
        then(Hex.toString(0x12345678)).isEqualTo("12345678");

        // 将 long 数据转为 16 进制字符串
        then(Hex.toString(0x1234567890ABCDEFL)).isEqualTo("1234567890ABCDEF");
    }

    /**
     * 测试 {@code Hex.toStringWithWhiteSpace(...)} 方法, 将字节数组转为 16 进制字符串且每两个字符用空格分隔
     */
    @Test
    void toStringWithWhiteSpace_shouldConvertBytesArrayToHexString() {
        var data = new byte[]{ (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

        // 将字节数组全部转为 16 进制字符串
        then(Hex.toStringWithWhiteSpace(data)).isEqualTo("AB CD EF");

        // 将字节数组部分转为 16 进制字符串
        then(Hex.toStringWithWhiteSpace(data, 1)).isEqualTo("CD EF");
        then(Hex.toStringWithWhiteSpace(data, 2, 1)).isEqualTo("EF");
        then(Hex.toStringWithWhiteSpace(data, 1, 2)).isEqualTo("CD EF");
    }

    /**
     * 测试 {@code Hex.dump(...)} 方法, 将字节数组转为内存转储字符串
     */
    @Test
    void dump_shouldDumpBytesArrayToMemoryView() {
        // 讲一段文本转为字节数组
        var data = "This is memory dump testing, should show the memory view".getBytes(Charsets.US_ASCII);

        // 将字节数组转为内存转储字符串
        then(Hex.dump(data)).isEqualTo("""
            0x00000000 54 68 69 73 20 69 73 20 6D 65 6D 6F 72 79 20 64 This is memory d
            0x00000010 75 6D 70 20 74 65 73 74 69 6E 67 2C 20 73 68 6F ump testing, sho
            0x00000020 75 6C 64 20 73 68 6F 77 20 74 68 65 20 6D 65 6D uld show the mem
            0x00000030 6F 72 79 20 76 69 65 77                         ory view""");
    }
}
