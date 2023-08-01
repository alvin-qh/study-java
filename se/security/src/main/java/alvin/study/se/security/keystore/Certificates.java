package alvin.study.se.security.keystore;

import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * 证书类
 *
 * <p>
 * 证书中存储着从密钥库中导出的公钥信息
 * </p>
 */
public abstract class Certificates {
    // JDK 证书对象
    private final Certificate cert;

    /**
     * 构造器, 设置证书对象
     *
     * @param cert JDK 证书对象
     */
    protected Certificates(Certificate cert) {
        this.cert = cert;
    }

    /**
     * 获取证书中的公钥信息
     */
    public PublicKey getPublicKey() {
        return cert.getPublicKey();
    }
}
