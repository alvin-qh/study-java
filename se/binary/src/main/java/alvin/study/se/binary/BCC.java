package alvin.study.se.binary;

import org.jetbrains.annotations.NotNull;

/**
 * 计算 BCC 校验码
 *
 * <p>
 * BCC (Block Check Character, 信息组校验码), 因校验码是将所有数据异或得出, 故俗称异或校验
 * </p>
 *
 * <p>
 * 具体算法是: 将每一个字节的数据进行异或后即得到校验码
 *
 * <pre>
 * 如: 0x01, 0xA0, 0x7C, 0xFF, 0x02
 * 计算: 0x01 ^ 0xA0 ^ 0x7C ^ 0xFF ^ 0x02 = 0x20
 * 校验码是：0x20
 * </pre>
 * </p>
 */
public final class BCC {
    // 求和校验码
    private int code;

    /**
     * 默认构造器
     */
    public BCC() {
        this(0);
    }

    /**
     * 构造器
     *
     * @param code 异或结果初始值
     */
    private BCC(int code) {
        this.code = code;
    }

    /**
     * 添加一个 {@code byte} 值
     *
     * @param b {@code byte} 值
     */
    public void update(int b) {
        code ^= Bytes.byteToInt(b);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data {@code byte} 数组, 对数组的每一项进行异或运算
     */
    public void update(byte[] data) {
        update(data, 0, data.length);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行异或运算
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     */
    public void update(byte[] data, int offset) {
        update(data, offset, data.length);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行异或运算
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @param length 长度, 即从偏移量开始要计算的字节长度
     */
    public void update(byte @NotNull [] data, int offset, int length) {
        var lastIndex = Math.min(offset + length, data.length);
        for (var i = offset; i < lastIndex; i++) {
            update(data[i]);
        }
    }

    /**
     * 计算 BCC 校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果
     * </p>
     *
     * @return BCC 校验码
     */
    public int doFinal() {
        return code;
    }

    /**
     * 计算 BCC 校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果基础上计算本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行异或运算
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @return BCC 校验码
     */
    public int doFinal(byte[] data, int offset) {
        return doFinal(data, offset, data.length - offset);
    }

    /**
     * 计算 BCC 校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果基础上计算本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行异或运算
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @param length 长度, 即从偏移量开始要计算的字节长度
     * @return BCC 校验码
     */
    public int doFinal(byte[] data, int offset, int length) {
        var bcc = new BCC(this.code);
        bcc.update(data, offset, length);
        return bcc.doFinal();
    }

    /**
     * 计算 BCC 校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果基础上计算本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data {@code byte} 数组, 对数组的每一项进行异或运算
     * @return BCC 校验码
     */
    public int doFinal(byte[] data) {
        return doFinal(data, 0, data.length);
    }
}
