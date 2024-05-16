package alvin.study.se.security.keystore;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * 使用 PKCS12 格式的密钥库
 *
 * <p>
 * <ol>
 * <li>
 * 生成 PKCS12 格式的密钥库文件, 保存名为 {@code root} 的密钥对; 每个密钥库可以保存多组密钥对, 用 {@code -alias} 参数明明区分
 *
 * <pre>
 * $ keytool -genkey -v               \
 *           -alias root              \
 *           -keyalg RSA              \
 *           -storetype pkcs12        \
 *           -keystore keystore.p12   \
 *           -dname "CN=alvin.study,OU=alvin,L=Xi'an,ST=Shaanxi,C=China"    \
 *           -storepass password      \
 *           -keypass password        \
 *           -validity 1000000
 * </pre>
 *
 * </li>
 * <li>
 * 查看生成的密钥库信息
 *
 * <pre>
 * $ keytool -list                    \
 *           -keystore keystore.p12   \
 *           -storepass password      \
 *           -storetype pkcs12
 * </pre>
 *
 * </li>
 * </ol>
 * </p>
 *
 * @see java.security.cert.X509Certificate X509Certificate
 */
public class PKCS12KeyStores extends KeyStores {
    /**
     * 构造器, 通过密码库文件和密码实例化对象
     *
     * <p>
     * 如果在创建密钥库文件时设定了密码, 则打开该文件时, 需要提供当时设置的密码
     * </p>
     *
     * @param storeFile     PKCS12 格式密钥库文件对象
     * @param storePassword 密钥库的密码
     */
    public PKCS12KeyStores(File storeFile, String storePassword) {
        super(createKeyStore(storeFile, storePassword));
    }

    /**
     * 读取密钥库文件
     *
     * @param storeFile     PKCS12 格式密钥库文件对象
     * @param storePassword 密钥库文件密码
     * @return {@link KeyStore} 对象
     */
    private static @NotNull KeyStore createKeyStore(File storeFile, String storePassword) {
        try {
            var store = KeyStore.getInstance("PKCS12");
            try (var input = new BufferedInputStream(new FileInputStream(storeFile))) {
                store.load(input, storePassword.toCharArray());
            }
            return store;
        } catch (Exception e) {
            throw new KeyStoresException(e);
        }
    }
}
