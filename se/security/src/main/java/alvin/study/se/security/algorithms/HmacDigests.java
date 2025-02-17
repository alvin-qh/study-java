package alvin.study.se.security.algorithms;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 支持 HMAC 盐密钥的摘要算法工具类
 *
 * <p>
 * 通常计算 MD5 时可以采用 {@code md5(message + salt)}. 类似的加盐校验方法有: {@code MAC = H(key + message)},
 * {@code MAC = H(message + key)} 或者 {@code MAC = H(key +message + key)}. 但是这些算法依旧存在安全隐患, 这些粗陋的 MAC 实现
 * 方法让大家意识到需要一种靠得住的 MAC 实现方法, 这便是 HMAC 的由来
 * </p>
 *
 * <p>
 * <img src="../../../../../../../../assets/hmac.png" alt="HMAC"/>
 * </p>
 *
 * <p>
 * 密钥散列消息认证码 (Keyed-hash message authentication code), 又称散列消息认证码 (Hash-based message authentication code,
 * 缩写为HMAC), 是一种通过特别计算方式之后产生的消息认证码 (MAC), 使用密码散列函数, 同时结合一个加密密钥. 它可以用来保证数据的完整性,
 * 同时可以用来作某个消息的身份验证
 * </p>
 *
 * <p>
 * HMAC 通过一个标准算法, 在计算哈希的过程中, 把 key 混入计算过程中. 和我们自定义的加 salt 算法不同, HMAC 算法针对各种哈希算法都通用,
 * 无论是 MD5 还是 SHA-1. 采用 HMAC 替代我们自己的 salt 算法, 可以使程序算法更标准化也更安全.
 * </p>
 *
 * <p>
 * HMAC 支持的算法有: {@code MD5}, {@code SHA1}, {@code SHA256}, {@code SHA512}, {@code ADLER32}, {@code CRC32},
 * {@code CRC32B}, {@code FNV132}, {@code FNV164}, {@code FNV1A32}, {@code FNV1A64}, {@code GOST}, {@code GOST-CRYPTO},
 * {@code HAVAL128,3}, {@code HAVAL128,4}, {@code HAVAL128,5}, {@code HAVAL160,3}, {@code HAVAL160,4},
 * {@code HAVAL160,5}, {@code HAVAL192,3}, {@code HAVAL192,4}, {@code HAVAL192,5}, {@code HAVAL224,3},
 * {@code HAVAL224,4}, {@code HAVAL224,5}, {@code HAVAL256,3}, {@code HAVAL256,4}, {@code HAVAL256,5}, {@code JOAAT},
 * {@code MD2}, {@code MD4}, {@code RIPEMD128}, {@code RIPEMD160}, {@code RIPEMD256}, {@code RIPEMD320}, {@code SHA224},
 * {@code SHA384}, {@code SNEFRU}, {@code SNEFRU256}, {@code TIGER128,3}, {@code TIGER128,4}, {@code TIGER160,3},
 * {@code TIGER160,4}, {@code TIGER192,3}, {@code TIGER192,4}, {@code WHIRLPOOL}
 * </p>
 *
 * <p>
 * HMAC 的加密实现: {@code HMAC(k,m) = H((k XOR opad) + H((k XOR ipad) + m))}, 其中:
 * <ul>
 * <li>
 * {@code H} 是一个 Hash 函数, 比如, MD5, SHA-1 或 SHA-256
 * </li>
 * <li>
 * {@code k} 是一个密钥, 从左到右用 {@code 0} 填充到 hash 函数规定的 block 的长度, 如果密钥长度大于 block 的长度, 就对先对输入 key
 * 作 hash
 * </li>
 * <li>
 * {@code m} 是需要认证的消息
 * </li>
 * <li>
 * {@code +} 代表"连接"运算
 * </li>
 * <li>
 * {@code XOR} 代表异或运算
 * </li>
 * <li>
 * {@code opad} 是外部填充常数 (0x5c5c5c…5c5c, 一段十六进制常量)
 * </li>
 * <li>
 * {@code ipad} 是内部填充常数 (0x363636…3636, 一段十六进制常量)
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * <table border="1">
 * <tr>
 * <th>算法种类</th>
 * <th>摘要长度</th>
 * </tr>
 * <tr>
 * <td>HMAC-MD5</td>
 * <td>128</td>
 * </tr>
 * <tr>
 * <td>HMAC-SHA1</td>
 * <td>160</td>
 * </tr>
 * <tr>
 * <td>HMAC-SHA256</td>
 * <td>256</td>
 * </tr>
 * <tr>
 * <td>HMAC-SHA384</td>
 * <td>384</td>
 * </tr>
 * <tr>
 * <td>HMAC-SHA512</td>
 * <td>512</td>
 * </tr>
 * </table>
 * </p>
 */
public class HmacDigests {
    // 默认的缓冲区大小
    private static final int BUFFER_SIZE = 1024;

    // 算法信息枚举对象
    private final Algorithm algorithm;

    /**
     * 构造器, 通过算法名称初始化对象
     *
     * @param algorithmName 算法名称字符串
     */
    public HmacDigests(String algorithmName) {
        this(Algorithm.valueOf(algorithmName));
    }

    /**
     * 构造器, 通过算法信息枚举对象初始化对象
     *
     * @param algorithm {@link Algorithm} 枚举对象
     */
    public HmacDigests(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 对字节串进行摘要计算
     *
     * @param hmacKey 密钥串
     * @param data    要摘要的字节串数据
     * @param offset  偏移量, 即从 {@code data} 参数的该位置起开始计算
     * @param length  长度, 即对 {@code data} 参数求摘要的数据长度
     * @return 摘要结果字符串
     * @throws InvalidKeyException      无效的密钥
     * @throws NoSuchAlgorithmException 无效的算法名称
     */
    public String digest(byte[] hmacKey, byte[] data, int offset, int length)
            throws InvalidKeyException, NoSuchAlgorithmException {
        var mac = algorithm.instance(hmacKey);
        mac.update(data, offset, length);
        return Hex.encodeHexString(mac.doFinal());
    }

    /**
     * 对字节串进行摘要计算
     *
     * @param hmacKey 密钥串
     * @param data    要摘要的字节串数据
     * @return 摘要信息字符串
     * @throws InvalidKeyException      无效的密钥
     * @throws NoSuchAlgorithmException 无效的算法名称
     */
    public String digest(byte[] hmacKey, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException {
        var mac = algorithm.instance(hmacKey);
        return Hex.encodeHexString(mac.doFinal(data));
    }

    /**
     * 将输入流的内容进行摘要
     *
     * @param hmacKey HMAC 密钥
     * @param input   输入流
     * @return 摘要信息字符串
     * @throws InvalidKeyException      无效的密钥
     * @throws NoSuchAlgorithmException 无效的算法名称
     * @throws IOException              读取输入流失败
     */
    public String digest(byte[] hmacKey, InputStream input)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        var mac = algorithm.instance(hmacKey);

        var buf = new byte[BUFFER_SIZE];
        var count = 0;
        while ((count = input.read(buf)) > 0) {
            mac.update(buf, 0, count);
        }
        return Hex.encodeHexString(mac.doFinal());
    }

    /**
     * 对指定文件的内容进行摘要
     *
     * @param hmacKey HMAC 密钥
     * @param file    要摘要的文件路径名
     * @return 摘要信息字符串
     * @throws NoSuchAlgorithmException 算法名错误
     * @throws InvalidKeyException      无效的密钥
     * @throws IOException              读取流时发生异常
     */
    public String digest(byte[] hmacKey, Path file) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        try (var channel = (FileChannel) Files.newByteChannel(file, StandardOpenOption.READ)) {
            var mac = algorithm.instance(hmacKey);

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
                mac.update(buf);

                buf.clear();
            }

            return Hex.encodeHexString(mac.doFinal());
        }
    }

    /**
     * 获取当前算法的摘要长度
     *
     * @return 摘要结果的字节串长度
     */
    public int byteSize() {
        return algorithm.byteSize();
    }

    /**
     * 定义算法信息的枚举类型
     */
    public enum Algorithm {
        /**
         * HmacMD5
         */
        MD5("MD5", 64),

        /**
         * HmacSHA1
         */
        SHA1("SHA1", 80),

        /**
         * HmacSHA224
         */
        SHA224("SHA224", 112),

        /**
         * HmacSHA256
         */
        SHA256("SHA256", 128),

        /**
         * HmacSHA384
         */
        SHA384("SHA384", 192),

        /**
         * HmacSHA512
         */
        SHA512("SHA512", 256);

        // 算法名称
        private final String name;

        // 摘要的字节长度
        private final int byteSize;

        /**
         * 构造器, 初始化对象
         *
         * @param name     算法名称
         * @param byteSize 摘要长度
         */
        Algorithm(String name, int byteSize) {
            this.name = name;
            this.byteSize = byteSize;
        }

        /**
         * 创建 {@link Mac} 对象
         *
         * @param hmacKey 密钥数据
         * @return {@link Mac} 对象
         */
        public Mac instance(byte[] hmacKey) throws NoSuchAlgorithmException, InvalidKeyException {
            // 生成完整算法名称
            var algorithmName = "Hmac" + name;

            // 根据算法名称获取 Mac 对象
            var mac = Mac.getInstance(algorithmName);
            // 为 Mac 对象初始化密钥
            mac.init(new SecretKeySpec(hmacKey, algorithmName));
            return mac;
        }

        /**
         * 获取摘要的字节长度
         *
         * @return 摘要字节长度
         */
        public int byteSize() {
            return byteSize;
        }
    }
}
