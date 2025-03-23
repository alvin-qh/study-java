package alvin.study.springcloud.gateway.client.endpoint.model;

import java.io.Serializable;
import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证信息 DTO 类型
 *
 * <p>
 * 用于 {@link alvin.study.springcloud.gateway.client.endpoint.JWTController
 * JWTController} 返回数据
 * </p>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class AuthDto implements Serializable {
    private String issuer;
    private String audience;
    private String subject;
    private String subjectOrgCode;
    private String subjectUserType;
    private Instant issuedAt;
    private Instant expiresAt;
}
