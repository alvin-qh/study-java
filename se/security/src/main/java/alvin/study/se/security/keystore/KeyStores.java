package alvin.study.se.security.keystore;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 获取密钥存储 (KeyStore) 中存储的公钥和私钥
 */
public abstract class KeyStores {
    // JDK 密钥库对象
    private final KeyStore store;

    /**
     * 构造器, 设置密钥库对象
     *
     * @param store JDK 密钥库对象
     */
    protected KeyStores(KeyStore store) {
        this.store = store;
    }

    /**
     * 读取密钥库并获取私钥对象
     *
     * <p>
     * 一般来说, 密钥库的私钥是被加密的, 无法导出, 使用时需要提供加密私钥的密码
     * </p>
     *
     * @param alias    私钥在 KeyStore 中的别名
     * @param password 私钥的存储密码
     * @return 私钥对象
     */
    public PrivateKey getPrivateKey(String alias, String password) {
        try {
            return (PrivateKey) store.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            throw new KeyStoresException(e);
        }
    }

    /**
     * 读取密钥库并获取公钥对象
     *
     * <p>
     * 一般来说, 密钥库的公钥是明文存储的, 可以导出为证书
     * </p>
     *
     * @param alias 公钥在 KeyStore 中的别名
     * @return 公钥对象
     */
    public PublicKey getPublicKey(String alias) {
        try {
            var cert = store.getCertificate(alias);
            return cert.getPublicKey();
        } catch (Exception e) {
            throw new KeyStoresException(e);
        }
    }
}
