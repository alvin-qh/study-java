package alvin.study.se.binary;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

import java.nio.charset.StandardCharsets;

/**
 * 测试 {@link Bytes} 类操作 byte 流
 */
class BytesTest {
    /**
     * 测试 {@link Bytes#copy(byte[], byte[])} 方法, 且目标数组为空或不存在
     */
    @Test
    void copy_shouldCopyBytesToEmptyByteArray() {
        // 用来拷贝的原始数组
        var src = new byte[] { 0x1, 0x2, 0x3, 0x4, 0x5 };

        // 将原数组拷贝到长度为 0 的目标数组
        var result = Bytes.copy(new byte[0], src);
        // 确认产生新数组并完成拷贝
        then(result).isEqualTo(src);

        // 将原数组拷贝到不存在的目标数组
        result = Bytes.copy(null, src);
        // 确认产生新数组并完成拷贝
        then(result).isEqualTo(src);
    }

    /**
     * 测试 {@link Bytes#copy(byte[], byte[])} 方法, 且目标数组为空或不存在
     */
    @Test
    void copy_shouldCopyBytesToByteArray() {
        // 用来拷贝的原始数组
        var src = new byte[] { 0x1, 0x2, 0x3, 0x4, 0x5 };

        // 目标数组, 长度比原数组多 1
        var dist = new byte[src.length + 1];

        // 将原数组拷贝到目标数组
        dist = Bytes.copy(dist, src);
        // 确认完成拷贝
        then(dist).containsExactly(0x1, 0x2, 0x3, 0x4, 0x5, 0x0);
    }

    /**
     * 测试 {@link Bytes#fill(byte[], int, byte...)} 方法, 且目标数组为空或不存在
     */
    @Test
    void fill_shouldFillBytesToEmptyByteArray() {
        // 将 3 个字节填充到一个空数组中
        var result = Bytes.fill(new byte[0], 0, (byte) 0x1, (byte) 0x2, (byte) 0x3);
        // 确认产生新数组并完成填充
        then(result).containsExactly(0x1, 0x2, 0x3);

        // 将 3 个字节填充到一个不存在的数组中
        result = Bytes.fill(null, 2, (byte) 0x1, (byte) 0x2, (byte) 0x3);
        // 确认产生新数组并完成填充
        then(result).containsExactly(0x0, 0x0, 0x1, 0x2, 0x3);
    }

    /**
     * 测试 {@link Bytes#fill(byte[], int, byte...)} 方法
     */
    @Test
    void fill_shouldFillBytesToByteArray() {
        // 要被填充的目标数组
        var dist = new byte[] { 0x10, 0x11, 0x12, 0x13 };

        // 将 3 个字节填充到原数组的指定位置上
        dist = Bytes.fill(dist, 1, (byte) 0x1, (byte) 0x2, (byte) 0x3);
        // 确认填充结果
        then(dist).containsExactly(0x10, 0x1, 0x2, 0x3);
    }

    /**
     * 测试 {@link Bytes#fillShort(byte[], int, short)} 方法
     */
    @Test
    void fillShort_shouldFillShortIntoByteArray() {
        // 定义 short 整数值
        var value = (short) 0x1234;
        // 获取其高低位, 确认高低位计算正确
        then(Bytes.sHi(value)).isEqualTo(0x12);
        then(Bytes.sLo(value)).isEqualTo(0x34);

        // 将 short 值填充到长度为 2 的 byte 数组中
        var result = Bytes.fillShort(new byte[2], 0, value);
        // 确认填充结果正确
        then(result).containsExactly(0x12, 0x34);
    }

    /**
     * 测试 {@link Bytes#fillInt(byte[], int, int)} 方法
     */
    @Test
    void fillInt_shouldFillIntIntoByteArray() {
        // 定义 int 整数值
        var value = 0x12345678;
        // 获取其高低位, 确认高低位计算正确
        then(Bytes.nHi(value)).isEqualTo(0x1234);
        then(Bytes.nLo(value)).isEqualTo(0x5678);

        // 将 int 值填充到长度为 4 的 byte 数组中
        var result = Bytes.fillInt(new byte[4], 0, value);
        // 确认填充结果正确
        then(result).containsExactly(0x12, 0x34, 0x56, 0x78);
    }

    /**
     * 测试 {@link Bytes#fillLong(byte[], int, long)} 方法
     */
    @Test
    void fillLong_shouldFillLongIntoByteArray() {
        // 定义 long 整数值
        var value = 0x1234567890ABCDEFL;
        // 获取其高低位, 确认高低位计算正确
        then(Bytes.lHi(value)).isEqualTo(0x12345678);
        then(Bytes.lLo(value)).isEqualTo(0x90ABCDEF);

        // 将 long 值填充到长度为 8 的 byte 数组中
        var result = Bytes.fillLong(new byte[8], 0, value);
        // 确认填充结果正确
        then(result).containsExactly(0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
    }

    /**
     * 测试 {@link Bytes#concat(byte[], int, byte[]...)} 方法, 将多个 {@code byte} 数组连接成一个
     */
    @Test
    void concat_shouldConcatBytesIntoByteArray() {
        // 要被连接的目标数组
        var dist = new byte[] { 0x1, 0x2, 0x3 };

        // 将 3 个字节连接到原 byte 数组指定位置
        dist = Bytes.concat(dist, 1, new byte[] { 0x10, 0x11, 0x12 });
        // 确认连接结果
        then(dist).containsExactly(0x1, 0x10, 0x11, 0x12);

        // 在原数组基础上, 将 7 个字节连接到原数组末尾
        dist = Bytes.concat(dist,
                dist.length,
                new byte[] { 0x20, 0x21, 0x22 },
                new byte[] { 0x30, 0x31, 0x32, 0x33 });
        // 确认连接结果
        then(dist).containsExactly(
                0x1, 0x10, 0x11, 0x12,
                0x20, 0x21, 0x22, 0x30,
                0x31, 0x32, 0x33);
    }

    /**
     * 测试 {@link Bytes#append(byte[], byte...)} 方法, 将多个 {@code byte} 数组追加到指定数组末尾
     */
    @Test
    void append_shouldAppendBytesIntoByteArray() {
        // 要被追加的目标数组
        var dist = new byte[] {
                0x0, 0x0, 0x0, 0x0
        };

        // 将 2 个字节追加到原 byte 数组末尾
        dist = Bytes.append(dist, new byte[] { 0x10, 0x11 });
        // 确认追加结果
        then(dist).containsExactly(0x0, 0x0, 0x0, 0x0, 0x10, 0x11);
    }

    /**
     * 测试 {@link Bytes#appendShort(byte[], short)} 方法, 将 {@code short} 整数追加到指定数组末尾
     */
    @Test
    void appendShort_shouldAppendShortIntoBytesArray() {
        // 要被追加的目标数组
        var dist = new byte[] { 0x0, 0x0, 0x0, 0x0 };

        // 将 short 值追加到原 byte 数组末尾
        dist = Bytes.appendShort(dist, (short) 0x1234);
        // 确认追加结果
        then(dist).containsExactly(0x0, 0x0, 0x0, 0x0, 0x12, 0x34);
    }

    /**
     * 测试 {@link Bytes#appendInt(byte[], int)} 方法, 将 {@code int} 整数追加到指定数组末尾
     */
    @Test
    void appendInt_shouldAppendIntIntoBytesArray() {
        // 要被追加的目标数组
        var dist = new byte[] { 0x0, 0x0, 0x0, 0x0 };

        // 将 int 值追加到原 byte 数组末尾
        dist = Bytes.appendInt(dist, 0x12345678);
        // 确认追加结果
        then(dist).containsExactly(0x0, 0x0, 0x0, 0x0, 0x12, 0x34, 0x56, 0x78);
    }

    /**
     * 测试 {@link Bytes#appendLong(byte[], long)} 方法, 将 {@code long} 整数追加到指定数组末尾
     */
    @Test
    void shouldAppendLongIntoArray() {
        // 要被追加的目标数组
        var dist = new byte[] { 0x0, 0x0, 0x0, 0x0 };

        // 将 long 值追加到原 byte 数组末尾
        var result = Bytes.appendLong(dist, 0x1234567890ABCDEFL);
        // 确认追加结果
        then(result).containsExactly(
                0x0, 0x0, 0x0, 0x0, 0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
    }

    /**
     * 测试 {@link Bytes#toBytes(byte)}, 将 {@code byte} 值转为数组
     */
    @Test
    void toBytes_shouldConvertByteToBytesArray() {
        then(Bytes.toBytes((byte) 0x01)).containsExactly(0x01);
    }

    /**
     * 测试 {@link Bytes#toBytes(short)}, 将 {@code short} 值转为数组
     */
    @Test
    void toBytes_shouldConvertShortToBytesArray() {
        then(Bytes.toBytes((short) 0x1234)).containsExactly(0x12, 0x34);
    }

    /**
     * 测试 {@link Bytes#toBytes(int)}, 将 {@code int} 值转为数组
     */
    @Test
    void toBytes_shouldConvertIntToBytesArray() {
        then(Bytes.toBytes(0x12345678)).containsExactly(0x12, 0x34, 0x56, 0x78);
    }

    /**
     * 测试 {@link Bytes#toBytes(long)}, 将 {@code long} 值转为数组
     */
    @Test
    void toBytes_shouldConvertLongToBytesArray() {
        then(Bytes.toBytes(0x1234567890ABCDEFL)).containsExactly(
                0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
    }

    /**
     * 测试 {@link Bytes#toShort(byte, byte)}, {@link Bytes#toShort(byte[])} 以及
     * {@link Bytes#toShort(byte[], int)} 方法, 将 2 个 {@code byte} 值转为 {@code short}
     * 整数值
     */
    @Test
    void toShort_shouldBytesToShort() {
        then(Bytes.toShort((byte) 0x12, (byte) 0x34)).isEqualTo(0x1234);
        then(Bytes.toShort(new byte[] { 0x12, 0x34 })).isEqualTo(0x1234);
        then(Bytes.toShort(new byte[] { 0x0, 0x0, 0x0, 0x12, 0x34 }, 3)).isEqualTo(0x1234);
    }

    /**
     * 测试 {@link Bytes#toInt(byte, byte, byte, byte)}, {@link Bytes#toInt(byte[])}
     * 以及 {@link Bytes#toInt(byte[], int)} 方法, 将 4 个 {@code byte} 值转为 {@code int}
     * 整数值
     */
    @Test
    void toInt_shouldBytesToInt() {
        then(Bytes.toInt((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78)).isEqualTo(0x12345678);
        then(Bytes.toInt(new byte[] { 0x12, 0x34, 0x56, 0x78 })).isEqualTo(0x12345678);
        then(Bytes.toInt(new byte[] { 0x0, 0x0, 0x0, 0x12, 0x34, 0x56, 0x78 }, 3)).isEqualTo(0x12345678);
    }

    /**
     * 测试 {@link Bytes#toLong(byte, byte, byte, byte, byte, byte, byte, byte)},
     * {@link Bytes#toLong(byte[])} 以及 {@link Bytes#toLong(byte[], int)} 方法, 将 8 个
     * {@code byte} 值转为 {@code long} 整数值
     */
    @Test
    void toLong_shouldBytesToLong() {
        then(Bytes.toLong(
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x56,
                (byte) 0x78,
                (byte) 0x90,
                (byte) 0xAB,
                (byte) 0xCD,
                (byte) 0xEF)).isEqualTo(0x1234567890ABCDEFL);

        then(Bytes.toLong(new byte[] {
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x56,
                (byte) 0x78,
                (byte) 0x90,
                (byte) 0xAB,
                (byte) 0xCD,
                (byte) 0xEF
        })).isEqualTo(0x1234567890ABCDEFL);

        then(Bytes.toLong(new byte[] {
                (byte) 0x0,
                (byte) 0x0,
                (byte) 0x0,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x56,
                (byte) 0x78,
                (byte) 0x90,
                (byte) 0xAB,
                (byte) 0xCD,
                (byte) 0xEF
        }, 3)).isEqualTo(0x1234567890ABCDEFL);
    }

    /**
     * 测试 {@link Bytes#flipByte(int)}, 互换 {@code byte} 值的高低位
     */
    @Test
    void flipByte_shouldFlipByteValue() {
        then(Bytes.flipByte(0xAB)).isEqualTo(0xBA);
    }

    /**
     * 测试 {@link Bytes#flipShort(int)}, 互换 {@code short} 值的高低位
     */
    @Test
    void flipShort_shouldFlipShortValue() {
        then(Bytes.flipShort(0x1234)).isEqualTo(0x3412);
    }

    /**
     * 测试 {@link Bytes#flipInt(int)}, 互换 {@code int} 值的高低位
     */
    @Test
    void flipInt_shouldFlipIntValue() {
        then(Bytes.flipInt(0x12345678)).isEqualTo(0x56781234);
    }

    /**
     * 测试 {@link Bytes#flipLong(long)}, 互换 {@code long} 值的高低位
     */
    @Test
    void flipLong_shouldFlipLongValue() {
        then(Bytes.flipLong(0x1234567890ABCDEFL)).isEqualTo(0x90ABCDEF12345678L);
    }

    /**
     * 测试 {@link Bytes#byteToInt(int)}, 将 {@code byte} 值转为 {@code int} 值
     */
    @Test
    void byteToInt_shouldConvertByteToInt() {
        then(Bytes.byteToInt(0xFF)).isEqualTo(255);
    }

    /**
     * 测试 {@link Bytes#fromHex(char)} 方法
     */
    @Test
    void fromHex_shouldFromHexCharToByteValue() {
        // 确认 16 进制字符转为的整数表意正确
        then(Bytes.fromHex('A')).isEqualTo(0x0A);
        then(Bytes.fromHex('b')).isEqualTo(0x0B);
        then(Bytes.fromHex('6')).isEqualTo(0x06);
    }

    /**
     * 测试 {@link Bytes#compare(byte[], int, byte[], int, int)} 方法, 比较两个字节数组
     */
    @Test
    void compare_shouldCompareTwoArrays() {
        var b1 = "Hello".getBytes(StandardCharsets.UTF_8);
        var b2 = "Hello".getBytes(StandardCharsets.UTF_8);
        then(Bytes.compare(b1, 0, b2, 0, b1.length)).isTrue();
        then(Bytes.compare(b1, 1, b2, 1, b1.length)).isTrue();
        then(Bytes.compare(b1, 2, b2, 3, 1)).isTrue();
        then(Bytes.compare(b1, 1, b2, 2, b1.length)).isFalse();

        b2 = "HHello".getBytes(StandardCharsets.UTF_8);
        then(Bytes.compare(b1, 1, b2, 2, b2.length)).isTrue();
    }
}
