package alvin.study.se.security.keystore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * 读取 X509 格式证书文件, 导出公钥为证书文件
 *
 * <p>
 * 从 Keystore 中导出别名为 root 公钥
 *
 * <pre>
 * $ keytool -export                    \
 *           -alias root                \
 *           -keystore keystore.p12     \
 *           -storetype pkcs12          \
 *           -storepass password        \
 *           -rfc                       \
 *           -file pub.cer
 * </pre>
 * </p>
 *
 * @see PKCS12KeyStores
 */
public class X509Certificates extends Certificates {
    /**
     * 构造器, 设置证书文件路径名
     *
     * @param certFile 证书文件路径名
     */
    protected X509Certificates(File certFile) {
        super(createCertificate(certFile));
    }

    /**
     * 读取 X509 证书文件
     *
     * @param certificateFile 证书文件路径
     */
    private static Certificate createCertificate(File certificateFile) {
        try (var in = new BufferedInputStream(new FileInputStream(certificateFile))) {
            var cerFactory = CertificateFactory.getInstance("X509");
            return cerFactory.generateCertificate(in);
        } catch (Exception e) {
            throw new CertificatesException(e);
        }
    }
}
