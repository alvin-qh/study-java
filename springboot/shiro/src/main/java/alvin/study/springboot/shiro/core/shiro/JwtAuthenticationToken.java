package alvin.study.springboot.shiro.core.shiro;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 保存 JWT 的用户凭证
 *
 * <p>
 * 该类型只保存了已登录用户访问时携带的 JWT 作为凭证
 * </p>
 */
@RequiredArgsConstructor
public class JwtAuthenticationToken implements AuthenticationToken {
    // jwt 令牌字符串
    private final String token;

    /**
     * 获取凭证主体, 即登录用户. 此处使用 jwt 字符串作为结果
     */
    @Override
    public Object getPrincipal() { return token; }

    /**
     * 获取登录凭证, 对于 jwt 来说, 其本身已经表达了所有的登录信息, 所以此处返回 {@code null} 表示无需凭证即可
     */
    @Override
    public Object getCredentials() { return null; }
}
