package alvin.study.se.security.util;

import alvin.study.se.security.algorithms.RSAKeyLoader;
import com.google.common.io.ByteStreams;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * 从指定文件中读取 RSA 公私钥的工具类
 *
 * <p>
 * 为了简便期间, 本工具类规定公钥存储在 {@code <basename>.pub} 文件中, 私钥存储在 {@code <basename>} 文件中
 * </p>
 */
public class RSAKeyReader {
    // 创建密钥工厂对象
    private final RSAKeyLoader rsaKeyLoader = new RSAKeyLoader();

    // 密钥文件的基本名称, 为私钥文件的名称, 公钥文件名称为 '{baseName}.pub'
    private final String baseName;

    /**
     * 初始化对象, 设定密钥文件的 {@code basename} 值
     *
     * @param baseName 基本名称
     */
    public RSAKeyReader(String baseName) {
        this.baseName = baseName;
    }

    /**
     * 读取私钥
     *
     * @return RSA 私钥 {@link RSAPrivateKey} 对象
     */
    public RSAPrivateKey readPrivateKey() throws IOException, InvalidKeySpecException {
        try (var is = new FileInputStream(baseName)) {
            var key = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
            return (RSAPrivateKey) rsaKeyLoader.loadPrivateKey(key);
        }
    }

    /**
     * 读取公钥
     *
     * @return RSA 公钥 {@link RSAPublicKey} 对象
     */
    public RSAPublicKey readPublicKey() throws IOException, InvalidKeySpecException {
        try (var is = new FileInputStream(baseName + ".pub")) {
            var key = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
            return (RSAPublicKey) rsaKeyLoader.loadPublicKey(key);
        }
    }
}
