package alvin.study.se.security.algorithms;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import jakarta.annotation.Nullable;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

/**
 * 对称加密密钥和初始向量类型
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class SecretKeyParameterSpec {
    // 加密算法名称
    private final String algorithm;

    // 密钥字节串
    private final byte[] key;

    // 加密初始化向量
    private final byte[] iv;

    /**
     * 获取密钥对象
     *
     * @return 密钥对象
     */
    public SecretKeySpec getSecretKeySpec() { return new SecretKeySpec(key, algorithm); }

    /**
     * 获取加密向量
     *
     * @return 加密向量对象
     */
    public @Nullable AlgorithmParameterSpec getParameterSpec() {
        if (iv == null || iv.length == 0) {
            return null;
        }
        return new IvParameterSpec(iv);
    }

    @Override
    public String toString() {
        if (iv != null) {
            return String.format(
                "algorithm=%s, key=%s, iv=%s",
                algorithm,
                Hex.encodeHexString(key),
                Hex.encodeHexString(iv));
        }

        return String.format(
            "algorithm=%s, key=%s",
            algorithm,
            Hex.encodeHexString(key));
    }
}
