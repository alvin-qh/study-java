package alvin.study.se.binary;

import static java.lang.Character.digit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 二进制转 16 进制字符串工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Hex {
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    /**
     * 将 16 进制字符串转为 {@code byte} 数组
     *
     * @param s      16 进展字符串
     * @param offset 字符串偏移量, 从该位置开始计算
     * @param length 需转换的字符串长度, 即从 {@code offset} 开始计算的长度, 必须为 2 的倍数
     * @return 转换后的 {@code byte} 数组
     */
    public static byte[] toBytes(String s, int offset, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length");
        }

        // 计算结束下标值
        var lastIndex = Math.min(offset + length, s.length());

        // 重新计算要转换的字符串长度
        length = lastIndex - offset;
        if (length < 2 || length % 2 != 0) {
            // 如果长度不满足 2 的倍数, 则无法进行转换
            throw new IllegalArgumentException("Invalid HEX string length");
        }

        // 计算结果 byte 数组长度
        var data = new byte[length / 2];
        // 逐字符进行转换
        for (var i = 0; i < length; i += 2) {
            var hi = (digit(s.charAt(offset + i), 16) << 4) & 0xF0;
            var lo = digit(s.charAt(offset + i + 1), 16) & 0xF;
            // 保存转换结果
            data[i / 2] = (byte) (hi | lo);
        }
        return data;
    }

    /**
     * 将 16 进制字符串转为 {@code byte} 数组
     *
     * @param s      16 进展字符串
     * @param offset 字符串偏移量, 从该位置开始计算, 到字符串末尾结束
     * @return 转换后的 {@code byte} 数组
     */
    public static byte[] toBytes(String s, int offset) {
        return toBytes(s, offset, s.length() - offset);
    }

    /**
     * 将 16 进制字符串转为 {@code byte} 数组
     *
     * @param s 16 进展字符串
     * @return 转换后的 {@code byte} 数组
     */
    public static byte[] toBytes(String s) {
        return toBytes(s, 0, s.length());
    }

    /**
     * {@code byte} 数组转 16 进制字符串
     *
     * @param data   {@code byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @param length 长度, 即从偏移量开始, 要转换的 {@code byte} 个数
     * @return 转换后的字符串
     */
    public static String toString(byte[] data, int offset, int length) {
        // 计算结束下标值
        var lastIndex = Math.min(offset + length, data.length);
        // 重新计算可转换的长度
        length = lastIndex - offset;

        // 定义字符数组保存结果, 数组长度是 byte 数的 2 倍
        var buf = new char[length * 2];
        for (var i = 0; i < length; i++) {
            // 获取要转换的 byte
            var b = data[offset + i];

            // 一个 byte 转换为 2 个字符
            buf[i * 2] = HEX_DIGITS[(b >>> 4) & 0xF];
            buf[i * 2 + 1] = HEX_DIGITS[b & 0xF];
        }
        return new String(buf);
    }

    /**
     * {@code byte} 数组转 16 进制字符串
     *
     * @param data   {@code byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @return 转换后的字符串
     */
    public static String toString(byte[] data, int offset) {
        return toString(data, offset, data.length - offset);
    }

    /**
     * {@code byte} 数组转 16 进制字符串
     *
     * @param data {@code byte} 数组
     * @return 转换后的字符串
     */
    public static String toString(byte[] data) {
        return toString(data, 0, data.length);
    }

    /**
     * 单个 {@code byte} 值转 16 进制字符串
     *
     * @param b 单个 {@code byte} 值
     * @return 转换后的字符串
     */
    public static String toString(byte b) {
        return toString(Bytes.toBytes(b));
    }

    /**
     * 单个 {@code short} 值转 16 进制字符串
     *
     * @param i 单个 {@code short} 值
     * @return 转换后的字符串
     */
    public static String toString(short i) {
        return toString(Bytes.toBytes(i));
    }

    /**
     * 单个 {@code int} 值转 16 进制字符串
     *
     * @param i 单个 {@code int} 值
     * @return 转换后的字符串
     */
    public static String toString(int i) {
        return toString(Bytes.toBytes(i));
    }

    /**
     * 单个 {@code long} 值转 16 进制字符串
     *
     * @param i 单个 {@code long} 值
     * @return 转换后的字符串
     */
    public static String toString(long i) {
        return toString(Bytes.toBytes(i));
    }

    /**
     * 将 {@code byte} 数组转为 16 进制字符串, 且每两个字符用空格分隔
     *
     * @param data 要转换的 {@link byte} 数组
     * @return 转换后的字符串
     */
    public static String toStringWithWhiteSpace(byte[] data) {
        return toStringWithWhiteSpace(data, 0, data.length);
    }

    /**
     * 将 {@code byte} 数组转为 16 进制字符串, 且每两个字符用空格分隔
     *
     * @param data   要转换的 {@link byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @return 转换后的字符串
     */
    public static String toStringWithWhiteSpace(byte[] data, int offset) {
        return toStringWithWhiteSpace(data, offset, data.length - offset);
    }

    /**
     * 将 {@code byte} 数组转为 16 进制字符串, 且每两个字符用空格分隔
     *
     * @param data   要转换的 {@link byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @param length 要转换的数据长度
     * @return 转换后的字符串
     */
    public static String toStringWithWhiteSpace(byte[] data, int offset, int length) {
        var lastIndex = Math.min(offset + length, data.length);

        length = lastIndex - offset;
        var buf = new char[length * 3];

        for (var i = 0; i < length; i++) {
            var b = data[offset + i];
            buf[i * 3] = HEX_DIGITS[(b >>> 4) & 0xF];
            buf[i * 3 + 1] = HEX_DIGITS[b & 0xF];
            buf[i * 3 + 2] = ' ';
        }
        return new String(buf, 0, buf.length - 1);
    }

    /**
     * 将 {@code byte} 数组转为内存转储字符串格式
     *
     * <p>
     * 转储字符串的格式如下:
     *
     * <pre>
     * 0x00000000 54 68 69 73 20 69 73 20 6D 65 6D 6F 72 79 20 64 This is memory d
     * 0x00000010 75 6D 70 20 74 65 73 74 69 6E 67 2C 20 73 68 6F ump testing, sho
     * ^^^^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^
     *  行偏移量               每行数据的 16 进制字符串                   原始字符
     * </pre>
     * <p>
     * 对于非可见字符, 则使用 {@code '.'} 字符替代
     * </p>
     *
     * @param data 要转换的 {@code byte} 数组
     * @return 内存转储字符串
     */
    public static String dump(byte[] data) {
        return dump(data, 0, data.length);
    }

    /**
     * 将 {@code byte} 数组转为内存转储字符串格式
     *
     * <p>
     * 转储字符串的格式如下:
     *
     * <pre>
     * 0x00000000 54 68 69 73 20 69 73 20 6D 65 6D 6F 72 79 20 64 This is memory d
     * 0x00000010 75 6D 70 20 74 65 73 74 69 6E 67 2C 20 73 68 6F ump testing, sho
     * ^^^^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^
     *  行偏移量               每行数据的 16 进制字符串                   原始字符
     * </pre>
     * <p>
     * 对于非可见字符, 则使用 {@code '.'} 字符替代
     * </p>
     *
     * @param data   要转换的 {@code byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @return 内存转储字符串
     */
    public static String dump(byte[] data, int offset) {
        return dump(data, offset, data.length - offset);
    }

    /**
     * 将 {@code byte} 数组转为内存转储字符串格式
     *
     * <p>
     * 转储字符串的格式如下:
     *
     * <pre>
     * 0x00000000 54 68 69 73 20 69 73 20 6D 65 6D 6F 72 79 20 64 This is memory d
     * 0x00000010 75 6D 70 20 74 65 73 74 69 6E 67 2C 20 73 68 6F ump testing, sho
     * ^^^^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^
     *  行偏移量               每行数据的 16 进制字符串                   原始字符
     * </pre>
     * <p>
     * 对于非可见字符, 则使用 {@code '.'} 字符替代
     * </p>
     *
     * @param data   要转换的 {@code byte} 数组
     * @param offset 数组偏移量, 即从该位置开始转换
     * @param length 要转换的数据长度
     * @return 内存转储字符串
     */
    public static String dump(byte[] data, int offset, int length) {
        if (data == null || data.length == 0) {
            return "";
        }

        // 计算真实的转换数据长度
        length = Math.min(offset + length, data.length) - offset;

        // 存放结果的字符串缓冲
        var result = new StringBuilder();

        // 行缓存, 用于写入每行最后的 ASCII 字符串
        var line = new byte[16];
        // 每行写入的字节数
        var lineIndex = 0;

        // 写入第一行的偏移量
        result.append("0x").append(toString(0));

        // 遍历所要转换的字节
        for (var i = 0; i < length; i++) {
            // 如果一行 16 个字节已经写满, 则准备写入最后的 ASCII 字符串
            if (lineIndex == 16) {
                // 写入一个空格, 分隔 16 进制字符串和后续的 ASCII 字符串
                result.append(' ');

                // 逐 byte 写入 ASCII 字符
                for (var j = 0; j < 16; j++) {
                    // 判断是否为可见字符
                    if (line[j] >= (byte) ' ' && line[j] <= (byte) '~') {
                        // 写入可见字符
                        result.append((char) line[j]);
                    } else {
                        // 对于不可见字符, 用 '.' 字符代替
                        result.append('.');
                    }
                }

                if (i + 1 < length) {
                    // 写入下一行的行偏移量
                    result.append("\n0x").append(toString(i));
                }
                lineIndex = 0;
            }

            // 写入 byte 值对应的 16 进制字符
            var b = data[offset + i];
            result.append(' ')
                    .append(HEX_DIGITS[(b >>> 4) & 0xF])
                    .append(HEX_DIGITS[b & 0xF]);

            // 将字符放入行
            line[lineIndex++] = b;
        }

        // 对剩余不足一行的数据进行处理
        if (lineIndex != 16) {
            var count = (16 - lineIndex) * 3;
            count++;

            for (var i = 0; i < count; i++) {
                result.append(' ');
            }

            for (var i = 0; i < lineIndex; i++) {
                if (line[i] >= (byte) ' ' && line[i] <= (byte) '~') {
                    result.append((char) line[i]);
                } else {
                    result.append('.');
                }
            }
        }

        return result.toString();
    }
}
