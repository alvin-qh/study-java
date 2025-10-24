package alvin.study.se.security.algorithms;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import lombok.SneakyThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 测试 {@link HmacDigests} 类
 */
class HmacDigestsTest {
    // 要获取摘要的数据
    private static final byte[] DATA = "Hello World, this is message digest demo".getBytes(StandardCharsets.UTF_8);

    // 密钥数据
    private static final byte[] SECRET = "TS#1}'v(xo{5QhU]*~>3CHEBk)|MLqRe".getBytes(StandardCharsets.UTF_8);

    /**
     * 测试 {@link HmacDigests#digest(byte[], byte[])} 方法
     */
    @ParameterizedTest
    @SneakyThrows
    @CsvSource({
        "MD5,16e34733d8068c4dac8d0784d5160721",
        "SHA1,e88ac0b27d44f773b8876cb95c36377a2117acee",
        "SHA224,90b6a525379e5b8b1d10a909caed4dacba371324d43c285bb446c423",
        "SHA256,4d858734f65bb5ba9b540f547dc52adce5aff488f0f2ed5db71b393da4343575",
        "SHA384,c96ab10edbb92a887b5cb21afbc8f1bbffe6866a950ec9bc88248ba54b924c9af8968cd17e3903bdeac1a79fb91e709a",
        "SHA512,a4d395ce9ebf6f6da6c8de5f73119001360fc921fe6078b3ac63dba387f461c4232c6d34a7227850869b70268cf2d1570164de4ea8b8fb3c4d93ffcef83dd069"
    })
    void digest_shouldEncodeBytesDigest(String algorithmName, String expectedHash) {
        // 实例化对象
        var digests = new HmacDigests(algorithmName);

        // 计算散列值
        var hash = digests.digest(SECRET, DATA);

        // 确认散列长度符合预期, 且结果符合预期
        then(hash).hasSize(digests.byteSize() / 2).isEqualTo(expectedHash);
    }

    /**
     * 测试 {@link HmacDigests#digest(byte[], byte[])} 方法
     */
    @ParameterizedTest
    @SneakyThrows
    @CsvSource({
        "MD5,90cd773299baab07e0936090f64dd474",
        "SHA1,2c89454c82fd9ffdd015ffba0be027dcd99125af",
        "SHA224,817a4eb33419ea10092b679a3cd8945fb029985161bf4d203e5b33d8",
        "SHA256,4cddd41d40562fa82f40497257836b9f09af1bb5fb6f72d0a741fdcb3c39ca81",
        "SHA384,dd95c96553159f52d68b48fc27cdf329edd7fe0422046685b7640b22b1e01d1246f67f18e124f805f9461b8d56388ada",
        "SHA512,b133d2af490c76173a763a6aea24f5a4922ccc4fc60c1607a269e384fc67be1a3f3f51805830f13f53e6fa0f12168cf0ac7229b6795fcdbe368edb6ddfff266e"
    })
    void digest_shouldEncodeInputStreamDigest(String algorithmName, String expectedHash) {
        // 获取保存测试数据的文件资源
        var file = new File(Objects.requireNonNull(getClass().getResource("/data/test-data.txt")).getFile());
        then(file.exists()).isTrue();

        // 实例化对象
        var digests = new HmacDigests(algorithmName);

        // 在文件上打开输入流, 进行摘要
        String hash;
        try (var input = new FileInputStream(file)) {
            hash = digests.digest(SECRET, input);
        }

        // 确认散列结果符合预期
        then(hash).hasSize(digests.byteSize() / 2).isEqualTo(expectedHash);
    }

    /**
     * 测试 {@link HmacDigests#digest(byte[], java.nio.file.Path) HmacDigests.digest(byte[], Path)} 方法
     */
    @ParameterizedTest
    @SneakyThrows
    @CsvSource({
        "MD5,90cd773299baab07e0936090f64dd474",
        "SHA1,2c89454c82fd9ffdd015ffba0be027dcd99125af",
        "SHA224,817a4eb33419ea10092b679a3cd8945fb029985161bf4d203e5b33d8",
        "SHA256,4cddd41d40562fa82f40497257836b9f09af1bb5fb6f72d0a741fdcb3c39ca81",
        "SHA384,dd95c96553159f52d68b48fc27cdf329edd7fe0422046685b7640b22b1e01d1246f67f18e124f805f9461b8d56388ada",
        "SHA512,b133d2af490c76173a763a6aea24f5a4922ccc4fc60c1607a269e384fc67be1a3f3f51805830f13f53e6fa0f12168cf0ac7229b6795fcdbe368edb6ddfff266e"
    })
    void digest_shouldEncodeFileDigest(String algorithmName, String expectedHash) {
        // 获取保存测试数据的文件资源
        var file = Paths.get(Objects.requireNonNull(getClass().getResource("/data/test-data.txt")).getFile());
        then(Files.exists(file)).isTrue();

        // 实例化对象
        var digests = new HmacDigests(algorithmName);

        // 对文件进行摘要
        var hash = digests.digest(SECRET, file);

        // 确认散列结果符合预期
        then(hash).hasSize(digests.byteSize() / 2).isEqualTo(expectedHash);
    }
}
