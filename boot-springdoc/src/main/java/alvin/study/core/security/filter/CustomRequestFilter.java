package alvin.study.core.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Strings;

import alvin.study.core.security.auth.CustomAuthenticationToken;
import alvin.study.util.http.Headers;
import lombok.SneakyThrows;

/**
 * 对客户端请求进行拦截, 处理 Basic Auth 和 Jwt 两类 Header 信息
 *
 * <p>
 * 如果客户端通过 {@code swaggerUrlMatchers} 匹配的 URL 进行访问, 且 Header 中携带
 * {@code Authorization: Basic xxxxxx}, 则按照 Basic Auth 逻辑获取用户登录凭证
 * </p>
 *
 * <p>
 * 如果客户端通过 {@code apiUrlMatchers} 匹配的 URL 进行访问, 且 Header 中携带
 * {@code Authorization: Bearer xxxxxx}, 则按照 JWT 逻辑获取用户登录凭证
 * </p>
 *
 * <p>
 * {@code excludeRequestMatchers} 表明了那些请求不进行过滤, 即这部分请求放行, 不纳入认证处理逻辑
 * </p>
 *
 * <p>
 * 本拦截器会产生 {@link CustomAuthenticationToken} 用户凭证对象并存储在上下文中, 交友后续的
 * {@link alvin.study.core.security.auth.CustomAuthenticationProvider
 * CustomAuthenticationProvider} 对象进行认证处理
 * </p>
 */
@SuppressWarnings("java:S1192")
public class CustomRequestFilter extends OncePerRequestFilter {
    // 拦截 swagger 访问的 URL 匹配器
    private final AntPathRequestMatcher[] swaggerUrlMatchers;

    // 拦截 api 访问的 URL 匹配器
    private final AntPathRequestMatcher[] apiUrlMatchers;

    // 需要排除拦截的 URL 匹配器
    private final AntPathRequestMatcher[] excludeRequestMatchers;

    /**
     * 构造过滤器对象, 传递需要拦截或忽略的 URL 匹配模式
     *
     * @param swaggerUrlPatterns     需要进行 Basic Auth 处理的 URL 匹配模式
     * @param apiUrlPatterns         需要进行 JWT 处理的 URL 匹配模式
     * @param excludeRequestMatchers 无需进行身份认证的 URL 匹配模式
     */
    public CustomRequestFilter(String[] swaggerUrlPatterns, String[] apiUrlPatterns, String[] excludeRequestMatchers) {
        this.swaggerUrlMatchers = buildMatchers(swaggerUrlPatterns);
        this.apiUrlMatchers = buildMatchers(apiUrlPatterns);
        this.excludeRequestMatchers = buildMatchers(excludeRequestMatchers);
    }

    /**
     * 将 URL 匹配字符串数组转为 {@link AntPathRequestMatcher} 数组
     *
     * @param urlPatterns URL 匹配字符串数组
     * @return {@link AntPathRequestMatcher} 数组
     */
    private static AntPathRequestMatcher[] buildMatchers(String[] urlPatterns) {
        var matchers = new AntPathRequestMatcher[urlPatterns.length];
        for (var i = 0; i < urlPatterns.length; i++) {
            matchers[i] = new AntPathRequestMatcher(urlPatterns[i]);
        }
        return matchers;
    }

    /**
     * 进行拦截操作
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链, 用于调用下一个过滤器
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        // 判断安全上下文中是否已经存储用户信息
        var context = SecurityContextHolder.getContext();

        CustomAuthenticationToken auth = null;
        // 是否调用后续的过滤器
        boolean chainNext = true;
        // 如果尚未进行认证, 则进行认证操作
        if (context.getAuthentication() == null && !checkIfMatcherMatches(excludeRequestMatchers, request)) {
            if (checkIfMatcherMatches(swaggerUrlMatchers, request)) {
                // 若为 swagger 文档访问, 则进入 basic auth 认证逻辑
                auth = checkAsBasicAuth(request, response);
                // 如果 basic auth 认证成功, 则继续之后的流程, 否则停止, 让客户端输入用户名密码
                chainNext = auth != null;
            } else if (checkIfMatcherMatches(apiUrlMatchers, request)) {
                // 若为 API 访问, 则进入 jwt 认证逻辑
                auth = checkAsJwtAuth(request);
            }

            if (auth != null) {
                // 设置认证详细信息
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 在上下文中设置登录凭证对象
                context.setAuthentication(auth);
            }
        }

        if (chainNext) {
            // 通过过滤器链调用下一个过滤器
            chain.doFilter(request, response);
        }
    }

    /**
     * 检测一个 {@link HttpServletRequest} 对象是否匹配指定的 URL 匹配模式
     *
     * @param matchers URL 匹配模式对象数组
     * @param request  请求对象
     * @return 是否匹配成功
     */
    private static boolean checkIfMatcherMatches(AntPathRequestMatcher[] matchers, HttpServletRequest request) {
        for (var matcher : matchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对请求进行 Basic Auth 登录认证处理, 生成用户凭证对象
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 用户凭证对象
     */
    @SneakyThrows
    private CustomAuthenticationToken checkAsBasicAuth(HttpServletRequest request, HttpServletResponse response) {
        var token = request.getHeader(Headers.AUTHORIZATION);
        if (Strings.isNullOrEmpty(token)) {
            // 如果 Header 中不包含 Authorization 头信息, 则返回包含 WWW-Authenticate
            response.setHeader("WWW-Authenticate", "Basic realm=\"Spring Document\"");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return null;
        }

        try {
            // 将 Basic Auth 认证字符串进行 Base64 解码
            var b64Val = Base64.getDecoder().decode(token.substring(Headers.BASIC.length()).trim());
            token = new String(b64Val, 0, b64Val.length, StandardCharsets.UTF_8);

            // 从解码结果中获取用户名和密码
            var nameAndPass = token.split(":", 2);
            if (nameAndPass.length != 2) {
                throw new BadCredentialsException("bad_auth_token");
            }

            // 生成用户凭证返回
            return new CustomAuthenticationToken(nameAndPass[0], nameAndPass[1], true);
        } catch (Exception e) {
            throw new BadCredentialsException("bad_auth_token");
        }
    }

    /**
     * 对请求进行 JWT 登录认证处理, 生成用户凭证对象
     *
     * @param request 请求对象
     * @return 用户凭证对象
     */
    @SneakyThrows
    private CustomAuthenticationToken checkAsJwtAuth(HttpServletRequest request) {
        var token = request.getHeader(Headers.AUTHORIZATION);
        if (Strings.isNullOrEmpty(token)) {
            // 如果 Header 中不包含 JWT 串, 则不生成用户凭证
            return null;
        }

        try {
            // 生成包含 JWT 串的凭证对象并返回
            return new CustomAuthenticationToken(token.substring(Headers.BEARER.length()).trim());
        } catch (Exception e) {
            throw new BadCredentialsException("bad_auth_token");
        }
    }
}
