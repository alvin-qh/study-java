package alvin.study.springboot.springdoc.util.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 密码加密处理工具类
 *
 * <p>
 * 所谓加密, 实际上是对明文字符串进行 HMAC-xxxx (xxxx 为某种散列算法) 散列算法, 加密后的结果并无法还原.
 * 该算法的目的是对密码进行散列操作, 以防止密码明文泄露
 * </p>
 *
 * @see SecretKeySpec
 * @see Mac
 */
public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {
    // 密钥生成对象
    private final SecretKeySpec secretKeySpec;

    /**
     * 构造密钥对象
     *
     * @param algorithm 加密算法名称
     * @param hmacKey   用于密钥认证的 hmac (散列信息认证码)
     * @see SecretKeySpec#SecretKeySpec(byte[], String)
     */
    public PasswordEncoder(String algorithm, String hmacKey) {
        // 实例化密钥生成对象
        secretKeySpec = new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), algorithm);
    }

    /**
     * 获取密钥的 HMAC 对象 (表示散列信息认证的对象)
     *
     * @return {@link javax.crypto.Mac Mac} 对象
     * @throws NoSuchAlgorithmException 指定的加密算法无效
     * @throws InvalidKeyException      所给的 {@link SecretKeySpec} 对象表示的密钥无效
     * @see Mac#getInstance(String)
     * @see Mac#init(java.security.Key)
     */
    private Mac hmac() throws NoSuchAlgorithmException, InvalidKeyException {
        // 根据加密算法名称获取 Mac 类型的对应实例
        var mac = Mac.getInstance(secretKeySpec.getAlgorithm());
        // 初始化 Mac 对象
        mac.init(secretKeySpec);
        return mac;
    }

    /**
     * 加密字符串, 返回加密后 bytes 的 16 进制格式字符串
     *
     * @param rawPassword 待加密的字符串内容
     * @return 加密后的结果转化为 16 进制字符串
     * @see Mac#doFinal(byte[])
     * @see Hex#encodeHexString(byte[])
     */
    @Override
    public String encode(CharSequence rawPassword) {
        // 获取当前密钥对应的 Mac 对象
        try {
            var mac = hmac();
            // 将待加密字符串转化为 bytes 后进行加密
            var data = mac.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            // 将加密结果 bytes 转化为 16 进制字符串格式并返回
            return Hex.encodeHexString(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InternalError(e);
        }
    }

    /**
     * 验证源字符串和加密结果相符
     *
     * @param rawPassword     未加密的源字符串
     * @param encodedPassword 经过加密的字符串
     * @return 两个字符串是否相符, 即将源字符串进行加密后是否和加密字符串相同
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
