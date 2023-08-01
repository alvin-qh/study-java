package alvin.study.se.security.keystore;

import alvin.study.se.security.algorithms.Ciphers;
import alvin.study.se.security.util.DataGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link X509Certificates} 类型, 从证书中获取公钥
 */
class X509CertificatesTest {
    /**
     * 测试 {@link X509Certificates#X509Certificates(File)} 方法, 创建证书对象, 并读取公钥对象
     */
    @Test
    void certificates_shouldLoadPublicKeyFromCertificateFile() {
        // 生成随机测试数据
        var data = DataGenerator.generate(5011);
        then(data).hasSize(5011);

        // 读取密码库文件
        var keyStoreFile = new File(Objects.requireNonNull(getClass().getResource("/keystore/keystore.p12")).getFile());
        var keyStore = new PKCS12KeyStores(keyStoreFile, "password");

        // 从资源中获取证书文件名
        var cerFile = new File(Objects.requireNonNull(getClass().getResource("/keystore/pub.cer")).getFile());
        // 读取证书文件
        var cer = new X509Certificates(cerFile);

        var cipher = new Ciphers("RSA");

        // 从密钥库中读取私钥, 对数据进行加密
        var encData = cipher.encrypt(keyStore.getPrivateKey("root", "password"), data);
        // 从证书中读取公钥, 对数据进行解密
        var decData = cipher.decrypt(cer.getPublicKey(), encData);

        // 确认解密后的数据和原数据一致
        then(decData).isEqualTo(data);
    }
}
