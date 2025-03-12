package alvin.study.testing.pioneer;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Locale;
import java.util.TimeZone;

import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * 测试通过 Pioneer 库对测试方法指定默认的国际化和时区标识
 */
class LocaleAndTimeZoneTest {
    /**
     * 检查当前的默认时区
     *
     * @return 当前默认时区的偏移量是否为 {@code 0} (即是否为 UTC 时区)
     */
    static boolean checkTimeZone() {
        return TimeZone.getDefault().getRawOffset() != 0;
    }

    /**
     * 测试获取系统默认的国际化标识
     *
     * <p>
     * 和测试所需的国际化标识不一致
     * </p>
     */
    @Test
    void locale_shouldGetOriginalLocale() {
        then(Locale.getDefault()).isNotEqualTo(Locale.forLanguageTag("zh-Hans-CN"));
    }

    /**
     * 测试指定默认的国际化标识
     *
     * <p>
     * 通过 {@link DefaultLocale @DefaultLocale} 注解可以为测试方法指定默认的国际化标识,
     * 即修改 {@link Locale#getDefault()} 方法的返回值
     * </p>
     *
     * <p>
     * 相当于通过 {@link Locale#forLanguageTag(String)} 方法得到的 {@link Locale} 对象
     * </p>
     */
    @Test
    @DefaultLocale("zh-Hans-CN")
    void locale_shouldDefaultLocaleChanged() {
        then(Locale.getDefault()).isEqualTo(Locale.forLanguageTag("zh-Hans-CN"));
    }

    /**
     * 测试指定默认的国际化标识
     *
     * <p>
     * 通过 {@link DefaultLocale @DefaultLocale} 注解的 {@code country},
     * {@code language} 以及 {@code variant} 属性为测试方法设置默认的国际化标识
     * </p>
     *
     * <p>
     * 相当于通过 {@link Locale.Builder#setRegion(String)},
     * {@link Locale.Builder#setLanguage(String)} 以及
     * {@link Locale.Builder#setVariant(String)} 方法进行设置,
     * 并通过 {@link Locale.Builder#build()} 方法得到的
     * {@link Locale} 对象
     * </p>
     */
    @Test
    @DefaultLocale(country = "cn", language = "zh", variant = "chinese")
    void locale_shouldSetDefaultLocaleByProperties() {
        then(Locale.getDefault()).isEqualTo(
            new Locale.Builder()
                    .setRegion("cn")
                    .setLanguage("zh")
                    .setVariant("chinese")
                    .build());
    }

    /**
     * 测试获取系统默认的时区标识
     *
     * <p>
     * 和测试所需的时区标识不一致
     * </p>
     */
    @Test
    @EnabledIf("checkTimeZone")
    void timezone_shouldGetOriginalTimeZone() {
        then(TimeZone.getDefault()).isNotEqualTo(TimeZone.getTimeZone("UTC"));
    }

    /**
     * 测试指定默认的时区标识
     *
     * <p>
     * 通过 {@link DefaultTimeZone @DefaultTimeZone} 注解为测试方法指定默认的时区标识
     * </p>
     */
    @Test
    @DefaultTimeZone("UTC")
    void timezone_shouldSetDefaultTimeZone() {
        then(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("UTC"));
    }
}
