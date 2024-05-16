package alvin.study.se.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * 测试 {@link MailClient} 接口
 *
 * <p>
 * 本例中通过 {@link SimpleMailClientBuilder} 创建 {@link MailClient} 接口对象, 完成邮件发送
 * </p>
 *
 * <p>
 * 本例中使用 SMTP 协议进行邮件发送, 所以邮件服务开启 SMTP 协议支持
 * </p>
 */
class MailClientTest {
    /**
     * 测试 {@link MailClient#send(String, String, String, String)} 方法, 发送一封邮件
     */
    @Test
    @Disabled
    void send_shouldSendMailByMailClient() throws Exception {
        // 创建邮件发送客户端对象
        var client = new SimpleMailClientBuilder("smtp.163.com", 994, "quhao317@163.com", "****").build();

        // 发送邮件
        client.send(
            "quhao317@163.com",
            "mousebaby8080@gmail.com",
            "Welcome Alvin",
            "<html><head><title>Test</title></head><body>Hello Alvin, This is a mail for testing</body></html>");
    }
}
