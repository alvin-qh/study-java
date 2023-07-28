package alvin.study.springboot.springdoc.app.endpoint.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 返回访问日志的 DTO 类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class AccessLogDto {
    // 用户名
    private String username;

    // 上次访问时间
    private Instant lastAccessTime;

    // 是否是登录访问
    private boolean actionLogin;
}
