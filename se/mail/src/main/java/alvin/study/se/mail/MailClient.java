package alvin.study.se.mail;

import jakarta.mail.MessagingException;

/**
 * 邮件发送客户端接口
 *
 * <p>
 * 本接口的一个实现类可通过 {@link SimpleMailClientBuilder} 创建
 * </p>
 */
public interface MailClient {
    /**
     * 发送邮件
     *
     * @param from    发送方邮件地址
     * @param to      接收方邮件地址
     * @param subject 邮件标题
     * @param content 邮件正文
     * @throws MessagingException 发送过程中发生的异常
     */
    void send(String from, String to, String subject, String content) throws MessagingException;
}
