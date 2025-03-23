package alvin.study.se.binary;

/**
 * 计算累加和校验码
 *
 * <p>
 * 累加和校验码即将所有的字节累加后, 对累加结果求反, 得到校验码
 * </p>
 *
 * <p>
 * 注意, 本例只是累加和算法的基本型, 不同的系统会使用不同的累加算法 (例如对要累加的每 2 字节组成一个 {@code short} 值后进行累加等)
 * </p>
 */
public final class Checksum {
    // 求和校验码
    private int sum;

    /**
     * 默认构造器
     */
    public Checksum() {
        this(0);
    }

    /**
     * 构造器
     *
     * @param sum 累加和初始值
     */
    private Checksum(int sum) {
        this.sum = sum;
    }

    /**
     * 添加一个 {@code byte} 值
     *
     * @param b {@code byte} 值
     */
    public void update(int b) {
        sum += Bytes.byteToInt(b);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data {@code byte} 数组, 对数组的每一项进行累加
     */
    public void update(byte[] data) {
        update(data, 0, data.length);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行累加
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     */
    public void update(byte[] data, int offset) {
        update(data, offset, data.length);
    }

    /**
     * 添加一组 {@code byte} 值
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行累加
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @param length 长度, 即从偏移量开始要计算的字节长度
     */
    public void update(byte[] data, int offset, int length) {
        var lastIndex = Math.min(offset + length, data.length);
        for (var i = offset; i < lastIndex; i++) {
            update(data[i]);
        }
    }

    /**
     * 计算累加和校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果
     * </p>
     *
     * @return 累加和校验码
     */
    public int doFinal() {
        if (sum <= 0xFF) {
            // 如果累加结果不超过 255, 则返回累加结果
            return sum;
        }
        // 如果累加结果超过 255, 则对结果求补码
        return Bytes.byteToInt(~sum) + 1;
    }

    /**
     * 计算累加和校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果累加上本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行累加
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @return 累加和校验码
     */
    public int doFinal(byte[] data, int offset) {
        return doFinal(data, offset, data.length - offset);
    }

    /**
     * 计算累加和校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果累加上本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 对数组的每一项进行累加
     * @param offset 偏移量, 从偏移量指定的位置开始计算
     * @param length 长度, 即从偏移量开始要计算的字节长度
     * @return 累加和校验码
     */
    public int doFinal(byte[] data, int offset, int length) {
        var checksum = new Checksum(sum);
        checksum.update(data, offset, length);
        return checksum.doFinal();
    }

    /**
     * 计算累加和校验码
     *
     * <p>
     * 最后的结果依赖于之前 {@code update} 方法执行的结果, 并在之前的结果累加上本次传递参数的计算结果
     * </p>
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data {@code byte} 数组, 对数组的每一项进行累加
     * @return 累加和校验码
     */
    public int doFinal(byte[] data) {
        return doFinal(data, 0, data.length);
    }
}
