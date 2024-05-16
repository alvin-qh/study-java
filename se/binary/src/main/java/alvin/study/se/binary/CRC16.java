package alvin.study.se.binary;

import java.util.function.BiFunction;

/**
 * 计算 CRC16 校验码
 *
 * <p>
 * 本例中演示了两种 CRC16 计算方法: 查表法和位运算法, 前者具备更高的效率, 后者消耗更少的内存, 一般情况下推荐使用前者
 * </p>
 *
 * <p>
 * 由于 Java 自带 CRC32 校验码计算库 ({@link java.util.zip.CRC32 CRC32}), 所以本例中只对 CRC16
 * 进行处理
 * </p>
 */
public final class CRC16 {
    // @formatter:off
    private static final int[] TABLE = {
            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
        };
    // @formatter:on

    // 计算方法
    private final AlgorithmMode mode;

    // 保存中间结果
    private int code;

    /**
     * 构造器, 通过查表法计算 CRC16
     */
    public CRC16() {
        this(AlgorithmMode.TABLE_LOOKUP);
    }

    /**
     * 构造器, 设置算法模式
     *
     * @param mode 算法模式
     */
    public CRC16(AlgorithmMode mode) {
        this(mode, 0);
    }

    /**
     * 构造器, 设置算法模式和 crc 初始值
     *
     * @param mode 算法模式
     * @param code crc 初始值
     */
    private CRC16(AlgorithmMode mode, int code) {
        this.mode = mode;
        this.code = code;
    }

    /**
     * 累计计算一个 {@code byte} 的 crc 结果
     *
     * @param b 字节值
     */
    public void update(byte b) {
        code = mode.exec(code, b);
    }

    /**
     * 累计计算 {@code byte} 数组的 crc 结果
     *
     * @param data {@code byte} 数组, 逐字节计算其 crc 值
     */
    public void update(byte[] data) {
        update(data, 0, data.length);
    }

    /**
     * 累计计算 {@code byte} 数组的 crc 结果
     *
     * @param data   {@code byte} 数组, 逐字节计算其 crc 值
     * @param offset 数组偏移量, 即从当前位置开始计算
     */
    public void update(byte[] data, int offset) {
        update(data, offset, data.length);
    }

    /**
     * 累计计算 {@code byte} 数组的 crc 结果
     *
     * @param data   {@code byte} 数组, 逐字节计算其 crc 值
     * @param offset 数组偏移量, 即从当前位置开始计算
     * @param length 要计算的数据长度
     */
    public void update(byte[] data, int offset, int length) {
        var lastIndex = Math.min(offset + length, data.length);
        for (var i = offset; i < lastIndex; i++) {
            update(data[i]);
        }
    }

    /**
     * 计算最终 crc 结果
     *
     * @return crc 计算结果
     */
    public int doFinal() {
        return code;
    }

    /**
     * 计算最终 crc 结果
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 逐字节计算其 crc 值
     * @param offset 数组偏移量, 即从当前位置开始计算
     * @return crc 计算结果
     */
    public int doFinal(byte[] data, int offset) {
        return doFinal(data, offset, data.length - offset);
    }

    /**
     * 计算最终 crc 结果
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data   {@code byte} 数组, 逐字节计算其 crc 值
     * @param offset 数组偏移量, 即从当前位置开始计算
     * @param length 要计算的数据长度
     * @return crc 计算结果
     */
    public int doFinal(byte[] data, int offset, int length) {
        var crc = new CRC16(this.mode, this.code);
        crc.update(data, offset, length);
        return crc.doFinal();
    }

    /**
     * 计算最终 crc 结果
     *
     * <p>
     * 注意: {@code doFinal} 是一个幂等方法, 不会影响之前 {@code update} 的结果
     * </p>
     *
     * @param data {@code byte} 数组, 逐字节计算其 crc 值
     * @return crc 计算结果
     */
    public int doFinal(byte[] data) {
        return doFinal(data, 0, data.length);
    }

    /**
     * CRC16 计算算法枚举
     */
    public enum AlgorithmMode {
        /**
         * 查表法
         *
         * <p>
         * 查表法调用 {@link AlgorithmMode#tableLookup(Integer, Byte)} 方法进行计算
         * </p>
         */
        TABLE_LOOKUP(AlgorithmMode::tableLookup),

        /**
         * 异或运算法
         *
         * <p>
         * 异或法调用 {@link AlgorithmMode#xor(Integer, Byte)} 方法进行计算
         * </p>
         */
        XOR(AlgorithmMode::xor);

        // 计算 crc 值的函数对象
        private final BiFunction<Integer, Byte, Integer> func;

        /**
         * 构造器, 设置计算用的函数
         *
         * @param func 计算函数
         */
        AlgorithmMode(BiFunction<Integer, Byte, Integer> func) {
            this.func = func;
        }

        /**
         * 通过查表法计算 crc 值
         *
         * @param crc 上一次 crc 计算结果
         * @param b   要计算的 {@code byte} 值
         * @return crc 计算结果
         */
        private static int tableLookup(Integer crc, Byte b) {
            assert crc != null;
            assert b != null;

            return (crc >>> 8) ^ TABLE[(crc ^ b) & 0xff];
        }

        /**
         * 通过异或法计算 crc 值
         *
         * @param crc 上一次 crc 计算结果
         * @param b   要计算的 {@code byte} 值
         * @return crc 计算结果
         */
        private static int xor(Integer crc, Byte b) {
            assert crc != null;
            assert b != null;

            crc = crc ^ Bytes.byteToInt(b);
            for (var j = 0; j < 8; j++) {
                var bit = (crc & 0x1);
                crc >>= 1;

                if (bit > 0) {
                    crc ^= 0xA001;
                }
            }
            return crc;
        }

        /**
         * 计算一个字节的 crc 值
         *
         * @param crc 上一次 crc 计算结果
         * @param b   要计算的 {@code byte} 值
         * @return crc 计算结果
         */
        public int exec(int crc, byte b) {
            return func.apply(crc, b);
        }
    }
}
