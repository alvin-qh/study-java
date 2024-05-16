package alvin.study.se.security.keystore;

import alvin.study.se.security.algorithms.Ciphers;
import alvin.study.se.security.util.DataGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link PKCS12KeyStores} 类, 从一个密钥库文件中读取公钥和私钥信息
 */
class PKCS12KeyStoresTest {
    /**
     * 测试 {@link PKCS12KeyStores#PKCS12KeyStores(File, String)} 方法, 根据所给的密钥库文件和密码读取公钥和私钥
     */
    @Test
    void loadKeys_shouldLoadPrivateAndPublicKeyFromKeyStoreFile() {
        // 产生随机测试数据
        var data = DataGenerator.generate(5011);
        then(data).hasSize(5011);

        // 从资源中获取密钥库文件名
        var keyStoreFile = new File(Objects.requireNonNull(getClass().getResource("/keystore/keystore.p12")).getFile());

        // 根据密钥库文件名和库密码实例化密钥库读取对象
        var keyStore = new PKCS12KeyStores(keyStoreFile, "password");

        var cipher = new Ciphers("RSA");

        // 根据密钥库读取的私钥加密数据
        var encData = cipher.encrypt(keyStore.getPrivateKey("root", "password"), data);
        // 根据密钥库读取的公钥解密数据
        var decData = cipher.decrypt(keyStore.getPublicKey("root"), encData);

        // 确认解密后的数据和原数据一致
        then(decData).isEqualTo(data);
    }
}
