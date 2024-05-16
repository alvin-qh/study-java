package alvin.study.springboot.mvc.core.i18n;

import java.util.Locale;

/**
 * I18n 接口类
 *
 * <p>
 * 通过本地化语言对象和 Message 的 {@code Key} 值获取对应语言的 Message 字符串
 * </p>
 *
 * <p>
 * 通过
 * {@code BeanConfig.i18n(MessageSource} 方法实例化当前接口的对象, {@code messageSource} 参数由
 * {@code WebConfig.messageSource()} 方法产生
 * </p>
 *
 * @see MessageI18n
 */
public interface I18n {
    /**
     * 根据本地化语言对象和 {@code Key} 值获取一个 Message 字符串
     *
     * @param locale         本地化语言对象
     * @param key            Message 的 {@code Key} 值
     * @param defaultMessage {@code Key} 不存在时的默认 Message
     * @param args           对 Message 中的占位符进行格式化的参数
     * @return Message 字符串
     */
    String getMessage(Locale locale, String key, String defaultMessage, Object... args);

    /**
     * 根据 {@code Key} 值获取一个 Message 字符串
     *
     * @param key  Message 的 {@code Key} 值
     * @param args 对 Message 中的占位符进行格式化的参数
     * @return Message 字符串
     */
    String getMessage(String key, Object... args);

    /**
     * 如果给定 {@code Key} 存在，则返回对应的 Message，否则返回默认 Message
     *
     * @param key            Message 的 {@code Key} 值
     * @param defaultMessage {@code Key} 不存在时返回的默认 Message
     * @return Message 字符串
     */
    String getMessageOrElse(String key, String defaultMessage, Object... args);

    /**
     * 返回本地化语言对象
     *
     * @return 本地化语言对象
     */
    Locale getLocale();
}
