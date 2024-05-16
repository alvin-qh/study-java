package alvin.study.se.security.algorithms;

import alvin.study.se.security.util.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Ciphers} 类型, 使用对称和非对称方式对数据进行加密
 */
class CiphersTest {
    /**
     * 测试 {@link Ciphers#encrypt(java.security.Key, byte[]) Ciphers.encrypt(Key, byte[])} 方法和
     * {@link Ciphers#decrypt(java.security.Key, byte[]) Ciphers.decrypt(Key, byte[])} 方法, 根据
     * 所给的算法名称对字节串进行对称加解密
     */
    @CsvSource({
        "DES",
        "DES/CBC/NoPadding",
        "DES/CBC/PKCS5Padding",
        "DES/CBC/ISO10126Padding",
        "DES/ECB/NoPadding",
        "DES/ECB/PKCS5Padding",
        "DES/ECB/ISO10126Padding",
        "DESede",
        "DESede/CBC/NoPadding",
        "DESede/CBC/PKCS5Padding",
        "DESede/CBC/ISO10126Padding",
        "DESede/ECB/NoPadding",
        "DESede/ECB/PKCS5Padding",
        "DESede/ECB/ISO10126Padding",
        "AES",
        "AES/CBC/NoPadding",
        "AES/CBC/PKCS5Padding",
        "AES/CBC/ISO10126Padding",
        "AES/ECB/NoPadding",
        "AES/ECB/PKCS5Padding",
        "AES/ECB/ISO10126Padding",
        "RC2",
        "RC2/ECB/NoPadding",
        "RC2/ECB/PKCS5Padding",
        "RC2/CBC/NoPadding",
        "RC2/CBC/PKCS5Padding",
        "RC4"
    })
    @ParameterizedTest
    void symmetric_shouldEncryptAndDecryptBytes(String algorithmsName) throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        // 根据算法名称实例化对象
        var cipher = new Ciphers(algorithmsName);

        // 产生随机密钥
        var spec = cipher.makeSecretKeyParameterSpec();

        // 通过密钥进行加密运算
        var encData = cipher.encrypt(spec.getSecretKeySpec(), spec.getParameterSpec(), data);
        // 确认加密成功
        then(encData).isNotEqualTo(data);

        // 通过密钥进行解密运算
        var decData = cipher.decrypt(spec.getSecretKeySpec(), spec.getParameterSpec(), encData);
        // 确认解密成功
        then(decData).isEqualTo(data);
    }

    /**
     * 测试 {@link Ciphers#encrypt(java.security.Key, InputStream, OutputStream)
     * Ciphers.encrypt(Key, InputStream, OutputStream)} 方法和
     * {@link Ciphers#decrypt(java.security.Key, InputStream, OutputStream)
     * Ciphers.decrypt(Key, InputStream, OutputStream)} 方法, 根据所给的算法名称对输入流进行对称加解密
     */
    @CsvSource({
        "DES",
        "DES/CBC/NoPadding",
        "DES/CBC/PKCS5Padding",
        "DES/CBC/ISO10126Padding",
        "DES/ECB/NoPadding",
        "DES/ECB/PKCS5Padding",
        "DES/ECB/ISO10126Padding",
        "DESede",
        "DESede/CBC/NoPadding",
        "DESede/CBC/PKCS5Padding",
        "DESede/CBC/ISO10126Padding",
        "DESede/ECB/NoPadding",
        "DESede/ECB/PKCS5Padding",
        "DESede/ECB/ISO10126Padding",
        "AES",
        "AES/CBC/NoPadding",
        "AES/CBC/PKCS5Padding",
        "AES/CBC/ISO10126Padding",
        "AES/ECB/NoPadding",
        "AES/ECB/PKCS5Padding",
        "AES/ECB/ISO10126Padding",
        "RC2",
        "RC2/ECB/NoPadding",
        "RC2/ECB/PKCS5Padding",
        "RC2/CBC/NoPadding",
        "RC2/CBC/PKCS5Padding",
        "RC4"
    })
    @ParameterizedTest
    void symmetric_shouldEncryptFromInputStreamAndDecryptToOutputStream(String algorithmsName) throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        // 根据算法名称实例化对象
        var cipher = new Ciphers(algorithmsName);

        // 根据密钥和 iv 的大小创建密钥对象
        var spec = cipher.makeSecretKeyParameterSpec();

        byte[] encData;
        try (var in = new ByteArrayInputStream(data)) {
            try (var out = new ByteArrayOutputStream()) {
                // 将输入流的内容加密后写入输出流
                cipher.encrypt(spec.getSecretKeySpec(), spec.getParameterSpec(), in, out);

                // 保存加密数据
                out.flush();
                encData = out.toByteArray();
            }
        }

        byte[] decData;
        try (var in = new ByteArrayInputStream(encData)) {
            try (var out = new ByteArrayOutputStream()) {
                // 将输入流的内容解密后写入输出流
                cipher.decrypt(spec.getSecretKeySpec(), spec.getParameterSpec(), in, out);

                // 保存解密数据
                out.flush();
                // 对于填充模式为 NoPadding, 解密的明文需要进行去除填充数据处理
                decData = cipher.removePadding(out.toByteArray());
            }
        }

        // 确认数据加密并解密后仍为原数据
        then(decData).isEqualTo(data);
    }

    /**
     * 测试通过 {@link Ciphers#sign(String, java.security.PrivateKey, byte[])
     * Ciphers.sign(String, PrivateKey, byte[])} 对一个字节串进行签名, 以及通过
     * {@link Ciphers#verifySign(String, java.security.PublicKey, byte[], byte[])
     * Ciphers.verifySign(String, PublicKey, byte[], byte[])} 对同一个字节串进行验签
     *
     * <p>
     * 按照算法规则, 通过公钥对一个字节串进行签名, 并通过私钥对同样的字节串进行验签
     * </p>
     *
     * <p>
     * 注意: 一般来说, 对于非对称加密, 公钥用于加密和验签, 而私钥用于解密和签名
     * </p>
     */
    @Test
    void signature_shouldSignAndVerityFromBytesByRSA() throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        var cipher = new Ciphers("RSA");

        // 产生密钥对
        var keyPair = cipher.makeSecretKeyPair();

        // 通过私钥签名
        var signature = cipher.sign("SHA256", keyPair.generatePrivateKey(), data);

        // 通过公钥验签
        then(cipher.verifySign("SHA256", keyPair.generatePublicKey(), data, signature)).isTrue();
    }

    /**
     * 测试通过 {@link Ciphers#sign(String, java.security.PrivateKey, InputStream)
     * Ciphers.sign(String, PrivateKey, InputStream)} 对一个输入流进行签名, 以及通过
     * {@link Ciphers#verifySign(String, java.security.PublicKey, InputStream, byte[])
     * Ciphers.verifySign(String, PublicKey, InputStream, byte[])} 对一个输入流进行验签
     *
     * <p>
     * 按照算法规则, 通过公钥对一个字节串进行签名, 并通过私钥对同样的字节串进行验签
     * </p>
     *
     * <p>
     * 注意: 一般来说, 对于非对称加密, 公钥用于加密和验签, 而私钥用于解密和签名
     * </p>
     */
    @Test
    void signature_shouldSignAndVerityFromInputStreamByRSA() throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        var cipher = new Ciphers("RSA");
        var keyPair = cipher.makeSecretKeyPair();

        byte[] signature;
        try (var in = new ByteArrayInputStream(data)) {
            // 通过私钥签名
            signature = cipher.sign("SHA256", keyPair.generatePrivateKey(), in);
        }

        try (var in = new ByteArrayInputStream(data)) {
            // 通过公钥验签
            then(cipher.verifySign("SHA256", keyPair.generatePublicKey(), in, signature)).isTrue();
        }
    }

    /**
     * 测试通过 {@link Ciphers#encrypt(java.security.Key, byte[]) Ciphers.encrypt(Key, byte[])} 方法进行加密, 并通过
     * {@link Ciphers#decrypt(java.security.Key, byte[]) Ciphers.decrypt(Key, byte[])} 方法进行解密
     *
     * <p>
     * 按照算法规则, 通过公钥对一个字节串进行加密, 得到密文, 再通过私钥对密文进行解密
     * </p>
     *
     * <p>
     * 注意: 一般来说, 对于非对称加密, 公钥用于加密和验签, 而私钥用于解密和签名
     * </p>
     */
    @CsvSource({
        "RSA",
        "RSA/ECB/PKCS1Padding",
        "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
    })
    @ParameterizedTest
    void asymmetric_shouldEncryptAndDecryptBytes(String algorithmsName) throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        // 根据名称实例化算法对象
        var cipher = new Ciphers(algorithmsName);
        // 产生一对密钥
        var keyPair = cipher.makeSecretKeyPair();

        // 用公钥进行加密
        var encData = cipher.encrypt(keyPair.generatePublicKey(), data);
        // 用私钥进行解密
        var decData = cipher.decrypt(keyPair.generatePrivateKey(), encData);

        // 确认解密的数据和原数据一致
        then(decData).isEqualTo(data);

        // 填充模式为 "OAEP" 的算法模式无法使用私钥加密, 公钥解密
        // 标准的非对称算法应该是使用公钥加密, 私钥解密, 而不应该反过来 (因为公钥解密并不安全)
        // 但除过 OAEP 的 RSA 算法仍支持通过私钥加密和公钥解密的用法
        if (cipher.getPadding() == null || !cipher.getPadding().startsWith("OAEP")) {
            cipher = new Ciphers(algorithmsName);
            keyPair = cipher.makeSecretKeyPair();

            // 用私钥进行加密
            encData = cipher.encrypt(keyPair.generatePrivateKey(), data);
            // 用公钥进行解密
            decData = cipher.decrypt(keyPair.generatePublicKey(), encData);

            // 确认解密的数据和原数据一致
            then(decData).isEqualTo(data);
        }
    }

    /**
     * 测试通过 {@link Ciphers#encrypt(java.security.Key, byte[]) Ciphers.encrypt(Key, byte[])} 方法进行加密, 并通过
     * {@link Ciphers#decrypt(java.security.Key, byte[]) Ciphers.decrypt(Key, byte[])} 方法进行解密
     *
     * <p>
     * 按照算法规则, 通过公钥对一个字节串进行加密, 得到密文, 再通过私钥对密文进行解密
     * </p>
     *
     * <p>
     * 注意: 一般来说, 对于非对称加密, 公钥用于加密和验签, 而私钥用于解密和签名
     * </p>
     */
    @CsvSource({
        "RSA",
        "RSA/ECB/PKCS1Padding",
        "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
    })
    @ParameterizedTest
    void asymmetric_shouldEncryptFromInputStreamAndDecryptToOutputStream(String algorithmsName) throws Exception {
        // 产生测试数据
        var data = DataGenerator.generate(1315);
        then(data).hasSize(1315);

        // 根据名称实例化算法对象
        var cipher = new Ciphers(algorithmsName);
        // 产生一对密钥
        var keyPair = cipher.makeSecretKeyPair();

        var encData = (byte[]) null;
        try (var in = new ByteArrayInputStream(data)) {
            try (var out = new ByteArrayOutputStream()) {
                // 将输入流的内容加密后写入输出流
                cipher.encrypt(keyPair.generatePublicKey(), in, out);

                // 获取密文数据
                out.flush();
                encData = out.toByteArray();
            }
        }

        var decData = (byte[]) null;
        try (var in = new ByteArrayInputStream(encData)) {
            try (var out = new ByteArrayOutputStream()) {
                // 将输入流的内容解密后写入输出流
                cipher.decrypt(keyPair.generatePrivateKey(), in, out);

                // 获取明文数据
                out.flush();
                decData = out.toByteArray();
            }
        }
        // 确认解密后的明文数据和原数据一致
        then(decData).isEqualTo(data);

        // 填充模式为 "OAEP" 的算法模式无法使用私钥加密, 公钥解密
        // 标准的非对称算法应该是使用公钥加密, 私钥解密, 而不应该反过来 (因为公钥解密并不安全)
        // 但除过 OAEP 的 RSA 算法仍支持通过私钥加密和公钥解密的用法
        if (cipher.getPadding() == null || !cipher.getPadding().startsWith("OAEP")) {
            cipher = new Ciphers(algorithmsName);
            keyPair = cipher.makeSecretKeyPair();

            try (var in = new ByteArrayInputStream(data)) {
                try (var out = new ByteArrayOutputStream()) {
                    // 将输入流的内容加密后写入输出流
                    cipher.encrypt(keyPair.generatePrivateKey(), in, out);

                    // 获取密文数据
                    out.flush();
                    encData = out.toByteArray();
                }
            }

            try (var in = new ByteArrayInputStream(encData)) {
                try (var out = new ByteArrayOutputStream()) {
                    // 将输入流的内容解密后写入输出流
                    cipher.decrypt(keyPair.generatePublicKey(), in, out);

                    // 获取明文数据
                    out.flush();
                    decData = out.toByteArray();
                }
            }
            // 确认解密后的明文数据和原数据一致
            then(decData).isEqualTo(data);
        }
    }
}
