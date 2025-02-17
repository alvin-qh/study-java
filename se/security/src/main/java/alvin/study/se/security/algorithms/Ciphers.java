package alvin.study.se.security.algorithms;

import alvin.study.se.security.util.DataGenerator;
import com.google.common.base.Strings;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * 密码应用
 *
 * <p>
 * 密码学中, 分组 (Block) 密码的工作模式 (Mode of Operation) 允许使用同一个分组密码密钥对多于一块的数据进行加密,
 * 并保证其安全性.
 * 分组密码自身只能加密长度等于密码分组长度的单块数据, 若要加密变长数据, 则数据必须先被划分为一些单独的密码块.
 * 通常而言, 最后一块数据也需要使用合适填充方式将数据扩展到匹配密码块大小的长度. 一种工作模式描述了加密每一数据块的过程,
 * 并常常使用基于一个通常称为初始化向量的附加输入值以进行随机化, 以保证安全
 * </p>
 *
 * <p>
 * 不同的工作模式的实现细节也不相同, 在Java中, 指定加密算法的字符串是 {@code AES/ECB/PKCS5Padding},
 * 也就是"算法/模式/填充方式".
 * 而 {@code IV} 就和工作模式有关. 常见的工作模式包括, {@code ECB}, {@code CBC}, {@code PCBC},
 * {@code CFB}, {@code OFB},
 * {@code CTR}等
 * </p>
 *
 * <p>
 * 在密码学的领域里, 初始化向量 (Initialization Vector, 即 IV, 或译"初向量", 又称"初始变量" (Starting
 * Variable, 缩写为 SV),
 * 是一个固定长度的输入值. 一般的使用上会要求它是随机数或拟随机数 (Pseudorandom). 使用随机数产生的初始化向量才能达到语义安全
 * (散列函数与消息验证码也有相同要求), 并让攻击者难以对同一把密钥的密文进行破解. 在区块加密中, 使用了初始化向量的加密模式被称为区块加密模式
 * </p>
 *
 * <p>
 * ECB
 * <ul>
 * <li>
 * 加密: <img src="../../../../../../../../assets/ecb-decrypt.png">
 * </li>
 * <li>
 * 解密: <img src="../../../../../../../../assets/ecb-decrypt.png">
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * CBC
 * <ul>
 * <li>
 * 加密: <img src="../../../../../../../../assets/cbc-encrypt.png">
 * </li>
 * <li>
 * 解密: <img src="../../../../../../../../assets/cbc-decrypt.png">
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * PCBC
 * <ul>
 * <li>
 * 加密: <img src="../../../../../../../../assets/pcbc-encrypt.png">
 * </li>
 * <li>
 * 解密: <img src="../../../../../../../../assets/pcbc-decrypt.png">
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 根据上面的图可以看出, {@code ECB} 模式下是不需要 {@code IV} 的, 而 {@code CBC} 和 {@code PCBC}
 * 是需要 {@code PCBC} 的
 * </p>
 *
 * <p>
 * {@code IV} 取值的注意事项:
 * <ol>
 * <li>
 * 初始化向量的值依密码算法而不同, 最基本的要求是"唯一性", 也就是说同一把密钥不重复使用同一个初始化向量.
 * 这个特性无论在区块加密或流加密中都非常重要
 * </li>
 * <li>
 * 初始化向量必须让攻击者无法预测, 这种要求一般使用随机数或拟随机数来达到. 在这种应用中, 重复的初始化向量是可以被忽略的,
 * 但是生日攻击的问题依然得列入考量, 因为若向量可以被预测, 会让攻击者找到撤消明文的线索
 * </li>
 * <li>
 * 初始化向量的值主要还是取决于密码算法, 其做法不外乎就是随机或指定 (Stateful). 使用随机的方式则取值由发送方计算, 并要将向量值送交给接收方.
 * 指定的方式则是让收发两方分享初始化向量所能指定的所有值 (State), 这些值收发双方必须预先就定义好
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Java 内置的密码学支持包括:
 * <ol>
 * <li>
 * 算法包括: {@code AES}, {@code DES}, {@code DESede} ({@code DES3}) 和 {@code RSA}
 * 四种
 * </li>
 * <li>
 * 模式包括: {@code CBC} (有向量模式) 和 {@code ECB} (无向量模式), 向量模式可以简单理解为偏移量, 使用
 * {@code CBC} 模式需要定义一个
 * {@link IvParameterSpec} 对象
 * </li>
 * <li>
 * 填充方式包括:
 * <ul>
 * <li>
 * {@code NoPadding}: 加密内容不足 {@code 8/16} 位用指定字节补足 {@code 8/16} 位,
 * {@link Cipher} 类不提供补位功能,
 * 需自行实现代码给加密内容添加补位, 如 <code>{65, 65, 65, 0, 0, 0, 0, 0}</code>
 * </li>
 * <li>
 * {@code PKCS5Padding}: 加密内容不足 {@code 8/16} 位用余位数补足 {@code 8/16} 位, 如
 * <code>{65, 65, 65, 5, 5, 5, 5, 5} </code> 或
 * <code>{97, 97, 97, 97, 97, 97, 2, 2}</code>
 * </li>
 * </ul>
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * 所以按照 {@code 算法/模式/填充方式} 的组合, 共有以下算法名称参数
 * <ul>
 * <li>
 * {@code AES/CBC/NoPadding} (128)
 * </li>
 * <li>
 * {@code AES/CBC/PKCS5Padding} (128)
 * </li>
 * <li>
 * {@code AES/ECB/NoPadding} (128)
 * </li>
 * <li>
 * {@code AES/ECB/PKCS5Padding} (128)
 * </li>
 * <li>
 * {@code DES/CBC/NoPadding} (56)
 * </li>
 * <li>
 * {@code DES/CBC/PKCS5Padding} (56)
 * </li>
 * <li>
 * {@code DES/ECB/NoPadding} (56)
 * </li>
 * <li>
 * {@code DES/ECB/PKCS5Padding} (56)
 * </li>
 * <li>
 * {@code DESede/CBC/NoPadding} (168)
 * </li>
 * <li>
 * {@code DESede/CBC/PKCS5Padding} (168)
 * </li>
 * <li>
 * {@code DESede/ECB/NoPadding} (168)
 * </li>
 * <li>
 * {@code DESede/ECB/PKCS5Padding} (168)
 * </li>
 * <li>
 * {@code RSA/ECB/PKCS1Padding} (1024, 2048)
 * </li>
 * <li>
 * {@code RSA/ECB/OAEPWithSHA-1AndMGF1Padding} (1024, 2048)
 * </li>
 * <li>
 * {@code RSA/ECB/OAEPWithSHA-256AndMGF1Padding} (1024, 2048)
 * </li>
 * </ul>
 * </p>
 */
public class Ciphers {
    // 默认缓冲区大小
    private static final int BUFFER_SIZE = 1024;

    // 1 Byte 的位数
    private static final int BYTE_SIZE = 8;

    // 表示手动补位计算规则
    private static final String NO_PADDING = "NoPadding";

    // 算法名称
    private final Algorithm algorithm;

    // 算法模式
    @Getter
    private final String mode;

    // 填充方式
    @Getter
    private final String padding;

    /**
     * 构造器, 初始化算法参数
     *
     * @param algorithmName 加密算法名称
     */
    public Ciphers(String algorithmName) {
        if (Strings.isNullOrEmpty(algorithmName)) {
            throw new IllegalArgumentException("algorithmName");
        }

        // 将算法名称以 / 分割为 3 部分
        var parts = algorithmName.split("/", 3);

        // 第一部分表示算法名, 如 DES, AES 等
        if (parts.length > 0) {
            this.algorithm = Algorithm.valueOf(parts[0].trim());
        } else {
            throw new IllegalArgumentException("algorithmName");
        }

        // 第二部分表示算法模式, 如 ECB, CBC 等, 表示是否需要初始向量
        if (parts.length > 1) {
            this.mode = parts[1].trim();
        } else {
            this.mode = null;
        }

        // 第三部分表示补位算法, 如 NoPadding, PKCS5Padding 等
        if (parts.length > 2) {
            this.padding = parts[2].trim();
        } else {
            this.padding = null;
        }
    }

    /**
     * 产生一个对称加密参数对象
     *
     * @return {@link SecretKeyParameterSpec} 类型对象, 包括密钥以及初始化向量
     */
    public SecretKeyParameterSpec makeSecretKeyParameterSpec() throws NoSuchAlgorithmException {
        // 根据加密算法产生随机密钥
        var generator = KeyGenerator.getInstance(algorithm.algorithm());
        if (algorithm.keySize() > 0) {
            // 初始化密钥生成器, 设置密钥长度
            generator.init(algorithm.keySize());
        }

        byte[] iv = null;
        // 未设置 mode, 初始向量长度为 0 或者算法模式为 ECB 表示无需初始向量
        if (!Strings.isNullOrEmpty(mode) && !"ECB".equalsIgnoreCase(mode) && algorithm.ivSize() > 0) {
            // 生成初始化向量值
            iv = DataGenerator.generate(algorithm.ivSize());
        }

        // 产生对称加密参数对象
        return new SecretKeyParameterSpec(algorithm.algorithm(), generator.generateKey().getEncoded(), iv);

    }

    /**
     * 产生加密解密算法对象
     *
     * @param opmode        运算模式, {@link Cipher#ENCRYPT_MODE} 或者
     *                      {@link Cipher#DECRYPT_MODE}
     * @param keySpec       密钥对象
     * @param parameterSpec 加密初始化向量对象
     * @return {@link Cipher} 密码学计算对象
     */
    private Cipher makeCipherInstance(int opmode, Key keySpec, AlgorithmParameterSpec parameterSpec) {
        // 组装加密算法名称, 格式为: 算法名/算法模式/填充方式
        // 如果忽略后两者, 则按缺省情况设置算法, 一般缺省为 算法名/ECB/PKCS5Padding, 表示无需初始向量, 通过 PKCS5 标准填充
        var algorithmName = algorithm.algorithm();
        if (mode != null && padding != null) {
            algorithmName = String.format("%s/%s/%s", algorithm.algorithm(), mode, padding);
        }

        try {
            // 实例化算法对象
            var cipher = Cipher.getInstance(algorithmName);
            if (parameterSpec == null) {
                // 通过密钥初始化算法对象
                cipher.init(opmode, keySpec);
            } else {
                // 通过密钥和初始化向量值初始化算法对象
                cipher.init(opmode, keySpec, parameterSpec);
            }
            return cipher;
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 手动为数据添加 Padding 内容
     *
     * <p>
     * 对于填充方式为 {@code NoPadding} 时, 算法不会自动为数据进行填充, 此时需要手动填充, 将待加密数据长度填充为
     * {@code 8/16} 的整数倍
     * </p>
     *
     * <p>
     * 如果算法指定了填充方式, 例如: {@code PKCS5Padding}, 则无需进行这个步骤, 算法会自行处理
     * </p>
     *
     * @param data   待加密的数据
     * @param length 数据的实际长度
     * @return 进行填充后的数据
     */
    private byte[] appendPadding(byte[] data, int length) {
        // 获取算法相关的填充大小, 为 8/16
        var paddingSize = algorithm.paddingSize();

        // 如果无需手动填充, 或数据长度本身已经符合要求, 则跳过
        if (!NO_PADDING.equals(padding) || length % paddingSize == 0) {
            return data;
        }

        // 计算填充后的数据长度, 令数据长度为 8/16 的整数倍
        var newLen = (length / paddingSize + 1) * paddingSize;

        // 将原数据复制到新数组中
        var newData = new byte[newLen];
        System.arraycopy(data, 0, newData, 0, length);

        // 在新数组末尾填充所需数据, 本例中使用填充数据的长度作为填充数据字节值
        var pad = (byte) (newLen - length);
        for (var i = length; i < newLen; i++) {
            newData[i] = pad;
        }
        return newData;
    }

    /**
     * 对称加密, 将给定的字节串进行加密
     *
     * @param keySpec       对称加密密钥
     * @param parameterSpec 加密初始化向量
     * @param data          明文数据
     * @return 密文数据
     */
    public byte[] encrypt(SecretKeySpec keySpec, AlgorithmParameterSpec parameterSpec, byte[] data) {
        // 实例化密码学对象
        var cipher = makeCipherInstance(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        try {
            // 将输入数据进行填充补齐后进行加密, 返回密文
            return cipher.doFinal(appendPadding(data, data.length));
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 对称加密, 对给定的输入流进行加密, 将密文数据写入输出流
     *
     * @param keySpec       对称加密密钥
     * @param parameterSpec 加密初始化向量
     * @param in            读入明文数据的流
     * @param out           输出密文数据的流
     * @return 加密数据的长度
     */
    @SuppressWarnings("resource")
    public long encrypt(SecretKeySpec keySpec, AlgorithmParameterSpec parameterSpec, InputStream in, OutputStream out) {
        // 实例化密码学对象
        var cipher = makeCipherInstance(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

        // 注意, 该对象不能 close, 否则可能会导致填充数据写入错误
        var cout = new CipherOutputStream(out, cipher);
        var buffer = new byte[BUFFER_SIZE];

        var total = 0L;
        var count = 0;
        try {
            // 从输入流中读取明文数据, 写入 CipherOutputStream 流中进行加密
            while ((count = in.read(buffer)) > 0) {
                // 判断是否需要进行填充操作
                // 如果读取的明文数据未写满缓冲数组, 则表示已无更多输入数据, 且该段数据长度可能不满足 8/16 的整数倍
                // 当然, 只有填充方式为 NoPadding 时才需要这段代码进行处理
                if (count < buffer.length) {
                    // 对缓冲区数据进行填充补齐
                    var paddingData = appendPadding(buffer, count);
                    // 如果得到了一个新的数组, 则表示已经完成填充操作, 否则表示无需填充
                    if (paddingData != buffer) {
                        // 写入填充后的数据
                        cout.write(paddingData);
                        // 以完成填充的数据长度为本次写入长度
                        count = paddingData.length;
                    } else {
                        // 无需进行手动填充操作 (填充模式不是 NoPadding, 或数据长度恰好是 8/16 的整数倍)
                        cout.write(buffer, 0, count);
                    }
                } else {
                    cout.write(buffer, 0, count);
                }
                total += count;
            }

            // 追加 padding 部分, 这部分只有填充模式不为 NoPadding 才有有效返回, 为程序自动计算的填充值
            var paddingData = cipher.doFinal();
            if (paddingData.length > 0) {
                // 将自动计算的填充值写入输出流, 注意, 这部分数据已经完成加密, 不能重复写入 CipherOutputStream 流
                out.write(paddingData);
                total += paddingData.length;
            }

            return total;
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 从解密后的明文中去掉填充数据
     *
     * <p>
     * 只有填充方式为 {@code NoPadding} 时, 表示算法本身不会处理填充数据, 在加密时, 会把填充数据作为明文一起进行加密, 也就表示解密时
     * 得到的明文中也包含了填充数据. 此时需要开发者自行处理得到的明文, 以去掉填充数据
     * </p>
     *
     * <p>
     * 其它填充方式 (如 {@code PKCS5Padding}) 会被算法自行处理, 可跳过此步骤
     * </p>
     *
     * @param data 解密后的明文
     * @return 去掉填充数据的明文数据
     */
    public byte[] removePadding(byte[] data) {
        // 对于非 NoPadding 填充方式的算法, 跳过此步骤
        if (!NO_PADDING.equals(padding)) {
            return data;
        }

        // 这里先假设最后一个字节为填充字节, 获取填充数据长度
        // 按本例算法, 填充的字节值为填充数据长度
        var padLen = (int) data[data.length - 1];

        // 根据假设的填充数据长度, 计算明文数据的原始长度
        var dataLen = data.length - padLen;

        // 遍历假设的填充数据, 确认这部分数据是否真的是填充数据
        for (var i = dataLen; i < data.length; i++) {
            // 如果填充的数据不符合预期, 则认为没有填充数据, 返回原始明文数据
            if (data[i] != (byte) padLen) {
                return data;
            }
        }

        // 去掉填充数据部分, 获取正确的明文数据内容
        return Arrays.copyOf(data, dataLen);
    }

    /**
     * 对称解密, 将给定的字节串解密为明文
     *
     * @param keySpec       对称加密密钥对象
     * @param parameterSpec 加密初始化向量
     * @param data          密文数据
     * @return 明文数据
     */
    public byte[] decrypt(SecretKeySpec keySpec, AlgorithmParameterSpec parameterSpec, byte[] data) {
        // 实例化密码学对象
        var cipher = makeCipherInstance(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
        try {
            // 将密文进行解密, 并对明文进行去除填充数据操作
            // 当然, 如果填充方式不为 NoPadding, 则无需进行去除填充操作
            return removePadding(cipher.doFinal(data));
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 对称解密, 对给定的输入流内容解密后写入给定的输出流
     *
     * <p>
     * 注意, 如果填充方式为 {@code NoPadding}, 表示算法本身不会处理填充数据, 在加密时, 会把填充数据作为明文一起进行加密,
     * 也就表示解密时
     * 得到的明文中也包含了填充数据. 此时需要开发者自行处理得到的明文, 以去掉填充数据
     * </p>
     *
     * <p>
     * 其它填充方式 (如 {@code PKCS5Padding}) 会被算法自行处理, 开发者无需关注
     * </p>
     *
     * @param keySpec       对称密钥对象
     * @param parameterSpec 加密初始化向量
     * @param in            读取密文的输入流
     * @param out           写入明文的输出流
     * @return 明文数据长度
     */
    public long decrypt(SecretKeySpec keySpec, AlgorithmParameterSpec parameterSpec, InputStream in, OutputStream out) {
        // 实例化密码学对象
        var cipher = makeCipherInstance(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

        var total = 0L;
        var count = 0;

        var buffer = new byte[BUFFER_SIZE];
        try (var cin = new CipherInputStream(in, cipher)) {
            // 从 CipherInputStream 读取解密后的明文数据, 写入结果流中
            while ((count = cin.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                total += count;
            }
            return total;
        } catch (IOException e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 创建非对称加密密钥对
     *
     * @return 密钥对对象
     */
    public SecretKeyPairSpec makeSecretKeyPair() throws NoSuchAlgorithmException {
        // 获取密钥生成器
        var keyPairGenerator = KeyPairGenerator.getInstance(algorithm.algorithm());

        // 根据密钥长度初始化密钥生成器
        keyPairGenerator.initialize(algorithm.keySize());

        // 产生非对称密钥
        var keyPair = keyPairGenerator.generateKeyPair();
        return new SecretKeyPairSpec(
            algorithm.algorithm(),
            keyPair.getPrivate().getEncoded(),
            keyPair.getPublic().getEncoded());
    }

    /**
     * 利用私钥对数据进行签名
     *
     * @param hashAlgorithm 签名算法, 例如 MD5, SHA1, SHA256 等
     * @param privateKey    私钥对象
     * @param data          要签名的数据
     * @return 签名
     */
    public byte[] sign(String hashAlgorithm, PrivateKey privateKey, byte[] data) {
        // 通过签名算法名称产生签名算法
        // 签名算法名称由 "摘要算法" + with + "非对称加密算法" 组成, 例如 "SHA256withRSA"
        try {
            var sign = Signature.getInstance(hashAlgorithm + "with" + algorithm);
            sign.initSign(privateKey);

            sign.update(data);
            return sign.sign();
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 利用私钥对 {@link InputStream} 流中的数据进行签名
     *
     * @param hashAlgorithm 签名算法, 例如 MD5, SHA1, SHA256 等
     * @param privateKey    私钥对象
     * @param in            输入流
     * @return 签名
     */
    public byte[] sign(String hashAlgorithm, PrivateKey privateKey, InputStream in) {
        // 通过签名算法名称产生签名算法
        // 签名算法名称由 "摘要算法" + with + "非对称加密算法" 组成, 例如 "SHA256withRSA"
        try {
            var sign = Signature.getInstance(hashAlgorithm + "with" + algorithm);
            sign.initSign(privateKey);

            var buffer = new byte[BUFFER_SIZE];

            var count = 0;
            while ((count = in.read(buffer)) > 0) {
                sign.update(buffer, 0, count);
            }
            return sign.sign();
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 利用公钥对签名进行验签
     *
     * @param hashAlgorithm 签名算法, 例如 MD5, SHA1, SHA256 等
     * @param publicKey     公钥对象
     * @param data          被签名的数据
     * @param signature     要验证的签名
     * @return 是否验签成功
     */
    public boolean verifySign(String hashAlgorithm, PublicKey publicKey, byte[] data, byte[] signature) {
        try {
            // 通过签名算法名称产生签名算法
            // 签名算法名称由 "摘要算法" + with + "非对称加密算法" 组成, 例如 "SHA256withRSA"
            var sign = Signature.getInstance(hashAlgorithm + "with" + algorithm);

            sign.initVerify(publicKey);
            sign.update(data);

            return sign.verify(signature);
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 利用公钥对签名进行验签
     *
     * @param hashAlgorithm 签名算法, 例如 {@code MD5}, {@code SHA1}, {@code SHA256} 等
     * @param publicKey     公钥对象
     * @param in            被签名的数据输入流
     * @param signature     要验证的签名
     * @return 是否验签成功
     */
    public boolean verifySign(String hashAlgorithm, PublicKey publicKey, InputStream in, byte[] signature) {
        try {
            // 通过签名算法名称产生签名算法
            // 签名算法名称由 "摘要算法" + with + "非对称加密算法" 组成, 例如 "SHA256withRSA"
            var sign = Signature.getInstance(hashAlgorithm + "with" + algorithm);

            sign.initVerify(publicKey);

            var buffer = new byte[BUFFER_SIZE];
            var count = 0;
            while ((count = in.read(buffer)) > 0) {
                sign.update(buffer, 0, count);
            }
            return sign.verify(signature);
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 计算非对称加密数据块大小
     *
     * <p>
     * 对于非对称加密 (或解密), 每次只能处理不大于指定块大小的数据
     * </p>
     *
     * @param key     密钥
     * @param encrypt 加密 (或解密) 方式
     */
    private int calculateRSABlockSize(Key key, boolean encrypt) {
        int blockSize = ((RSAKey) key).getModulus().bitLength() / BYTE_SIZE;
        if (encrypt) {
            // 对于不同的填充算法, 在加密时块大小要减去填充数据大小
            blockSize -= switch (padding == null ? "" : padding) {
            case "", "PKCS5Padding" -> 11;
            case "OAEPWithSHA-256AndMGF1Padding" -> 66;
            default -> 11;
            };
        }
        return blockSize;
    }

    /**
     * 通过非对称算法, 对一个字节串进行加密操作
     *
     * @param key  密钥对象, 一般为公钥对象
     * @param data 明文数据
     * @return 密文数据
     */
    public byte[] encrypt(Key key, byte[] data) {
        // 计算加密块大小
        var blockSize = calculateRSABlockSize(key, true);

        // 产生密码对象
        var cipher = makeCipherInstance(Cipher.ENCRYPT_MODE, key, null);

        // 产生一个内存输出流, 存放加密后的密文数据流
        try (var out = new ByteArrayOutputStream()) {
            // 按计算得到的块大小进行分块加密
            for (int i = 0; i < data.length; i += blockSize) {
                // 将每块数据加密后, 将得到的密文写入输出流
                out.write(cipher.doFinal(data, i, Math.min(data.length - i, blockSize)));
            }

            out.flush();
            // 返回密文结果
            return out.toByteArray();
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 通过非对称算法, 对一段密文进行解密操作
     *
     * @param key  密钥对象, 一般为私钥对象, 必须和加密密钥对应
     * @param data 密文数据
     * @return 明文数据
     */
    public byte[] decrypt(Key key, byte[] data) {
        // 计算解密块大小
        var blockSize = calculateRSABlockSize(key, false);

        // 产生密码对象
        var cipher = makeCipherInstance(Cipher.DECRYPT_MODE, key, null);

        // 产生一个内存输出流, 存放解密后的明文数据流
        try (var out = new ByteArrayOutputStream()) {
            // 按计算得到的块大小进行分块解密
            for (int i = 0; i < data.length; i += blockSize) {
                // 将每块数据解密后, 将得到的明文写入输出流
                out.write(cipher.doFinal(data, i, Math.min(data.length - i, blockSize)));
            }

            out.flush();
            // 返回明文结果
            return out.toByteArray();
        } catch (Exception e) {
            throw new CiphersException(e);
        }
    }

    /**
     * 通过非对称算法, 对输入流的数据进行加密操作, 将密文写入到输出流中
     *
     * @param key    密钥对象, 一般为公钥对象
     * @param input  读取原数据的输入流
     * @param output 写入密文数据的输出流
     * @return 处理的字节数
     */
    public long encrypt(Key key, InputStream input, OutputStream output) {
        // 计算加密块大小
        var blockSize = calculateRSABlockSize(key, true);

        // 产生密码对象
        var cipher = makeCipherInstance(Cipher.ENCRYPT_MODE, key, null);

        // 缓冲区, 每次缓冲一个块的数据
        var buffer = new byte[blockSize];

        var total = 0L;
        var count = 0;
        try {
            while ((count = input.read(buffer)) > 0) {
                // 将输入流的数据按块大小读取, 加密后写入到输出流中
                var data = cipher.doFinal(buffer, 0, count);
                output.write(data);

                // 计算处理的字节数
                total += data.length;
            }
        } catch (Exception e) {
            throw new CiphersException(e);
        }
        return total;
    }

    /**
     * 通过非对称算法, 对输入流的密文进行解密操作, 将解密后的明文写入到输出流中
     *
     * @param key 密钥对象, 一般为私钥对象, 需和加密的密钥对应
     * @param in  读取密文数据的输入流
     * @param out 写入解密后明文数据的输出流
     * @return 处理的字节数
     */
    public long decrypt(Key key, InputStream in, OutputStream out) {
        // 计算解密块大小
        var blockSize = calculateRSABlockSize(key, false);

        // 产生密码对象
        var cipher = makeCipherInstance(Cipher.DECRYPT_MODE, key, null);

        // 缓冲区, 每次缓冲一个块的数据
        var buffer = new byte[blockSize];

        var count = 0;
        var total = 0L;
        try {
            while ((count = in.read(buffer)) > 0) {
                // 将输入流的数据按块大小读取, 解密后写入到输出流中
                var data = cipher.doFinal(buffer, 0, count);
                out.write(data);

                // 计算处理的字节数
                total += data.length;
            }
        } catch (Exception e) {
            throw new CiphersException(e);
        }
        return total;
    }

    /**
     * 表示各类密码算法的枚举类型
     */
    public enum Algorithm {
        /**
         * DES 算法
         */
        DES("DES", 56, 8, 8),

        /**
         * 3DES 算法
         */
        DESede("DESede", 168, 8, 8),

        /**
         * AES 算法
         */
        AES("AES", 128, 16, 16),

        /**
         * RC2 算法
         */
        RC2("RC2", 0, 8, 8),

        /**
         * RC4 算法
         */
        RC4("RC4", 0, 0, 8),

        /**
         * RSA 算法
         */
        RSA("RSA", 2048, 0, 16);

        // 算法名称
        private final String name;

        // 默认密钥长度
        private final int keySize;

        // 默认初始化向量长度
        private final int ivSize;

        // 数据填充块大小
        private final int paddingSize;

        /**
         * 构造器, 构造一个算法枚举项
         *
         * @param name        算法名称
         * @param keySize     默认密钥长度
         * @param ivSize      默认初始化向量长度, {@code 0} 表示无需初始化向量
         * @param paddingSize 数据填充块大小, {@code 0} 表示无需填充数据
         */
        Algorithm(String name, int keySize, int ivSize, int paddingSize) {
            this.name = name;
            this.keySize = keySize;
            this.ivSize = ivSize;
            this.paddingSize = paddingSize;
        }

        /**
         * 获取算法名称
         *
         * @return 算法名称
         */
        public String algorithm() {
            return name;
        }

        /**
         * 获取默认密钥长度
         *
         * @return 默认密钥长度
         */
        public int keySize() {
            return keySize;
        }

        /**
         * 获取默认初始化向量长度
         *
         * @return 默认初始化向量长度
         */
        public int ivSize() {
            return ivSize;
        }

        /**
         * 获取填充块大小
         *
         * @return 数据填充块大小
         */
        public int paddingSize() {
            return paddingSize;
        }
    }
}
