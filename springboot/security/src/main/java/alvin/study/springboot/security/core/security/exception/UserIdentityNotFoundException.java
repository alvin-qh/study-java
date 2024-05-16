package alvin.study.springboot.security.core.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * {@link UserIdentityNotFoundException} 继承自 {@link AuthenticationException}
 * 类型，当找不到用户时抛出
 */
public class UserIdentityNotFoundException extends AuthenticationException {
    /**
     * 构造器
     *
     * @param id 用户对象 {@code id} 属性
     */
    public UserIdentityNotFoundException(long id) {
        this(id, null);
    }

    /**
     * 构造器
     *
     * @param id    用户对象 {@code id} 属性
     * @param cause 引发此异常的异常对象
     */
    public UserIdentityNotFoundException(long id, Throwable cause) {
        super(String.format("User with identity %d not exist", id), cause);
    }
}
