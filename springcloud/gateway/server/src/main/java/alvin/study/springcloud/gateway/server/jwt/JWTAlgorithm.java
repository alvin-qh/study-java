package alvin.study.springcloud.gateway.server.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * JWT 签名算法枚举
 */
public enum JWTAlgorithm {
    RSA256("RS256"),
    RSA384("RSA384"),
    RSA512("RSA512"),
    HMAC256("HMAC256"),
    HMAC384("HMAC384"),
    HMAC512("HMAC512"),
    ECDSA256("ECDSA256"),
    ECDSA384("ECDSA384"),
    ECDSA512("ECDSA512");

    // 签名算法名称
    private String name;

    /**
     * 构造器, 设置签名算法的名称
     *
     * @param name 签名算法名称
     */
    JWTAlgorithm(String name) {
        this.name = name;
    }

    /**
     * 获取签名算法的名称
     *
     * @return 签名算法名称
     */
    public String getName() { return name; }

    /**
     * 根据密钥创建密码学算法对象
     *
     * @param securityKey 密钥
     * @return 密码学算法对象
     */
    @SneakyThrows
    public Algorithm build(String securityKey) {
        return switch (this) {
            case RSA256 -> Algorithm.RSA256(null, decodeRSAPrivateKey(securityKey));
            case RSA384 -> Algorithm.RSA384(null, decodeRSAPrivateKey(securityKey));
            case RSA512 -> Algorithm.RSA512(null, decodeRSAPrivateKey(securityKey));
            case ECDSA256 -> Algorithm.ECDSA256(null, decodeECPrivateKey(securityKey));
            case ECDSA384 -> Algorithm.ECDSA384(null, decodeECPrivateKey(securityKey));
            case ECDSA512 -> Algorithm.ECDSA512(null, decodeECPrivateKey(securityKey));
            case HMAC256 -> Algorithm.HMAC256(securityKey);
            case HMAC384 -> Algorithm.HMAC384(securityKey);
            case HMAC512 -> Algorithm.HMAC512(securityKey);
        };
    }

    /**
     * 将给定的密钥字符串构造为 RSA 私钥对象
     *
     * @param securityKey 密钥字符串
     * @return RSA 私钥对象
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private RSAPrivateKey decodeRSAPrivateKey(String securityKey)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        // 将密钥字符串进行解码, 获取原始 Byte 数据
        var keyData = Base64.decodeBase64(securityKey);
        // 产生 RSA 密钥对象
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyData));
    }

    /**
     * 将给定的密钥字符串构造为 EC 私钥对象
     *
     * @param securityKey 密钥字符串
     * @return EC 私钥对象
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private ECPrivateKey decodeECPrivateKey(String securityKey)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        // 将密钥字符串进行解码, 获取原始 Byte 数据
        var keyData = Base64.decodeBase64(securityKey);
        // 产生 EC 密钥对象
        return (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(keyData));
    }
}
