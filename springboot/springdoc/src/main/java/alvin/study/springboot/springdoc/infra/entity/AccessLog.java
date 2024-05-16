package alvin.study.springboot.springdoc.infra.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/**
 * 表示访问记录的实体对象
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessLog {
    // 访问用户名
    private final String username;

    // 访问时间
    private final Instant lastAccessAt;

    // 是否登录操作
    private final boolean actionLogin;

    /**
     * 创建一个表示登录的访问日志实体对象
     *
     * @param username 访问用户名
     * @return 实体对象
     */
    public static AccessLog forLogin(String username) {
        return new AccessLog(username, Instant.now(), true);
    }

    /**
     * 创建一个表示非登录访问的访问日志实体对象
     *
     * @param username 访问用户名
     * @return 实体对象
     */
    public static AccessLog forAccess(String username) {
        return new AccessLog(username, Instant.now(), false);
    }
}
