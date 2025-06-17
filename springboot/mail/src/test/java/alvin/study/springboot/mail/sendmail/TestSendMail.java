package alvin.study.springboot.mail.sendmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.mail.IntegrationTest;

class TestSendMail extends IntegrationTest {
    @Autowired
    private JavaMailSender mailSender;

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
