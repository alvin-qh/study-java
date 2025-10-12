package alvin.study.springboot.shiro.app.endpoint.model;

import java.io.Serializable;
import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回 Token 信息的类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class TokenDto implements Serializable {
    /**
     * Token 字符串
     */
    private String token;

    /**
     * Token 过期时间
     */
    private Instant expiredAt;
}
