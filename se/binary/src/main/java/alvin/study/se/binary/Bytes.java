package alvin.study.se.binary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 字节串操作工具类
 *
 * <p>
 * 通过该工具类可以方便的进行字节串的各类操作
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bytes {
    /**
     * 对字节串进行拷贝
     *
     * <p>
     * 该方法是将 {@code src} 数组从 {@code srcPos} 的位置复制到 {@code dist} 数组从 {@code distPos}
     * 开始的位置上, 复制 {@code Length} 个字节
     * </p>
     *
     * @param dist    目标字节数组, 如果为 {@code null} 或长度小于要复制的字节数, 则创建新数组完成复制
     * @param distPos 目标字节数组的起始索引
     * @param src     源字节数组
     * @param srcPos  原字节数组的起始索引
     * @param length  要复制的字节数
     * @return 复制结果字节数组
     */
    public static byte[] copy(byte[] dist, int distPos, byte[] src, int srcPos, int length) {
        if (src == null || src.length == 0) {
            return dist;
        }

        // 计算要复制的字节总长度
        var totalLength = distPos + length;

        // 判断目标数组是否可以容纳要复制的内容
        if (dist == null || dist.length < totalLength) {
            // 实例化足够长度的新目标数组
            var newDist = new byte[totalLength];
            if (dist != null) {
                // 将原目标数组的内容复制到新目标数组中
                // 调用系统的 arraycopy 函数赋值数组以提高性能
                System.arraycopy(dist, 0, newDist, 0, dist.length);
            }
            // 将目标数组指向新目标数组
            dist = newDist;
        }
        // 将要复制的内容从源数组拷贝到目标数组
        System.arraycopy(src, srcPos, dist, distPos, length);
        return dist;
    }

    /**
     * 对字节串进行拷贝
     *
     * <p>
     * 该方法是将 {@code src} 数组全部复制到 {@code dist} 数组从 {@code distPos} 开始的位置上
     * </p>
     *
     * @param dist    目标字节数组, 如果为 {@code null} 或长度小于要复制的字节数, 则创建新数组完成复制
     * @param distPos 目标字节数组的起始索引
     * @param src     源字节数组
     * @return 复制结果字节数组
     */
    public static byte[] copy(byte[] dist, int distPos, byte[] src) {
        return copy(dist, distPos, src, 0, src.length);
    }

    /**
     * 对字节串进行拷贝
     *
     * <p>
     * 该方法是将 {@code src} 数组全部复制到 {@code dist} 数组中
     * </p>
     *
     * @param dist 目标字节数组, 如果为 {@code null} 或长度小于要复制的字节数, 则创建新数组完成复制
     * @param src  源字节数组
     * @return 复制结果字节数组
     */
    public static byte[] copy(byte[] dist, byte[] src) {
        return copy(dist, 0, src, 0, src.length);
    }

    /**
     * 将指定字节集合填充到目标数组的指定位置中
     *
     * <p>
     * 该方法可以用来拼接字节串, 可以将所需的内容填充到目标数组的所需位置上
     * </p>
     *
     * @param dist    目标字节数组
     * @param distPos 在目标字节数组上填充内容的起始位置
     * @param bytes   要填充的字节数组
     * @return 填充后的数组
     */
    public static byte[] fill(byte[] dist, int distPos, byte... bytes) {
        // 将要填充的字节内容复制到目标数组的指定位置上
        return copy(dist, distPos, bytes, 0, bytes.length);
    }

    /**
     * 获取 {@code short} 整数的低位
     *
     * <p>
     * 例如获取 {@code 0xABCD} 的低位, 结果为 {@code 0xCD} 的整数值
     * </p>
     *
     * @param s {@code short} 类型整数
     * @return {@code s} 参数的低位值
     */
    public static int sLo(int s) {
        return s & 0xFF;
    }

    /**
     * 获取 {@code short} 整数的高位
     *
     * <p>
     * 例如获取 {@code 0xABCD} 的高位, 结果为 {@code 0xAB} 的整数值
     * </p>
     *
     * @param s {@code short} 类型整数
     * @return {@code s} 参数的高位值
     */
    public static int sHi(int s) {
        return (s >> 8) & 0xFF;
    }

    /**
     * 将 {@code short} 整数的 2 个字节填充到 {@code dist} 数组的指定位置上
     *
     * @param dist   目标字节数组
     * @param offset 目标字节数组的偏移量, 从该位置开始填充
     * @param value  要填充的 {@code short} 类型整数值, 2 个字节
     * @return 填充后的字节数组
     */
    public static byte[] fillShort(byte[] dist, int offset, short value) {
        return fill(dist, offset, (byte) sHi(value), (byte) sLo(value));
    }

    /**
     * 获取 {@code int} 整数的低位
     *
     * <p>
     * 例如获取 {@code 0x12345678} 的低位, 结果为 {@code 0x5678} 的整数值
     * </p>
     *
     * @param n {@code int} 类型整数
     * @return 参数 {@code n} 的低位结果
     */
    public static int nLo(int n) {
        return n & 0xFFFF;
    }

    /**
     * 获取 {@code int} 整数的高位
     *
     * <p>
     * 例如获取 {@code 0x12345678} 的高位, 结果为 {@code 0x1234} 的整数值
     * </p>
     *
     * @param n {@code int} 类型整数
     * @return 参数 {@code n} 的高位结果
     */
    public static int nHi(int n) {
        return (n >> 16) & 0xFFFF;
    }

    /**
     * 将 {@code int} 整数的 4 个字节填充到 {@code dist} 数组的指定位置上
     *
     * @param dist   目标字节数组
     * @param offset 目标字节数组的偏移量, 从该位置开始填充
     * @param value  要填充的 {@code int} 类型整数值, 4 个字节
     * @return 填充后的字节数组
     */
    public static byte[] fillInt(byte[] dist, int offset, int value) {
        // 获取整数的低 4 位
        var l = (short) nLo(value);
        // 获取整数的高 4 位
        var h = (short) nHi(value);

        // 进一步将两个 short 值拆为 4 个 byte 值, 填充到目标数组中
        return fill(dist, offset, (byte) sHi(h), (byte) sLo(h), (byte) sHi(l), (byte) sLo(l));
    }

    /**
     * 获取 {@code long} 整数的低位
     *
     * <p>
     * 例如获取 {@code 0x1234567887654321} 的低位, 结果为 {@code 0x87654321} 的整数值
     * </p>
     *
     * @param l {@code long} 类型整数
     * @return 参数 {@code l} 的低位结果
     */
    public static int lLo(long l) {
        return (int) (l & 0xFFFFFFFFL);
    }

    /**
     * 获取 {@code long} 整数的高位
     *
     * <p>
     * 例如获取 {@code 0x1234567887654321} 的高位, 结果为 {@code 0x12345678} 的整数值
     * </p>
     *
     * @param l {@code long} 类型整数
     * @return 参数 {@code l} 的高位结果
     */
    public static int lHi(long l) {
        return (int) ((l >> 32) & 0xFFFFFFFFL);
    }

    /**
     * 将 {@code long} 整数的 8 个字节填充到 {@code dist} 数组的指定位置上
     *
     * @param dist   目标字节数组
     * @param offset 目标字节数组的偏移量, 从该位置开始填充
     * @param value  要填充的 {@code long} 类型整数值, 8 个字节
     * @return 填充后的字节数组
     */
    public static byte[] fillLong(byte[] dist, int offset, long value) {
        // 将 long 类型的高位拆为 2 个 short 值
        var lh = lHi(value);
        var nhh = nHi(lh);
        var nhl = nLo(lh);

        // 将 long 类型的低位拆为 2 个 short 值
        var ll = lLo(value);
        var nlh = nHi(ll);
        var nll = nLo(ll);

        // 进一步将得到的 4 个 short 值拆为 8 个 byte 并填充到目标数组中
        return fill(
            dist,
            offset,
            (byte) sHi(nhh),
            (byte) sLo(nhh),
            (byte) sHi(nhl),
            (byte) sLo(nhl),
            (byte) sHi(nlh),
            (byte) sLo(nlh),
            (byte) sHi(nll),
            (byte) sLo(nll));
    }

    /**
     * 将若干字节串连接成一个
     *
     * <p>
     * 本方法将 {@code subs} 变量表示的多个字节数组按顺序拼接在 {@code dist} 字节数组以 {@code offset} 起始的位置上,
     * 如果 {@code dist} 数组为 {@code null} 或长度不足以容纳结果, 则会产生新的数组以容纳结果
     * </p>
     *
     * @param dist   目标字节数组
     * @param offset 目标字节数组的偏移量
     * @param subs   一组字节数组
     * @return 拼装后的字节数组
     */
    public static byte[] concat(byte[] dist, int offset, byte[]... subs) {
        if (subs == null) {
            return dist;
        }

        // 按顺序将 subs 表示的多个数组依次复制到目标数组的指定位置上
        for (var sub : subs) {
            dist = copy(dist, offset, sub, 0, sub.length);
            // 没操作一次, offset 的值要加上以复制数组的长度
            offset += sub.length;
        }
        return dist;
    }

    /**
     * 将多个字节流追加到指定字节流的末尾
     *
     * @param dist   目标字节数组
     * @param values 字节数组, 会被追加到目标数组末尾
     * @return 追加后的结果
     */
    public static byte[] append(byte[] dist, byte... values) {
        var offset = 0;
        if (dist != null) {
            offset = dist.length;
        }
        return fill(dist, offset, values);
    }

    /**
     * 将 {@code short} 整数值追加到字节串之后
     *
     * @param dist  目标字节数组
     * @param value {@code short} 整数值
     * @return {@code short} 值追加后的字节数组
     */
    public static byte[] appendShort(byte[] dist, short value) {
        // 将 short 整数拆为 2 个字节追加到目标数组
        return append(dist, (byte) sHi(value), (byte) sLo(value));
    }

    /**
     * 将 {@code int} 整数值追加到字节串之后
     *
     * @param dist  目标字节数组
     * @param value {@code int} 整数值
     * @return {@code int} 值追加后的字节数组
     */
    public static byte[] appendInt(byte[] dist, int value) {
        // 将 int 整数拆为 2 个 short 值
        var l = (short) nLo(value);
        var h = (short) nHi(value);

        // 将 2 个 short 值拆为 4 个 byte 追加到目标数组
        return append(dist, (byte) sHi(h), (byte) sLo(h), (byte) sHi(l), (byte) sLo(l));
    }

    /**
     * 将 {@code long} 整数值追加到字节串之后
     *
     * @param dist  目标字节数组
     * @param value {@code long} 整数值
     * @return {@code long} 值追加后的字节数组
     */
    public static byte[] appendLong(byte[] dist, long value) {
        // 将 long 类型整数拆为 4 个 short 整数
        var lh = lHi(value);
        var nhh = nHi(lh);
        var nhl = nLo(lh);

        var ll = lLo(value);
        var nlh = nHi(ll);
        var nll = nLo(ll);

        return append(
            dist,
            (byte) sHi(nhh),
            (byte) sLo(nhh),
            (byte) sHi(nhl),
            (byte) sLo(nhl),
            (byte) sHi(nlh),
            (byte) sLo(nlh),
            (byte) sHi(nll),
            (byte) sLo(nll));
    }

    /**
     * 将一个字节转化为字节数组
     *
     * @param value 一个字节值
     * @return 包含单个字节的字节数组
     */
    public static byte[] toBytes(byte value) {
        return fill(null, 0, value);
    }

    /**
     * 将一个 {@code short} 值转化为字节数组
     *
     * @param value 一个 {@code short} 值
     * @return 包含 2 个字节的字节数组
     */
    public static byte[] toBytes(short value) {
        return fill(null, 0, (byte) sHi(value), (byte) sLo(value));
    }

    /**
     * 将一个 {@code int} 值转化为字节数组
     *
     * @param value 一个 {@code int} 值
     * @return 包含 4 个字节的字节数组
     */
    public static byte[] toBytes(int value) {
        // 将 int 整数拆为 2 个 short 值
        var l = (short) nLo(value);
        var h = (short) nHi(value);

        // 将 2 个 short 值拆为 4 个字节填充到目标数组中
        return fill(null, 0, (byte) sHi(h), (byte) sLo(h), (byte) sHi(l), (byte) sLo(l));
    }

    /**
     * 将一个 {@code long} 值转化为字节数组
     *
     * @param value 一个 {@code long} 值
     * @return 包含 8 个字节的字节数组
     */
    public static byte[] toBytes(long value) {
        // 将 long 类型整数拆为 4 个 short 整数
        var lh = lHi(value);
        var nhh = nHi(lh);
        var nhl = nLo(lh);

        var ll = lLo(value);
        var nlh = nHi(ll);
        var nll = nLo(ll);

        // 将 4 个 short 值拆为 8 个 byte 填充到目标数组
        return fill(
            null,
            0,
            (byte) sHi(nhh),
            (byte) sLo(nhh),
            (byte) sHi(nhl),
            (byte) sLo(nhl),
            (byte) sHi(nlh),
            (byte) sLo(nlh),
            (byte) sHi(nll),
            (byte) sLo(nll));
    }

    /**
     * 将 2 个 {@code byte} 值转为一个 {@code short} 值
     *
     * @param hi {@code short} 的高位
     * @param lo {@code short} 的低位
     * @return 2 个 {@code byte} 组成的 {@code short} 值
     */
    public static int toShort(byte hi, byte lo) {
        return 0xFFFF & ((hi << 8 & 0xFF00) | (lo & 0xFF));
    }

    /**
     * 将字节数组指定位置之后的 2 字节转为一个 {@code short} 值
     *
     * @param data   字节数组, 自 {@code offset} 之后的第一个字节为 {@code short} 的高位, 第二个字节为
     *               {@code short} 的低位
     * @param offset 指定的偏移量, 从偏移量位置开始转换
     * @return 2 个 {@code byte} 组成的 {@code short} 值
     */
    public static int toShort(byte[] data, int offset) {
        return toShort(data[offset], data[offset + 1]);
    }

    /**
     * 将字节数组开始的 2 字节转为一个 {@code short} 值
     *
     * @param data 字节数组, 第一个字节为 {@code short} 的高位, 第二个字节为 {@code short} 的低位
     * @return 2 个 {@code byte} 组成的 {@code short} 值
     */
    public static int toShort(byte[] data) {
        return toShort(data, 0);
    }

    /**
     * 将 4 个 {@code byte} 值转为一个 {@code int} 值
     *
     * @param b1 {@code int} 的高位
     * @param b2 {@code int} 的高位
     * @param b3 {@code int} 的低位
     * @param b4 {@code int} 的低位
     * @return 4 个 {@code byte} 组成的 {@code int} 值
     */
    public static int toInt(byte b1, byte b2, byte b3, byte b4) {
        return ((b1 << 24) & 0xFF000000) | ((b2 << 16) & 0xFF0000) | ((b3 << 8) & 0xFF00) | (b4 & 0xFF);
    }

    /**
     * 将字节数组指定位置之后的 4 字节转为一个 {@code int} 值
     *
     * @param data   字节数组, 自 {@code offset} 之后的 2 字节为 {@code int} 的高位, 再之后的 2 字节为
     *               {@code int} 的低位
     * @param offset 指定的偏移量, 从偏移量位置开始转换
     * @return 4 个 {@code byte} 组成的 {@code int} 值
     */
    public static int toInt(byte[] data, int offset) {
        return toInt(data[offset], data[offset + 1], data[offset + 2], data[offset + 3]);
    }

    /**
     * 将字节数组开始的 4 字节转为一个 {@code int} 值
     *
     * @param data 字节数组, 前 2 个字节为 {@code int} 的高位, 之后 2 个字节为 {@code int} 的低位
     * @return 4 个 {@code byte} 组成的 {@code int} 值
     */
    public static int toInt(byte[] data) {
        return toInt(data, 0);
    }

    /**
     * 将 8 个 {@code byte} 值转为一个 {@code long} 值
     *
     * @param b1 {@code long} 的高位
     * @param b2 {@code long} 的高位
     * @param b3 {@code long} 的高位
     * @param b4 {@code long} 的高位
     * @param b5 {@code long} 的低位
     * @param b6 {@code long} 的低位
     * @param b7 {@code long} 的低位
     * @param b8 {@code long} 的低位
     * @return 8 个 {@code byte} 组成的 {@code long} 值
     */
    public static long toLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return (((long) b1 << 56) & 0xFF00000000000000L)
               | (((long) b2 << 48) & 0xFF000000000000L)
               | (((long) b3 << 40) & 0xFF0000000000L)
               | (((long) b4 << 32) & 0xFF00000000L)
               | (((long) b5 << 24) & 0xFF000000L)
               | (((long) b6 << 16) & 0xFF0000L)
               | (((long) b7 << 8) & 0xFF00L)
               | (b8 & 0xFFL);
    }

    /**
     * 将字节数组指定位置之后的 8 字节转为一个 {@code long} 值
     *
     * @param data   字节数组, 自 {@code offset} 之后的 4 字节为 {@code long} 的高位, 再之后的 4 字节为
     *               {@code long} 的低位
     * @param offset 指定的偏移量, 从偏移量位置开始转换
     * @return 8 个 {@code byte} 组成的 {@code long} 值
     */
    public static long toLong(byte[] data, int offset) {
        return toLong(
            data[offset],
            data[offset + 1],
            data[offset + 2],
            data[offset + 3],
            data[offset + 4],
            data[offset + 5],
            data[offset + 6],
            data[offset + 7]);
    }

    /**
     * 将字节数组开始的 8 字节转为一个 {@code long} 值
     *
     * @param data 字节数组, 前 4 个字节为 {@code long} 的高位, 之后 4 个字节为 {@code long} 的低位
     * @return 8 个 {@code byte} 组成的 {@code long} 值
     */
    public static long toLong(byte[] data) {
        return toLong(data, 0);
    }

    /**
     * 将 {@code byte} 的高低位互换
     *
     * <p>
     * 在不同系统间直接传递二进制数据时, 有可能因为寄存器的存储结构不同, 存在 Big Endian 和 Little Endian 两种整数存储方式,
     * 此时为保证传输结果正确, 需要对整数的高低位进行调换
     * </p>
     *
     * @param value 要互换高低位的 {@code byte} 值
     * @return 高低位互换后的结果
     */
    public static int flipByte(int value) {
        var hi = value >>> 4 & 0xF;
        var lo = value & 0xF;
        return (lo << 4 & 0xF0) | (hi & 0xF);
    }

    /**
     * 将 {@code short} 的高低位互换
     *
     * <p>
     * 在不同系统间直接传递二进制数据时, 有可能因为寄存器的存储结构不同, 存在 Big Endian 和 Little Endian 两种整数存储方式,
     * 此时为保证传输结果正确, 需要对整数的高低位进行调换
     * </p>
     *
     * @param value 要互换高低位的 {@code short} 值
     * @return 高低位互换后的结果
     */
    public static int flipShort(int value) {
        var hi = value >>> 8 & 0xFF;
        var lo = value & 0xFF;
        return (lo << 8 & 0xFF00) | (hi & 0xFF);
    }

    /**
     * 将 {@code int} 的高低位互换
     *
     * <p>
     * 在不同系统间直接传递二进制数据时, 有可能因为寄存器的存储结构不同, 存在 Big Endian 和 Little Endian 两种整数存储方式,
     * 此时为保证传输结果正确, 需要对整数的高低位进行调换
     * </p>
     *
     * @param value 要互换高低位的 {@code int} 值
     * @return 高低位互换后的结果
     */
    public static int flipInt(int value) {
        var hi = value >>> 16 & 0xFFFF;
        var lo = value & 0xFFFF;
        return (lo << 16 & 0xFFFF0000) | (hi & 0xFFFF);
    }

    /**
     * 将一个 {@code long} 的高低位互换
     *
     * <p>
     * 在不同系统间直接传递二进制数据时, 有可能因为寄存器的存储结构不同, 存在 Big Endian 和 Little Endian 两种整数存储方式,
     * 此时为保证传输结果正确, 需要对整数的高低位进行调换
     * </p>
     *
     * @param value 要互换高低位的 {@code long} 值
     * @return 高低位互换后的结果
     */
    public static long flipLong(long value) {
        var hi = value >>> 32 & 0xFFFFFFFFL;
        var lo = value & 0xFFFFFFFFL;
        return (lo << 32 & 0xFFFFFFFF00000000L) | (hi & 0xFFFFFFFFL);
    }

    /**
     * 将一个表示 16 进制的字符转为整数
     *
     * <p>
     * 例如 {@code 'A' => 0xA}, {@code '9' => 0x9} 等
     * </p>
     *
     * @param c 一个字符, 取值范围为 {@code '0'~'9', 'A'~'F', 'a'~'f'}
     */
    public static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return (c - '0');
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A' + 10);
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a' + 10);
        }
        throw new IllegalArgumentException(String.format("Invalid hex char '%c'", c));
    }

    /**
     * 将 {@code byte} 值转为 {@code int} 值
     *
     * <p>
     * 由于 Java 的 {@code byte} 类型是有符号的 (类似于 C 语言中的 {@code char}) 类型, 所以直接强制转换
     * (int a = (int)((byte) b)) 有可能导致一个负数结果, 本方法保证转换结果不会为负数
     * </p>
     *
     * @param b {@code byte} 类型值
     * @return 转换后的 {@code int} 值
     */
    public static int byteToInt(int b) {
        return 0xFF & b;
    }

    /**
     * 比较两个字节串
     *
     * <p>
     * 比较 {@code a} 和 {@code b} 两个字节数组, 且 {@code a} 数组从 {@code offsetA} 开始比较,
     * {@code b} 数组从 {@code offsetB} 开始比较, 共比较 {@code length} 个字节
     * </p>
     *
     * <p>
     * 如果 {@code a} 和 {@code b} 同为 {@code null}, 则认为相等
     * </p>
     */
    public static boolean compare(byte[] a, int offsetA, byte[] b, int offsetB, int length) {
        // 判断 null 的情况
        if (a == null || b == null) {
            // 如果 a 为 null, 则 b 为 null 时表示相等
            if (a == null) {
                return b != null;
            }
            // 如果 b 为 null, 则 a 为 null 时表示相等
            return a != null;
        }

        // 计算两个数组要比较的字节数
        var endA = Math.min(offsetA + length, a.length);
        var endB = Math.min(offsetB + length, b.length);

        // 如果两个数组要比较的字节数不同, 则两个数组不相等
        if (endA - offsetA != endB - offsetB) {
            return false;
        }

        // 从指定位置开始, 逐字节比较两个数组
        for (int i = offsetA, j = offsetB; i < endA && j < endB; i++, j++) {
            if (a[i] != b[j]) {
                return false;
            }
        }
        return true;
    }
}
