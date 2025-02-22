package alvin.study.springboot.shiro.core.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.shiro.util.security.PasswordEncoder;

/**
 * 自定义凭证验证类
 *
 * <p>
 * 当通过 {@link org.apache.shiro.subject.Subject#login(AuthenticationToken)
 * Subject.login(AuthenticationToken)} 方法进行登录时, 会调用
 * {@link org.apache.shiro.realm.Realm#getAuthenticationInfo(AuthenticationToken)
 * Realm.getAuthenticationInfo(AuthenticationToken)} 方法对传入的
 * {@link AuthenticationToken} 对象中存储的用户凭证进行处理, 返回表示实际用户信息的
 * {@link AuthenticationInfo} 对象
 * </p>
 *
 * <p>
 * {@link #doCredentialsMatch(AuthenticationToken, AuthenticationInfo)}
 * 方法即是对用户发送的凭证和用户实际信息进行比对, 以确认用户凭证的有效性
 * </p>
 *
 * <p>
 * 本例中处理 {@link UsernamePasswordToken} 对象表示的用户凭证, 即用户名和密码
 * </p>
 */
@RequiredArgsConstructor
public class CustomerCredentialsMatcher implements CredentialsMatcher {
    // 注入密码处理对象
    private final PasswordEncoder passwordEncoder;

    /**
     * 进行用户凭证匹配
     *
     * @param authToken 用户登录凭证对象
     * @param info      用户实际信息对象
     * @return {@code true} 表示用户凭证有效
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authToken, AuthenticationInfo info) {
        // 判断用户凭证的类型
        if (authToken instanceof UsernamePasswordToken upToken) {
            // 对于用户名密码凭证, 则比对凭证中的密码和用户原本设置的密码是否一致
            var rawPassword = new String(upToken.getPassword());
            return passwordEncoder.matches(rawPassword, (String) info.getCredentials());
        }

        // 对于其它用户凭证, 查看是否具备凭证信息即可
        return authToken.getPrincipal() != null;
    }
}
