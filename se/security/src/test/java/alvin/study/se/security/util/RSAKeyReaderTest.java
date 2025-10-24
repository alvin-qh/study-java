package alvin.study.se.security.util;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Objects;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link RSAKeyReader} 类型
 */
class RSAKeyReaderTest {
    // 获取保存密钥文件的资源名
    private final String keyFile = Objects.requireNonNull(RSAKeyReaderTest.class.getResource("/keyfile/key")).getFile();

    /**
     * 测试 {@link RSAKeyReader#readPrivateKey()} 方法, 从 {@code /keyfile/key} 文件中读取私钥数据
     */
    @Test
    @SneakyThrows
    void readPrivateKey_shouldReadPrivateKeyFromFile() {
        // 依据 basename 创建对象
        var keyReader = new RSAKeyReader(keyFile);

        // 读取私钥对象
        var privateKey = keyReader.readPrivateKey();
        then(privateKey).isNotNull();
    }

    /**
     * 测试 {@link RSAKeyReader#readPublicKey()} 方法, 从 {@code /keyfile/key.pub} 文件中读取公钥数据
     */
    @Test
    @SneakyThrows
    void readPublicKey_shouldReadPublicKeyFromFile() {
        // 依据 basename 创建对象
        var keyReader = new RSAKeyReader(keyFile);

        // 读取公钥对象
        var publicKey = keyReader.readPublicKey();
        then(publicKey).isNotNull();
    }
}
