package alvin.study.quarkus.web.template;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import alvin.study.quarkus.web.persist.entity.Gender;
import io.quarkus.qute.TemplateExtension;

/**
 * 模板扩展类
 *
 * <p>
 * 模板扩展的作用是为指定的对象添加属性, 当这个对象传递到模板中后, 既可以通过 <code>{<对象>.<方法名>}</code> 来使用这个类中的方法,
 * 参见 {@code resources:templates/TemplatedResource/checkedTemplate.html} 文件中的使用
 * </p>
 *
 * <p>
 * 整个类需要 {@link TemplateExtension @TemplateExtension} 注解, 且所有的方法都必须修饰为 {@code static},
 * 方法参数为模板中要使用的对象, 方法返回值为该对象的属性值, 方法名即属性名
 * </p>
 */
@TemplateExtension
public class TemplateExt {
    // 日期格式化模板
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    /**
     * 为 {@link Gender} 类型增加 {@code genderChinese} 属性, 属性值为 {@link Gender} 枚举值对应的中文值
     *
     * @param gender {@link Gender} 值
     * @return {@link Gender} 枚举值对应的中文值
     */
    static String genderChinese(Gender gender) {
        return gender == Gender.MALE ? "男" : "女";
    }

    /**
     * 为 {@link LocalDate} 类型增加 {@code dateChinese} 属性, 属性值为日期格式化后的结果
     *
     * @param date {@link LocalDate} 对象
     * @return {@link LocalDate} 对象格式化后的结果
     */
    static String dateChinese(LocalDate date) {
        return date.format(DATE_TIME_FORMATTER);
    }
}
