package alvin.study.springboot.shiro.app.endpoint.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * 返回 Token 信息的类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.MODULE)
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
