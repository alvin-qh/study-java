package alvin.study.quarkus.web.i18n;

import java.util.Locale;

import org.jboss.resteasy.reactive.common.util.LocaleHelper;

import alvin.study.quarkus.util.StringUtil;
import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Localized.Literal;
import io.quarkus.qute.i18n.MessageBundles;
import io.smallrye.common.vertx.ContextLocals;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.http.HttpHeaders;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;

/**
 * {@link AppMessage} 类型对象工厂类
 *
 * <p>
 * 用于根据请求中 {@code Accept-Language} 头信息获取 {@link AppMessage} 接口对象
 * </p>
 */
@RequestScoped
public class AppMessager {
    private static final String CTX_KEY_MESSAGE = "_ctx_app_message";

    // 注入请求上下文对象
    @Context
    HttpServerRequest request;

    /**
     * 获取指定语系的 {@link AppMessage} 接口对象
     *
     * @param language 语系代码, 例如 {@code zh}, {@code en} 等
     * @return 指定 (或默认) 语系的 {@link AppMessage} 接口类
     */
    public AppMessage appMessage(String language) {
        return MessageBundles.get(AppMessage.class, Localized.Literal.of(language));
    }

    /**
     * 根据请求中指定的 {@code Accept-Language} 头参数获取对应语系的 {@link AppMessage} 接口对象 (或默认语系)
     *
     * @return 指定 (或默认) 语系的 {@link AppMessage} 接口类
     */
    public AppMessage appMessage() {
        // 从上下文对象中获取 Literal 对象
        var literal = ContextLocals.<Literal>get(CTX_KEY_MESSAGE, null);
        if (literal == null) {
            // 从请求中解析 Literal 对象
            literal = Localized.Literal.of(LocaleHelper.toLanguageString(parseLocale()));
            // 将 Literal 对象缓存在上下文中
            ContextLocals.put(CTX_KEY_MESSAGE, literal);
        }
        try {
            // 根据获取的 Literal 对象实例化不同的 AppMessage 对象
            return MessageBundles.get(AppMessage.class, literal);
        } catch (java.lang.IllegalStateException e) {
            // 对于解析失败, 获取默认 AppMessage 对象
            return MessageBundles.get(AppMessage.class);
        }
    }

    /**
     * 请求中指定的 {@code Accept-Language} 头参数获取对应语系的 {@link Locale} 对象
     *
     * @return 指定 (或默认) 语系的 {@link Locale} 对象
     */
    private Locale parseLocale() {
        // 从请求头中获取 Accept-Language 参数
        var acceptLang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        if (StringUtil.isNullOrEmpty(acceptLang)) {
            // 返回系统默认 Locale 对象
            return Locale.getDefault();
        }

        // 从 Accept-Language 参数中解析 Locale 对象
        var locale = LocaleHelper.extractLocale(acceptLang);
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }
}
