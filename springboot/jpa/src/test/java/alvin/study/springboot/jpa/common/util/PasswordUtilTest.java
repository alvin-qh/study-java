package alvin.study.springboot.jpa.common.util;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.util.security.PasswordUtil;

/**
 * 测试密码工具类
 */
class PasswordUtilTest extends IntegrationTest {
    @Autowired
    private PasswordUtil passwordUtil;

    /**
     * 测试密码生成器生成密码是否正确
     */
    @Test
    @SneakyThrows
    void encrypt_shouldPasswordEncrypt() {
        var expected = "c926d53ca183e8bb5a369e8752b4ed574304bf1f15d680b8304f3251306915ec";

        var pass = passwordUtil.encrypt("admin@123");
        then(expected).isEqualTo(pass);

        var success = passwordUtil.verify("admin@123", expected);
        then(success).isTrue();
    }
}
