package alvin.study.springboot.mvc.core.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.google.common.base.Strings;

import alvin.study.springboot.mvc.util.http.Servlets;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring {@code MessageSource} 机制的 i18n 实现类
 *
 * <p>
 * 该类型对象注入一个 {@link MessageSource} 类型对象, 该对象会根据 {@link Locale} 对象定义的语言标识从
 * {@code classpath:/i18n} 目录下挑选合适的语言文件, 并通过 {@code Key} 获取对应的 {@code Message}
 * </p>
 *
 * @see I18n
 * @see MessageSource
 */
@RequiredArgsConstructor
public class MessageI18n implements I18n {
    // 注入MessageSource 对象
    private final MessageSource messageSource;
    private final Locale locale;

    /**
     * 获取 {@link java.util.Locale Locale} 对象
     *
     * <p>
     * 获取的策略包括:
     * <ul>
     * <li>
     * 从请求参数中获取 {@code lang} 参数, 参数值表示一个 {@code Language Tag}, 通过该值产生一个
     * 获取 {@link Locale} 对象
     * </li>
     * <li>
     * 从请求参数中获取 {@link Locale} 对象, 即通过请求头的 {@code Accept-Langrage} 属性获取
     * {@code Language Tag}
     * </li>
     * <li>
     * 如果前两点都未达成, 则默认为英语
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 也可以通过 Spring 框架提供的
     * {@link org.springframework.context.i18n.LocaleContextHolder
     * LocaleContextHolder} 类型配合拦截器完成, 即在拦截器中产生 {@link Locale} 对象并存储到
     * {@link org.springframework.context.i18n.LocaleContextHolder
     * LocaleContextHolder} 对象中, 在一次请求的后续操作使用. 这种方法的一个缺陷在于, 对于 API 请求, 并不是每个请求都需要进行
     * i18n 处理, 所以用拦截器会降低代码的效率
     * </p>
     *
     * @see Servlets#getHttpServletRequest()
     * @see jakarta.servlet.http.HttpServletRequest#getLocale()
     */
    public static Locale createRequestLocale() {
        var req = Servlets.getHttpServletRequest();
        var lang = req.getParameter("lang");
        if (!Strings.isNullOrEmpty(lang)) {
            return Locale.forLanguageTag(lang);
        }

        // 可以从 HttpServletRequest 对象中获取请求 Locale 对象, 是通过 header 中的 Accept-Language 属性指定的
        return req.getLocale();

        // 也可以通过 LocaleContextHolder 获取 Locale 对象, 原理和上面类似
        // return LocaleContextHolder.getLocale();
    }

    /**
     * 根据指定的本地化对象获取文本
     */
    @Override
    public String getMessage(Locale locale, String key, String defaultMessage, Object... args) {
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }

    /**
     * 根据请求中指定的本地化对象获取文本
     */
    @Override
    public String getMessage(String key, Object... args) {
        return getMessage(locale, key, key, args);
    }

    /**
     * 获取文本, 当 key 不存在时返回默认值
     */
    @Override
    public String getMessageOrElse(String key, String defaultMessage, Object... args) {
        return getMessage(locale, key, defaultMessage, args);
    }

    /**
     * 返回本地化语言对象
     *
     * @return 本地化语言对象
     */
    @Override
    public Locale getLocale() { return locale; }
}
