package alvin.study.se.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Properties;

/**
 * 通过 {@code jakarta} ({@code javax}) 邮件 API 发送邮件
 */
public class SimpleMailClientBuilder {
    // 指定 SSL Socket 工厂类
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    // 指定邮件正文内容编码格式
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

    // 是否开启 debug 模式
    private static boolean debug = true;

    // smtp 服务器地址
    private final String smtpHost;
    // smtp 服务器端口
    private final int smtpPort;
    // 邮件服务登录账号
    private final String account;
    // 邮件服务登录密码
    private final String password;

    // 是否启用身份认证
    private boolean auth = true;
    // 是否需要回执
    private boolean fallback = false;
    // 是否开启 TLS 协议
    private boolean startTlsEnable = true;
    // 使用邮件服务的协议
    private String protocol = "smtp";

    /**
     * 构造器, 初始化邮件客户端
     *
     * @param smtpHost SMTP 服务地址
     * @param smtpPort SMTP 服务端口号
     * @param account  邮件服务认证账号名
     * @param password 邮件服务器认证密码
     */
    public SimpleMailClientBuilder(String smtpHost, int smtpPort, String account, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.account = account;
        this.password = password;
    }

    /**
     * 获取 debug 模式的状态
     *
     * @return {@code true} 表示开启 debug 模式
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * 设置是否开启 debug 模式
     *
     * @param debug {@code true} 表示开启 debug 模式
     */
    public static void setDebug(boolean debug) {
        SimpleMailClientBuilder.debug = debug;
    }

    /**
     * 设置是否需要服务器认证
     *
     * @param auth {@code true} 表示开启服务器认证
     * @return 当前对象
     */
    public SimpleMailClientBuilder auth(boolean auth) {
        this.auth = auth;
        return this;
    }

    /**
     * 设置是否需要回执
     *
     * @param fallback {@code true} 表示需要回执
     * @return 当前对象
     */
    public SimpleMailClientBuilder fallback(boolean fallback) {
        this.fallback = fallback;
        return this;
    }

    /**
     * 设置是否开启 TLS 网络协议
     *
     * @param startTlsEnable {@code true} 表示开启 TLS 网络协议
     * @return 当前对象
     */
    public SimpleMailClientBuilder startTlsEnable(boolean startTlsEnable) {
        this.startTlsEnable = startTlsEnable;
        return this;
    }

    /**
     * 设置邮件发送服务协议
     *
     * @param protocol 邮件发送服务协议, 默认为 SMTP
     * @return 当前对象
     */
    public SimpleMailClientBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * 创建邮件发送客户端对象
     *
     * @return {@link MailClient} 邮件发送客户端对象
     * @throws MessagingException 邮件发送异常
     */
    public MailClient build() throws MessagingException {
        var props = makeProperties();

        // 返回 MailClient 对象
        return (from, to, subject, content) -> {
            // 创建发送邮件上下文
            var session = Session.getDefaultInstance(props, new Authenticator() {
                /**
                 * 设置邮件服务器验证对象
                 *
                 * @return {@link PasswordAuthentication} 对象, 表示一个通过账号密码进行认证的认证对象
                 */
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account, password);
                }
            });

            // 创建要发送的邮件消息对象
            var message = new MimeMessage(session);

            // 创建消息板块, 可以有多个, 例如正文, 附件
            var mimeBodyPart = new MimeBodyPart();
            // 向消息板块中填入内容
            mimeBodyPart.setContent(content, HTML_CONTENT_TYPE);

            // 创建一个多板块消息体
            var multipart = new MimeMultipart();
            // 将创建的内容板块加入
            multipart.addBodyPart(mimeBodyPart);

            // 设置发送方
            message.setFrom(new InternetAddress(from));
            // 设置接收方
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 设置发送时间
            message.setSentDate(new Date());
            // 设置邮件主题
            message.setSubject(subject);
            // 设置邮件内容
            message.setContent(multipart);

            // 发送邮件
            Transport.send(message);
        };
    }

    /**
     * 创建邮件发送属性对象
     *
     * <p>
     * 本方法返回一个 {@link Properties} 对象, 包含了要创建的邮件客户端相关的配置属性
     * </p>
     *
     * @return 邮件配置属性
     */
    private @NotNull Properties makeProperties() {
        var props = new Properties();
        props.put("mail.smtp.starttls.enable", startTlsEnable);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", auth);
        props.put("mail.debug", debug);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.socketFactory.port", smtpPort);
        props.put("mail.smtp.ssl.checkserveridentity", true);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", fallback);
        props.put("mail.transport.protocol", protocol);
        return props;
    }
}
