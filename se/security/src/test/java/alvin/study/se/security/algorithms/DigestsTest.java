package alvin.study.se.security.algorithms;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Digests} 类型
 */
class DigestsTest {
    // 要获取摘要的数据
    private static final byte[] DATA = "Hello World, this is message digest demo".getBytes(StandardCharsets.UTF_8);

    /**
     * 测试 {@link Digests#digest(byte[])} 方法, 对一个字节串进行摘要
     */
    @CsvSource({
        "MD2,f97d05677d4586e491b650122479d849",
        "MD5,2f5cce2c7b8e26706ea0049211a26033",
        "SHA1,c811931bca72a90d96e8b7125543a0e5bec75028",
        "SHA224,5911c407bd17f134f7b0426cde5383c80a7739a244c32351e1bf7188",
        "SHA256,92f943be3df1a98a17300379e95b9bbbe0e60901addf9ac9bcce321e4c8273a7",
        "SHA384,dccc3cb909daec315913016a5956c6d85bf3504fbe4e4bd2ea35e0d59ddeb6d081b2acf2de0df8556349f68d9e6bbd54",
        "SHA512,caa2be9425815f5d8cc98a49b271035f0a2b85f8bfebcd6c287633087dce8d274ae29f4e566267c9b4f01e725ddf6ee046ebff8b153a1a18775eb35963a90fa5"
    })
    @ParameterizedTest
    void digest_shouldEncodeStringDigest(String algorithmName, String expectedHash) {
        var digest = new Digests(algorithmName);
        var hash = digest.digest(DATA);

        then(hash).hasSize(digest.byteSize() / 2).isEqualTo(expectedHash);
    }

    /**
     * 测试 {@link Digests#digest(java.io.InputStream)} 方法, 对一个输入流内容进行摘要
     */
    @CsvSource({
        "MD2,87dd9942e6de4fda3858795ba8dc01d7",
        "MD5,daf9789eaf9524f44fc03ccd7489461d",
        "SHA1,8d72eb2a994f5d153ad04ddac2e67062fb9cbb12",
        "SHA224,a483f7f7e3243f3eefe146e828ca2d662c932c0ee4488538c58f4288",
        "SHA256,e8899b769a26c0f44394a599e4f4b9b7c5fcc44af80607826fc023e18aa1c46b",
        "SHA384,a0d03a758c69700255f2e7247a537b6cfb5276f3cc48ccbba2b29e6743c97366f17cb5bdd2fec557901126f3483caa03",
        "SHA512,417501790168a59e443a140fcfad38b617bc4694460c6e30015fb30f0ba61646bb8a36b301c61a9b5f6ef50c379b2960d78919e9be4ddd758916981f01f8d655"
    })
    @ParameterizedTest
    void digest_shouldEncodeInputStreamDigest(String algorithmName, String expectedHash) throws Exception {
        // 获取保存测试数据的文件资源
        var file = new File(Objects.requireNonNull(getClass().getResource("/data/test-data.txt")).getFile());
        then(file.exists()).isTrue();

        // 对文件内容通过各种摘要算法进行计算
        var digest = new Digests(algorithmName);

        // 在文件上打开输入流, 进行摘要
        String hash;
        try (var input = new FileInputStream(file)) {
            hash = digest.digest(input);
        }

        // 确认摘要结果符合预期
        then(hash).hasSize(digest.byteSize() / 2).isEqualTo(expectedHash);
    }

    /**
     * 测试 {@link Digests#digest(java.nio.file.Path) Digests.digest(Path)} 方法, 对一个文件内容进行摘要
     */
    @CsvSource({
        "MD2,87dd9942e6de4fda3858795ba8dc01d7",
        "MD5,daf9789eaf9524f44fc03ccd7489461d",
        "SHA1,8d72eb2a994f5d153ad04ddac2e67062fb9cbb12",
        "SHA224,a483f7f7e3243f3eefe146e828ca2d662c932c0ee4488538c58f4288",
        "SHA256,e8899b769a26c0f44394a599e4f4b9b7c5fcc44af80607826fc023e18aa1c46b",
        "SHA384,a0d03a758c69700255f2e7247a537b6cfb5276f3cc48ccbba2b29e6743c97366f17cb5bdd2fec557901126f3483caa03",
        "SHA512,417501790168a59e443a140fcfad38b617bc4694460c6e30015fb30f0ba61646bb8a36b301c61a9b5f6ef50c379b2960d78919e9be4ddd758916981f01f8d655"
    })
    @ParameterizedTest
    void digest_shouldEncodeFileDigest(String algorithmName, String expectedHash) throws Exception {
        // 获取保存测试数据的文件资源
        var path = Paths.get(Objects.requireNonNull(getClass().getResource("/data/test-data.txt")).getFile());
        then(Files.exists(path)).isTrue();

        // 对文件通过各种摘要算法进行计算
        var digest = new Digests(algorithmName);
        var hash = digest.digest(path);

        // 确认摘要结果符合预期
        then(hash).hasSize(digest.byteSize() / 2).isEqualTo(expectedHash);
    }
}
