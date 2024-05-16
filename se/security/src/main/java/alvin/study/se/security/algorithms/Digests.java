package alvin.study.se.security.algorithms;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

/**
 * 摘要算法工具类
 */

public class Digests {
    // 读取文件的默认缓冲区大小
    private static final int BUFFER_SIZE = 1024;

    // 摘要算法枚举对象
    private final Algorithm algorithm;

    /**
     * 构造器, 通过一个枚举对象初始化对象
     *
     * @param algorithm {@link Algorithm} 枚举对象
     */
    public Digests(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 构造器, 通过一个表示算法名称的字符串初始化对象
     *
     * @param algorithmName 表示算法名称的字符串初始化对象
     */
    public Digests(String algorithmName) {
        this(Algorithm.valueOf(algorithmName));
    }

    /**
     * 对一个字节串进行摘要计算
     *
     * @param data 字节串数据
     * @return 摘要结果, 为摘要数据的 16 进制字符串表示
     */
    public String digest(byte[] data) {
        var md = algorithm.instance();
        md.update(data);
        return Hex.encodeHexString(md.digest());
    }

    /**
     * 对一个字节串进行摘要计算
     *
     * @param data   字节串数据
     * @param offset 偏移量, 即从 {@code data} 参数的该位置开始计算
     * @param length 要计算摘要的字节长度
     * @return 摘要结果, 为摘要数据的 16 进制字符串表示
     */
    public String digest(byte[] data, int offset, int length) {
        var md = algorithm.instance();
        md.update(data, offset, length);
        return Hex.encodeHexString(md.digest());
    }

    /**
     * 对一个输入流进行摘要计算
     *
     * @param input 输入流对象
     * @return 摘要结果, 为摘要数据的 16 进制字符串表示
     * @throws IOException 读取流时发生异常
     */
    public String digest(InputStream input) throws IOException {
        var md = algorithm.instance();

        var buf = new byte[BUFFER_SIZE];
        var n = 0;
        while ((n = input.read(buf)) > 0) {
            md.update(buf, 0, n);
        }
        return Hex.encodeHexString(md.digest());
    }

    /**
     * 对指定文件的内容进行摘要
     *
     * @param file 文件路径名
     * @return 摘要信息, 为摘要数据的 16 进制字符串表示
     * @throws IOException 读取流时发生异常
     */
    public String digest(Path file) throws IOException {
        try (var channel = (FileChannel) Files.newByteChannel(file, StandardOpenOption.READ)) {
            var md = algorithm.instance();

            // 设置 ByteBuffer 的默认大小
            var bufSize = BUFFER_SIZE;
            if (bufSize > channel.size()) {
                // 如果设置的缓存大小比文件本身还要大, 则将缓冲区设置成文件大小
                // 以避免浪费内存
                bufSize = (int) channel.size();
            }

            var buf = ByteBuffer.allocateDirect(bufSize);
            while (channel.read(buf) > 0) {
                // 对 buf 进行写操作后执行翻转操作, 准备进行读操作
                // flip: mark = -1; limit = position; position = 0;
                buf.flip();
                md.update(buf);

                buf.clear();
            }

            return Hex.encodeHexString(md.digest());
        }
    }

    /**
     * 获取摘要结果的字节数
     *
     * @return 摘要结果的字节数
     */
    public int byteSize() {
        return algorithm.byteSize();
    }

    /**
     * 支持的摘要算法枚举
     */
    public enum Algorithm {
        /**
         * MD2 摘要算法
         */
        MD2("MD2", 64),

        /**
         * MD5 摘要算法
         */
        MD5("MD5", 64),

        /**
         * SHA-1 摘要算法
         */
        SHA1("SHA-1", 80),

        /**
         * SHA-224 摘要算法
         */
        SHA224("SHA-224", 112),

        /**
         * SHA-256 摘要算法
         */
        SHA256("SHA-256", 128),

        /**
         * SHA-384 摘要算法
         */
        SHA384("SHA-384", 192),

        /**
         * SHA-512 摘要算法
         */
        SHA512("SHA-512", 256);

        /**
         * 算法名称
         */
        private final String name;

        /**
         * 摘要长度
         */
        private final int byteSize;

        /**
         * 构造器, 创建一个摘要算法枚举项目
         *
         * @param name     算法名称
         * @param byteSize 摘要长度
         */
        Algorithm(String name, int byteSize) {
            this.name = name;
            this.byteSize = byteSize;
        }

        /**
         * 创建摘要算法 ({@link MessageDigest}) 实例对象
         *
         * @return {@link MessageDigest} 对象
         */
        @SneakyThrows
        public MessageDigest instance() {
            return MessageDigest.getInstance(this.name);
        }

        /**
         * 获取摘要结果的字节数
         *
         * @return 摘要结果的字节数
         */
        public int byteSize() {
            return byteSize;
        }
    }
}
