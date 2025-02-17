package alvin.study.se.security.algorithms;

import com.google.common.annotations.VisibleForTesting;

import jakarta.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Pattern;

/**
 * 产生 RSA 加密算法密钥
 *
 * <p>
 * 该类从文本文件 (或字符串) 中读取密钥, 并根据所定义的格式对密钥进行解码处理, 得到 RSA 算法所需的公私钥密钥对
 * </p>
 */
public class RSAKeyLoader {
    // 公钥内容起始标识
    private static final String PUB_KEY_START = "-----BEGIN PUBLIC KEY-----";
    // 公钥内容结束标识
    private static final String PUB_KEY_END = "-----END PUBLIC KEY-----";

    // 私钥内容起始标识
    private static final String PRI_KEY_START = "-----BEGIN PRIVATE KEY-----";
    // 私钥内容结束标识
    private static final String PRI_KEY_END = "-----END PRIVATE KEY-----";

    // 空白字符正则
    private static final Pattern BLANK_REG = Pattern.compile("[\r\n\t ]", Pattern.MULTILINE);

    // 算法名称
    private final String algorithm;

    /**
     * 构造器, 默认算法名称为 {@code "RSA"}
     */
    public RSAKeyLoader() {
        this("RSA");
    }

    /**
     * 构造器, 设置算法名称
     *
     * @param algorithm 算法名称
     */
    public RSAKeyLoader(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 将给定的密钥文本字符串转为公钥字节串
     *
     * @param keyString 密钥文本字符串
     * @return 公钥字节串
     */
    @VisibleForTesting
    static byte[] decodePublicKey(String keyString) {
        return Base64.decodeBase64(fixKeyString(keyString, PUB_KEY_START, PUB_KEY_END));
    }

    /**
     * 将给定的密钥文本字符串转为私钥字节串
     *
     * @param keyString 密钥文本字符串
     * @return 私钥字节串
     */
    @VisibleForTesting
    static byte[] decodePrivateKey(String keyString) {
        return Base64.decodeBase64(fixKeyString(keyString, PRI_KEY_START, PRI_KEY_END));
    }

    /**
     * 将密钥定义文本中有效的部分取出
     *
     * @param keyString 密钥定义文本字符串
     * @param beginLine 密钥起始标识符
     * @param endLine   密钥结束标识符
     * @return 密钥的有效部分
     */
    private static String fixKeyString(@Nonnull String keyString, String beginLine, String endLine) {
        /// 定位到密钥有效部分的起始位置
        var start = keyString.indexOf(beginLine);
        if (start < 0) {
            start = 0;
        } else {
            start += beginLine.length();
        }

        /// 定位到密钥有效部分的结束位置
        var end = keyString.lastIndexOf(endLine);
        if (end < 0) {
            end = keyString.length() - 1;
        }

        /// 去除密钥中的空白字符
        var matcher = BLANK_REG.matcher(keyString.substring(start, end));
        return matcher.replaceAll("");
    }

    /**
     * 将给定的字节串转化为 {@link java.security.interfaces.RSAPublicKey RSAPublicKey} 对象
     *
     * @param keyData 保存密钥的字节串
     * @return {@link java.security.interfaces.RSAPublicKey RSAPublicKey} 公钥对象
     * @see KeyFactory#getInstance(String)
     * @see KeyFactory#generatePublic(java.security.spec.KeySpec)
     * @see X509EncodedKeySpec(byte[])
     */
    public PublicKey loadPublicKey(byte[] keyData) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyData));
        } catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    /**
     * 将给定的密钥定义文本转化为 {@link java.security.interfaces.RSAPublicKey RSAPublicKey} 对象
     *
     * @param keyString 保存密钥定义的文本字符串
     * @return {@link java.security.interfaces.RSAPublicKey RSAPublicKey} 公钥对象
     * @see #loadPublicKey(byte[])
     * @see #decodePublicKey(String)
     */
    public PublicKey loadPublicKey(String keyString) throws InvalidKeySpecException {
        return loadPublicKey(decodePublicKey(keyString));
    }

    /**
     * 将给定的字节串转化为 {@link java.security.interfaces.RSAPrivateKey RSAPrivateKey} 对象
     *
     * @param keyData 保存密钥的字节串
     * @return {@link java.security.interfaces.RSAPrivateKey RSAPrivateKey} 私钥对象
     * @see KeyFactory#getInstance(String)
     * @see KeyFactory#generatePrivate(java.security.spec.KeySpec)
     * @see PKCS8EncodedKeySpec(byte[])
     */
    public PrivateKey loadPrivateKey(byte[] keyData) throws InvalidKeySpecException {
        try {
            // formatter:off
            /* Add PKCS#8 formatting */
            // var v2 = new ASN1EncodableVector();
            // v2.add(newASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
            // v2.add(DERNull.INSTANCE);

            // var v1 = new ASN1EncodableVector();
            // v1.add(new ASN1Integer(0));
            // v1.add(new DERSequence(v2));
            // v1.add(new DEROctetString(keyBytes));
            // var seq = new DERSequence(v1);
            // keyData = seq.getEncoded("DER");
            // System.out.println(Base64.encodeBase64String(keyData));
            // formatter:on

            return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(keyData));
        } catch (NoSuchAlgorithmException /* | IOException */ ignore) {
            return null;
        }
    }

    /**
     * 将给定的密钥文本字符串转化为 {@link java.security.interfaces.RSAPrivateKey RSAPrivateKey}
     * 私钥对象
     *
     * @param keyString 保存密钥定义的文本字符串
     * @return {@link java.security.interfaces.RSAPrivateKey RSAPrivateKey} 私钥对象
     * @see #loadPrivateKey(byte[])
     * @see #decodePrivateKey(String)
     */
    public PrivateKey loadPrivateKey(String keyString) throws InvalidKeySpecException {
        return loadPrivateKey(decodePrivateKey(keyString));
    }
}
