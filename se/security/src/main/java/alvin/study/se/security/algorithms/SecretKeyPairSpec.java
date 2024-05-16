package alvin.study.se.security.algorithms;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 非对称加密密钥参数类型
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class SecretKeyPairSpec {
    // 算法名称
    private final String algorithm;

    // 私钥数据
    private final byte[] privateKey;

    // 公钥数据
    private final byte[] publicKey;

    /**
     * 获取公钥 {@link PublicKey} 对象
     *
     * @return 公钥 {@link PublicKey} 类型对象
     */
    public PublicKey generatePublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        var keySpec = new X509EncodedKeySpec(publicKey);
        return KeyFactory.getInstance(algorithm).generatePublic(keySpec);
    }

    /**
     * 获取私钥 {@link PrivateKey} 对象
     *
     * @return 私钥 {@link PrivateKey} 类型对象
     */
    public PrivateKey generatePrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        var keySpec = new PKCS8EncodedKeySpec(privateKey);
        return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
    }
}
