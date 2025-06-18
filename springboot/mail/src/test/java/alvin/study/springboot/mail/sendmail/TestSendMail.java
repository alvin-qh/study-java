package alvin.study.springboot.mail.sendmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.mail.IntegrationTest;

/**
 * 测试 Springboot 发送邮件功能
 */
class TestSendMail extends IntegrationTest {
    // 注入邮件发送对象
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 测试发送邮件功能
     */
    @Test
    @SneakyThrows
    void send_shouldSendMail() {
        var message = mailSender.createMimeMessage();

        var helper = new MimeMessageHelper(message, false);
        helper.setFrom("quhao317@163.com");
        helper.setTo("mousebaby8080@gmail.com");
        helper.setSubject("测试邮件");
        helper.setText(
            "<html><head><title>Test</title></head><body>Hello Alvin, This is a mail for testing</body></html>", true);

        mailSender.send(message);
    }
}
